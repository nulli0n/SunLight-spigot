package su.nightexpress.sunlight.module.essential.command;

import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
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
import su.nightexpress.nightcore.util.EntityUtil;
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

public class HealthCommandProvider extends AbstractCommandProvider {

    private static final String COMMAND_RESTORE = "restore";
    private static final String COMMAND_ADD     = "add";
    private static final String COMMAND_SET     = "set";
    private static final String COMMAND_REMOVE  = "remove";

    private static final Permission PERM_ADD     = EssentialPerms.COMMAND.permission("health.add");
    private static final Permission PERM_SET     = EssentialPerms.COMMAND.permission("health.set");
    private static final Permission PERM_REMOVE  = EssentialPerms.COMMAND.permission("health.remove");
    private static final Permission PERM_RESTORE = EssentialPerms.COMMAND.permission("health.restore");
    private static final Permission PERM_ROOT    = EssentialPerms.COMMAND.permission("health.root");
    private static final Permission PERM_OTHERS  = EssentialPerms.COMMAND.permission("health.others");

    private static final TextLocale DESCRIPTION_ROOT    = LangEntry.builder("Command.Health.Root.Desc").text("Health commands.");
    private static final TextLocale DESCRIPTION_ADD     = LangEntry.builder("Command.Health.Add.Desc").text("Add health points.");
    private static final TextLocale DESCRIPTION_SET     = LangEntry.builder("Command.Health.Set.Desc").text("Set health points.");
    private static final TextLocale DESCRIPTION_REMOVE  = LangEntry.builder("Command.Health.Remove.Desc").text("Remove health points.");
    private static final TextLocale DESCRIPTION_RESTORE = LangEntry.builder("Command.Health.Restore.Desc").text("Restore player's health or revive.");

    private static final MessageLocale MESSAGE_ADD_FEEDBACK = LangEntry.builder("Command.Health.Add.Target").chatMessage(
        Sound.ENTITY_WITCH_DRINK,
        GRAY.wrap("You have " + GREEN.wrap("healed") + " " + WHITE.wrap(PLAYER_DISPLAY_NAME) + " for " + GREEN.wrap(GENERIC_AMOUNT + "❤") + " (" + WHITE.wrap(GENERIC_OLD_VALUE + "❤") + DARK_GRAY.wrap(" → ") + GREEN.wrap(GENERIC_NEW_VALUE + "❤") + ")")
    );

    private static final MessageLocale MESSAGE_REMOVE_FEEDBACK = LangEntry.builder("Command.Health.Remove.Target").chatMessage(
        Sound.ENTITY_PLAYER_HURT,
        GRAY.wrap("You have " + RED.wrap("damaged") + " " + WHITE.wrap(PLAYER_DISPLAY_NAME) + " for " + RED.wrap(GENERIC_AMOUNT + "❤") + " (" + WHITE.wrap(GENERIC_OLD_VALUE + "❤") + DARK_GRAY.wrap(" → ") + RED.wrap(GENERIC_NEW_VALUE + "❤") + ")")
    );

    private static final MessageLocale MESSAGE_SET_FEEDBACK = LangEntry.builder("Command.Health.Set.Target").chatMessage(
        Sound.ENTITY_WITCH_DRINK,
        GRAY.wrap("You have " + YELLOW.wrap("set") + " " + WHITE.wrap(PLAYER_DISPLAY_NAME) + "'s health to " + YELLOW.wrap(GENERIC_AMOUNT + "❤") + " (" + WHITE.wrap(GENERIC_OLD_VALUE + "❤") + DARK_GRAY.wrap(" → ") + RED.wrap(GENERIC_NEW_VALUE + "❤") + ")")
    );

    private static final MessageLocale MESSAGE_ADD_NOTIFY = LangEntry.builder("Command.Health.Add.Notify").chatMessage(
        Sound.ENTITY_WITCH_DRINK,
        GRAY.wrap("You have been " + GREEN.wrap("healed") + " for " + GREEN.wrap(GENERIC_AMOUNT + "❤") + ".")
    );

    private static final MessageLocale MESSAGE_REMOVE_NOTIFY = LangEntry.builder("Command.Health.Remove.Notify").chatMessage(
        Sound.ENTITY_PLAYER_HURT,
        GRAY.wrap("You have been " + RED.wrap("damaged") + " for " + RED.wrap(GENERIC_AMOUNT + "❤") + ".")
    );

    private static final MessageLocale MESSAGE_SET_NOTIFY = LangEntry.builder("Command.Health.Set.Notify").chatMessage(
        Sound.ENTITY_WITCH_DRINK,
        GRAY.wrap("Your health has been set to " + YELLOW.wrap(GENERIC_AMOUNT + "❤") + ".")
    );

    private static final MessageLocale MESSAGE_RESTORE_NOTIFY = LangEntry.builder("Command.Health.Restore.Notfiy").chatMessage(
        Sound.ENTITY_WITCH_DRINK,
        GRAY.wrap("You have been fully healed.")
    );

    private static final MessageLocale MESSAGE_RESTORE_FEEDBACK = LangEntry.builder("Command.Health.Restore.Info").chatMessage(
        Sound.ENTITY_WITCH_DRINK,
        GRAY.wrap("You have restored " + WHITE.wrap(PLAYER_DISPLAY_NAME) + "'s health.")
    );

    private static final MessageLocale MESSAGE_REVIVE_NOTIFY = LangEntry.builder("Command.Health.Revive.Feedback").chatMessage(
        Sound.ITEM_TOTEM_USE,
        GRAY.wrap("You have been revived.")
    );

