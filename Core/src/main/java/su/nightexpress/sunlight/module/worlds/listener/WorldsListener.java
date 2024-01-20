package su.nightexpress.sunlight.module.worlds.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.command.CommandRegister;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.worlds.WorldsModule;
import su.nightexpress.sunlight.module.worlds.config.WorldsConfig;
import su.nightexpress.sunlight.module.worlds.config.WorldsLang;
import su.nightexpress.sunlight.module.worlds.config.WorldsPerms;

import java.util.Set;

public class WorldsListener extends AbstractListener<SunLight> {

    //private final WorldsModule worldsModule;

    public WorldsListener(@NotNull WorldsModule worldsModule) {
        super(worldsModule.plugin());
        //this.worldsModule = worldsModule;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onFlyDisableToggle(PlayerToggleFlightEvent e) {
        if (!e.isFlying()) return;

        Player player = e.getPlayer();
        if (player.hasPermission(WorldsPerms.BYPASS_FLY)) return;
        if (!WorldsConfig.NO_FLY_WORLDS.get().contains(player.getWorld().getName())) return;

        e.setCancelled(true);
        player.setAllowFlight(false);
        player.setFlying(false);
        plugin.getMessage(WorldsLang.ERROR_FLY_DISABLED).send(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFlyDisableTeleport(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        if (!player.getAllowFlight()) return;
        if (player.hasPermission(WorldsPerms.BYPASS_FLY)) return;
        if (!WorldsConfig.NO_FLY_WORLDS.get().contains(player.getWorld().getName())) return;

        player.setAllowFlight(false);
        player.setFlying(false);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldCommandsBlocked(PlayerCommandPreprocessEvent e) {
        if (!WorldsConfig.COMMAND_BLOCKER_ENABLED.get()) return;

        Player player = e.getPlayer();
        if (player.hasPermission(WorldsPerms.BYPASS_COMMANDS)) return;

        Set<String> deniedCommands = WorldsConfig.COMMAND_BLOCKER_COMMANDS.get().get(player.getWorld().getName().toLowerCase());
        if (deniedCommands == null || deniedCommands.isEmpty()) return;

        String command = StringUtil.extractCommandName(e.getMessage());
        boolean doBlock = CommandRegister.getAliases(command, true).stream().anyMatch(deniedCommands::contains);

        if (doBlock) {
            plugin.getMessage(WorldsLang.ERROR_COMMAND_BLOCKED).send(player);
            e.setCancelled(true);
        }
    }
}
