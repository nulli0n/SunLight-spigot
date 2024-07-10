package su.nightexpress.sunlight.module.kits.menu;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuSize;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.impl.ConfigMenu;
import su.nightexpress.nightcore.menu.item.ItemHandler;
import su.nightexpress.nightcore.menu.item.MenuItem;
import su.nightexpress.nightcore.menu.link.Linked;
import su.nightexpress.nightcore.menu.link.ViewLink;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.kits.Kit;
import su.nightexpress.sunlight.module.kits.KitsModule;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static su.nightexpress.nightcore.util.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class KitPreviewMenu extends ConfigMenu<SunLightPlugin> implements Linked<Kit> {

    private static final String FILE_NAME = "kit_preview.yml";

    private int[] itemSlots;
    private int[] armorSlots;
    private int[] extraSlots;

    private final ViewLink<Kit> link;
    private final ItemHandler returnHandler;

    public KitPreviewMenu(@NotNull SunLightPlugin plugin, @NotNull KitsModule module) {
        super(plugin, FileConfig.loadOrExtract(plugin, module.getLocalUIPath(), FILE_NAME));
        this.link = new ViewLink<>();

        this.addHandler(this.returnHandler = ItemHandler.forReturn(this, (viewer, event) -> {
            this.runNextTick(() -> module.openKitsMenu(viewer.getPlayer()));
        }));

        this.load();
    }

    @NotNull
    @Override
    public ViewLink<Kit> getLink() {
        return link;
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {

    }

    @Override
    public void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {
        Kit kit = this.getLink(viewer);

        int count = 0;
        for (ItemStack item : kit.getItems()) {
            if (count >= itemSlots.length || count >= inventory.getSize()) break;
            inventory.setItem(itemSlots[count++], item);
        }

        int armorCount = 0;
        for (ItemStack armor : kit.getArmor()) {
            if (armorCount >= armorSlots.length || armorCount >= inventory.getSize()) break;
            inventory.setItem(armorSlots[armorCount++], armor);
        }

        for (int index = 0; index < extraSlots.length; index++) {
            inventory.setItem(extraSlots[index], kit.getExtras()[index]);
        }
    }

    @Override
    @NotNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions(BLACK.enclose("Kit Preivew"), MenuSize.CHEST_54);
    }

    @Override
    @NotNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack back = ItemUtil.getSkinHead(SKIN_ARROW_DOWN);
        ItemUtil.editMeta(back, meta -> {
            meta.setDisplayName(Lang.EDITOR_ITEM_RETURN.getDefaultName());
        });
        list.add(new MenuItem(back).setPriority(10).setSlots(49).setHandler(this.returnHandler));

        return list;
    }

    @Override
    protected void loadAdditional() {
        itemSlots = ConfigValue.create("Item_Slots", IntStream.range(9, 45).toArray()).read(cfg);

        armorSlots = ConfigValue.create("Armor_Slots", new int[] {5,4,3,2}).read(cfg);

        extraSlots = ConfigValue.create("Extra_Slots", new int[] {6}).read(cfg);
    }
}