    private static final MessageLocale MESSAGE_REVIVE_FEEDBACK = LangEntry.builder("Command.Health.Revive.Notify").chatMessage(
        Sound.ITEM_TOTEM_USE,
        GRAY.wrap("You have revived " + WHITE.wrap(PLAYER_DISPLAY_NAME) + ".")
    );

    private static final MessageLocale MESSAGE_DEAD_FEEDBACK = LangEntry.builder("Command.Health.TargetDead.Feedback").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap(WHITE.wrap(PLAYER_DISPLAY_NAME) + "'s health can not modified, because they are dead.")
    );

    private static final MessageLocale MESSAGE_DEAD_NOTIFY = LangEntry.builder("Command.Health.TargetDead.Notify").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You can't change your health while you are dead.")
    );

    private final EssentialModule   module;
    private final EssentialSettings settings;
    private final UserManager       userManager;

    public HealthCommandProvider(@NotNull SunLightPlugin plugin, @NotNull EssentialModule module, @NotNull EssentialSettings settings, @NotNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.settings = settings;
        this.userManager = userManager;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral(COMMAND_ADD, false, new String[]{"addhealth"}, builder -> this.builderMode(builder, ModifyMode.ADD));
        this.registerLiteral(COMMAND_SET, false, new String[]{"sethealth"}, builder -> this.builderMode(builder, ModifyMode.SET));
        this.registerLiteral(COMMAND_REMOVE, false, new String[]{"removehealth"}, builder -> this.builderMode(builder, ModifyMode.REMOVE));

        this.registerLiteral(COMMAND_RESTORE, true, new String[]{"heal"}, builder -> builder
            .description(DESCRIPTION_RESTORE)
            .permission(PERM_RESTORE)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER).permission(PERM_OTHERS).optional())
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::restoreHealth)
        );

        this.registerRoot("Health", true, new String[]{"health", "hp"},
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
            .withArguments(
                Arguments.decimal(CommandArguments.AMOUNT, 0)
                    .localized(CoreLang.COMMAND_ARGUMENT_NAME_AMOUNT.text())
                    .suggestions((reader, tabContext) -> IntStream.range(0, 21).boxed().map(String::valueOf).toList()),
                Arguments.playerName(CommandArguments.PLAYER).permission(PERM_OTHERS).optional()
            )
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes((context, arguments) -> this.changeHealth(context, arguments, mode));
    }

    private boolean changeHealth(@NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull ModifyMode mode) {
        return this.loadPlayerOrSenderAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            if (target.isDead()) {
                context.send(context.getSender() == target ? MESSAGE_DEAD_NOTIFY : MESSAGE_DEAD_FEEDBACK, replacer -> replacer.replace(forPlayer(target)));
                return;
            }

            double amount = arguments.getDouble(CommandArguments.AMOUNT);
            double oldHealth = target.getHealth();
            double maxHealth = EntityUtil.getAttributeValue(target, Attribute.MAX_HEALTH);
            double newHealth = Math.clamp(mode.modify(oldHealth, amount), 0D, maxHealth);

            if (newHealth < oldHealth) {
                target.sendHurtAnimation(0f); // Play hurt animation.
            }

            target.setHealth(newHealth);

            MessageLocale feedbackMessage = switch (mode) {
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
                this.module.sendPrefixed(feedbackMessage, context.getSender(), builder -> builder
                    .with(GENERIC_OLD_VALUE, () -> NumberUtil.format(oldHealth))
                    .with(GENERIC_NEW_VALUE, () -> NumberUtil.format(target.getHealth()))
                    .with(GENERIC_AMOUNT, () -> NumberUtil.format(amount))
                    .with(GENERIC_CURRENT, () -> NumberUtil.format(target.getHealth())) // old
                    .with(GENERIC_MAX, () -> NumberUtil.format(maxHealth)) // old
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                );
            }
            if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                this.module.sendPrefixed(notifyMessage, target, builder -> builder
                    .with(GENERIC_OLD_VALUE, () -> NumberUtil.format(oldHealth))
                    .with(GENERIC_NEW_VALUE, () -> NumberUtil.format(target.getHealth()))
                    .with(GENERIC_AMOUNT, () -> NumberUtil.format(amount))
                    .with(GENERIC_CURRENT, () -> NumberUtil.format(target.getHealth())) // old
                    .with(GENERIC_MAX, () -> NumberUtil.format(maxHealth)) // old
                );
            }
        });
    }

    private boolean restoreHealth(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return this.loadPlayerOrSenderAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            MessageLocale feedbackLocale;
            MessageLocale notifyLocale;

            if (target.isDead()) {
                feedbackLocale = MESSAGE_REVIVE_FEEDBACK;
                notifyLocale = MESSAGE_REVIVE_NOTIFY;

                target.spigot().respawn();
            }
            else {
                feedbackLocale = MESSAGE_RESTORE_FEEDBACK;
                notifyLocale = MESSAGE_RESTORE_NOTIFY;

                double maxHealth = EntityUtil.getAttributeValue(target, Attribute.MAX_HEALTH);

                target.setHealth(maxHealth);
                if (this.settings.healthClearEffects.get()) {
                    target.getActivePotionEffects().forEach(effect -> target.removePotionEffect(effect.getType()));
                }
            }

            if (target != context.getSender()) {
                this.module.sendPrefixed(feedbackLocale, context.getSender(), builder -> builder.with(CommonPlaceholders.PLAYER.resolver(target)));
            }
            if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                this.module.sendPrefixed(notifyLocale, target);
            }
        });
    }
}
