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
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.mode.ModifyMode;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.module.essential.EssentialModule;
import su.nightexpress.sunlight.module.essential.EssentialPerms;
import su.nightexpress.sunlight.user.UserManager;

import java.util.Map;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.*;

public class ExperienceCommandsProvider extends AbstractCommandProvider {

    private static final String COMMAND_LEVEL_ADD    = "level_add";
    private static final String COMMAND_LEVEL_SET    = "level_set";
    private static final String COMMAND_LEVEL_REMOVE = "level_remove";

    private static final String COMMAND_XP_ADD    = "xp_add";
    private static final String COMMAND_XP_SET    = "xp_set";
    private static final String COMMAND_XP_REMOVE = "xp_remove";

    private static final Permission PERMISSION_LEVEL        = EssentialPerms.COMMAND.permission("experience.level");
    private static final Permission PERMISSION_LEVEL_OTHERS = EssentialPerms.COMMAND.permission("experience.level.others");

    private static final Permission PERMISSION_XP        = EssentialPerms.COMMAND.permission("experience.xp");
    private static final Permission PERMISSION_XP_OTHERS = EssentialPerms.COMMAND.permission("experience.xp.others");

    private static final Permission PERMISSION_ROOT = EssentialPerms.COMMAND.permission("experience.root");

    private static final TextLocale DESCRIPTION_ROOT = LangEntry.builder("Command.Experience.Root.Desc").text("Experience commands.");

    private static final TextLocale DESCRIPTION_LEVEL_ADD    = LangEntry.builder("Command.Experience.AddLevel.Desc").text("Add XP levels.");
    private static final TextLocale DESCRIPTION_LEVEL_SET    = LangEntry.builder("Command.Experience.SetLevel.Desc").text("Set XP levels.");
    private static final TextLocale DESCRIPTION_LEVEL_REMOVE = LangEntry.builder("Command.Experience.RemoveLevel.Desc").text("Remove XP levels.");

    private static final TextLocale DESCRIPTION_XP_ADD    = LangEntry.builder("Command.Experience.AddXP.Desc").text("Add experience points.");
    private static final TextLocale DESCRIPTION_XP_SET    = LangEntry.builder("Command.Experience.SetXP.Desc").text("Set experience points.");
    private static final TextLocale DESCRIPTION_XP_REMOVE = LangEntry.builder("Command.Experience.RemoveXP.Desc").text("Remove experience points.");

    private static final MessageLocale MESSAGE_XP_SET_FEEDBACK = LangEntry.builder("Command.Experience.SetXP.Feedback").chatMessage(
        Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
        GRAY.wrap("You have set " + WHITE.wrap(PLAYER_DISPLAY_NAME) + "'s total XP to " + SOFT_YELLOW.wrap(GENERIC_VALUE) + " (" + YELLOW.wrap(GENERIC_OLD_VALUE) + DARK_GRAY.wrap(" → ") + GREEN.wrap(GENERIC_NEW_VALUE) + ")")
    );

    private static final MessageLocale MESSAGE_XP_SET_NOTIFY = LangEntry.builder("Command.Experience.SetXP.Notify").chatMessage(
        Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
        GRAY.wrap("Your total XP has been set to " + SOFT_YELLOW.wrap(GENERIC_VALUE) + ".")
    );

    private static final MessageLocale MESSAGE_XP_ADD_FEEDBACK = LangEntry.builder("Command.Experience.AddXP.Feedback").chatMessage(
        Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
        GRAY.wrap("You have added " + SOFT_GREEN.wrap(GENERIC_VALUE) + " XP to " + WHITE.wrap(PLAYER_DISPLAY_NAME) + " (" + YELLOW.wrap(GENERIC_OLD_VALUE) + DARK_GRAY.wrap(" → ") + GREEN.wrap(GENERIC_NEW_VALUE) + ")")
    );

    private static final MessageLocale MESSAGE_XP_ADD_NOTIFY = LangEntry.builder("Command.Experience.AddXP.Notify").chatMessage(
        Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
        GRAY.wrap("You have been given " + SOFT_GREEN.wrap(GENERIC_VALUE) + " XP.")
    );

    private static final MessageLocale MESSAGE_XP_REMOVE_FEEDBACK = LangEntry.builder("Command.Experience.RemoveXP.Feedback").chatMessage(
        Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
        GRAY.wrap("You have took " + SOFT_RED.wrap(GENERIC_VALUE) + " XP from " + WHITE.wrap(PLAYER_DISPLAY_NAME) + " (" + YELLOW.wrap(GENERIC_OLD_VALUE) + DARK_GRAY.wrap(" → ") + GREEN.wrap(GENERIC_NEW_VALUE) + ")")
    );

    private static final MessageLocale MESSAGE_XP_REMOVE_NOTIFY = LangEntry.builder("Command.Experience.RemoveXP.Notify").chatMessage(
        Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
        GRAY.wrap("You have been taken " + SOFT_RED.wrap(GENERIC_VALUE) + " XP.")
    );



