package su.nightexpress.sunlight.module.bans.config;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.lang.LangKey;
import su.nightexpress.sunlight.module.bans.punishment.Punishment;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;
import su.nightexpress.sunlight.module.bans.util.Placeholders;

import static su.nexmedia.engine.utils.Colors.*;

public class BansLang {

    public static final LangKey COMMAND_HISTORY_MUTE_DESC  = LangKey.of("Bans.Command.History.Mute.Desc", "View player's mutes history.");
    public static final LangKey COMMAND_HISTORY_MUTE_USAGE = LangKey.of("Bans.Command.History.Mute.Usage", "<player>");
    public static final LangKey COMMAND_HISTORY_BAN_DESC   = LangKey.of("Bans.Command.History.Ban.Desc", "View player's bans history.");
    public static final LangKey COMMAND_HISTORY_BAN_USAGE  = LangKey.of("Bans.Command.History.Ban.Usage", "<player>");
    public static final LangKey COMMAND_HISTORY_WARN_DESC  = LangKey.of("Bans.Command.History.Warn.Desc", "View player's warns history.");
    public static final LangKey COMMAND_HISTORY_WARN_USAGE = LangKey.of("Bans.Command.History.Warn.Usage", "<player>");

    public static final LangKey COMMAND_BANLIST_DESC   = LangKey.of("Bans.Command.BanList.Desc", "List of banned players and IPs.");
    public static final LangKey COMMAND_MUTELIST_DESC = LangKey.of("Bans.Command.MuteList.Desc", "List of muted players and IPs.");
    public static final LangKey COMMAND_WARNLIST_DESC = LangKey.of("Bans.Command.WarnList.Desc", "List of warned players.");

    public static final LangKey COMMAND_BAN_DESC  = LangKey.of("Bans.Command.Ban.Desc", "Ban player.");
    public static final LangKey COMMAND_BAN_USAGE = LangKey.of("Bans.Command.Ban.Usage", "<player> <time> [reason]");

    public static final LangKey COMMAND_BANIP_DESC  = LangKey.of("Bans.Command.Banip.Desc", "Ban IP address.");
    public static final LangKey COMMAND_BANIP_USAGE = LangKey.of("Bans.Command.Banip.Usage", "<player/ip> <time> [reason]");

    public static final LangKey COMMAND_MUTE_DESC  = LangKey.of("Bans.Command.Mute.Desc", "Mute player(s) by name or IP.");
    public static final LangKey COMMAND_MUTE_USAGE = LangKey.of("Bans.Command.Mute.Usage", "<player/ip> <time> [reason]");

    public static final LangKey COMMAND_KICK_DESC  = LangKey.of("Bans.Command.Kick.Desc", "Kick player(s) by name or IP.");
    public static final LangKey COMMAND_KICK_USAGE = LangKey.of("Bans.Command.Kick.Usage", "<player/ip> [reason]");

    public static final LangKey COMMAND_UNMUTE_DESC  = LangKey.of("Bans.Command.Unmute.Desc", "Unmute player(s) by name or IP.");
    public static final LangKey COMMAND_UNMUTE_USAGE = LangKey.of("Bans.Command.Unmute.Usage", "<player/ip>");

    public static final LangKey COMMAND_UNBAN_DESC  = LangKey.of("Bans.Command.Unban.Desc", "Unban player(s) by name or IP.");
    public static final LangKey COMMAND_UNBAN_USAGE = LangKey.of("Bans.Command.Unban.Usage", "<player/ip>");

    public static final LangKey COMMAND_WARN_DESC  = LangKey.of("Bans.Command.Warn.Desc", "Warn player.");
    public static final LangKey COMMAND_WARN_USAGE = LangKey.of("Bans.Command.Warn.Usage", "<player> <time> [reason]");

    public static final LangKey COMMAND_UNWARN_DESC  = LangKey.of("Bans.Command.Unwarn.Desc", "Remove the most recent warn of player.");
    public static final LangKey COMMAND_UNWARN_USAGE = LangKey.of("Bans.Command.Unwarn.Usage", "<player>");

