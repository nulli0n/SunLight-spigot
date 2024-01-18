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
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.kits.util.KitsUtils;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.config.KitsConfig;

public class KitListener extends AbstractListener<SunLight> {

    //private final KitsManager kitsManager;

    public KitListener(@NotNull KitsModule kitsModule) {
        super(kitsModule.plugin());
        //this.kitsManager = kitsManager;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBindItemPickup(EntityPickupItemEvent e) {
        if (!KitsConfig.BIND_ITEMS_TO_PLAYERS.get()) return;

        ItemStack item = e.getItem().getItemStack();
        LivingEntity entity = e.getEntity();
        if (!KitsUtils.isItemOwner(item, entity)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBindItemClick(InventoryClickEvent e) {
        if (!KitsConfig.BIND_ITEMS_TO_PLAYERS.get()) return;

        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType().isAir()) return;

        Player player = (Player) e.getWhoClicked();
        if (!KitsUtils.isItemOwner(item, player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBindItemDispense(BlockDispenseArmorEvent e) {
        if (!KitsConfig.BIND_ITEMS_TO_PLAYERS.get()) return;

        ItemStack item = e.getItem();
        LivingEntity entity = e.getTargetEntity();
        if (!KitsUtils.isItemOwner(item, entity)) {
            e.setCancelled(true);
        }
    }
}
