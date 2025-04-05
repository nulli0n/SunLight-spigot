package su.nightexpress.sunlight.module.chat.config;

import org.bukkit.Sound;
import su.nightexpress.nightcore.language.entry.LangEnum;
import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.entry.LangText;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.chat.module.spy.SpyType;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.sunlight.module.chat.util.Placeholders.*;
import static su.nightexpress.nightcore.language.tag.MessageTags.*;

public class ChatLang extends Lang {

    public static final LangEnum<SpyType> SPY_TYPE = LangEnum.of("Chat.SpyType", SpyType.class);

    public static final LangString COMMAND_ARGUMENT_NAME_CHANNEL = LangString.of("Chat.Commant.Argument.Name.Channel", "channel");

    public static final LangString COMMAND_SHORT_CHANNEL_DESC = LangString.of("Chat.Command.ShortChannel.Desc", "Switch channel or send message.");
    public static final LangString COMMAND_CHANNEL_JOIN_DESC  = LangString.of("Chat.Command.Channel.Join.Desc", "Join a channel.");
    public static final LangString COMMAND_CHANNEL_LEAVE_DESC = LangString.of("Chat.Command.Channel.Leave.Desc", "Leave a channel.");
    public static final LangString COMMAND_CHANNEL_SET_DESC   = LangString.of("Chat.Command.Channel.Set.Desc", "Set active channel.");
    public static final LangString COMMAND_MENTIONS_DESC      = LangString.of("Chat.Command.Mentions.Desc", "Toggle Mentions.");
    public static final LangString COMMAND_TOGGLE_PM_DESC     = LangString.of("Chat.Command.TogglePM.Desc", "Toggle Private Messages.");
    public static final LangString COMMAND_CLEAR_CHAT_DESC    = LangString.of("Chat.Command.ClearChat.Desc", "Clear chat.");
    public static final LangString COMMAND_ME_DESC            = LangString.of("Chat.Command.Me.Desc", "Show action in chat.");
    public static final LangString COMMAND_TELL_DESC          = LangString.of("Chat.Command.Tell.Desc", "Send private message.");
    public static final LangString COMMAND_SPY_MODE_DESC      = LangString.of("Chat.Command.Spy.Mode.Desc", "Toggle " + GENERIC_TYPE + " spy.");
    public static final LangString COMMAND_SPY_LOGGER_DESC    = LangString.of("Chat.Command.Spy.Logger.Desc", "Toggle spy logger.");
    public static final LangString COMMAND_REPLY_DESC         = LangString.of("Chat.Command.Reply.Desc", "Quick reply on latest PM.");


    public static final LangText COMMAND_CLEAR_CHAT_DONE = LangText.of("Chat.Command.ClearChat.Done",
        TAG_NO_PREFIX,
        LIGHT_GRAY.wrap("Chat has been cleared by " + LIGHT_YELLOW.wrap(GENERIC_NAME) + ".")
    );

    public static final LangText COMMAND_REPLY_ERROR_EMPTY = LangText.of("Chat.Command.Reply.Error.Empty",
        TAG_NO_PREFIX,
        LIGHT_RED.wrap("You have not received any private messages.")
    );


    public static final LangText COMMAND_SPY_MODE_DONE_OTHERS = LangText.of("Chat.Command.Spy.Mode.Done.Others",
        LIGHT_GRAY.wrap("Set " + LIGHT_YELLOW.wrap(GENERIC_TYPE) + " spy for " + LIGHT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + " to " + LIGHT_YELLOW.wrap(GENERIC_STATE))
    );

    public static final LangText COMMAND_SPY_MODE_DONE_NOTIFY = LangText.of("Chat.Command.Spy.Mode.Done.Notify",
        LIGHT_GRAY.wrap(LIGHT_YELLOW.wrap(GENERIC_TYPE) + " spy: " + LIGHT_YELLOW.wrap(GENERIC_STATE))
    );

    public static final LangText COMMAND_SPY_LOGGER_DONE = LangText.of("Chat.Command.Spy.Logger.Done",
        LIGHT_GRAY.wrap("Set" + LIGHT_YELLOW.wrap(GENERIC_TYPE) + " logger for " + LIGHT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + " to " + LIGHT_YELLOW.wrap(GENERIC_STATE))
    );


    public static final LangText COMMAND_MENTIONS_TOGGLE_NOTIFY = LangText.of("Chat.Command.Mentions.Toggle.Notify",
        LIGHT_GRAY.wrap("Mention notifications: " + LIGHT_YELLOW.wrap(GENERIC_STATE) + ".")
    );

    public static final LangText COMMAND_MENTIONS_TOGGLE_TARGET = LangText.of("Chat.Command.Mentions.Toggle.Target",
        LIGHT_GRAY.wrap("Set mention notifications on " + LIGHT_YELLOW.wrap(GENERIC_STATE) + " for " + LIGHT_YELLOW.wrap(PLAYER_NAME) + ".")
    );


