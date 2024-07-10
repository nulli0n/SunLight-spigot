package su.nightexpress.sunlight.module.worlds.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.nightcore.util.CommandUtil;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.worlds.WorldsModule;
import su.nightexpress.sunlight.module.worlds.config.WorldsConfig;
import su.nightexpress.sunlight.module.worlds.config.WorldsLang;
import su.nightexpress.sunlight.module.worlds.config.WorldsPerms;

import java.util.Set;

public class WorldsListener extends AbstractListener<SunLightPlugin> {

    private final WorldsModule module;

    public WorldsListener(@NotNull SunLightPlugin plugin, @NotNull WorldsModule module) {
        super(plugin);
        this.module = module;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onFlyDisableToggle(PlayerToggleFlightEvent event) {
        if (!event.isFlying()) return;

        Player player = event.getPlayer();
        if (this.module.canFlyThere(player)) return;

        event.setCancelled(true);
        player.setAllowFlight(false);
        player.setFlying(false);

        WorldsLang.ERROR_FLY_DISABLED.getMessage().send(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFlyDisableTeleport(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (!player.getAllowFlight() || this.module.canFlyThere(player)) return;

        player.setAllowFlight(false);
        player.setFlying(false);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWorldCommandsBlocked(PlayerCommandPreprocessEvent event) {
        if (!WorldsConfig.COMMAND_BLOCKER_ENABLED.get()) return;

        Player player = event.getPlayer();
        if (player.hasPermission(WorldsPerms.BYPASS) || player.hasPermission(WorldsPerms.BYPASS_COMMANDS)) return;

        Set<String> deniedCommands = WorldsConfig.COMMAND_BLOCKER_COMMANDS.get().get(player.getWorld().getName().toLowerCase());
        if (deniedCommands == null || deniedCommands.isEmpty()) return;

        String command = CommandUtil.getCommandName(event.getMessage());
        boolean doBlock = CommandUtil.getAliases(command, true).stream().anyMatch(deniedCommands::contains);

        if (doBlock) {
            WorldsLang.ERROR_COMMAND_BLOCKED.getMessage().send(player);
            event.setCancelled(true);
        }
    }
}
