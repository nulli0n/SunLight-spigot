package su.nightexpress.sunlight.module.spawns.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.builder.ArgumentNodeBuilder;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.commands.exceptions.CommandSyntaxException;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.module.spawns.SpawnsModule;
import su.nightexpress.sunlight.module.spawns.config.SpawnsLang;
import su.nightexpress.sunlight.module.spawns.config.SpawnsPerms;
import su.nightexpress.sunlight.module.spawns.Spawn;
import su.nightexpress.sunlight.user.UserManager;

import java.util.Optional;

public class SpawnCommands extends AbstractCommandProvider {

    private static final String COMMAND_CREATE = "spawn_create";
    private static final String COMMAND_DELETE = "spawn_delete";
    private static final String COMMAND_EDITOR   = "spawn_editor";
    private static final String COMMAND_TELEPORT = "spawn_teleport";

    public static final String DEF_EDITOR_ALIAS = "editspawn";

    private final SpawnsModule module;
    private final UserManager userManager;

    public SpawnCommands(@NotNull SunLightPlugin plugin, @NotNull SpawnsModule module, @NotNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.userManager = userManager;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral(COMMAND_CREATE, true, new String[]{"setspawn"}, builder -> builder
            .playerOnly()
            .description(SpawnsLang.COMMAND_SPAWN_SET_DESC)
            .permission(SpawnsPerms.COMMAND_SPAWNS_CREATE)
            .withArguments(Arguments.string(CommandArguments.NAME).optional().localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME).suggestions((reader, context) -> module.getSpawnIds()))
            .executes(this::setSpawn)
        );

        this.registerLiteral(COMMAND_DELETE, true, new String[]{"delspawn"}, builder -> builder
            .description(SpawnsLang.COMMAND_SPAWN_DELETE_DESC)
            .permission(SpawnsPerms.COMMAND_SPAWNS_DELETE)
            .withArguments(spawnArgument())
            .executes(this::deleteSpawn)
        );

        this.registerLiteral(COMMAND_EDITOR, true, new String[]{"editspawn"}, builder -> builder
            .playerOnly()
            .description(SpawnsLang.COMMAND_SPAWN_EDITOR_DESC)
            .permission(SpawnsPerms.COMMAND_SPAWNS_EDITOR)
            .executes(this::openEditor)
        );

        this.registerLiteral(COMMAND_TELEPORT, true, new String[]{"spawn"}, builder -> builder
            .description(SpawnsLang.COMMAND_SPAWN_TELEPORT_DESC)
            .permission(SpawnsPerms.COMMAND_SPAWNS_TELEPORT)
            .withArguments(
                spawnArgument().optional(),
                Arguments.playerName(CommandArguments.PLAYER).permission(SpawnsPerms.COMMAND_SPAWNS_TELEPORT_OTHERS).optional()
            )
            .withFlags(CommandArguments.FLAG_SILENT, CommandArguments.FLAG_FORCE)
            .executes(this::teleport)
        );
    }

    @NotNull
    private ArgumentNodeBuilder<Spawn> spawnArgument() {
        return Commands.argument(CommandArguments.NAME, (context, str) ->
                Optional.ofNullable(this.module.getSpawn(str)).orElseThrow(() -> CommandSyntaxException.custom(SpawnsLang.COMMAND_SYNTAX_INVALID_SPAWN))
            )
            .localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME)
            .suggestions((reader, context) -> this.module.getSpawnIds());
    }

    private boolean setSpawn(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        String spawnId = arguments.getString(CommandArguments.NAME, SLPlaceholders.DEFAULT);
        return module.createSpawn(player, spawnId);
    }

    private boolean deleteSpawn(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Spawn spawn = arguments.get(CommandArguments.NAME, Spawn.class);

        this.module.sendPrefixed(SpawnsLang.SPAWN_DELETE_FEEDBACK, context.getSender(), replacer -> replacer.with(spawn.placeholders()));

        return this.module.deleteSpawn(spawn);
    }

    private boolean openEditor(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        this.module.openEditor(player);
        return true;
    }

    private boolean teleport(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Spawn spawn = arguments.contains(CommandArguments.NAME) ? arguments.get(CommandArguments.NAME, Spawn.class) : this.module.getDefaultSpawn();
        if (spawn == null) {
            this.module.sendPrefixed(SpawnsLang.ERROR_NO_DEFAULT_SPAWN, context.getSender());
            return false;
        }

        return this.loadPlayerOrSenderAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            boolean force = arguments.contains(CommandArguments.FLAG_FORCE);
            boolean silent = arguments.contains(CommandArguments.FLAG_SILENT);
            if (!this.module.teleport(spawn, target, force, silent)) return;

            if (force) {
                this.module.sendPrefixed(SpawnsLang.SPAWN_TELEPORT_FEEDBACK, context.getSender(), replacer -> replacer
                    .with(spawn.placeholders())
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                );
            }
        });
    }
}
