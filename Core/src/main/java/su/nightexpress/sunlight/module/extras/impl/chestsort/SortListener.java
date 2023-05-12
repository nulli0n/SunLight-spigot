package su.nightexpress.sunlight.module.extras.impl.chestsort;

import org.bukkit.GameMode;
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
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.data.impl.SunUser;

public class SortListener extends AbstractListener<SunLight> {

    private final SortManager manager;

    public SortListener(@NotNull SortManager manager) {
        super(manager.plugin());
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSortInventoryClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR) return;

        Inventory inventory = e.getInventory();
        InventoryHolder holder = inventory.getHolder();
        if (!(holder instanceof Chest) && !(holder instanceof DoubleChest) && !(holder instanceof ShulkerBox)) {
            return;
        }

        SunUser user = plugin.getUserManager().getUserData(player);
        if (!SortManager.isChestSortEnabled(user)) return;

        this.manager.sortInventory(inventory);
    }
}
