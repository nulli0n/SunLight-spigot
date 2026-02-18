package su.nightexpress.sunlight.module.bans.config;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.configuration.AbstractConfig;
import su.nightexpress.nightcore.configuration.ConfigProperty;
import su.nightexpress.nightcore.configuration.ConfigType;
import su.nightexpress.nightcore.configuration.ConfigTypes;
import su.nightexpress.nightcore.integration.permission.PermissionPlugins;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.sunlight.SLLinks;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentReason;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;
import su.nightexpress.sunlight.module.bans.time.BanTime;
import su.nightexpress.sunlight.module.bans.time.BanTimeUnit;

import java.util.*;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.module.bans.BansPlaceholders.*;

public class BansSettings extends AbstractConfig {

    private static final ConfigType<BanTime> DURATION_LIMIT_CONFIG_TYPE = ConfigType.of(
        (config, path) -> Optional.ofNullable(config.getString(path)).map(BanTime::parse).orElse(null),
        (config, path, value) -> config.set(path, value.serialize())
    );

    private static final ConfigType<PunishmentReason> PUNISHMENT_REASON_CONFIG_TYPE = ConfigType.of(
        PunishmentReason::read,
        FileConfig::set
    );

    public ConfigProperty<String> dataTablePrefix = this.addProperty(ConfigTypes.STRING, "Data.Table-Prefix",
        "sunlight_bans",
        "Sets the prefix for the database table.",
        "[Case #1] Use different prefixes on different servers to isolate data from one another.",
        "[Case #2] Use the same prefix across multiple servers to synchronize data between them."
    );

    public ConfigProperty<Integer> dataSaveInterval = this.addProperty(ConfigTypes.INT, "Data.Save-Interval",
        10,
        "Sets how often (in seconds) changes to punishment data will be written to the database.",
        "This applies, for example, when the punishment status is changed through the GUI."
    );

    public final ConfigProperty<Integer> consolePriority = this.addProperty(ConfigTypes.INT, "Priority.Console",
        10_000,
        "Sets the priority for punishment operations executed through the console.",
        "This allows console to kick, ban, mute and warn players whose rank priority is under the console's one."
    );

    public final ConfigProperty<Map<String, Integer>> rankPriorities = this.addProperty(ConfigTypes.forMapWithLowerKeys(ConfigTypes.INT), "Priority.Ranks",
        Map.of(
            "moderator", 50,
            "admin", 100,
            "owner", 999
        ),
        "Sets the priority for punishment operations executed by players based on their rank (aka permission group).",
        "This allows players to kick, ban, mute and warn other players with smaller rank priority.",
        "The greatest value is always used.",
        "[*] Requires LuckPerms for the best experience. If LuckPerms is not installed, this feature will not work properly for offline players."
    );

    public final ConfigProperty<Map<BanTimeUnit, String[]>> timeUnitAliases = this.addProperty(
        ConfigTypes.forMap(string -> Enums.get(string, BanTimeUnit.class), unit -> LowerCase.INTERNAL.apply(unit.name()), ConfigTypes.STRING_ARRAY),
        "Time.Unit-Aliases",
        getDefaultAliases(),
        "Here you can specify custom aliases (right side only) for time units used in punishment commands. Split with comma."
    );

    public final ConfigProperty<Map<String, Map<PunishmentType, BanTime>>> durationLimitsRanks = this.addProperty(
        ConfigTypes.forMap(LowerCase.INTERNAL::apply, s -> s, ConfigTypes.forMap(string -> Enums.get(string, PunishmentType.class), Enum::name, DURATION_LIMIT_CONFIG_TYPE)),
        "Time.Duration-Limits.Ranks",
        getDefaultDurationLimits(),
        "Here you can set the maximum allowed time limit for each type of punishment, based on the rank of the player issuing the punishment.",
        "If a player belongs to multiple ranks, the highest available limit will be selected for them.",
        "- Limit Format: [AMOUNT] [TIME UNIT], for example: 4 %s".formatted(BanTimeUnit.DAYS.name()),
        "- Available Time Units: [%s]".formatted(Enums.inline(BanTimeUnit.class)),
        "- Available Punishment Types: [%s]".formatted(Enums.inline(PunishmentType.class)),
        "[*] To remove the limit for a specific punishment type, use -1 without a time unit.",
        "[*] To remove all limits for a specific group, completely delete that group's section from the file",
        "[*] To bypass all limits, give '%s' permission.".formatted(BansPerms.BYPASS_DURATION_LIMIT.getName()),
        "[*] Requires %s with a compatible permissions plugin OR %s to work.".formatted(PermissionPlugins.VAULT, PermissionPlugins.LUCK_PERMS)
    );

