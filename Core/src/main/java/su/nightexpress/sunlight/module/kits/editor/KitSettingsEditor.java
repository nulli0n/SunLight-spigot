package su.nightexpress.sunlight.module.kits.editor;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.Menu;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.integration.VaultHook;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.EngineUtils;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.kits.Kit;
import su.nightexpress.sunlight.module.kits.config.KitsLang;
import su.nightexpress.sunlight.module.kits.util.Placeholders;

public class KitSettingsEditor extends EditorMenu<SunLight, Kit> {

    public KitSettingsEditor(@NotNull Kit kit) {
        super(kit.getModule().plugin(), kit, Placeholders.EDITOR_TITLE, 45);

        this.addReturn(40).setClick((viewer, event) -> {
            kit.getModule().getEditor().openNextTick(viewer, 1);
        });

        this.addItem(Material.NAME_TAG, EditorLocales.KIT_NAME, 2).setClick((viewer, event) -> {
            this.handleInput(viewer, KitsLang.EDITOR_ENTER_NAME, wrapper -> {
                kit.setName(wrapper.getText());
                kit.save();
                return true;
            });
        });

        this.addItem(Material.ITEM_FRAME, EditorLocales.KIT_ICON, 4).setClick((viewer, event) -> {
            ItemStack cursor = event.getCursor();
            if (cursor == null || cursor.getType().isAir()) return;

            kit.setIcon(cursor);
            kit.save();
            event.getWhoClicked().setItemOnCursor(null);
            this.save(viewer);
        }).getOptions().addDisplayModifier((viewer, item) -> {
            item.setType(kit.getIcon().getType());
            item.setItemMeta(kit.getIcon().getItemMeta());
            ItemUtil.mapMeta(item, meta -> {
                meta.setDisplayName(EditorLocales.KIT_ICON.getLocalizedName());
                meta.setLore(EditorLocales.KIT_ICON.getLocalizedLore());
            });
        });

        this.addItem(Material.MAP, EditorLocales.KIT_DESCRIPTION, 6).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                kit.getDescription().clear();
                this.save(viewer);
                return;
            }
            this.handleInput(viewer, KitsLang.EDITOR_ENTER_DESCRIPTION, wrapper -> {
                kit.getDescription().add(Colorizer.apply(wrapper.getText()));
                kit.save();
                return true;
            });
        });


        this.addItem(Material.COMPARATOR, EditorLocales.KIT_PRIORITY, 10).setClick((viewer, event) -> {
            this.handleInput(viewer, KitsLang.EDITOR_ENTER_PRIORITY, wrapper -> {
                kit.setPriority(wrapper.asInt());
                kit.save();
                return true;
            });
        });

        this.addItem(Material.REDSTONE_TORCH, EditorLocales.KIT_PERMISSION, 12).setClick((viewer, event) -> {
            kit.setPermissionRequired(!kit.isPermissionRequired());
            this.save(viewer);
        });

        this.addItem(Material.CLOCK, EditorLocales.KIT_COOLDOWN, 14).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                kit.setCooldown(-1);
                this.save(viewer);
                return;
            }
            this.handleInput(viewer, KitsLang.EDITOR_ENTER_COOLDOWN, wrapper -> {
                kit.setCooldown(wrapper.asInt());
                kit.save();
                return true;
            });
        });

        this.addItem(Material.GOLD_NUGGET, EditorLocales.KIT_COST, 16).setClick((viewer, event) -> {
            if (!EngineUtils.hasVault() || !VaultHook.hasEconomy()) return;
            if (event.isRightClick()) {
                kit.setCost(0);
                this.save(viewer);
                return;
            }

            this.handleInput(viewer, KitsLang.EDITOR_ENTER_COST, wrapper -> {
                kit.setCost(wrapper.asDouble());
                kit.save();
                return true;
            });
        });


        this.addItem(Material.ARMOR_STAND, EditorLocales.KIT_ARMOR, 20).setClick((viewer, event) -> {
            new ContentEditor(this.object, 9).openNextTick(viewer, 1);
        });

        this.addItem(Material.COMMAND_BLOCK, EditorLocales.KIT_COMMANDS, 22).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                kit.getCommands().clear();
                this.save(viewer);
                return;
            }

            this.handleInput(viewer, KitsLang.EDITOR_ENTER_COMMAND, wrapper -> {
                kit.getCommands().add(Colorizer.plain(wrapper.getText()));
                kit.save();
                return true;
            });
        });

        this.addItem(Material.CHEST_MINECART, EditorLocales.KIT_INVENTORY, 24).setClick((viewer, event) -> {
            new ContentEditor(this.object, 36).openNextTick(viewer, 1);
        });

        this.getItems().forEach(menuItem -> {
            menuItem.getOptions().addDisplayModifier((viewer, item) -> ItemUtil.replace(item, kit.replacePlaceholders()));
        });
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.save();
        this.openNextTick(viewer, 1);
    }

    @Override
    public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
        super.onClick(viewer, item, slotType, slot, event);
        if (slotType == SlotType.PLAYER || slotType == SlotType.PLAYER_EMPTY) {
            event.setCancelled(false);
        }
    }

    private static class ContentEditor extends Menu<SunLight> {

        private final Kit     kit;
        private final boolean isArmor;

        public ContentEditor(@NotNull Kit kit, int size) {
            super(kit.getModule().plugin(), "Kit Content", size);
            this.kit = kit;
            this.isArmor = size == 9;
        }

        @Override
        public boolean isPersistent() {
            return false;
        }

        @Override
        public void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {
            inventory.setContents(this.isArmor ? this.kit.getArmor() : this.kit.getItems());
        }

        @Override
        public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
            super.onClick(viewer, item, slotType, slot, event);
            event.setCancelled(false);
        }

        @Override
        public void onClose(@NotNull MenuViewer viewer, @NotNull InventoryCloseEvent event) {

            Inventory inventory = event.getInventory();
            ItemStack[] items = new ItemStack[this.isArmor ? 4 : 36];

            for (int slot = 0; slot < items.length; slot++) {
                ItemStack item = inventory.getItem(slot);
                if (item == null) continue;

                items[slot] = item;
            }

            if (this.isArmor) {
                this.kit.setArmor(items);
                this.kit.setExtras(new ItemStack[]{inventory.getItem(4)});
            }
            else this.kit.setItems(items);

            this.kit.save();
            this.kit.getEditor().openNextTick(viewer, 1);
            super.onClose(viewer, event);
        }
    }
}
