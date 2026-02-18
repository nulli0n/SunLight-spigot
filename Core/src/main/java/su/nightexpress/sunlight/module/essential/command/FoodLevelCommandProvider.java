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
import su.nightexpress.sunlight.module.essential.EssentialSettings;
import su.nightexpress.sunlight.user.UserManager;

import java.util.Map;
import java.util.stream.IntStream;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.*;

public class FoodLevelCommandProvider extends AbstractCommandProvider {

    private static final String COMMAND_RESTORE = "restore";
    private static final String COMMAND_ADD     = "add";
    private static final String COMMAND_SET     = "set";
    private static final String COMMAND_REMOVE  = "remove";

    private static final Permission PERM_ADD     = EssentialPerms.COMMAND.permission("foodlevel.add");
    private static final Permission PERM_SET     = EssentialPerms.COMMAND.permission("foodlevel.set");
    private static final Permission PERM_REMOVE  = EssentialPerms.COMMAND.permission("foodlevel.remove");
    private static final Permission PERM_RESTORE = EssentialPerms.COMMAND.permission("foodlevel.restore");
    private static final Permission PERM_ROOT    = EssentialPerms.COMMAND.permission("foodlevel.root");
    private static final Permission PERM_OTHERS  = EssentialPerms.COMMAND.permission("foodlevel.others");

    private static final TextLocale DESCRIPTION_ROOT    = LangEntry.builder("Command.Food.Root.Desc").text("Food level commands.");
    private static final TextLocale DESCRIPTION_ADD     = LangEntry.builder("Command.Food.Add.Desc").text("Add food points.");
    private static final TextLocale DESCRIPTION_SET     = LangEntry.builder("Command.Food.Set.Desc").text("Set food level.");
    private static final TextLocale DESCRIPTION_REMOVE  = LangEntry.builder("Command.Food.Remove.Desc").text("Remove food points.");
    private static final TextLocale DESCRIPTION_RESTORE = LangEntry.builder("Command.Food.Restore.Desc").text("Restore food level.");

    private static final MessageLocale MESSAGE_ADD_FEEDBACK = LangEntry.builder("Command.Food.Give.Target").chatMessage(
        GRAY.wrap("You have " + GREEN.wrap("added " + GENERIC_AMOUNT) + " food points to " + WHITE.wrap(PLAYER_DISPLAY_NAME) + " (" + WHITE.wrap(GENERIC_OLD_VALUE) + DARK_GRAY.wrap(" → ") + GREEN.wrap(GENERIC_NEW_VALUE) + ")"));

    private static final MessageLocale MESSAGE_REMOVE_FEEDBACK = LangEntry.builder("Command.Food.Take.Target").chatMessage(
        GRAY.wrap("You have " + RED.wrap("removed " + GENERIC_AMOUNT) + " food points from " + WHITE.wrap(PLAYER_DISPLAY_NAME) + " (" + WHITE.wrap(GENERIC_OLD_VALUE) + DARK_GRAY.wrap(" → ") + RED.wrap(GENERIC_NEW_VALUE) + ")"));

    private static final MessageLocale MESSAGE_SET_FEEDBACK = LangEntry.builder("Command.Food.Set.Target").chatMessage(
        GRAY.wrap("You have set " + WHITE.wrap(PLAYER_DISPLAY_NAME) + "'s food level to " + YELLOW.wrap(GENERIC_AMOUNT) + " (" + WHITE.wrap(GENERIC_OLD_VALUE) + DARK_GRAY.wrap(" → ") + YELLOW.wrap(GENERIC_NEW_VALUE) + ")"));

    private static final MessageLocale MESSAGE_ADD_NOTIFY = LangEntry.builder("Command.Food.Give.Notify").chatMessage(
        GRAY.wrap("Your food level has been increased by " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + "."));

    private static final MessageLocale MESSAGE_REMOVE_NOTIFY = LangEntry.builder("Command.Food.Take.Notify").chatMessage(
        GRAY.wrap("Your food level has been decreased by " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + "."));

    private static final MessageLocale MESSAGE_SET_NOTIFY = LangEntry.builder("Command.Food.Set.Notify").chatMessage(
        GRAY.wrap("Your food level has been set to " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + "."));

    private static final MessageLocale MESSAGE_RESTORE_NOTIFY = LangEntry.builder("Command.Food.Restore.Notify").chatMessage(
        Sound.ENTITY_GENERIC_EAT,
        GRAY.wrap("You have been fed!"));

    private static final MessageLocale MESSAGE_RESTORE_FEEDBACK = LangEntry.builder("Command.Food.Restore.Info").chatMessage(
        Sound.ENTITY_GENERIC_EAT,
        GRAY.wrap("You have fed " + WHITE.wrap(PLAYER_DISPLAY_NAME) + "."));

    private static final int MAX_VALUE = 20;

    private final EssentialModule module;
    private final EssentialSettings settings;
    private final UserManager userManager;

