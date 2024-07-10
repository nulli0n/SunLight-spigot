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
import su.nightexpress.nightcore.util.regex.TimedMatcher;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.nightcore.util.text.tag.TagPool;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.CommandPerms;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.command.CommandTools;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.user.SunUser;

import java.util.Set;
import java.util.regex.Pattern;

public class NickCommand {

    public static final String NODE_CHANGE = "nick_change";
    public static final String NODE_CLEAR  = "nick_clear";
    public static final String NODE_SET    = "nick_set";

    private static int         maxLength;
    private static int         minLength;
    private static Set<String> bannedWords;
    private static Pattern     regex;

    public static void load(@NotNull SunLightPlugin plugin) {
        CommandRegistry.registerDirectExecutor(NODE_CHANGE, (template, config) -> builderChange(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_CLEAR, (template, config) -> builderClear(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_SET, (template, config) -> builderSet(plugin, template, config));

        CommandRegistry.addTemplate("nick", CommandTemplate.group(new String[]{"nick"},
            "Nick commands.",
            CommandPerms.PREFIX + "nick",
            CommandTemplate.direct(new String[]{"change"}, NODE_CHANGE),
            CommandTemplate.direct(new String[]{"clear"}, NODE_CLEAR),
            CommandTemplate.direct(new String[]{"set"}, NODE_SET)
        ));
        CommandRegistry.addTemplate("changename", CommandTemplate.direct(new String[]{"changename"}, NODE_CHANGE));
        CommandRegistry.addTemplate("setnick", CommandTemplate.direct(new String[]{"setnick"}, NODE_SET));
        CommandRegistry.addTemplate("clearnick", CommandTemplate.direct(new String[]{"clearnick"}, NODE_CLEAR));
    }

    @NotNull
    private static String filterColors(@NotNull String name) {
        return NightMessage.stripTags(name, TagPool.BASE_COLORS_AND_STYLES);
    }

    @NotNull
    public static DirectNodeBuilder builderChange(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        minLength = ConfigValue.create("Settings.Nick.Min_Length",
            3,
            "Sets minimal allowed length for the '" + NODE_CHANGE + "' command.'"
        ).read(config);

        maxLength = ConfigValue.create("Settings.Nick.Max_Length",
            20,
            "Sets maximal allowed length for the '" + NODE_CHANGE + "' command.'"
        ).read(config);

        bannedWords = ConfigValue.create("Settings.Nick.Banned_Words",
            Set.of("admin", "ass", "dick", "nigga"),
            "A list of words that can not be used in the '" + NODE_CHANGE + "' command.'"
        ).read(config);

        regex = Pattern.compile(ConfigValue.create("Settings.Nick.Regex",
            "[a-zA-Zа-яА-Я0-9_\\s]*",
            "Sets regex pattern that custom nick must match to be used.",
            "By default it accepts all EN-RU characters, numbers, spaces and underscore."
        ).read(config));


        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(Lang.COMMAND_NICK_CHANGE_DESC)
            .permission(CommandPerms.NICK_CHANGE)
            .withArgument(ArgumentTypes.string(CommandArguments.NAME)
                .required()
                .complex()
                .localized(Lang.COMMAND_ARGUMENT_NAME_NAME)
            )
            .executes((context, arguments) -> executeChange(plugin, context, arguments))
            ;
    }

    public static boolean executeChange(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getExecutor();
        if (player == null) return false;

        String nick = arguments.getStringArgument(CommandArguments.NAME);

        String raw = NightMessage.stripAll(nick);
        if (!player.hasPermission(CommandPerms.NICK_BYPASS_LENGTH)) {
            if (raw.length() < minLength) {
                context.send(Lang.COMMAND_NICK_CHANGE_ERROR_TOO_SHORT.getMessage().replace(Placeholders.GENERIC_AMOUNT, minLength));
                return false;
            }
            if (raw.length() > maxLength) {
                context.send(Lang.COMMAND_NICK_CHANGE_ERROR_TOO_LONG.getMessage().replace(Placeholders.GENERIC_AMOUNT, maxLength));
                return false;
            }
        }

        if (!player.hasPermission(CommandPerms.NICK_BYPASS_WORDS)) {
            if (bannedWords.stream().anyMatch(word -> raw.toLowerCase().contains(word.toLowerCase()))) {
                context.send(Lang.COMMAND_NICK_CHANGE_ERROR_BAD_WORDS.getMessage());
                return false;
            }
            if (plugin.getServer().getPlayerExact(raw) != null) {
                context.send(Lang.COMMAND_NICK_CHANGE_ERROR_BAD_WORDS.getMessage());
                return false;
            }
        }

        if (!player.hasPermission(CommandPerms.NICK_BYPASS_REGEX)) {
            if (!TimedMatcher.create(regex, raw).matches()) {
                context.send(Lang.COMMAND_NICK_CHANGE_ERROR_REGEX.getMessage());
                return false;
            }
        }

        if (player.hasPermission(CommandPerms.NICK_COLORS)) {
            nick = filterColors(nick);
        }
        else nick = raw;

        SunUser user = plugin.getUserManager().getUserData(player);
        user.setCustomName(nick);
        user.updatePlayerName();
        plugin.getUserManager().scheduleSave(user);
        context.send(Lang.COMMAND_NICK_CHANGE_DONE.getMessage().replace(Placeholders.GENERIC_NAME, nick));

        return true;
    }

    @NotNull
    public static DirectNodeBuilder builderClear(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_NICK_CLEAR_DESC)
            .permission(CommandPerms.NICK_CLEAR)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(CommandPerms.NICK_CLEAR_OTHERS))
            .withFlag(CommandFlags.silent().permission(CommandPerms.NICK_CLEAR_OTHERS))
            .executes((context, arguments) -> executeClear(plugin, context, arguments))
            ;
    }

