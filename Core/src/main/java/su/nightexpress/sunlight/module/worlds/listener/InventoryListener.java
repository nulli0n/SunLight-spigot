package su.nightexpress.sunlight.module.worlds.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.worlds.impl.WorldInventory;
import su.nightexpress.sunlight.module.worlds.WorldsModule;

public class InventoryListener extends AbstractListener<SunLight> {

    private final WorldsModule module;

    public InventoryListener(@NotNull WorldsModule module) {
        super(module.plugin());
        this.module = module;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onWorldInventoryChange(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        String groupTo = this.module.getWorldGroup(player.getWorld());
        String groupFrom = this.module.getWorldGroup(e.getFrom());

        // Do not affect snapshots for the same group
        if (groupFrom != null && groupTo != null) {
            if (groupTo.equalsIgnoreCase(groupFrom)) {
                return;
            }
        }

        WorldInventory worldInventory = this.module.getWorldInventory(player);
        // And here do snapshot for current player inv for world he comes from
        if (groupFrom != null) {
            worldInventory.doSnapshot(player, groupFrom);
        }

        // And now replace it by the inventory of new world
        if (groupTo != null) {
            worldInventory.apply(player);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onWorldInventoryQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        String playerId = player.getUniqueId().toString();

        WorldInventory worldInventory = this.module.getInventoryMap().remove(playerId);
        if (worldInventory != null) {
            worldInventory.save();
        }
    }
}
