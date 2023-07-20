package su.nightexpress.sunlight.module.warps.command.basic;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.module.warps.config.WarpsLang;
import su.nightexpress.sunlight.module.warps.config.WarpsPerms;

public class WarpsCreateCommand extends ModuleCommand<WarpsModule> {

    public static final String NAME = "create";

    public WarpsCreateCommand(@NotNull WarpsModule module) {
        super(module, new String[]{NAME, "set"}, WarpsPerms.COMMAND_WARPS_CREATE);
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.plugin.getMessage(WarpsLang.COMMAND_WARPS_CREATE_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.plugin.getMessage(WarpsLang.COMMAND_WARPS_CREATE_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        Player player = (Player) sender;
        String id = result.getArg(1);
        this.module.create(player, id, false);
    }
}
