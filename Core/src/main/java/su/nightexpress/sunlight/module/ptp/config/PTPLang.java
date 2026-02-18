package su.nightexpress.sunlight.module.ptp.config;

import org.bukkit.Sound;
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.locale.message.MessageData;
import su.nightexpress.sunlight.module.ptp.command.PTPCommands;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.*;

public class PTPLang implements LangContainer {

    public static final TextLocale COMMAND_PTP_DESC             = LangEntry.builder("PTP.Command.Root.Desc").text("PTP commands.");
    public static final TextLocale COMMAND_INVITE_DESC          = LangEntry.builder("PTP.Command.Teleport.Invite.Desc").text("Ask player to be summoned.");
    public static final TextLocale COMMAND_REQUEST_DESC         = LangEntry.builder("PTP.Command.Teleport.Request.Desc").text("Ask player to teleport you.");
    public static final TextLocale COMMAND_ACCEPT_DESC          = LangEntry.builder("PTP.Command.Teleport.Accept.Desc").text("Accept teleport request.");
    public static final TextLocale COMMAND_DECLINE_DESC         = LangEntry.builder("PTP.Command.Teleport.Decline.Desc").text("Decline teleport request.");
    public static final TextLocale COMMAND_REQUESTS_TOGGLE_DESC = LangEntry.builder("PTP.Command.Requests.Toggle.Desc").text("Toggle teleport requests.");
    public static final TextLocale COMMAND_REQUESTS_ON_DESC     = LangEntry.builder("PTP.Command.Requests.On.Desc").text("Enable teleport requests.");
    public static final TextLocale COMMAND_REQUESTS_OFF_DESC    = LangEntry.builder("PTP.Command.Requests.Off.Desc").text("Disable teleport requests.");



    public static final MessageLocale REQUESTS_TOGGLE_FEEDBACK = LangEntry.builder("PTP.Command.Toggle.Target").chatMessage(
        GRAY.wrap("You have set " + ORANGE.wrap("Teleport Requests") + " to " + WHITE.wrap(GENERIC_STATE) + " for " + ORANGE.wrap(PLAYER_DISPLAY_NAME) + ".")
    );

    public static final MessageLocale REQUESTS_TOGGLE_NOTIFY = LangEntry.builder("PTP.Command.Toggle.Notify").chatMessage(
        GRAY.wrap("You have set " + ORANGE.wrap("Teleport Requests") + " to: " + WHITE.wrap(GENERIC_STATE) + ".")
    );


    public static final MessageLocale REQUEST_ACCEPT_DONE = LangEntry.builder("PTP.Request.Accept.Done").chatMessage(
        Sound.ENTITY_ENDERMAN_TELEPORT,
        GRAY.wrap("You accepted " + ORANGE.wrap(PLAYER_DISPLAY_NAME) + "'s teleport request!")
    );

    public static final MessageLocale REQUEST_ACCEPT_NOTIFY = LangEntry.builder("PTP.Request.Accept.Notify").chatMessage(
        Sound.ENTITY_ENDERMAN_TELEPORT,
        GRAY.wrap(ORANGE.wrap(PLAYER_DISPLAY_NAME) + " accepted your teleport request!")
    );

