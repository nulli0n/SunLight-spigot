package su.nightexpress.sunlight.module.worlds.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.CommandArgument;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.ArgumentBuilder;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.module.worlds.WorldsModule;
import su.nightexpress.sunlight.module.worlds.config.WorldsLang;
import su.nightexpress.sunlight.module.worlds.config.WorldsPerms;
import su.nightexpress.sunlight.module.worlds.impl.WorldData;
import su.nightexpress.sunlight.module.worlds.util.DeletionType;
import su.nightexpress.sunlight.module.worlds.util.Placeholders;

import java.util.ArrayList;
import java.util.function.Predicate;

public class WorldCommands {

    public static final String NODE_CREATE = "world_create";
    public static final String NODE_DELETE = "world_delete";
    public static final String NODE_EDITOR = "world_editor";
    public static final String NODE_LOAD   = "world_load";
    public static final String NODE_UNLOAD = "world_unload";

    public static void load(@NotNull SunLightPlugin plugin, @NotNull WorldsModule module) {
        CommandRegistry.registerDirectExecutor(NODE_CREATE, (template, config) -> builderCreate(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(NODE_DELETE, (template, config) -> builderDelete(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(NODE_EDITOR, (template, config) -> builderEditor(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(NODE_LOAD, (template, config) -> builderLoad(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(NODE_UNLOAD, (template, config) -> builderUnload(plugin, module, template, config));

        CommandRegistry.addTemplate("worldmanager", CommandTemplate.group(new String[]{"worldmanager"},
            "World commands.",
            WorldsPerms.PREFIX_COMMAND + "worldmanager",
            CommandTemplate.direct(new String[]{"create"}, NODE_CREATE),
            CommandTemplate.direct(new String[]{"delete"}, NODE_DELETE),
            CommandTemplate.direct(new String[]{"editor"}, NODE_EDITOR),
            CommandTemplate.direct(new String[]{"load"}, NODE_LOAD),
            CommandTemplate.direct(new String[]{"unload"}, NODE_UNLOAD)
        ));

        CommandRegistry.addTemplate("createworld", CommandTemplate.direct(new String[]{"createworld"}, NODE_CREATE));
        CommandRegistry.addTemplate("deleteworld", CommandTemplate.direct(new String[]{"deleteworld"}, NODE_DELETE));
        CommandRegistry.addTemplate("editworld", CommandTemplate.direct(new String[]{"editworld"}, NODE_EDITOR));
        CommandRegistry.addTemplate("loadworld", CommandTemplate.direct(new String[]{"loadworld"}, NODE_LOAD));
        CommandRegistry.addTemplate("unloadworld", CommandTemplate.direct(new String[]{"unloadworld"}, NODE_UNLOAD));
    }

    @NotNull
    private static ArgumentBuilder<WorldData> dataArgument(@NotNull WorldsModule module) {
        return CommandArgument.builder(CommandArguments.NAME, module::getWorldData)
            .required()
            .localized(WorldsLang.COMMAND_ARGUMENT_NAME_NAME)
            .customFailure(WorldsLang.ERROR_COMMAND_INVALID_WORLD_DATA_ARGUMENT)
            ;
    }

    @NotNull
    public static DirectNodeBuilder builderCreate(@NotNull SunLightPlugin plugin, @NotNull WorldsModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(WorldsLang.COMMAND_CREATE_WORLD_DESC)
            .permission(WorldsPerms.COMMAND_WORLDS_CREATE)
            .withArgument(ArgumentTypes.string(CommandArguments.NAME).required().localized(WorldsLang.COMMAND_ARGUMENT_NAME_NAME))
            .executes((context, arguments) -> createWorld(plugin, module, context, arguments))
            ;
    }

    public static boolean createWorld(@NotNull SunLightPlugin plugin, @NotNull WorldsModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        String name = arguments.getStringArgument(CommandArguments.NAME);
        WorldData worldData = module.createWorldData(name);

        if (worldData == null) {
            context.send(WorldsLang.COMMAND_CREATE_WORLD_ERROR.getMessage());
            return false;
        }

        Player player = context.getExecutor();
        if (player != null) {
            module.openGenerationSettings(player, worldData);
        }

        context.send(WorldsLang.COMMAND_CREATE_WORLD_DONE.getMessage().replace(Placeholders.WORLD_ID, worldData.getId()));
        return true;
    }




    @NotNull
    public static DirectNodeBuilder builderDelete(@NotNull SunLightPlugin plugin, @NotNull WorldsModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(WorldsLang.COMMAND_DELETE_WORLD_DESC)
            .permission(WorldsPerms.COMMAND_WORLDS_DELETE)
            .withArgument(dataArgument(module).withSamples(context -> new ArrayList<>(module.getDataMap().keySet())))
            .executes((context, arguments) -> deleteWorld(plugin, module, context, arguments))
            ;
    }

    public static boolean deleteWorld(@NotNull SunLightPlugin plugin, @NotNull WorldsModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        WorldData worldData = arguments.getArgument(CommandArguments.NAME, WorldData.class);

        if (!worldData.delete(DeletionType.FULL)) {
            context.send(WorldsLang.COMMAND_DELETE_WORLD_ERROR.getMessage().replace(Placeholders.WORLD_ID, worldData.getId()));
            return false;
        }

        context.send(WorldsLang.COMMAND_DELETE_WORLD_DONE.getMessage().replace(Placeholders.WORLD_ID, worldData.getId()));
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderEditor(@NotNull SunLightPlugin plugin, @NotNull WorldsModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(WorldsLang.COMMAND_EDITOR_DESC)
            .permission(WorldsPerms.COMMAND_WORLDS_EDITOR)
            .executes((context, arguments) -> openEditor(plugin, module, context, arguments))
            ;
    }

    public static boolean openEditor(@NotNull SunLightPlugin plugin, @NotNull WorldsModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getExecutor();
        if (player == null) return false;

        module.openEditor(player);
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderLoad(@NotNull SunLightPlugin plugin, @NotNull WorldsModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(WorldsLang.COMMAND_LOAD_WORLD_DESC)
            .permission(WorldsPerms.COMMAND_WORLDS_LOAD)
            .withArgument(dataArgument(module).withSamples(context -> module.getDatas().stream().filter(Predicate.not(WorldData::isLoaded)).map(WorldData::getId).toList()))
            .executes((context, arguments) -> loadWorld(plugin, module, context, arguments))
            ;
    }

    public static boolean loadWorld(@NotNull SunLightPlugin plugin, @NotNull WorldsModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        WorldData worldData = arguments.getArgument(CommandArguments.NAME, WorldData.class);

        if (worldData.loadWorld() == null) {
            context.send(WorldsLang.COMMAND_LOAD_WORLD_ERROR.getMessage().replace(Placeholders.WORLD_ID, worldData.getId()));
            return false;
        }

        context.send(WorldsLang.COMMAND_LOAD_WORLD_DONE.getMessage().replace(Placeholders.WORLD_ID, worldData.getId()));
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderUnload(@NotNull SunLightPlugin plugin, @NotNull WorldsModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(WorldsLang.COMMAND_UNLOAD_WORLD_DESC)
            .permission(WorldsPerms.COMMAND_WORLDS_UNLOAD)
            .withArgument(dataArgument(module).withSamples(context -> module.getDatas().stream().filter(WorldData::isLoaded).map(WorldData::getId).toList()))
            .executes((context, arguments) -> unloadWorld(plugin, module, context, arguments))
            ;
    }

    public static boolean unloadWorld(@NotNull SunLightPlugin plugin, @NotNull WorldsModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        WorldData worldData = arguments.getArgument(CommandArguments.NAME, WorldData.class);

        if (!worldData.unloadWorld()) {
            context.send(WorldsLang.COMMAND_UNLOAD_WORLD_ERROR.getMessage());
            return false;
        }

        context.send(WorldsLang.COMMAND_UNLOAD_WORLD_DONE.getMessage().replace(Placeholders.WORLD_ID, worldData.getId()));
        return true;
    }
}
