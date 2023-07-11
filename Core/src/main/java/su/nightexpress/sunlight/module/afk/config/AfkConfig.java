package su.nightexpress.sunlight.module.afk.config;

import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.api.lang.LangColors;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.EngineUtils;
import su.nexmedia.engine.utils.PlayerRankMap;
import su.nightexpress.sunlight.Placeholders;

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
        "Use '" + Placeholders.PLAYER_NAME + "' for player name.",
        "You can use " + EngineUtils.PLACEHOLDER_API + " here.");

    public static final JOption<Integer> AFK_COOLDOWN = JOption.create("AFK.Cooldown", 60,
        "Sets period of time (in seconds) while player will be unable to enter AFK mode for idleness after leaving it.");

    public static final JOption<PlayerRankMap<Integer>> AFK_IDLE_TIMES = new JOption<>("AFK.Idle_Time",
        (cfg, path, def) -> PlayerRankMap.read(cfg, path, Integer.class),
        new PlayerRankMap<>(Map.of(Placeholders.DEFAULT, 300)),
        "List of permission group based times (in seconds) for auto-enter AFK mode for idleness.",
        "Use '-1' to make players never auto-enter AFK mode."
    ).mapReader(map -> map.setNegativeBetter(true)).setWriter((cfg, path, map) -> map.write(cfg, path));

    public static final JOption<PlayerRankMap<Integer>> AFK_KICK_TIMES = new JOption<>("AFK.Kick_Time",
        (cfg, path, def) -> PlayerRankMap.read(cfg, path, Integer.class),
        new PlayerRankMap<>(Map.of(Placeholders.DEFAULT, 1200)),
        "List of permission group based times (in seconds) for kicking AFK mode players staying there too long.",
        "Use '-1' to make players never kick AFK players."
    ).mapReader(map -> map.setNegativeBetter(true)).setWriter((cfg, path, map) -> map.write(cfg, path));

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
        "Use '" + Placeholders.PLAYER_NAME + "' for player name.",
        "You can use " + EngineUtils.PLACEHOLDER_API + " here.");
}
