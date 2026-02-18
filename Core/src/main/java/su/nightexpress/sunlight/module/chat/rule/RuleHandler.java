package su.nightexpress.sunlight.module.chat.rule;

import org.jetbrains.annotations.NotNull;

public class RuleHandler {

    private final TrieNode      node;
    private final RuleValidator validator;
    private final RuleResult    result;

    public RuleHandler(@NotNull RuleValidator validator, @NotNull RuleResult result) {
        this.node = new TrieNode();
        this.validator = validator;
        this.result = result;
    }

    public boolean canHandle(@NotNull String rule) {
        return this.validator.detect(rule);
    }

    @NotNull
    public String clean(@NotNull String rule) {
        return this.validator.clean(rule);
    }

    public void addWord(@NotNull String word) {
        this.node.add(this.validator.prepare(word));
    }

    @NotNull
    public RuleResult scan(@NotNull String input) {
        if (this.validator.matches(this.node, input)) {
            return this.result;
        }

        return RuleResult.NONE;
    }
}
