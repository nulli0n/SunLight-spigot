package su.nightexpress.sunlight.utils;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.placeholder.PlaceholderContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public final class ConditionExpression {

    private static final Node ALWAYS_TRUE = context -> true;

    private final String raw;
    private final String owner;
    private final Node root;
    private final String error;
    private boolean warned;

    private ConditionExpression(@NotNull String raw, @NotNull String owner, Node root, String error) {
        this.raw = raw;
        this.owner = owner;
        this.root = root;
        this.error = error;
    }

    @NotNull
    public static ConditionExpression of(String raw, @NotNull String owner) {
        String expression = raw == null ? "" : raw.trim();
        if (expression.isEmpty()) {
            return new ConditionExpression("", owner, ALWAYS_TRUE, null);
        }

        try {
            List<Token> tokens = tokenize(expression);
            Parser parser = new Parser(tokens);
            Node root = parser.parse();
            return new ConditionExpression(expression, owner, root, null);
        }
        catch (ParseException ex) {
            return new ConditionExpression(expression, owner, null, ex.getMessage());
        }
    }

    public boolean evaluate(@NotNull PlaceholderContext context, @NotNull Logger logger) {
        if (this.root == null) {
            this.warnInvalid(logger);
            return false;
        }
        return this.root.evaluate(context);
    }

    private void warnInvalid(@NotNull Logger logger) {
        if (this.warned || this.error == null) return;
        this.warned = true;
        logger.warning("Invalid condition expression for " + this.owner + ": " + this.error + " (expression: " + this.raw + ")");
    }

    private interface Node {
        boolean evaluate(@NotNull PlaceholderContext context);
    }

    private interface ValueNode {
        @NotNull String resolve(@NotNull PlaceholderContext context);
    }

    private static final class PlaceholderValue implements ValueNode {
        private final String token;

        private PlaceholderValue(@NotNull String token) {
            this.token = token;
        }

        @Override
        @NotNull
        public String resolve(@NotNull PlaceholderContext context) {
            String value = context.apply(this.token);
            return value == null ? "" : value;
        }
    }

    private static final class LiteralValue implements ValueNode {
        private final String value;

        private LiteralValue(@NotNull String value) {
            this.value = value;
        }

        @Override
        @NotNull
        public String resolve(@NotNull PlaceholderContext context) {
            return this.value;
        }
    }

    private static final class CompareNode implements Node {
        private final ValueNode left;
        private final ValueNode right;
        private final boolean equals;

        private CompareNode(@NotNull ValueNode left, @NotNull ValueNode right, boolean equals) {
            this.left = left;
            this.right = right;
            this.equals = equals;
        }

        @Override
        public boolean evaluate(@NotNull PlaceholderContext context) {
            String leftValue = this.left.resolve(context);
            String rightValue = this.right.resolve(context);
            boolean result = leftValue.equals(rightValue);
            return this.equals == result;
        }
    }

    private static final class TruthyNode implements Node {
        private final ValueNode value;

        private TruthyNode(@NotNull ValueNode value) {
            this.value = value;
        }

        @Override
        public boolean evaluate(@NotNull PlaceholderContext context) {
            return isTruthy(this.value.resolve(context));
        }
    }

    private static final class NotNode implements Node {
        private final Node node;

        private NotNode(@NotNull Node node) {
            this.node = node;
        }

        @Override
        public boolean evaluate(@NotNull PlaceholderContext context) {
            return !this.node.evaluate(context);
        }
    }

    private static final class AndNode implements Node {
        private final Node left;
        private final Node right;

        private AndNode(@NotNull Node left, @NotNull Node right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean evaluate(@NotNull PlaceholderContext context) {
            return this.left.evaluate(context) && this.right.evaluate(context);
        }
    }

    private static final class OrNode implements Node {
        private final Node left;
        private final Node right;

        private OrNode(@NotNull Node left, @NotNull Node right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean evaluate(@NotNull PlaceholderContext context) {
            return this.left.evaluate(context) || this.right.evaluate(context);
        }
    }

    private static boolean isTruthy(@NotNull String value) {
        String trimmed = value.trim();
        if (trimmed.isEmpty()) return false;
        String lower = trimmed.toLowerCase(Locale.ENGLISH);
        return !lower.equals("false") && !lower.equals("0");
    }

    private enum TokenType {
        PLACEHOLDER,
        STRING,
        WORD,
        OPERATOR,
        LPAREN,
        RPAREN,
        END
    }

    private static final class Token {
        private final TokenType type;
        private final String value;

        private Token(@NotNull TokenType type, @NotNull String value) {
            this.type = type;
            this.value = value;
        }
    }

    private static final class Parser {
        private final List<Token> tokens;
        private int index;

        private Parser(@NotNull List<Token> tokens) {
            this.tokens = tokens;
        }

        @NotNull
        private Node parse() throws ParseException {
            Node node = parseOr();
            if (!check(TokenType.END)) {
                throw error("Unexpected token: " + peek().value);
            }
            return node;
        }

        private Node parseOr() throws ParseException {
            Node node = parseAnd();
            while (matchOperator("||")) {
                node = new OrNode(node, parseAnd());
            }
            return node;
        }

        private Node parseAnd() throws ParseException {
            Node node = parseNot();
            while (matchOperator("&&")) {
                node = new AndNode(node, parseNot());
            }
            return node;
        }

        private Node parseNot() throws ParseException {
            if (matchOperator("!")) {
                return new NotNode(parseNot());
            }
            return parsePrimary();
        }

        private Node parsePrimary() throws ParseException {
            if (match(TokenType.LPAREN)) {
                Node node = parseOr();
                consume(TokenType.RPAREN, "Expected ')' after expression");
                return node;
            }

            ValueNode left = parseValue();
            if (matchOperator("==")) {
                ValueNode right = parseValue();
                return new CompareNode(left, right, true);
            }
            if (matchOperator("!=")) {
                ValueNode right = parseValue();
                return new CompareNode(left, right, false);
            }

            return new TruthyNode(left);
        }

        private ValueNode parseValue() throws ParseException {
            Token token = advance();
            if (token.type == TokenType.PLACEHOLDER) {
                return new PlaceholderValue(token.value);
            }
            if (token.type == TokenType.STRING || token.type == TokenType.WORD) {
                return new LiteralValue(token.value);
            }
            throw error("Expected value but found: " + token.value);
        }

        private boolean matchOperator(@NotNull String operator) {
            if (check(TokenType.OPERATOR) && operator.equals(peek().value)) {
                advance();
                return true;
            }
            return false;
        }

        private boolean match(@NotNull TokenType type) {
            if (check(type)) {
                advance();
                return true;
            }
            return false;
        }

        private Token consume(@NotNull TokenType type, @NotNull String message) throws ParseException {
            if (check(type)) return advance();
            throw error(message);
        }

        private boolean check(@NotNull TokenType type) {
            return peek().type == type;
        }

        private Token advance() {
            if (!isAtEnd()) index++;
            return previous();
        }

        private boolean isAtEnd() {
            return peek().type == TokenType.END;
        }

        private Token peek() {
            return tokens.get(index);
        }

        private Token previous() {
            return tokens.get(index - 1);
        }

        private ParseException error(@NotNull String message) {
            return new ParseException(message);
        }
    }

    private static List<Token> tokenize(@NotNull String input) throws ParseException {
        List<Token> tokens = new ArrayList<>();
        int length = input.length();
        int index = 0;

        while (index < length) {
            char current = input.charAt(index);
            if (Character.isWhitespace(current)) {
                index++;
                continue;
            }

            switch (current) {
                case '(' -> {
                    tokens.add(new Token(TokenType.LPAREN, "("));
                    index++;
                }
                case ')' -> {
                    tokens.add(new Token(TokenType.RPAREN, ")"));
                    index++;
                }
                case '!' -> {
                    if (peek(input, index + 1) == '=') {
                        tokens.add(new Token(TokenType.OPERATOR, "!="));
                        index += 2;
                    }
                    else {
                        tokens.add(new Token(TokenType.OPERATOR, "!"));
                        index++;
                    }
                }
                case '=' -> {
                    if (peek(input, index + 1) == '=') {
                        tokens.add(new Token(TokenType.OPERATOR, "=="));
                        index += 2;
                    }
                    else {
                        throw new ParseException("Unexpected '='; did you mean '=='?");
                    }
                }
                case '&' -> {
                    if (peek(input, index + 1) == '&') {
                        tokens.add(new Token(TokenType.OPERATOR, "&&"));
                        index += 2;
                    }
                    else {
                        throw new ParseException("Unexpected '&'; did you mean '&&'?");
                    }
                }
                case '|' -> {
                    if (peek(input, index + 1) == '|') {
                        tokens.add(new Token(TokenType.OPERATOR, "||"));
                        index += 2;
                    }
                    else {
                        throw new ParseException("Unexpected '|'; did you mean '||'?");
                    }
                }
                case '"' -> {
                    int start = index + 1;
                    StringBuilder builder = new StringBuilder();
                    index++;
                    while (index < length) {
                        char ch = input.charAt(index);
                        if (ch == '\\' && index + 1 < length) {
                            char next = input.charAt(index + 1);
                            if (next == '"' || next == '\\') {
                                builder.append(next);
                                index += 2;
                                continue;
                            }
                        }
                        if (ch == '"') {
                            break;
                        }
                        builder.append(ch);
                        index++;
                    }
                    if (index >= length || input.charAt(index) != '"') {
                        throw new ParseException("Unterminated string literal starting at index " + start);
                    }
                    index++;
                    tokens.add(new Token(TokenType.STRING, builder.toString()));
                }
                case '%' -> {
                    int start = index;
                    index++;
                    while (index < length && input.charAt(index) != '%') {
                        index++;
                    }
                    if (index >= length) {
                        throw new ParseException("Unterminated placeholder starting at index " + start);
                    }
                    index++;
                    tokens.add(new Token(TokenType.PLACEHOLDER, input.substring(start, index)));
                }
                default -> {
                    int start = index;
                    while (index < length && isWordChar(input.charAt(index))) {
                        index++;
                    }
                    tokens.add(new Token(TokenType.WORD, input.substring(start, index)));
                }
            }
        }

        tokens.add(new Token(TokenType.END, ""));
        return tokens;
    }

    private static boolean isWordChar(char ch) {
        if (Character.isWhitespace(ch)) return false;
        return ch != '(' && ch != ')' && ch != '!' && ch != '=' && ch != '&' && ch != '|' && ch != '"';
    }

    private static char peek(@NotNull String input, int index) {
        if (index >= input.length()) return '\0';
        return input.charAt(index);
    }

    private static final class ParseException extends Exception {
        private ParseException(@NotNull String message) {
            super(message);
        }
    }
}
