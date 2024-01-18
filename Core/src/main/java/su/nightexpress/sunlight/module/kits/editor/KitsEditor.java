package su.nightexpress.sunlight.module.kits.editor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.kits.Kit;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.config.KitsLang;
import su.nightexpress.sunlight.module.kits.util.Placeholders;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class KitsEditor extends EditorMenu<SunLight, KitsModule> implements AutoPaged<Kit> {

    public KitsEditor(@NotNull KitsModule kitsModule) {
        super(kitsModule.plugin(), kitsModule, Placeholders.EDITOR_TITLE, 45);

        this.addExit(39);
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.KIT_CREATE, 41).setClick((viewer, event) -> {
            this.handleInput(viewer, KitsLang.EDITOR_ENTER_KIT_ID, wrapper -> {
                return kitsModule.create(viewer.getPlayer(), StringUtil.lowerCaseUnderscore(wrapper.getTextRaw()));
            });
        });
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        this.getItemsForPage(viewer).forEach(this::addItem);
    }

    @Override
    public int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    @NotNull
    public List<Kit> getObjects(@NotNull Player player) {
        return this.object.getKits().stream().sorted(Comparator.comparingInt(Kit::getPriority).reversed()).toList();
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull Kit kit) {
        ItemStack item = kit.getIcon();
        ItemUtil.mapMeta(item, meta -> {
            meta.setDisplayName(EditorLocales.KIT_OBJECT.getLocalizedName());
            meta.setLore(EditorLocales.KIT_OBJECT.getLocalizedLore());
            meta.addItemFlags(ItemFlag.values());
            ItemUtil.replace(meta, kit.replacePlaceholders());
        });
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull Kit kit) {
        return (viewer, event) -> {
            if (event.isShiftClick() && event.isRightClick()) {
                this.object.delete(kit);
                this.openNextTick(viewer, 1);
                return;
            }
            kit.getEditor().openNextTick(viewer, 1);
        };
    }
}
