package su.nightexpress.sunlight.module.essential.command;

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
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.sunlight.SLPlaceholders;
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

public class AirCommandProvider extends AbstractCommandProvider {

    private static final String COMMAND_ADD    = "add";
    private static final String COMMAND_SET    = "set";
    private static final String COMMAND_REMOVE = "remove";

    private static final Permission PERMISSION_ROOT   = EssentialPerms.COMMAND.permission("air.root");
    private static final Permission PERMISSION_ADD    = EssentialPerms.COMMAND.permission("air.add");
    private static final Permission PERMISSION_SET    = EssentialPerms.COMMAND.permission("air.set");
    private static final Permission PERMISSION_REMOVE = EssentialPerms.COMMAND.permission("air.remove");
    private static final Permission PERMISSION_OTHERS = EssentialPerms.COMMAND.permission("air.others");

    private static final TextLocale DESCRIPTION_ROOT   = LangEntry.builder("Command.Air.Root.Desc").text("Add commands.");
    private static final TextLocale DESCRIPTION_ADD    = LangEntry.builder("Command.Air.Add.Desc").text("Add air ticks.");
    private static final TextLocale DESCRIPTION_SET    = LangEntry.builder("Command.Air.Set.Desc").text("Set air ticks.");
    private static final TextLocale DESCRIPTION_REMOVE = LangEntry.builder("Command.Air.Remove.Desc").text("Remove air ticks.");

    private static final MessageLocale ADD_TARGET = LangEntry.builder("Command.Air.Give.Target").chatMessage(
        GRAY.wrap("You have added " + GREEN.wrap(GENERIC_AMOUNT) + " air ticks to " + SOFT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + ". New air ticks: " + SOFT_YELLOW.wrap(GENERIC_CURRENT) + "/" + SOFT_YELLOW.wrap(GENERIC_MAX) + ".")
    );

    private static final MessageLocale REMOVE_TARGET = LangEntry.builder("Command.Air.Take.Target").chatMessage(
        GRAY.wrap("You have removed " + RED.wrap(GENERIC_AMOUNT) + " air ticks from " + SOFT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + ". New air ticks: " + SOFT_YELLOW.wrap(GENERIC_CURRENT) + "/" + SOFT_YELLOW.wrap(GENERIC_MAX) + ".")
    );

    private static final MessageLocale SET_TARGET = LangEntry.builder("Command.Air.Set.Target").chatMessage(
        GRAY.wrap("You have set " + YELLOW.wrap(GENERIC_AMOUNT) + " air ticks for " + SOFT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + ". New air ticks: " + SOFT_YELLOW.wrap(GENERIC_CURRENT) + "/" + SOFT_YELLOW.wrap(GENERIC_MAX) + ".")
    );

    private static final MessageLocale ADD_NOTIFY = LangEntry.builder("Command.Air.Give.Notify").chatMessage(
        GRAY.wrap("You got " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + " air ticks: " + SOFT_YELLOW.wrap(GENERIC_CURRENT) + "/" + SOFT_YELLOW.wrap(GENERIC_MAX) + ".")
    );

    private static final MessageLocale REMOVE_NOTIFY = LangEntry.builder("Command.Air.Take.Notify").chatMessage(
        GRAY.wrap("You lost " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + " air ticks: " + SOFT_YELLOW.wrap(GENERIC_CURRENT) + "/" + SOFT_YELLOW.wrap(GENERIC_MAX) + ".")
    );

    private static final MessageLocale SET_NOTIFY = LangEntry.builder("Command.Air.Set.Notify").chatMessage(
        GRAY.wrap("Your air ticks set to " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + ": " + SOFT_YELLOW.wrap(GENERIC_CURRENT) + "/" + SOFT_YELLOW.wrap(GENERIC_MAX) + ".")
    );

    private final EssentialModule module;
    private final UserManager userManager;

    public AirCommandProvider(@NotNull SunLightPlugin plugin, @NotNull EssentialModule module, @NotNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.userManager = userManager;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral(COMMAND_ADD, false, new String[]{"addair"}, builder -> this.buildExecutor(builder, ModifyMode.ADD));
        this.registerLiteral(COMMAND_SET, false, new String[]{"setair"}, builder -> this.buildExecutor(builder, ModifyMode.SET));
        this.registerLiteral(COMMAND_REMOVE, false, new String[]{"removeair"}, builder -> this.buildExecutor(builder, ModifyMode.REMOVE));

        this.registerRoot("Air", true, new String[]{"air"}, Map.of(
                COMMAND_ADD, "add",
                COMMAND_REMOVE, "remove",
                COMMAND_SET, "set"),
            builder -> builder.description(DESCRIPTION_ROOT).permission(PERMISSION_ROOT)
        );
    }

    private void buildExecutor(@NotNull LiteralNodeBuilder builder, @NotNull ModifyMode mode) {
        TextLocale description = switch (mode) {
            case ADD -> DESCRIPTION_ADD;
            case SET -> DESCRIPTION_SET;
            case REMOVE -> DESCRIPTION_REMOVE;
        };

        Permission permission = switch (mode) {
            case ADD -> PERMISSION_ADD;
            case SET -> PERMISSION_SET;
            case REMOVE -> PERMISSION_REMOVE;
        };

        builder
            .description(description)
            .permission(permission)
            .withArguments(Arguments.integer(CommandArguments.AMOUNT, 0).localized(CoreLang.COMMAND_ARGUMENT_NAME_AMOUNT)
                .suggestions((reader, context) -> Lists.newList("100", "200", "300", "400", "500"))
            )
            .withArguments(Arguments.playerName(CommandArguments.PLAYER).permission(PERMISSION_OTHERS))
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes((context, arguments) -> this.executeMode(context, arguments, mode));
    }

    private boolean executeMode(@NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull ModifyMode mode) {
        int amount = arguments.getInt(CommandArguments.AMOUNT);

        return this.loadPlayerOrSenderAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            target.setRemainingAir((int) Math.max(0, mode.modify(target.getRemainingAir(), amount)));

            MessageLocale message = (switch (mode) {
                case SET -> SET_TARGET;
                case REMOVE -> REMOVE_TARGET;
                case ADD -> ADD_TARGET;
            });

            MessageLocale notify = (switch (mode) {
                case ADD -> ADD_NOTIFY;
                case SET -> SET_NOTIFY;
                case REMOVE -> REMOVE_NOTIFY;
            });

            if (target != context.getSender()) {
                this.module.sendPrefixed(message, context.getSender(), replacer -> replacer
                    .with(SLPlaceholders.GENERIC_CURRENT, () -> String.valueOf(target.getRemainingAir()))
                    .with(SLPlaceholders.GENERIC_MAX, () -> String.valueOf(target.getMaximumAir()))
                    .with(SLPlaceholders.GENERIC_AMOUNT, () -> NumberUtil.format(amount))
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                );
            }

            if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                this.module.sendPrefixed(notify, target, replacer -> replacer
                    .with(SLPlaceholders.GENERIC_CURRENT, () -> String.valueOf(target.getRemainingAir()))
                    .with(SLPlaceholders.GENERIC_MAX, () -> String.valueOf(target.getMaximumAir()))
                    .with(SLPlaceholders.GENERIC_AMOUNT, () -> NumberUtil.format(amount))
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                );
            }
        });
    }
}
