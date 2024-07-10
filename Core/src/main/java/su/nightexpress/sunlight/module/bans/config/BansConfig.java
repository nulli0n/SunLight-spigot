package su.nightexpress.sunlight.module.bans.config;

import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentReason;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;
import su.nightexpress.sunlight.module.bans.util.RankDuration;
import su.nightexpress.sunlight.module.bans.util.TimeUnit;

import java.util.*;

import static su.nightexpress.sunlight.module.bans.util.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class BansConfig {

    public static final ConfigValue<Set<String>> IMMUNE_LIST = ConfigValue.create("General.Immunies",
        Lists.newSet("put_here_your_staff_names", "127.0.0.1"),
        "A list of player names/IP addresses that are immune to all punishments. Case insensetive."
    );

    public static final ConfigValue<String> DEFAULT_REASON = ConfigValue.create("General.Default_Reason",
        DEFAULT,
        "Sets default reason to use when no other reason specified in punishment commands.",
        "Must be a valid reason name from the 'Reasons' section.",
        "If invalid reason provided, a reason with the 'NoReason' text field from the lang config will be used."
    );

    public static final ConfigValue<Map<String, PunishmentReason>> REASONS = ConfigValue.forMap("General.Reasons",
        (cfg, path, name) -> PunishmentReason.read(cfg, path + "." + name),
        (cfg, path, map) -> map.forEach((id, reason) -> reason.write(cfg, path + "." + id)),
        () -> {
            Map<String, PunishmentReason> map = new HashMap<>();
            map.put(DEFAULT, new PunishmentReason("Violation of the rules"));
            map.put("advertisement", new PunishmentReason("Advertising other servers/websites"));
            map.put("grief", new PunishmentReason("Griefing"));
            map.put("toxic", new PunishmentReason("Toxic behavior"));
            map.put("cheating", new PunishmentReason("Cheating"));
            return map;
        },
        "Add here punishment reasons you would like to use in all punishment commands."
    );

    public static final ConfigValue<List<String>> GENERAL_DISCONNECT_INFO_KICK = ConfigValue.create("General.DisconnectInfo.Kick",
        Lists.newList(
            LIGHT_RED.enclose(BOLD.enclose("Server")) + " " + DARK_GRAY.enclose("»") + " " + GRAY.enclose("You have been kicked!"),
            " ",
            LIGHT_RED.enclose("Reason") + " " + DARK_GRAY.enclose("»") + " " + GRAY.enclose(PUNISHMENT_REASON),
            LIGHT_RED.enclose("By") + " " + DARK_GRAY.enclose("»") + " " + GRAY.enclose(PUNISHMENT_PUNISHER)
        ),
        "Text to display for player in disconnect window when kicked."
    );

    public static final ConfigValue<List<String>> GENERAL_DISCONNECT_INFO_BAN_PERMANENT = ConfigValue.create("General.DisconnectInfo.Ban.Permanent",
        Lists.newList(
            LIGHT_RED.enclose(BOLD.enclose("Server")) + " " + DARK_GRAY.enclose("»") + " " + GRAY.enclose("You're permanently banned!"),
            " ",
            LIGHT_RED.enclose("Reason") + " " + DARK_GRAY.enclose("»") + " " + GRAY.enclose(PUNISHMENT_REASON),
            LIGHT_RED.enclose("Date") + " " + DARK_GRAY.enclose("»") + " " + GRAY.enclose(PUNISHMENT_CREATION_DATE),
            LIGHT_RED.enclose("By") + " " + DARK_GRAY.enclose("»") + " " + GRAY.enclose(PUNISHMENT_PUNISHER),
            " ",
            LIGHT_GREEN.enclose("Appeal at") + " " + DARK_GRAY.enclose("»") + " " + YELLOW.enclose(UNDERLINED.enclose("http://put_your_site.com"))
        ),
        "Text to display for player in disconnect window when kicked.");

    public static final ConfigValue<List<String>> GENERAL_DISCONNECT_INFO_BAN_TEMP = ConfigValue.create("General.DisconnectInfo.Ban.Temp",
        Lists.newList(
            LIGHT_RED.enclose(BOLD.enclose("Server")) + " " + DARK_GRAY.enclose("»") + " " + GRAY.enclose("You're banned!"),
            " ",
            LIGHT_RED.enclose("Reason") + " " + DARK_GRAY.enclose("»") + " " + GRAY.enclose(PUNISHMENT_REASON),
            LIGHT_RED.enclose("Date") + " " + DARK_GRAY.enclose("»") + " " + GRAY.enclose(PUNISHMENT_CREATION_DATE),
            LIGHT_RED.enclose("By") + " " + DARK_GRAY.enclose("»") + " " + GRAY.enclose(PUNISHMENT_PUNISHER),
            LIGHT_RED.enclose("Unban in") + " " + DARK_GRAY.enclose("»") + " " + GRAY.enclose(PUNISHMENT_EXPIRES_IN),
            " ",
            LIGHT_GREEN.enclose("Appeal at") + " " + DARK_GRAY.enclose("»") + " " + YELLOW.enclose(UNDERLINED.enclose("http://put_your_site.com"))
        ),
        "Text to display for player in disconnect window when kicked.");

    public static final ConfigValue<Set<String>> PUNISHMENTS_MUTE_BLOCKED_COMMANDS = ConfigValue.create("Punishments.Mute.Blocked_Commands",
        Set.of("tell", "me", "broadcast"),
        "List of disabled commands for muted players.",
        "All aliases for listed commands will be auto-detected and disabled too."
    );

    public static final ConfigValue<RankMap<Integer>> PUNISHMENTS_RANK_PRIORITY = ConfigValue.create("Punishments.Rank.Priority",
        (cfg, path, def) -> RankMap.readInt(cfg, path, 0),
        (cfg, path, map) -> map.write(cfg, path),
        () -> new RankMap<>(RankMap.Mode.RANK, "sunlight.bans.priority.", 0,
            Map.of(
                "helper", 1,
                "moderator", 2,
                "admin", 1000
            )
        ),
        "=".repeat(20) + " BETA FEATURE " + "=".repeat(20),
        "Set here priority for each rank that has access to punishment commands.",
        "Players with greatest ranks can not be punished by lowest ones.",
        "[WARNING] This setting will not prevent IP bans!"
    );

    public static final ConfigValue<Boolean> PUNISHMENTS_DURATION_LIMIT_TO_UNPUNISH = ConfigValue.create("Punishments.Rank.Duration_Limit_to_Unpunish",
        true,
        "When enabled, the 'Duration_Limit' settings will apply to all unpunish commands as well (unban, unmute, unwarn).",
        "Players can not remove punishments with a duration greater than set in player's limit."
    );

    public static final ConfigValue<Map<String, Map<PunishmentType, RankDuration>>> PUNISHMENTS_RANK_MAX_TIMES = ConfigValue.create(
        "Punishments.Rank.Duration_Limit",
        (cfg, path, def) -> {
            Map<String, Map<PunishmentType, RankDuration>> times = new HashMap<>();
            for (String rankId : cfg.getSection(path)) {
                for (PunishmentType type : PunishmentType.values()) {
                    RankDuration duration = RankDuration.read(cfg, path + "." + rankId + "." + type.name());
                    if (duration.getAmount() < 0) continue;

                    times.computeIfAbsent(rankId.toLowerCase(), k -> new HashMap<>()).put(type, duration);
                }
            }
            return times;
        },
        (cfg, path, map) -> map.forEach((rank, times) -> times.forEach((type, time) -> time.write(cfg, path + "." + rank + "." + type.name()))),
        () -> {
            Map<String, Map<PunishmentType, RankDuration>> times = new HashMap<>();

            Map<PunishmentType, RankDuration> durationMap1 = times.computeIfAbsent("moderator", k -> new HashMap<>());
            durationMap1.put(PunishmentType.MUTE, new RankDuration(1, TimeUnit.WEEKS));
            durationMap1.put(PunishmentType.BAN, new RankDuration(2, TimeUnit.MONTHS));
            durationMap1.put(PunishmentType.WARN, new RankDuration(3, TimeUnit.MONTHS));

            Map<PunishmentType, RankDuration> durationMap2 = times.computeIfAbsent("helper", k -> new HashMap<>());
            durationMap2.put(PunishmentType.MUTE, new RankDuration(1, TimeUnit.DAYS));
            durationMap2.put(PunishmentType.BAN, new RankDuration(2, TimeUnit.WEEKS));
            durationMap2.put(PunishmentType.WARN, new RankDuration(3, TimeUnit.WEEKS));

            return times;
        },
    "A list of rank based duration limits for each punishment type.",
        "(" + Plugins.VAULT + " and a compatible permissions plugins are required)",
        "(Player permission groups are auto-detected)",
        "This will prevent players to punish others for a time longer than you set here.",
        "'Amount' = amount of units for specified time unit. Example: 'TimeUnit' = '" + TimeUnit.DAYS.name() + "' and 'Amount' = '3' equals to 3 days.",
        "For the '" + TimeUnit.PERMANENT.name() + "' time unit, amount value does not matter.",
        "Available Time Units: " + StringUtil.inlineEnum(TimeUnit.class, ", "),
        "The greatest duration limit will be used if player has multiple ranks with duration limits.",
        "To remove limit for certain punishment type, set 'Amount' to -1.",
        "To remove all limits, give '" + BansPerms.BYPASS_DURATION_LIMIT.getName() + "' permission."
    );

    public static final ConfigValue<Integer> PUNISHMENTS_WARN_MAX_AMOUNT = ConfigValue.create("Punishments.Warn.Max_Amount",
        5,
        "Sets active warn limit for a player when all previous warns gets expired.",
        "Example: With default value 5, when player gets 5th active warn, all previous 4 active warns becomes expired.",
        "This setting is required for the 'Warns -> Auto_Commands' feature to work properly.",
        "Set to '0' or '-1' for unlimit."
    );

    public static final ConfigValue<Map<Integer, List<String>>> PUNISHMENTS_WARN_AUTO_COMMANDS = ConfigValue.forMap("Punishments.Warn.Auto_Commands",
        (sId) -> NumberUtil.getInteger(sId, 0),
        (cfg, path, sId) -> cfg.getStringList(path + "." + sId),
        (cfg, path, map) -> map.forEach((amount, commands) -> cfg.set(path + "." + amount, commands)),
        () -> Map.of(
            3, Lists.newList("mute " + PLAYER_NAME + " " + 15 + TimeUnit.MINUTES.getAliases()[0]),
            5, Lists.newList("kick " + PLAYER_NAME)
        ),
        "Executes custom commands (from console) when player reaches certain amount of active warns.",
        "Use '" + PLAYER_NAME + "' placeholder for a player name."
    );
}
