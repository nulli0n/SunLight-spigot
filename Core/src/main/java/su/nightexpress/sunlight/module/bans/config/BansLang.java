package su.nightexpress.sunlight.module.bans.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.language.entry.LangEnum;
import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.entry.LangText;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.bans.menu.SortMode;
import su.nightexpress.sunlight.module.bans.punishment.PunishData;
import su.nightexpress.sunlight.module.bans.punishment.PunishedIP;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.sunlight.module.bans.util.Placeholders.*;

public class BansLang extends Lang {

    private static final String INFO = "\uD83D\uDEC8";

    public static final LangString COMMAND_ARGUMENT_NAME_REASON    = LangString.of("Command.Argument.Name.Reason", "reason");
    public static final LangString COMMAND_ARGUMENT_NAME_IP_OR_NAME    = LangString.of("Command.Argument.Name.IPorName", "name | address");

    public static final LangEnum<SortMode> SORT_MODE = LangEnum.of("Bans.SortMode", SortMode.class);

    public static final LangString COMMAND_BAN_HISTORY_DESC = LangString.of("Bans.Command.History.Ban.Desc",
        "View bans history of a player or IP.");

    public static final LangString COMMAND_MUTE_HISTORY_DESC = LangString.of("Bans.Command.History.Mute.Desc",
        "View mutes history of a player or IP.");

    public static final LangString COMMAND_WARN_HISTORY_DESC = LangString.of("Bans.Command.History.Warn.Desc",
        "View warns history of a player or IP.");

    public static final LangString COMMAND_BAN_LIST_DESC = LangString.of("Bans.Command.BanList.Desc",
        "List of banned players and IPs.");

    public static final LangString COMMAND_MUTE_LIST_DESC = LangString.of("Bans.Command.MuteList.Desc",
        "List of muted players.");

    public static final LangString COMMAND_WARN_LIST_DESC = LangString.of("Bans.Command.WarnList.Desc",
        "List of warned players.");

    public static final LangString COMMAND_BAN_DESC  = LangString.of("Bans.Command.Ban.Desc",
        "Ban player.");

    public static final LangString COMMAND_BAN_IP_DESC = LangString.of("Bans.Command.Banip.Desc",
        "Ban IP address.");

    public static final LangString COMMAND_MUTE_DESC  = LangString.of("Bans.Command.Mute.Desc",
        "Mute player.");

    public static final LangString COMMAND_KICK_DESC  = LangString.of("Bans.Command.Kick.Desc",
        "Kick player.");


    public static final LangString COMMAND_UNMUTE_DESC  = LangString.of("Bans.Command.Unmute.Desc",
        "Unmute player.");

    public static final LangString COMMAND_UNBAN_DESC  = LangString.of("Bans.Command.Unban.Desc",
        "Unban player or IP address.");

    public static final LangString COMMAND_WARN_DESC  = LangString.of("Bans.Command.Warn.Desc",
        "Warn player.");

    public static final LangString COMMAND_UNWARN_DESC  = LangString.of("Bans.Command.Unwarn.Desc",
        "Remove the most recent warn of player.");

    // -------------------------------------

    public static final LangText PUNISHMENT_ERROR_RANK_PRIORITY = LangText.of("Bans.Punishment.Error.RankPriority",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Error!")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " You can't punish " + LIGHT_RED.enclose(PUNISHMENT_TARGET) + "!"),
        " "
    );

    public static final LangText PUNISHMENT_ERROR_PUNISH_DURATION = LangText.of("Bans.Punishment.Error.PunishDuration",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Error!")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " You can't punish more than " + LIGHT_RED.enclose(GENERIC_TIME) + "!"),
        " "
    );

    public static final LangText PUNISHMENT_ERROR_UNPUNISH_DURATION = LangText.of("Bans.Punishment.Error.UnPunishDuration",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Error!")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " You can't remove punishments with duration more than " + LIGHT_RED.enclose(GENERIC_TIME) + "!"),
        " "
    );

