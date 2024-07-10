package su.nightexpress.sunlight.module.homes.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.dialog.Dialog;
import su.nightexpress.nightcore.dialog.DialogHandler;
import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuSize;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.click.ClickResult;
import su.nightexpress.nightcore.menu.impl.ConfigMenu;
import su.nightexpress.nightcore.menu.item.ItemHandler;
import su.nightexpress.nightcore.menu.item.MenuItem;
import su.nightexpress.nightcore.menu.link.Linked;
import su.nightexpress.nightcore.menu.link.ViewLink;
import su.nightexpress.nightcore.util.ItemReplacer;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.config.HomesLang;
import su.nightexpress.sunlight.module.homes.impl.Home;

import java.util.ArrayList;
import java.util.List;

import static su.nightexpress.sunlight.module.homes.util.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class HomeMenu extends ConfigMenu<SunLightPlugin> implements Linked<Home> {

    private static final String FILE_NAME = "home_settings.yml";

    private final HomesModule module;
    private final ViewLink<Home> link;

    private final ItemHandler returnHandler;
    private final ItemHandler locationHandler;
    private final ItemHandler respawnHandler;
    private final ItemHandler setDefaultHandler;
    private final ItemHandler typeHandler;
    private final ItemHandler iconHandler;
    private final ItemHandler inviteHandler;
    private final ItemHandler nameHandler;

    public HomeMenu(@NotNull SunLightPlugin plugin, @NotNull HomesModule module) {
        super(plugin, FileConfig.loadOrExtract(plugin, module.getLocalUIPath(), FILE_NAME));
        this.module = module;
        this.link = new ViewLink<>();

        this.addHandler(this.returnHandler = ItemHandler.forReturn(this, (viewer, event) -> {
            Home home = this.getLink(viewer);
            Player player = viewer.getPlayer();
            this.runNextTick(() -> module.openHomes(player, home.getOwner().getId()));
        }));

        this.addHandler(this.locationHandler = new ItemHandler("home_change_location", (viewer, event) -> {
            this.module.setHome(viewer.getPlayer(), this.getLink(viewer).getId(), false);
        }));

        this.addHandler(this.respawnHandler = new ItemHandler("home_change_respawn_point", (viewer, event) -> {
            Home home = this.getLink(viewer);
            home.setRespawnPoint(!home.isRespawnPoint());
            this.save(viewer);
        }));

        this.addHandler(this.setDefaultHandler = new ItemHandler("home_change_default", (viewer, event) -> {
            Home home = this.getLink(viewer);
            home.setDefault(!home.isDefault());
            this.save(viewer);
        }));

        this.addHandler(this.typeHandler = new ItemHandler("home_change_type", (viewer, event) -> {
            Home home = this.getLink(viewer);
            home.setType(Lists.next(home.getType()));
            this.save(viewer);
        }));

        this.addHandler(this.iconHandler = new ItemHandler("home_change_icon", (viewer, event) -> {
            ItemStack item = event.getCursor();
            if (item == null || item.getType().isAir()) return;

            Home home = this.getLink(viewer);
            home.setIcon(item);
            Players.addItem(viewer.getPlayer(), item);
            event.getView().setCursor(null);
            this.save(viewer);
        }));

        this.addHandler(this.inviteHandler = new ItemHandler("home_change_invited_players", (viewer, event) -> {
            Home home = this.getLink(viewer);

            if (event.isRightClick()) {
                home.getInvitedPlayers().clear();
                this.save(viewer);
                return;
            }

            this.handleInput(viewer, HomesLang.EDITOR_ENTER_PLAYER_NAME, (dialog, input) -> {
                this.module.addHomeInvite(viewer.getPlayer(), home, input.getText());
                this.module.saveHome(home);
                return true;
            });
        }));

        this.addHandler(this.nameHandler = new ItemHandler("home_change_name", (viewer, event) -> {
            Home home = this.getLink(viewer);

            this.handleInput(viewer, HomesLang.EDITOR_ENTER_HOME_NAME, (dialog, input) -> {
                home.setName(input.getText());
                this.module.saveHome(home);
                return true;
            });
        }));

        this.load();

        this.getItems().forEach(menuItem -> {
            menuItem.getOptions().addDisplayModifier((viewer, item) -> ItemReplacer.replace(item, this.getLink(viewer).replacePlaceholders()));

            if (menuItem.getHandler() == this.iconHandler) {
                menuItem.getOptions().addDisplayModifier((viewer, item) -> {
                    Home home = this.getLink(viewer);

                    ItemMeta originMeta = item.getItemMeta();
                    ItemMeta iconMeta = home.getIcon().getItemMeta();
                    if (iconMeta == null || originMeta == null) return;

                    item.setType(home.getIcon().getType());
                    item.setItemMeta(iconMeta);

                    ItemMeta updatedMeta = item.getItemMeta();
                    if (updatedMeta == null) return;

                    updatedMeta.setDisplayName(originMeta.getDisplayName());
                    updatedMeta.setLore(originMeta.getLore());
                    item.setItemMeta(updatedMeta);

//                    ItemUtil.editMeta(item, meta -> {
//                        if (iconMeta.hasCustomModelData()) meta.setCustomModelData(iconMeta.getCustomModelData());
//                    });
//                    String texture = ItemUtil.getHeadSkin(home.getIcon());
//                    if (texture != null) ItemUtil.setHeadSkin(item, texture);
                });
            }
        });
    }

    @NotNull
    @Override
    public ViewLink<Home> getLink() {
        return link;
    }

    private void save(@NotNull MenuViewer viewer) {
        this.module.saveHome(this.getLink(viewer));
        this.runNextTick(() -> this.flush(viewer));
    }

    protected void handleInput(@NotNull MenuViewer viewer, @NotNull LangString prompt, @NotNull DialogHandler handler) {
        this.handleInput(viewer.getPlayer(), prompt, handler);
    }

    protected void handleInput(@NotNull Player player, @NotNull LangString prompt, @NotNull DialogHandler handler) {
        Dialog dialog = Dialog.create(player, handler);
        dialog.prompt(prompt.getMessage());
        this.runNextTick(player::closeInventory);
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

    @Override
    @NotNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions(BLACK.enclose("Home Settings"), MenuSize.CHEST_45);
    }

    @Override
    @NotNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack back = ItemUtil.getSkinHead(SKIN_ARROW_DOWN);
        ItemUtil.editMeta(back, meta -> {
            meta.setDisplayName(Lang.EDITOR_ITEM_RETURN.getLocalizedName());
        });
        list.add(new MenuItem(back).setPriority(10).setSlots(40).setHandler(this.returnHandler));

        ItemStack location = new ItemStack(Material.COMPASS);
        ItemUtil.editMeta(location, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Location")));
            meta.setLore(Lists.newList(
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("World: ") + HOME_LOCATION_WORLD),
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("X: ") + HOME_LOCATION_X),
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Y: ") + HOME_LOCATION_Y),
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Z: ") + HOME_LOCATION_Z),
                "",
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Click to " + LIGHT_YELLOW.enclose("set to yours") + ".")
            ));
        });
        list.add(new MenuItem(location).setPriority(10).setSlots(4).setHandler(this.locationHandler));

        ItemStack setDefault = new ItemStack(Material.GREEN_BED);
        ItemUtil.editMeta(setDefault, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Default Home")));
            meta.setLore(Lists.newList(
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Enabled: ") + HOME_IS_DEFAULT),
                "",
                LIGHT_GRAY.enclose("When enabled, uses this home by"),
                LIGHT_GRAY.enclose("default in home commands."),
                DARK_GRAY.enclose("(if no home specified)"),
                "",
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Click to " + LIGHT_YELLOW.enclose("toggle") + ".")
            ));
        });
        list.add(new MenuItem(setDefault).setPriority(10).setSlots(19).setHandler(this.setDefaultHandler));

        ItemStack respawn = new ItemStack(Material.RED_BED);
        ItemUtil.editMeta(respawn, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Respawn on Death")));
            meta.setLore(Lists.newList(
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Enabled: ") + HOME_IS_RESPAWN_POINT),
                "",
                LIGHT_GRAY.enclose("When enabled, sets this home"),
                LIGHT_GRAY.enclose("as your respawn location."),
                "",
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Click to " + LIGHT_YELLOW.enclose("toggle") + ".")
            ));
        });
        list.add(new MenuItem(respawn).setPriority(10).setSlots(20).setHandler(this.respawnHandler));

        ItemStack name = new ItemStack(Material.NAME_TAG);
        ItemUtil.editMeta(name, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Display Name")));
            meta.setLore(Lists.newList(
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Current: ") + HOME_NAME),
                "",
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Click to " + LIGHT_YELLOW.enclose("change") + ".")
            ));
        });
        list.add(new MenuItem(name).setPriority(10).setSlots(21).setHandler(this.nameHandler));

        ItemStack homeType = new ItemStack(Material.PAINTING);
        ItemUtil.editMeta(homeType, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Home Type")));
            meta.setLore(Lists.newList(
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Current: ") + HOME_TYPE),
                "",
                LIGHT_GRAY.enclose("Public home allows anyone to visit it."),
                LIGHT_GRAY.enclose("Private home is accessible by invites only."),
                "",
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Click to " + LIGHT_YELLOW.enclose("toggle") + ".")
            ));
        });
        list.add(new MenuItem(homeType).setPriority(10).setSlots(22).setHandler(this.typeHandler));

        ItemStack homeIcon = new ItemStack(Material.ITEM_FRAME);
        ItemUtil.editMeta(homeIcon, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Home Icon")));
            meta.setLore(Lists.newList(
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Drag & Drop to " + LIGHT_YELLOW.enclose("change") + ".")
            ));
        });
        list.add(new MenuItem(homeIcon).setPriority(10).setSlots(23).setHandler(this.iconHandler));

        ItemStack invites = new ItemStack(Material.PLAYER_HEAD);
        ItemUtil.editMeta(invites, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Invites")));
            meta.setLore(Lists.newList(
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Invited: ") + HOME_INVITED_PLAYERS),
                "",
                LIGHT_GRAY.enclose("List of players, who can visit"),
                LIGHT_GRAY.enclose("your home at any time."),
                "",
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Left-Click to " + LIGHT_YELLOW.enclose("add player") + "."),
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Right-Click to " + LIGHT_YELLOW.enclose("remove all") + ".")
            ));
        });
        list.add(new MenuItem(invites).setPriority(10).setSlots(24).setHandler(this.inviteHandler));

        return list;
    }

    @Override
    protected void loadAdditional() {

    }
}
