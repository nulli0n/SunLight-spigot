package su.nightexpress.sunlight.module.warps.command.warps.child;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.module.warps.config.WarpsLang;
import su.nightexpress.sunlight.module.warps.impl.Warp;
import su.nightexpress.sunlight.module.warps.util.Placeholders;
import su.nightexpress.sunlight.module.warps.config.WarpsPerms;

import java.util.List;

public class TeleportSubCommand extends ModuleCommand<WarpsModule> {

    public static final String NAME = "teleport";

    public TeleportSubCommand(@NotNull WarpsModule module) {
        super(module, new String[]{NAME, "tp"}, WarpsPerms.COMMAND_WARPS_TELEPORT);
        this.setDescription(plugin.getMessage(WarpsLang.COMMAND_WARPS_TELEPORT_DESC));
        this.setUsage(plugin.getMessage(WarpsLang.COMMAND_WARPS_TELEPORT_USAGE));
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
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        if (result.length() >= 3 && !sender.hasPermission(WarpsPerms.COMMAND_WARPS_TELEPORT_OTHERS)) {
            this.errorPermission(sender);
            return;
        }

        String warpId = result.getArg(1);
        Warp warp = this.module.getWarpById(warpId);
        if (warp == null) {
            this.plugin.getMessage(WarpsLang.WARP_ERROR_INVALID).send(sender);
            return;
        }

        String name = result.getArg(2, sender.getName());
        Player target = plugin.getServer().getPlayer(name);
        if (target == null) {
            this.errorPlayer(sender);
            return;
        }

        boolean isForced = sender != target;
        if (isForced) {
            this.plugin.getMessage(WarpsLang.COMMAND_WARPS_TELEPORT_OTHERS)
                .replace(Placeholders.forPlayer(target))
                .replace(warp.replacePlaceholders())
                .send(sender);
        }
        warp.teleport(target, isForced);
    }
}
