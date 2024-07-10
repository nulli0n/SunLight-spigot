package su.nightexpress.sunlight.module.chat.rule;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.manager.SimpleManager;
import su.nightexpress.nightcore.util.FileUtil;
import su.nightexpress.nightcore.util.regex.TimedMatcher;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.chat.ChatModule;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class RuleManager extends SimpleManager<SunLightPlugin> {

    public static final String DIR_RULES = "/rules/";

    private final ChatModule            module;
    private final Map<String, RuleData> dataMap;

    public RuleManager(@NotNull SunLightPlugin plugin, @NotNull ChatModule module) {
        super(plugin);
        this.module = module;
        this.dataMap = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        this.loadDefaults();
        this.loadRules();
    }

    @Override
    protected void onShutdown() {
        this.dataMap.clear();
    }

    @NotNull
    public String getRulesPath() {
        return this.module.getAbsolutePath() + DIR_RULES;
    }

    private void loadDefaults() {
        File dir = new File(this.module.getAbsolutePath(), DIR_RULES);
        if (dir.exists()) return;

        dir.mkdirs();

        new RuleCreator(this.plugin, this).create();
    }

    private void loadRules() {
        for (File file : FileUtil.getConfigFiles(this.module.getAbsolutePath() + DIR_RULES)) {
            RuleData ruleData = new RuleData(this.plugin, file);
            if (ruleData.load()) {
                this.dataMap.put(ruleData.getId(), ruleData);
            }
            else this.module.error("Rule not loaded: '" + file.getName() + "'!");
        }

        this.module.info("Loaded " + this.dataMap.size() + " chat rules!");
    }

    @Nullable
    public String checkRules(@NotNull Player player, @NotNull String message, @NotNull String rawMessage) {
        Set<RuleData> punishes = new HashSet<>();

        Root:
        for (RuleData ruleData : this.dataMap.values()) {
            for (ChatRule rule : ruleData.getRuleMap().values()) {
                for (Pattern pattern : rule.getPatterns()) {
                    TimedMatcher matcher = TimedMatcher.create(pattern, rawMessage, 100);
                    if (!matcher.find()) continue;

                    String find = matcher.getMatcher().group(0).trim();

                    String finalMessage = message;
                    boolean skip = rule.getIgnoredWords().stream().anyMatch(word -> {
                        return finalMessage.contains(word) && (find.contains(word) || word.contains(find));
                    });
                    if (skip) continue;

                    punishes.add(ruleData);

                    switch (rule.getAction()) {
                        case DENY -> {
                            message = null;
                            break Root;
                        }
                        case CENSOR -> message = message.replace(find, rule.getReplacer());
                        case REPLACE -> {
                            message = rule.getReplacer();
                            break Root;
                        }
                    }
                }
            }
        }

        // Back to the main thread.
        this.plugin.runTask(task -> punishes.forEach(ruleData -> ruleData.punish(player)));

        return message;
    }
}