    private static final MessageLocale MESSAGE_LEVEL_SET_FEEDBACK = LangEntry.builder("Command.Experience.SetLevel.Feedback").chatMessage(
        Sound.ENTITY_PLAYER_LEVELUP,
        GRAY.wrap("You have set " + WHITE.wrap(PLAYER_DISPLAY_NAME) + "'s XP level to " + SOFT_YELLOW.wrap(GENERIC_VALUE) + " (" + YELLOW.wrap(GENERIC_OLD_VALUE) + DARK_GRAY.wrap(" → ") + GREEN.wrap(GENERIC_NEW_VALUE) + ")")
    );

    private static final MessageLocale MESSAGE_LEVEL_SET_NOTIFY = LangEntry.builder("Command.Experience.SetLevel.Notify").chatMessage(
        Sound.ENTITY_PLAYER_LEVELUP,
        GRAY.wrap("Your XP level has been set to " + SOFT_YELLOW.wrap(GENERIC_VALUE) + ".")
    );

    private static final MessageLocale MESSAGE_LEVEL_ADD_FEEDBACK = LangEntry.builder("Command.Experience.AddLevel.Feedback").chatMessage(
        Sound.ENTITY_PLAYER_LEVELUP,
        GRAY.wrap("You have added " + SOFT_GREEN.wrap(GENERIC_VALUE) + " XP levels to " + WHITE.wrap(PLAYER_DISPLAY_NAME) + " (" + YELLOW.wrap(GENERIC_OLD_VALUE) + DARK_GRAY.wrap(" → ") + GREEN.wrap(GENERIC_NEW_VALUE) + ")")
    );

    private static final MessageLocale MESSAGE_LEVEL_ADD_NOTIFY = LangEntry.builder("Command.Experience.AddLevel.Notify").chatMessage(
        Sound.ENTITY_PLAYER_LEVELUP,
        GRAY.wrap("You have been given " + SOFT_GREEN.wrap(GENERIC_VALUE) + " XP levels.")
    );

    private static final MessageLocale MESSAGE_LEVEL_REMOVE_FEEDBACK = LangEntry.builder("Command.Experience.RemoveLevel.Feedback").chatMessage(
        Sound.ENTITY_PLAYER_LEVELUP,
        GRAY.wrap("You have took " + SOFT_RED.wrap(GENERIC_VALUE) + " XP levels from " + WHITE.wrap(PLAYER_DISPLAY_NAME) + " (" + YELLOW.wrap(GENERIC_OLD_VALUE) + DARK_GRAY.wrap(" → ") + GREEN.wrap(GENERIC_NEW_VALUE) + ")")
    );

    private static final MessageLocale MESSAGE_LEVEL_REMOVE_NOTIFY = LangEntry.builder("Command.Experience.RemoveLevel.Notify").chatMessage(
        Sound.ENTITY_PLAYER_LEVELUP,
        GRAY.wrap("You have been taken " + SOFT_RED.wrap(GENERIC_VALUE) + " XP levels.")
    );

    private final EssentialModule module;
    private final UserManager userManager;

    public ExperienceCommandsProvider(@NotNull SunLightPlugin plugin, @NotNull EssentialModule module, @NotNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.userManager = userManager;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral(COMMAND_LEVEL_ADD, true, new String[]{"addlevel"}, builder -> {
            this.buildLevelCommand(builder, DESCRIPTION_LEVEL_ADD, ModifyMode.ADD);
        });

        this.registerLiteral(COMMAND_LEVEL_SET, true, new String[]{"setlevel"}, builder -> {
            this.buildLevelCommand(builder, DESCRIPTION_LEVEL_SET, ModifyMode.SET);
        });

        this.registerLiteral(COMMAND_LEVEL_REMOVE, true, new String[]{"removelevel"}, builder -> {
            this.buildLevelCommand(builder, DESCRIPTION_LEVEL_REMOVE, ModifyMode.REMOVE);
        });



        this.registerLiteral(COMMAND_XP_ADD, true, new String[]{"addxp"}, builder -> {
            this.buildXPCommand(builder, DESCRIPTION_XP_ADD, ModifyMode.ADD);
        });

        this.registerLiteral(COMMAND_XP_SET, true, new String[]{"setxp"}, builder -> {
            this.buildXPCommand(builder, DESCRIPTION_XP_SET, ModifyMode.SET);
        });

        this.registerLiteral(COMMAND_XP_REMOVE, true, new String[]{"removexp"}, builder -> {
            this.buildXPCommand(builder, DESCRIPTION_XP_REMOVE, ModifyMode.REMOVE);
        });



        this.registerRoot("XP", true, new String[]{"experience", "exp", "xp"},
            Map.of(
                COMMAND_XP_ADD, "add",
                COMMAND_XP_SET, "set",
                COMMAND_XP_REMOVE, "remove",
                COMMAND_LEVEL_ADD, "addlevel",
                COMMAND_LEVEL_SET, "setlevel",
                COMMAND_LEVEL_REMOVE, "removelevel"
            ),
            builder -> builder
                .description(DESCRIPTION_ROOT)
                .permission(PERMISSION_ROOT)
        );

        // TODO Split for level command
    }

