package su.nightexpress.sunlight.module.spawns.editor;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuSize;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.impl.EditorMenu;
import su.nightexpress.nightcore.util.ItemReplacer;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.spawns.SpawnsModule;
import su.nightexpress.sunlight.module.spawns.config.SpawnsLang;
import su.nightexpress.sunlight.module.spawns.impl.Spawn;

public class SpawnSettingsEditor extends EditorMenu<SunLightPlugin, Spawn> {

    private final SpawnsModule module;

    public SpawnSettingsEditor(@NotNull SunLightPlugin plugin, @NotNull SpawnsModule module) {
        super(plugin, SpawnsLang.EDITOR_TITLE_SETTINGS.getString(), MenuSize.CHEST_36);
        this.module = module;

        this.addReturn(31, (viewer, event, spawn) -> {
            this.runNextTick(() -> this.module.openEditor(viewer.getPlayer()));
        });

        this.addItem(Material.NAME_TAG, SpawnsLang.EDITOR_SPAWN_NAME, 10, (viewer, event, spawn) -> {
            this.handleInput(viewer, SpawnsLang.EDITOR_ENTER_NAME, (dialog, input) -> {
                spawn.setName(input.getText());
                spawn.save();
                return true;
            });
        });

        this.addItem(Material.REDSTONE_TORCH, SpawnsLang.EDITOR_SPAWN_PERMISSION, 11, (viewer, event, spawn) -> {
            spawn.setPermissionRequired(!spawn.isPermissionRequired());
            this.save(viewer);
        }).getOptions().addDisplayModifier((viewer, item) -> item.setType(this.getLink(viewer).isPermissionRequired() ? Material.REDSTONE_TORCH : Material.TORCH));

        this.addItem(Material.COMPARATOR, SpawnsLang.EDITOR_SPAWN_EDIT_PRIORITY, 12, (viewer, event, spawn) -> {
            this.handleInput(viewer, SpawnsLang.EDITOR_ENTER_PRIORITY, (dialog, input) -> {
                spawn.setPriority(input.asInt());
                spawn.save();
                return true;
            });
        });

        this.addItem(Material.COMPASS, SpawnsLang.EDITOR_SPAWN_LOCATION, 13, (viewer, event, spawn) -> {
            if (event.isRightClick()) {
                World world = spawn.getLocation().getWorld();
                if (world == null) return;

                world.setSpawnLocation(spawn.getLocation());
                return;
            }
            spawn.setLocation(viewer.getPlayer().getLocation());
            this.save(viewer);
        });

        this.addItem(Material.IRON_DOOR, SpawnsLang.EDITOR_SPAWN_LOGIN_TELEPORT_TOGGLE, 14, (viewer, event, spawn) -> {
            spawn.setLoginTeleport(!spawn.isLoginTeleport());
            this.save(viewer);
        }).getOptions().addDisplayModifier((viewer, item) -> item.setType(this.getLink(viewer).isLoginTeleport() ? Material.IRON_DOOR : Material.DARK_OAK_DOOR));

        this.addItem(Material.PAPER, SpawnsLang.EDITOR_SPAWN_LOGIN_TELEPORT_RANKS, 23, (viewer, event, spawn) -> {
            if (event.isRightClick()) {
                spawn.getLoginGroups().clear();
                this.save(viewer);
            }
            else {
                this.handleInput(viewer, SpawnsLang.EDITOR_ENTER_GROUP, (dialog, input) -> {
                    spawn.getLoginGroups().add(input.getTextRaw());
                    spawn.save();
                    return true;
                });
            }
        }).getOptions().setVisibilityPolicy(viewer -> this.getLink(viewer).isLoginTeleport());


        this.addItem(Material.RED_BED, SpawnsLang.EDITOR_SPAWN_DEATH_TELEPORT_TOGGLE, 15, (viewer, event, spawn) -> {
            spawn.setDeathTeleport(!spawn.isDeathTeleport());
            this.save(viewer);
        }).getOptions().addDisplayModifier((viewer, item) -> item.setType(this.getLink(viewer).isDeathTeleport() ? Material.GREEN_BED : Material.GRAY_BED));

        this.addItem(Material.PAPER, SpawnsLang.EDITOR_SPAWN_DEATH_TELEPORT_RANKS, 24, (viewer, event, spawn) -> {
            if (event.isRightClick()) {
                spawn.getRespawnGroups().clear();
                this.save(viewer);
                return;
            }
            this.handleInput(viewer, SpawnsLang.EDITOR_ENTER_GROUP, (dialog, input) -> {
                spawn.getRespawnGroups().add(input.getTextRaw());
                spawn.save();
                return true;
            });
        }).getOptions().setVisibilityPolicy(viewer -> this.getLink(viewer).isDeathTeleport());



        this.getItems().forEach(menuItem -> menuItem.getOptions().addDisplayModifier((viewer, item) -> {
            ItemReplacer.replace(item, this.getLink(viewer).getEditorPlaceholders().replacer());
        }));
    }

    private void save(@NotNull MenuViewer viewer) {
        this.getLink(viewer).save();
        this.runNextTick(() -> this.flush(viewer));
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {

    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }
}