    public static final LangKey PUNISHMENT_ERROR_RANK_DURATION = LangKey.of("Bans.Punishment.Error.RankDuration",
        GRAY + "You can't punish for longer than " + RED + Placeholders.GENERIC_TIME + GRAY + "!");

    public static final LangKey PUNISHMENT_ERROR_IMMUNE     = LangKey.of("Bans.Punishment.Error.Immune", "<! prefix:\"false\" !>" +
        RED + Placeholders.GENERIC_USER + GRAY + " can not be punished.");

    public static final LangKey PUNISHMENT_ERROR_NOT_BANNED = LangKey.of("Bans.Punishment.Error.NotBanned", "<! prefix:\"false\" !>" +
        RED + Placeholders.GENERIC_USER + GRAY + " is not banned.");

    public static final LangKey PUNISHMENT_ERROR_NOT_MUTED  = LangKey.of("Bans.Punishment.Error.NotMuted", "<! prefix:\"false\" !>" +
        RED + Placeholders.GENERIC_USER + GRAY + " is not muted.");

    public static final LangKey PUNISHMENT_ERROR_NOT_WARNED = LangKey.of("Bans.Punishment.Error.NotWarned", "<! prefix:\"false\" !>" +
        RED + Placeholders.GENERIC_USER + GRAY + " does not have active warns.");

    public static final LangKey PUNISHMENT_ERROR_NOT_IP     = LangKey.of("Bans.Punishment.Error.NotIP", "<! prefix:\"false\" !>" +
        RED + Placeholders.GENERIC_USER + " is not IP address!");

    public static final LangKey BAN_USER_PERMA_DONE = LangKey.of("Bans.Ban.User.Perma.Done", "<! prefix:\"false\" !>" +
        RED + Placeholders.PUNISHMENT_USER + GRAY + " has been permanently banned: " + RED + Placeholders.PUNISHMENT_REASON);

    public static final LangKey BAN_USER_PERMA_BROADCAST = LangKey.of("Bans.Ban.User.Perma.Broadcast", "<! prefix:\"false\" !>" +
        RED + Placeholders.PUNISHMENT_PUNISHER + GRAY + " permanently banned " + RED + Placeholders.PUNISHMENT_USER + " "
        + "<? show_text:\"&7Reason: &f" + Placeholders.PUNISHMENT_REASON + "<newline>&7Date: &f" + Placeholders.PUNISHMENT_CREATION_DATE + "\" ?>" + ORANGE + "[Details]</>");

    public static final LangKey BAN_USER_TEMP_DONE = LangKey.of("Bans.Ban.User.Temp.Done", "<! prefix:\"false\" !>" +
        RED + Placeholders.PUNISHMENT_USER + GRAY +" has been banned for " + RED + Placeholders.PUNISHMENT_EXPIRES_IN + GRAY + ". Reason: " + RED + Placeholders.PUNISHMENT_REASON);

    public static final LangKey BAN_USER_TEMP_BROADCAST = LangKey.of("Bans.Ban.User.Temp.Broadcast", "<! prefix:\"false\" !>" +
        RED + Placeholders.PUNISHMENT_PUNISHER + GRAY + " banned " + RED + Placeholders.PUNISHMENT_USER + " "
        + "<? show_text:\"&7Duration: &f" + Placeholders.PUNISHMENT_EXPIRES_IN + "<newline>&7Reason: &f" + Placeholders.PUNISHMENT_REASON + "<newline>&7Date: &f" + Placeholders.PUNISHMENT_CREATION_DATE + "<newline>&7Expires: &f" + Placeholders.PUNISHMENT_EXPIRE_DATE + "\" ?>" + ORANGE + "[Details]</>");

    public static final LangKey BAN_IP_PERMA_DONE = LangKey.of("Bans.Ban.IP.Perma.Done", "<! prefix:\"false\" !>" +
        GRAY + "IP " + RED + Placeholders.PUNISHMENT_USER + GRAY + " has been permanently banned: " + RED + Placeholders.PUNISHMENT_REASON);

