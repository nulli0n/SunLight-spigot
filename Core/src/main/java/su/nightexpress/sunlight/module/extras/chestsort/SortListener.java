package su.nightexpress.sunlight.module.extras.chestsort;

import org.bukkit.GameMode;
import org.bukkit.block.Barrel;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.data.user.SunUser;

public class SortListener extends AbstractListener<SunLightPlugin> {

    private final SortManager manager;

    public SortListener(@NotNull SunLightPlugin plugin, @NotNull SortManager manager) {
        super(plugin);
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSortInventoryClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR) return;

        Inventory inventory = e.getInventory();
        InventoryHolder holder = inventory.getHolder();
        if (!(holder instanceof Chest) && !(holder instanceof DoubleChest) && !(holder instanceof ShulkerBox) && !(holder instanceof Barrel)) {
            return;
        }

        SunUser user = plugin.getUserManager().getUserData(player);
        if (!SortManager.isChestSortEnabled(user)) return;

        this.manager.sortInventory(inventory);
    }
}
