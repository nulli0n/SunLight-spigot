package su.nightexpress.sunlight.command.list;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.message.LangMessage;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.*;
import su.nightexpress.sunlight.command.mode.ModifyMode;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.config.Lang;

public class AirCommand {

    private static final String NODE_ADD    = "air_add";
    private static final String NODE_SET    = "air_set";
    private static final String NODE_REMOVE = "air_remove";

    public static void load(@NotNull SunLightPlugin plugin) {
        CommandRegistry.registerDirectExecutor(NODE_ADD, (template, config) -> builderMode(plugin, template, Lang.COMMAND_AIR_ADD_DESC, ModifyMode.ADD));
        CommandRegistry.registerDirectExecutor(NODE_SET, (template, config) -> builderMode(plugin, template, Lang.COMMAND_AIR_SET_DESC, ModifyMode.SET));
        CommandRegistry.registerDirectExecutor(NODE_REMOVE, (template, config) -> builderMode(plugin, template, Lang.COMMAND_AIR_REMOVE_DESC, ModifyMode.REMOVE));

        CommandTemplate template = CommandTemplate.group(
            new String[]{"air", "oxygen"},
            "Air commands.",
            CommandPerms.PREFIX + "air",
            CommandTemplate.direct(new String[]{"add"}, NODE_ADD),
            CommandTemplate.direct(new String[]{"set"}, NODE_SET),
            CommandTemplate.direct(new String[]{"remove"}, NODE_REMOVE)
        );
        CommandRegistry.addTemplate("air", template);
    }

    @NotNull
    private static DirectNodeBuilder builderMode(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull LangString description, @NotNull ModifyMode mode) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(description)
            .permission(CommandPerms.AIR_MODE.apply(mode))
            .withArgument(ArgumentTypes.integerAbs(CommandArguments.AMOUNT).required().localized(Lang.COMMAND_ARGUMENT_NAME_AMOUNT)
                .withSamples(tabContext -> Lists.newList("100", "200", "300", "400", "500"))
            )
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(CommandPerms.AIR_MODE_OTHERS.apply(mode)))
            .withFlag(CommandFlags.silent().permission(CommandPerms.AIR_MODE_OTHERS.apply(mode)))
            .executes((context, arguments) -> executeMode(plugin, context, arguments, mode));
    }

    private static boolean executeMode(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull ModifyMode mode) {
        int amount = arguments.getIntArgument(CommandArguments.AMOUNT);
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        target.setRemainingAir((int) Math.max(0, mode.modify(target.getRemainingAir(), amount)));
        if (!target.isOnline()) target.saveData();

        LangMessage message = switch (mode) {
            case SET -> Lang.COMMAND_AIR_SET_TARGET.getMessage();
            case REMOVE -> Lang.COMMAND_AIR_REMOVE_TARGET.getMessage();
            case ADD -> Lang.COMMAND_AIR_ADD_TARGET.getMessage();
        };

        LangMessage notify = switch (mode) {
            case ADD -> Lang.COMMAND_AIR_ADD_NOTIFY.getMessage();
            case SET -> Lang.COMMAND_AIR_SET_NOTIFY.getMessage();
            case REMOVE -> Lang.COMMAND_AIR_REMOVE_NOTIFY.getMessage();
        };

        int current = target.getRemainingAir();
        int max = target.getMaximumAir();

        if (target != context.getSender()) {
            context.send(message
                .replace(Placeholders.GENERIC_CURRENT, String.valueOf(current))
                .replace(Placeholders.GENERIC_MAX, String.valueOf(max))
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
                .replace(Placeholders.forPlayer(target))
            );
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            notify
                .replace(Placeholders.GENERIC_CURRENT, String.valueOf(current))
                .replace(Placeholders.GENERIC_MAX, String.valueOf(max))
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
                .replace(Placeholders.forPlayer(target))
                .send(target);
        }

        return true;
    }
}
