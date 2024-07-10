package su.nightexpress.sunlight.module.afk.config;

import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.RankMap;

import java.util.*;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.sunlight.Placeholders.*;

public class AfkConfig {

    /*public static final ConfigValue<Long> AFK_INSPECTION_INTERVAL = ConfigValue.create("AFK.Inspection_Interval",
        20L,
        "Sets how often (in ticks, 20 ticks = 1 second) plugin will inspect players for afk state.",
        "Setting this to high values (100+) may result in inaccurate afk state transition.",
        "[Asynchronous]",
        "[Default is 20]"
    );*/

    public static final ConfigValue<Integer> WAKE_UP_THRESHOLD = ConfigValue.create("WakeUp.Threshold",
        3,
        "Sets how many activities player have to do in order to leave AFK mode.",
        "You can configure how much points every activity produces below."
    );

    public static final ConfigValue<Integer> WAKE_UP_TIMEOUT = ConfigValue.create("WakeUp.Timeout",
        5,
        "Sets period of time (in seconds) for player interactions (see above).",
        "Example: By default player have to do 3 interactions in 5 seconds to reset their AFK mode."
    );

    public static final ConfigValue<Integer> WAKE_UP_ACTIVITY_POINTS_MOVEMENT = ConfigValue.create("WakeUp.ActivityPoints.Movement",
        1,
        "Sets how much activity points are produced by player movement."
    );

    public static final ConfigValue<Integer> WAKE_UP_ACTIVITY_POINTS_CHAT = ConfigValue.create("WakeUp.ActivityPoints.Chat",
        5,
        "Sets how much activity points are produced by player chat messages."
    );

    public static final ConfigValue<Integer> WAKE_UP_ACTIVITY_POINTS_COMMAND = ConfigValue.create("WakeUp.ActivityPoints.Command",
        0,
        "Sets how much activity points are produced by player commands."
    );

    public static final ConfigValue<Integer> WAKE_UP_ACTIVITY_POINTS_INTERACTION = ConfigValue.create("WakeUp.ActivityPoints.Interaction",
        1,
        "Sets how much activity points are produced by player interactions with items, blocks or entities."
    );

    public static final ConfigValue<List<String>> WAKE_UP_COMMANDS = ConfigValue.create("WakeUp.Commands",
        Collections.emptyList(),
        "A list of commands to execute when player leaves AFK mode.",
        "Use '" + PLAYER_NAME + "' for player name.",
        Plugins.PLACEHOLDER_API + " is also supported."
    );

    public static final ConfigValue<Integer> AFK_COOLDOWN = ConfigValue.create("AFK.Cooldown",
        60,
        "Sets auto AFK mode cooldown.",
        "This will prevent players from being moved into afk mode back soon after they leave it.",
        "This setting does NOT affect manual afk transition using commands."
    );

    public static final ConfigValue<String> AFK_PLACEHOLDER_IN = ConfigValue.create("AFK.Placeholder.In",
        LIGHT_GRAY.enclose(" AFK " + GENERIC_TIME),
        "Custom placeholder text for afk players.",
        "Used by %sunlight_afk_mode% and %sunlight_afk_since% placeholders.",
        "Use '" + GENERIC_TIME + "' to display AFK time."
    );

    public static final ConfigValue<String> AFK_PLACEHOLDER_OUT = ConfigValue.create("AFK.Placeholder.Out",
        "",
        "Custom placeholder text for non-afk players.",
        "Used by %sunlight_afk_mode% and %sunlight_afk_since% placeholders."
    );

    public static final ConfigValue<RankMap<Integer>> AFK_IDLE_TIMES = ConfigValue.create("AFK.Idle_Time",
        (cfg, path, def) -> RankMap.readInt(cfg, path, 300),
        (cfg, path, map) -> map.write(cfg, path),
        () -> new RankMap<>(
            RankMap.Mode.RANK,
            "sunlight.afk.idletime.",
            300,
            Map.of("vip", 600, "gold", 900)
        ),
        "Here you can setup auto-afk times (in seconds) based on permissions or permission groups.",
        "When player stays inactive at the same place, their idle time increased by 1.",
        "When idle time reaches suitable value (that is > 0) from the list below, player will be moved to AFK mode.",
        "Use '-1' to make players never be moved into AFK mode for idleness."
    );

    public static final ConfigValue<RankMap<Integer>> AFK_KICK_TIMES = ConfigValue.create("AFK.Kick_Time",
        (cfg, path, def) -> RankMap.readInt(cfg, path, 1200),
        (cfg, path, map) -> map.write(cfg, path),
        () -> new RankMap<>(
            RankMap.Mode.RANK,
            "sunlight.afk.kicktime.",
            1200,
            Map.of("vip", 1500, "gold", 1800, "admin", -1)
        ),
        "Here you can setup afk kick times based on permissions or permission groups.",
        "When player stays in AFK, their idle time is also increased by 1 every second.",
        "When idle time reaches suitable value (that is > 0) from the list below, player will be kicked from the server.",
        "Use '-1' to make players never be kicked for being AFK."
    );

    public static final ConfigValue<List<String>> AFK_KICK_MESSAGE = ConfigValue.create("AFK.Kick_Message",
        Arrays.asList(
            LIGHT_RED.enclose("You have been kicked for being AFK too long: " + LIGHT_ORANGE.enclose(GENERIC_TIME)),
            "",
            LIGHT_GREEN.enclose(UNDERLINED.enclose("You can join back now."))
        ),
        "Message displayed to player on disconnect window when kicked for being AFK long enough.",
        "Use '" + GENERIC_TIME + "' placeholder for a formatted kick time."
    );

    public static final ConfigValue<List<String>> AFK_COMMANDS = ConfigValue.create("AFK.Commands",
        Collections.emptyList(),
        "A list of commands to execute when player enters AFK mode.",
        "Use '" + PLAYER_NAME + "' for player name.",
        Plugins.PLACEHOLDER_API + " is also supported."
    );
}
