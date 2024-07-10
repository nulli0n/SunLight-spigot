package su.nightexpress.sunlight.command.list;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.entry.LangText;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.*;
import su.nightexpress.sunlight.command.mode.ModifyMode;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.config.Lang;

import java.util.function.Function;
import java.util.stream.IntStream;

public class FoodLevelCommand {

    public static final String                       NODE_RESTORE = "foodlevel_restore";
    public static final Function<ModifyMode, String> NODE_TYPE    = type -> "foodlevel_" + type.name().toLowerCase();

    private static final int MAX_VALUE = 20;

    private static boolean restoreGivesSaturation;
    private static float restoreSaturationAmount;

    public static void load(@NotNull SunLightPlugin plugin) {
        CommandRegistry.registerDirectExecutor(NODE_RESTORE, (template, config) -> builderRestore(plugin, template, config));
        for (ModifyMode mode : ModifyMode.values()) {
            CommandRegistry.registerDirectExecutor(NODE_TYPE.apply(mode), (template, config) -> builderMode(plugin, template, config, mode));
        }

        CommandRegistry.addTemplate("foodlevel", CommandTemplate.group(new String[]{"foodlevel", "food"},
            "Food level commands.",
            CommandPerms.PREFIX + "foodlevel",
            CommandTemplate.direct(new String[]{"restore"}, NODE_RESTORE),
            CommandTemplate.direct(new String[]{"add"}, NODE_TYPE.apply(ModifyMode.ADD)),
            CommandTemplate.direct(new String[]{"set"}, NODE_TYPE.apply(ModifyMode.SET)),
            CommandTemplate.direct(new String[]{"remove"}, NODE_TYPE.apply(ModifyMode.REMOVE))
        ));
        CommandRegistry.addTemplate("feed", CommandTemplate.direct(new String[]{"feed"}, NODE_RESTORE));
    }

    @NotNull
    public static DirectNodeBuilder builderMode(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config,
                                                @NotNull ModifyMode mode) {
        LangString description = switch (mode) {
            case ADD -> Lang.COMMAND_FOOD_LEVEL_ADD_DESC;
            case SET -> Lang.COMMAND_FOOD_LEVEL_SET_DESC;
            case REMOVE -> Lang.COMMAND_FOOD_LEVEL_REMOVE_DESC;
        };

        return DirectNode.builder(plugin, template.getAliases())
            .description(description)
            .permission(CommandPerms.FOOD_LEVEL_MODE.apply(mode))
            .withArgument(ArgumentTypes.integerAbs(CommandArguments.AMOUNT)
                .required()
                .localized(Lang.COMMAND_ARGUMENT_NAME_AMOUNT)
                .withSamples(context -> IntStream.range(0, 21).boxed().map(String::valueOf).toList())
            )
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(CommandPerms.FOOD_LEVEL_MODE_OTHERS.apply(mode)))
            .withFlag(CommandFlags.silent().permission(CommandPerms.FOOD_LEVEL_MODE_OTHERS.apply(mode)))
            .executes((context, arguments) -> modifyFood(plugin, context, arguments, mode))
            ;
    }

    public static boolean modifyFood(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull ModifyMode mode) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        int amount = arguments.getIntArgument(CommandArguments.AMOUNT);
        int has = target.getFoodLevel();
        int set = (int) Math.clamp(mode.modify(has, amount), 0, MAX_VALUE);

        target.setFoodLevel(set);
        if (!target.isOnline()) target.saveData();

        LangText infoMessage = switch (mode) {
            case SET -> Lang.COMMAND_FOOD_LEVEL_SET_TARGET;
            case REMOVE -> Lang.COMMAND_FOOD_LEVEL_REMOVE_TARGET;
            case ADD -> Lang.COMMAND_FOOD_LEVEL_ADD_TARGET;
        };

        LangText notifyMessage = switch (mode) {
            case ADD -> Lang.COMMAND_FOOD_LEVEL_ADD_NOTIFY;
            case SET -> Lang.COMMAND_FOOD_LEVEL_SET_NOTIFY;
            case REMOVE -> Lang.COMMAND_FOOD_LEVEL_REMOVE_NOTIFY;
        };

        int current = target.getFoodLevel();

        if (target != context.getSender()) {
            infoMessage.getMessage()
                .replace(Placeholders.GENERIC_CURRENT, NumberUtil.format(current))
                .replace(Placeholders.GENERIC_MAX, NumberUtil.format(MAX_VALUE))
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
                .replace(Placeholders.forPlayer(target))
                .send(context.getSender());
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            notifyMessage.getMessage()
                .replace(Placeholders.GENERIC_CURRENT, NumberUtil.format(current))
                .replace(Placeholders.GENERIC_MAX, NumberUtil.format(MAX_VALUE))
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
                .send(target);
        }

        return true;
    }


    @NotNull
    public static DirectNodeBuilder builderRestore(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        restoreGivesSaturation = ConfigValue.create("Settings.FoodLevel.Restore.GiveSaturation",
            true,
            "Sets whether or not '" + NODE_RESTORE + "' command will give saturation points."
        ).read(config);

        restoreSaturationAmount = ConfigValue.create("Settings.FoodLevel.Restore.Saturation_Amount",
            5D,
            "Sets amount of saturation points given by the '" + NODE_RESTORE + "' command."
        ).read(config).floatValue();

        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_FOOD_LEVEL_RESTORE_DESC)
            .permission(CommandPerms.FOOD_LEVEL_RESTORE)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(CommandPerms.FOOD_LEVEL_RESTORE_OTHERS))
            .withFlag(CommandFlags.silent().permission(CommandPerms.FOOD_LEVEL_RESTORE_OTHERS))
            .executes((context, arguments) -> restoreFood(plugin, context, arguments))
            ;
    }

    public static boolean restoreFood(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        target.setFoodLevel(MAX_VALUE);
        if (restoreGivesSaturation) {
            target.setSaturation(restoreSaturationAmount);
        }
        if (!target.isOnline()) target.saveData();

        if (context.getSender() != target) {
            Lang.COMMAND_FOOD_LEVEL_RESTORE_INFO.getMessage().replace(Placeholders.forPlayer(target)).send(context.getSender());
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            Lang.COMMAND_FOOD_LEVEL_RESTORE_NOTIFY.getMessage().send(target);
        }

        return true;
    }
}
