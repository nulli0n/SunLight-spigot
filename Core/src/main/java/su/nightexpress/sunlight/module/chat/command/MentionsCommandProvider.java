package su.nightexpress.sunlight.module.chat.command;

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
import su.nightexpress.sunlight.module.chat.core.ChatLang;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.core.ChatPerms;
import su.nightexpress.sunlight.module.chat.ChatProperties;
import su.nightexpress.sunlight.user.UserManager;

import java.util.Map;

public class MentionsCommandProvider extends AbstractCommandProvider {

    private static final String COMMAND_OFF    = "off";
    private static final String COMMAND_ON     = "on";
    private static final String COMMAND_TOGGLE = "toggle";

    private final ChatModule  module;
    private final UserManager userManager;

    public MentionsCommandProvider(@NotNull SunLightPlugin plugin, @NotNull ChatModule module, @NotNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.userManager = userManager;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral(COMMAND_TOGGLE, true, new String[]{"mentions-toggle"}, builder -> {
            this.buildToggleCommand(builder, ChatLang.COMMAND_MENTIONS_TOGGLE_DESC, ToggleMode.TOGGLE);
        });

        this.registerLiteral(COMMAND_ON, true, new String[]{"mentions-on"}, builder -> {
            this.buildToggleCommand(builder, ChatLang.COMMAND_MENTIONS_ON_DESC, ToggleMode.ON);
        });

        this.registerLiteral(COMMAND_OFF, true, new String[]{"mentions-off"}, builder -> {
            this.buildToggleCommand(builder, ChatLang.COMMAND_MENTIONS_OFF_DESC, ToggleMode.OFF);
        });

        this.registerRoot("mentions", true, new String[]{"mentions"},
            Map.of(
                COMMAND_OFF, "off",
                COMMAND_ON, "on",
                COMMAND_TOGGLE, "toggle"
            ),
            builder -> builder.description(ChatLang.COMMAND_MENTIONS_ROOT_DESC).permission(
                ChatPerms.COMMAND_MENTIONS_ROOT)
        );
    }

    private void buildToggleCommand(@NotNull LiteralNodeBuilder builder, @NotNull TextLocale description,
                                    @NotNull ToggleMode mode) {
        builder
            .description(description)
            .permission(ChatPerms.COMMAND_MENTIONS_TOGGLE)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER).permission(
                ChatPerms.COMMAND_MENTIONS_TOGGLE_OTHERS)
                .optional())
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes((context, arguments) -> this.toggleMentions(context, arguments, mode));
    }

    private boolean toggleMentions(@NotNull CommandContext context, @NotNull ParsedArguments arguments,
                                   @NotNull ToggleMode mode) {
        return this.loadPlayerOrSenderWithDataAndRunInMainThread(context, arguments, this.module, this.userManager, (
                                                                                                                     user,
                                                                                                                     target) -> {
            boolean state = mode.apply(user.getPropertyOrDefault(ChatProperties.MENTIONS));

            user.setProperty(ChatProperties.MENTIONS, state);
            user.markDirty();

            if (context.getSender() != target) {
                this.module.sendPrefixed(ChatLang.MENTIONS_TOGGLE_FEEDBACK, context.getSender(), builder -> builder
                    .with(SLPlaceholders.GENERIC_STATE, () -> CoreLang.STATE_ENABLED_DISALBED.get(state))
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                );
            }

            if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                this.module.sendPrefixed(ChatLang.MENTIONS_TOGGLE_NOTIFY, target, builder -> builder
                    .with(SLPlaceholders.GENERIC_STATE, () -> CoreLang.STATE_ENABLED_DISALBED.get(state))
                );
            }
        });
    }
}

