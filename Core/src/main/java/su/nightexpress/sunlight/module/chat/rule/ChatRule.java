package su.nightexpress.sunlight.module.chat.rule;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class ChatRule {

    private final List<String>  matches;
    private final ChatRule.Type action;
    private final String        replacer;
    private final Set<String>   ignoredWords;

    private final List<Pattern> patterns;

    public ChatRule(
        @NotNull List<String> matches,
        @NotNull ChatRule.Type action,
        @NotNull String replacer,
        @NotNull Set<String> ignoredWords
    ) {
        this.matches = matches;
        this.action = action;
        this.replacer = replacer;
        this.ignoredWords = ignoredWords;
        this.patterns = this.getMatches().stream().map(Pattern::compile).toList();
    }

    @NotNull
    public List<String> getMatches() {
        return matches;
    }

    @NotNull
    public ChatRule.Type getAction() {
        return this.action;
    }

    @NotNull
    public String getReplacer() {
        return this.replacer;
    }

    @NotNull
    public Set<String> getIgnoredWords() {
        return this.ignoredWords;
    }

    @NotNull
    public List<Pattern> getPatterns() {
        return patterns;
    }

    public enum Type {
        DENY, CENSOR, REPLACE,
    }
}
