package su.nightexpress.sunlight.command.list;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
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

import java.util.function.BiConsumer;

public class ExperienceCommand {

    private static final String NODE_ADD    = "exp_add";
    private static final String NODE_SET    = "exp_set";
    private static final String NODE_REMOVE = "exp_remove";
    private static final String NODE_VIEW   = "exp_view";

    public static void load(@NotNull SunLightPlugin plugin) {
        CommandRegistry.registerDirectExecutor(NODE_ADD, (template, config) -> builderModify(plugin, template, config, Lang.COMMAND_EXPERIENCE_ADD_DESC, ModifyMode.ADD));
        CommandRegistry.registerDirectExecutor(NODE_SET, (template, config) -> builderModify(plugin, template, config, Lang.COMMAND_EXPERIENCE_SET_DESC, ModifyMode.SET));
        CommandRegistry.registerDirectExecutor(NODE_REMOVE, (template, config) -> builderModify(plugin, template, config, Lang.COMMAND_EXPERIENCE_REMOVE_DESC, ModifyMode.REMOVE));
        CommandRegistry.registerDirectExecutor(NODE_VIEW, (template, config) -> builderView(plugin, template, config));

        CommandRegistry.addTemplate("experience", CommandTemplate.group(new String[]{"experience", "exp", "xp"},
            "Experience commands.",
            CommandPerms.PREFIX + "experience",
            CommandTemplate.direct(new String[]{"add"}, NODE_ADD),
            CommandTemplate.direct(new String[]{"set"}, NODE_SET),
            CommandTemplate.direct(new String[]{"remove"}, NODE_REMOVE),
            CommandTemplate.direct(new String[]{"view"}, NODE_VIEW)
        ));
    }

    public static DirectNodeBuilder builderModify(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config,
                                                  @NotNull LangString description,
                                                  @NotNull ModifyMode mode) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(description)
            .permission(CommandPerms.EXPERIENCE_MODE.apply(mode))
            .withArgument(ArgumentTypes.integerAbs(CommandArguments.AMOUNT).localized(Lang.COMMAND_ARGUMENT_NAME_AMOUNT).required())
            .withArgument(CommandArguments.enumed(CommandArguments.TYPE, Type.class).required())
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(CommandPerms.EXPERIENCE_MODE_OTHERS.apply(mode)))
            .withFlag(CommandFlags.silent().permission(CommandPerms.EXPERIENCE_MODE.apply(mode)))
            .executes((context, arguments) -> executeModify(plugin, context, arguments, mode))
            ;
    }

    public static DirectNodeBuilder builderView(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_EXPERIENCE_VIEW_DESC)
            .permission(CommandPerms.EXPERIENCE_VIEW)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(CommandPerms.EXPERIENCE_VIEW_OTHERS))
            .withFlag(CommandFlags.silent().permission(CommandPerms.EXPERIENCE_VIEW_OTHERS))
            .executes((context, arguments) -> executeView(plugin, context, arguments))
            ;
    }

    public static boolean executeModify(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull ModifyMode mode) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        int amount = arguments.getIntArgument(CommandArguments.AMOUNT);
        Type type = arguments.getArgument(CommandArguments.TYPE, Type.class);
        LangText infoMessage;
        LangText notifyMessage;

        switch (mode) {
            case SET -> {
                setExperience(target, amount, type);

                infoMessage = Lang.COMMAND_EXPERIENCE_SET_INFO;
                notifyMessage = Lang.COMMAND_EXPERIENCE_SET_NOTIFY;
            }
            case ADD -> {
                addExperience(target, amount, type);

                infoMessage = Lang.COMMAND_EXPERIENCE_ADD_INFO;
                notifyMessage = Lang.COMMAND_EXPERIENCE_ADD_NOTIFY;
            }
            case REMOVE -> {
                addExperience(target, -amount, type);

                infoMessage = Lang.COMMAND_EXPERIENCE_REMOVE_INFO;
                notifyMessage = Lang.COMMAND_EXPERIENCE_REMOVE_NOTIFY;
            }
            default -> {
                return false;
            }
        }

        if (!target.isOnline()) target.saveData();

        if (target != context.getSender()) {
            infoMessage.getMessage()
                .replace(Placeholders.GENERIC_TYPE, Lang.EXP_TYPE.getLocalized(type))
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
                .replace(Placeholders.GENERIC_TOTAL, NumberUtil.format(target.getTotalExperience()))
                .replace(Placeholders.GENERIC_LEVEL, NumberUtil.format(target.getLevel()))
                .replace(Placeholders.forPlayer(target))
                .send(context.getSender());
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            notifyMessage.getMessage()
                .replace(Placeholders.GENERIC_TYPE, Lang.EXP_TYPE.getLocalized(type))
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
                .replace(Placeholders.GENERIC_TOTAL, NumberUtil.format(target.getTotalExperience()))
                .replace(Placeholders.GENERIC_LEVEL, NumberUtil.format(target.getLevel()))
                .send(target);
        }

        return true;
    }

    public static boolean executeView(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        Lang.COMMAND_EXPERIENCE_VIEW_INFO.getMessage()
            .replace(Placeholders.GENERIC_MAX, NumberUtil.format(target.getExpToLevel()))
            .replace(Placeholders.GENERIC_TOTAL, NumberUtil.format(target.getTotalExperience()))
            .replace(Placeholders.GENERIC_LEVEL, NumberUtil.format(target.getLevel()))
            .replace(Placeholders.forPlayer(target))
            .send(context.getSender());

        return true;
    }

    private static void addExperience(@NotNull Player player, int amount, @NotNull Type type) {
        type.add.accept(player, amount);
    }

    private static void setExperience(@NotNull Player player, int amount, @NotNull Type type) {
        type.set.accept(player, amount);
    }

    public enum Type {
        POINTS(
            Player::giveExp,
            (player, amount) -> {
                player.setTotalExperience(0);
                player.setLevel(0);
                player.setExp(0F);
                player.giveExp(amount);
            }
        ),
        LEVELS(
            Player::giveExpLevels,
            Player::setLevel
        );

        public final BiConsumer<Player, Integer> add;
        public final BiConsumer<Player, Integer> set;

        Type(@NotNull BiConsumer<Player, Integer> add, @NotNull BiConsumer<Player, Integer> set) {
            this.add = add;
            this.set = set;
        }
    }
}
