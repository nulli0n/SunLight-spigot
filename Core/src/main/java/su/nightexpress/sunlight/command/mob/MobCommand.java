package su.nightexpress.sunlight.command.mob;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;

public class MobCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "mob";

    public MobCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_MOB);
        this.setUsage(plugin.getMessage(Lang.COMMAND_MOB_USAGE));
        this.setDescription(plugin.getMessage(Lang.COMMAND_MOB_DESC));

        this.addDefaultCommand(new HelpSubCommand<>(plugin));
        this.addChildren(new MobKillCommand(plugin));
        this.addChildren(new MobSpawnCommand(plugin));
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {

    }
}
