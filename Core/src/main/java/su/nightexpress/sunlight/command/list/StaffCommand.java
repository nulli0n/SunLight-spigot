package su.nightexpress.sunlight.command.list;

import me.clip.placeholderapi.PlaceholderAPI;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.sunlight.Placeholders.*;

public class StaffCommand {

    public static final String NAME = "staff";

    private static List<String> ranks;
    private static List<String> listFormat;
    private static String       entryFormat;

    public static void load(@NotNull SunLightPlugin plugin) {
        CommandRegistry.registerDirectExecutor(NAME, (template, config) -> builder(plugin, template, config));
        CommandRegistry.addSimpleTemplate(NAME);
    }

    public static DirectNodeBuilder builder(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        ranks = ConfigValue.create("Settings.Staff.Ranks",
            Lists.newList("admin", "moderator", "helper"),
            "List of staff ranks to display players from for the '" + NAME + "' command.",
            "You must have " + Plugins.VAULT + " with compatible permissions plugins installed."
        ).read(config);

        listFormat = ConfigValue.create("Settings.Staff.ListFormat",
            Lists.newList(
                " ",
                LIGHT_YELLOW.enclose(BOLD.enclose("Staff Online:")),
                GENERIC_ENTRY,
                " ",
                LIGHT_GRAY.enclose("Total " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " staff online."),
                " "
            ),
            "Staff list format.",
            "Use '" + GENERIC_ENTRY + "' placeholder for a player list.",
            "Available text formations: " + WIKI_TEXT_URL
        ).read(config);

        entryFormat = ConfigValue.create("Settings.Staff.EntryFormat",
            LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("●") + " %vault_prefix%" +
                HOVER.encloseHint(CLICK.enclose(PLAYER_DISPLAY_NAME, ClickEventType.SUGGEST_COMMAND, "/msg " + PLAYER_NAME + " "), GRAY.enclose("Click to send private message."))
                + "%vault_suffix%"
            ),
            "Player entry format.",
            "Available text formations: " + WIKI_TEXT_URL,
            Plugins.PLACEHOLDER_API + " is supported here."
        ).read(config);

        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_STAFF_DESC)
            .permission(CommandPerms.STAFF)
            .executes((context, arguments) -> execute(plugin, context, arguments))
            ;
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player executor = context.getExecutor();

        Set<Player> staffs = new HashSet<>();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (executor != null && !executor.canSee(player)) continue;

            Set<String> playerRanks = Players.getPermissionGroups(player);
            if (playerRanks.stream().anyMatch(ranks::contains)) {
                staffs.add(player);
            }
        }

        if (staffs.isEmpty()) {
            context.send(Lang.COMMAND_STAFF_EMPTY.getMessage());
            return false;
        }

        for (String line : listFormat) {
            if (line.equalsIgnoreCase(GENERIC_ENTRY)) {
                staffs.forEach(player -> {
                    String entry = Placeholders.forPlayer(player).apply(entryFormat);
                    if (Plugins.hasPlaceholderAPI()) {
                        entry = PlaceholderAPI.setPlaceholders(player, entry);
                    }
                    Players.sendModernMessage(context.getSender(), entry);
                });
                continue;
            }
            Players.sendModernMessage(context.getSender(), line.replace(GENERIC_AMOUNT, NumberUtil.format(staffs.size())));
        }

        return true;
    }
}
