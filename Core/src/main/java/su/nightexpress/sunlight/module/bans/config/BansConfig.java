package su.nightexpress.sunlight.module.bans.config;

import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentReason;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;
import su.nightexpress.sunlight.module.bans.punishment.RankDuration;
import su.nightexpress.sunlight.module.bans.util.BansPerms;
import su.nightexpress.sunlight.module.bans.util.BanTime;
import su.nightexpress.sunlight.module.bans.util.Placeholders;

import java.util.*;

public class BansConfig {

    public static final JOption<Set<String>> GENERAL_IMMUNIES = JOption.create("General.Immunies",
        Set.of("admin_name", "127.0.0.2"),
        "A list of player names/IP addresses that will be immune to all punishments.");

    public static final JOption<Map<BanTime, Set<String>>> GENERAL_TIME_ALIASES = new JOption<Map<BanTime, Set<String>>>("General.Time_Aliases",
        (cfg, path, def) -> {
            Map<BanTime, Set<String>> aliasesMap = new HashMap<>();
            for (BanTime banTime : BanTime.values()) {
                String[] aliases = cfg.getString(path + "." + banTime.name(), "").split(",");
                aliasesMap.put(banTime, new HashSet<>(Arrays.asList(aliases)));
            }
            return aliasesMap;
        },
        () -> Map.of(
            BanTime.SECONDS, Set.of("s"),
            BanTime.MINUTES, Set.of("min"),
            BanTime.HOURS, Set.of("h"),
            BanTime.DAYS, Set.of("d"),
            BanTime.WEEKS, Set.of("w"),
            BanTime.MONTHS, Set.of("mon"),
            BanTime.YEARS, Set.of("y")
        ),
        "Custom shortcuts/aliases for the time units.",
        "Example: When punishing, you specify duration as '60min', in this case '60' is amount, 'min' is time alias (minutes)."
    ).setWriter((cfg, path, map) -> map.forEach((time, aliases) -> cfg.set(path + "." + time.name(), String.join(",", aliases))));

    public static final JOption<Map<String, PunishmentReason>> GENERAL_REASONS = new JOption<Map<String, PunishmentReason>>("General.Reasons",
        (cfg, path, def) -> {
            Map<String, PunishmentReason> reasonMap = new HashMap<>();
            for (String sId : cfg.getSection(path)) {
                PunishmentReason reason = PunishmentReason.read(cfg, path + "." + sId, sId);
                reasonMap.put(reason.getId(), reason);
            }
            return reasonMap;
        },
        () -> Map.of(
            Placeholders.DEFAULT, new PunishmentReason(Placeholders.DEFAULT, "Violation of the rules"),
            "advertisement", new PunishmentReason("advertisement", "Advertising other servers/websites"),
            "grief", new PunishmentReason("grief", "Griefing"),
            "toxic", new PunishmentReason("toxic", "Toxic behavior")
        ),
        "A list of all possible reasons for punishments.",
        "Use '" + Placeholders.DEFAULT + "' keyword for a default reason (when none specified)."
    ).setWriter((cfg, path, map) -> map.forEach((id, reason) -> reason.write(cfg, path + "." + id)));

    public static final JOption<List<String>> GENERAL_DISCONNECT_INFO_KICK = JOption.create("General.DisconnectInfo.Kick",
        Arrays.asList(
            "&c&nYou have been kicked from the server!",
            "&7",
            "&cReason: &e%punishment_reason%",
            "&cAdmin: &e%punishment_punisher%"
        ),
        "Text to display for player in disconnect window when kicked.").mapReader(Colorizer::apply);

    public static final JOption<List<String>> GENERAL_DISCONNECT_INFO_BAN_PERMANENT = JOption.create("General.DisconnectInfo.Ban.Permanent",
        Arrays.asList(
            "&cYou are banned from this server!",
            "&7",
            "&7Banned on: &f%punishment_date_created%",
            "&7Banned by: &f%punishment_punisher%",
            "&7Reason: &f%punishment_reason%",
            "&7",
            "&eYou are permanently banned!",
            "&7Appeal at: &f&nwww.myserver.com"
        ),
        "Text to display for player in disconnect window when kicked.").mapReader(Colorizer::apply);