    public static final LangKey BAN_IP_PERMA_BROADCAST = LangKey.of("Bans.Ban.IP.Perma.Broadcast", "<! prefix:\"false\" !>" +
        RED + Placeholders.PUNISHMENT_PUNISHER + GRAY + " permanently banned IP " + RED + Placeholders.PUNISHMENT_USER + " "
        + "<? show_text:\"&7Reason: &f" + Placeholders.PUNISHMENT_REASON + "<newline>&7Date: &f" + Placeholders.PUNISHMENT_CREATION_DATE + "\" ?>" + ORANGE + "[Details]</>");

    public static final LangKey BAN_IP_TEMP_DONE = LangKey.of("Bans.Ban.IP.Temp.Done", "<! prefix:\"false\" !>" +
        GRAY + "IP " + RED + Placeholders.PUNISHMENT_USER + GRAY + " has been banned for " + RED + Placeholders.PUNISHMENT_EXPIRES_IN + GRAY + ". Reason: " + RED + Placeholders.PUNISHMENT_REASON);

    public static final LangKey BAN_IP_TEMP_BROADCAST = LangKey.of("Bans.Ban.IP.Temp.Broadcast", "<! prefix:\"false\" !>" +
        RED + Placeholders.PUNISHMENT_PUNISHER + GRAY + " banned IP " + RED + Placeholders.PUNISHMENT_USER + " "
        + "<? show_text:\"&7Duration: &f" + Placeholders.PUNISHMENT_EXPIRES_IN + "<newline>&7Reason: &f" + Placeholders.PUNISHMENT_REASON + "<newline>&7Date: &f" + Placeholders.PUNISHMENT_CREATION_DATE + "<newline>&7Expires: &f" + Placeholders.PUNISHMENT_EXPIRE_DATE + "\" ?>" + ORANGE + "[Details]</>");

    public static final LangKey UNBAN_USER_DONE = LangKey.of("Bans.Unban.User.Done", "<! prefix:\"false\" !>" +
        "User unbanned: " + GREEN + Placeholders.PUNISHMENT_USER + GRAY + ".");

    public static final LangKey UNBAN_USER_BROADCAST = LangKey.of("Bans.Unban.User.Broadcast", "<! prefix:\"false\" !>" +
        GREEN + Placeholders.GENERIC_ADMIN + GRAY + " unbanned user: " + GREEN + Placeholders.PUNISHMENT_USER + GRAY + ".");

    public static final LangKey UNBAN_IP_DONE = LangKey.of("Bans.Unban.IP.Done", "<! prefix:\"false\" !>" +
        "IP unbanned: " + GREEN + Placeholders.PUNISHMENT_USER + GRAY + ".");

    public static final LangKey UNBAN_IP_BROADCAST = LangKey.of("Bans.Unban.IP.Broadcast", "<! prefix:\"false\" !>" +
        GREEN + Placeholders.GENERIC_ADMIN + GRAY + " unbanned IP: " + GREEN + Placeholders.PUNISHMENT_USER + GRAY + ".");

    public static final LangKey KICK_DONE      = LangKey.of("Bans.Kick.Done", "<! prefix:\"false\" !>" +
        RED + Placeholders.PUNISHMENT_USER + GRAY + " has been kicked for " + RED + Placeholders.PUNISHMENT_REASON);

    public static final LangKey KICK_BROADCAST = LangKey.of("Bans.Kick.Broadcast", "<! prefix:\"false\" !>" +
        RED + Placeholders.PUNISHMENT_PUNISHER + GRAY + " kicked " + RED + Placeholders.PUNISHMENT_USER + " "
        + "<? show_text:\"&7Reason: &f" + Placeholders.PUNISHMENT_REASON + "\" ?>" + ORANGE + "[Details]</>");

    public static final LangKey MUTE_USER_PERMA_DONE = LangKey.of("Bans.Mute.User.Perma.Done", "<! prefix:\"false\" !>" +
        RED + Placeholders.PUNISHMENT_USER + GRAY + " has been permanently muted: " + RED + Placeholders.PUNISHMENT_REASON);