    private void buildLevelCommand(@NotNull LiteralNodeBuilder builder, @NotNull TextLocale description, @NotNull ModifyMode mode) {
        builder
            .description(description)
            .permission(PERMISSION_LEVEL)
            .withArguments(
                Arguments.integer(CommandArguments.AMOUNT).localized(CoreLang.COMMAND_ARGUMENT_NAME_AMOUNT.text()),
                Arguments.playerName(CommandArguments.PLAYER).permission(PERMISSION_LEVEL_OTHERS).optional()
            )
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes((context, arguments) -> this.changeLevels(context, arguments, mode));
    }

    private void buildXPCommand(@NotNull LiteralNodeBuilder builder, @NotNull TextLocale description, @NotNull ModifyMode mode) {
        builder
            .description(description)
            .permission(PERMISSION_XP)
            .withArguments(
                Arguments.integer(CommandArguments.AMOUNT).localized(CoreLang.COMMAND_ARGUMENT_NAME_AMOUNT.text()),
                Arguments.playerName(CommandArguments.PLAYER).permission(PERMISSION_XP_OTHERS).optional()
            )
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes((context, arguments) -> this.changeXP(context, arguments, mode));
    }

    private boolean changeXP(@NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull ModifyMode mode) {
        return this.loadPlayerOrSenderAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            int amount = arguments.getInt(CommandArguments.AMOUNT);
            int oldXP = target.getTotalExperience();
            MessageLocale feedbackMessage;
            MessageLocale notifyMessage;

            switch (mode) {
                case SET -> {
                    target.setTotalExperience(0);
                    target.setLevel(0);
                    target.setExp(0F);
                    target.giveExp(amount);

                    feedbackMessage = MESSAGE_XP_SET_FEEDBACK;
                    notifyMessage = MESSAGE_XP_SET_NOTIFY;
                }
                case ADD -> {
                    target.giveExp(amount);

                    feedbackMessage = MESSAGE_XP_ADD_FEEDBACK;
                    notifyMessage = MESSAGE_XP_ADD_NOTIFY;
                }
                case REMOVE -> {
                    target.giveExp(-amount);

                    feedbackMessage = MESSAGE_XP_REMOVE_FEEDBACK;
                    notifyMessage = MESSAGE_XP_REMOVE_NOTIFY;
                }
                default -> {
                    return;
                }
            }

            if (target != context.getSender()) {
                this.module.sendPrefixed(feedbackMessage, context.getSender(), builder -> builder
                    .with(GENERIC_VALUE, () -> NumberUtil.format(amount))
                    .with(GENERIC_OLD_VALUE, () -> NumberUtil.format(oldXP))
                    .with(GENERIC_NEW_VALUE, () -> NumberUtil.format(target.getTotalExperience()))
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                );
            }

            if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                this.module.sendPrefixed(notifyMessage, target, builder -> builder
                    .with(GENERIC_VALUE, () -> NumberUtil.format(amount))
                    .with(GENERIC_OLD_VALUE, () -> NumberUtil.format(oldXP))
                    .with(GENERIC_NEW_VALUE, () -> NumberUtil.format(target.getTotalExperience()))
                );
            }
        });
    }

    private boolean changeLevels(@NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull ModifyMode mode) {
        return this.loadPlayerOrSenderAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            int amount = arguments.getInt(CommandArguments.AMOUNT);
            int oldLevel = target.getLevel();

            MessageLocale feedbackMessage;
            MessageLocale notifyMessage;

            switch (mode) {
                case SET -> {
                    target.setLevel(amount);

                    feedbackMessage = MESSAGE_LEVEL_SET_FEEDBACK;
                    notifyMessage = MESSAGE_LEVEL_SET_NOTIFY;
                }
                case ADD -> {
                    target.giveExpLevels(amount);

                    feedbackMessage = MESSAGE_LEVEL_ADD_FEEDBACK;
                    notifyMessage = MESSAGE_LEVEL_ADD_NOTIFY;
                }
                case REMOVE -> {
                    target.giveExpLevels(-amount);

                    feedbackMessage = MESSAGE_LEVEL_REMOVE_FEEDBACK;
                    notifyMessage = MESSAGE_LEVEL_REMOVE_NOTIFY;
                }
                default -> {
                    return;
                }
            }

            if (target != context.getSender()) {
                this.module.sendPrefixed(feedbackMessage, context.getSender(), builder -> builder
                    .with(GENERIC_VALUE, () -> NumberUtil.format(amount))
                    .with(GENERIC_OLD_VALUE, () -> NumberUtil.format(oldLevel))
                    .with(GENERIC_NEW_VALUE, () -> NumberUtil.format(target.getLevel()))
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                );
            }

            if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                this.module.sendPrefixed(notifyMessage, target, builder -> builder
                    .with(GENERIC_VALUE, () -> NumberUtil.format(amount))
                    .with(GENERIC_OLD_VALUE, () -> NumberUtil.format(oldLevel))
                    .with(GENERIC_NEW_VALUE, () -> NumberUtil.format(target.getLevel()))
                );
            }
        });
    }
}