    public static final JOption<List<String>> GENERAL_DISCONNECT_INFO_BAN_TEMP = JOption.create("General.DisconnectInfo.Ban.Temp",
        Arrays.asList(
            "&cYou are banned from this server!",
            "&7",
            "&7Banned on: &f%punishment_date_created%",
            "&7Banned by: &f%punishment_punisher%",
            "&7Reason: &f%punishment_reason%",
            "&7Unban in: &f%punishment_expires_in%",
            "&7",
            "&7Appeal at: &f&nwww.myserver.com"
        ),
        "Text to display for player in disconnect window when kicked.").mapReader(Colorizer::apply);

    public static final JOption<Set<String>> PUNISHMENTS_MUTE_BLOCKED_COMMANDS = JOption.create("Punishments.Mute.Blocked_Commands",
        Set.of("tell", "me", "broadcast"),
        "A list of commands that will be blocked for muted players.");

    public static final JOption<Map<String, Map<PunishmentType, RankDuration>>> PUNISHMENTS_RANK_MAX_TIMES = new JOption<Map<String, Map<PunishmentType, RankDuration>>>(
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
        () -> {
            Map<String, Map<PunishmentType, RankDuration>> times = new HashMap<>();

            Map<PunishmentType, RankDuration> durationMap1 = times.computeIfAbsent("moderator", k -> new HashMap<>());
            durationMap1.put(PunishmentType.MUTE, new RankDuration(1, BanTime.WEEKS));
            durationMap1.put(PunishmentType.BAN, new RankDuration(2, BanTime.MONTHS));
            durationMap1.put(PunishmentType.WARN, new RankDuration(3, BanTime.MONTHS));

            Map<PunishmentType, RankDuration> durationMap2 = times.computeIfAbsent("helper", k -> new HashMap<>());
            durationMap2.put(PunishmentType.MUTE, new RankDuration(1, BanTime.DAYS));
            durationMap2.put(PunishmentType.BAN, new RankDuration(2, BanTime.WEEKS));
            durationMap2.put(PunishmentType.WARN, new RankDuration(3, BanTime.WEEKS));

            return times;
        },
    "A list of rank based duration limits for each punishment type.",
        "(You must have Vault installed for this feature to work)",
        "This will prevent players to punish others for a time longer than you set here.",
        "Player permission groups will be auto-deetected",
        "The greatest duration limit will be used if player has multiple ranks with duration limits.",
        "To remove limit for certain punishment type, set its 'Duration' to -1.",
        "To remove all limits, give '" + BansPerms.BYPASS_DURATION_LIMIT.getName() + "' permission."
    ).setWriter((cfg, path, map) -> map.forEach((rank, times) -> times.forEach((type, time) -> time.write(cfg, path + "." + rank + "." + type.name()))));

    public static final JOption<Integer> PUNISHMENTS_WARN_MAX_AMOUNT = JOption.create("Punishments.Warn.Max_Amount", 5,
        "How many warns player can receive before they all will be auto expired?",
        "Set this to -1 for unlimit.");

    public static final JOption<Map<Integer, List<String>>> PUNISHMENTS_WARN_AUTO_COMMANDS = new JOption<Map<Integer, List<String>>>(
        "Punishments.Warn.Auto_Commands",
        (cfg, path, def) -> {
            Map<Integer, List<String>> map = new HashMap<>();
            for (String sId : cfg.getSection(path)) {
                int amount = StringUtil.getInteger(sId, -1);
                if (amount <= 0) continue;

                map.put(amount, cfg.getStringList(path + "." + sId));
            }
            return map;
        },
        () -> Map.of(
            3, Collections.singletonList("mute " + Placeholders.PLAYER_NAME + " 15min"),
            5, Collections.singletonList("kick " + Placeholders.PLAYER_NAME)
        ),
        "A list of commands to be executed when player got certain amount of warnings.",
        "Commands get executed from the server console.",
        "Use '" + Placeholders.PLAYER_NAME + "' placeholder for a player name."
    ).setWriter((cfg, path, map) -> map.forEach((amount, commands) -> cfg.set(path + "." + amount, commands)));
}