    public static final LangKey MUTE_USER_PERMA_NOTIFY = LangKey.of("Bans.Mute.User.Perma.Notify", "<! prefix:\"false\" !>" +
        RED + "You have permanently been muted! "
        + "<? show_text:\"&7By: &f" + Placeholders.PUNISHMENT_PUNISHER + "<newline>&7Expires: &f" + Placeholders.PUNISHMENT_EXPIRES_IN + "<newline>&7Reason: &f" + Placeholders.PUNISHMENT_REASON + "<newline>&7Date: &f" + Placeholders.PUNISHMENT_CREATION_DATE + "\" ?>" + ORANGE + "[Details]</>");

    public static final LangKey MUTE_USER_PERMA_BROADCAST = LangKey.of("Bans.Mute.User.Perma.Broadcast", "<! prefix:\"false\" !>" +
        RED + Placeholders.PUNISHMENT_PUNISHER + GRAY + " permanently muted " + RED + Placeholders.PUNISHMENT_USER + " "
        + "<? show_text:\"&7Reason: &f" + Placeholders.PUNISHMENT_REASON + "<newline>&7Date: &f" + Placeholders.PUNISHMENT_CREATION_DATE + "\" ?>" + ORANGE + "[Details]</>");

    public static final LangKey MUTE_USER_TEMP_DONE = LangKey.of("Bans.Mute.User.Temp.Done", "<! prefix:\"false\" !>" +
        RED + Placeholders.PUNISHMENT_USER + GRAY + " has been muted for " + RED + Placeholders.PUNISHMENT_EXPIRES_IN + GRAY + ". Reason: " + RED + Placeholders.PUNISHMENT_REASON);

    public static final LangKey MUTE_USER_TEMP_NOTIFY = LangKey.of("Bans.Mute.User.Temp.Notify", "<! prefix:\"false\" !>" +
        RED + "You have been muted! "
        + "<? show_text:\"&7By: &f" + Placeholders.PUNISHMENT_PUNISHER + "<newline>&7Duration: &f" + Placeholders.PUNISHMENT_EXPIRES_IN + "<newline>&7Reason: &f" + Placeholders.PUNISHMENT_REASON + "<newline>&7Date: &f" + Placeholders.PUNISHMENT_CREATION_DATE + "<newline>&7Expires: &f" + Placeholders.PUNISHMENT_EXPIRE_DATE + "\" ?>" + ORANGE + "[Details]</>");

    public static final LangKey MUTE_USER_TEMP_BROADCAST = LangKey.of("Bans.Mute.User.Temp.Broadcast", "<! prefix:\"false\" !>" +
        RED + Placeholders.PUNISHMENT_PUNISHER + GRAY + " muted " + RED + Placeholders.PUNISHMENT_USER + " "
        + "<? show_text:\"&7Duration: &f" + Placeholders.PUNISHMENT_EXPIRES_IN + "<newline>&7Reason: &f" + Placeholders.PUNISHMENT_REASON + "<newline>&7Date: &f" + Placeholders.PUNISHMENT_CREATION_DATE + "<newline>&7Expires: &f" + Placeholders.PUNISHMENT_EXPIRE_DATE + "\" ?>" + ORANGE + "[Details]</>");

    public static final LangKey MUTE_IP_PERMA_DONE = LangKey.of("Bans.Mute.IP.Perma.Done", "<! prefix:\"false\" !>" +
        "IP " + RED + Placeholders.PUNISHMENT_USER + GRAY + " has been permanently muted: " + RED + Placeholders.PUNISHMENT_REASON);

    public static final LangKey MUTE_IP_PERMA_NOTIFY = LangKey.of("Bans.Mute.IP.Perma.Notify", "<! prefix:\"false\" !>" +
        RED + "You have permanently been muted! "
        + "<? show_text:\"&7By: &f" + Placeholders.PUNISHMENT_PUNISHER + "<newline>&7Expires: &f" + Placeholders.PUNISHMENT_EXPIRES_IN + "<newline>&7Reason: &f" + Placeholders.PUNISHMENT_REASON + "<newline>&7Date: &f" + Placeholders.PUNISHMENT_CREATION_DATE + "\" ?>" + ORANGE + "[Details]</>");

