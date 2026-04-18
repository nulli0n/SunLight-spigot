package su.nightexpress.sunlight.module.ptp.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.builder.LiteralNodeBuilder;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.mode.ToggleMode;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.module.ptp.PTPModule;
import su.nightexpress.sunlight.module.ptp.PTPProperties;
import su.nightexpress.sunlight.module.ptp.config.PTPLang;
import su.nightexpress.sunlight.module.ptp.config.PTPPerms;
import su.nightexpress.sunlight.module.ptp.request.TeleportMode;
import su.nightexpress.sunlight.module.ptp.request.TeleportRequest;
import su.nightexpress.sunlight.user.UserManager;

import java.util.Collections;
import java.util.Objects;

public class PTPCommands extends AbstractCommandProvider {

    private static final String COMMAND_OFF    = "off";
    private static final String COMMAND_ON     = "on";
    private static final String COMMAND_TOGGLE = "toggle";

    private static final String COMMAND_ACCEPT  = "ptp_accept";
    private static final String COMMAND_DECLINE = "ptp_decline";
    private static final String COMMAND_REQUEST = "request";
    private static final String COMMAND_INVITE  = "invite";

    public static final String ACCEPT_NAME  = "tpyes";
    public static final String DECLINE_NAME = "tpno";

    private final PTPModule   module;
    private final UserManager userManager;

    public PTPCommands(@NotNull SunLightPlugin plugin, PTPModule module, @NotNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.userManager = userManager;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral(COMMAND_ACCEPT, true, new String[]{ACCEPT_NAME}, builder -> {
            builder.description(PTPLang.COMMAND_ACCEPT_DESC);
            builder.permission(PTPPerms.COMMAND_ACCEPT);
            this.builderAccept(builder, true);
        }
        );

        this.registerLiteral(COMMAND_DECLINE, true, new String[]{DECLINE_NAME}, builder -> {
            builder.description(PTPLang.COMMAND_DECLINE_DESC);
            builder.permission(PTPPerms.COMMAND_DECLINE);
            this.builderAccept(builder, false);
        }
        );

        this.registerLiteral(COMMAND_REQUEST, true, new String[]{"tpa", "call"}, builder -> {
            builder.description(PTPLang.COMMAND_REQUEST_DESC);
            builder.permission(PTPPerms.COMMAND_REQUEST);
            this.buildRequest(builder, TeleportMode.REQUEST);
        });

        this.registerLiteral(COMMAND_INVITE, true, new String[]{"tpahere", "tpi"}, builder -> {
            builder.description(PTPLang.COMMAND_INVITE_DESC);
            builder.permission(PTPPerms.COMMAND_INVITE);
            this.buildRequest(builder, TeleportMode.INVITE);
        });

        this.registerLiteral(COMMAND_TOGGLE, true, new String[]{"tptoggle"}, builder -> this.buildRequests(builder,
            ToggleMode.TOGGLE));
        this.registerLiteral(COMMAND_ON, true, new String[]{"tptoggle-on"}, builder -> this.buildRequests(builder,
            ToggleMode.ON));
        this.registerLiteral(COMMAND_OFF, true, new String[]{"tptoggle-off"}, builder -> this.buildRequests(builder,
            ToggleMode.OFF));

        this.registerRoot("ptp", true, new String[]{"ptp"},
            map -> {
                map.put(COMMAND_REQUEST, "request");
                map.put(COMMAND_INVITE, "invite");
                map.put(COMMAND_ACCEPT, "accept");
                map.put(COMMAND_DECLINE, "decline");
                map.put(COMMAND_TOGGLE, "toggle");
            },
            builder -> builder.description(PTPLang.COMMAND_PTP_DESC).permission(PTPPerms.COMMAND_ROOT)
        );
    }

    private void builderAccept(@NotNull LiteralNodeBuilder builder, boolean accept) {
        builder
            .playerOnly()
            .withArguments(Arguments.playerName(CommandArguments.PLAYER)
                .optional()
                .suggestions((reader, context) -> {
                    if (!(context.getSender() instanceof Player player)) return Collections.emptyList();

                    return this.module.getRequests(player).stream().map(TeleportRequest::getSender).filter(
                        Objects::nonNull).map(Player::getName).toList();
                })
            )
            .executes((context, arguments) -> this.acceptOrDecline(context, arguments, accept));
    }

    private void buildRequest(@NotNull LiteralNodeBuilder builder, @NotNull TeleportMode mode) {
        builder
            .playerOnly()
            .withArguments(Arguments.player(CommandArguments.PLAYER))
            .executes((context, arguments) -> this.sendRequest(context, arguments, mode));
    }

    private void buildRequests(@NotNull LiteralNodeBuilder builder, @NotNull ToggleMode mode) {
        TextLocale description = switch (mode) {
            case TOGGLE -> PTPLang.COMMAND_REQUESTS_TOGGLE_DESC;
            case ON -> PTPLang.COMMAND_REQUESTS_ON_DESC;
            case OFF -> PTPLang.COMMAND_REQUESTS_OFF_DESC;
        };

        builder
            .description(description)
            .permission(PTPPerms.COMMAND_REQUESTS)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER).permission(PTPPerms.COMMAND_REQUESTS_OTHERS)
                .optional())
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes((context, arguments) -> this.toggleRequests(context, arguments, mode));
    }

    private boolean acceptOrDecline(@NotNull CommandContext context, @NotNull ParsedArguments arguments,
                                    boolean accept) {
        Player player = context.getPlayerOrThrow();
        String from = arguments.contains(CommandArguments.PLAYER) ? arguments.getString(CommandArguments.PLAYER) : null;

        return accept ? this.module.accept(player, from) : this.module.decline(player, from);
    }

    private boolean sendRequest(@NotNull CommandContext context, @NotNull ParsedArguments arguments,
                                @NotNull TeleportMode mode) {
        Player player = context.getPlayerOrThrow();

        Player target = arguments.getPlayer(CommandArguments.PLAYER);
        if (player == target) {
            this.module.sendPrefixed(CoreLang.COMMAND_EXECUTION_NOT_YOURSELF, context.getSender()); // TODO Custom
            return false;
        }

        return module.sendRequest(player, target, mode);
    }

    private boolean toggleRequests(@NotNull CommandContext context, @NotNull ParsedArguments arguments,
                                   @NotNull ToggleMode mode) {
        return this.loadPlayerOrSenderWithDataAndRunInMainThread(context, arguments, this.module, this.userManager, (
                                                                                                                     user,
                                                                                                                     target) -> {
            boolean state = mode.apply(user.getPropertyOrDefault(PTPProperties.TELEPORT_REQUESTS));
            user.setProperty(PTPProperties.TELEPORT_REQUESTS, state);
            user.markDirty();

            if (context.getSender() != target) {
                this.module.sendPrefixed(PTPLang.REQUESTS_TOGGLE_FEEDBACK, context.getSender(), replacer -> replacer
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                    .with(SLPlaceholders.GENERIC_STATE, () -> CoreLang.STATE_ENABLED_DISALBED.get(state))
                );
            }

            if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                this.module.sendPrefixed(PTPLang.REQUESTS_TOGGLE_NOTIFY, target, replacer -> replacer
                    .with(SLPlaceholders.GENERIC_STATE, () -> CoreLang.STATE_ENABLED_DISALBED.get(state))
                );
            }
        });
    }
}
