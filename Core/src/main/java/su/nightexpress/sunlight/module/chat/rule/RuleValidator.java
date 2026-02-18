package su.nightexpress.sunlight.module.chat.rule;

import org.jetbrains.annotations.NotNull;

public interface RuleValidator {

    boolean detect(@NotNull String rule);

    @NotNull String clean(@NotNull String rule);

    @NotNull String prepare(@NotNull String word);

    boolean matches(@NotNull TrieNode root, @NotNull String word);

    static RuleValidator forSequence(char prefix) {

        return new RuleValidator() {

            @Override
            public boolean detect(@NotNull String rule) {
                return rule.charAt(0) == prefix && rule.charAt(rule.length() - 1) == prefix;
            }

            @Override
            @NotNull
            public String clean(@NotNull String rule) {
                return rule.substring(1, rule.length() - 1);
            }

            @Override
            @NotNull
            public String prepare(@NotNull String word) {
                return word;
            }

            @Override
            public boolean matches(@NotNull TrieNode root, @NotNull String input) {
                for (int i = 0; i < input.length(); i++) {
                    if (RuleValidator.containsPrefix(root, input, i, 1)) return true;
                }
                return false;
            }
        };
    }

    static RuleValidator forPrefix(char prefix) {

        return new RuleValidator() {
            @Override
            public boolean detect(@NotNull String rule) {
                return rule.charAt(0) == prefix;
            }

            @Override
            @NotNull
            public String clean(@NotNull String rule) {
                return rule.substring(1);
            }

            @Override
            @NotNull
            public String prepare(@NotNull String word) {
                return new StringBuilder(word).reverse().toString();
            }

            @Override
            public boolean matches(@NotNull TrieNode root, @NotNull String input) {
                return RuleValidator.containsPrefix(root, input, input.length() - 1, -1);
            }
        };
    }

    static RuleValidator forSuffix(char suffix) {

        return new RuleValidator() {
            @Override
            public boolean detect(@NotNull String rule) {
                return rule.charAt(rule.length() - 1) == suffix;
            }

            @Override
            @NotNull
            public String clean(@NotNull String rule) {
                return rule.substring(0, rule.length() - 1);
            }

            @Override
            @NotNull
            public String prepare(@NotNull String word) {
                return word;
            }

            @Override
            public boolean matches(@NotNull TrieNode root, @NotNull String input) {
                return RuleValidator.containsPrefix(root, input, 0, 1);
            }
        };
    }

    private static boolean containsPrefix(@NotNull TrieNode root, @NotNull String input, int start, int step) {
        TrieNode currentNode = root;
        int length = input.length();

        for (int index = start; index >= 0 && index < length; index += step) {
            char letter = input.charAt(index);

            currentNode = currentNode.children(letter);
            if (currentNode == null) return false;
            if (currentNode.isEnd()) return true;
        }

        return false;
    }
}
