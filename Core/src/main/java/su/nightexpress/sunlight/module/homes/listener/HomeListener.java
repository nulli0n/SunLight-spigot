package su.nightexpress.sunlight.module.homes.listener;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.material.Colorable;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.config.HomesConfig;
import su.nightexpress.sunlight.module.homes.impl.Home;
import su.nightexpress.sunlight.module.homes.util.Placeholders;

public class HomeListener extends AbstractListener<SunLightPlugin> {

    private final HomesModule module;

    public HomeListener(@NotNull SunLightPlugin plugin, @NotNull HomesModule module) {
        super(plugin);
        this.module = module;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) return;
        this.module.loadHomes(event.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.module.unloadHomes(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Home home = this.module.getRespawnHome(player);
        if (home == null) return;

        event.setRespawnLocation(home.getLocation());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBedModeInteract(PlayerInteractEvent event) {
        if (!HomesConfig.BED_MODE_ENABLED.get()) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.useInteractedBlock() == Event.Result.DENY) return;

        Block block = event.getClickedBlock();
        if (block == null || !(block.getBlockData() instanceof Bed)) return;

        Player player = event.getPlayer();
        Location location = block.getLocation();

        DyeColor color = ((Colorable)block.getState()).getColor();
        if (color == null) color = DyeColor.RED;

        String homeId = Placeholders.DEFAULT;
        boolean overrideRespawn = HomesConfig.BED_MODE_OVERRIDE_RESPAWN.get();

        if (HomesConfig.BED_MODE_COLORS.get() && color != DyeColor.RED) {
            homeId = color.name().toLowerCase();
        }

        Home home = this.module.getHome(player, homeId);
        if (home == null || player.isSneaking()) {
            event.setUseInteractedBlock(Event.Result.DENY);

            if (this.module.setHome(player, homeId, location, false)) {
                if (overrideRespawn) {
                    home = this.module.getHome(player, homeId);
                    if (home != null) {
                        Home respawnHome = this.module.getRespawnHome(player);
                        if (respawnHome != null) respawnHome.setRespawnPoint(false);

                        home.setRespawnPoint(true);
                        player.setBedSpawnLocation(location);
                    }
                }
            }
            return;
        }

        if (overrideRespawn) {
            event.setUseInteractedBlock(Event.Result.DENY);
            player.setBedSpawnLocation(location);
            player.sleep(location, false);

            Home respawnHome = this.module.getRespawnHome(player);
            if (respawnHome != null) player.setBedSpawnLocation(respawnHome.getLocation());
        }
    }
}
