package su.nightexpress.sunlight.module.homes.command.basic;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.config.HomesLang;
import su.nightexpress.sunlight.module.homes.util.HomesPerms;

public class HomesCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "homes";

    public HomesCommand(@NotNull HomesModule homesModule, @NotNull String[] aliases) {
        super(homesModule.plugin(), aliases, HomesPerms.COMMAND_HOMES);
        this.addDefaultCommand(new HelpSubCommand<>(plugin));
        this.addChildren(new HomesDeleteCommand(homesModule));
        this.addChildren(new HomesSetCommand(homesModule));
        this.addChildren(new HomesTeleportCommand(homesModule));
        this.addChildren(new HomesListCommand(homesModule));
        this.addChildren(new HomesVisitCommand(homesModule));
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(HomesLang.COMMAND_HOMES_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(HomesLang.COMMAND_HOMES_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    protected void onExecute(@NotNull CommandSender commandSender, @NotNull CommandResult commandResult) {

    }
}