    public final ConfigProperty<Boolean> durationLimitsForPardon = this.addProperty(ConfigTypes.BOOLEAN, "Time.Duration-Limits.Include-For-Pardon",
        true,
        "Controls whether duration limit settings above should apply to pardon commands (/unban, /unmute, /unwarn).",
        "This will prevent players to /unban, /unmute or /unwarn other players if other player's remaining punishment time is greater than the one available by the limit settings."
    );

    public final ConfigProperty<Set<String>> immunityList = this.addProperty(ConfigTypes.STRING_SET_LOWER_CASE, "Security.Immunity-List",
        Set.of("add_your_names_or_uuids_or_ips", "127.0.0.1", "0.0.0.0"),
        "List of player names/IPs/UUIDs that are immune to all punishments no matter what."
    );

    public final ConfigProperty<Map<String, PunishmentReason>> reasonMap = this.addProperty(
        ConfigTypes.forMapWithLowerKeys(PUNISHMENT_REASON_CONFIG_TYPE),
        "General.Reasons",
        getDefaultReasons(),
        "Define here punishment reasons to use in the punishment commands."
    );

    public final ConfigProperty<List<String>> disconnectScreenKick = this.addProperty(ConfigTypes.STRING_LIST, "Disconnect-Screen.Kick",
        List.of(
            RED.and(BOLD).wrap("YOU HAVE BEEN KICKED"),
            "",
            WHITE.wrap("\"" + PUNISHMENT_REASON + "\"")
        ),
        "Text to display in disconnect window for kicked players."
    );

    public final ConfigProperty<List<String>> disconnectScreenPermaBan = this.addProperty(ConfigTypes.STRING_LIST, "Disconnect-Screen.Permanent-Ban",
        Lists.newList(
            SPRITE.apply("items", "item/barrier") + RED.and(BOLD).wrap(" YOU ARE PERMANENTLY BANNED ") + SPRITE.apply("items", "item/barrier"),
            "",
            WHITE.wrap("You " + GRAY.wrap("(" + YELLOW.wrap(PUNISHMENT_TARGET) + ")") + " have been permanently banned " + YELLOW.wrap(PUNISHMENT_CREATION_DATE) + " with the following reason: " + SOFT_RED.wrap("\"" + PUNISHMENT_REASON + "\"") + "."),
            "",
            WHITE.wrap("If you believe this happened by mistake, you can appeal the ban here: " + GREEN.wrap("< INSERT YOUR URL >"))
        ),
        "Text to display in disconnect window for permanently banned players."
    );

    public final ConfigProperty<List<String>> disconnectScreenTempBan = this.addProperty(ConfigTypes.STRING_LIST, "Disconnect-Screen.Temp-Ban",
        Lists.newList(
            SPRITE.apply("items", "item/barrier") + RED.and(BOLD).wrap(" YOU ARE TEMPORARY BANNED ") + SPRITE.apply("items", "item/barrier"),
            "",
            WHITE.wrap("You " + GRAY.wrap("(" + YELLOW.wrap(PUNISHMENT_TARGET) + ")") + " have been temporary banned " + YELLOW.wrap(PUNISHMENT_CREATION_DATE) + " with the following reason: " + SOFT_RED.wrap("\"" + PUNISHMENT_REASON + "\"") + "."),
            "",
            WHITE.wrap("You will be unbanned " + GOLD.wrap(PUNISHMENT_EXPIRATION_DATE) + GRAY.wrap(" (in " + PUNISHMENT_EXPIRES_IN + ")")),
            "",
            WHITE.wrap("If you believe this happened by mistake, you can appeal the ban here: " + GREEN.wrap("< INSERT YOUR URL >"))
        ),
        "Text to display in disconnect window for temporary banned players."
    );

    public final ConfigProperty<List<String>> disconnectScreenIpBan = this.addProperty(ConfigTypes.STRING_LIST, "Disconnect-Screen.IP-Ban",
        List.of(
            SPRITE.apply("items", "item/barrier") + RED.and(BOLD).wrap(" YOUR IP ADDRESS IS BANNED ") + SPRITE.apply("items", "item/barrier"),
            "",
            WHITE.wrap("Your IP address " + GRAY.wrap("(" + YELLOW.wrap(PUNISHMENT_TARGET) + ")") + " has been banned " + YELLOW.wrap(PUNISHMENT_CREATION_DATE) + " with the following reason: " + SOFT_RED.wrap("\"" + PUNISHMENT_REASON + "\"") + "."),
            "",
            WHITE.wrap("If you believe this happened by mistake, you can appeal the ban here: " + GREEN.wrap("< INSERT YOUR URL >"))
        ),
        "Text to display in disconnect window for players with banned IPs."
    );

