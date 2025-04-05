package su.nightexpress.sunlight.command.list;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.bridge.wrapper.ClickEventType;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandPerms;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.config.Lang;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.sunlight.Placeholders.*;

public class NearCommand {

    public static final String NAME = "near";

    private static int          radius;
    private static List<String> listFormat;
    private static String       entryFormat;

    public static void load(@NotNull SunLightPlugin plugin) {
        CommandRegistry.registerDirectExecutor(NAME, (template, config) -> builder(plugin, template, config));
        CommandRegistry.addSimpleTemplate(NAME);
    }

    public static DirectNodeBuilder builder(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        radius = ConfigValue.create("Settings.Near.Radius",
            100,
            "Sets radius for the '" + NAME + "' command."
        ).read(config);

        listFormat = ConfigValue.create("Settings.Near.Format",
            Lists.newList(
                " ",
                LIGHT_YELLOW.wrap(BOLD.wrap("Nearby Players:")),
                GENERIC_ENTRY,
                " "
            ),
            "List format for the '" + NAME + "' command.",
            "Use '" + GENERIC_RADIUS + "' placeholder for command radius value.",
            "Available text formations: " + URL_WIKI_TEXT
        ).read(config);

        entryFormat = ConfigValue.create("Settings.Near.EntryFormat",
            LIGHT_GRAY.wrap(LIGHT_YELLOW.wrap("●") + " %vault_prefix%" +
                HOVER.wrapShowText(CLICK.wrap(PLAYER_DISPLAY_NAME, ClickEventType.SUGGEST_COMMAND, "/ptp request " + PLAYER_NAME), GRAY.wrap("Click to send teleport request."))
                + "%vault_suffix%" + " " + GRAY.wrap("(" + WHITE.wrap(GENERIC_AMOUNT) + " blocks away)")
            ),
            "Player entry format.",
            "Available text formations: " + URL_WIKI_TEXT,
            Plugins.PLACEHOLDER_API + " is supported here."
        ).read(config);

        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(Lang.COMMAND_NEAR_DESC)
            .permission(CommandPerms.NEAR)
            .executes((context, arguments) -> execute(plugin, context, arguments))
            ;
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getExecutor();
        if (player == null) return false;

        Map<Player, Double> playerMap = new HashMap<>();
        Location location = player.getLocation();

        player.getNearbyEntities(radius, radius, radius).forEach(entity -> {
            if (!(entity instanceof Player nearby)) return;
            if (!player.canSee(nearby)) return;

            playerMap.put(nearby, location.distance(nearby.getLocation()));
        });

        if (playerMap.isEmpty()) {
            Lang.COMMAND_NEAR_NOTHING.getMessage().replace(GENERIC_RADIUS, radius).send(player);
            return true;
        }

        for (String line : listFormat) {
            if (line.contains(GENERIC_ENTRY)) {
                playerMap.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(mapEntry -> {
                    Player nearPlayer = mapEntry.getKey();
                    double distance = mapEntry.getValue();

                    String entry = Placeholders.forPlayer(nearPlayer).apply(entryFormat).replace(GENERIC_AMOUNT, NumberUtil.format(distance));
                    if (Plugins.hasPlaceholderAPI()) {
                        entry = PlaceholderAPI.setPlaceholders(nearPlayer, entry);
                    }
                    Players.sendModernMessage(player, entry);
                });
                continue;
            }
            Players.sendModernMessage(player, line.replace(GENERIC_RADIUS, NumberUtil.format(radius)));
        }

        return true;
    }
}