    public static final MessageLocale REQUEST_ACCEPT_NOTHING = LangEntry.builder("PTP.Request.Accept.Nothing").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You don't have any active requests.")
    );


    public static final MessageLocale REQUEST_DECLINE_DONE = LangEntry.builder("PTP.Request.Decline.Done").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You declined " + ORANGE.wrap(PLAYER_DISPLAY_NAME) + "'s teleport request.")
    );

    public static final MessageLocale REQUEST_DECLINE_NOTIFY = LangEntry.builder("PTP.Request.Decline.Notify").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap(ORANGE.wrap(PLAYER_DISPLAY_NAME) + " declines your teleport request.")
    );

    public static final MessageLocale REQUEST_DECLINE_NOTHING = LangEntry.builder("PTP.Request.Decline.Nothing").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You don't have any active requests.")
    );


    public static final MessageLocale REQUEST_SEND_TARGET_AFK = LangEntry.builder("PTP.Request.Send.TargetAfk").chatMessage(
        GRAY.wrap(WHITE.wrap(PLAYER_DISPLAY_NAME) + " is AFK and may not respond."));

    public static final MessageLocale REQUEST_SEND_ERROR_COOLDOWN = LangEntry.builder("PTP.Request.Send.Error.Cooldown").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You can send another request in " + ORANGE.wrap(GENERIC_TIME))
    );

    public static final MessageLocale REQUEST_SEND_ERROR_DISABLED = LangEntry.builder("PTP.Request.Send.Error.Disabled").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You can't send teleport requests to " + ORANGE.wrap(PLAYER_NAME) + ".")
    );


    public static final MessageLocale INVITE_SENT = LangEntry.builder("PTP.Command.Teleport.Invite.Notify.Sender").chatMessage(
        Sound.ENTITY_ENDER_PEARL_THROW,
        GRAY.wrap("Sent summon request to " + ORANGE.wrap(PLAYER_DISPLAY_NAME) + ".")
    );

    public static final MessageLocale INVITE_NOTIFY = LangEntry.builder("PTP.Command.Teleport.Invite.Notify.Target").message(
        MessageData.chat().usePrefix(false).sound(Sound.ENTITY_ENDER_PEARL_THROW).build(),
        " ",
        ORANGE.wrap(BOLD.wrap("Teleport Invite:")),
        " ",
        ORANGE.wrap("Player " + ORANGE.wrap(PLAYER_DISPLAY_NAME) + " wants summon you to them."),
        ORANGE.wrap("Type " + ORANGE.wrap("/" + PTPCommands.ACCEPT_NAME + " " + PLAYER_NAME) + " to accept and be summoned."),
        ORANGE.wrap("or " + ORANGE.wrap("/" + PTPCommands.DECLINE_NAME + " " + PLAYER_NAME) + " to decline."),
        " ",
        ORANGE.wrap(" ".repeat(12)
            + SHOW_TEXT.with(GRAY.wrap("Click to be summoned by " + GREEN.wrap(PLAYER_DISPLAY_NAME))).wrap(RUN_COMMAND.with("/" + PTPCommands.ACCEPT_NAME + " " + PLAYER_NAME).wrap(ORANGE.wrap(BOLD.wrap("[Accept]"))))
            + " ".repeat(6)
            + SHOW_TEXT.with(GRAY.wrap("Click to deny summoning by " + RED.wrap(PLAYER_DISPLAY_NAME))).wrap(RUN_COMMAND.with("/" + PTPCommands.DECLINE_NAME + " " + PLAYER_NAME).wrap(ORANGE.wrap(BOLD.wrap("[Decline]"))))
        ),
        " "
    );

    public static final MessageLocale REQUEST_SENT = LangEntry.builder("PTP.Request.Sent").chatMessage(
        Sound.ENTITY_ENDER_PEARL_THROW,
        GRAY.wrap("Sent teleport request to " + ORANGE.wrap(PLAYER_DISPLAY_NAME) + ".")
    );

    public static final MessageLocale REQUEST_NOTIFY = LangEntry.builder("PTP.Request.Notify").message(
        MessageData.chat().usePrefix(false).sound(Sound.ENTITY_ENDER_PEARL_THROW).build(),
        " ",
        ORANGE.wrap(BOLD.wrap("Teleport Request:")),
        " ",
        ORANGE.wrap("Player " + ORANGE.wrap(PLAYER_DISPLAY_NAME) + " wants teleport to you."),
        ORANGE.wrap("Type " + ORANGE.wrap("/" + PTPCommands.ACCEPT_NAME + " " + PLAYER_NAME) + " to accept and summon him."),
        ORANGE.wrap("or " + ORANGE.wrap("/" + PTPCommands.DECLINE_NAME + " " + PLAYER_NAME) + " to decline."),
        " ",
        ORANGE.wrap(" ".repeat(12)
            + SHOW_TEXT.with(GRAY.wrap("Click to teleport " + GREEN.wrap(PLAYER_DISPLAY_NAME))).wrap(RUN_COMMAND.with("/" + PTPCommands.ACCEPT_NAME + " " + PLAYER_NAME).wrap(ORANGE.wrap(BOLD.wrap("[Accept]"))))
            + " ".repeat(6)
            + SHOW_TEXT.with(GRAY.wrap("Click to deny " + RED.wrap(PLAYER_DISPLAY_NAME) + " teleport.")).wrap(RUN_COMMAND.with("/" + PTPCommands.DECLINE_NAME + " " + PLAYER_NAME).wrap(ORANGE.wrap(BOLD.wrap("[Decline]"))))
        ),
        " "
    );
}
