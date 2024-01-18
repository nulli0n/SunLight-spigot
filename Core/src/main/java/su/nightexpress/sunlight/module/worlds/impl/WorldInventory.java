package su.nightexpress.sunlight.module.worlds.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractConfigHolder;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.worlds.WorldsModule;
import su.nightexpress.sunlight.module.worlds.config.WorldsConfig;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WorldInventory extends AbstractConfigHolder<SunLight> {

    private final WorldsModule                               worldsModule;
    private final Map<String, Map<InventoryType, Inventory>> inventories;

    public WorldInventory(@NotNull WorldsModule worldsModule, @NotNull JYML cfg) {
        super(worldsModule.plugin(), cfg);
        this.worldsModule = worldsModule;
        this.inventories = new HashMap<>();
    }

    @Override
    public boolean load() {
        for (InventoryType type : WorldsConfig.INVENTORY_SPLIT_TYPES) {
            if (!WorldsConfig.isInventoryAffected(type)) continue;

            for (String worldGroup : cfg.getSection(type.name())) {
                ItemStack[] items = cfg.getItemsEncoded(type.name() + "." + worldGroup);

                Inventory inventory = plugin.getServer().createInventory(null, type);
                inventory.setContents(items);
                this.getInventoryGroupMap(worldGroup).put(type, inventory);
            }
        }
        return true;
    }

    @Override
    public void onSave() {
        this.inventories.forEach((worldGroup, groupMap) -> {
            groupMap.forEach((type, inventory) -> {
                cfg.setItemsEncoded(type.name() + "." + worldGroup, Arrays.asList(inventory.getContents()));
            });
        });
    }

    public void doSnapshot(@NotNull Player player, @NotNull String group) {
        // Always save all inventories to avoid items losing when change settings
        for (InventoryType type : WorldsConfig.INVENTORY_SPLIT_TYPES) {
            Inventory inventory = plugin.getServer().createInventory(null, type);
            this.transferContent(this.getInventory(player, type), inventory);
            this.getInventoryGroupMap(group).put(type, inventory);
        }
    }

    public void apply(@NotNull Player player) {
        String worldGroup = this.worldsModule.getWorldGroup(player.getWorld());
        if (worldGroup == null) return;

        for (InventoryType type : WorldsConfig.INVENTORY_SPLIT_TYPES) {
            Inventory inventoryHas = this.getInventory(player, type);
            inventoryHas.clear();

            Inventory inventoryNew = this.getInventoryGroupMap(worldGroup).get(type);
            if (inventoryNew != null) {
                this.transferContent(inventoryNew, inventoryHas);
            }
        }
    }

    @NotNull
    private Map<InventoryType, Inventory> getInventoryGroupMap(@NotNull String group) {
        return this.inventories.computeIfAbsent(group, k -> new HashMap<>());
    }

    @NotNull
    private Inventory getInventory(@NotNull Player player, @NotNull InventoryType type) {
        if (type == InventoryType.PLAYER) return player.getInventory();
        if (type == InventoryType.ENDER_CHEST) return player.getEnderChest();
        throw new UnsupportedOperationException("Unsupported inventory type!");
    }

    private void transferContent(@NotNull Inventory from, @NotNull Inventory to) {
        int slot = 0;
        for (ItemStack item : from.getContents()) {
            to.setItem(slot++, item != null ? new ItemStack(item) : new ItemStack(Material.AIR));
        }
    }
}
