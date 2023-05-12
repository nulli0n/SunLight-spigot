package su.nightexpress.sunlight.module.warps.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.editor.EditorInput;
import su.nexmedia.engine.api.menu.*;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.*;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.module.warps.config.WarpsConfig;
import su.nightexpress.sunlight.module.warps.config.WarpsLang;
import su.nightexpress.sunlight.module.warps.impl.Warp;
import su.nightexpress.sunlight.module.warps.util.WarpsPerms;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.function.Predicate;

public class WarpSettingsMenu extends AbstractMenu<SunLight> {

    private final Warp warp;

    public WarpSettingsMenu(@NotNull Warp warp) {
        super(warp.getWarpsModule().plugin(), JYML.loadOrExtract(warp.plugin(), warp.getWarpsModule().getLocalPath() + "/menu/warp_settings.yml"), "");
        this.warp = warp;

        EditorInput<Warp, Type> input = (player, warp2, type, e) -> {
            String msg = e.getMessage();
            switch (type) {
                case WARP_CHANGE_NAME -> warp2.setName(msg);
                case WARP_CHANGE_VISIT_COST -> warp2.setVisitCostMoney(StringUtil.getDouble(Colorizer.strip(msg), 0D));
                case WARP_CHANGE_VISIT_COOLDOWN -> warp2.setVisitCooldown(StringUtil.getInteger(Colorizer.strip(msg), 0, true));
                case WARP_CHANGE_VISIT_TIMES -> {
                    String[] split = msg.split(" ");
                    if (split.length < 2) return true;
                    try {
                        LocalTime from = LocalTime.parse(split[0], Warp.TIME_FORMATTER);
                        LocalTime to = LocalTime.parse(split[1], Warp.TIME_FORMATTER);
                        warp2.getVisitTimes().add(Pair.of(from, to));
                    }
                    catch (DateTimeParseException ex) {
                        return true;
                    }
                }
                case WARP_CHANGE_COMMAND_SHORTCUT -> warp2.setCommandShortcut(msg);
                case WARP_CHANGE_DESCRIPTION -> {
                    String desc = msg;
                    if (!player.hasPermission(WarpsPerms.BYPASS_DESCRIPTION_SIZE)) {
                        int maxSize = WarpsConfig.WARP_DESCRIPTION_MAX_SIZE.get();
                        if (desc.length() > maxSize) desc = desc.substring(0, maxSize);
                    }
                    warp2.setDescription(desc);
                }
                default -> {}
            }

            warp2.save();
            return true;
        };
        
        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    warp.getWarpsModule().getWarpsMenu().open(player, 1);
                }
                else this.onItemClickDefault(player, type2);
            }
            else if (type instanceof Type type2) {
                WarpsModule warpsModule = warp.getWarpsModule();
                switch (type2) {
                    case WARP_CHANGE_VISIT_COST -> {
                        if (e.isRightClick()) {
                            warp.setVisitCostMoney(0);
                            break;
                        }
                        EditorManager.startEdit(player, warp, type2, input);
                        EditorManager.prompt(player, plugin.getMessage(WarpsLang.EDITOR_ENTER_COST).getLocalized());
                        player.closeInventory();
                        return;
                    }
                    case WARP_CHANGE_VISIT_TIMES -> {
                        if (e.isShiftClick()) {
                            if (e.isLeftClick()) {
                                EditorManager.startEdit(player, warp, type2, input);
                                EditorManager.prompt(player, plugin.getMessage(WarpsLang.EDITOR_ENTER_TIMES).getLocalized());
                                player.closeInventory();
                                return;
                            }
                            else if (e.isRightClick()) {
                                warp.getVisitTimes().clear();
                            }
                        }
                        else {
                            if (e.isLeftClick()) {
                                EditorManager.startEdit(player, warp, Type.WARP_CHANGE_VISIT_COOLDOWN, input);
                                EditorManager.prompt(player, plugin.getMessage(WarpsLang.EDITOR_ENTER_COOLDOWN).getLocalized());
                                player.closeInventory();
                                return;
                            }
                            else if (e.isRightClick()) {
                                warp.setVisitCooldown(0);
                            }
                        }
                    }
                    case WARP_CHANGE_COMMAND_SHORTCUT -> {
                        EditorManager.startEdit(player, warp, type2, input);
                        EditorManager.prompt(player, plugin.getMessage(WarpsLang.EDITOR_ENTER_COMMAND).getLocalized());
                        player.closeInventory();
                        return;
                    }
                    case WARP_CHANGE_ICON -> {
                        ItemStack icon = e.getCursor();
                        if (icon == null || icon.getType().isAir()) return;

                        warp.setIcon(icon);
                        PlayerUtil.addItem(player, icon);
                        e.getWhoClicked().setItemOnCursor(null);
                    }
                    case WARP_CHANGE_NAME -> {
                        EditorManager.startEdit(player, warp, type2, input);
                        EditorManager.prompt(player, plugin.getMessage(WarpsLang.EDITOR_ENTER_NAME).getLocalized());
                        player.closeInventory();
                        return;
                    }
                    case WARP_CHANGE_PERMISSION -> warp.setPermissionRequired(!warp.isPermissionRequired());
                    case WARP_CHANGE_TYPE -> warp.setType(CollectionsUtil.next(warp.getType()));
                    case WARP_CHANGE_DESCRIPTION -> {
                        EditorManager.startEdit(player, warp, type2, input);
                        EditorManager.prompt(player, plugin.getMessage(WarpsLang.EDITOR_ENTER_DESCRIPTION).getLocalized());
                        player.closeInventory();
                        return;
                    }
                    default -> {
                        return;
                    }
                }
                warp.save();
                this.open(player, 1);
            }
        };

        for (String sId : cfg.getSection("Content")) {
            MenuItem menuItem = cfg.getMenuItem("Content." + sId, MenuItemType.class);

            if (menuItem.getType() != null) {
                menuItem.setClickHandler(click);
            }
            this.addItem(menuItem);
        }

        for (String sId : cfg.getSection("Editor")) {
            MenuItem menuItem = cfg.getMenuItem("Editor." + sId, Type.class);

            if (menuItem.getType() instanceof Type type) {
                menuItem.setClickHandler(click);

                Predicate<Player> policy = switch (type) {
                    case WARP_CHANGE_VISIT_TIMES -> player -> player.hasPermission(WarpsPerms.EDITOR_VISIT_TIMES);
                    case WARP_CHANGE_VISIT_COST -> player -> player.hasPermission(WarpsPerms.EDITOR_VISIT_COST);
                    case WARP_CHANGE_VISIT_COOLDOWN -> player -> player.hasPermission(WarpsPerms.EDITOR_VISIT_COOLDOWN);
                    case WARP_CHANGE_PERMISSION -> player -> player.hasPermission(WarpsPerms.EDITOR_PERMISSION);
                    case WARP_CHANGE_TYPE -> player -> player.hasPermission(WarpsPerms.EDITOR_TYPE);
                    case WARP_CHANGE_COMMAND_SHORTCUT -> player -> player.hasPermission(WarpsPerms.EDITOR_COMMAND_SHORTCUT);
                    default -> null;
                };

                if (policy != null) {
                    menuItem.setVisibility(MenuItemVisibility.HIDDEN);
                    menuItem.setVisibilityPolicy(policy);
                }
            }

            this.addItem(menuItem);
        }
    }

    enum Type {
        WARP_CHANGE_LOCATION,
        WARP_CHANGE_NAME,
        WARP_CHANGE_DESCRIPTION,
        WARP_CHANGE_VISIT_COST,
        WARP_CHANGE_VISIT_TIMES,
        WARP_CHANGE_VISIT_COOLDOWN,
        WARP_CHANGE_PERMISSION,
        WARP_CHANGE_ICON,
        WARP_CHANGE_TYPE,
        WARP_CHANGE_COMMAND_SHORTCUT
    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull MenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);
        if (menuItem.getType() instanceof Type type) {
            if (type == Type.WARP_CHANGE_ICON) {
                item.setType(this.warp.getIcon().getType());
            }
        }
        ItemUtil.replace(item, this.warp.replacePlaceholders());
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return slotType != SlotType.PLAYER && slotType != SlotType.EMPTY_PLAYER;
    }
}
