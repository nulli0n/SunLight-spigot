package su.nightexpress.sunlight.module.kits.editor;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.integration.VaultHook;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuSize;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.click.ClickResult;
import su.nightexpress.nightcore.menu.impl.AbstractMenu;
import su.nightexpress.nightcore.menu.impl.EditorMenu;
import su.nightexpress.nightcore.util.ItemReplacer;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.kits.Kit;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.config.KitsLang;

public class KitSettingsEditor extends EditorMenu<SunLightPlugin, Kit> {

    public KitSettingsEditor(@NotNull SunLightPlugin plugin, @NotNull KitsModule module) {
        super(plugin, KitsLang.EDITOR_TITLE_SETTINGS.getString(), MenuSize.CHEST_45);

        this.addReturn(40, (viewer, event, kit) -> {
            this.runNextTick(() -> module.openEditor(viewer.getPlayer()));
        });

        this.addItem(Material.NAME_TAG, KitsLang.KIT_SET_NAME, 11, (viewer, event, kit) -> {
            this.handleInput(viewer, KitsLang.EDITOR_INPUT_GENERIC_NAME, (dialog, input) -> {
                kit.setName(input.getText());
                kit.save();
                return true;
            });
        });

        this.addItem(Material.WRITABLE_BOOK, KitsLang.EDITOR_KIT_SET_DESCRIPTION, 12, (viewer, event, kit) -> {
            if (event.isRightClick()) {
                kit.getDescription().clear();
                this.save(viewer);
                return;
            }
            this.handleInput(viewer, KitsLang.EDITOR_ENTER_DESCRIPTION, (dialog, input) -> {
                kit.getDescription().add(input.getText());
                kit.save();
                return true;
            });
        });

        this.addItem(Material.ITEM_FRAME, KitsLang.EDITOR_KIT_SET_ICON, 13, (viewer, event, kit) -> {
            ItemStack cursor = event.getCursor();
            if (cursor == null || cursor.getType().isAir()) return;

            kit.setIcon(cursor);
            kit.save();
            event.getWhoClicked().setItemOnCursor(null);
            this.save(viewer);
        }).getOptions().addDisplayModifier((viewer, item) -> {
            Kit kit = this.getLink(viewer);

            item.setType(kit.getIcon().getType());
            item.setItemMeta(kit.getIcon().getItemMeta());
            ItemReplacer.create(item).readLocale(KitsLang.EDITOR_KIT_SET_ICON).writeMeta();
        });

        this.addItem(Material.COMPARATOR, KitsLang.EDITOR_KIT_SET_PRIORITY, 14, (viewer, event, kit) -> {
            this.handleInput(viewer, KitsLang.EDITOR_ENTER_PRIORITY, (dialog, input) -> {
                kit.setPriority(input.asInt());
                kit.save();
                return true;
            });
        });

        this.addItem(Material.REDSTONE_TORCH, KitsLang.EDITOR_KIT_SET_PERMISSION, 15, (viewer, event, kit) -> {
            kit.setPermissionRequired(!kit.isPermissionRequired());
            this.save(viewer);
        });



        this.addItem(Material.GOLD_NUGGET, KitsLang.EDITOR_KIT_SET_COST, 20, (viewer, event, kit) -> {
            if (!Plugins.hasVault() || !VaultHook.hasEconomy()) return;
            if (event.isRightClick()) {
                kit.setCost(0);
                this.save(viewer);
                return;
            }

            this.handleInput(viewer, KitsLang.EDITOR_ENTER_COST, (dialog, input) -> {
                kit.setCost(input.asDouble());
                kit.save();
                return true;
            });
        });

        this.addItem(Material.CLOCK, KitsLang.EDITOR_KIT_SET_COOLDOWN, 21, (viewer, event, kit) -> {
            if (event.getClick() == ClickType.SWAP_OFFHAND) {
                kit.setCooldown(-1);
                this.save(viewer);
                return;
            }
            if (event.getClick() == ClickType.DROP) {
                kit.setCooldown(0);
                this.save(viewer);
                return;
            }

            this.handleInput(viewer, KitsLang.EDITOR_INPUT_GENERIC_SECONDS, (dialog, input) -> {
                kit.setCooldown(input.asInt());
                kit.save();
                return true;
            });
        });

        this.addItem(Material.COMMAND_BLOCK, KitsLang.EDITOR_KIT_SET_COMMANDS, 22, (viewer, event, kit) -> {
            if (event.isRightClick()) {
                kit.getCommands().clear();
                this.save(viewer);
                return;
            }

            this.handleInput(viewer, KitsLang.EDITOR_ENTER_COMMAND, (dialog, input) -> {
                kit.getCommands().add(input.getText());
                kit.save();
                return true;
            });
        });

        this.addItem(Material.ARMOR_STAND, KitsLang.EDITOR_KIT_ARMOR, 23, (viewer, event, kit) -> {
            this.runNextTick(() -> new ContentEditor(plugin, kit, MenuSize.CHEST_9).open(viewer));
        });

        this.addItem(Material.CHEST, KitsLang.EDITOR_KIT_INVENTORY, 24, (viewer, event, kit) -> {
            this.runNextTick(() -> new ContentEditor(plugin, kit, MenuSize.CHEST_36).open(viewer));
        });

        this.getItems().forEach(menuItem -> {
            menuItem.getOptions().addDisplayModifier((viewer, item) -> ItemReplacer.replace(item, this.getLink(viewer).replacePlaceholders()));
        });
    }

    private void save(@NotNull MenuViewer viewer) {
        this.getLink(viewer).save();
        this.runNextTick(() -> this.flush(viewer));
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {

    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    public void onClick(@NotNull MenuViewer viewer, @NotNull ClickResult result, @NotNull InventoryClickEvent event) {
        super.onClick(viewer, result, event);
        if (result.isInventory()) {
            event.setCancelled(false);
        }
    }

    private static class ContentEditor extends AbstractMenu<SunLightPlugin> {

        private final Kit     kit;
        private final boolean isArmor;

        public ContentEditor(@NotNull SunLightPlugin plugin, @NotNull Kit kit, MenuSize size) {
            super(plugin, KitsLang.EDITOR_TITLE_CONTENT.getString(), size);
            this.kit = kit;
            this.isArmor = size == MenuSize.CHEST_9;
        }

        @Override
        public boolean isPersistent() {
            return false;
        }

        @Override
        protected void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {

        }

        @Override
        public void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {
            if (this.isArmor) {
                inventory.setContents(this.kit.getArmor());
                inventory.setItem(4, this.kit.getExtras()[0]);
            }
            else inventory.setContents(this.kit.getItems());
        }

        @Override
        public void onClick(@NotNull MenuViewer viewer, @NotNull ClickResult result, @NotNull InventoryClickEvent event) {
            super.onClick(viewer, result, event);
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
            this.runNextTick(() -> this.kit.getModule().openKitSettings(viewer.getPlayer(), this.kit));
            super.onClose(viewer, event);
        }
    }
}
