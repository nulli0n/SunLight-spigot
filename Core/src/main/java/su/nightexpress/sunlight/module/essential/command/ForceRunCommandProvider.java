package su.nightexpress.sunlight.module.essential.command;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.essential.EssentialModule;
import su.nightexpress.sunlight.module.essential.EssentialPerms;

import static su.nightexpress.nightcore.util.Placeholders.PLAYER_DISPLAY_NAME;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.GENERIC_COMMAND;

public class ForceRunCommandProvider extends AbstractCommandProvider {

    private static final Permission PERMISSION        = EssentialPerms.COMMAND.permission("forcerun");
    private static final Permission PERMISSION_BYPASS = EssentialPerms.COMMAND.permission("forcerun.bypass");

    private static final TextLocale DESCRIPTION = LangEntry.builder("Command.ForceRun.Description").text("Force player to execute a command.");

    private static final MessageLocale MESSAGE_FORCED_FEEDBACK = LangEntry.builder("Command.ForceRun.Forced.Feedback").chatMessage(
        GRAY.wrap("You have forced " + WHITE.wrap(PLAYER_DISPLAY_NAME) + " to execute: " + SOFT_YELLOW.wrap(GENERIC_COMMAND))
    );

    private static final MessageLocale MESSAGE_IMMUNITY = LangEntry.builder("Command.ForceRun.Immunity").chatMessage(
        GRAY.wrap("Player " + WHITE.wrap(PLAYER_DISPLAY_NAME) + " is immune to this command.")
    );

    private final EssentialModule module;

    public ForceRunCommandProvider(@NotNull SunLightPlugin plugin, @NotNull EssentialModule module) {
        super(plugin);
        this.module = module;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral("forcerun", true, new String[]{"forcerun"}, builder -> builder
            .description(DESCRIPTION)
            .permission(PERMISSION)
            .withArguments(
                Arguments.player(CommandArguments.PLAYER),
                Arguments.greedyString(CommandArguments.TEXT).localized(Lang.COMMAND_ARGUMENT_NAME_COMMAND)
            )
            .executes(this::forceRun)
        );
    }

    private boolean forceRun(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = arguments.getPlayer(CommandArguments.PLAYER);
        if (context.getSender() == target) {
            this.module.sendPrefixed(CoreLang.COMMAND_EXECUTION_NOT_YOURSELF, context.getSender()); // TODO Custom
            return false;
        }

        if (context.getSender() instanceof Player && target.hasPermission(PERMISSION_BYPASS)) {
            this.module.sendPrefixed(MESSAGE_IMMUNITY, context.getSender(), replacer -> replacer.with(CommonPlaceholders.PLAYER.resolver(target)));
            return false;
        }

        String commandToRun = arguments.getString(CommandArguments.TEXT);

        target.performCommand(commandToRun);

        this.module.sendPrefixed(MESSAGE_FORCED_FEEDBACK, context.getSender(), replacer -> replacer
            .with(GENERIC_COMMAND, () -> commandToRun)
            .with(CommonPlaceholders.PLAYER.resolver(target))
        );
        return true;
    }
}
