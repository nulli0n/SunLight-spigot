package su.nightexpress.sunlight.module.warps.command.basic;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.module.warps.config.WarpsLang;
import su.nightexpress.sunlight.module.warps.impl.Warp;
import su.nightexpress.sunlight.module.warps.util.Placeholders;
import su.nightexpress.sunlight.module.warps.util.WarpsPerms;

import java.util.List;
import java.util.Map;

public class WarpsTeleportCommand extends ModuleCommand<WarpsModule> {

    public static final String NAME = "teleport";

    public WarpsTeleportCommand(@NotNull WarpsModule module) {
        super(module, new String[]{NAME, "tp"}, WarpsPerms.COMMAND_WARPS_TELEPORT);
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.plugin.getMessage(WarpsLang.COMMAND_WARPS_TELEPORT_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.plugin.getMessage(WarpsLang.COMMAND_WARPS_TELEPORT_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return this.module.getWarpsAvailable(player).stream().map(Warp::getId).toList();
        }
        if (arg == 2) {
            if (player.hasPermission(WarpsPerms.COMMAND_WARPS_TELEPORT_OTHERS)) {
                return CollectionsUtil.playerNames(player);
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

        if (args.length >= 3 && !sender.hasPermission(WarpsPerms.COMMAND_WARPS_TELEPORT_OTHERS)) {
            this.errorPermission(sender);
            return;
        }

        String warpId = args[1];
        Warp warp = this.module.getWarpById(warpId);
        if (warp == null) {
            this.plugin.getMessage(WarpsLang.WARP_ERROR_INVALID).send(sender);
            return;
        }

        String pName = args.length >= 3 ? args[2] : sender.getName();
        Player player = plugin.getServer().getPlayer(pName);
        if (player == null) {
            this.errorPlayer(sender);
            return;
        }

        boolean isForced = !(sender.equals(player));
        if (isForced) {
            this.plugin.getMessage(WarpsLang.COMMAND_WARPS_TELEPORT_OTHERS)
                .replace(Placeholders.Player.replacer(player))
                .replace(warp.replacePlaceholders())
                .send(sender);
        }
        warp.teleport(player, isForced);
    }
}
