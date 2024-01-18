package su.nightexpress.sunlight.module.warps.command.warps;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.module.warps.command.warps.child.*;
import su.nightexpress.sunlight.module.warps.config.WarpsLang;
import su.nightexpress.sunlight.module.warps.config.WarpsPerms;

public class WarpsCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "warps";

    public WarpsCommand(@NotNull WarpsModule module, @NotNull String[] aliases) {
        super(module.plugin(), aliases, WarpsPerms.COMMAND_WARPS);
        this.addDefaultCommand(new HelpSubCommand<>(module.plugin()));
        this.addChildren(new CreateSubCommand(module));
        this.addChildren(new DeleteSubCommand(module));
        this.addChildren(new ListSubCommand(module));
        this.addChildren(new TeleportSubCommand(module));
        this.addChildren(new ResetCooldownSubCommand(module));
        this.addChildren(new SetCooldownSubCommand(module));
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(WarpsLang.COMMAND_WARPS_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(WarpsLang.COMMAND_WARPS_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    protected void onExecute(@NotNull CommandSender commandSender, @NotNull CommandResult commandResult) {

    }
}