    public static final LangKey MUTE_IP_PERMA_BROADCAST = LangKey.of("Bans.Mute.IP.Perma.Broadcast", "<! prefix:\"false\" !>" +
        RED + Placeholders.PUNISHMENT_PUNISHER + GRAY + " permanently muted IP " + RED + Placeholders.PUNISHMENT_USER + " "
        + "<? show_text:\"&7Reason: &f" + Placeholders.PUNISHMENT_REASON + "<newline>&7Date: &f" + Placeholders.PUNISHMENT_CREATION_DATE + "\" ?>" + ORANGE + "[Details]</>");

    public static final LangKey MUTE_IP_TEMP_DONE = LangKey.of("Bans.Mute.IP.Temp.Done", "<! prefix:\"false\" !>" +
        "IP " + RED + Placeholders.PUNISHMENT_USER + GRAY + " has been muted for " + RED + Placeholders.PUNISHMENT_EXPIRES_IN + GRAY + ". Reason: " + RED + Placeholders.PUNISHMENT_REASON);

    public static final LangKey MUTE_IP_TEMP_NOTIFY = LangKey.of("Bans.Mute.IP.Temp.Notify", "<! prefix:\"false\" !>" +
        RED + "You have been muted! "
        + "<? show_text:\"&7By: &f" + Placeholders.PUNISHMENT_PUNISHER + "<newline>&7Duration: &f" + Placeholders.PUNISHMENT_EXPIRES_IN + "<newline>&7Reason: &f" + Placeholders.PUNISHMENT_REASON + "<newline>&7Date: &f" + Placeholders.PUNISHMENT_CREATION_DATE + "<newline>&7Expires: &f" + Placeholders.PUNISHMENT_EXPIRE_DATE + "\" ?>" + ORANGE + "[Details]</>");

    public static final LangKey MUTE_IP_TEMP_BROADCAST = LangKey.of("Bans.Mute.IP.Temp.Broadcast", "<! prefix:\"false\" !>" +
        RED + Placeholders.PUNISHMENT_PUNISHER + GRAY + " muted IP " + RED + Placeholders.PUNISHMENT_USER + " "
        + "<? show_text:\"&7Duration: &f" + Placeholders.PUNISHMENT_EXPIRES_IN + "<newline>&7Reason: &f" + Placeholders.PUNISHMENT_REASON + "<newline>&7Date: &f" + Placeholders.PUNISHMENT_CREATION_DATE + "<newline>&7Expires: &f" + Placeholders.PUNISHMENT_EXPIRE_DATE + "\" ?>" + ORANGE + "[Details]</>");

    public static final LangKey UNMUTE_USER_DONE = LangKey.of("Bans.Unmute.User.Done", "<! prefix:\"false\" !>" +
        "User unmuted: " + GREEN + Placeholders.PUNISHMENT_USER + GRAY + ".");

    public static final LangKey UNMUTE_USER_BROADCAST = LangKey.of("Bans.Unmute.User.Broadcast", "<! prefix:\"false\" !>" +
        GREEN + Placeholders.GENERIC_ADMIN + GRAY + " unmuted user: " + GREEN + Placeholders.PUNISHMENT_USER + GRAY + ".");

    public static final LangKey UNMUTE_IP_DONE = LangKey.of("Bans.Unmute.IP.Done", "<! prefix:\"false\" !>" +
        "IP unmuted: " + GREEN + Placeholders.PUNISHMENT_USER + GRAY + ".");

    public static final LangKey UNMUTE_IP_BROADCAST = LangKey.of("Bans.Unmute.IP.Broadcast", "<! prefix:\"false\" !>" +
        GREEN + Placeholders.GENERIC_ADMIN + GRAY + " unmuted IP: " + GREEN + Placeholders.PUNISHMENT_USER + GRAY + ".");

