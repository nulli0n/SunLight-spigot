package su.nightexpress.sunlight.module.warps.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.warps.config.WarpsLang;
import su.nightexpress.sunlight.module.warps.impl.Warp;

import java.util.Map;

public class WarpShortcutCommand extends GeneralCommand<SunLight> {

    private final Warp warp;

    public WarpShortcutCommand(@NotNull Warp warp, @NotNull String alias) {
        super(warp.plugin(), new String[]{alias}, warp.getPermission());
        this.warp = warp;
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(WarpsLang.COMMAND_WARPS_TELEPORT_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
        Player player = (Player) sender;
        this.warp.teleport(player, false);
    }
}
