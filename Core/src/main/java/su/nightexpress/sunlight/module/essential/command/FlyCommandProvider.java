package su.nightexpress.sunlight.module.essential.command;

import org.bukkit.Sound;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.builder.LiteralNodeBuilder;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.mode.ToggleMode;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.module.essential.EssentialModule;
import su.nightexpress.sunlight.module.essential.EssentialPerms;
import su.nightexpress.sunlight.user.UserManager;

import java.util.Map;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.GRAY;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.WHITE;
import static su.nightexpress.sunlight.SLPlaceholders.GENERIC_STATE;
import static su.nightexpress.sunlight.SLPlaceholders.PLAYER_DISPLAY_NAME;

public class FlyCommandProvider extends AbstractCommandProvider {

    private static final String COMMAND_TOGGLE = "toggle";
    private static final String COMMAND_OFF    = "off";
    private static final String COMMAND_ON     = "on";

    // TODO world restrictions, tempfly etc.

    private static final Permission PERMISSION        = EssentialPerms.COMMAND.permission("fly");
    private static final Permission PERMISSION_OTHERS = EssentialPerms.COMMAND.permission("fly.others");
    private static final Permission PERMISSION_ROOT   = EssentialPerms.COMMAND.permission("fly.root");

    private static final TextLocale DESCRIPTION_ROOT   = LangEntry.builder("Command.Fly.Root.Desc").text("Fly commands.");
    private static final TextLocale DESCRIPTION_TOGGLE = LangEntry.builder("Command.Fly.Toggle.Desc").text("Toggle fly.");
    private static final TextLocale DESCRIPTION_ON     = LangEntry.builder("Command.Fly.On.Desc").text("Enable fly.");
    private static final TextLocale DESCRIPTION_OFF    = LangEntry.builder("Command.Fly.Off.Desc").text("Disable fly.");

    private static final MessageLocale MESSAGE_TOGGLE_FEEDBACK = LangEntry.builder("Command.Fly.Target").chatMessage(
        Sound.ITEM_FIRECHARGE_USE,
        GRAY.wrap("You have set " + WHITE.wrap(PLAYER_DISPLAY_NAME) + "'s fly on " + WHITE.wrap(GENERIC_STATE) + ".")
    );

    private static final MessageLocale MESSAGE_TOGGLE_NOTIFY = LangEntry.builder("Command.Fly.Notify").chatMessage(
        Sound.ITEM_FIRECHARGE_USE,
        GRAY.wrap("Your fly has been set on " + WHITE.wrap(GENERIC_STATE) + ".")
    );

    private final EssentialModule module;
    private final UserManager userManager;

    public FlyCommandProvider(@NotNull SunLightPlugin plugin, @NotNull EssentialModule module, @NotNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.userManager = userManager;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral(COMMAND_TOGGLE, true, new String[]{"fly", "togglefly"}, builder -> this.buildCommand(builder, DESCRIPTION_TOGGLE, ToggleMode.TOGGLE));
        this.registerLiteral(COMMAND_ON, true, new String[]{"fly-on"}, builder -> this.buildCommand(builder, DESCRIPTION_ON, ToggleMode.ON));
        this.registerLiteral(COMMAND_OFF, true, new String[]{"fly-off"}, builder -> this.buildCommand(builder, DESCRIPTION_OFF, ToggleMode.OFF));

        this.registerRoot("Fly", false, new String[]{"flymode"},
            Map.of(
                COMMAND_TOGGLE, "toggle",
                COMMAND_ON, "on",
                COMMAND_OFF, "off"
            ),
            builder -> builder.description(DESCRIPTION_ROOT).permission(PERMISSION_ROOT)
        );
    }

    private void buildCommand(@NotNull LiteralNodeBuilder builder, @NotNull TextLocale description, @NotNull ToggleMode mode) {
        builder
            .description(description)
            .permission(PERMISSION)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER).permission(PERMISSION_OTHERS).optional())
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes((context, arguments) -> this.toggleFly(context, arguments, mode));
    }

    private boolean toggleFly(@NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull ToggleMode mode) {
        return this.loadPlayerOrSenderAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            target.setAllowFlight(mode.apply(target.getAllowFlight()));

            if (context.getSender() != target) {
                this.module.sendPrefixed(MESSAGE_TOGGLE_FEEDBACK, context.getSender(), builder -> builder
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                    .with(SLPlaceholders.GENERIC_STATE, () -> CoreLang.STATE_ENABLED_DISALBED.get(target.getAllowFlight()))
                );
            }

            if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                this.module.sendPrefixed(MESSAGE_TOGGLE_NOTIFY, target, builder -> builder
                    .with(SLPlaceholders.GENERIC_STATE, () -> CoreLang.STATE_ENABLED_DISALBED.get(target.getAllowFlight()))
                );
            }
        });
    }
}
