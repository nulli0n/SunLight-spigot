package su.nightexpress.sunlight.module.warps.command.basic;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.module.warps.config.WarpsLang;
import su.nightexpress.sunlight.module.warps.util.WarpsPerms;

import java.util.Map;

public class WarpsCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "warps";

    public WarpsCommand(@NotNull WarpsModule module, @NotNull String[] aliases) {
        super(module.plugin(), aliases, WarpsPerms.COMMAND_WARPS);
        this.addDefaultCommand(new HelpSubCommand<>(module.plugin()));
        this.addChildren(new WarpsCreateCommand(module));
        this.addChildren(new WarpsDeleteCommand(module));
        this.addChildren(new WarpsListCommand(module));
        this.addChildren(new WarpsTeleportCommand(module));
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
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {

    }
}
