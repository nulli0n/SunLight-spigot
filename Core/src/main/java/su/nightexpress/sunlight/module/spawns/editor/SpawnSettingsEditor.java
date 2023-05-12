package su.nightexpress.sunlight.module.spawns.editor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.editor.EditorInput;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenu;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.spawns.impl.Spawn;
import su.nightexpress.sunlight.module.spawns.config.SpawnsLang;
import su.nightexpress.sunlight.module.spawns.util.Placeholders;

import java.util.Map;

public class SpawnSettingsEditor extends AbstractEditorMenu<SunLight, Spawn> {

    private final Spawn spawn;

    public SpawnSettingsEditor(@NotNull Spawn spawn) {
        super(spawn.plugin(), spawn, Placeholders.EDITOR_TITLE, 45);
        this.spawn = spawn;

        EditorInput<Spawn, SpawnsEditorType> input = (player, spawn2, type, e) -> {
            String message = e.getMessage();
            switch (type) {
                case SPAWN_CHANGE_NAME -> spawn2.setName(message);
                case SPAWN_CHANGE_PRIORITY -> spawn2.setPriority(StringUtil.getInteger(message, 0));
                case SPAWN_CHANGE_LOGIN_TELEPORT_GROUPS -> spawn2.getLoginTeleportGroups().add(message);
                case SPAWN_CHANGE_RESPAWN_TELEPORT_GROUPS -> spawn2.getRespawnTeleportGroups().add(message);
            }

            spawn2.save();
            return true;
        };
        
        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2 && type2 == MenuItemType.RETURN) {
                spawn.getSpawnManager().getEditor().open(player, 1);
            }
            else if (type instanceof SpawnsEditorType type2) {
                switch (type2) {
                    case SPAWN_CHANGE_NAME -> {
                        EditorManager.startEdit(player, spawn, type2, input);
                        EditorManager.prompt(player, plugin.getMessage(SpawnsLang.SPAWNS_EDITOR_ENTER_NAME).getLocalized());
                        player.closeInventory();
                        return;
                    }
                    case SPAWN_CHANGE_LOCATION -> spawn.setLocation(player.getLocation());
                    case SPAWN_CHANGE_PERMISSION -> spawn.setPermissionRequired(!spawn.isPermissionRequired());
                    case SPAWN_CHANGE_DEFAULT -> spawn.setDefault(!spawn.isDefault());
                    case SPAWN_CHANGE_PRIORITY -> {
                        EditorManager.startEdit(player, spawn, type2, input);
                        EditorManager.prompt(player, plugin.getMessage(SpawnsLang.SPAWNS_EDITOR_ENTER_PRIORITY).getLocalized());
                        player.closeInventory();
                        return;
                    }
                    case SPAWN_CHANGE_LOGIN_TELEPORT -> {
                        if (e.isShiftClick()) {
                            if (e.isLeftClick()) {
                                EditorManager.startEdit(player, spawn, SpawnsEditorType.SPAWN_CHANGE_LOGIN_TELEPORT_GROUPS, input);
                                EditorManager.prompt(player, plugin.getMessage(SpawnsLang.SPAWNS_EDITOR_ENTER_GROUP).getLocalized());
                                player.closeInventory();
                                return;
                            }
                            else {
                                spawn.getLoginTeleportGroups().clear();
                            }
                        }
                        else {
                            if (e.isRightClick()) {
                                spawn.setFirstLoginTeleportEnabled(!spawn.isFirstLoginTeleportEnabled());
                            }
                            else {
                                spawn.setLoginTeleportEnabled(!spawn.isLoginTeleportEnabled());
                            }
                        }
                    }
                    case SPAWN_CHANGE_RESPAWN_TELEPORT -> {
                        if (e.isShiftClick()) {
                            if (e.isLeftClick()) {
                                EditorManager.startEdit(player, spawn, SpawnsEditorType.SPAWN_CHANGE_RESPAWN_TELEPORT_GROUPS, input);
                                EditorManager.prompt(player, plugin.getMessage(SpawnsLang.SPAWNS_EDITOR_ENTER_GROUP).getLocalized());
                                player.closeInventory();
                                return;
                            }
                            else {
                                spawn.getRespawnTeleportGroups().clear();
                            }
                        }
                        else {
                            spawn.setRespawnTeleportEnabled(!spawn.isRespawnTeleportEnabled());
                        }
                    }
                }

                spawn.save();
                this.open(player, this.getPage(player));
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(SpawnsEditorType.SPAWN_CHANGE_NAME, 11);
        map.put(SpawnsEditorType.SPAWN_CHANGE_PERMISSION, 12);
        map.put(SpawnsEditorType.SPAWN_CHANGE_PRIORITY, 13);
        map.put(SpawnsEditorType.SPAWN_CHANGE_DEFAULT, 14);
        map.put(SpawnsEditorType.SPAWN_CHANGE_LOCATION, 15);
        map.put(SpawnsEditorType.SPAWN_CHANGE_RESPAWN_TELEPORT, 23);
        map.put(SpawnsEditorType.SPAWN_CHANGE_LOGIN_TELEPORT, 21);
        map.put(MenuItemType.RETURN, 40);
    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull MenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);
        if (menuItem.getType() instanceof SpawnsEditorType type) {
            if (type == SpawnsEditorType.SPAWN_CHANGE_DEFAULT) {
                item.setType(spawn.isDefault() ? Material.LIME_DYE : Material.GRAY_DYE);
            }
            else if (type == SpawnsEditorType.SPAWN_CHANGE_LOGIN_TELEPORT) {
                item.setType(spawn.isLoginTeleportEnabled() ? Material.IRON_DOOR : Material.DARK_OAK_DOOR);
            }
            else if (type == SpawnsEditorType.SPAWN_CHANGE_RESPAWN_TELEPORT) {
                item.setType(spawn.isRespawnTeleportEnabled() ? Material.REDSTONE : Material.GUNPOWDER);
            }
            else if (type == SpawnsEditorType.SPAWN_CHANGE_PERMISSION) {
                item.setType(spawn.isPermissionRequired() ? Material.REDSTONE_TORCH : Material.TORCH);
            }
        }
        ItemUtil.replace(item, this.spawn.replacePlaceholders());
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
