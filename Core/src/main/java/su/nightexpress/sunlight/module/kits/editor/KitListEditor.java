package su.nightexpress.sunlight.module.kits.editor;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuSize;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.api.AutoFill;
import su.nightexpress.nightcore.menu.api.AutoFilled;
import su.nightexpress.nightcore.menu.impl.EditorMenu;
import su.nightexpress.nightcore.util.ItemReplacer;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.kits.Kit;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.config.KitsLang;

import java.util.Comparator;
import java.util.stream.IntStream;

public class KitListEditor extends EditorMenu<SunLightPlugin, KitsModule> implements AutoFilled<Kit> {

    private final KitsModule module;

    public KitListEditor(@NotNull SunLightPlugin plugin, @NotNull KitsModule module) {
        super(plugin, KitsLang.EDITOR_TITLE_LIST.getString(), MenuSize.CHEST_45);
        this.module = module;

        this.addExit(39);
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(KitsLang.EDITOR_KIT_CREATE, 41, (viewer, event, kit) -> {
            this.handleInput(viewer, KitsLang.EDITOR_ENTER_KIT_ID, (dialog, input) -> {
                if (!module.createKit(input.getTextRaw())) {
                    dialog.error(KitsLang.EDITOR_ERROR_ALREADY_EXISTS.getMessage());
                }
                return true;
            });
        });
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    public void onAutoFill(@NotNull MenuViewer viewer, @NotNull AutoFill<Kit> autoFill) {
        autoFill.setSlots(IntStream.range(0, 36).toArray());
        autoFill.setItems(this.module.getKits().stream().sorted(Comparator.comparingInt(Kit::getPriority).reversed()).toList());
        autoFill.setItemCreator(kit -> {
            ItemStack item = kit.getIcon();
            ItemReplacer.create(item).hideFlags().trimmed()
                .readLocale(KitsLang.EDITOR_KIT_OBJECT)
                .replace(kit.getPlaceholders())
                .writeMeta();
            return item;
        });
        autoFill.setClickAction(kit -> (viewer1, event) -> {
            if (event.isShiftClick() && event.isRightClick()) {
                this.module.deleteKit(kit);
                this.runNextTick(() -> this.flush(viewer));
                return;
            }
            this.runNextTick(() -> this.module.openKitSettings(viewer.getPlayer(), kit));
        });
    }
}
