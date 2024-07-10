package su.nightexpress.sunlight.module.worlds.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.worlds.impl.WorldInventories;
import su.nightexpress.sunlight.module.worlds.WorldsModule;

public class InventoryListener extends AbstractListener<SunLightPlugin> {

    private final WorldsModule module;

    public InventoryListener(@NotNull SunLightPlugin plugin, @NotNull WorldsModule module) {
        super(plugin);
        this.module = module;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventorySplitChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        String groupTo = this.module.getWorldGroup(player.getWorld());
        String groupFrom = this.module.getWorldGroup(event.getFrom());

        // Do not affect snapshots for the same group
        if (groupFrom != null && groupTo != null) {
            if (groupTo.equalsIgnoreCase(groupFrom)) {
                return;
            }
        }

        WorldInventories worldInventories = this.module.getWorldInventory(player);
        // And here do snapshot for current player inv for world he comes from
        if (groupFrom != null) {
            worldInventories.saveInventory(player, groupFrom);
        }

        // And now replace it by the inventory of new world
        if (groupTo != null) {
            worldInventories.loadInventory(player);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventorySplitQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String playerId = player.getUniqueId().toString();

        WorldInventories worldInventories = this.module.getInventoryMap().remove(playerId);
        if (worldInventories != null) {
            worldInventories.save();
        }
    }
}
