package su.nightexpress.sunlight.module.worlds.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.builder.ArgumentNodeBuilder;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.commands.exceptions.CommandSyntaxException;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.module.worlds.WorldsModule;
import su.nightexpress.sunlight.module.worlds.config.WorldsLang;
import su.nightexpress.sunlight.module.worlds.config.WorldsPerms;
import su.nightexpress.sunlight.module.worlds.impl.WorldData;
import su.nightexpress.sunlight.module.worlds.util.DeletionType;
import su.nightexpress.sunlight.module.worlds.util.Placeholders;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class WorldCommands extends AbstractCommandProvider {

    public static final String NODE_CREATE = "world_create";
    public static final String NODE_DELETE = "world_delete";
    public static final String NODE_EDITOR = "world_editor";
    public static final String NODE_LOAD   = "world_load";
    public static final String NODE_UNLOAD = "world_unload";

    private final WorldsModule module;

    public WorldCommands(@NotNull SunLightPlugin plugin, @NotNull WorldsModule module) {
        super(plugin);
        this.module = module;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral(NODE_CREATE, true, new String[]{"createworld"}, builder -> builder
            .description(WorldsLang.COMMAND_CREATE_WORLD_DESC)
            .permission(WorldsPerms.COMMAND_WORLDS_CREATE)
            .withArguments(Arguments.string(CommandArguments.NAME).localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME))
            .executes(this::createWorld)
        );

        this.registerLiteral(NODE_DELETE, true, new String[]{"deleteworld"}, builder -> builder
            .description(WorldsLang.COMMAND_DELETE_WORLD_DESC)
            .permission(WorldsPerms.COMMAND_WORLDS_DELETE)
            .withArguments(dataArgument(module).suggestions((reader, context) -> new ArrayList<>(module.getDataMap().keySet())))
            .executes(this::deleteWorld)
        );

        this.registerLiteral(NODE_EDITOR, true, new String[]{"editworld"}, builder -> builder
            .playerOnly()
            .description(WorldsLang.COMMAND_EDITOR_DESC)
            .permission(WorldsPerms.COMMAND_WORLDS_EDITOR)
            .executes(this::openEditor)
        );

        this.registerLiteral(NODE_LOAD, true, new String[]{"loadworld"}, builder -> builder
            .description(WorldsLang.COMMAND_LOAD_WORLD_DESC)
            .permission(WorldsPerms.COMMAND_WORLDS_LOAD)
            .withArguments(dataArgument(module).suggestions((reader, context) -> module.getDatas().stream().filter(Predicate.not(WorldData::isLoaded)).map(WorldData::getId).toList()))
            .executes(this::loadWorld)
        );

        this.registerLiteral(NODE_UNLOAD, true, new String[]{"unloadworld"}, builder -> builder
            .description(WorldsLang.COMMAND_UNLOAD_WORLD_DESC)
            .permission(WorldsPerms.COMMAND_WORLDS_UNLOAD)
            .withArguments(dataArgument(module).suggestions((reader, context) -> module.getDatas().stream().filter(WorldData::isLoaded).map(WorldData::getId).toList()))
            .executes(this::unloadWorld)
        );

        this.registerRoot("world_manager", true, new String[]{"worldmanager"},
            map -> {
            map.put(NODE_CREATE, "create");
            map.put(NODE_DELETE, "delete");
            map.put(NODE_EDITOR, "editor");
            map.put(NODE_LOAD, "load");
            map.put(NODE_UNLOAD, "unload");
            },
            builder -> builder.description(WorldsLang.COMMAND_WORLDS_ROOT_DESC).permission(WorldsPerms.COMMAND_WORLDS_ROOT)
        );
    }

    @NotNull
    private static ArgumentNodeBuilder<WorldData> dataArgument(@NotNull WorldsModule module) {
        return Commands.argument(CommandArguments.NAME, (context, str) ->
                Optional.ofNullable(module.getWorldData(str)).orElseThrow(() -> CommandSyntaxException.custom(WorldsLang.ERROR_COMMAND_INVALID_WORLD_DATA_ARGUMENT))
            )
            .localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME);
    }

    private boolean createWorld(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        String name = arguments.getString(CommandArguments.NAME);
        WorldData worldData = module.createWorldData(name);

        if (worldData == null) {
            context.send(WorldsLang.COMMAND_CREATE_WORLD_ERROR);
            return false;
        }

        Player player = context.getPlayer();
        if (player != null) {
            module.openGenerationSettings(player, worldData);
        }

        context.send(WorldsLang.COMMAND_CREATE_WORLD_DONE, replacer -> replacer.replace(Placeholders.WORLD_ID, worldData.getId()));
        return true;
    }

    private boolean deleteWorld(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        WorldData worldData = arguments.get(CommandArguments.NAME, WorldData.class);

        if (!worldData.delete(DeletionType.FULL)) {
            context.send(WorldsLang.COMMAND_DELETE_WORLD_ERROR, replacer -> replacer.replace(Placeholders.WORLD_ID, worldData.getId()));
            return false;
        }

        context.send(WorldsLang.COMMAND_DELETE_WORLD_DONE, replacer -> replacer.replace(Placeholders.WORLD_ID, worldData.getId()));
        return true;
    }

    private boolean openEditor(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        module.openEditor(player);
        return true;
    }

    private boolean loadWorld(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        WorldData worldData = arguments.get(CommandArguments.NAME, WorldData.class);

        if (worldData.loadWorld() == null) {
            context.send(WorldsLang.COMMAND_LOAD_WORLD_ERROR, replacer -> replacer.replace(Placeholders.WORLD_ID, worldData.getId()));
            return false;
        }

        context.send(WorldsLang.COMMAND_LOAD_WORLD_DONE, replacer -> replacer.replace(Placeholders.WORLD_ID, worldData.getId()));
        return true;
    }

    private boolean unloadWorld(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        WorldData worldData = arguments.get(CommandArguments.NAME, WorldData.class);

        if (!this.module.unloadWorld(worldData)) {
            context.send(WorldsLang.COMMAND_UNLOAD_WORLD_ERROR);
            return false;
        }

        context.send(WorldsLang.COMMAND_UNLOAD_WORLD_DONE, replacer -> replacer.replace(Placeholders.WORLD_ID, worldData.getId()));
        return true;
    }
}