    public static boolean executeClear(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        SunUser user = plugin.getUserManager().getUserData(target);
        user.setCustomName(null);
        target.setDisplayName(null);
        plugin.getUserManager().scheduleSave(user);

        if (context.getSender() != target) {
            context.send(Lang.COMMAND_NICK_CLEAR_TARGET.getMessage().replace(Placeholders.forPlayer(target)));
        }
        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            Lang.COMMAND_NICK_CLEAR_NOTIFY.getMessage().send(target);
        }
        return true;
    }

    @NotNull
    public static DirectNodeBuilder builderSet(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_NICK_SET_DESC)
            .permission(CommandPerms.NICK_SET)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .withArgument(ArgumentTypes.string(CommandArguments.NAME)
                .required()
                .complex()
                .localized(Lang.COMMAND_ARGUMENT_NAME_NAME)
            )
            .withFlag(CommandFlags.silent())
            .executes((context, arguments) -> executeSet(plugin, context, arguments))
            ;
    }

    public static boolean executeSet(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        String nick = arguments.getStringArgument(CommandArguments.NAME);
        String raw = NightMessage.stripAll(nick);
        if (context.checkPermission(CommandPerms.NICK_COLORS)) {
            nick = filterColors(nick);
        }
        else nick = raw;

        SunUser user = plugin.getUserManager().getUserData(target);
        user.setCustomName(nick);
        user.updatePlayerName();
        plugin.getUserManager().scheduleSave(user);

        if (context.getSender() != user.getPlayer()) {
            context.send(Lang.COMMAND_NICK_SET_TARGET.getMessage()
                .replace(Placeholders.GENERIC_NAME, nick)
                .replace(Placeholders.PLAYER_NAME, user.getName())
            );
        }
        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            Lang.COMMAND_NICK_SET_NOTIFY.getMessage().replace(Placeholders.GENERIC_NAME, nick).send(target);
        }

        return true;
    }
}
