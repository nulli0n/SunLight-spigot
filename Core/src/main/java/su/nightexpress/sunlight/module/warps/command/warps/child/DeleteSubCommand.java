package su.nightexpress.sunlight.module.warps.command.warps.child;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.module.warps.config.WarpsLang;
import su.nightexpress.sunlight.module.warps.impl.Warp;
import su.nightexpress.sunlight.module.warps.config.WarpsPerms;

import java.util.List;

public class DeleteSubCommand extends ModuleCommand<WarpsModule> {

    public static final String NAME = "delete";

    public DeleteSubCommand(@NotNull WarpsModule module) {
        super(module, new String[]{NAME}, WarpsPerms.COMMAND_WARPS_DELETE);
        this.setDescription(plugin.getMessage(WarpsLang.COMMAND_WARPS_DELETE_DESC));
        this.setUsage(plugin.getMessage(WarpsLang.COMMAND_WARPS_DELETE_USAGE));
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            if (player.hasPermission(WarpsPerms.COMMAND_WARPS_DELETE_OTHERS)) {
                return this.module.getWarps().stream().map(Warp::getId).toList();
            }
            else {
                return this.module.getWarpsCreated(player).stream().map(Warp::getId).toList();
            }
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        Warp warp = this.module.getWarpById(result.getArg(1));
        if (warp == null) {
            this.plugin.getMessage(WarpsLang.WARP_ERROR_INVALID).send(sender);
            return;
        }

        if (sender instanceof Player player) {
            if (!warp.isOwner(player) && !sender.hasPermission(WarpsPerms.COMMAND_WARPS_DELETE_OTHERS)) {
                this.errorPermission(sender);
                return;
            }
        }

        this.module.delete(warp);
        this.plugin.getMessage(WarpsLang.WARP_DELETE_DONE).replace(warp.replacePlaceholders()).send(sender);
    }
}
