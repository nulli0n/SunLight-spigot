package su.nightexpress.sunlight.command.list;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.CommandPerms;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.CommandTools;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.core.user.IgnoredUser;
import su.nightexpress.sunlight.data.user.SunUser;

public class IgnoreCommands {

    public static final String NODE_ADD = "ignore_add";
    public static final String NODE_LIST = "ignore_list";
    public static final String NODE_REMOVE = "ignore_remove";

    public static final String DEF_LIST_ALIAS = "ignorelist";

    public static void load(@NotNull SunLightPlugin plugin) {
        CommandRegistry.registerDirectExecutor(NODE_ADD, (template, config) -> builderAdd(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_LIST, (template, config) -> builderList(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_REMOVE, (template, config) -> builderRemove(plugin, template, config));

        CommandRegistry.addTemplate("ignore", CommandTemplate.group(new String[]{"ignore"},
            "Ignore commands.",
            CommandPerms.PREFIX + "ignore",
            CommandTemplate.direct(new String[]{"add"}, NODE_ADD),
            CommandTemplate.direct(new String[]{"list"}, NODE_LIST),
            CommandTemplate.direct(new String[]{"remove"}, NODE_REMOVE)
        ));
        CommandRegistry.addTemplate("addignore", CommandTemplate.direct(new String[]{"addignore"}, NODE_ADD));
        CommandRegistry.addTemplate("ignorelist", CommandTemplate.direct(new String[]{"ignorelist"}, NODE_LIST));
        CommandRegistry.addTemplate("unignore", CommandTemplate.direct(new String[]{"unignore"}, NODE_LIST));
    }

    @NotNull
    public static DirectNodeBuilder builderAdd(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(Lang.COMMAND_IGNORE_ADD_DESC)
            .permission(CommandPerms.IGNORE_ADD)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .executes((context, arguments) -> executeAdd(plugin, context, arguments))
            ;
    }

    public static boolean executeAdd(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getExecutor();
        if (player == null) return false;

        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        if (player == target) {
            Lang.ERROR_COMMAND_NOT_YOURSELF.getMessage().send(player);
            return false;
        }

        SunUser user = plugin.getUserManager().getOrFetch(player);
        if (!user.addIgnoredUser(target)) {
            Lang.COMMAND_IGNORE_ADD_ERROR_ALREADY_IN.getMessage().send(player);
            return false;
        }
        plugin.getUserManager().save(user);

        Lang.COMMAND_IGNORE_ADD_DONE.getMessage().replace(Placeholders.forPlayer(target)).send(player);
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderList(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(Lang.COMMAND_IGNORE_LIST_DESC)
            .permission(CommandPerms.IGNORE_LIST)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(CommandPerms.IGNORE_LIST_OTHERS))
            .executes((context, arguments) -> executeList(plugin, context, arguments))
            ;
    }

    public static boolean executeList(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getExecutor();
        if (player == null) return false;

        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

//        if (player != target) {
        plugin.getUserManager().getIgnoreListMenu().open(player, target.getUniqueId());
//        }
//        else {
//            plugin.getUserManager().getIgnoreListMenu().open(player);
//        }
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderRemove(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(Lang.COMMAND_IGNORE_REMOVE_DESC)
            .permission(CommandPerms.IGNORE_REMOVE)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .executes((context, arguments) -> executeRemove(plugin, context, arguments))
            ;
    }

    public static boolean executeRemove(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getExecutor();
        if (player == null) return false;

        String userName = arguments.getStringArgument(CommandArguments.PLAYER);
        SunUser user = plugin.getUserManager().getOrFetch(player);

        IgnoredUser ignoredUser = user.getIgnoredUser(userName);
        if (ignoredUser == null) {
            Lang.COMMAND_IGNORE_REMOVE_ERROR_NOT_IN.getMessage().send(player);
            return false;
        }

        if (user.removeIgnoredUser(ignoredUser.getUserInfo().getId())) {
            plugin.getUserManager().save(user);
        }

        Lang.COMMAND_IGNORE_REMOVE_DONE.getMessage()
            .replace(Placeholders.PLAYER_NAME, ignoredUser.getUserInfo().getName())
            .send(player);
        return true;
    }
}
