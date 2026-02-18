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

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.GENERIC_TEXT;
import static su.nightexpress.sunlight.SLPlaceholders.PLAYER_DISPLAY_NAME;

public class ForceSayCommandProvider extends AbstractCommandProvider {

    private static final Permission PERMISSION        = EssentialPerms.COMMAND.permission("forcesay");
    private static final Permission PERMISSION_BYPASS = EssentialPerms.COMMAND.permission("forcesay.bypass");

    private static final TextLocale DESCRIPTION = LangEntry.builder("Command.ForceSay.Description").text("Force player to say something.");

    private static final MessageLocale MESSAGE_FORCED_FEEDBACK = LangEntry.builder("Command.ForceSay.Forced.Feedback").chatMessage(
        GRAY.wrap("You have forced " + WHITE.wrap(PLAYER_DISPLAY_NAME) + " to say: " + SOFT_YELLOW.wrap(GENERIC_TEXT))
    );

    private static final MessageLocale MESSAGE_IMMUNITY = LangEntry.builder("Command.ForceSay.Immunity").chatMessage(
        GRAY.wrap("Player " + WHITE.wrap(PLAYER_DISPLAY_NAME) + " is immune to this command.")
    );

    private final EssentialModule module;

    public ForceSayCommandProvider(@NotNull SunLightPlugin plugin, @NotNull EssentialModule module) {
        super(plugin);
        this.module = module;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral("forcesay", true, new String[]{"forcesay"}, builder -> builder
            .description(DESCRIPTION)
            .permission(PERMISSION)
            .withArguments(
                Arguments.player(CommandArguments.PLAYER),
                Arguments.greedyString(CommandArguments.TEXT).localized(Lang.COMMAND_ARGUMENT_NAME_TEXT)
            )
            .executes(this::forceChat)
        );
    }

    private boolean forceChat(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = arguments.getPlayer(CommandArguments.PLAYER);
        if (context.getSender() == target) {
            this.module.sendPrefixed(CoreLang.COMMAND_EXECUTION_NOT_YOURSELF, context.getSender()); // TODO Custom
            return false;
        }

        if (context.getSender() instanceof Player && target.hasPermission(PERMISSION_BYPASS)) {
            this.module.sendPrefixed(MESSAGE_IMMUNITY, context.getSender(), builder -> builder.with(CommonPlaceholders.PLAYER.resolver(target)));
            return false;
        }

        String textToSay = arguments.getString(CommandArguments.TEXT);

        target.chat(textToSay);

        this.module.sendPrefixed(MESSAGE_FORCED_FEEDBACK, context.getSender(), builder -> builder
            .with(GENERIC_TEXT, () -> textToSay)
            .with(CommonPlaceholders.PLAYER.resolver(target))
        );
        return true;
    }
}
