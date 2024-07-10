package su.nightexpress.sunlight.module.worlds.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractFileData;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.worlds.WorldsModule;
import su.nightexpress.sunlight.module.worlds.config.WorldsConfig;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WorldInventories extends AbstractFileData<SunLightPlugin> {

    private final WorldsModule                               module;
    private final Map<String, Map<InventoryType, Inventory>> inventories;

    public WorldInventories(@NotNull SunLightPlugin plugin, @NotNull WorldsModule module, @NotNull File file) {
        super(plugin, file);
        this.module = module;
        this.inventories = new HashMap<>();
    }

    @Override
    protected boolean onLoad(@NotNull FileConfig config) {
        for (InventoryType type : WorldsConfig.INVENTORY_SPLIT_TYPES) {
            if (!WorldsConfig.isInventoryAffected(type)) continue;

            for (String worldGroup : config.getSection(type.name())) {
                ItemStack[] items = config.getItemsEncoded(type.name() + "." + worldGroup);

                Inventory inventory = plugin.getServer().createInventory(null, type);
                inventory.setContents(items);
                this.getInventoryMap(worldGroup).put(type, inventory);
            }
        }
        return true;
    }

    @Override
    protected void onSave(@NotNull FileConfig config) {
        this.inventories.forEach((worldGroup, groupMap) -> {
            groupMap.forEach((type, inventory) -> {
                config.setItemsEncoded(type.name() + "." + worldGroup, Arrays.asList(inventory.getContents()));
            });
        });
    }

    /**
     * Saves current player's inventory to a specified world group.
     * @param player Player to save inventories from.
     * @param group Name of the world group.
     */
    public void saveInventory(@NotNull Player player, @NotNull String group) {
        // Always save all inventories to avoid items losing when change settings
        for (InventoryType type : WorldsConfig.INVENTORY_SPLIT_TYPES) {
            Inventory inventory = plugin.getServer().createInventory(null, type);
            this.transferContent(this.getInventory(player, type), inventory);
            this.getInventoryMap(group).put(type, inventory);
        }
    }

    /**
     * Loads inventories for a player in their current world group.
     * @param player Player to load inventories for.
     */
    public void loadInventory(@NotNull Player player) {
        String worldGroup = this.module.getWorldGroup(player.getWorld());
        if (worldGroup == null) return;

        for (InventoryType type : WorldsConfig.INVENTORY_SPLIT_TYPES) {
            Inventory inventoryHas = this.getInventory(player, type);
            inventoryHas.clear();

            Inventory inventoryNew = this.getInventoryMap(worldGroup).get(type);
            if (inventoryNew != null) {
                this.transferContent(inventoryNew, inventoryHas);
            }
        }
    }

    @NotNull
    private Map<InventoryType, Inventory> getInventoryMap(@NotNull String group) {
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
