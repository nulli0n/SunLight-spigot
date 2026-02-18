package su.nightexpress.sunlight.module.bans.config;

import org.bukkit.Sound;
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.EnumLocale;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.locale.message.MessageData;
import su.nightexpress.nightcore.util.sound.VanillaSound;
import su.nightexpress.nightcore.util.text.night.wrapper.TagWrapper;
import su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers;
import su.nightexpress.sunlight.SLSharedConsts;
import su.nightexpress.sunlight.module.bans.menu.SortMode;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.module.bans.BansPlaceholders.*;

public class BansLang implements LangContainer {

    private static final TagWrapper RED_GRADIENT   = TagWrappers.GRADIENT_3.with("#FF0000", "#D11717", "#B51F1F");

    public static final EnumLocale<SortMode>       SORT_MODE       = LangEntry.builder("Bans.SortMode").enumeration(SortMode.class);
    public static final EnumLocale<PunishmentType> PUNISHMENT_TYPE = LangEntry.builder("Bans.PunishmentType").enumeration(PunishmentType.class);

    public static final TextLocale COMMAND_ARGUMENT_NAME_REASON    = LangEntry.builder("Bans.Command.Argument.Name.Reason").text("reason");
    public static final TextLocale COMMAND_ARGUMENT_NAME_TIME_UNIT = LangEntry.builder("Bans.Command.Argument.Name.TimeUnit").text("time unit");

    public static final TextLocale COMMAND_BAN_HISTORY_DESC  = LangEntry.builder("Bans.Command.History.Ban.Desc").text("List player's bans history.");
    public static final TextLocale COMMAND_MUTE_HISTORY_DESC = LangEntry.builder("Bans.Command.History.Mute.Desc").text("List player's mutes history.");
    public static final TextLocale COMMAND_WARN_HISTORY_DESC = LangEntry.builder("Bans.Command.History.Warn.Desc").text("List player's warns history.");
    public static final TextLocale COMMAND_BAN_LIST_DESC     = LangEntry.builder("Bans.Command.BanList.Desc").text("List active bans.");
    public static final TextLocale COMMAND_MUTE_LIST_DESC    = LangEntry.builder("Bans.Command.MuteList.Desc").text("List active mutes.");
    public static final TextLocale COMMAND_WARN_LIST_DESC    = LangEntry.builder("Bans.Command.WarnList.Desc").text("List active warns.");
    public static final TextLocale COMMAND_BAN_DESC          = LangEntry.builder("Bans.Command.Ban.Desc").text("Ban player.");
    public static final TextLocale COMMAND_BAN_IP_DESC       = LangEntry.builder("Bans.Command.Banip.Desc").text("Ban IP address.");
    public static final TextLocale COMMAND_MUTE_DESC         = LangEntry.builder("Bans.Command.Mute.Desc").text("Mute player.");
    public static final TextLocale COMMAND_KICK_DESC         = LangEntry.builder("Bans.Command.Kick.Desc").text("Kick player.");
    public static final TextLocale COMMAND_UNMUTE_DESC       = LangEntry.builder("Bans.Command.Unmute.Desc").text("Unmute player.");
    public static final TextLocale COMMAND_UNBAN_DESC        = LangEntry.builder("Bans.Command.Unban.Desc").text("Unban player.");
    public static final TextLocale COMMAND_UNBAN_IP_DESC     = LangEntry.builder("Bans.Command.UnbanIP.Desc").text("Unban IP address.");
    public static final TextLocale COMMAND_WARN_DESC         = LangEntry.builder("Bans.Command.Warn.Desc").text("Warn player.");
    public static final TextLocale COMMAND_UNWARN_DESC       = LangEntry.builder("Bans.Command.Unwarn.Desc").text("Remove latest player's warn.");
    public static final TextLocale COMMAND_ALTS_DESC         = LangEntry.builder("Bans.Command.Alts.Desc").text("List player's alt accounts.");

