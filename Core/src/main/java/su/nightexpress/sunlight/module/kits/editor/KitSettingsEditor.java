package su.nightexpress.sunlight.module.kits.editor;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.editor.EditorInput;
import su.nexmedia.engine.api.menu.AbstractMenu;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenu;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.hooks.external.VaultHook;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.kits.Kit;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.config.KitsLang;
import su.nightexpress.sunlight.module.kits.util.Placeholders;

import java.util.Map;

public class KitSettingsEditor extends AbstractEditorMenu<SunLight, Kit> {

    private final KitsModule kitsModule;

    public KitSettingsEditor(@NotNull Kit kit) {
        super(kit.getKitManager().plugin(), kit, Placeholders.EDITOR_TITLE, 45);
        this.kitsModule = kit.getKitManager();

        EditorInput<Kit, KitsEditorType> input = (player, kit2, type, e) -> {
            String text = e.getMessage();
            switch (type) {
                case KIT_CHANGE_COMMANDS -> kit.getCommands().add(Colorizer.plain(text));
                case KIT_CHANGE_COOLDOWN -> kit.setCooldown(StringUtil.getInteger(text, 0));
                case KIT_CHANGE_COST -> kit.setCost(StringUtil.getDouble(text, 0D));
                case KIT_CHANGE_PRIORITY -> kit.setPriority(StringUtil.getInteger(text, 0));
                case KIT_CHANGE_NAME -> kit.setName(text);
                case KIT_CHANGE_DESCRIPTION -> kit.getDescription().add(Colorizer.apply(text));
                default -> {}
            }

            kit.save();
            return true;
        };
        
        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    this.kitsModule.getEditor().open(player, 1);
                }
            }
            else if (type instanceof KitsEditorType type2) {
                switch (type2) {
                    case KIT_CHANGE_ARMOR -> {
                        new ContentEditor(this.object, 9).open(player, 1);
                        return;
                    }
                    case KIT_CHANGE_INVENTORY -> {
                        new ContentEditor(this.object, 36).open(player, 1);
                        return;
                    }
                    case KIT_CHANGE_COMMANDS -> {
                        if (e.isRightClick()) {
                            kit.getCommands().clear();
                            break;
                        }

                        EditorManager.startEdit(player, kit, type2, input);
                        EditorManager.prompt(player, plugin.getMessage(KitsLang.EDITOR_ENTER_COMMAND).getLocalized());
                        EditorManager.sendCommandTips(player);
                        player.closeInventory();
                        return;
                    }
                    case KIT_CHANGE_COOLDOWN -> {
                        if (e.isRightClick()) {
                            kit.setCooldown(-1);
                            break;
                        }
                        EditorManager.startEdit(player, kit, type2, input);
                        EditorManager.prompt(player, plugin.getMessage(KitsLang.EDITOR_ENTER_COOLDOWN).getLocalized());
                        player.closeInventory();
                        return;
                    }
                    case KIT_CHANGE_COST -> {
                        if (!VaultHook.hasEconomy()) return;
                        if (e.isRightClick()) {
                            kit.setCost(0);
                            break;
                        }

                        EditorManager.startEdit(player, kit, type2, input);
                        EditorManager.prompt(player, plugin.getMessage(KitsLang.EDITOR_ENTER_COST).getLocalized());
                        player.closeInventory();
                        return;
                    }
                    case KIT_CHANGE_ICON -> {
                        ItemStack cursor = e.getCursor();
                        if (cursor == null || cursor.getType().isAir()) return;

                        kit.setIcon(cursor);
                        e.getWhoClicked().setItemOnCursor(null);
                    }
                    case KIT_CHANGE_NAME -> {
                        EditorManager.startEdit(player, kit, type2, input);
                        EditorManager.prompt(player, plugin.getMessage(KitsLang.EDITOR_ENTER_NAME).getLocalized());
                        player.closeInventory();
                        return;
                    }
                    case KIT_CHANGE_DESCRIPTION -> {
                        if (e.isRightClick()) {
                            kit.getDescription().clear();
                            break;
                        }
                        EditorManager.startEdit(player, kit, type2, input);
                        EditorManager.prompt(player, plugin.getMessage(KitsLang.EDITOR_ENTER_DESCRIPTION).getLocalized());
                        player.closeInventory();
                        return;
                    }
                    case KIT_CHANGE_PRIORITY -> {
                        EditorManager.startEdit(player, kit, type2, input);
                        EditorManager.prompt(player, plugin.getMessage(KitsLang.EDITOR_ENTER_PRIORITY).getLocalized());
                        player.closeInventory();
                        return;
                    }
                    case KIT_CHANGE_PERMISSION -> kit.setPermissionRequired(!kit.isPermissionRequired());
                    default -> {
                        return;
                    }
                }
                kit.save();
                this.open(player, 1);
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(KitsEditorType.KIT_CHANGE_NAME, 2);
        map.put(KitsEditorType.KIT_CHANGE_ICON, 4);
        map.put(KitsEditorType.KIT_CHANGE_DESCRIPTION, 6);

        map.put(KitsEditorType.KIT_CHANGE_PRIORITY, 10);
        map.put(KitsEditorType.KIT_CHANGE_PERMISSION, 12);
        map.put(KitsEditorType.KIT_CHANGE_COOLDOWN, 14);
        map.put(KitsEditorType.KIT_CHANGE_COST, 16);

        map.put(KitsEditorType.KIT_CHANGE_ARMOR, 20);
        map.put(KitsEditorType.KIT_CHANGE_COMMANDS, 22);
        map.put(KitsEditorType.KIT_CHANGE_INVENTORY, 24);

        map.put(MenuItemType.RETURN, 40);
    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull MenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);
        ItemUtil.replace(item, this.object.replacePlaceholders());
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return slotType != SlotType.PLAYER && slotType != SlotType.EMPTY_PLAYER;
    }

    static class ContentEditor extends AbstractMenu<SunLight> {

        private final Kit     kit;
        private final boolean isArmor;

        public ContentEditor(@NotNull Kit kit, int size) {
            super(kit.getKitManager().plugin(), "Kit Content", size);
            this.kit = kit;
            this.isArmor = size == 9;
        }

        @Override
        public boolean onPrepare(@NotNull Player player, @NotNull Inventory inventory) {
            inventory.setContents(this.isArmor ? this.kit.getArmor() : this.kit.getItems());
            return true;
        }

        @Override
        public void onClose(@NotNull Player player, @NotNull InventoryCloseEvent e) {
            Inventory inventory = e.getInventory();
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
            this.plugin.runTask(task -> this.kit.getEditor().open(player, 1));
            super.onClose(player, e);
        }

        @Override
        public boolean destroyWhenNoViewers() {
            return true;
        }

        @Override
        public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
            return false;
        }
    }
}
