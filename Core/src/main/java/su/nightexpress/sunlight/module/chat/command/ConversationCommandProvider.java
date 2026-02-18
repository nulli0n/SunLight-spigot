package su.nightexpress.sunlight.module.chat.command;

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
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.chat.*;
import su.nightexpress.sunlight.module.chat.core.ChatLang;
import su.nightexpress.sunlight.module.chat.core.ChatPerms;
import su.nightexpress.sunlight.user.UserManager;

import java.util.Map;
import java.util.UUID;

public class ConversationCommandProvider extends AbstractCommandProvider {

    private static final String COMMAND_MESSAGE = "message";
    private static final String COMMAND_REPLY   = "reply";
    private static final String COMMAND_TOGGLE  = "toggle";
    private static final String COMMAND_ON      = "on";
    private static final String COMMAND_OFF     = "off";

    private final ChatModule  module;
    private final UserManager userManager;

    public ConversationCommandProvider(@NotNull SunLightPlugin plugin, @NotNull ChatModule module, @NotNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.userManager = userManager;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral(COMMAND_MESSAGE, true, new String[]{ChatDefaults.DEFAULT_PM_ALIAS, "pm", "whisper", "w", "tell"}, builder -> builder
            .playerOnly()
            .description(ChatLang.COMMAND_TELL_DESC)
            .permission(ChatPerms.COMMAND_TELL)
            .withArguments(
                Arguments.player(CommandArguments.PLAYER),
                Arguments.greedyString(CommandArguments.TEXT).localized(Lang.COMMAND_ARGUMENT_NAME_TEXT)
            )
            .executes(this::sendConversationMessage)
        );

        this.registerLiteral(COMMAND_REPLY, true, new String[]{ChatDefaults.DEFAULT_REPLY_ALIAS, "r"}, builder -> builder
            .playerOnly()
            .description(ChatLang.COMMAND_REPLY_DESC)
            .permission(ChatPerms.COMMAND_REPLY)
            .withArguments(Arguments.greedyString(CommandArguments.TEXT).localized(Lang.COMMAND_ARGUMENT_NAME_TEXT))
            .executes(this::replyToConversation)
        );

        this.registerLiteral(COMMAND_TOGGLE, false, new String[]{"togglepm", "pmtoggle"}, builder -> {
            this.buildToggleCommand(builder, ChatLang.COMMAND_CONVERSATIONS_TOGGLE_DESC, ToggleMode.TOGGLE);
        });

        this.registerLiteral(COMMAND_ON, false, new String[]{"allowpm"}, builder -> {
            this.buildToggleCommand(builder, ChatLang.COMMAND_CONVERSATIONS_ON_DESC, ToggleMode.ON);
        });

        this.registerLiteral(COMMAND_OFF, false, new String[]{"denypm"}, builder -> {
            this.buildToggleCommand(builder, ChatLang.COMMAND_CONVERSATIONS_OFF_DESC, ToggleMode.OFF);
        });

        this.registerRoot("Conversations", true, new String[]{"conversations", "dm"},
            Map.of(
                COMMAND_MESSAGE, "send",
                COMMAND_REPLY, "reply",
                COMMAND_TOGGLE, "toggle",
                COMMAND_ON, "on",
                COMMAND_OFF, "off"
            ),
            builder -> builder.description(ChatLang.COMMAND_CONVERSATIONS_ROOT_DESC).permission(ChatPerms.COMMAND_CONVERSATIONS_ROOT)
        );
    }

    private void buildToggleCommand(@NotNull LiteralNodeBuilder builder, @NotNull TextLocale description, @NotNull ToggleMode mode) {
        builder
            .description(description)
            .permission(ChatPerms.COMMAND_CONVERSATIONS_TOGGLE)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER).permission(ChatPerms.COMMAND_CONVERSATIONS_OTHERS).optional())
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes((context, arguments) -> this.toggleConversations(context, arguments, mode));
    }

    private boolean sendConversationMessage(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        Player target = arguments.getPlayer(CommandArguments.PLAYER);
        String message = arguments.getString(CommandArguments.TEXT);
        return this.module.sendPrivateMessage(player, target, message);
    }

    private boolean replyToConversation(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();

        UUID targetId = this.module.getChatCache(player).getLastConversationWith();
        if (targetId == null) {
            context.send(ChatLang.COMMAND_REPLY_ERROR_EMPTY);
            return false;
        }

        Player target = this.plugin.getServer().getPlayer(targetId);
        if (target == null) {
            context.errorBadPlayer();
            return false;
        }

        String message = arguments.getString(CommandArguments.TEXT);

        return this.module.sendPrivateMessage(player, target, message);
    }

    private boolean toggleConversations(@NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull ToggleMode mode) {
        return this.loadPlayerOrSenderWithDataAndRunInMainThread(context, arguments, this.module, this.userManager, (user, target) -> {
            boolean state = mode.apply(user.getPropertyOrDefault(ChatProperties.CONVERSATIONS));

            user.setProperty(ChatProperties.CONVERSATIONS, state);
            user.markDirty();

            if (context.getSender() != target) {
                this.module.sendPrefixed(ChatLang.CONVERSATIONS_TOGGLE_FEEDBACK, context.getSender(), builder -> builder
                    .with(SLPlaceholders.GENERIC_STATE, () -> CoreLang.STATE_ENABLED_DISALBED.get(state))
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                );
            }

            if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                this.module.sendPrefixed(ChatLang.CONVERSATIONS_TOGGLE_NOTIFY, target, builder -> builder
                    .with(SLPlaceholders.GENERIC_STATE, () -> CoreLang.STATE_ENABLED_DISALBED.get(state))
                );
            }
        });
    }
}
