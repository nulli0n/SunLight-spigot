package su.nightexpress.sunlight.module.warps.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.editor.InputHandler;
import su.nexmedia.engine.api.lang.LangKey;
import su.nexmedia.engine.api.lang.LangMessage;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.api.menu.impl.ConfigMenu;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.Pair;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.warps.config.WarpsConfig;
import su.nightexpress.sunlight.module.warps.config.WarpsLang;
import su.nightexpress.sunlight.module.warps.impl.Warp;
import su.nightexpress.sunlight.module.warps.config.WarpsPerms;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Predicate;

public class WarpSettingsMenu extends ConfigMenu<SunLight> {

    private final Warp warp;

    public WarpSettingsMenu(@NotNull Warp warp) {
        super(warp.getModule().plugin(), JYML.loadOrExtract(warp.plugin(), warp.getModule().getLocalPath() + "/menu/", "warp_settings.yml"));
        this.warp = warp;

        this.registerHandler(MenuItemType.class)
            .addClick(MenuItemType.RETURN, (viewer, event) -> {
                warp.getModule().getWarpsMenu().openNextTick(viewer, 1);
            });

        this.registerHandler(Type.class)
            .addClick(Type.WARP_CHANGE_TYPE, (viewer, event) -> {
                warp.setType(CollectionsUtil.next(warp.getType()));
                this.save(viewer);
            })
            .addClick(Type.WARP_CHANGE_COMMAND_SHORTCUT, (viewer, event) -> {
                this.handleInput(viewer, WarpsLang.EDITOR_ENTER_COMMAND, wrapper -> {
                    warp.setCommandShortcut(wrapper.getTextRaw());
                    warp.save();
                    return true;
                });
            })
            .addClick(Type.WARP_CHANGE_DESCRIPTION, (viewer, event) -> {
                this.handleInput(viewer, WarpsLang.EDITOR_ENTER_DESCRIPTION, wrapper -> {
                    String desc = wrapper.getText();
                    if (!viewer.getPlayer().hasPermission(WarpsPerms.BYPASS_DESCRIPTION_SIZE)) {
                        int maxSize = WarpsConfig.WARP_DESCRIPTION_MAX_SIZE.get();
                        if (desc.length() > maxSize) desc = desc.substring(0, maxSize);
                    }
                    warp.setDescription(desc);
                    warp.save();
                    return true;
                });
            })
            .addClick(Type.WARP_CHANGE_ICON, (viewer, event) -> {
                ItemStack icon = event.getCursor();
                if (icon == null || icon.getType().isAir()) return;

                warp.setIcon(icon);
                PlayerUtil.addItem(viewer.getPlayer(), icon);
                event.getWhoClicked().setItemOnCursor(null);
                this.save(viewer);
            })
            .addClick(Type.WARP_CHANGE_LOCATION, (viewer, event) -> {

            })
            .addClick(Type.WARP_CHANGE_NAME, (viewer, event) -> {
                this.handleInput(viewer, WarpsLang.EDITOR_ENTER_NAME, wrapper -> {
                    warp.setName(wrapper.getText());
                    warp.save();
                    return true;
                });
            })
            .addClick(Type.WARP_CHANGE_PERMISSION, (viewer, event) -> {
                warp.setPermissionRequired(!warp.isPermissionRequired());
                this.save(viewer);
            })
            .addClick(Type.WARP_CHANGE_VISIT_COOLDOWN, (viewer, event) -> {

            })
            .addClick(Type.WARP_CHANGE_VISIT_COST, (viewer, event) -> {
                if (event.isRightClick()) {
                    warp.setVisitCostMoney(0);
                    this.save(viewer);
                    return;
                }
                this.handleInput(viewer, WarpsLang.EDITOR_ENTER_COST, wrapper -> {
                    warp.setVisitCostMoney(wrapper.asDouble());
                    warp.save();
                    return true;
                });
            })
            .addClick(Type.WARP_CHANGE_VISIT_TIMES, (viewer, event) -> {
                if (event.isShiftClick()) {
                    if (event.isRightClick()) {
                        warp.getVisitTimes().clear();
                        this.save(viewer);
                        return;
                    }
                    this.handleInput(viewer, WarpsLang.EDITOR_ENTER_TIMES, wrapper -> {
                        String[] split = wrapper.getTextRaw().split(" ");
                        if (split.length < 2) return true;
                        try {
                            LocalTime from = LocalTime.parse(split[0], Warp.TIME_FORMATTER);
                            LocalTime to = LocalTime.parse(split[1], Warp.TIME_FORMATTER);
                            warp.getVisitTimes().add(Pair.of(from, to));
                            warp.save();
                        }
                        catch (DateTimeParseException ex) {
                            return true;
                        }
                        return true;
                    });
                }
                else {
                    if (event.isRightClick()) {
                        warp.setVisitCooldown(0);
                        this.save(viewer);
                        return;
                    }

                    this.handleInput(viewer, WarpsLang.EDITOR_ENTER_COOLDOWN, wrapper -> {
                        warp.setVisitCooldown(wrapper.asAnyInt(0));
                        warp.save();
                        return true;
                    });
                }
            })
            ;

        this.load();

        this.getItems().forEach(menuItem -> {
            if (menuItem.getType() instanceof Type type) {
                Predicate<MenuViewer> policy = switch (type) {
                    case WARP_CHANGE_VISIT_TIMES -> player -> player.getPlayer().hasPermission(WarpsPerms.EDITOR_VISIT_TIMES);
                    case WARP_CHANGE_VISIT_COST -> player -> player.getPlayer().hasPermission(WarpsPerms.EDITOR_VISIT_COST);
                    case WARP_CHANGE_VISIT_COOLDOWN -> player -> player.getPlayer().hasPermission(WarpsPerms.EDITOR_VISIT_COOLDOWN);
                    case WARP_CHANGE_PERMISSION -> player -> player.getPlayer().hasPermission(WarpsPerms.EDITOR_PERMISSION);
                    case WARP_CHANGE_TYPE -> player -> player.getPlayer().hasPermission(WarpsPerms.EDITOR_TYPE);
                    case WARP_CHANGE_COMMAND_SHORTCUT -> player -> player.getPlayer().hasPermission(WarpsPerms.EDITOR_COMMAND_SHORTCUT);
                    default -> null;
                };

                if (policy != null) {
                    menuItem.getOptions().setVisibilityPolicy(policy);
                }
                if (type == Type.WARP_CHANGE_ICON) {
                    menuItem.getOptions().addDisplayModifier((viewer, item) -> {
                        String nameHas = ItemUtil.getItemName(item);
                        List<String> loreHas = ItemUtil.getLore(item);

                        item.setType(warp.getIcon().getType());
                        item.setItemMeta(warp.getIcon().getItemMeta());
                        ItemUtil.mapMeta(item, meta -> {
                            meta.setDisplayName(nameHas);
                            meta.setLore(loreHas);
                        });
                    });
                }
            }
            menuItem.getOptions().addDisplayModifier((viewer, item) -> ItemUtil.replace(item, warp.replacePlaceholders()));
        });
    }

    private void save(@NotNull MenuViewer viewer) {
        this.warp.save();
        this.openNextTick(viewer, viewer.getPage());
    }

    protected void handleInput(@NotNull MenuViewer viewer, @NotNull LangKey prompt, @NotNull InputHandler handler) {
        this.handleInput(viewer.getPlayer(), prompt, handler);
    }

    protected void handleInput(@NotNull Player player, @NotNull LangKey prompt, @NotNull InputHandler handler) {
        this.handleInput(player, this.plugin.getMessage(prompt), handler);
    }

    protected void handleInput(@NotNull Player player, @NotNull LangMessage prompt, @NotNull InputHandler handler) {
        EditorManager.prompt(player, prompt.getLocalized());
        EditorManager.startEdit(player, handler);
        this.plugin.runTask((task) -> {
            player.closeInventory();
        });
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
    public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
        super.onClick(viewer, item, slotType, slot, event);
        if (slotType == SlotType.PLAYER || slotType == SlotType.PLAYER_EMPTY) {
            event.setCancelled(false);
        }
    }
}
