package su.nightexpress.sunlight.module.worlds.editor;

import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.worlds.config.WorldsLang;
import su.nightexpress.sunlight.module.worlds.impl.WorldConfig;
import su.nightexpress.sunlight.module.worlds.util.Placeholders;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class WorldRulesEditor extends EditorMenu<SunLight, WorldConfig> implements AutoPaged<GameRule<?>> {

    private final WorldConfig worldConfig;

    public WorldRulesEditor(@NotNull SunLight plugin, @NotNull WorldConfig worldConfig) {
        super(plugin, worldConfig, "Game Rules for: " + worldConfig.getId(), 45);
        this.worldConfig = worldConfig;

        this.addNextPage(44);
        this.addPreviousPage(36);
        this.addReturn(40).setClick((viewer, event) -> {
            this.plugin.runTask(task -> worldConfig.getEditor().open(viewer.getPlayer(), 1));
        });
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        options.setTitle(options.getTitle().replace(Placeholders.GENERIC_WORLD, this.worldConfig.getId()));
        this.getItemsForPage(viewer).forEach(this::addItem);
    }

    @NotNull
    private String getRuleName(@NotNull String name) {
        StringBuilder builder = new StringBuilder();
        for (char c : name.toCharArray()) {
            if (Character.isUpperCase(c) && builder.length() > 0) builder.append(" ");
            builder.append(c);
        }
        return StringUtil.capitalizeFully(builder.toString());
    }

    @Override
    public int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    @NotNull
    public List<GameRule<?>> getObjects(@NotNull Player player) {
        return Arrays.stream(GameRule.values()).sorted(Comparator.comparing(GameRule::getName)).toList();
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull GameRule<?> gameRule) {
        World world = this.worldConfig.getWorld();
        if (world == null) return new ItemStack(Material.AIR);

        Material material = Material.MAP;
        if (gameRule.getType() == Boolean.class) {
            if (world.getGameRuleValue(gameRule) == Boolean.TRUE) {
                material = Material.LIME_DYE;
            }
            else material = Material.GRAY_DYE;
        }

        ItemStack item = new ItemStack(material);
        ItemUtil.mapMeta(item, meta -> {
            meta.setDisplayName(EditorLocales.WORLD_RULE_OBJECT.getLocalizedName());
            meta.setLore(EditorLocales.WORLD_RULE_OBJECT.getLocalizedLore());
            meta.addItemFlags(ItemFlag.values());
            ItemUtil.replace(meta, str -> str
                .replace(Placeholders.GENERIC_NAME, this.getRuleName(gameRule.getName()))
                .replace(Placeholders.GENERIC_VALUE, String.valueOf(world.getGameRuleValue(gameRule)))
            );
        });
        return item;
    }

    @Override
    @NotNull
    @SuppressWarnings("unchecked")
    public ItemClick getObjectClick(@NotNull GameRule<?> gameRule) {
        return (viewer, event) -> {
            World world = this.worldConfig.getWorld();
            if (world == null) {
                this.plugin.runTask(task -> worldConfig.getEditor().open(viewer.getPlayer(), 1));
                return;
            }

            if (gameRule.getType() == Boolean.class) {
                GameRule<Boolean> gBool = (GameRule<Boolean>) gameRule;
                Boolean has = world.getGameRuleValue(gBool);

                world.setGameRule(gBool, has != null && !has);
                this.plugin.runTask(task -> this.open(viewer.getPlayer(), viewer.getPage()));
            }
            else if (gameRule.getType() == Integer.class) {
                GameRule<Integer> gInt = (GameRule<Integer>) gameRule;
                this.handleInput(viewer, WorldsLang.EDITOR_ENTER_VALUE, wrapper -> {
                    world.setGameRule(gInt, StringUtil.getInteger(wrapper.getText(), 0));
                    return true;
                });
            }
        };
    }
}