    public static final MessageLocale COMMAND_SYNTAX_INVALID_TIME_UNIT = LangEntry.builder("Bans.Command.Syntax.InvalidTimeUnit").chatMessage(
        GRAY.wrap(TagWrappers.SOFT_RED.wrap(GENERIC_INPUT) + " is not a valid time unit."));

    public static final MessageLocale COMMAND_SYNTAX_INVALID_REASON = LangEntry.builder("Bans.Error.Command.Argument.InvalidReason").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_VALUE) + " is not a valid reason."));



    public static final MessageLocale ALTS_GLOBAL_NOTHING = LangEntry.builder("Bans.Alts.Global.Nothing").chatMessage(
        GRAY.wrap("Player " + ORANGE.wrap(PLAYER_NAME) + " has no known alt accounts.")
    );

    public static final MessageLocale ALTS_GLOBAL_LIST = LangEntry.builder("Bans.Alts.Global.List").message(
        MessageData.CHAT_NO_PREFIX,
        " ",
        DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(7)) + RED_GRADIENT.and(BOLD).wrap(" ALT DETECTED ") + DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(7)),
        GRAY.wrap("Player " + RED.wrap(PLAYER_NAME) + " (IP: " + WHITE.wrap(GENERIC_ADDRESS) + ")" + " has " + RED.wrap(GENERIC_AMOUNT) + " possible alt account(s):"),
        GENERIC_ENTRY,
        DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(30)),
        " "
    );

    public static final TextLocale ALTS_GLOBAL_ENTRY = LangEntry.builder("Bans.Alts.Global.Entry").text(
        DARK_GRAY.wrap("» ") + RUN_COMMAND.with("/" + SLSharedConsts.COMMAND_PLAYER_INFO + " " + GENERIC_NAME).wrap(SHOW_TEXT.with(GRAY.wrap("Click for more details.")).wrap(RED.wrap(GENERIC_NAME)))
    );



    public static final MessageLocale IMMUNITY_FEEDBACK = LangEntry.builder("Bans.Immunity.Feedback").chatMessage(
        GRAY.wrap(ORANGE.wrap(GENERIC_TARGET) + " is immune to punishments.")
    );

    // -------------------------------------
    // Kicks
    // -------------------------------------

    public static final MessageLocale KICK_ERROR_YOURSELF = LangEntry.builder("Bans.Kick.Error.Yourself").chatMessage(
        GRAY.wrap( "You can't kick " + ORANGE.wrap("yourself") + ".")
    );

    public static final MessageLocale KICK_ERROR_LOW_PRIORITY = LangEntry.builder("Bans.Kick.Error.LowPriority").chatMessage(
        GRAY.wrap( "You're not allowed to kick " + ORANGE.wrap(PLAYER_DISPLAY_NAME) + ".")
    );

    public static final MessageLocale KICK_FEEDBACK = LangEntry.builder("Bans.Kick.Done").chatMessage(
        GRAY.wrap("You have kicked " + ORANGE.wrap(PLAYER_DISPLAY_NAME) + " due to " + WHITE.wrap(GENERIC_REASON) + ".")
    );

    public static final MessageLocale KICK_BROADCAST = LangEntry.builder("Bans.Kick.Broadcast").message(
        MessageData.CHAT_NO_PREFIX,
        " ",
        DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(7)) + RED_GRADIENT.and(BOLD).wrap(" PLAYER KICKED ") + DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(7)),
        GRAY.wrap(DARK_GRAY.wrap("»") + " Player " + RED.wrap(PLAYER_DISPLAY_NAME) + " has been kicked."),
        GRAY.wrap(DARK_GRAY.wrap("»") + " Kicked by: " + WHITE.wrap(GENERIC_EXECUTOR)),
        GRAY.wrap(DARK_GRAY.wrap("»") + " Reason: " + WHITE.wrap(GENERIC_REASON)),
        DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(30)),
        " "
    );



    public static final MessageLocale BAN_ERROR_YOURSELF = LangEntry.builder("Bans.Ban.Error.Yourself").chatMessage(
        GRAY.wrap( "You can't ban " + ORANGE.wrap("yourself") + ".")
    );

    public static final MessageLocale BAN_ERROR_LOW_PRIORITY = LangEntry.builder("Bans.Ban.Error.LowPriority").chatMessage(
        GRAY.wrap( "You're not allowed to ban " + ORANGE.wrap(PLAYER_NAME) + ".")
    );

    public static final MessageLocale BAN_ERROR_HIGH_DURATION = LangEntry.builder("Bans.Ban.Error.HighDuration").chatMessage(
        GRAY.wrap("You're not allowed to ban players for more than " + ORANGE.wrap(GENERIC_TIME) + ".")
    );

    public static final MessageLocale BAN_ERROR_HIGHER_EXISTS = LangEntry.builder("Bans.Ban.Error.HigherExists").chatMessage(
        GRAY.wrap(ORANGE.wrap(PUNISHMENT_TARGET) + " is already banned for a longer period.")
    );

    // -------------------------------------
    // Bans - Permanent
    // -------------------------------------

    public static final MessageLocale BAN_PERMANENT_FEEDBACK = LangEntry.builder("Bans.Command.Ban.Done.Permanent").chatMessage(
        GRAY.wrap("You have banned " + ORANGE.wrap(PUNISHMENT_TARGET) + " permanently due to " + WHITE.wrap(PUNISHMENT_REASON))
    );

    public static final MessageLocale BAN_PERMANENT_BROADCAST = LangEntry.builder("Bans.Ban.Permanent.Broadcast").message(
        MessageData.chat().usePrefix(false).sound(VanillaSound.of(Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f)).build(),
        " ",
        DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(7)) + RED_GRADIENT.and(BOLD).wrap(" PLAYER BANNED ") + DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(7)),
        GRAY.wrap(DARK_GRAY.wrap("»") + " Player " + RED.wrap(PUNISHMENT_TARGET) + " has been banned permanently."),
        GRAY.wrap(DARK_GRAY.wrap("»") + " Banned by: " + WHITE.wrap(PUNISHMENT_WHO)),
        GRAY.wrap(DARK_GRAY.wrap("»") + " Reason: " + WHITE.wrap(PUNISHMENT_REASON)),
        DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(30)),
        " "
    );

    // -------------------------------------
    // Bans - Temporary
    // -------------------------------------

    public static final MessageLocale BAN_TEMPORARY_FEEDBACK = LangEntry.builder("Bans.Command.Ban.Done.Temporary").chatMessage(
        GRAY.wrap("You have banned " + ORANGE.wrap(PUNISHMENT_TARGET) + " for " + WHITE.wrap(PUNISHMENT_DURATION) + " due to " + WHITE.wrap(PUNISHMENT_REASON) + ".")
    );

    public static final MessageLocale BAN_TEMPORARY_BROADCAST = LangEntry.builder("Bans.Ban.Temporary.Broadcast").message(
        MessageData.chat().usePrefix(false).sound(VanillaSound.of(Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f)).build(),
        " ",
        DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(7)) + RED_GRADIENT.and(BOLD).wrap(" PLAYER BANNED ") + DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(7)),
        GRAY.wrap(DARK_GRAY.wrap("»") + " Player " + RED.wrap(PUNISHMENT_TARGET) + " has been banned for " + WHITE.wrap(PUNISHMENT_DURATION)),
        GRAY.wrap(DARK_GRAY.wrap("»") + " Banned by: " + WHITE.wrap(PUNISHMENT_WHO)),
        GRAY.wrap(DARK_GRAY.wrap("»") + " Reason: " + WHITE.wrap(PUNISHMENT_REASON)),
        DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(30)),
        " "
    );

    // -------------------------------------
    // Bans IP - Permanent
    // -------------------------------------

    public static final MessageLocale BAN_INET_PERMANENT_FEEDBACK = LangEntry.builder("Bans.Command.BanIP.Done.Permanent").chatMessage(
        GRAY.wrap("You have banned " + ORANGE.wrap(PUNISHMENT_TARGET) + " permanently due to " + WHITE.wrap(PUNISHMENT_REASON) + ".")
    );

    public static final MessageLocale BAN_INET_PERMANENT_BROADCAST = LangEntry.builder("Bans.BanIP.Permanent.Broadcast").message(
        MessageData.chat().usePrefix(false).sound(VanillaSound.of(Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f)).build(),
        " ",
        DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(7)) + RED_GRADIENT.and(BOLD).wrap(" IP BANNED ") + DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(7)),
        GRAY.wrap(DARK_GRAY.wrap("»") + " IP " + WHITE.wrap(PUNISHMENT_TARGET) + " have been banned permanently."),
        GRAY.wrap(DARK_GRAY.wrap("»") + " Banned by: " + RED.wrap(PUNISHMENT_WHO)),
        GRAY.wrap(DARK_GRAY.wrap("»") + " Reason: " + RED.wrap(PUNISHMENT_REASON)),
        DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(30)),
        " "
    );

    // -------------------------------------
    // Bans IP - Temporary
    // -------------------------------------

    public static final MessageLocale BAN_INET_TEMPORARY_FEEDBACK = LangEntry.builder("Bans.Command.BanIP.Done.Temporary").chatMessage(
        GRAY.wrap("You have banned " + ORANGE.wrap(PUNISHMENT_TARGET) + " for " + WHITE.wrap(PUNISHMENT_DURATION) + " due to " + WHITE.wrap(PUNISHMENT_REASON) + ".")
    );

    public static final MessageLocale BAN_INET_TEMPORARY_BROADCAST = LangEntry.builder("Bans.BanIP.Temporary.Broadcast").message(
        MessageData.chat().usePrefix(false).sound(VanillaSound.of(Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f)).build(),
        " ",
        DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(7)) + RED_GRADIENT.and(BOLD).wrap(" IP BANNED ") + DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(7)),
        GRAY.wrap(DARK_GRAY.wrap("»") + " IP " + WHITE.wrap(PUNISHMENT_TARGET) + " have been banned for " + RED.wrap(PUNISHMENT_DURATION)),
        GRAY.wrap(DARK_GRAY.wrap("»") + " Banned by: " + RED.wrap(PUNISHMENT_WHO)),
        GRAY.wrap(DARK_GRAY.wrap("»") + " Reason: " + RED.wrap(PUNISHMENT_REASON)),
        DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(30)),
        " "
    );

    // -------------------------------------
    // Bans - Removal
    // -------------------------------------

    public static final MessageLocale UNBAN_ERROR_HIGH_DURATION = LangEntry.builder("Bans.Unban.Error.HighDuration").chatMessage(
        GRAY.wrap("You're not allowed to unban players whose remaining ban time is greater than " + ORANGE.wrap(GENERIC_TIME) + ".")
    );

    public static final MessageLocale UNBAN_PLAYER_ERROR_NOT_BANNED = LangEntry.builder("Bans.Punishment.Error.PlayerNotBanned").chatMessage(
        GRAY.wrap("Player " + ORANGE.wrap(PUNISHMENT_TARGET) + " is not banned.")
    );

    public static final MessageLocale UNBAN_PLAYER_FEEDBACK = LangEntry.builder("Bans.UnBan.Done").chatMessage(
        GRAY.wrap("You have unbanned " + ORANGE.wrap(PUNISHMENT_TARGET) + ".")
    );

    public static final MessageLocale UNBAN_PLAYER_BROADCAST = LangEntry.builder("Bans.UnBan.Broadcast").chatMessage(
        GRAY.wrap(WHITE.wrap(GENERIC_EXECUTOR) + " has unbanned " + ORANGE.wrap(PUNISHMENT_TARGET) + ".")
    );

    // -------------------------------------
    // Bans IP - Removal
    // -------------------------------------

    public static final MessageLocale UNBAN_INET_ERROR_NOT_BANNED = LangEntry.builder("Bans.Punishment.Error.IPNotBanned").chatMessage(
        GRAY.wrap("IP Address " + ORANGE.wrap(GENERIC_TARGET) + " is not banned.")
    );

    public static final MessageLocale UNBAN_INET_FEEDBACK = LangEntry.builder("Bans.UnBanIP.Done").chatMessage(
        GRAY.wrap("You have unbanned " + ORANGE.wrap(PUNISHMENT_TARGET) + ".")
    );

    public static final MessageLocale UNBAN_INET_BROADCAST = LangEntry.builder("Bans.UnBanIP.Broadcast").chatMessage(
        GRAY.wrap(WHITE.wrap(GENERIC_EXECUTOR) + " has unbanned " + ORANGE.wrap(PUNISHMENT_TARGET) + ".")
    );



    public static final MessageLocale MUTE_ERROR_YOURSELF = LangEntry.builder("Bans.Mute.Error.Yourself").chatMessage(
        GRAY.wrap( "You can't mute " + ORANGE.wrap("yourself") + ".")
    );

    public static final MessageLocale MUTE_ERROR_LOW_PRIORITY = LangEntry.builder("Bans.Mute.Error.LowPriority").chatMessage(
        GRAY.wrap( "You're not allowed to mute " + ORANGE.wrap(PLAYER_NAME) + ".")
    );

    public static final MessageLocale MUTE_ERROR_HIGH_DURATION = LangEntry.builder("Bans.Mute.Error.HighDuration").chatMessage(
        GRAY.wrap("You're not allowed to mute players for more than " + ORANGE.wrap(GENERIC_TIME) + ".")
    );

    public static final MessageLocale MUTE_ERROR_HIGHER_EXISTS = LangEntry.builder("Bans.Mute.Error.HigherExists").chatMessage(
        GRAY.wrap(WHITE.wrap(PUNISHMENT_TARGET) + " is already muted for a longer period.")
    );

    public static final MessageLocale MUTE_SPEAK_NOTIFY_TEMPORAL = LangEntry.builder("Bans.Mute.Speak.Notify").chatMessage(
        GRAY.wrap("You're muted. You can speak again in " + ORANGE.wrap(PUNISHMENT_EXPIRES_IN))
    );

    public static final MessageLocale MUTE_SPEAK_NOTIFY_PERMANENT = LangEntry.builder("Bans.Mute.Speak.Notify").chatMessage(
        GRAY.wrap("You're permanently muted. You can speak again once a " + ORANGE.wrap("staff member") + " unmutes you manually.")
    );

    // -------------------------------------
    // Mutes - Permanent
    // -------------------------------------

    public static final MessageLocale MUTE_PERMANENT_FEEDBACK = LangEntry.builder("Bans.Command.Mute.Done.Permanent").chatMessage(
        GRAY.wrap("You have muted " + ORANGE.wrap(PUNISHMENT_TARGET) + " permanently due to " + WHITE.wrap(PUNISHMENT_REASON) + ".")
    );

    public static final MessageLocale MUTE_PERMANENT_NOTIFY = LangEntry.builder("Bans.Mute.Permanent.Notify").chatMessage(
        GRAY.wrap("You have been muted permanently. " + SHOW_TEXT.with(
            DARK_GRAY.wrap("»") + GRAY.wrap(" Muted by: ") + WHITE.wrap(PUNISHMENT_WHO)
                + BR
                + DARK_GRAY.wrap("»") + GRAY.wrap(" Reason: ") + WHITE.wrap(PUNISHMENT_REASON)
        ).wrap(ORANGE.and(BOLD).wrap("[HOVER FOR DETAILS]")))
    );

    public static final MessageLocale MUTE_PERMANENT_BROADCAST = LangEntry.builder("Bans.Mute.Permanent.Broadcast").message(
        MessageData.chat().usePrefix(false).sound(VanillaSound.of(Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f)).build(),
        " ",
        DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(7)) + RED_GRADIENT.and(BOLD).wrap(" PLAYER MUTED ") + DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(7)),
        GRAY.wrap(DARK_GRAY.wrap("»") + " Player " + RED.wrap(PUNISHMENT_TARGET) + " has been muted permanently."),
        GRAY.wrap(DARK_GRAY.wrap("»") + " Muted by: " + WHITE.wrap(PUNISHMENT_WHO)),
        GRAY.wrap(DARK_GRAY.wrap("»") + " Reason: " + WHITE.wrap(PUNISHMENT_REASON)),
        DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(30)),
        " "
    );

    // -------------------------------------
    // Mutes - Temporary
    // -------------------------------------

    public static final MessageLocale MUTE_TEMPORARY_FEEDBACK = LangEntry.builder("Bans.Command.Mute.Done.Temporary").chatMessage(
        GRAY.wrap("You have muted " + ORANGE.wrap(PUNISHMENT_TARGET) + " for " + WHITE.wrap(PUNISHMENT_DURATION) + " due to " + WHITE.wrap(PUNISHMENT_REASON) + ".")
    );

    public static final MessageLocale MUTE_TEMPORARY_NOTIFY = LangEntry.builder("Bans.Mute.Temporary.Notify").chatMessage(
        GRAY.wrap("You have been muted (" + WHITE.wrap(PUNISHMENT_DURATION + "). " + SHOW_TEXT.with(
            DARK_GRAY.wrap("»") + GRAY.wrap(" Muted by: ") + WHITE.wrap(PUNISHMENT_WHO)
                + BR
                + DARK_GRAY.wrap("»") + GRAY.wrap(" Reason: ") + WHITE.wrap(PUNISHMENT_REASON)
        ).wrap(ORANGE.and(BOLD).wrap("[HOVER FOR DETAILS]"))))
    );

    public static final MessageLocale MUTE_TEMPORARY_BROADCAST = LangEntry.builder("Bans.Mute.Temporary.Broadcast").message(
        MessageData.chat().usePrefix(false).sound(VanillaSound.of(Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f)).build(),
        " ",
        DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(7)) + RED_GRADIENT.and(BOLD).wrap(" PLAYER MUTED ") + DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(7)),
        GRAY.wrap(DARK_GRAY.wrap("»") + " Player " + RED.wrap(PUNISHMENT_TARGET) + " have been muted for " + WHITE.wrap(PUNISHMENT_DURATION)),
        GRAY.wrap(DARK_GRAY.wrap("»") + " Muted by: " + WHITE.wrap(PUNISHMENT_WHO)),
        GRAY.wrap(DARK_GRAY.wrap("»") + " Reason: " + WHITE.wrap(PUNISHMENT_REASON)),
        DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(30)),
        " "
    );

    // -------------------------------------
    // Mutes - Removal
    // -------------------------------------

    public static final MessageLocale UNMUTE_ERROR_NOT_MUTED = LangEntry.builder("Bans.Punishment.Error.NotMuted").chatMessage(
        GRAY.wrap("Player " + ORANGE.wrap(PUNISHMENT_TARGET) + " is not muted.")
    );

    public static final MessageLocale UNMUTE_ERROR_HIGH_DURATION = LangEntry.builder("Bans.Unmute.Error.HighDuration").chatMessage(
        GRAY.wrap("You're not allowed to unmute players whose remaining mute time is greater than " + ORANGE.wrap(GENERIC_TIME) + ".")
    );

    public static final MessageLocale UNMUTE_SUCCESS_FEEDBACK = LangEntry.builder("Bans.Unmute.Done").chatMessage(
        GRAY.wrap("You have unmuted " + ORANGE.wrap(GENERIC_EXECUTOR) + ".")
    );

    public static final MessageLocale UNMUTE_SUCCESS_NOTIFY = LangEntry.builder("Bans.Unmute.Notify").chatMessage(
        GRAY.wrap("You have been unmuted by " + WHITE.wrap(GENERIC_EXECUTOR) + ".")
    );

    public static final MessageLocale UNMUTE_SUCCESS_BROADCAST = LangEntry.builder("Bans.Unmute.Broadcast").chatMessage(
        GRAY.wrap("Player " + ORANGE.wrap(PUNISHMENT_TARGET) + " has been unmuted by " + WHITE.wrap(GENERIC_EXECUTOR) + ".")
    );

    // ======= WARNS =======

    public static final MessageLocale WARN_ERROR_YOURSELF = LangEntry.builder("Bans.Warn.Error.Yourself").chatMessage(
        GRAY.wrap( "You can't warn " + ORANGE.wrap("yourself") + ".")
    );

    public static final MessageLocale WARN_ERROR_LOW_PRIORITY = LangEntry.builder("Bans.Warn.Error.LowPriority").chatMessage(
        GRAY.wrap( "You're not allowed to warn " + ORANGE.wrap(PLAYER_NAME) + ".")
    );

    public static final MessageLocale WARN_ERROR_HIGH_DURATION = LangEntry.builder("Bans.Warn.Error.HighDuration").chatMessage(
        GRAY.wrap("You're not allowed to warn players for more than " + ORANGE.wrap(GENERIC_TIME) + ".")
    );


    // -------------------------------------
    // Warns - Permanent
    // -------------------------------------

    public static final MessageLocale WARN_PERMANENT_FEEDBACK = LangEntry.builder("Bans.Command.Warn.Done.Permanent").chatMessage(
        GRAY.wrap("You have warned " + ORANGE.wrap(PUNISHMENT_TARGET) + " permanently due to " + WHITE.wrap(PUNISHMENT_REASON) + ".")
    );

    public static final MessageLocale WARN_PERMANENT_NOTIFY = LangEntry.builder("Bans.Warn.Permanent.Notify").chatMessage(
        GRAY.wrap("You have been warned permanently. " + SHOW_TEXT.with(
            DARK_GRAY.wrap("»") + GRAY.wrap(" Warned by: ") + WHITE.wrap(PUNISHMENT_WHO)
                + BR
                + DARK_GRAY.wrap("»") + GRAY.wrap(" Reason: ") + WHITE.wrap(PUNISHMENT_REASON)
        ).wrap(ORANGE.and(BOLD).wrap("[HOVER FOR DETAILS]")))
    );

    public static final MessageLocale WARN_PERMANENT_BROADCAST = LangEntry.builder("Bans.Warn.Permanent.Broadcast").message(
        MessageData.chat().usePrefix(false).sound(VanillaSound.of(Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f)).build(),
        " ",
        DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(7)) + RED_GRADIENT.and(BOLD).wrap(" PLAYER WARNED ") + DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(7)),
        GRAY.wrap(DARK_GRAY.wrap("»") + " Player " + RED.wrap(PUNISHMENT_TARGET) + " has been warned permanently"),
        GRAY.wrap(DARK_GRAY.wrap("»") + " By: " + WHITE.wrap(PUNISHMENT_WHO)),
        GRAY.wrap(DARK_GRAY.wrap("»") + " Reason: " + WHITE.wrap(PUNISHMENT_REASON)),
        DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(30)),
        " "
    );

    // -------------------------------------
    // Warns - Temporary
    // -------------------------------------

    public static final MessageLocale WARN_TEMPORARY_FEEDBACK = LangEntry.builder("Bans.Command.Warn.Done.Temporary").chatMessage(
        GRAY.wrap("You have warned " + ORANGE.wrap(PUNISHMENT_TARGET) + " for " + WHITE.wrap(PUNISHMENT_DURATION) + " due to " + WHITE.wrap(PUNISHMENT_REASON) + ".")
    );

    public static final MessageLocale WARN_TEMPORARY_NOTIFY = LangEntry.builder("Bans.Warn.Temporary.Notify").chatMessage(
        GRAY.wrap("You have been warned (" + WHITE.wrap(PUNISHMENT_DURATION + "). " + SHOW_TEXT.with(
            DARK_GRAY.wrap("»") + GRAY.wrap(" Warned by: ") + WHITE.wrap(PUNISHMENT_WHO)
                + BR
                + DARK_GRAY.wrap("»") + GRAY.wrap(" Reason: ") + WHITE.wrap(PUNISHMENT_REASON)
        ).wrap(ORANGE.and(BOLD).wrap("[HOVER FOR DETAILS]"))))
    );

    public static final MessageLocale WARN_TEMPORARY_BROADCAST = LangEntry.builder("Bans.Warn.Temporary.Broadcast").message(
        MessageData.chat().usePrefix(false).sound(VanillaSound.of(Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5f)).build(),
        " ",
        DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(7)) + RED_GRADIENT.and(BOLD).wrap(" PLAYER WARNED ") + DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(7)),
        GRAY.wrap(DARK_GRAY.wrap("»") + " Player " + RED.wrap(PUNISHMENT_TARGET) + " has been warned for " + WHITE.wrap(PUNISHMENT_DURATION)),
        GRAY.wrap(DARK_GRAY.wrap("»") + " By: " + WHITE.wrap(PUNISHMENT_WHO)),
        GRAY.wrap(DARK_GRAY.wrap("»") + " Reason: " + WHITE.wrap(PUNISHMENT_REASON)),
        DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(30)),
        " "
    );

    // -------------------------------------
    // Warns - Removal
    // -------------------------------------

    public static final MessageLocale UNWARN_ERROR_NOT_WARNED = LangEntry.builder("Bans.Punishment.Error.NotWarned").chatMessage(
        GRAY.wrap("Player " + ORANGE.wrap(PUNISHMENT_TARGET) + " has no active warns to remove.")
    );

    public static final MessageLocale UNWARN_ERROR_HIGH_DURATION = LangEntry.builder("Bans.Unwarn.Error.HighDuration").chatMessage(
        GRAY.wrap("You're not allowed to unwarn players whose remaining warn time is greater than " + ORANGE.wrap(GENERIC_TIME) + ".")
    );

    public static final MessageLocale UNWARN_SUCCESS_FEEDBACK = LangEntry.builder("Bans.Unwarn.Done").chatMessage(
        GRAY.wrap("You have removed all warns from " + ORANGE.wrap(PUNISHMENT_TARGET) + ".")
    );

    public static final MessageLocale UNWARN_SUCCESS_NOTIFY = LangEntry.builder("Bans.Unwarn.Notify").chatMessage(
        GRAY.wrap(WHITE.wrap(GENERIC_EXECUTOR) + " has removed all warns from you.")
    );

    public static final MessageLocale UNWARN_SUCCESS_BROADCAST = LangEntry.builder("Bans.Unwarn.Broadcast").chatMessage(
        GRAY.wrap(WHITE.wrap(GENERIC_EXECUTOR) + " has removed all warns from " + ORANGE.wrap(PUNISHMENT_TARGET) + ".")
    );

    // -------------------------------------

    public static final MessageLocale ERROR_DATA_NOT_LOADED_USER_FEEDBACK = LangEntry.builder("Bans.Error.DataNotLoaded.UserFeedback").chatMessage(
        SOFT_RED.wrap("Systems are reloading, please try again later.")
    );

    public static final MessageLocale ERROR_DATA_NOT_LOADED_ADMIN_FEEDBACK = LangEntry.builder("Bans.Error.DataNotLoaded.AdminFeedback").chatMessage(
        SOFT_RED.wrap("Action is unavailable, data is currently loading from the database...")
    );

    public static final TextLocale OTHER_NO_REASON = LangEntry.builder("Bans.Other.NoReason").text("<no reason>");
}
