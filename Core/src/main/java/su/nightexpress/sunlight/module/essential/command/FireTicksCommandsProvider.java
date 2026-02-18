package su.nightexpress.sunlight.module.essential.command;

import org.bukkit.Sound;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.nightcore.util.time.TimeFormats;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.essential.EssentialModule;
import su.nightexpress.sunlight.module.essential.EssentialPerms;
import su.nightexpress.sunlight.user.UserManager;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.GENERIC_TIME;
import static su.nightexpress.sunlight.SLPlaceholders.PLAYER_DISPLAY_NAME;

public class FireTicksCommandsProvider extends AbstractCommandProvider {

    private static final String COMMAND_IGNITE     = "ignite";
    private static final String COMMAND_EXTINGUISH = "extinguish";

    private static final Permission PERMISSION_IGNITE            = EssentialPerms.COMMAND.permission("ignite");
    private static final Permission PERMISSION_EXTINGUISH        = EssentialPerms.COMMAND.permission("extinguish");
    private static final Permission PERMISSION_EXTINGUISH_OTHERS = EssentialPerms.COMMAND.permission("extinguish.others");

    private static final TextLocale DESCRIPTION_IGNITE     = LangEntry.builder("Command.Ignite.Desc").text("Ignite a player.");
    private static final TextLocale DESCRIPTION_EXTINGUISH = LangEntry.builder("Command.Extinguish.Desc").text("Extinguish a player.");

    private static final MessageLocale MESSAGE_EXTINGUISH_FEEDBACK = LangEntry.builder("Command.Extinguish.Feedback").chatMessage(
        Sound.BLOCK_FIRE_EXTINGUISH,
        GRAY.wrap("You have extinguished " + WHITE.wrap(PLAYER_DISPLAY_NAME) + ".")
    );

    private static final MessageLocale MESSAGE_EXTINGUISH_NOTIFY = LangEntry.builder("Command.Extinguish.Notify").chatMessage(
        Sound.BLOCK_FIRE_EXTINGUISH,
        GRAY.wrap("You have been extinguished.")
    );

    private static final MessageLocale MESSAGE_IGNITE_FEEDBACK = LangEntry.builder("Command.Ignite.Feedback").chatMessage(
        Sound.ENTITY_PLAYER_HURT_ON_FIRE,
        GRAY.wrap("You have ignited " + WHITE.wrap(PLAYER_DISPLAY_NAME) + " for " + SOFT_YELLOW.wrap(GENERIC_TIME))
    );

    private static final MessageLocale MESSAGE_IGNITE_NOTIFY = LangEntry.builder("Command.Ignite.Notify").chatMessage(
        Sound.ENTITY_PLAYER_HURT_ON_FIRE,
        GRAY.wrap("You have been ignited for " + SOFT_RED.wrap(GENERIC_TIME))
    );

    private final EssentialModule module;
    private final UserManager userManager;

    public FireTicksCommandsProvider(@NotNull SunLightPlugin plugin, @NotNull EssentialModule module, @NotNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.userManager = userManager;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral(COMMAND_IGNITE, true, new String[]{"ignite"}, builder -> builder
            .description(DESCRIPTION_IGNITE)
            .permission(PERMISSION_IGNITE)
            .withArguments(
                Arguments.playerName(CommandArguments.PLAYER),
                Arguments.integer(CommandArguments.TIME, 1, 86400)
                    .localized(Lang.COMMAND_ARGUMENT_NAME_TIME.text())
                    .suggestions((reader, context) -> Lists.newList("5", "10", "30", "60"))
            )
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::ignite)
        );

        this.registerLiteral(COMMAND_EXTINGUISH, true, new String[]{"extinguish"}, builder -> builder
            .description(DESCRIPTION_EXTINGUISH)
            .permission(PERMISSION_EXTINGUISH)
            .withArguments(
                Arguments.playerName(CommandArguments.PLAYER).permission(PERMISSION_EXTINGUISH_OTHERS).optional()
            )
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::extinguish)
        );
    }

    private boolean ignite(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return this.loadPlayerOrSenderAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            int seconds = arguments.getInt(CommandArguments.TIME);
            long ticks = TimeUtil.secondsToTicks(seconds);

            target.setFireTicks((int) ticks);

            if (context.getSender() != target) {
                this.module.sendPrefixed(MESSAGE_IGNITE_FEEDBACK, context.getSender(), builder -> builder
                    .with(SLPlaceholders.GENERIC_TIME, () -> TimeFormats.toLiteral(seconds * 1000L))
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                );
            }

            if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                this.module.sendPrefixed(MESSAGE_IGNITE_NOTIFY, target, builder -> builder
                    .with(SLPlaceholders.GENERIC_TIME, () -> TimeFormats.toLiteral(seconds * 1000L))
                );
            }
        });
    }

    private boolean extinguish(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return this.loadPlayerOrSenderAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            target.setFireTicks(0);

            if (context.getSender() != target) {
                this.module.sendPrefixed(MESSAGE_EXTINGUISH_FEEDBACK, context.getSender(), builder -> builder.with(CommonPlaceholders.PLAYER.resolver(target)));
            }

            if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                this.module.sendPrefixed(MESSAGE_EXTINGUISH_NOTIFY, target);
            }
        });
    }
}