    public static final LangText COMMAND_TOGGLE_PM_TOGGLE_NOTIFY = LangText.of("Chat.Command.TogglePM.Toggle.Notify",
        LIGHT_GRAY.wrap("Private Messages: " + LIGHT_YELLOW.wrap(GENERIC_STATE) + ".")
    );

    public static final LangText COMMAND_TOGGLE_PM_TOGGLE_TARGET = LangText.of("Chat.Command.TogglePM.Toggle.Target",
        LIGHT_GRAY.wrap("Set Private Messages on " + LIGHT_YELLOW.wrap(GENERIC_STATE) + " for " + LIGHT_YELLOW.wrap(PLAYER_NAME) + ".")
    );


    public static final LangText ANTI_SPAM_SIMILAR_MESSAGE = LangText.of("Chat.Chat.AntiSpam.Similar.Msg",
        TAG_NO_PREFIX,
        LIGHT_RED.wrap("Do not spam messages!")
    );

    public static final LangText ANTI_SPAM_SIMILAR_COMMAND = LangText.of("Chat.Chat.AntiSpam.Similar.Cmd",
        TAG_NO_PREFIX,
        LIGHT_RED.wrap("Do not spam commands!")
    );

    public static final LangText ANTI_SPAM_DELAY_MESSAGE = LangText.of("Chat.Chat.AntiSpam.Delay.Msg",
        TAG_NO_PREFIX,
        LIGHT_RED.wrap("You have to wait " + ORANGE.wrap(GENERIC_TIME) + " before you can send another message.")
    );

    public static final LangText ANTI_SPAM_DELAY_COMMAND = LangText.of("Chat.Chat.AntiSpam.Delay.Cmd",
        TAG_NO_PREFIX,
        LIGHT_RED.wrap("&cYou have to wait " + ORANGE.wrap(GENERIC_TIME) + " before you can use another command.")
    );


    public static final LangText CHANNEL_JOIN_SUCCESS = LangText.of("Chat.Channel.Join.Success",
        TAG_NO_PREFIX,
        LIGHT_GRAY.wrap("You have joined the " + LIGHT_YELLOW.wrap(CHANNEL_NAME) + " chat channel.")
    );

    public static final LangText CHANNEL_JOIN_ERROR_NO_PERMISSION = LangText.of("Chat.Channel.Join.Error.NoPermission",
        TAG_NO_PREFIX,
        LIGHT_RED.wrap("You don't have permissions to join this channel.")
    );

    public static final LangText CHANNEL_JOIN_ERROR_ALREADY_IN = LangText.of("Chat.Channel.Join.Error.AlreadyIn",
        TAG_NO_PREFIX,
        LIGHT_RED.wrap("You already joined this channel!")
    );

    public static final LangText CHANNEL_LEAVE_SUCCESS = LangText.of("Chat.Channel.Leave.Success",
        TAG_NO_PREFIX,
        LIGHT_GRAY.wrap("You have left the " + LIGHT_YELLOW.wrap(CHANNEL_NAME) + " chat channel.")
    );

    public static final LangText CHANNEL_LEAVE_ERROR_NOT_IN = LangText.of("Chat.Channel.Leave.Error.NotIn",
        TAG_NO_PREFIX,
        LIGHT_RED.wrap("You're are not in the channel!")
    );

    public static final LangText CHANNEL_SET_SUCCESS = LangText.of("Chat.Channel.Set.Success",
        TAG_NO_PREFIX,
        LIGHT_GRAY.wrap("Set " + LIGHT_YELLOW.wrap(CHANNEL_NAME) + " as default chat channel.")
    );


    public static final LangText MENTION_NOTIFY = LangText.of("Chat.Mentions.Notify",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.BLOCK_NOTE_BLOCK_BELL),
        LIGHT_GREEN.wrap(BOLD.wrap("Mentioned!")),
        LIGHT_GRAY.wrap(LIGHT_GREEN.wrap(PLAYER_DISPLAY_NAME) + " mentioned you in chat.")
    );

    public static final LangText MENTION_ERROR_COOLDOWN = LangText.of("Chat.Chat.Mention.Error.Cooldown",
        TAG_NO_PREFIX,
        LIGHT_RED.wrap("You can mention " + ORANGE.wrap(GENERIC_NAME) + " again in " + ORANGE.wrap(GENERIC_TIME))
    );


    public static final LangText PRIVATE_MESSAGE_ERROR_DISABLED = LangText.of("Chat.PrivateMessage.Error.Disabled",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(PLAYER_DISPLAY_NAME) + " doesn't accept messages.")
    );

    public static final LangText PRIVATE_MESSAGE_ERROR_IGNORANCE = LangText.of("Chat.PrivateMessage.Error.Ignorance",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(PLAYER_DISPLAY_NAME) + " doesn't accept your messages.")
    );

    public static final LangText ERROR_COMMAND_INVALID_CHANNEL_ARGUMENT = new LangText("Chat.Error.Command.Argument.InvalidChannel",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(GENERIC_VALUE) + " is not a valid channel!")
    );
}
