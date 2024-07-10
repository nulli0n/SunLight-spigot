package su.nightexpress.sunlight.module.worlds.editor;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuSize;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.impl.EditorMenu;
import su.nightexpress.nightcore.util.ItemReplacer;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.worlds.WorldsModule;
import su.nightexpress.sunlight.module.worlds.config.WorldsLang;
import su.nightexpress.sunlight.module.worlds.impl.WorldData;
import su.nightexpress.sunlight.module.worlds.impl.WrappedWorld;

import java.util.function.Predicate;

public class WorldMainEditor extends EditorMenu<SunLightPlugin, WrappedWorld> {

    private static final String TEXTURE_UNLOAD        = "3e4f2f9698c3f186fe44cc63d2f3c4f9a241223acf0581775d9cecd7075";
    private static final String TEXTURE_AUTO_LOAD_OFF = "f53a8d5855c99787dfca1687b2153fe51284eb9b6017be211cc4cc266bd26ffa";
    private static final String TEXTURE_AUTO_LOAD_ON  = "629b19ad3c51edad643e44dd5da6b9d731754c15a9ffbf81c32c2886c9290577";

    public WorldMainEditor(@NotNull SunLightPlugin plugin, @NotNull WorldsModule module) {
        super(plugin, WorldsLang.EDITOR_TITLE_SETTINGS.getString(), MenuSize.CHEST_18);

        this.addReturn(13, (viewer, event, wrappedWorld) -> {
            this.runNextTick(() -> module.openEditor(viewer.getPlayer()));
        });

        this.addItem(ItemUtil.getSkinHead(TEXTURE_AUTO_LOAD_OFF), WorldsLang.EDITOR_WORLD_AUTO_LOAD, 6, (viewer, event, wrappedWorld) -> {
            WorldData data = wrappedWorld.getData();
            data.setAutoLoad(!data.isAutoLoad());
            this.save(viewer);
        }).getOptions().setDisplayModifier((viewer, item) -> {
            if (this.getLink(viewer).getData().isAutoLoad()) {
                ItemUtil.setHeadSkin(item, TEXTURE_AUTO_LOAD_ON);
            }
        }).setVisibilityPolicy(this.visibility(WrappedWorld::isCustom));

        this.addItem(ItemUtil.getSkinHead(TEXTURE_UNLOAD), WorldsLang.EDITOR_WORLD_UNLOAD, 7, (viewer, event, wrappedWorld) -> {
            wrappedWorld.getData().unloadWorld();
            this.runNextTick(() -> module.openGenerationSettings(viewer.getPlayer(), wrappedWorld.getData()));
        }).getOptions().setVisibilityPolicy(this.visibility(WrappedWorld::isCustom));




        this.addItem(Material.BUCKET, WorldsLang.EDITOR_WORLD_AUTO_WIPE, 8, (viewer, event, wrappedWorld) -> {
            WorldData data = wrappedWorld.getData();
            if (event.isLeftClick()) {
                data.setAutoReset(!data.isAutoReset());
                this.save(viewer);
                return;
            }
            if (event.getClick() == ClickType.DROP) {
                data.setLatestWipeDate();
                this.save(viewer);
                return;
            }
            if (event.isRightClick()) {
                this.handleInput(viewer, WorldsLang.EDITOR_INPUT_GENERIC_SECONDS, (dialog, wrapper) -> {
                    data.setResetInterval(wrapper.asAnyInt(-1));
                    data.save();
                    return true;
                });
            }
        }).getOptions().setVisibilityPolicy(this.visibility(WrappedWorld::isCustom)).setDisplayModifier((viewer, item) -> {
            if (this.getLink(viewer).getData().isAutoReset()) item.setType(Material.LAVA_BUCKET);
        });


        this.addItem(Material.MOJANG_BANNER_PATTERN, WorldsLang.EDITOR_WORLD_GAME_RULES, 1, (viewer, event, wrappedWorld) -> {
            this.runNextTick(() -> module.openGameRules(viewer.getPlayer(), wrappedWorld));
        });

        this.addItem(Material.ROTTEN_FLESH, WorldsLang.EDITOR_WORLD_DIFFICULTY, 0, (viewer, event, wrappedWorld) -> {
            World world = wrappedWorld.getWorld();
            world.setDifficulty(Lists.next(world.getDifficulty()));
            this.runNextTick(() -> this.flush(viewer));
        });


        this.getItems().forEach(menuItem -> menuItem.getOptions().addDisplayModifier((viewer, itemStack) -> {
            ItemReplacer.replace(itemStack, this.getLink(viewer).replacePlaceholders());
        }));
    }

    @NotNull
    private Predicate<MenuViewer> visibility(@NotNull Predicate<WrappedWorld> predicate) {
        return viewer -> predicate.test(this.getLink(viewer));
    }

    private void save(@NotNull MenuViewer viewer) {
        this.getLink(viewer).getData().save();
        this.runNextTick(() -> this.flush(viewer));
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {

    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }
}
