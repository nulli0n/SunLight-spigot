package su.nightexpress.sunlight.module.afk.config;

import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.utils.Colorizer;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.config.LangColors;

import java.util.*;

public class AfkConfig {

    public static final JOption<Integer> WAKE_UP_THRESHOLD = JOption.create("WakeUp.Threshold", 3,
        "Sets how many interactions player have to do in order to leave AFK mode.");

    public static final JOption<Integer> WAKE_UP_TIMEOUT = JOption.create("WakeUp.Timeout", 5,
        "Sets period of time (in seconds) for player interactions (see above).",
        "Example: By default player have to do 3 interactions in 5 seconds to reset their AFK mode.");

    public static final JOption<List<String>> WAKE_UP_COMMANDS = JOption.create("WakeUp.Commands",
        Collections.emptyList(),
        "A list of commands to execute when player leaves AFK mode.",
        "Use '" + Placeholders.Player.NAME + "' for player name.",
        "You can use " + Hooks.PLACEHOLDER_API + " here.");

    public static final JOption<Integer> AFK_COOLDOWN = JOption.create("AFK.Cooldown", 60,
        "Sets period of time (in seconds) while player will be unable to enter AFK mode for idleness after leaving it.");

    public static final JOption<Map<String, Integer>> AFK_IDLE_TIMES = new JOption<>("AFK.Idle_Time",
        (cfg, path, def) -> {
            Map<String, Integer> map = new HashMap<>();
            for (String rank : cfg.getSection(path)) {
                int time = cfg.getInt(path + "." + rank);
                map.put(rank.toLowerCase(), time);
            }
            return map;
        },
        Map.of(Placeholders.DEFAULT, 300),
        "List of permission group based times (in seconds) for auto-enter AFK mode for idleness.",
        "Use '-1' to make players never auto-enter AFK mode."
    ).setWriter((cfg, path, map) -> map.forEach((rank, time) -> cfg.set(path + "." + rank, time)));

    public static final JOption<Map<String, Integer>> AFK_KICK_TIMES = new JOption<>("AFK.Kick_Time",
        (cfg, path, def) -> {
            Map<String, Integer> map = new HashMap<>();
            for (String rank : cfg.getSection(path)) {
                int time = cfg.getInt(path + "." + rank);
                map.put(rank.toLowerCase(), time);
            }
            return map;
        },
        Map.of(Placeholders.DEFAULT, 1200),
        "List of permission group based times (in seconds) for kicking AFK mode players staying there too long.",
        "Use '-1' to make players never kick AFK players."
    ).setWriter((cfg, path, map) -> map.forEach((rank, time) -> cfg.set(path + "." + rank, time)));

    public static final JOption<List<String>> AFK_KICK_MESSAGE = JOption.create("AFK.Kick_Message",
        Arrays.asList(
            LangColors.RED + "You have been kicked for being AFK too long!",
            "",
            LangColors.LIGHT_YELLOW + "&nYou can rejoin now."
        ),
        "Message displayed to player on disconnect window when kicked for being AFK long enough.").mapReader(Colorizer::apply);

    public static final JOption<List<String>> AFK_COMMANDS = JOption.create("AFK.Commands",
        Collections.emptyList(),
        "A list of commands to execute when player enters AFK mode.",
        "Use '" + Placeholders.Player.NAME + "' for player name.",
        "You can use " + Hooks.PLACEHOLDER_API + " here.");
}
