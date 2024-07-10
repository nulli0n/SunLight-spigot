package su.nightexpress.sunlight.module.kits.listener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.util.KitsUtils;

public class KitBindListener extends AbstractListener<SunLightPlugin> {

    public KitBindListener(@NotNull SunLightPlugin plugin, @NotNull KitsModule module) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBindItemPickup(EntityPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();
        LivingEntity entity = event.getEntity();
        if (!KitsUtils.isItemOwner(item, entity)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBindItemClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType().isAir()) return;

        Player player = (Player) event.getWhoClicked();
        if (!KitsUtils.isItemOwner(item, player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBindItemDispense(BlockDispenseArmorEvent event) {
        ItemStack item = event.getItem();
        LivingEntity entity = event.getTargetEntity();
        if (!KitsUtils.isItemOwner(item, entity)) {
            event.setCancelled(true);
        }
    }
}