    public static final LangKey WARN_USER_TEMP_DONE = LangKey.of("Bans.Warn.User.Temp.Done", "<! prefix:\"false\" !>" +
        RED + Placeholders.PUNISHMENT_USER + GRAY + " has been warned for " + RED + Placeholders.PUNISHMENT_EXPIRES_IN + GRAY + ". Reason: " + RED + Placeholders.PUNISHMENT_REASON);

    public static final LangKey WARN_USER_TEMP_NOTIFY = LangKey.of("Bans.Warn.User.Temp.Notify", "<! prefix:\"false\" !>" +
        RED + "You have been warned! "
        + "<? show_text:\"&7By: &f" + Placeholders.PUNISHMENT_PUNISHER + "<newline>&7Duration: &f" + Placeholders.PUNISHMENT_EXPIRES_IN + "<newline>&7Reason: &f" + Placeholders.PUNISHMENT_REASON + "<newline>&7Date: &f" + Placeholders.PUNISHMENT_CREATION_DATE + "<newline>&7Expires: &f" + Placeholders.PUNISHMENT_EXPIRE_DATE + "\" ?>" + ORANGE + "[Details]</>");

    public static final LangKey WARN_USER_TEMP_BROADCAST = LangKey.of("Bans.Warn.User.Temp.Broadcast", "<! prefix:\"false\" !>" +
        RED + Placeholders.PUNISHMENT_PUNISHER + GRAY + " warned " + RED + Placeholders.PUNISHMENT_USER + " "
        + "<? show_text:\"&7Duration: &f" + Placeholders.PUNISHMENT_EXPIRES_IN + "<newline>&7Reason: &f" + Placeholders.PUNISHMENT_REASON + "<newline>&7Date: &f" + Placeholders.PUNISHMENT_CREATION_DATE + "<newline>&7Expires: &f" + Placeholders.PUNISHMENT_EXPIRE_DATE + "\" ?>" + ORANGE + "[Details]</>");

    public static final LangKey WARN_USER_PERMA_DONE = LangKey.of("Bans.Warn.User.Perma.Done", "<! prefix:\"false\" !>" +
        RED + Placeholders.PUNISHMENT_USER + GRAY + " has been permanently warned for " + RED + Placeholders.PUNISHMENT_REASON);

    public static final LangKey WARN_USER_PERMA_NOTIFY = LangKey.of("Bans.Warn.User.Perma.Notify", "<! prefix:\"false\" !>" +
        RED + "You have been warned! "
        + "<? show_text:\"&7By: &f" + Placeholders.PUNISHMENT_PUNISHER + "<newline>&7Expires: &f" + Placeholders.PUNISHMENT_EXPIRES_IN + "<newline>&7Reason: &f" + Placeholders.PUNISHMENT_REASON + "<newline>&7Date: &f" + Placeholders.PUNISHMENT_CREATION_DATE + "\" ?>" + ORANGE + "[Details]</>");

    public static final LangKey WARN_USER_PERMA_BROADCAST = LangKey.of("Bans.Warn.User.Perma.Broadcast", "<! prefix:\"false\" !>" +
        RED + Placeholders.PUNISHMENT_PUNISHER + GRAY + " permanently warned " + RED + Placeholders.PUNISHMENT_USER + " "
        + "<? show_text:\"&7Reason: &f" + Placeholders.PUNISHMENT_REASON + "<newline>&7Date: &f" + Placeholders.PUNISHMENT_CREATION_DATE + "\" ?>" + ORANGE + "[Details]</>");

    public static final LangKey UNWARN_USER_DONE = LangKey.of("Bans.Unwarn.User.Done", "<! prefix:\"false\" !>" +
        GRAY + "Removed warn from " + GREEN + Placeholders.PUNISHMENT_USER + GRAY + ".");

    public static final LangKey UNWARN_USER_BROADCAST = LangKey.of("Bans.Unwarn.User.Broadcast", "<! prefix:\"false\" !>" +
        GREEN + Placeholders.PUNISHMENT_PUNISHER + GRAY + " removed warn from " + GREEN + Placeholders.PUNISHMENT_USER + GRAY + ".");

