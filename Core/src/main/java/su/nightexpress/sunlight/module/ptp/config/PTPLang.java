package su.nightexpress.sunlight.module.ptp.config;

import org.bukkit.Sound;
import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.entry.LangText;
import su.nightexpress.sunlight.module.ptp.command.AcceptCommands;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.nightcore.language.tag.MessageTags.*;
import static su.nightexpress.sunlight.Placeholders.*;

public class PTPLang extends CoreLang {

    public static final LangString COMMAND_INVITE_DESC  = LangString.of("PTP.Command.Teleport.Invite.Desc", "Ask player to be summoned.");
    public static final LangString COMMAND_REQUEST_DESC = LangString.of("PTP.Command.Teleport.Request.Desc", "Ask player to teleport you.");
    public static final LangString COMMAND_ACCEPT_DESC  = LangString.of("PTP.Command.Teleport.Accept.Desc", "Accept teleport request.");
    public static final LangString COMMAND_DECLINE_DESC = LangString.of("PTP.Command.Teleport.Decline.Desc", "Decline teleport request.");
    public static final LangString COMMAND_TOGGLE_DESC  = LangString.of("PTP.Command.Toggle.Desc", "Toggle teleport requests/invites.");

    public static final LangText COMMAND_TOGGLE_DONE = LangText.of("PTP.Command.Toggle.Target",
        LIGHT_GRAY.wrap("Set teleport requests " + LIGHT_YELLOW.wrap(GENERIC_STATE) + " for " + LIGHT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + ".")
    );

    public static final LangText COMMAND_TOGGLE_NOTIFY = LangText.of("PTP.Command.Toggle.Notify",
        LIGHT_GRAY.wrap("Teleport requests: " + LIGHT_YELLOW.wrap(GENERIC_STATE) + ".")
    );


    public static final LangText REQUEST_ACCEPT_DONE = LangText.of("PTP.Request.Accept.Done",
        SOUND.wrap(Sound.ENTITY_ENDERMAN_TELEPORT),
        LIGHT_GRAY.wrap("You accepted " + LIGHT_GREEN.wrap(PLAYER_DISPLAY_NAME) + "'s teleport request!")
    );

    public static final LangText REQUEST_ACCEPT_NOTIFY = LangText.of("PTP.Request.Accept.Notify",
        SOUND.wrap(Sound.ENTITY_ENDERMAN_TELEPORT),
        LIGHT_GRAY.wrap(LIGHT_GREEN.wrap(PLAYER_DISPLAY_NAME) + " accepted your teleport request!")
    );

    public static final LangText REQUEST_ACCEPT_NOTHING = LangText.of("PTP.Request.Accept.Nothing",
        SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_GRAY.wrap("You don't have any active requests.")
    );


    public static final LangText REQUEST_DECLINE_DONE = LangText.of("PTP.Request.Decline.Done",
        SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_GRAY.wrap("You declined " + LIGHT_RED.wrap(PLAYER_DISPLAY_NAME) + "'s teleport request.")
    );

