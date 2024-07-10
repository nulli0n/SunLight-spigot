package su.nightexpress.sunlight.module.chat.rule;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Lists;

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
    public static ChatRule read(@NotNull FileConfig config, @NotNull String path) {
        List<String> matches = ConfigValue.create(path + ".Matches",
            Lists.newList(),
            "A list of Regex Expressions to match in message.",
            "If you want to match a simple text/word, just add it as is, like 'bad word'.",
            "For a more complex expressions, you have to deal with Regex."
        ).read(config);

        ChatRule.Type action = ConfigValue.create(path + ".Action",
            ChatRule.Type.class,
            ChatRule.Type.DENY,
            "What do you want to do when this rule matches text in message?",
            "Allowed Values:",
            ChatRule.Type.CENSOR + " - Replace mathced text with a text from option below.",
            ChatRule.Type.REPLACE + " - Replace all message with a text from option below.",
            ChatRule.Type.DENY + " - Prevents message/command from being sent."
        ).read(config);

        String replaceWith = ConfigValue.create(path + ".Replace_With",
            "***",
            "Custom text to replace matched text in message with."
        ).read(config);

        Set<String> ignoredWords = ConfigValue.create(path + ".Ignored_Words",
            Lists.newSet(),
            "A list of words (non-regex) that will be ignored by this rule."
        ).read(config);

        return new ChatRule(matches, action, replaceWith, ignoredWords);
    }

    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Matches", this.matches);
        config.set(path + ".Action", this.action.name());
        config.set(path + ".Replace_With", this.replacer);
        config.set(path + ".Ignored_Words", this.ignoredWords);
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