    public static final LangText PUNISHMENT_ERROR_IMMUNE = LangText.of("Bans.Punishment.Error.Immune",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Error!")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Player " + LIGHT_RED.enclose(PUNISHMENT_TARGET) + " can't be punished!"),
        " "
    );

    public static final LangText PUNISHMENT_ERROR_PLAYER_NOT_BANNED = LangText.of("Bans.Punishment.Error.PlayerNotBanned",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Error!")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Player " + LIGHT_RED.enclose(PUNISHMENT_TARGET) + " is not banned!"),
        " "
    );

    public static final LangText PUNISHMENT_ERROR_IP_NOT_BANNED = LangText.of("Bans.Punishment.Error.IPNotBanned",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Error!")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " IP Address " + LIGHT_RED.enclose(PUNISHMENT_TARGET) + " is not banned!"),
        " "
    );

    public static final LangText PUNISHMENT_ERROR_NOT_MUTED = LangText.of("Bans.Punishment.Error.NotMuted",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Error!")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Player " + LIGHT_RED.enclose(PUNISHMENT_TARGET) + " is not muted!"),
        " "
    );

    public static final LangText PUNISHMENT_ERROR_NOT_WARNED = LangText.of("Bans.Punishment.Error.NotWarned",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Error!")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Player " + LIGHT_RED.enclose(PUNISHMENT_TARGET) + " has no warns!"),
        " "
    );

    // -------------------------------------
    // Kicks
    // -------------------------------------

    public static final LangText KICK_DONE = LangText.of("Bans.Kick.Done",
        TAG_NO_PREFIX,
        LIGHT_GRAY.enclose(LIGHT_RED.enclose(INFO) + " Kicked " + LIGHT_RED.enclose(PUNISHMENT_TARGET) + " for " + LIGHT_RED.enclose(PUNISHMENT_REASON) + ".")
    );

    public static final LangText KICK_BROADCAST = LangText.of("Bans.Kick.Broadcast",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Kick Notification")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Player " + LIGHT_RED.enclose(PUNISHMENT_TARGET) + " have been kicked!"),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " By: " + LIGHT_RED.enclose(PUNISHMENT_PUNISHER)),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Reason: " + LIGHT_RED.enclose(PUNISHMENT_REASON)),
        " "
    );

    // -------------------------------------
    // Bans - Permanent
    // -------------------------------------

    public static final LangText COMMAND_BAN_DONE_PERMANENT = LangText.of("Bans.Command.Ban.Done.Permanent",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Ban Info")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " You permanently banned " + LIGHT_RED.enclose(PUNISHMENT_TARGET) + "."),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Reason: " + LIGHT_RED.enclose(PUNISHMENT_REASON)),
        " "
    );

    public static final LangText BAN_PERMANENT_BROADCAST = LangText.of("Bans.Ban.Permanent.Broadcast",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Ban Notification")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Player " + LIGHT_RED.enclose(PUNISHMENT_TARGET) + " have permanently been banned!"),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " By: " + LIGHT_RED.enclose(PUNISHMENT_PUNISHER)),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Reason: " + LIGHT_RED.enclose(PUNISHMENT_REASON)),
        " "
    );

    // -------------------------------------
    // Bans - Temporary
    // -------------------------------------

    public static final LangText COMMAND_BAN_DONE_TEMPORARY = LangText.of("Bans.Command.Ban.Done.Temporary",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Ban Info")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " You banned " + LIGHT_RED.enclose(PUNISHMENT_TARGET) + "."),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Reason: " + LIGHT_RED.enclose(PUNISHMENT_REASON)),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Duration: " + LIGHT_RED.enclose(PUNISHMENT_EXPIRES_IN)),
        " "
    );

    public static final LangText BAN_TEMPORARY_BROADCAST = LangText.of("Bans.Ban.Temporary.Broadcast",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Ban Notification")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Player " + LIGHT_RED.enclose(PUNISHMENT_TARGET) + " have been banned!"),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " By: " + LIGHT_RED.enclose(PUNISHMENT_PUNISHER)),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Reason: " + LIGHT_RED.enclose(PUNISHMENT_REASON)),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Duration: " + LIGHT_RED.enclose(PUNISHMENT_EXPIRES_IN)),
        " "
    );

    // -------------------------------------
    // Bans IP - Permanent
    // -------------------------------------

    public static final LangText COMMAND_BAN_IP_DONE_PERMANENT = LangText.of("Bans.Command.BanIP.Done.Permanent",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Ban Info")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " You permanently banned IP: " + LIGHT_RED.enclose(PUNISHMENT_TARGET) + "."),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Reason: " + LIGHT_RED.enclose(PUNISHMENT_REASON)),
        " "
    );

    public static final LangText BAN_IP_PERMANENT_BROADCAST = LangText.of("Bans.BanIP.Permanent.Broadcast",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Ban Notification")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " IP Address " + LIGHT_RED.enclose(PUNISHMENT_TARGET) + " have permanently been banned!"),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " By: " + LIGHT_RED.enclose(PUNISHMENT_PUNISHER)),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Reason: " + LIGHT_RED.enclose(PUNISHMENT_REASON)),
        " "
    );

    // -------------------------------------
    // Bans IP - Temporary
    // -------------------------------------

    public static final LangText COMMAND_BAN_IP_DONE_TEMPORARY = LangText.of("Bans.Command.BanIP.Done.Temporary",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Ban Info")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " You banned IP: " + LIGHT_RED.enclose(PUNISHMENT_TARGET) + "."),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Reason: " + LIGHT_RED.enclose(PUNISHMENT_REASON)),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Duration: " + LIGHT_RED.enclose(PUNISHMENT_EXPIRES_IN)),
        " "
    );

    public static final LangText BAN_IP_TEMPORARY_BROADCAST = LangText.of("Bans.BanIP.Temporary.Broadcast",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Ban Notification")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " IP Address " + LIGHT_RED.enclose(PUNISHMENT_TARGET) + " have been banned!"),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " By: " + LIGHT_RED.enclose(PUNISHMENT_PUNISHER)),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Reason: " + LIGHT_RED.enclose(PUNISHMENT_REASON)),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Duration: " + LIGHT_RED.enclose(PUNISHMENT_EXPIRES_IN)),
        " "
    );

    // -------------------------------------
    // Bans - Removal
    // -------------------------------------

    public static final LangText UNBAN_DONE = LangText.of("Bans.UnBan.Done",
        TAG_NO_PREFIX,
        " ",
        LIGHT_GREEN.enclose(INFO + " " + BOLD.enclose("Ban Info")),
        LIGHT_GRAY.enclose(LIGHT_GREEN.enclose("»") + " You unbanned " + LIGHT_GREEN.enclose(PUNISHMENT_TARGET) + "."),
        " "
    );

    public static final LangText UNBAN_BROADCAST = LangText.of("Bans.UnBan.Broadcast",
        TAG_NO_PREFIX,
        " ",
        LIGHT_GREEN.enclose(INFO + " " + BOLD.enclose("Ban Info")),
        LIGHT_GRAY.enclose(LIGHT_GREEN.enclose("»") + " Player " + LIGHT_GREEN.enclose(PUNISHMENT_TARGET) + " have been unbanned."),
        LIGHT_GRAY.enclose(LIGHT_GREEN.enclose("»") + " By: " + LIGHT_GREEN.enclose(PUNISHMENT_PUNISHER)),
        " "
    );

    // -------------------------------------
    // Bans IP - Removal
    // -------------------------------------

    public static final LangText UNBAN_IP_DONE = LangText.of("Bans.UnBanIP.Done",
        TAG_NO_PREFIX,
        " ",
        LIGHT_GREEN.enclose(INFO + " " + BOLD.enclose("Ban Info")),
        LIGHT_GRAY.enclose(LIGHT_GREEN.enclose("»") + " You unbanned IP " + LIGHT_GREEN.enclose(PUNISHMENT_TARGET) + "."),
        " "
    );

    public static final LangText UNBAN_IP_BROADCAST = LangText.of("Bans.UnBanIP.Broadcast",
        TAG_NO_PREFIX,
        " ",
        LIGHT_GREEN.enclose(INFO + " " + BOLD.enclose("Ban Info")),
        LIGHT_GRAY.enclose(LIGHT_GREEN.enclose("»") + " IP Address " + LIGHT_GREEN.enclose(PUNISHMENT_TARGET) + " have been unbanned."),
        LIGHT_GRAY.enclose(LIGHT_GREEN.enclose("»") + " By: " + LIGHT_GREEN.enclose(PUNISHMENT_PUNISHER)),
        " "
    );

    // -------------------------------------
    // Mutes - Permanent
    // -------------------------------------

    public static final LangText COMMAND_MUTE_DONE_PERMANENT = LangText.of("Bans.Command.Mute.Done.Permanent",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Mute Info")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " You permanently muted " + LIGHT_RED.enclose(PUNISHMENT_TARGET) + "."),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Reason: " + LIGHT_RED.enclose(PUNISHMENT_REASON)),
        " "
    );

    public static final LangText MUTE_PERMANENT_NOTIFY = LangText.of("Bans.Mute.Permanent.Notify",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Mute Notification")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " You have permanently been muted!"),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " By: " + LIGHT_RED.enclose(PUNISHMENT_PUNISHER)),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Reason: " + LIGHT_RED.enclose(PUNISHMENT_REASON)),
        " "
    );

    public static final LangText MUTE_PERMANENT_BROADCAST = LangText.of("Bans.Mute.Permanent.Broadcast",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Mute Notification")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Player " + LIGHT_RED.enclose(PUNISHMENT_TARGET) + " have permanently been muted!"),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " By: " + LIGHT_RED.enclose(PUNISHMENT_PUNISHER)),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Reason: " + LIGHT_RED.enclose(PUNISHMENT_REASON)),
        " "
    );

    // -------------------------------------
    // Mutes - Temporary
    // -------------------------------------

    public static final LangText COMMAND_MUTE_DONE_TEMPORARY = LangText.of("Bans.Command.Mute.Done.Temporary",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Mute Info")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " You muted " + LIGHT_RED.enclose(PUNISHMENT_TARGET) + "."),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Reason: " + LIGHT_RED.enclose(PUNISHMENT_REASON)),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Duration: " + LIGHT_RED.enclose(PUNISHMENT_EXPIRES_IN)),
        " "
    );

    public static final LangText MUTE_TEMPORARY_NOTIFY = LangText.of("Bans.Mute.Temporary.Notify",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Mute Notification")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " You have been muted!"),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " By: " + LIGHT_RED.enclose(PUNISHMENT_PUNISHER)),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Reason: " + LIGHT_RED.enclose(PUNISHMENT_REASON)),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Duration: " + LIGHT_RED.enclose(PUNISHMENT_EXPIRES_IN)),
        " "
    );

    public static final LangText MUTE_TEMPORARY_BROADCAST = LangText.of("Bans.Mute.Temporary.Broadcast",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Mute Notification")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Player " + LIGHT_RED.enclose(PUNISHMENT_TARGET) + " have been muted!"),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " By: " + LIGHT_RED.enclose(PUNISHMENT_PUNISHER)),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Reason: " + LIGHT_RED.enclose(PUNISHMENT_REASON)),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Duration: " + LIGHT_RED.enclose(PUNISHMENT_EXPIRES_IN)),
        " "
    );

    // -------------------------------------
    // Mutes - Removal
    // -------------------------------------

    public static final LangText UNMUTE_DONE = LangText.of("Bans.Unmute.Done",
        TAG_NO_PREFIX,
        " ",
        LIGHT_GREEN.enclose(INFO + " " + BOLD.enclose("Mute Info")),
        LIGHT_GRAY.enclose(LIGHT_GREEN.enclose("»") + " You unmuted " + LIGHT_GREEN.enclose(PUNISHMENT_TARGET) + "."),
        " "
    );

    public static final LangText UNMUTE_NOTIFY = LangText.of("Bans.Unmute.Notify",
        TAG_NO_PREFIX,
        " ",
        LIGHT_GREEN.enclose(INFO + " " + BOLD.enclose("Mute Info")),
        LIGHT_GRAY.enclose(LIGHT_GREEN.enclose("»") + " You have been unmuted."),
        LIGHT_GRAY.enclose(LIGHT_GREEN.enclose("»") + " By: " + LIGHT_GREEN.enclose(PUNISHMENT_PUNISHER)),
        " "
    );

    public static final LangText UNMUTE_BROADCAST = LangText.of("Bans.Unmute.Broadcast",
        TAG_NO_PREFIX,
        " ",
        LIGHT_GREEN.enclose(INFO + " " + BOLD.enclose("Mute Info")),
        LIGHT_GRAY.enclose(LIGHT_GREEN.enclose("»") + " Player " + LIGHT_GREEN.enclose(PUNISHMENT_TARGET) + " have been unmuted."),
        LIGHT_GRAY.enclose(LIGHT_GREEN.enclose("»") + " By: " + LIGHT_GREEN.enclose(PUNISHMENT_PUNISHER)),
        " "
    );

    // -------------------------------------
    // Warns - Permanent
    // -------------------------------------

    public static final LangText COMMAND_WARN_DONE_PERMANENT = LangText.of("Bans.Command.Warn.Done.Permanent",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Warn Info")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " You permanently warned " + LIGHT_RED.enclose(PUNISHMENT_TARGET) + "."),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Reason: " + LIGHT_RED.enclose(PUNISHMENT_REASON)),
        " "
    );

    public static final LangText WARN_PERMANENT_NOTIFY = LangText.of("Bans.Warn.Permanent.Notify",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Warn Notification")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " You have permanently been warned!"),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " By: " + LIGHT_RED.enclose(PUNISHMENT_PUNISHER)),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Reason: " + LIGHT_RED.enclose(PUNISHMENT_REASON)),
        " "
    );

    public static final LangText WARN_PERMANENT_BROADCAST = LangText.of("Bans.Warn.Permanent.Broadcast",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Warn Notification")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Player " + LIGHT_RED.enclose(PUNISHMENT_TARGET) + " have permanently been warned!"),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " By: " + LIGHT_RED.enclose(PUNISHMENT_PUNISHER)),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Reason: " + LIGHT_RED.enclose(PUNISHMENT_REASON)),
        " "
    );

    // -------------------------------------
    // Warns - Temporary
    // -------------------------------------

    public static final LangText COMMAND_WARN_DONE_TEMPORARY = LangText.of("Bans.Command.Warn.Done.Temporary",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Warn Info")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " You warned " + LIGHT_RED.enclose(PUNISHMENT_TARGET) + "."),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Reason: " + LIGHT_RED.enclose(PUNISHMENT_REASON)),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Duration: " + LIGHT_RED.enclose(PUNISHMENT_EXPIRES_IN)),
        " "
    );

    public static final LangText WARN_TEMPORARY_NOTIFY = LangText.of("Bans.Warn.Temporary.Notify",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Warn Notification")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " You have been warned!"),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " By: " + LIGHT_RED.enclose(PUNISHMENT_PUNISHER)),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Reason: " + LIGHT_RED.enclose(PUNISHMENT_REASON)),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Duration: " + LIGHT_RED.enclose(PUNISHMENT_EXPIRES_IN)),
        " "
    );

    public static final LangText WARN_TEMPORARY_BROADCAST = LangText.of("Bans.Warn.Temporary.Broadcast",
        TAG_NO_PREFIX,
        " ",
        LIGHT_RED.enclose(INFO + " " + BOLD.enclose("Warn Notification")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Player " + LIGHT_RED.enclose(PUNISHMENT_TARGET) + " have been warned!"),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " By: " + LIGHT_RED.enclose(PUNISHMENT_PUNISHER)),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Reason: " + LIGHT_RED.enclose(PUNISHMENT_REASON)),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("»") + " Duration: " + LIGHT_RED.enclose(PUNISHMENT_EXPIRES_IN)),
        " "
    );

    // -------------------------------------
    // Warns - Removal
    // -------------------------------------

    public static final LangText UNWARN_DONE = LangText.of("Bans.Unwarn.Done",
        TAG_NO_PREFIX,
        " ",
        LIGHT_GREEN.enclose(INFO + " " + BOLD.enclose("Warn Info")),
        LIGHT_GRAY.enclose(LIGHT_GREEN.enclose("»") + " You removed a warn from " + LIGHT_GREEN.enclose(PUNISHMENT_TARGET) + "."),
        " "
    );

    public static final LangText UNWARN_NOTIFY = LangText.of("Bans.Unwarn.Notify",
        TAG_NO_PREFIX,
        " ",
        LIGHT_GREEN.enclose(INFO + " " + BOLD.enclose("Warn Info")),
        LIGHT_GRAY.enclose(LIGHT_GREEN.enclose("»") + " Your latest warn has been removed."),
        LIGHT_GRAY.enclose(LIGHT_GREEN.enclose("»") + " By: " + LIGHT_GREEN.enclose(PUNISHMENT_PUNISHER)),
        " "
    );

    public static final LangText UNWARN_BROADCAST = LangText.of("Bans.Unwarn.Broadcast",
        TAG_NO_PREFIX,
        " ",
        LIGHT_GREEN.enclose(INFO + " " + BOLD.enclose("Warn Info")),
        LIGHT_GRAY.enclose(LIGHT_GREEN.enclose("»") + " Latest warn has been removed from " + LIGHT_GREEN.enclose(PUNISHMENT_TARGET) + "."),
        LIGHT_GRAY.enclose(LIGHT_GREEN.enclose("»") + " By: " + LIGHT_GREEN.enclose(PUNISHMENT_PUNISHER)),
        " "
    );

    // -------------------------------------

    public static final LangText ERROR_COMMAND_INVALID_REASON_ARGUMENT = LangText.of("Error.Command.Argument.InvalidReason",
        LIGHT_GRAY.enclose(LIGHT_RED.enclose(GENERIC_VALUE) + " is not a valid reason!"));

    public static final LangString OTHER_NO_REASON = LangString.of("Bans.Other.NoReason", "<no reason>");

    @NotNull
    public static LangText getNotPunished(@NotNull PunishmentType type) {
        return switch (type) {
            case BAN -> PUNISHMENT_ERROR_PLAYER_NOT_BANNED;
            case MUTE -> PUNISHMENT_ERROR_NOT_MUTED;
            case WARN -> PUNISHMENT_ERROR_NOT_WARNED;
        };
    }

    @Nullable
    public static LangText getPunishNotify(@NotNull PunishData punishData, @NotNull PunishmentType type) {
        if (type == PunishmentType.BAN) {
            return null;
        }
        else if (type == PunishmentType.MUTE) {
            return punishData.isPermanent() ? MUTE_PERMANENT_NOTIFY : MUTE_TEMPORARY_NOTIFY;
        }
        else {
            return punishData.isPermanent() ? WARN_PERMANENT_NOTIFY : WARN_TEMPORARY_NOTIFY;
        }
    }

    @NotNull
    public static LangText getPunishOutput(@NotNull PunishData punishData, @NotNull PunishmentType type) {
        if (type == PunishmentType.BAN) {
            if (punishData instanceof PunishedIP) {
                return punishData.isPermanent() ? COMMAND_BAN_IP_DONE_PERMANENT : COMMAND_BAN_IP_DONE_TEMPORARY;
            }
            else {
                return punishData.isPermanent() ? COMMAND_BAN_DONE_PERMANENT : COMMAND_BAN_DONE_TEMPORARY;
            }
        }
        else if (type == PunishmentType.MUTE) {
            return punishData.isPermanent() ? COMMAND_MUTE_DONE_PERMANENT : COMMAND_MUTE_DONE_TEMPORARY;
        }
        else {
            return punishData.isPermanent() ? COMMAND_WARN_DONE_PERMANENT : COMMAND_WARN_DONE_TEMPORARY;
        }
    }

    @NotNull
    public static LangText getPunishBroadcast(@NotNull PunishData punishData, @NotNull PunishmentType type) {
        if (type == PunishmentType.BAN) {
            if (punishData instanceof PunishedIP) {
                return punishData.isPermanent() ? BAN_IP_PERMANENT_BROADCAST : BAN_IP_TEMPORARY_BROADCAST;
            }
            else {
                return punishData.isPermanent() ? BAN_PERMANENT_BROADCAST : BAN_TEMPORARY_BROADCAST;
            }
        }
        else if (type == PunishmentType.MUTE) {
            return punishData.isPermanent() ? MUTE_PERMANENT_BROADCAST : MUTE_TEMPORARY_BROADCAST;
        }
        else {
            return punishData.isPermanent() ? WARN_PERMANENT_BROADCAST : WARN_TEMPORARY_BROADCAST;
        }
    }

    @NotNull
    public static LangText getUnPunishOutput(@NotNull PunishData punishData, @NotNull PunishmentType type) {
        return switch (type) {
            case BAN -> punishData instanceof PunishedIP ? UNBAN_IP_DONE : UNBAN_DONE;
            case MUTE -> UNMUTE_DONE;
            case WARN -> UNWARN_DONE;
        };
    }

    @NotNull
    public static LangText getUnPunishBroadcast(@NotNull PunishData punishData, @NotNull PunishmentType type) {
        return switch (type) {
            case BAN -> punishData instanceof PunishedIP ? UNBAN_IP_BROADCAST : UNBAN_BROADCAST;
            case MUTE -> UNMUTE_BROADCAST;
            case WARN -> UNWARN_BROADCAST;
        };
    }
}
