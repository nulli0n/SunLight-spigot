package su.nightexpress.sunlight.module.chat.core;

import org.bukkit.Sound;
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.EnumLocale;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.locale.message.MessageData;
import su.nightexpress.sunlight.module.chat.channel.ChannelDistanceType;
import su.nightexpress.sunlight.module.chat.spy.SpyType;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.*;
import static su.nightexpress.sunlight.module.chat.ChatPlaceholders.*;

public class ChatLang implements LangContainer {

    public static final EnumLocale<ChannelDistanceType> CHANNEL_DISTANCE_TYPE = LangEntry.builder("Chat.ChannelDistanceType").enumeration(ChannelDistanceType.class);
    public static final EnumLocale<SpyType>             SPY_TYPE              = LangEntry.builder("Chat.SpyType").enumeration(SpyType.class);

    public static final TextLocale COMMAND_ARGUMENT_NAME_CHANNEL = LangEntry.builder("Chat.Commant.Argument.Name.Channel").text("channel");

    public static final TextLocale COMMAND_CHANNEL_ROOT_DESC  = LangEntry.builder("Chat.Command.Channel.Root.Desc").text("Chat channel commands.");
    public static final TextLocale COMMAND_CHANNEL_JOIN_DESC  = LangEntry.builder("Chat.Command.Channel.Join.Desc").text("Join a channel.");
    public static final TextLocale COMMAND_CHANNEL_LEAVE_DESC = LangEntry.builder("Chat.Command.Channel.Leave.Desc").text("Leave a channel.");

    public static final TextLocale COMMAND_MENTIONS_ROOT_DESC   = LangEntry.builder("Chat.Command.Mentions.Root.Desc").text("Chat mentions commands.");
    public static final TextLocale COMMAND_MENTIONS_TOGGLE_DESC = LangEntry.builder("Chat.Command.Mentions.Toggle.Desc").text("Toggle chat mentions.");
    public static final TextLocale COMMAND_MENTIONS_ON_DESC     = LangEntry.builder("Chat.Command.Mentions.On.Desc").text("Enable chat mentions.");
    public static final TextLocale COMMAND_MENTIONS_OFF_DESC    = LangEntry.builder("Chat.Command.Mentions.Off.Desc").text("Disable chat mentions.");

    public static final TextLocale COMMAND_CONVERSATIONS_ROOT_DESC   = LangEntry.builder("Chat.Command.Conversations.On.Desc").text("Conversations commands.");
    public static final TextLocale COMMAND_CONVERSATIONS_ON_DESC     = LangEntry.builder("Chat.Command.Conversations.On.Desc").text("Allow private messages from players.");
    public static final TextLocale COMMAND_CONVERSATIONS_OFF_DESC    = LangEntry.builder("Chat.Command.Conversations.Off.Desc").text("Deny private messages from players.");
    public static final TextLocale COMMAND_CONVERSATIONS_TOGGLE_DESC = LangEntry.builder("Chat.Command.Conversations.Toggle.Desc").text("Toggle private messages from players.");

    public static final TextLocale COMMAND_CLEAR_CHAT_DESC = LangEntry.builder("Chat.Command.ClearChat.Desc").text("Clear chat.");
    public static final TextLocale COMMAND_ME_DESC         = LangEntry.builder("Chat.Command.Me.Desc").text("Show action in chat.");
    public static final TextLocale COMMAND_TELL_DESC       = LangEntry.builder("Chat.Command.Tell.Desc").text("Send private message.");
    public static final TextLocale COMMAND_SPY_MODE_TOGGLE_DESC   = LangEntry.builder("Chat.Command.SpyMode.Toggle.Desc").text("Toggle " + GENERIC_TYPE + " spy.");
    public static final TextLocale COMMAND_SPY_MODE_ON_DESC   = LangEntry.builder("Chat.Command.SpyMode.On.Desc").text("Enable " + GENERIC_TYPE + " spy.");
    public static final TextLocale COMMAND_SPY_MODE_OFF_DESC   = LangEntry.builder("Chat.Command.SpyMode.Off.Desc").text("Disable " + GENERIC_TYPE + " spy.");
    public static final TextLocale COMMAND_SPY_LOGGER_DESC = LangEntry.builder("Chat.Command.SpyLogger.Desc").text("Toggle spy logger.");
    public static final TextLocale COMMAND_REPLY_DESC      = LangEntry.builder("Chat.Command.Reply.Desc").text("Quick reply on latest PM.");

