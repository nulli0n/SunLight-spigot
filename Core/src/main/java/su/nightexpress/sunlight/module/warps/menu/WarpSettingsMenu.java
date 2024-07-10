package su.nightexpress.sunlight.module.warps.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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
import su.nightexpress.nightcore.util.*;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.module.warps.config.WarpsConfig;
import su.nightexpress.sunlight.module.warps.config.WarpsLang;
import su.nightexpress.sunlight.module.warps.impl.Warp;
import su.nightexpress.sunlight.module.warps.config.WarpsPerms;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static su.nightexpress.sunlight.module.warps.util.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class WarpSettingsMenu extends ConfigMenu<SunLightPlugin> implements Linked<Warp> {

    public static final String FILE = "warp_settings.yml";

    private final ViewLink<Warp> link;

    private final ItemHandler returnHandler;
    private final ItemHandler typeHandler;
    private final ItemHandler commandHandler;
    private final ItemHandler descriptionHandler;
    private final ItemHandler iconHandler;
//    private final ItemHandler locationHandler;
    private final ItemHandler nameHandler;
    private final ItemHandler permissionHandler;
    private final ItemHandler visitCostHandler;
    private final ItemHandler visitCooldownHandler;
    private final ItemHandler visitTimesHandler;

    public WarpSettingsMenu(@NotNull SunLightPlugin plugin, @NotNull WarpsModule module) {
        super(plugin, FileConfig.loadOrExtract(plugin, module.getLocalUIPath(), FILE));
        this.link = new ViewLink<>();

        this.addHandler(this.returnHandler = ItemHandler.forReturn(this, (viewer, event) -> {
            this.runNextTick(() -> module.openServerWarps(viewer.getPlayer()));
        }));

        this.addHandler(this.typeHandler = new ItemHandler("warp_change_type", (viewer, event) -> {
            Warp warp = this.getLink(viewer);
            warp.setType(Lists.next(warp.getType()));
            this.save(viewer);
        }));

        this.addHandler(this.commandHandler = new ItemHandler("warp_change_command_shortcut", (viewer, event) -> {
            Warp warp = this.getLink(viewer);
            this.handleInput(viewer, WarpsLang.EDITOR_ENTER_COMMAND, (dialog, input) -> {
                warp.setCommandShortcut(input.getTextRaw());
                warp.save();
                return true;
            });
        }));

        this.addHandler(this.descriptionHandler = new ItemHandler("warp_change_description", (viewer, event) -> {
            Warp warp = this.getLink(viewer);
            this.handleInput(viewer, WarpsLang.EDITOR_ENTER_DESCRIPTION, (dialog, input) -> {
                String desc = input.getText();
                if (!viewer.getPlayer().hasPermission(WarpsPerms.BYPASS_DESCRIPTION_SIZE)) {
                    int maxSize = WarpsConfig.WARP_DESCRIPTION_MAX_SIZE.get();
                    if (desc.length() > maxSize) desc = desc.substring(0, maxSize);
                }
                warp.setDescription(desc);
                warp.save();
                return true;
            });
        }));

        this.addHandler(this.iconHandler = new ItemHandler("warp_change_icon", (viewer, event) -> {
            Warp warp = this.getLink(viewer);
            ItemStack icon = event.getCursor();
            if (icon == null || icon.getType().isAir()) return;

            warp.setIcon(icon);
            Players.addItem(viewer.getPlayer(), icon);
            event.getWhoClicked().setItemOnCursor(null);
            this.save(viewer);
        }));

//        this.addHandler(this.locationHandler = new ItemHandler("warp_change_location", (viewer, event) -> {
//
//        }));

        this.addHandler(this.nameHandler = new ItemHandler("warp_change_name", (viewer, event) -> {
            Warp warp = this.getLink(viewer);
            this.handleInput(viewer, WarpsLang.EDITOR_ENTER_NAME, (dialog, input) -> {
                warp.setName(input.getText());
                warp.save();
                return true;
            });
        }));

        this.addHandler(this.permissionHandler = new ItemHandler("warp_change_permission", (viewer, event) -> {
            Warp warp = this.getLink(viewer);
            warp.setPermissionRequired(!warp.isPermissionRequired());
            this.save(viewer);
        }));

        this.addHandler(this.visitCostHandler = new ItemHandler("warp_change_visit_cost", (viewer, event) -> {
            Warp warp = this.getLink(viewer);
            if (event.isRightClick()) {
                warp.setVisitCostMoney(0);
                this.save(viewer);
                return;
            }
            this.handleInput(viewer, WarpsLang.EDITOR_ENTER_COST, (dialog, input) -> {
                warp.setVisitCostMoney(input.asDouble());
                warp.save();
                return true;
            });
        }));

        this.addHandler(this.visitCooldownHandler = new ItemHandler("warp_change_visit_cooldown", (viewer, event) -> {
            Warp warp = this.getLink(viewer);
            if (event.isRightClick()) {
                warp.setVisitCooldown(0);
                this.save(viewer);
                return;
            }

            this.handleInput(viewer, WarpsLang.EDITOR_ENTER_COOLDOWN, (dialog, input) -> {
                warp.setVisitCooldown(input.asInt());
                warp.save();
                return true;
            });
        }));

        this.addHandler(this.visitTimesHandler = new ItemHandler("warp_change_visit_times", (viewer, event) -> {
            Warp warp = this.getLink(viewer);
            if (event.isRightClick()) {
                warp.getVisitTimes().clear();
                this.save(viewer);
                return;
            }

            this.handleInput(viewer, WarpsLang.EDITOR_ENTER_TIMES, (dialog, wrapper) -> {
                String[] split = wrapper.getTextRaw().split(" ");
                if (split.length < 2) return true;
                try {
                    LocalTime from = LocalTime.parse(split[0], Warp.TIME_FORMATTER);
                    LocalTime to = LocalTime.parse(split[1], Warp.TIME_FORMATTER);
                    warp.getVisitTimes().add(Pair.of(from, to));
                    warp.save();
                }
                catch (DateTimeParseException ignored) {}
                return true;
            });
        }));

        this.load();

        this.getItems().forEach(menuItem -> {
            Predicate<MenuViewer> policy = null;
            ItemHandler handler = menuItem.getHandler();

            if (handler == this.visitTimesHandler) policy = player -> player.getPlayer().hasPermission(WarpsPerms.EDITOR_VISIT_TIMES);
            else if (handler == this.visitCostHandler) policy = player -> player.getPlayer().hasPermission(WarpsPerms.EDITOR_VISIT_COST);
            else if (handler == this.visitCooldownHandler) policy = player -> player.getPlayer().hasPermission(WarpsPerms.EDITOR_VISIT_COOLDOWN);
            else if (handler == this.permissionHandler) policy = player -> player.getPlayer().hasPermission(WarpsPerms.EDITOR_PERMISSION);
            else if (handler == this.typeHandler) policy = player -> player.getPlayer().hasPermission(WarpsPerms.EDITOR_TYPE);
            else if (handler == this.commandHandler) policy = player -> player.getPlayer().hasPermission(WarpsPerms.EDITOR_COMMAND_SHORTCUT);

            if (policy != null) {
                menuItem.getOptions().setVisibilityPolicy(policy);
            }
            if (handler == this.iconHandler) {
                menuItem.getOptions().addDisplayModifier((viewer, item) -> {
                    Warp warp = this.getLink(viewer);
                    String nameHas = ItemUtil.getItemName(item);
                    List<String> loreHas = ItemUtil.getLore(item);

                    item.setType(warp.getIcon().getType());
                    item.setItemMeta(warp.getIcon().getItemMeta());
                    ItemUtil.editMeta(item, meta -> {
                        meta.setDisplayName(nameHas);
                        meta.setLore(loreHas);
                    });
                });
            }
            menuItem.getOptions().addDisplayModifier((viewer, item) -> ItemReplacer.replace(item, this.getLink(viewer).replacePlaceholders()));
        });
    }

    @NotNull
    @Override
    public ViewLink<Warp> getLink() {
        return link;
    }

    private void save(@NotNull MenuViewer viewer) {
        this.getLink(viewer).save();
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
        return new MenuOptions(BLACK.enclose("Warp Settings"), MenuSize.CHEST_27);
    }

    @Override
    @NotNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack back = ItemUtil.getSkinHead(SKIN_ARROW_DOWN);
        ItemUtil.editMeta(back, meta -> {
            meta.setDisplayName(Lang.EDITOR_ITEM_RETURN.getDefaultName());
        });
        list.add(new MenuItem(back).setPriority(10).setSlots(22).setHandler(this.returnHandler));

//        ItemStack locationItem = new ItemStack(Material.COMPASS);
//        ItemUtil.editMeta(locationItem, meta -> {
//            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Location")));
//            meta.setLore(Lists.newList(
//                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("World: ") + WARP_LOCATION_WORLD),
//                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("X: ") + WARP_LOCATION_X),
//                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Y: ") + WARP_LOCATION_Y),
//                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Z: ") + WARP_LOCATION_Z)
//            ));
//        });
//        list.add(new MenuItem(locationItem).setPriority(10).setSlots(0).setHandler(this.locationHandler));

        ItemStack nameItem = ItemUtil.getSkinHead("f5a19af0e61ca42532c0599fa0a391753df6b71f9fa4a177f1aa9b1d81fe6ee2");
        ItemUtil.editMeta(nameItem, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Display Name")));
            meta.setLore(Lists.newList(
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Current: ") + WARP_NAME),
                " ",
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Click to " + LIGHT_YELLOW.enclose("change") + ".")
            ));
        });
        list.add(new MenuItem(nameItem).setPriority(10).setSlots(3).setHandler(this.nameHandler));

        ItemStack descriptionItem = new ItemStack(Material.WRITABLE_BOOK);
        ItemUtil.editMeta(descriptionItem, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Descrpition")));
            meta.setLore(Lists.newList(
                WARP_DESCRIPTION,
                "",
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Click to " + LIGHT_YELLOW.enclose("change") + ".")
            ));
        });
        list.add(new MenuItem(descriptionItem).setPriority(10).setSlots(4).setHandler(this.descriptionHandler));

        ItemStack iconItem = new ItemStack(Material.ITEM_FRAME);
        ItemUtil.editMeta(iconItem, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Icon")));
            meta.setLore(Lists.newList(
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Drag & Drop to " + LIGHT_YELLOW.enclose("change") + ".")
            ));
        });
        list.add(new MenuItem(iconItem).setPriority(10).setSlots(5).setHandler(this.iconHandler));

        ItemStack permissionItem = new ItemStack(Material.REDSTONE_TORCH);
        ItemUtil.editMeta(permissionItem, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Permission Requirement")));
            meta.setLore(Lists.newList(
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Enabled: ") + WARP_PERMISSION_REQUIRED),
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Node: ") + WARP_PERMISSION_NODE),
                "",
                LIGHT_GRAY.enclose("Sets whether or not permission is"),
                LIGHT_GRAY.enclose("required to visit this warp."),
                "",
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Click to " + LIGHT_YELLOW.enclose("toggle") + ".")
            ));
        });
        list.add(new MenuItem(permissionItem).setPriority(10).setSlots(11).setHandler(this.permissionHandler));



        ItemStack visitCostItem = new ItemStack(Material.GOLD_NUGGET);
        ItemUtil.editMeta(visitCostItem, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Visit Cost")));
            meta.setLore(Lists.newList(
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Current:") + " $" + WARP_VISIT_COST),
                "",
                LIGHT_GRAY.enclose("Sets how much money player will have to pay"),
                LIGHT_GRAY.enclose("in order to use this warp."),
                "",
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Left-Click to " + LIGHT_YELLOW.enclose("change") + "."),
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Right-Click to " + LIGHT_YELLOW.enclose("disable") + ".")
            ));
        });
        list.add(new MenuItem(visitCostItem).setPriority(10).setSlots(12).setHandler(this.visitCostHandler));

        ItemStack visitCooldownItem = new ItemStack(Material.CLOCK);
        ItemUtil.editMeta(visitCooldownItem, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Visit Cooldown")));
            meta.setLore(Lists.newList(
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Current: ") + WARP_VISIT_COOLDOWN),
                "",
                LIGHT_GRAY.enclose("Sets the cooldown for next warp visit per a player."),
                "",
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Left-Click to " + LIGHT_YELLOW.enclose("change") + "."),
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Right-Click to " + LIGHT_YELLOW.enclose("disable") + ".")
            ));
        });
        list.add(new MenuItem(visitCooldownItem).setPriority(10).setSlots(13).setHandler(this.visitCooldownHandler));

        ItemStack visitTimesItem = new ItemStack(Material.DAYLIGHT_DETECTOR);
        ItemUtil.editMeta(visitTimesItem, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Visit Times")));
            meta.setLore(Lists.newList(
                WARP_VISIT_TIMES,
                "",
                LIGHT_GRAY.enclose("Sets the times when the warp is available for visits."),
                "",
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Left-Click to " + LIGHT_YELLOW.enclose("add time") + "."),
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Right-Click to " + LIGHT_YELLOW.enclose("remove all") + ".")
            ));
        });
        list.add(new MenuItem(visitTimesItem).setPriority(10).setSlots(14).setHandler(this.visitTimesHandler));



        ItemStack warpType = new ItemStack(Material.BEACON);
        ItemUtil.editMeta(warpType, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Type")));
            meta.setLore(Lists.newList(
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Current: ") + WARP_TYPE),
                " ",
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Click to " + LIGHT_YELLOW.enclose("toggle") + ".")
            ));
        });
        list.add(new MenuItem(warpType).setPriority(10).setSlots(15).setHandler(this.typeHandler));

        ItemStack warpCommand = new ItemStack(Material.COMMAND_BLOCK);
        ItemUtil.editMeta(warpCommand, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Dedicated Command")));
            meta.setLore(Lists.newList(
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Current:") + " /" + WARP_COMMAND_SHORTCUT),
                "",
                LIGHT_GRAY.enclose("Allows to access the warp"),
                LIGHT_GRAY.enclose("using dedicated command."),
                "",
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Left-Click to " + LIGHT_YELLOW.enclose("change") + "."),
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Right-Click to " + LIGHT_YELLOW.enclose("disable") + ".")
            ));
        });
        list.add(new MenuItem(warpCommand).setPriority(10).setSlots(16).setHandler(this.commandHandler));

        return list;
    }

    @Override
    protected void loadAdditional() {

    }
}
