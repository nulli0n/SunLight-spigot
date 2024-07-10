package su.nightexpress.sunlight.module.homes.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuSize;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.api.AutoFill;
import su.nightexpress.nightcore.menu.api.AutoFilled;
import su.nightexpress.nightcore.menu.impl.ConfigMenu;
import su.nightexpress.nightcore.menu.item.ItemHandler;
import su.nightexpress.nightcore.menu.item.MenuItem;
import su.nightexpress.nightcore.menu.link.Linked;
import su.nightexpress.nightcore.menu.link.ViewLink;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.impl.Home;

import java.util.*;
import java.util.stream.IntStream;

import static su.nightexpress.sunlight.module.homes.util.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class HomesMenu extends ConfigMenu<SunLightPlugin> implements AutoFilled<Home>, Linked<UUID> {

    private static final String FILE_NAME = "home_list.yml";

    private static final String IS_RESPAWN = "%respawn%";
    private static final String IS_DEFAULT = "%is_default%";

    private final HomesModule    module;
    private final ViewLink<UUID> link;
    
    private String       homeName;
    private List<String> homeLore;
    private int[]        homeSlots;

    private List<String> loreIsRespawn;
    private List<String> loreIsDefault;

    public HomesMenu(@NotNull SunLightPlugin plugin, @NotNull HomesModule module) {
        super(plugin, FileConfig.loadOrExtract(plugin, module.getLocalUIPath(), FILE_NAME));
        this.module = module;
        this.link = new ViewLink<>();

        this.load();
    }

    @NotNull
    @Override
    public ViewLink<UUID> getLink() {
        return link;
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    public void onAutoFill(@NotNull MenuViewer viewer, @NotNull AutoFill<Home> autoFill) {
        Player player = viewer.getPlayer();
        UUID targetId = this.getLink(player);

        autoFill.setSlots(this.homeSlots);
        autoFill.setItems(this.module.getHomes(targetId).values().stream().sorted(Comparator.comparing(Home::getId)).toList());
        autoFill.setItemCreator(home -> {
            ItemStack item = home.getIcon();

            List<String> isDefaultLore = home.isDefault() ? new ArrayList<>(this.loreIsDefault) : Collections.emptyList();
            List<String> isRespawnLore = home.isRespawnPoint() ? new ArrayList<>(this.loreIsRespawn) : Collections.emptyList();

            ItemReplacer.create(item).hideFlags().trimmed()
                .setDisplayName(this.homeName)
                .setLore(this.homeLore)
                .replace(IS_DEFAULT, isDefaultLore)
                .replace(IS_RESPAWN, isRespawnLore)
                .replace(home.replacePlaceholders())
                .writeMeta();

            return item;
        });
        autoFill.setClickAction(home -> (viewer1, event) -> {
            if (!this.module.isLoaded(targetId)) {
                this.runNextTick(player::closeInventory);
                return;
            }

            if (event.isRightClick()) {
                if (event.isShiftClick()) {
                    this.module.removeHome(home);
                    this.runNextTick(() -> this.open(player, viewer.getPage()));
                    return;
                }
                this.runNextTick(() -> this.module.openHomeSettings(player, home));
            }
            else {
                home.teleport(player);
            }
        });
    }

    @Override
    @NotNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions(BLACK.enclose("Homes"), MenuSize.CHEST_36);
    }

    @Override
    @NotNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack close = ItemUtil.getSkinHead(SKIN_WRONG_MARK);
        ItemUtil.editMeta(close, meta -> {
            meta.setDisplayName(Lang.EDITOR_ITEM_CLOSE.getDefaultName());
        });
        list.add(new MenuItem(close).setPriority(10).setSlots(31).setHandler(ItemHandler.forClose(this)));

        ItemStack nextPage = ItemUtil.getSkinHead(SKIN_ARROW_RIGHT);
        ItemUtil.editMeta(nextPage, meta -> {
            meta.setDisplayName(Lang.EDITOR_ITEM_NEXT_PAGE.getDefaultName());
        });
        list.add(new MenuItem(nextPage).setPriority(10).setSlots(35).setHandler(ItemHandler.forNextPage(this)));

        ItemStack backPage = ItemUtil.getSkinHead(SKIN_ARROW_LEFT);
        ItemUtil.editMeta(backPage, meta -> {
            meta.setDisplayName(Lang.EDITOR_ITEM_PREVIOUS_PAGE.getDefaultName());
        });
        list.add(new MenuItem(backPage).setPriority(10).setSlots(27).setHandler(ItemHandler.forPreviousPage(this)));

        return list;
    }

    @Override
    protected void loadAdditional() {
        this.homeName = ConfigValue.create("Home.Name", 
            LIGHT_YELLOW.enclose(BOLD.enclose("Home: ")) + WHITE.enclose(HOME_NAME) + GRAY.enclose(" (ID: " + WHITE.enclose(HOME_ID) + ")")
        ).read(cfg);

        this.homeLore = ConfigValue.create("Home.Lore.Default", Lists.newList(
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("World: ") + HOME_LOCATION_WORLD),
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("X: ") + HOME_LOCATION_X),
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Y: ") + HOME_LOCATION_Y),
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Z: ") + HOME_LOCATION_Z),
            "",
            IS_RESPAWN,
            IS_DEFAULT,
            "",
            LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Left-Click to " + LIGHT_YELLOW.enclose("teleport") + "."),
            LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Right-Click for " + LIGHT_YELLOW.enclose("settings") + "."),
            LIGHT_GRAY.enclose(LIGHT_RED.enclose("[▶]") + " Shift-Right to " + LIGHT_RED.enclose("delete") + ".")
        )).read(cfg);
        
        this.loreIsRespawn = ConfigValue.create("Home.Lore.Respawn", Lists.newList(
            LIGHT_GRAY.enclose(LIGHT_GREEN.enclose("✔") + " This home is set as " + LIGHT_GREEN.enclose("respawn") + " point.")
        )).read(cfg);
        
        this.loreIsDefault = ConfigValue.create("Home.Lore.Is_Default", Lists.newList(
            LIGHT_GRAY.enclose(LIGHT_GREEN.enclose("✔") + " This home is set as " + LIGHT_GREEN.enclose("default") + " home.")
        )).read(cfg);
        
        this.homeSlots = ConfigValue.create("Home.Slots", IntStream.range(10, 17).toArray()).read(cfg);
    }
}
