package su.nightexpress.sunlight.module.worlds.editor;

import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuSize;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.api.AutoFill;
import su.nightexpress.nightcore.menu.api.AutoFilled;
import su.nightexpress.nightcore.menu.impl.EditorMenu;
import su.nightexpress.nightcore.util.ItemReplacer;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.worlds.WorldsModule;
import su.nightexpress.sunlight.module.worlds.config.WorldsLang;
import su.nightexpress.sunlight.module.worlds.impl.WrappedWorld;
import su.nightexpress.sunlight.module.worlds.util.Placeholders;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorldRulesEditor extends EditorMenu<SunLightPlugin, WrappedWorld> implements AutoFilled<GameRule<?>> {

    public WorldRulesEditor(@NotNull SunLightPlugin plugin, @NotNull WorldsModule module) {
        super(plugin, WorldsLang.EDITOR_TITLE_GAME_RULES.getString(), MenuSize.CHEST_45);

        this.addNextPage(44);
        this.addPreviousPage(36);
        this.addReturn(40, (viewer, event, wrappedWorld) -> {
            this.runNextTick(() -> module.openWorldSettings(viewer.getPlayer(), wrappedWorld));
        });
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    @SuppressWarnings("unchecked")
    public void onAutoFill(@NotNull MenuViewer viewer, @NotNull AutoFill<GameRule<?>> autoFill) {
        World world = this.getLink(viewer).getWorld();

        autoFill.setSlots(IntStream.range(0, 36).toArray());
        autoFill.setItems(Stream.of(world.getGameRules())
            .map(GameRule::getByName).filter(Objects::nonNull)
            .sorted(Comparator.comparing(GameRule::getName))
            .collect(Collectors.toCollection(ArrayList::new))
        );
        autoFill.setItemCreator(gameRule -> {

            Material material = Material.MAP;
            if (gameRule.getType() == Boolean.class) {
                if (world.getGameRuleValue(gameRule) == Boolean.TRUE) {
                    material = Material.LIME_DYE;
                }
                else material = Material.GRAY_DYE;
            }

            ItemStack item = new ItemStack(material);
            ItemReplacer.create(item).hideFlags().trimmed()
                .readLocale(WorldsLang.EDITOR_WORLD_RULE_OBJECT)
                .replace(Placeholders.GENERIC_NAME, this.getRuleName(gameRule.getName()))
                .replace(Placeholders.GENERIC_VALUE, String.valueOf(world.getGameRuleValue(gameRule)))
                .writeMeta();
            return item;
        });
        autoFill.setClickAction(gameRule -> (viewer1, event) -> {
            if (gameRule.getType() == Boolean.class) {
                GameRule<Boolean> rule = (GameRule<Boolean>) gameRule;
                Boolean value = world.getGameRuleValue(rule);

                world.setGameRule(rule, value != null && !value);
                this.runNextTick(() -> this.open(viewer.getPlayer(), viewer.getPage()));
            }
            else if (gameRule.getType() == Integer.class) {
                GameRule<Integer> rule = (GameRule<Integer>) gameRule;
                this.handleInput(viewer, WorldsLang.EDITOR_INPUT_GENERIC_VALUE, (dialog, input) -> {
                    world.setGameRule(rule, input.asInt());
                    return true;
                });
            }
        });
    }

    @NotNull
    private String getRuleName(@NotNull String name) {
        StringBuilder builder = new StringBuilder();
        for (char letter : name.toCharArray()) {
            if (Character.isUpperCase(letter) && !builder.isEmpty()) builder.append(" ");
            builder.append(letter);
        }
        return StringUtil.capitalizeFully(builder.toString());
    }
}
