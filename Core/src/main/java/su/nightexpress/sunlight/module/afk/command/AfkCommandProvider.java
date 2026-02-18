package su.nightexpress.sunlight.module.afk.command;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.builder.LiteralNodeBuilder;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.mode.ToggleMode;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.module.afk.AfkModule;
import su.nightexpress.sunlight.module.afk.core.AfkLang;
import su.nightexpress.sunlight.module.afk.core.AfkPerms;

public class AfkCommandProvider extends AbstractCommandProvider {

    private static final String COMMAND_TOGGLE = "toggle";
    private static final String COMMAND_ON     = "on";
    private static final String COMMAND_OFF    = "off";

    private final AfkModule module;

    public AfkCommandProvider(@NotNull SunLightPlugin plugin, @NotNull AfkModule module) {
        super(plugin);
        this.module = module;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral(COMMAND_TOGGLE, true, new String[]{"afk"}, builder -> this.buildCommand(builder, ToggleMode.TOGGLE));
        this.registerLiteral(COMMAND_ON, false, new String[]{"afk-on"}, builder -> this.buildCommand(builder, ToggleMode.ON));
        this.registerLiteral(COMMAND_OFF, false, new String[]{"afk-off"}, builder -> this.buildCommand(builder, ToggleMode.OFF));

        /*this.registerRoot("mode", false, new String[]{"afkmode"},
            Map.of(
                COMMAND_OFF, "off",
                COMMAND_ON, "on",
                COMMAND_TOGGLE, "toggle"
            ),
            builder -> builder.description(AfkLang.COMMAND_AFK_ROOT_DESC).permission(AfkPerms.COMMAND_AFK)
        );*/
    }

    private void buildCommand(@NotNull LiteralNodeBuilder builder, @NotNull ToggleMode mode) {
        TextLocale description = switch (mode) {
            case ON -> AfkLang.COMMAND_AFK_ON_DESC;
            case OFF -> AfkLang.COMMAND_AFK_OFF_DESC;
            case TOGGLE -> AfkLang.COMMAND_AFK_TOGGLE_DESC;
        };

        builder
            .description(description)
            .permission(AfkPerms.COMMAND_AFK)
            .withArguments(Arguments.player(CommandArguments.PLAYER).optional().permission(AfkPerms.COMMAND_AFK_OTHERS))
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes((context, arguments) -> this.toggleAfkMode(context, arguments, mode));
    }

    private boolean toggleAfkMode(@NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull ToggleMode mode) {
        return this.runForOnlinePlayerOrSender(context, arguments, this.module, target -> {
            boolean state = mode.apply(this.module.isAfk(target));
            if (state) {
                this.module.enterAfk(target, false);
            }
            else {
                this.module.exitAfk(target, false);
            }

            if (context.getSender() != target) {
                this.module.sendPrefixed(AfkLang.AFK_TOGGLE_FEEDBACK, context.getSender(), builder -> builder
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                );
            }

            return true;
        });
    }
}
