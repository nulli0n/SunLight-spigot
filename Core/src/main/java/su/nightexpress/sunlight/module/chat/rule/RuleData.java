package su.nightexpress.sunlight.module.chat.rule;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractFileData;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.sunlight.SunLightPlugin;

import java.io.File;
import java.util.*;

public class RuleData extends AbstractFileData<SunLightPlugin> {

    private List<String>          commands;
    private Map<String, ChatRule> ruleMap;

    public RuleData(@NotNull SunLightPlugin plugin, @NotNull File file) {
        super(plugin, file);
        this.commands = new ArrayList<>();
        this.ruleMap = new HashMap<>();
    }

    @Override
    protected boolean onLoad(@NotNull FileConfig config) {
        this.commands = ConfigValue.create("Punishments",
            Lists.newList(),
            "List of commands that will be executed when any rule is triggered."
        ).read(config);

        this.ruleMap = new HashMap<>();
        for (String ruleId : config.getSection("Rules")) {
            String path = "Rules." + ruleId;

            if (!ConfigValue.create(path + ".Enabled", true, "Enables the rule.").read(config)) continue;

            ChatRule rule = ChatRule.read(config, path);

            this.ruleMap.put(ruleId.toLowerCase(), rule);
        }

        return true;
    }

    @Override
    protected void onSave(@NotNull FileConfig config) {
        config.set("Punishments", this.commands);
        config.remove("Rules");
        this.ruleMap.forEach((sId, rule) -> rule.write(config, "Rules." + sId));
    }

    @NotNull
    public Map<String, ChatRule> getRuleMap() {
        return this.ruleMap;
    }

    @NotNull
    public List<String> getCommands() {
        return this.commands;
    }

    public void punish(@NotNull Player player) {
        this.commands.forEach(command -> Players.dispatchCommand(player, command));
    }
}
