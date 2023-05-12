package su.nightexpress.sunlight.module.kits.editor;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.editor.EditorInput;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenuAuto;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.kits.Kit;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.config.KitsLang;
import su.nightexpress.sunlight.module.kits.util.Placeholders;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class KitsEditor extends AbstractEditorMenuAuto<SunLight, KitsModule, Kit> {

    public KitsEditor(@NotNull KitsModule kitsModule) {
        super(kitsModule.plugin(), kitsModule, Placeholders.EDITOR_TITLE, 45);

        EditorInput<KitsModule, KitsEditorType> input = (player, kitManager2, type, e) -> {
            if (type == KitsEditorType.KIT_CREATE) {
                return kitManager2.create(player, e.getMessage());
            }
            return true;
        };

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                this.onItemClickDefault(player, type2);
            }
            else if (type instanceof KitsEditorType type2) {
                if (type2 == KitsEditorType.KIT_CREATE) {
                    EditorManager.startEdit(player, kitsModule, type2, input);
                    EditorManager.prompt(player, plugin.getMessage(KitsLang.EDITOR_ENTER_KIT_ID).getLocalized());
                    player.closeInventory();
                }
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(KitsEditorType.KIT_CREATE, 41);
        map.put(MenuItemType.CLOSE, 39);
        map.put(MenuItemType.PAGE_NEXT, 44);
        map.put(MenuItemType.PAGE_PREVIOUS, 36);
    }

    @Override
    public int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    @NotNull
    protected List<Kit> getObjects(@NotNull Player player) {
        return this.parent.getKits().stream().sorted(Comparator.comparingInt(Kit::getPriority).reversed()).toList();
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull Kit kit) {
        ItemStack icon = kit.getIcon();
        ItemMeta meta = icon.getItemMeta();
        if (meta == null) return icon;

        ItemStack object = KitsEditorType.KIT_OBJECT.getItem();

        meta.setDisplayName(ItemUtil.getItemName(object));
        meta.setLore(ItemUtil.getLore(object));
        icon.setItemMeta(meta);
        ItemUtil.replace(icon, kit.replacePlaceholders());
        return icon;
    }

    @Override
    @NotNull
    protected MenuClick getObjectClick(@NotNull Player player, @NotNull Kit kit) {
        return (player1, type, e) -> {
            if (e.isShiftClick() && e.isRightClick()) {
                player.closeInventory();
                this.parent.delete(kit);
                this.open(player, 1);
                return;
            }
            kit.getEditor().open(player1, 1);
        };
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
