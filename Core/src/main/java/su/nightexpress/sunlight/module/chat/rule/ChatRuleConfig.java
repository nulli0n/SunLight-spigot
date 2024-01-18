package su.nightexpress.sunlight.module.chat.rule;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nightexpress.sunlight.module.chat.util.Placeholders;

import java.util.*;

public class ChatRuleConfig {

    private final List<String> punishmentActions;
    private final Map<String, ChatRule> rules;

    public ChatRuleConfig(@NotNull JYML cfg) {
        this.punishmentActions = JOption.create("Punishments", Collections.emptyList(),
            "A list of commands that will be executed when at least one rule is triggered.",
            Placeholders.ENGINE_URL_CONFIG_COMMANDS).read(cfg);

        this.rules = new HashMap<>();
        for (String ruleId : cfg.getSection("Rules")) {
            String path2 = "Rules." + ruleId + ".";

            if (!JOption.create(path2 + "Enabled", true, "Enables/Activates the rule.")
                .read(cfg)) continue;

            List<String> matches = JOption.create(path2 + "Matches", Collections.emptyList(),
                "A list of Regex Expressions to match in message.",
                "If you want to match a simple text/word, just add it as is, like 'bad word'.",
                "For a more complex expressions, you have to deal with Regex.").read(cfg);
            ChatRule.Type rAction = JOption.create(path2 + "Action", ChatRule.Type.class, ChatRule.Type.DENY,
                "What do you want to do when this rule matches text in message?",
                "Allowed Values:",
                ChatRule.Type.CENSOR + " - Replace mathced text with a text from option below.",
                ChatRule.Type.REPLACE + " - Replace all message with a text from option below.",
                ChatRule.Type.DENY + " - Prevents message/command from being sent.").read(cfg);
            String rReplace = JOption.create(path2 + "Replace_With", "",
                "Custom text to replace matched text in message with.").read(cfg);
            Set<String> rIgnored = JOption.create(path2 + "Ignored_Words", Collections.emptySet(),
                "A list of words (non-regex) that will be ignored by this rule.").read(cfg);
            this.rules.put(ruleId, new ChatRule(matches, rAction, rReplace, rIgnored));
        }

        cfg.saveChanges();
    }

    @NotNull
    public Map<String, ChatRule> getRules() {
        return rules;
    }

    @NotNull
    public List<String> getPunishmentActions() {
        return punishmentActions;
    }

    public void punish(@NotNull Player player) {
        this.punishmentActions.forEach(command -> PlayerUtil.dispatchCommand(player, command));
    }
}
