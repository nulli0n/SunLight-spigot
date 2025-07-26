package su.nightexpress.sunlight.command.list;

import me.clip.placeholderapi.PlaceholderAPI;
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
import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandPerms;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.command.CommandTools;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.user.SunUser;

import java.util.ArrayList;
import java.util.List;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.sunlight.Placeholders.*;

public class PlayerInfoCommand {

    public static final String NAME = "playerinfo";

    private static final String INET_ADDRESS = "%ip%";
    private static final String LAST_JOIN    = "%last_join%";
    private static final String IS_ONLINE    = "%is_online%";
    private static final String GAME_MODE    = "%game_mode%";
    private static final String CAN_FLY      = "%can_fly%";
    private static final String FOOD_LEVEL   = "%food_level%";
    private static final String SATURATION   = "%saturation%";
    private static final String HEALTH       = "%health%";
    private static final String MAX_HEALTH   = "%max_health%";

    private static List<String> format;

    public static void load(@NotNull SunLightPlugin plugin) {
        CommandRegistry.registerDirectExecutor(NAME, (template, config) -> builder(plugin, template, config));
        CommandRegistry.addTemplate(NAME, CommandTemplate.direct(new String[]{NAME, "pinfo", "seen"}, NAME));
    }

    public static DirectNodeBuilder builder(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        format = ConfigValue.create("Settings.PlayerInfo.Format",
            Lists.newList(
                " ",
                LIGHT_YELLOW.enclose(BOLD.enclose(PLAYER_NAME + " Info:")),
                " ",
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Real Name: ") + PLAYER_NAME),
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Display Name: ") + PLAYER_DISPLAY_NAME),
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Online: ") + IS_ONLINE),
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Last Join: ") + LAST_JOIN),
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Last IP: ") + INET_ADDRESS),
                " ",
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Location: ") + LOCATION_X + ", " + LOCATION_Y + ", " + LOCATION_Z + " in " + LOCATION_WORLD),
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Game Mode: ") + GAME_MODE),
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Can Fly: ") + CAN_FLY),
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Food Level: ") + FOOD_LEVEL + " " + LIGHT_GREEN.enclose("+" + SATURATION)),
                LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Health: ") + HEALTH + LIGHT_GRAY.enclose("/") + MAX_HEALTH),
                " "
            ),
            "Player info format.",
            Plugins.PLACEHOLDER_API + " is supported here.",
            "Text formations: " + WIKI_TEXT_URL
        ).read(config);

        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_PLAYER_INFO_DESC)
            .permission(CommandPerms.PLAYER_INFO)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .executes((context, arguments) -> execute(plugin, context, arguments))
            ;
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        SunUser user = plugin.getUserManager().getOrFetch(target);

        List<String> format = new ArrayList<>(PlayerInfoCommand.format);
        if (Plugins.hasPlaceholderAPI()) format.replaceAll(str -> PlaceholderAPI.setPlaceholders(target, str));

        PlaceholderMap placeholderMap = new PlaceholderMap()
            .add(INET_ADDRESS, user::getInetAddress)
            .add(LAST_JOIN, () -> TimeUtil.formatDuration(user.getLastOnline(), System.currentTimeMillis()))
            .add(LOCATION_X, () -> NumberUtil.format(target.getLocation().getX()))
            .add(LOCATION_Y, () -> NumberUtil.format(target.getLocation().getY()))
            .add(LOCATION_Z, () -> NumberUtil.format(target.getLocation().getZ()))
            .add(LOCATION_WORLD, () -> LangAssets.get(target.getWorld()))
            .add(IS_ONLINE, () -> Lang.getYesOrNo(target.isOnline()))
            .add(CAN_FLY, () -> Lang.getYesOrNo(target.getAllowFlight()))
            .add(FOOD_LEVEL, () -> NumberUtil.format(target.getFoodLevel()))
            .add(SATURATION, () -> NumberUtil.format(target.getSaturation()))
            .add(MAX_HEALTH, () -> NumberUtil.format(EntityUtil.getAttribute(target, Attribute.MAX_HEALTH)))
            .add(HEALTH, () -> NumberUtil.format(target.getHealth()))
            .add(GAME_MODE, () -> Lang.GAME_MODE.getLocalized(target.getGameMode()))
            .add(PLAYER_DISPLAY_NAME, target::getDisplayName)
            .add(PLAYER_NAME, target::getName)
            ;
        format.replaceAll(placeholderMap.replacer());
        format.forEach(line -> Players.sendModernMessage(context.getSender(), line));

        return true;
    }
}
