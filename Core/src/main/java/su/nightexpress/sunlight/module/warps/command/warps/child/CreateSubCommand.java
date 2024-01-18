package su.nightexpress.sunlight.module.warps.command.warps.child;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.module.warps.config.WarpsLang;
import su.nightexpress.sunlight.module.warps.config.WarpsPerms;

public class CreateSubCommand extends ModuleCommand<WarpsModule> {

    public static final String NAME = "create";

    public CreateSubCommand(@NotNull WarpsModule module) {
        super(module, new String[]{NAME, "set"}, WarpsPerms.COMMAND_WARPS_CREATE);
        this.setDescription(plugin.getMessage(WarpsLang.COMMAND_WARPS_CREATE_DESC));
        this.setUsage(plugin.getMessage(WarpsLang.COMMAND_WARPS_CREATE_USAGE));
        this.setPlayerOnly(true);
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
