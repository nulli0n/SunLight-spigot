package su.nightexpress.sunlight.module.chat.command;

import org.bukkit.permissions.Permission;
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
import su.nightexpress.sunlight.module.chat.core.ChatLang;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.core.ChatPerms;
import su.nightexpress.sunlight.module.chat.ChatProperties;
import su.nightexpress.sunlight.module.chat.spy.SpyType;
import su.nightexpress.sunlight.user.UserManager;
import su.nightexpress.sunlight.user.property.UserProperty;

public class SpyCommandProvider extends AbstractCommandProvider {

    private final ChatModule module;
    private final UserManager userManager;

    public SpyCommandProvider(@NotNull SunLightPlugin plugin, @NotNull ChatModule module, @NotNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.userManager = userManager;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral("logger", true, new String[]{"spylogger"}, builder -> builder
            .description(ChatLang.COMMAND_SPY_LOGGER_DESC)
            .permission(ChatPerms.COMMAND_SPY_LOGGER)
            .withArguments(
                CommandArguments.enumed(CommandArguments.TYPE, SpyType.class).localized(Lang.COMMAND_ARGUMENT_NAME_TYPE),
                Arguments.playerName(CommandArguments.PLAYER)
            )
            .executes((context, arguments) -> this.toggleLogger(context, arguments, ToggleMode.TOGGLE))
        );

        this.registerLiteral("chatspy-toggle", true, new String[]{"chatspy"}, builder -> this.builderMode(builder, SpyType.CHAT, ToggleMode.TOGGLE));
        this.registerLiteral("chatspy-on", true, new String[]{"chatspy-on"}, builder -> this.builderMode(builder, SpyType.CHAT, ToggleMode.ON));
        this.registerLiteral("chatspy-off", true, new String[]{"chatspy-off"}, builder -> this.builderMode(builder, SpyType.CHAT, ToggleMode.OFF));

        this.registerLiteral("commandspy-toggle", true, new String[]{"commandspy"}, builder -> this.builderMode(builder, SpyType.COMMAND, ToggleMode.TOGGLE));
        this.registerLiteral("commandspy-on", true, new String[]{"commandspy-on"}, builder -> this.builderMode(builder, SpyType.COMMAND, ToggleMode.ON));
        this.registerLiteral("commandspy-off", true, new String[]{"commandspy-off"}, builder -> this.builderMode(builder, SpyType.COMMAND, ToggleMode.OFF));

        this.registerLiteral("socialspy-toggle", true, new String[]{"socialspy"}, builder -> this.builderMode(builder, SpyType.SOCIAL, ToggleMode.TOGGLE));
        this.registerLiteral("socialspy-on", true, new String[]{"socialspy-on"}, builder -> this.builderMode(builder, SpyType.SOCIAL, ToggleMode.ON));
        this.registerLiteral("socialspy-off", true, new String[]{"socialspy-off"}, builder -> this.builderMode(builder, SpyType.SOCIAL, ToggleMode.OFF));
    }

    private void builderMode(@NotNull LiteralNodeBuilder builder, @NotNull SpyType spyType, @NotNull ToggleMode mode) {
        TextLocale description = switch (mode) {
            case TOGGLE -> ChatLang.COMMAND_SPY_MODE_TOGGLE_DESC;
            case ON -> ChatLang.COMMAND_SPY_MODE_ON_DESC;
            case OFF -> ChatLang.COMMAND_SPY_MODE_OFF_DESC;
        };

        Permission permission = switch (spyType) {
            case CHAT -> ChatPerms.COMMAND_SPY_CHAT;
            case COMMAND -> ChatPerms.COMMAND_SPY_COMMAND;
            case SOCIAL -> ChatPerms.COMMAND_SPY_SOCIAL;
        };

        Permission permissionOthers = switch (spyType) {
            case CHAT -> ChatPerms.COMMAND_SPY_CHAT_OTHERS;
            case COMMAND -> ChatPerms.COMMAND_SPY_COMMAND_OTHERS;
            case SOCIAL -> ChatPerms.COMMAND_SPY_SOCIAL_OTEHRS;
        };

        builder
            .description(description.text().replace(SLPlaceholders.GENERIC_TYPE, ChatLang.SPY_TYPE.getLocalized(spyType)))
            .permission(permission)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER).permission(permissionOthers).optional())
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes((context, arguments) -> this.toggleMode(context, arguments, spyType, mode));
    }

    private boolean toggleMode(@NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull SpyType spyType, @NotNull ToggleMode mode) {
        return this.loadPlayerOrSenderWithDataAndRunInMainThread(context, arguments, this.module, this.userManager, (user, target) -> {
            UserProperty<Boolean> property = ChatProperties.getSpyInfoProperty(spyType);
            boolean state = mode.apply(user.getPropertyOrDefault(property));

            user.setProperty(property, state);
            user.markDirty();

            if (context.getSender() != target) {
                this.module.sendPrefixed(ChatLang.SPY_MODE_TOGGLE_FEEDBACK, context.getSender(), replacer -> replacer
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                    .with(SLPlaceholders.GENERIC_TYPE, () -> ChatLang.SPY_TYPE.getLocalized(spyType))
                    .with(SLPlaceholders.GENERIC_STATE, () -> CoreLang.STATE_ENABLED_DISALBED.get(state))
                );
            }

            if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                this.module.sendPrefixed(ChatLang.SPY_MODE_TOGGLE_NOTIFY, target, replacer -> replacer
                    .with(SLPlaceholders.GENERIC_TYPE, () -> ChatLang.SPY_TYPE.getLocalized(spyType))
                    .with(SLPlaceholders.GENERIC_STATE, () -> CoreLang.STATE_ENABLED_DISALBED.get(state))
                );
            }
        });
    }

    private boolean toggleLogger(@NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull ToggleMode mode) {
        SpyType spyType = arguments.get(CommandArguments.TYPE, SpyType.class);

        return this.loadPlayerOrSenderWithDataAndRunInMainThread(context, arguments, this.module, this.userManager, (user, target) -> {
            UserProperty<Boolean> property = ChatProperties.getSpyLogProperty(spyType);
            boolean state = mode.apply(user.getPropertyOrDefault(property));

            user.setProperty(property, state);
            user.markDirty();

            this.module.sendPrefixed(ChatLang.SPY_LOGGER_TOGGLE_FEEDBACK, context.getSender(), replacer -> replacer
                .with(CommonPlaceholders.PLAYER.resolver(target))
                .with(SLPlaceholders.GENERIC_TYPE, () -> ChatLang.SPY_TYPE.getLocalized(spyType))
                .with(SLPlaceholders.GENERIC_STATE, () -> CoreLang.STATE_ENABLED_DISALBED.get(state))
            );
        });
    }
}