    public static final MessageLocale COMMAND_SYNTAX_INVALID_CHANNEL = LangEntry.builder("Chat.Error.Command.Argument.InvalidChannel").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_INPUT) + " is not a valid channel.")
    );


    public static final MessageLocale CLEAR_CHAT_NOTIFY = LangEntry.builder("Chat.Command.ClearChat.Done").chatMessage(
        GRAY.wrap("Chat has been cleared by " + ORANGE.wrap(GENERIC_NAME) + ".")
    );

    public static final MessageLocale COMMAND_REPLY_ERROR_EMPTY = LangEntry.builder("Chat.Command.Reply.Error.Empty").message(
        MessageData.CHAT_NO_PREFIX,
        SOFT_RED.wrap("You have not received any private messages.")
    );



    public static final MessageLocale SPY_MODE_TOGGLE_FEEDBACK = LangEntry.builder("Chat.Command.Spy.Mode.Done.Others").chatMessage(
        GRAY.wrap("You have set " + ORANGE.wrap(PLAYER_DISPLAY_NAME) + "'s " + WHITE.wrap(GENERIC_TYPE) + " spy mode to " + WHITE.wrap(GENERIC_STATE) + ".")
    );

    public static final MessageLocale SPY_MODE_TOGGLE_NOTIFY = LangEntry.builder("Chat.Command.Spy.Mode.Done.Notify").chatMessage(
        GRAY.wrap("Your " + ORANGE.wrap(GENERIC_TYPE) + " spy mode has been set to " + WHITE.wrap(GENERIC_STATE))
    );

    public static final MessageLocale SPY_LOGGER_TOGGLE_FEEDBACK = LangEntry.builder("Chat.Command.Spy.Logger.Done").chatMessage(
        GRAY.wrap("You have set" + WHITE.wrap(GENERIC_TYPE) + " spy logger for " + ORANGE.wrap(PLAYER_DISPLAY_NAME) + " to " + WHITE.wrap(GENERIC_STATE) + ".")
    );



    public static final MessageLocale MENTIONS_TOGGLE_NOTIFY = LangEntry.builder("Chat.Command.Mentions.Toggle.Notify").chatMessage(
        GRAY.wrap("You have set chat mentions to " + WHITE.wrap(GENERIC_STATE) + ".")
    );

    public static final MessageLocale MENTIONS_TOGGLE_FEEDBACK = LangEntry.builder("Chat.Command.Mentions.Toggle.Target").chatMessage(
        GRAY.wrap("You have set " + ORANGE.wrap(PLAYER_DISPLAY_NAME) + "'s chat mentions on " + WHITE.wrap(GENERIC_STATE) + ".")
    );



    public static final MessageLocale ANTI_FLOOD_SIMILAR_CONTENT = LangEntry.builder("Chat.Chat.AntiSpam.Similar.Msg").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("Please do not flood with the same content.")
    );

    public static final MessageLocale ANTI_FLOOD_COMMAND_COOLDOWN = LangEntry.builder("Chat.Chat.AntiSpam.Similar.Cmd").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("Please do not spam commands.")
    );



    public static final MessageLocale CHANNEL_MESSAGE_COOLDOWN = LangEntry.builder("Chat.Chat.AntiSpam.Delay.Msg").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("Please wait " + ORANGE.wrap(GENERIC_TIME) + " before send another message.")
    );

    public static final MessageLocale CHANNEL_SPEAK_NO_PERMISSION = LangEntry.builder("Chat.Channel.Speak.NoPermission").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You don't have permissions to speak in this channel.")
    );

    public static final MessageLocale CHANNEL_JOIN_SUCCESS = LangEntry.builder("Chat.Channel.Join.Success").chatMessage(
        Sound.BLOCK_WOODEN_DOOR_OPEN,
        GRAY.wrap("You have joined the " + WHITE.wrap(CHANNEL_NAME) + " channel.")
    );

    public static final MessageLocale CHANNEL_JOIN_ERROR_NO_PERMISSION = LangEntry.builder("Chat.Channel.Join.Error.NoPermission").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You don't have permissions to join this channel.")
    );

    public static final MessageLocale CHANNEL_JOIN_ERROR_ALREADY_IN = LangEntry.builder("Chat.Channel.Join.Error.AlreadyIn").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You already joined this channel!")
    );

    public static final MessageLocale CHANNEL_LEAVE_SUCCESS = LangEntry.builder("Chat.Channel.Leave.Success").chatMessage(
        Sound.BLOCK_WOODEN_DOOR_CLOSE,
        GRAY.wrap("You have left the " + WHITE.wrap(CHANNEL_NAME) + " channel.")
    );

    public static final MessageLocale CHANNEL_LEAVE_ERROR_NOT_IN = LangEntry.builder("Chat.Channel.Leave.Error.NotIn").message(
        MessageData.CHAT_NO_PREFIX,
        GRAY.wrap("You're are not in the channel!")
    );

    public static final MessageLocale CHANNEL_NOBODY_HERE = LangEntry.builder("Chat.Channel.NobodyHere").chatMessage(
        GRAY.wrap("Nobody heard you.")
    );


    public static final MessageLocale MENTION_NOTIFY = LangEntry.builder("Chat.Mentions.Notify").titleMessage(
        GREEN.wrap(BOLD.wrap("Mentioned!")),
        GRAY.wrap(GREEN.wrap(PLAYER_DISPLAY_NAME) + " mentioned you in chat."),
        Sound.BLOCK_NOTE_BLOCK_BELL
    );

    public static final MessageLocale MENTION_ERROR_COOLDOWN = LangEntry.builder("Chat.Chat.Mention.Error.Cooldown").chatMessage(
        GRAY.wrap("You can mention " + WHITE.wrap(GENERIC_NAME) + " again in " + ORANGE.wrap(GENERIC_TIME))
    );



    public static final MessageLocale ITEM_SHOW_NOTHING = LangEntry.builder("Chat.ItemShow.Nothing").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You must hold an item in hand to show it in chat.")
    );



    public static final MessageLocale CONVERSATIONS_TOGGLE_NOTIFY = LangEntry.builder("Chat.Command.TogglePM.Toggle.Notify").chatMessage(
        GRAY.wrap("Your conversations have been set on " + WHITE.wrap(GENERIC_STATE) + ".")
    );

    public static final MessageLocale CONVERSATIONS_TOGGLE_FEEDBACK = LangEntry.builder("Chat.Command.TogglePM.Toggle.Target").chatMessage(
        GRAY.wrap("You have set " + ORANGE.wrap(PLAYER_DISPLAY_NAME) + "'s conversations on " + WHITE.wrap(GENERIC_STATE) + ".")
    );

    public static final MessageLocale CONVERSATIONS_SEND_YOURSELF = LangEntry.builder("Chat.PrivateMessage.Error.Yourself").chatMessage(
        GRAY.wrap("You can't send private messages to " + ORANGE.wrap("yourself") + ".")
    );

    public static final MessageLocale CONVERSATIONS_SEND_DENIED = LangEntry.builder("Chat.PrivateMessage.Error.Disabled").chatMessage(
        GRAY.wrap(ORANGE.wrap(PLAYER_DISPLAY_NAME) + " has conversations disabled.")
    );

    public static final MessageLocale CONVERSATIONS_SEND_BLOCKED = LangEntry.builder("Chat.PrivateMessage.Error.Ignorance").chatMessage(
        GRAY.wrap(ORANGE.wrap(PLAYER_DISPLAY_NAME) + " is not accepting conversations with you.")
    );

    public static final MessageLocale CONVERSATIONS_TARGET_AFK = LangEntry.builder("Chat.Conversations.TargetAfk").chatMessage(
        GRAY.wrap(WHITE.wrap(PLAYER_DISPLAY_NAME) + " is AFK and may not respond."));
}
