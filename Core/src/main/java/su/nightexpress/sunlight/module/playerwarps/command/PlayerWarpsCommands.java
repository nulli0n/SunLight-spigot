package su.nightexpress.sunlight.module.playerwarps.command;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.builder.ArgumentNodeBuilder;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.commands.exceptions.CommandSyntaxException;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.module.playerwarps.PlayerWarp;
import su.nightexpress.sunlight.module.playerwarps.core.PlayerWarpsLang;
import su.nightexpress.sunlight.module.playerwarps.PlayerWarpsModule;
import su.nightexpress.sunlight.module.playerwarps.core.PlayerWarpsPerms;
import su.nightexpress.sunlight.user.UserManager;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class PlayerWarpsCommands extends AbstractCommandProvider {

    private static final String ARGUMENT_WARP = "warp";

    private static final String COMMAND_CREATE = "create";
    private static final String COMMAND_UPDATE = "update";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_MENU   = "menu";
    private static final String COMMAND_JUMP   = "teleport";

    private final PlayerWarpsModule module;
    private final UserManager       userManager;

    public PlayerWarpsCommands(@NonNull SunLightPlugin plugin, @NonNull PlayerWarpsModule module, @NonNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.userManager = userManager;
    }

    /*@NonNull
    private ArgumentNodeBuilder<PlayerWarp> warpArgument() {
        return this.warpArgument(null, null);
    }*/

    @NonNull
    private ArgumentNodeBuilder<PlayerWarp> warpArgument(@Nullable BiFunction<Player, PlayerWarp, Boolean> suggestions) {
        return this.warpArgument(null, suggestions);
    }

    @NonNull
    private ArgumentNodeBuilder<PlayerWarp> warpArgument(@Nullable Predicate<PlayerWarp> predicate,
                                                         @Nullable BiFunction<Player, PlayerWarp, Boolean> suggestions) {
        return Commands.argument(ARGUMENT_WARP, (context, str) -> Optional.ofNullable(this.module.getRepository()
            .getById(str))
            .filter(warp -> predicate == null || predicate.test(warp))
            .orElseThrow(() -> CommandSyntaxException.custom(PlayerWarpsLang.COMMAND_SYNTAX_INVALID_WARP))
        )
            .localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME)
            .suggestions((reader, context) -> {
                Player player = context.getPlayer();
                return this.module.getRepository().stream()
                    .filter(warp -> player == null || (suggestions == null || suggestions.apply(player, warp)))
                    .map(PlayerWarp::getId)
                    .toList();
            });
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral(COMMAND_CREATE, false, new String[]{"setplayerwarp"}, builder -> builder
            .playerOnly()
            .description(PlayerWarpsLang.COMMAND_WARPS_CREATE_DESC)
            .permission(PlayerWarpsPerms.COMMAND_WARPS_CREATE)
            .withArguments(Arguments.string(CommandArguments.NAME).localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME))
            .executes(this::createWarp)
        );

        this.registerLiteral(COMMAND_UPDATE, false, new String[]{"updateplayerwarp"}, builder -> builder
            .playerOnly()
            .description(PlayerWarpsLang.COMMAND_WARPS_UPDATE_DESC)
            .permission(PlayerWarpsPerms.COMMAND_WARPS_UPDATE)
            .withArguments(this.warpArgument((player, warp) -> warp.canEdit(player)))
            .executes(this::updateWarp)
        );

        this.registerLiteral(COMMAND_DELETE, false, new String[]{"delplayerwarp"}, builder -> builder
            .description(PlayerWarpsLang.COMMAND_WARPS_DELETE_DESC)
            .permission(PlayerWarpsPerms.COMMAND_WARPS_DELETE)
            .withArguments(this.warpArgument((player, warp) -> warp.canEdit(player)))
            .executes(this::deleteWarp)
        );

        this.registerLiteral(COMMAND_JUMP, false, new String[]{"playerwarp"}, builder -> builder
            .description(PlayerWarpsLang.COMMAND_WARPS_JUMP_DESC)
            .permission(PlayerWarpsPerms.COMMAND_WARPS_JUMP)
            .withArguments(
                this.warpArgument(PlayerWarp::isActive, (player, warp) -> warp.canUse(player)),
                Arguments.playerName(CommandArguments.PLAYER).optional().permission(
                    PlayerWarpsPerms.COMMAND_WARPS_JUMP_OTHERS)
            )
            .withFlags(CommandArguments.FLAG_FORCE)
            .executes(this::moveToWarp)
        );

        this.registerLiteral(COMMAND_MENU, true, new String[]{"pwarps"}, builder -> builder
            .description(PlayerWarpsLang.COMMAND_WARPS_LIST_DESC)
            .permission(PlayerWarpsPerms.COMMAND_WARPS_LIST)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER).permission(
                PlayerWarpsPerms.COMMAND_WARPS_LIST_OTHERS).optional())
            .executes(this::openWarps)
        );

        this.registerRoot("PlayerWarps", true, new String[]{"pwarp", "playerwarps"},
            map -> {
                map.put(COMMAND_CREATE, "create");
                map.put(COMMAND_UPDATE, "update");
                map.put(COMMAND_DELETE, "delete");
                map.put(COMMAND_MENU, "menu");
                map.put(COMMAND_JUMP, "jump");
            },
            builder -> builder.description(PlayerWarpsLang.COMMAND_WARPS_ROOT_DESC).permission(
                PlayerWarpsPerms.COMMAND_WARPS_ROOT)
        );
    }

    private boolean createWarp(@NonNull CommandContext context, @NonNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();

        return module.create(player, arguments.getString(CommandArguments.NAME), false);
    }

    private boolean updateWarp(@NonNull CommandContext context, @NonNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        PlayerWarp warp = arguments.get(ARGUMENT_WARP, PlayerWarp.class);

        return this.module.updateWarp(player, warp, false);
    }

    private boolean deleteWarp(@NonNull CommandContext context, @NonNull ParsedArguments arguments) {
        PlayerWarp warp = arguments.get(ARGUMENT_WARP, PlayerWarp.class);

        return this.module.removeWarp(context.getSender(), warp, false);
    }

    private boolean openWarps(@NonNull CommandContext context, @NonNull ParsedArguments arguments) {
        return this.runForOnlinePlayerOrSender(context, arguments, this.module, target -> {
            this.module.openWarpsMenu(target);

            if (context.getSender() != target) {
                this.module.sendPrefixed(PlayerWarpsLang.WARP_LIST_OPEN_FEEDBACK, context.getSender(),
                    replacer -> replacer.with(CommonPlaceholders.PLAYER.resolver(target)));
            }

            return true;
        });
    }

    private boolean moveToWarp(@NonNull CommandContext context, @NonNull ParsedArguments arguments) {
        return this.loadPlayerOrSenderAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            PlayerWarp warp = arguments.get(ARGUMENT_WARP, PlayerWarp.class);
            boolean force = context.hasFlag(CommandArguments.FLAG_FORCE);

            if (context.getSender() != target) {
                this.module.sendPrefixed(PlayerWarpsLang.WARP_JUMP_FEEDBACK, context.getSender(), replacer -> replacer
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                    .with(warp.placeholders())
                );
            }

            this.module.teleportToWarp(warp, target, force);
        });
    }
}