    public final ConfigProperty<Boolean> altDetectorEnabled = this.addProperty(ConfigTypes.BOOLEAN, "AltDetector.Enabled",
        true,
        "Controls whether Alt Detector feature is enabled."
    );

    public final ConfigProperty<Boolean> muteBlockCommandsEnabled = this.addProperty(ConfigTypes.BOOLEAN, "Mute.Block-Commands.Enabled",
        true,
        "Controls whether muted players will be restricted from using certain commands."
    );

    public final ConfigProperty<Set<String>> muteBlockCommandsList = this.addProperty(ConfigTypes.STRING_SET, "Mute.Block-Commands.List",
        Set.of("me", "broadcast"),
        "Commands to block for muted players (without the '/' slash)."
    );

    public final ConfigProperty<Integer> warnMaxAmountToReset = this.addProperty(ConfigTypes.INT, "Warn.Max-Amount-To-Reset",
        5,
        "Sets the warn limit for a player.",
        "When this limit is reached, all active warnings are automatically reset.",
        "This setting is required for the 'Commands-For-Active-Warns' setting to function correctly.",
        "[*] Set to '-1' to disable."
    );

    public final ConfigProperty<Map<Integer, List<String>>> warnAutoCommands = this.addProperty(
        ConfigTypes.forMap(key -> Numbers.parseInteger(key).orElse(null), String::valueOf, ConfigTypes.STRING_LIST),
        "Warn.Commands-For-Active-Warns",
        Map.of(
            3, Lists.newList("smite " + PLAYER_NAME),
            5, Lists.newList("kick " + PLAYER_NAME)
        ),
        "Runs the listed commands from the console when a player reaches a certain number of warns.",
        "[*] Placeholders: '%s', '%s'".formatted(PLAYER_NAME, "Punishment (" + SLLinks.WIKI_PLACEHOLDERS + ")")
    );

    public boolean isAltCheckerEnabled() {
        return this.altDetectorEnabled.get();
    }

    @NotNull
    private static Map<BanTimeUnit, String[]> getDefaultAliases() {
        Map<BanTimeUnit, String[]> aliases = new LinkedHashMap<>();

        for (BanTimeUnit unit : BanTimeUnit.values()) {
            String[] names = switch (unit) {
                case SECONDS -> new String[]{"sec", "s"};
                case MINUTES -> new String[]{"min"};
                case HOURS -> new String[]{"hour", "h"};
                case DAYS -> new String[]{"day", "d"};
                case WEEKS -> new String[]{"week", "w"};
                case MONTHS -> new String[]{"mon"};
                case YEARS -> new String[]{"year", "y"};
            };

            aliases.put(unit, names);
        }

        return aliases;
    }

    @NotNull
    private static Map<String, Map<PunishmentType, BanTime>> getDefaultDurationLimits() {
        Map<String, Map<PunishmentType, BanTime>> map = new LinkedHashMap<>();

        Map<PunishmentType, BanTime> durationMap1 = map.computeIfAbsent("moderator", k -> new LinkedHashMap<>());
        durationMap1.put(PunishmentType.MUTE, BanTime.temporary(BanTimeUnit.WEEKS, 1));
        durationMap1.put(PunishmentType.BAN, BanTime.temporary(BanTimeUnit.MONTHS, 2));
        durationMap1.put(PunishmentType.WARN, BanTime.temporary(BanTimeUnit.MONTHS, 3));

        Map<PunishmentType, BanTime> durationMap2 = map.computeIfAbsent("helper", k -> new LinkedHashMap<>());
        durationMap2.put(PunishmentType.MUTE, BanTime.temporary(BanTimeUnit.DAYS, 1));
        durationMap2.put(PunishmentType.BAN, BanTime.temporary(BanTimeUnit.WEEKS, 2));
        durationMap2.put(PunishmentType.WARN, BanTime.temporary(BanTimeUnit.WEEKS, 3));

        return map;
    }

    @NotNull
    private static Map<String, PunishmentReason> getDefaultReasons() {
        Map<String, PunishmentReason> map = new HashMap<>();
        map.put(DEFAULT, new PunishmentReason("Violation of the rules"));
        map.put("advertisement", new PunishmentReason("Advertising other servers/websites"));
        map.put("grief", new PunishmentReason("Griefing"));
        map.put("toxic", new PunishmentReason("Toxic behavior"));
        map.put("cheating", new PunishmentReason("Cheating"));
        return map;
    }
}
