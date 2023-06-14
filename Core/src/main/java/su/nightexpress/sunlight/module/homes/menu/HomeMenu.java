package su.nightexpress.sunlight.module.homes.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.editor.EditorHandler;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.api.menu.impl.ConfigMenu;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.config.HomesLang;
import su.nightexpress.sunlight.module.homes.impl.Home;

public class HomeMenu extends ConfigMenu<SunLight> {

    private final HomesModule module;
    private final Home home;

    public HomeMenu(@NotNull HomesModule module, @NotNull Home home) {
        super(module.plugin(), JYML.loadOrExtract(module.plugin(), module.getLocalPath() + "/menu/home_settings.yml"));
        this.module = module;
        this.home = home;

        this.registerHandler(MenuItemType.class)
            .addClick(MenuItemType.CLOSE, (viewer, event) -> plugin.runTask(task -> viewer.getPlayer().closeInventory()))
            .addClick(MenuItemType.RETURN, (viewer, event) -> {
                Player player = viewer.getPlayer();
                if (!home.isOwner(player)) {
                    module.getHomesMenu().open(player, home.getOwner().getId());
                }
                else {
                    module.getHomesMenu().open(player, 1);
                }
            });

        this.registerHandler(Type.class)
            .addClick(Type.HOME_CHANGE_LOCATION, (viewer, event) -> {
                home.setLocation(viewer.getPlayer().getLocation());
                this.save(viewer);
            })
            .addClick(Type.HOME_CHANGE_RESPAWN_POINT, (viewer, event) -> {
                home.setRespawnPoint(!home.isRespawnPoint());
                this.save(viewer);
            })
            .addClick(Type.HOME_CHANGE_DEFAULT, (viewer, event) -> {
                home.setDefault(!home.isDefault());
                this.save(viewer);
            })
            .addClick(Type.HOME_CHANGE_TYPE, (viewer, event) -> {
                home.setType(CollectionsUtil.next(home.getType()));
                this.save(viewer);
            })
            .addClick(Type.HOME_CHANGE_ICON, (viewer, event) -> {
                ItemStack item = event.getCursor();
                if (item == null || item.getType().isAir()) return;

                home.setIcon(item);
                PlayerUtil.addItem(viewer.getPlayer(), item);
                event.getView().setCursor(null);
                this.save(viewer);
            })
            .addClick(Type.HOME_CHANGE_INVITED_PLAYERS, (viewer, event) -> {
                if (event.isRightClick()) {
                    home.getInvitedPlayers().clear();
                    this.save(viewer);
                    return;
                }

                viewer.getPlayer().closeInventory();
                EditorManager.prompt(viewer.getPlayer(), plugin.getMessage(HomesLang.EDITOR_ENTER_PLAYER_NAME).getLocalized());
                EditorManager.startEdit(viewer.getPlayer(), (EditorHandler) chat -> {
                    this.module.addHomeInvite(viewer.getPlayer(), home, chat.getMessage());
                    return true;
                });
            })
            .addClick(Type.HOME_CHANGE_NAME, (viewer, event) -> {
                viewer.getPlayer().closeInventory();
                EditorManager.prompt(viewer.getPlayer(), plugin.getMessage(HomesLang.EDITOR_ENTER_HOME_NAME).getLocalized());
                EditorManager.startEdit(viewer.getPlayer(), (EditorHandler) chat -> {
                    home.setName(chat.getMessage());
                    home.save();
                    return true;
                });
            });

        this.load();

        this.getItems().forEach(menuItem -> {
            menuItem.getOptions().addDisplayModifier((viewer, item) -> ItemUtil.replace(item, home.replacePlaceholders()));
            if (menuItem.getType() == Type.HOME_CHANGE_ICON) {
                ItemMeta metaIcon = home.getIcon().getItemMeta();
                if (metaIcon == null) return;

                menuItem.getOptions().addDisplayModifier((viewer, item) -> {
                    item.setType(home.getIcon().getType());
                    ItemUtil.mapMeta(item, meta -> {
                        if (metaIcon.hasCustomModelData()) meta.setCustomModelData(metaIcon.getCustomModelData());
                    });
                    String texture = ItemUtil.getSkullTexture(home.getIcon());
                    if (texture != null) ItemUtil.setSkullTexture(item, texture);
                });
            }
        });
    }

    private void save(@NotNull MenuViewer viewer) {
        this.home.save();
        this.plugin.runTask(task -> this.open(viewer.getPlayer(), viewer.getPage()));
    }

    enum Type {
        HOME_CHANGE_NAME,
        HOME_CHANGE_LOCATION,
        HOME_CHANGE_ICON,
        HOME_CHANGE_DEFAULT,
        HOME_CHANGE_RESPAWN_POINT,
        HOME_CHANGE_TYPE,
        HOME_CHANGE_INVITED_PLAYERS,
    }

    @Override
    public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
        super.onClick(viewer, item, slotType, slot, event);
        if (slotType == SlotType.PLAYER || slotType == SlotType.PLAYER_EMPTY) {
            event.setCancelled(false);
        }
    }
}
