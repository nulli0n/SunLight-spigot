package su.nightexpress.sunlight.module.warps.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.warps.config.WarpsLang;
import su.nightexpress.sunlight.module.warps.impl.Warp;

public class WarpShortcutCommand extends GeneralCommand<SunLight> {

    private final Warp warp;

    public WarpShortcutCommand(@NotNull Warp warp, @NotNull String alias) {
        super(warp.plugin(), new String[]{alias}, warp.getPermission());
        this.setDescription(plugin.getMessage(WarpsLang.COMMAND_WARPS_TELEPORT_DESC));
        this.setPlayerOnly(true);
        this.warp = warp;
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player player = (Player) sender;
        this.warp.teleport(player, false);
    }
}
