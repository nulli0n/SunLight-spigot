package su.nightexpress.sunlight.module.warps.command.basic;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.module.warps.config.WarpsLang;
import su.nightexpress.sunlight.module.warps.impl.Warp;
import su.nightexpress.sunlight.module.warps.util.WarpsPerms;

import java.util.List;
import java.util.Map;

public class WarpsDeleteCommand extends ModuleCommand<WarpsModule> {

    public static final String NAME = "delete";

    public WarpsDeleteCommand(@NotNull WarpsModule module) {
        super(module, new String[]{NAME}, WarpsPerms.COMMAND_WARPS_DELETE);
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.plugin.getMessage(WarpsLang.COMMAND_WARPS_DELETE_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.plugin.getMessage(WarpsLang.COMMAND_WARPS_DELETE_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
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
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
        if (args.length < 2) {
            this.printUsage(sender);
            return;
        }

        Warp warp = this.module.getWarpById(args[1]);
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