    @NotNull
    public static LangKey getNotPunished(@NotNull PunishmentType type) {
        return switch (type) {
            case BAN -> PUNISHMENT_ERROR_NOT_BANNED;
            case MUTE -> PUNISHMENT_ERROR_NOT_MUTED;
            case WARN -> PUNISHMENT_ERROR_NOT_WARNED;
        };
    }

    @NotNull
    public static LangKey getPunishNotify(@NotNull Punishment punishment) {
        boolean isIp = punishment.isIp();
        if (punishment.isPermanent()) {
            return switch (punishment.getType()) {
                case MUTE -> isIp ? MUTE_IP_PERMA_NOTIFY : MUTE_USER_PERMA_NOTIFY;
                case BAN -> KICK_DONE; // TODO
                case WARN -> WARN_USER_PERMA_NOTIFY;
            };
        }
        else {
            return switch (punishment.getType()) {
                case MUTE -> isIp ? MUTE_IP_TEMP_NOTIFY : MUTE_USER_TEMP_NOTIFY;
                case BAN -> KICK_DONE; // TODO
                case WARN -> WARN_USER_TEMP_NOTIFY;
            };
        }
    }

    @NotNull
    public static LangKey getPunishSender(@NotNull Punishment punishment) {
        boolean isIp = punishment.isIp();
        if (punishment.isPermanent()) {
            return switch (punishment.getType()) {
                case MUTE -> isIp ? MUTE_IP_PERMA_DONE : MUTE_USER_PERMA_DONE;
                case BAN -> isIp ? BAN_IP_PERMA_DONE : BAN_USER_PERMA_DONE;
                case WARN -> WARN_USER_PERMA_DONE;
            };
        }
        else {
            return switch (punishment.getType()) {
                case MUTE -> isIp ? MUTE_IP_TEMP_DONE : MUTE_USER_TEMP_DONE;
                case BAN -> isIp ? BAN_IP_TEMP_DONE : BAN_USER_TEMP_DONE;
                case WARN -> WARN_USER_TEMP_DONE;
            };
        }
    }

    @NotNull
    public static LangKey getPunishBroadcast(@NotNull Punishment punishment) {
        boolean isIp = punishment.isIp();
        if (punishment.isPermanent()) {
            return switch (punishment.getType()) {
                case MUTE -> isIp ? MUTE_IP_PERMA_BROADCAST : MUTE_USER_PERMA_BROADCAST;
                case BAN -> isIp ? BAN_IP_PERMA_BROADCAST : BAN_USER_PERMA_BROADCAST;
                case WARN -> WARN_USER_PERMA_BROADCAST;
            };
        }
        else {
            return switch (punishment.getType()) {
                case MUTE -> isIp ? MUTE_IP_TEMP_BROADCAST : MUTE_USER_TEMP_BROADCAST;
                case BAN -> isIp ? BAN_IP_TEMP_BROADCAST : BAN_USER_TEMP_BROADCAST;
                case WARN -> WARN_USER_TEMP_BROADCAST;
            };
        }
    }

    @NotNull
    public static LangKey getForgiveSender(@NotNull Punishment punishment) {
        boolean isIp = punishment.isIp();
        return switch (punishment.getType()) {
            case MUTE -> isIp ? UNMUTE_IP_DONE : UNMUTE_USER_DONE;
            case BAN -> isIp ? UNBAN_IP_DONE : UNBAN_USER_DONE;
            case WARN -> UNWARN_USER_DONE;
        };
    }

    @NotNull
    public static LangKey getForgiveBroadcast(@NotNull Punishment punishment) {
        boolean isIp = punishment.isIp();
        return switch (punishment.getType()) {
            case MUTE -> isIp ? UNMUTE_IP_BROADCAST : UNMUTE_USER_BROADCAST;
            case BAN -> isIp ? UNBAN_IP_BROADCAST : UNBAN_USER_BROADCAST;
            case WARN -> UNWARN_USER_BROADCAST;
        };
    }
}
