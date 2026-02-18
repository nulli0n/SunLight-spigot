package su.nightexpress.sunlight.module.scoreboard.command;

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
import su.nightexpress.sunlight.module.scoreboard.ScoreboardModule;
import su.nightexpress.sunlight.module.scoreboard.ScoreboardProperties;
import su.nightexpress.sunlight.module.scoreboard.config.SBLang;
import su.nightexpress.sunlight.module.scoreboard.config.SBPerms;
import su.nightexpress.sunlight.user.UserManager;

public class ScoreboardCommand extends AbstractCommandProvider {

    private static final String COMMAND_OFF    = "off";
    private static final String COMMAND_ON     = "on";
    private static final String COMMAND_TOGGLE = "toggle";

    private final ScoreboardModule module;
    private final UserManager userManager;

    public ScoreboardCommand(@NotNull SunLightPlugin plugin, @NotNull ScoreboardModule module, @NotNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.userManager = userManager;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral(COMMAND_TOGGLE, true, new String[]{"scoreboard", "board", "sb"}, builder -> this.buildToggleCommand(builder, ToggleMode.TOGGLE));
        this.registerLiteral(COMMAND_ON, true, new String[]{"scoreboard-on", "board-on", "sb-on"}, builder -> this.buildToggleCommand(builder, ToggleMode.ON));
        this.registerLiteral(COMMAND_OFF, true, new String[]{"scoreboard-off", "board-off", "sb-off"}, builder -> this.buildToggleCommand(builder, ToggleMode.OFF));
    }

    private void buildToggleCommand(@NotNull LiteralNodeBuilder builder, @NotNull ToggleMode mode) {
        TextLocale description = switch (mode) {
            case TOGGLE -> SBLang.COMMAND_SCOREBOARD_TOGGLE_DESC;
            case ON -> SBLang.COMMAND_SCOREBOARD_ON_DESC;
            case OFF -> SBLang.COMMAND_SCOREBOARD_OFF_DESC;
        };

        builder
            .description(description)
            .permission(SBPerms.COMMAND_SCOREBOARD)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER).permission(SBPerms.COMMAND_SCOREBOARD_OTHERS).optional())
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes((context, arguments) -> this.toggleScoreboard(context, arguments, mode));
    }

    private boolean toggleScoreboard(@NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull ToggleMode mode) {
        this.loadPlayerOrSenderWithDataAndRunInMainThread(context, arguments, this.module, this.userManager, (user, target) -> {
            boolean state = mode.apply(user.getPropertyOrDefault(ScoreboardProperties.SCOREBOARD));

            if (state) {
                this.module.addBoard(target);
            }
            else {
                this.module.removeBoard(target);
            }

            user.setProperty(ScoreboardProperties.SCOREBOARD, state);
            user.markDirty();

            if (context.getSender() != target) {
                this.module.sendPrefixed(SBLang.COMMAND_SCOREBOARD_TARGET, context.getSender(), builder -> builder
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                    .with(SLPlaceholders.GENERIC_STATE, () -> CoreLang.STATE_ENABLED_DISALBED.get(state))
                );
            }

            if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                this.module.sendPrefixed(SBLang.COMMAND_SCOREBOARD_NOTIFY, target, builder -> builder
                    .with(SLPlaceholders.GENERIC_STATE, () -> CoreLang.STATE_ENABLED_DISALBED.get(state))
                );
            }
        });

        return true;
    }
}
