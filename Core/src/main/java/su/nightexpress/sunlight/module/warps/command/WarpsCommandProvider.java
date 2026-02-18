package su.nightexpress.sunlight.module.warps.command;

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
import su.nightexpress.sunlight.module.warps.Warp;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.module.warps.core.WarpsLang;
import su.nightexpress.sunlight.module.warps.core.WarpsPerms;
import su.nightexpress.sunlight.user.UserManager;

import java.util.Optional;
import java.util.function.Predicate;

public class WarpsCommandProvider extends AbstractCommandProvider {

    private static final String ARGUMENT_WARP = "warp";

    private static final String COMMAND_CREATE = "create";
    private static final String COMMAND_UPDATE = "update";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_MENU   = "menu";
    private static final String COMMAND_JUMP   = "jump";
    private static final String COMMAND_EDIT   = "edit";

    private final WarpsModule module;
    private final UserManager userManager;

    public WarpsCommandProvider(@NonNull SunLightPlugin plugin, @NonNull WarpsModule module, @NonNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.userManager = userManager;
    }

    @NonNull
    private ArgumentNodeBuilder<Warp> warpArgument() {
        return this.warpArgument(null);
    }

    @NonNull
    private ArgumentNodeBuilder<Warp> warpArgument(@Nullable Predicate<Warp> predicate) {
        return Commands.argument(ARGUMENT_WARP, (context, str) ->
                Optional.ofNullable(this.module.getWarpById(str))
                    .filter(warp -> predicate == null || predicate.test(warp))
                    .orElseThrow(() -> CommandSyntaxException.custom(WarpsLang.COMMAND_SYNTAX_INVALID_WARP))
            )
            .suggestions((reader, context) -> {
                return this.module.getWarps().stream().map(Warp::getId).toList();
            })
            .localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME);
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral(COMMAND_CREATE, true, new String[]{"setwarp"}, builder -> builder
            .playerOnly()
            .description(WarpsLang.COMMAND_WARPS_CREATE_DESC)
            .permission(WarpsPerms.COMMAND_WARPS_CREATE)
            .withArguments(Arguments.string(CommandArguments.NAME).localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME))
            .executes(this::createWarp)
        );

        this.registerLiteral(COMMAND_UPDATE, false, new String[]{"updatewarp"}, builder -> builder
            .playerOnly()
            .description(WarpsLang.COMMAND_WARPS_UPDATE_DESC)
            .permission(WarpsPerms.COMMAND_WARPS_UPDATE)
            .withArguments(this.warpArgument())
            .executes(this::updateWarp)
        );

        this.registerLiteral(COMMAND_DELETE, true, new String[]{"delwarp"}, builder -> builder
            .description(WarpsLang.COMMAND_WARPS_DELETE_DESC)
            .permission(WarpsPerms.COMMAND_WARPS_DELETE)
            .withArguments(this.warpArgument())
            .executes(this::deleteWarp)
        );

        this.registerLiteral(COMMAND_EDIT, true, new String[]{"editwarp"}, builder -> builder
            .description(WarpsLang.COMMAND_WARPS_EDIT_DESC)
            .permission(WarpsPerms.COMMAND_WARPS_EDIT)
            .withArguments(this.warpArgument())
            .executes(this::editWarp)
        );

        this.registerLiteral(COMMAND_MENU, true, new String[]{"warplist"}, builder -> builder
            .description(WarpsLang.COMMAND_WARPS_LIST_DESC)
            .permission(WarpsPerms.COMMAND_WARPS_LIST)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER).permission(WarpsPerms.COMMAND_WARPS_LIST_OTHERS).optional())
            .executes(this::openWarps)
        );

        this.registerLiteral(COMMAND_JUMP, true, new String[]{"warp"}, builder -> builder
            .description(WarpsLang.COMMAND_WARPS_JUMP_DESC)
            .permission(WarpsPerms.COMMAND_WARPS_JUMP)
            .withArguments(
                this.warpArgument(Warp::isActive).suggestions((reader, context) -> {
                    Player player = context.getPlayer();
                    return (player == null ? this.module.getWarps() : this.module.getAvailableWarps(player)).stream().map(Warp::getId).toList();
                }),
                Arguments.playerName(CommandArguments.PLAYER).optional().permission(WarpsPerms.COMMAND_WARPS_JUMP_OTHERS)
            )
            .withFlags(CommandArguments.FLAG_FORCE)
            .executes(this::jumpToWarp)
        );

        this.registerRoot("Warps", true, new String[]{"warps"},
            map -> {
                map.put(COMMAND_CREATE, "create");
                map.put(COMMAND_UPDATE, "update");
                map.put(COMMAND_DELETE, "delete");
                map.put(COMMAND_EDIT, "edit");
                map.put(COMMAND_MENU, "menu");
                map.put(COMMAND_JUMP, "jump");
            },
            builder -> builder.description(WarpsLang.COMMAND_WARPS_ROOT_DESC).permission(WarpsPerms.COMMAND_WARPS_ROOT)
        );
    }

    private boolean createWarp(@NonNull CommandContext context, @NonNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();

        return this.module.create(player, arguments.getString(CommandArguments.NAME), false);
    }

    private boolean updateWarp(@NonNull CommandContext context, @NonNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        Warp warp = arguments.get(ARGUMENT_WARP, Warp.class);

        return this.module.updateWarp(player, warp);
    }

    private boolean deleteWarp(@NonNull CommandContext context, @NonNull ParsedArguments arguments) {
        Warp warp = arguments.get(ARGUMENT_WARP, Warp.class);

        return this.module.removeWarp(context.getSender(), warp, false);
    }

    private boolean editWarp(@NonNull CommandContext context, @NonNull ParsedArguments arguments) {
        Warp warp = arguments.get(ARGUMENT_WARP, Warp.class);

        return this.module.openWarpSettings(context.getPlayerOrThrow(), warp);
    }

    private boolean openWarps(@NonNull CommandContext context, @NonNull ParsedArguments arguments) {
        return this.runForOnlinePlayerOrSender(context, arguments, this.module, target -> {
            if (!this.module.openWarpsMenu(target)) {
                return false;
            }

            if (context.getSender() != target) {
                this.module.sendPrefixed(WarpsLang.BROWSER_FEEDBACK, context.getSender(), replacer -> replacer.with(CommonPlaceholders.PLAYER.resolver(target)));
            }

            return true;
        });
    }

    private boolean jumpToWarp(@NonNull CommandContext context, @NonNull ParsedArguments arguments) {
        return this.loadPlayerOrSenderAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            Warp warp = arguments.get(ARGUMENT_WARP, Warp.class);
            boolean force = context.hasFlag(CommandArguments.FLAG_FORCE);

            if (context.getSender() != target) {
                this.module.sendPrefixed(WarpsLang.WARP_TELEPORT_FEEDBACK, context.getSender(), replacer -> replacer
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                    .with(warp.placeholders())
                );
            }

            this.module.teleportToWarp(warp, target, force);
        });
    }
}
