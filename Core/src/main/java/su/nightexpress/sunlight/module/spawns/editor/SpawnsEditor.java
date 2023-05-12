package su.nightexpress.sunlight.module.spawns.editor;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenuAuto;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.spawns.impl.Spawn;
import su.nightexpress.sunlight.module.spawns.SpawnsModule;
import su.nightexpress.sunlight.module.spawns.util.Placeholders;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class SpawnsEditor extends AbstractEditorMenuAuto<SunLight, SpawnsModule, Spawn> {

    private final SpawnsModule spawnsModule;

    public SpawnsEditor(@NotNull SpawnsModule module) {
        super(module.plugin(), module, Placeholders.EDITOR_TITLE, 45);
        this.spawnsModule = module;

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                this.onItemClickDefault(player, type2);
            }
        };
        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(MenuItemType.PAGE_NEXT, 44);
        map.put(MenuItemType.PAGE_PREVIOUS, 36);
        map.put(MenuItemType.CLOSE, 40);
    }

    @Override
    public int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    @NotNull
    protected MenuClick getObjectClick(@NotNull Player player, @NotNull Spawn spawn) {
        return (player1, type, e) -> {
            if (e.isShiftClick() && e.isRightClick()) {
                spawn.getSpawnManager().deleteSpawn(spawn);
                this.open(player, 1);
                return;
            }
            spawn.getEditor().open(player1, 1);
        };
    }

    @Override
    @NotNull
    protected List<Spawn> getObjects(@NotNull Player player) {
        return this.spawnsModule.getSpawns().stream()
            .sorted(Comparator.comparingInt(Spawn::getPriority).thenComparing(Spawn::getName)).toList();
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull Spawn spawn) {
        ItemStack item = SpawnsEditorType.SPAWN_OBJECT.getItem();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        ItemUtil.replace(item, spawn.replacePlaceholders());
        return item;
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
