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
        LIGHT_GRAY.enclose("Set teleport requests " + LIGHT_YELLOW.enclose(GENERIC_STATE) + " for " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + ".")
    );

    public static final LangText COMMAND_TOGGLE_NOTIFY = LangText.of("PTP.Command.Toggle.Notify",
        LIGHT_GRAY.enclose("Teleport requests: " + LIGHT_YELLOW.enclose(GENERIC_STATE) + ".")
    );


    public static final LangText REQUEST_ACCEPT_DONE = LangText.of("PTP.Request.Accept.Done",
        SOUND.enclose(Sound.ENTITY_ENDERMAN_TELEPORT),
        LIGHT_GRAY.enclose("You accepted " + LIGHT_GREEN.enclose(PLAYER_DISPLAY_NAME) + "'s teleport request!")
    );

    public static final LangText REQUEST_ACCEPT_NOTIFY = LangText.of("PTP.Request.Accept.Notify",
        SOUND.enclose(Sound.ENTITY_ENDERMAN_TELEPORT),
        LIGHT_GRAY.enclose(LIGHT_GREEN.enclose(PLAYER_DISPLAY_NAME) + " accepted your teleport request!")
    );

    public static final LangText REQUEST_ACCEPT_NOTHING = LangText.of("PTP.Request.Accept.Nothing",
        SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_YELLOW + "You don't have any active requests."
    );


    public static final LangText REQUEST_DECLINE_DONE = LangText.of("PTP.Request.Decline.Done",
        SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_GRAY.enclose("You declined " + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + "'s teleport request.")
    );

    public static final LangText REQUEST_DECLINE_NOTIFY = LangText.of("PTP.Request.Decline.Notify",
        SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " declines your teleport request.")
    );

    public static final LangText REQUEST_DECLINE_NOTHING = LangText.of("PTP.Request.Decline.Nothing",
        SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_GRAY.enclose("You don't have any active requests.")
    );


    public static final LangText REQUEST_SEND_ERROR_COOLDOWN = LangText.of("PTP.Request.Send.Error.Cooldown",
        SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_GRAY.enclose("You can send another request in " + LIGHT_RED.enclose(GENERIC_TIME))
    );

    public static final LangText REQUEST_SEND_ERROR_DISABLED = LangText.of("PTP.Request.Send.Error.Disabled",
        SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_GRAY.enclose("You can't send teleport requests to " + LIGHT_RED.enclose(PLAYER_NAME) + ".")
    );


    public static final LangText INVITE_SENT = LangText.of("PTP.Command.Teleport.Invite.Notify.Sender",
        TAG_NO_PREFIX + SOUND.enclose(Sound.ENTITY_ENDER_PEARL_THROW),
        LIGHT_GRAY.enclose("Sent summon request to " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + ".")
    );

    public static final LangText INVITE_NOTIFY = LangText.of("PTP.Command.Teleport.Invite.Notify.Target",
        TAG_NO_PREFIX + SOUND.enclose(Sound.ENTITY_ENDER_PEARL_THROW),
        " ",
        LIGHT_YELLOW.enclose(BOLD.enclose("Teleport Invite:")),
        " ",
        LIGHT_YELLOW.enclose("Player " + ORANGE.enclose(PLAYER_DISPLAY_NAME) + " wants summon you to them."),
        LIGHT_YELLOW.enclose("Type " + LIGHT_GREEN.enclose("/" + AcceptCommands.ACCEPT_NAME + " " + PLAYER_NAME) + " to accept and be summoned."),
        LIGHT_YELLOW.enclose("or " + LIGHT_RED.enclose("/" + AcceptCommands.DECLINE_NAME + " " + PLAYER_NAME) + " to decline."),
        " ",
        LIGHT_YELLOW.enclose(" ".repeat(12)
            + HOVER.encloseHint(CLICK.encloseRun(LIGHT_GREEN.enclose(BOLD.enclose("[Accept]")), "/" + AcceptCommands.ACCEPT_NAME + " " + PLAYER_NAME), GRAY.enclose("Click to be summoned by " + GREEN.enclose(PLAYER_DISPLAY_NAME)))
            + " ".repeat(6)
            + HOVER.encloseHint(CLICK.encloseRun(LIGHT_RED.enclose(BOLD.enclose("[Decline]")), "/" + AcceptCommands.DECLINE_NAME + " " + PLAYER_NAME), GRAY.enclose("Click to deny summoning by " + RED.enclose(PLAYER_DISPLAY_NAME)))
        ),
        " "
    );

    public static final LangText REQUEST_SENT = LangText.of("PTP.Request.Sent",
        TAG_NO_PREFIX + SOUND.enclose(Sound.ENTITY_ENDER_PEARL_THROW),
        LIGHT_GRAY.enclose("Sent teleport request to " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + ".")
    );

    public static final LangText REQUEST_NOTIFY = LangText.of("PTP.Request.Notify",
        TAG_NO_PREFIX + SOUND.enclose(Sound.ENTITY_ENDER_PEARL_THROW),
        " ",
        LIGHT_YELLOW.enclose(BOLD.enclose("Teleport Request:")),
        " ",
        LIGHT_YELLOW.enclose("Player " + ORANGE.enclose(PLAYER_DISPLAY_NAME) + " wants teleport to you."),
        LIGHT_YELLOW.enclose("Type " + LIGHT_GREEN.enclose("/" + AcceptCommands.ACCEPT_NAME + " " + PLAYER_NAME) + " to accept and summon him."),
        LIGHT_YELLOW.enclose("or " + LIGHT_RED.enclose("/" + AcceptCommands.DECLINE_NAME + " " + PLAYER_NAME) + " to decline."),
        " ",
        LIGHT_YELLOW.enclose(" ".repeat(12)
            + HOVER.encloseHint(CLICK.encloseRun(LIGHT_GREEN.enclose(BOLD.enclose("[Accept]")), "/" + AcceptCommands.ACCEPT_NAME + " " + PLAYER_NAME), GRAY.enclose("Click to teleport " + GREEN.enclose(PLAYER_DISPLAY_NAME)))
            + " ".repeat(6)
            + HOVER.encloseHint(CLICK.encloseRun(LIGHT_RED.enclose(BOLD.enclose("[Decline]")), "/" + AcceptCommands.DECLINE_NAME + " " + PLAYER_NAME), GRAY.enclose("Click to deny " + RED.enclose(PLAYER_DISPLAY_NAME) + " teleport."))
        ),
        " "
    );
}
