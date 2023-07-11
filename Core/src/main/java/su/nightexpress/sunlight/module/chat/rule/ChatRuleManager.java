package su.nightexpress.sunlight.module.chat.rule;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.Loadable;
import su.nexmedia.engine.utils.regex.RegexUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.chat.ChatModule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatRuleManager implements Loadable {

    private final SunLight   plugin;
    private final ChatModule chatModule;

    private Map<String, ChatRuleConfig> ruleConfigs;

    public ChatRuleManager(@NotNull ChatModule chatModule) {
        this.plugin = chatModule.plugin();
        this.chatModule = chatModule;
    }

    @Override
    public void setup() {
        this.plugin.getConfigManager().extractResources(this.chatModule.getLocalPath() + "/rules/");

        this.ruleConfigs = new HashMap<>();
        for (JYML cfg : JYML.loadAll(this.chatModule.getAbsolutePath() + "/rules/", true)) {
            try {
                ChatRuleConfig ruleConfig = new ChatRuleConfig(cfg);
                String id = cfg.getFile().getName().replace(".yml", "");
                this.ruleConfigs.put(id, ruleConfig);
            }
            catch (Exception e) {
                this.chatModule.error("Could not load rule config: '" + cfg.getFile().getName() + "' !");
                e.printStackTrace();
            }
        }

        this.chatModule.info("Loaded " + this.ruleConfigs.size() + " chat rules!");
    }

    @Override
    public void shutdown() {
        if (this.ruleConfigs != null) {
            this.ruleConfigs.clear();
            this.ruleConfigs = null;
        }
    }

    @Nullable
    public String checkRules(@NotNull Player player, @NotNull String msgReal, @NotNull String msgRaw) {
        Set<ChatRuleConfig> punishes = new HashSet<>();

        Label_Rule:
        for (ChatRuleConfig ruleConfig : this.ruleConfigs.values()) {
            for (ChatRule rule : ruleConfig.getRules().values()) {
                for (Pattern pattern : rule.getPatterns()) {
                    Matcher matcher = RegexUtil.getMatcher(pattern, msgReal, 100);
                    if (!matcher.find()) continue;

                    String find = matcher.group(0).trim();

                    String finalMsgReal = msgReal;
                    boolean skip = rule.getIgnoredWords().stream().anyMatch(word -> {
                        return finalMsgReal.contains(word) && (find.contains(word) || word.contains(find));
                    });
                    if (skip) continue;

                    punishes.add(ruleConfig);

                    switch (rule.getAction()) {
                        case DENY -> {
                            msgReal = null;
                            break Label_Rule;
                        }
                        case CENSOR -> msgReal = msgReal.replace(find, rule.getReplacer());
                        case REPLACE -> {
                            msgReal = rule.getReplacer();
                            break Label_Rule;
                        }
                    }
                }
            }
        }

        punishes.forEach(punish -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> punish.punish(player));
        });

        return msgReal;
    }
}
