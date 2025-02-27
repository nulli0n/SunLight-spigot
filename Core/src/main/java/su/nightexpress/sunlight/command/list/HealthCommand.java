package su.nightexpress.sunlight.command.list;

import org.bukkit.attribute.Attribute;
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
import su.nightexpress.nightcore.util.EntityUtil;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.*;
import su.nightexpress.sunlight.command.mode.ModifyMode;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.config.Lang;

import java.util.function.Function;
import java.util.stream.IntStream;

public class HealthCommand {

    private static final Function<ModifyMode, String> NODE_TYPE    = mode -> "health_" + mode.name().toLowerCase();
    private static final String                       NODE_RESTORE = "health_restore";

    private static boolean restoreClearEffects;

    public static void load(@NotNull SunLightPlugin plugin) {
        CommandRegistry.registerDirectExecutor(NODE_RESTORE, (template, config) -> builderRestore(plugin, template, config));
        for (ModifyMode mode : ModifyMode.values()) {
            CommandRegistry.registerDirectExecutor(NODE_TYPE.apply(mode), (template, config) -> builderMode(plugin, template, config, mode));
        }

        CommandRegistry.addTemplate("health", CommandTemplate.group(new String[]{"health", "hp"},
            "Health commands.",
            CommandPerms.PREFIX + "health",
            CommandTemplate.direct(new String[]{"restore"}, NODE_RESTORE),
            CommandTemplate.direct(new String[]{"add"}, NODE_TYPE.apply(ModifyMode.ADD)),
            CommandTemplate.direct(new String[]{"set"}, NODE_TYPE.apply(ModifyMode.SET)),
            CommandTemplate.direct(new String[]{"remove"}, NODE_TYPE.apply(ModifyMode.REMOVE))
        ));
        CommandRegistry.addTemplate("heal", CommandTemplate.direct(new String[]{"heal"}, NODE_RESTORE));
    }

    public static DirectNodeBuilder builderMode(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config, @NotNull ModifyMode mode) {
        LangString description = switch (mode) {
            case ADD -> Lang.COMMAND_HEALTH_ADD_DESC;
            case SET -> Lang.COMMAND_HEALTH_SET_DESC;
            case REMOVE -> Lang.COMMAND_HEALTH_REMOVE_DESC;
        };

        return DirectNode.builder(plugin, template.getAliases())
            .description(description)
            .permission(CommandPerms.HEALTH_MODE.apply(mode))
            .withArgument(ArgumentTypes.decimalAbs(CommandArguments.AMOUNT)
                .required()
                .localized(Lang.COMMAND_ARGUMENT_NAME_AMOUNT)
                .withSamples(tabContext -> IntStream.range(0, 21).boxed().map(String::valueOf).toList())
            )
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(CommandPerms.HEALTH_MODE_OTHERS.apply(mode)))
            .withFlag(CommandFlags.silent().permission(CommandPerms.HEALTH_MODE_OTHERS.apply(mode)))
            .executes((context, arguments) -> executeMode(plugin, context, arguments, mode))
            ;
    }

    public static DirectNodeBuilder builderRestore(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        restoreClearEffects = ConfigValue.create("Settings.Health.Restore.ClearEffects",
            true,
            "Sets whether or not '" + NODE_RESTORE + "' command will clear potion effects as well."
        ).read(config);

        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_HEALTH_RESTORE_DESC)
            .permission(CommandPerms.HEALTH_RESTORE)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(CommandPerms.HEALTH_RESTORE_OTHERS))
            .withFlag(CommandFlags.silent().permission(CommandPerms.HEALTH_RESTORE_OTHERS))
            .executes((context, arguments) -> executeRestore(plugin, context, arguments))
            ;
    }

    public static boolean executeMode(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull ModifyMode mode) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        double amount = arguments.getDoubleArgument(CommandArguments.AMOUNT);
        double has = target.getHealth();
        double max = EntityUtil.getAttribute(target, Attribute.MAX_HEALTH);
        double set = Math.clamp(mode.modify(has, amount), 0, max);

        target.setHealth(set);
        if (!target.isOnline()) target.saveData();

        LangText infoMessage = switch (mode) {
            case SET -> Lang.COMMAND_HEALTH_SET_INFO;
            case REMOVE -> Lang.COMMAND_HEALTH_REMOVE_INFO;
            case ADD -> Lang.COMMAND_HEALTH_ADD_INFO;
        };

        LangText notifyMessage = switch (mode) {
            case ADD -> Lang.COMMAND_HEALTH_ADD_NOTIFY;
            case SET -> Lang.COMMAND_HEALTH_SET_NOTIFY;
            case REMOVE -> Lang.COMMAND_HEALTH_REMOVE_NOTIFY;
        };

        double current = target.getHealth();

        if (target != context.getSender()) {
            infoMessage.getMessage()
                .replace(Placeholders.GENERIC_CURRENT, NumberUtil.format(current))
                .replace(Placeholders.GENERIC_MAX, NumberUtil.format(max))
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
                .replace(Placeholders.forPlayer(target))
                .send(context.getSender());
        }
        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            notifyMessage.getMessage()
                .replace(Placeholders.GENERIC_CURRENT, NumberUtil.format(current))
                .replace(Placeholders.GENERIC_MAX, NumberUtil.format(max))
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
                .replace(Placeholders.forPlayer(target))
                .send(target);
        }

        return true;
    }

    public static boolean executeRestore(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        double max = EntityUtil.getAttribute(target, Attribute.MAX_HEALTH);

        target.setHealth(max);
        if (restoreClearEffects) {
            target.getActivePotionEffects().forEach(effect -> target.removePotionEffect(effect.getType()));
        }
        if (!target.isOnline()) target.saveData();

        if (target != context.getSender()) {
            Lang.COMMAND_HEALTH_RESTORE_INFO.getMessage()
                .replace(Placeholders.forPlayer(target))
                .send(context.getSender());
        }
        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            Lang.COMMAND_HEALTH_RESTORE_NOTIFY.getMessage()
                .replace(Placeholders.forPlayer(target))
                .send(target);
        }

        return true;
    }
}