    public FoodLevelCommandProvider(@NotNull SunLightPlugin plugin, @NotNull EssentialModule module, @NotNull EssentialSettings settings, @NotNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.settings = settings;
        this.userManager = userManager;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral(COMMAND_ADD, false, new String[]{"addfood"}, builder -> this.builderMode(builder, ModifyMode.ADD));
        this.registerLiteral(COMMAND_SET, false, new String[]{"setfood"}, builder -> this.builderMode(builder, ModifyMode.SET));
        this.registerLiteral(COMMAND_REMOVE, false, new String[]{"removefood"}, builder -> this.builderMode(builder, ModifyMode.REMOVE));

        this.registerLiteral(COMMAND_RESTORE, true, new String[]{"feed"}, builder -> builder
            .description(DESCRIPTION_RESTORE)
            .permission(PERM_RESTORE)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER).permission(PERM_OTHERS).optional())
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::restoreFood)
        );

        this.registerRoot("Food Level", true, new String[]{"foodlevel"},
            Map.of(
                COMMAND_ADD, "add",
                COMMAND_SET, "set",
                COMMAND_REMOVE, "remove",
                COMMAND_RESTORE, "restore"
            ),
            builder -> builder.description(DESCRIPTION_ROOT).permission(PERM_ROOT)
        );
    }

    private void builderMode(@NotNull LiteralNodeBuilder builder, @NotNull ModifyMode mode) {
        TextLocale description = switch (mode) {
            case ADD -> DESCRIPTION_ADD;
            case SET -> DESCRIPTION_SET;
            case REMOVE -> DESCRIPTION_REMOVE;
        };

        Permission permission = switch (mode) {
            case ADD -> PERM_ADD;
            case SET -> PERM_SET;
            case REMOVE -> PERM_REMOVE;
        };

        builder
            .description(description)
            .permission(permission)
            .withArguments(Arguments.integer(CommandArguments.AMOUNT, 0)
                .localized(CoreLang.COMMAND_ARGUMENT_NAME_AMOUNT)
                .suggestions((reader, context) -> IntStream.range(0, 21).boxed().map(String::valueOf).toList()),
                Arguments.playerName(CommandArguments.PLAYER).permission(PERM_OTHERS).optional()
            )
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes((context, arguments) -> modifyFood(context, arguments, mode));
    }

    private boolean modifyFood(@NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull ModifyMode mode) {
        return this.loadPlayerOrSenderAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            int amount = arguments.getInt(CommandArguments.AMOUNT);
            int oldValue = target.getFoodLevel();
            int newValue = (int) Math.clamp(mode.modify(oldValue, amount), 0, MAX_VALUE);

            target.setFoodLevel(newValue);

            MessageLocale infoMessage = switch (mode) {
                case SET -> MESSAGE_SET_FEEDBACK;
                case REMOVE -> MESSAGE_REMOVE_FEEDBACK;
                case ADD -> MESSAGE_ADD_FEEDBACK;
            };

            MessageLocale notifyMessage = switch (mode) {
                case ADD -> MESSAGE_ADD_NOTIFY;
                case SET -> MESSAGE_SET_NOTIFY;
                case REMOVE -> MESSAGE_REMOVE_NOTIFY;
            };

            if (target != context.getSender()) {
                this.module.sendPrefixed(infoMessage, context.getSender(), builder -> builder
                    .with(GENERIC_NEW_VALUE, () -> NumberUtil.format(target.getFoodLevel()))
                    .with(GENERIC_OLD_VALUE, () -> NumberUtil.format(oldValue))
                    .with(GENERIC_MAX, () -> NumberUtil.format(MAX_VALUE))
                    .with(GENERIC_AMOUNT, () -> NumberUtil.format(amount))
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                );
            }

            if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                this.module.sendPrefixed(notifyMessage, target, builder -> builder
                    .with(GENERIC_NEW_VALUE, () -> NumberUtil.format(target.getFoodLevel()))
                    .with(GENERIC_OLD_VALUE, () -> NumberUtil.format(oldValue))
                    .with(GENERIC_MAX, () -> NumberUtil.format(MAX_VALUE))
                    .with(GENERIC_AMOUNT, () -> NumberUtil.format(amount))
                );
            }
        });
    }

    private boolean restoreFood(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return this.loadPlayerOrSenderAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            target.setFoodLevel(MAX_VALUE);

            if (this.settings.foodSaturationEnabled.get()) {
                target.setSaturation(Math.min(MAX_VALUE, target.getSaturation() + this.settings.foodSaturationAmount.get().floatValue()));
            }

            if (context.getSender() != target) {
                this.module.sendPrefixed(MESSAGE_RESTORE_FEEDBACK, context.getSender(), replacer -> replacer.with(CommonPlaceholders.PLAYER.resolver(target)));
            }

            if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                this.module.sendPrefixed(MESSAGE_RESTORE_NOTIFY, target);
            }
        });
    }
}
