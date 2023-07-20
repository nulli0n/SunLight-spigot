package su.nightexpress.sunlight.module.spawns.editor;

import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.spawns.config.SpawnsLang;
import su.nightexpress.sunlight.module.spawns.impl.Spawn;
import su.nightexpress.sunlight.module.spawns.util.Placeholders;

public class SpawnSettingsEditor extends EditorMenu<SunLight, Spawn> {

    //private final Spawn spawn;

    public SpawnSettingsEditor(@NotNull Spawn spawn) {
        super(spawn.plugin(), spawn, Placeholders.EDITOR_TITLE, 45);
        //this.spawn = spawn;

        this.addReturn(40).setClick((viewer, event) -> {
            spawn.getSpawnManager().getEditor().openNextTick(viewer, 1);
        });

        this.addItem(Material.NAME_TAG, EditorLocales.SPAWN_NAME, 11).setClick((viewer, event) -> {
            this.handleInput(viewer, SpawnsLang.SPAWNS_EDITOR_ENTER_NAME, wrapper -> {
                spawn.setName(wrapper.getText());
                spawn.save();
                return true;
            });
        });

        this.addItem(Material.REDSTONE_TORCH, EditorLocales.SPAWN_PERMISSION, 12).setClick((viewer, event) -> {
            spawn.setPermissionRequired(!spawn.isPermissionRequired());
            this.save(viewer);
        }).getOptions().addDisplayModifier((viewer, item) -> item.setType(spawn.isPermissionRequired() ? Material.REDSTONE_TORCH : Material.TORCH));

        this.addItem(Material.COMPARATOR, EditorLocales.SPAWN_PRIORITY, 13).setClick((viewer, event) -> {
            this.handleInput(viewer, SpawnsLang.SPAWNS_EDITOR_ENTER_PRIORITY, wrapper -> {
                spawn.setPriority(wrapper.asInt());
                spawn.save();
                return true;
            });
        });

        this.addItem(Material.LIME_DYE, EditorLocales.SPAWN_DEFAULT, 14).setClick((viewer, event) -> {
            spawn.setDefault(!spawn.isDefault());
            this.save(viewer);
        }).getOptions().addDisplayModifier((viewer, item) -> item.setType(spawn.isDefault() ? Material.LIME_DYE : Material.GRAY_DYE));

        this.addItem(Material.COMPASS, EditorLocales.SPAWN_LOCATION, 15).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                World world = spawn.getLocation().getWorld();
                if (world == null) return;

                world.setSpawnLocation(spawn.getLocation());
                return;
            }
            spawn.setLocation(viewer.getPlayer().getLocation());
            this.save(viewer);
        });

        this.addItem(Material.RED_BED, EditorLocales.SPAWN_RESPAWN_TELEPORT, 23).setClick((viewer, event) -> {
            if (event.isShiftClick()) {
                if (event.isRightClick()) {
                    spawn.getRespawnTeleportGroups().clear();
                    this.save(viewer);
                    return;
                }
                this.handleInput(viewer, SpawnsLang.SPAWNS_EDITOR_ENTER_GROUP, wrapper -> {
                    spawn.getRespawnTeleportGroups().add(wrapper.getTextRaw());
                    spawn.save();
                    return true;
                });
            }
            else {
                spawn.setRespawnTeleportEnabled(!spawn.isRespawnTeleportEnabled());
                this.save(viewer);
            }
        }).getOptions().addDisplayModifier((viewer, item) -> item.setType(spawn.isRespawnTeleportEnabled() ? Material.GREEN_BED : Material.GRAY_BED));

        this.addItem(Material.IRON_DOOR, EditorLocales.SPAWN_LOGIN_TELEPORT, 21).setClick((viewer, event) -> {
            if (event.isShiftClick()) {
               if (event.isRightClick()) {
                   spawn.getLoginTeleportGroups().clear();
                   this.save(viewer);
               }
               else {
                   this.handleInput(viewer, SpawnsLang.SPAWNS_EDITOR_ENTER_GROUP, wrapper -> {
                       spawn.getLoginTeleportGroups().add(wrapper.getTextRaw());
                       spawn.save();
                       return true;
                   });
               }
               return;
            }

            if (event.isRightClick()) {
                spawn.setFirstLoginTeleportEnabled(!spawn.isFirstLoginTeleportEnabled());
            }
            else {
                spawn.setLoginTeleportEnabled(!spawn.isLoginTeleportEnabled());
            }
            this.save(viewer);
        }).getOptions().addDisplayModifier((viewer, item) -> item.setType(spawn.isLoginTeleportEnabled() ? Material.IRON_DOOR : Material.DARK_OAK_DOOR));


        this.getItems().forEach(menuItem -> {
            menuItem.getOptions().addDisplayModifier((viewer, item) -> ItemUtil.replace(item, spawn.replacePlaceholders()));
        });
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.save();
        this.openNextTick(viewer, viewer.getPage());
    }
}