    public static final LangText REQUEST_DECLINE_NOTIFY = LangText.of("PTP.Request.Decline.Notify",
        SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(PLAYER_DISPLAY_NAME) + " declines your teleport request.")
    );

    public static final LangText REQUEST_DECLINE_NOTHING = LangText.of("PTP.Request.Decline.Nothing",
        SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_GRAY.wrap("You don't have any active requests.")
    );


    public static final LangText REQUEST_SEND_ERROR_COOLDOWN = LangText.of("PTP.Request.Send.Error.Cooldown",
        SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_GRAY.wrap("You can send another request in " + LIGHT_RED.wrap(GENERIC_TIME))
    );

    public static final LangText REQUEST_SEND_ERROR_DISABLED = LangText.of("PTP.Request.Send.Error.Disabled",
        SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_GRAY.wrap("You can't send teleport requests to " + LIGHT_RED.wrap(PLAYER_NAME) + ".")
    );


    public static final LangText INVITE_SENT = LangText.of("PTP.Command.Teleport.Invite.Notify.Sender",
        TAG_NO_PREFIX + SOUND.wrap(Sound.ENTITY_ENDER_PEARL_THROW),
        LIGHT_GRAY.wrap("Sent summon request to " + LIGHT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + ".")
    );

    public static final LangText INVITE_NOTIFY = LangText.of("PTP.Command.Teleport.Invite.Notify.Target",
        TAG_NO_PREFIX + SOUND.wrap(Sound.ENTITY_ENDER_PEARL_THROW),
        " ",
        LIGHT_YELLOW.wrap(BOLD.wrap("Teleport Invite:")),
        " ",
        LIGHT_YELLOW.wrap("Player " + ORANGE.wrap(PLAYER_DISPLAY_NAME) + " wants summon you to them."),
        LIGHT_YELLOW.wrap("Type " + LIGHT_GREEN.wrap("/" + AcceptCommands.ACCEPT_NAME + " " + PLAYER_NAME) + " to accept and be summoned."),
        LIGHT_YELLOW.wrap("or " + LIGHT_RED.wrap("/" + AcceptCommands.DECLINE_NAME + " " + PLAYER_NAME) + " to decline."),
        " ",
        LIGHT_YELLOW.wrap(" ".repeat(12)
            + HOVER.wrapShowText(CLICK.wrapRunCommand(LIGHT_GREEN.wrap(BOLD.wrap("[Accept]")), "/" + AcceptCommands.ACCEPT_NAME + " " + PLAYER_NAME), GRAY.wrap("Click to be summoned by " + GREEN.wrap(PLAYER_DISPLAY_NAME)))
            + " ".repeat(6)
            + HOVER.wrapShowText(CLICK.wrapRunCommand(LIGHT_RED.wrap(BOLD.wrap("[Decline]")), "/" + AcceptCommands.DECLINE_NAME + " " + PLAYER_NAME), GRAY.wrap("Click to deny summoning by " + RED.wrap(PLAYER_DISPLAY_NAME)))
        ),
        " "
    );

    public static final LangText REQUEST_SENT = LangText.of("PTP.Request.Sent",
        TAG_NO_PREFIX + SOUND.wrap(Sound.ENTITY_ENDER_PEARL_THROW),
        LIGHT_GRAY.wrap("Sent teleport request to " + LIGHT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + ".")
    );

    public static final LangText REQUEST_NOTIFY = LangText.of("PTP.Request.Notify",
        TAG_NO_PREFIX + SOUND.wrap(Sound.ENTITY_ENDER_PEARL_THROW),
        " ",
        LIGHT_YELLOW.wrap(BOLD.wrap("Teleport Request:")),
        " ",
        LIGHT_YELLOW.wrap("Player " + ORANGE.wrap(PLAYER_DISPLAY_NAME) + " wants teleport to you."),
        LIGHT_YELLOW.wrap("Type " + LIGHT_GREEN.wrap("/" + AcceptCommands.ACCEPT_NAME + " " + PLAYER_NAME) + " to accept and summon him."),
        LIGHT_YELLOW.wrap("or " + LIGHT_RED.wrap("/" + AcceptCommands.DECLINE_NAME + " " + PLAYER_NAME) + " to decline."),
        " ",
        LIGHT_YELLOW.wrap(" ".repeat(12)
            + HOVER.wrapShowText(CLICK.wrapRunCommand(LIGHT_GREEN.wrap(BOLD.wrap("[Accept]")), "/" + AcceptCommands.ACCEPT_NAME + " " + PLAYER_NAME), GRAY.wrap("Click to teleport " + GREEN.wrap(PLAYER_DISPLAY_NAME)))
            + " ".repeat(6)
            + HOVER.wrapShowText(CLICK.wrapRunCommand(LIGHT_RED.wrap(BOLD.wrap("[Decline]")), "/" + AcceptCommands.DECLINE_NAME + " " + PLAYER_NAME), GRAY.wrap("Click to deny " + RED.wrap(PLAYER_DISPLAY_NAME) + " teleport."))
        ),
        " "
    );
}
