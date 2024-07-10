package su.nightexpress.sunlight.module.spawns.command;

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
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.command.CommandTools;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.spawns.SpawnsModule;
import su.nightexpress.sunlight.module.spawns.config.SpawnsLang;
import su.nightexpress.sunlight.module.spawns.config.SpawnsPerms;
import su.nightexpress.sunlight.module.spawns.impl.Spawn;
import su.nightexpress.sunlight.module.spawns.util.Placeholders;

public class SpawnCommands {

    public static final String NODE_CREATE = "spawn_create";
    public static final String NODE_DELETE = "spawn_delete";
    public static final String NODE_EDITOR = "spawn_editor";
    public static final String NODE_TELEPORT = "spawn_teleport";

    public static final String DEF_EDITOR_ALIAS = "editspawn";

    @NotNull
    private static ArgumentBuilder<Spawn> spawnArgument(@NotNull SpawnsModule module) {
        return CommandArgument.builder(CommandArguments.NAME, (str, context) -> module.getSpawn(str))
            .localized(Lang.COMMAND_ARGUMENT_NAME_NAME)
            .customFailure(SpawnsLang.ERROR_COMMAND_INVALID_SPAWN_ARGUMENT)
            .withSamples(context -> module.getSpawnIds())
            ;
    }

    public static void load(@NotNull SunLightPlugin plugin, @NotNull SpawnsModule module) {
        CommandRegistry.registerDirectExecutor(NODE_CREATE, (template, config) -> builderSet(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(NODE_DELETE, (template, config) -> builderDelete(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(NODE_EDITOR, (template, config) -> builderEditor(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(NODE_TELEPORT, (template, config) -> builderTeleport(plugin, module, template, config));

        CommandRegistry.addTemplate("setspawn", CommandTemplate.direct(new String[]{"setspawn"}, NODE_CREATE));
        CommandRegistry.addTemplate("delspawn", CommandTemplate.direct(new String[]{"delspawn"}, NODE_DELETE));
        CommandRegistry.addTemplate(DEF_EDITOR_ALIAS, CommandTemplate.direct(new String[]{DEF_EDITOR_ALIAS}, NODE_EDITOR));
        CommandRegistry.addTemplate("spawn", CommandTemplate.direct(new String[]{"spawn"}, NODE_TELEPORT));
    }



    @NotNull
    public static DirectNodeBuilder builderSet(@NotNull SunLightPlugin plugin, @NotNull SpawnsModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(SpawnsLang.COMMAND_SET_SPAWN_DESC)
            .permission(SpawnsPerms.COMMAND_SPAWNS_CREATE)
            .withArgument(ArgumentTypes.string(CommandArguments.NAME).localized(Lang.COMMAND_ARGUMENT_NAME_NAME).withSamples(context -> module.getSpawnIds()))
            .executes((context, arguments) -> setSpawn(plugin, module, context, arguments))
            ;
    }

    public static boolean setSpawn(@NotNull SunLightPlugin plugin, @NotNull SpawnsModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getExecutor();
        if (player == null) return false;

        String spawnId = arguments.getStringArgument(CommandArguments.NAME, Placeholders.DEFAULT);

        return module.createSpawn(player, spawnId);
    }



    @NotNull
    public static DirectNodeBuilder builderDelete(@NotNull SunLightPlugin plugin, @NotNull SpawnsModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(SpawnsLang.COMMAND_DELETE_SPAWN_DESC)
            .permission(SpawnsPerms.COMMAND_SPAWNS_DELETE)
            .withArgument(spawnArgument(module).required())
            .executes((context, arguments) -> deleteSpawn(plugin, module, context, arguments))
            ;
    }

    public static boolean deleteSpawn(@NotNull SunLightPlugin plugin, @NotNull SpawnsModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Spawn spawn = arguments.getArgument(CommandArguments.NAME, Spawn.class);

        context.send(SpawnsLang.COMMAND_DELETE_SPAWN_DONE.getMessage().replace(spawn.replacePlaceholders()));
        return module.deleteSpawn(spawn);
    }


    @NotNull
    public static DirectNodeBuilder builderEditor(@NotNull SunLightPlugin plugin, @NotNull SpawnsModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(SpawnsLang.COMMAND_SPAWNS_EDITOR_DESC)
            .permission(SpawnsPerms.COMMAND_SPAWNS_EDITOR)
            .executes((context, arguments) -> openEditor(plugin, module, context, arguments))
            ;
    }

    public static boolean openEditor(@NotNull SunLightPlugin plugin, @NotNull SpawnsModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getExecutor();
        if (player == null) return false;

        module.openEditor(player);
        return true;
    }


    @NotNull
    public static DirectNodeBuilder builderTeleport(@NotNull SunLightPlugin plugin, @NotNull SpawnsModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(SpawnsLang.COMMAND_SPAWN_DESC)
            .permission(SpawnsPerms.COMMAND_SPAWNS_TELEPORT)
            .withArgument(spawnArgument(module))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(SpawnsPerms.COMMAND_SPAWNS_TELEPORT_OTHERS))
            .withFlag(CommandFlags.silent().permission(SpawnsPerms.COMMAND_SPAWNS_TELEPORT_OTHERS))
            .executes((context, arguments) -> teleport(plugin, module, context, arguments))
            ;
    }

    public static boolean teleport(@NotNull SunLightPlugin plugin, @NotNull SpawnsModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Spawn spawn = arguments.hasArgument(CommandArguments.NAME) ? arguments.getArgument(CommandArguments.NAME, Spawn.class) : module.getDefaultSpawn();
        if (spawn == null) {
            SpawnsLang.COMMAND_SPAWN_ERROR_NO_DEFAULT.getMessage().send(context.getSender());
            return false;
        }

        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        boolean isForced = context.getSender() != target;
        boolean silent = arguments.hasFlag(CommandFlags.SILENT);
        if (!spawn.teleport(target, isForced, silent)) return false;

        if (isForced) {
            SpawnsLang.COMMAND_SPAWN_DONE_OTHERS.getMessage()
                .replace(spawn.replacePlaceholders())
                .replace(Placeholders.forPlayer(target))
                .send(context.getSender());
        }
        return true;
    }
}
