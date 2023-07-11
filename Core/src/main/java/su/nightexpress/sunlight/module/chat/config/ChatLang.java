package su.nightexpress.sunlight.module.chat.config;

import su.nexmedia.engine.api.lang.LangKey;
import su.nightexpress.sunlight.module.chat.util.Placeholders;

public class ChatLang {

    private static final String NO_PREFIX = "<! prefix:\"false\" !>";

    public static final LangKey Chat_AntiSpam_Similar_Msg = new LangKey("Chat.Chat.AntiSpam.Similar.Msg", NO_PREFIX + "&cDo not spam messages!");
    public static final LangKey Chat_AntiSpam_Similar_Cmd = new LangKey("Chat.Chat.AntiSpam.Similar.Cmd", NO_PREFIX + "&cDo not spam commands!");
    public static final LangKey Chat_AntiSpam_Delay_Msg   = new LangKey("Chat.Chat.AntiSpam.Delay.Msg", NO_PREFIX + "&cYou have to wait &c" + Placeholders.GENERIC_TIME + " &cbefore you can send another message.");
    public static final LangKey Chat_AntiSpam_Delay_Cmd   = new LangKey("Chat.Chat.AntiSpam.Delay.Cmd", NO_PREFIX + "&cYou have to wait &c" + Placeholders.GENERIC_TIME + " &cbefore you can use another command.");
    public static final LangKey CHAT_MENTION_ERROR_COOLDOWN = LangKey.of("Chat.Chat.Mention.Error.Cooldown", NO_PREFIX + "&cYou can mention &e" + Placeholders.GENERIC_NAME + "&c again in &e%time%&c.");

    public static final LangKey COMMAND_SHORT_CHANNEL_DESC  = new LangKey("Chat.Command.ShortChannel.Desc", "Switch chat channel or send a message to channel");
    public static final LangKey COMMAND_SHORT_CHANNEL_USAGE = new LangKey("Chat.Command.ShortChannel.Usage", "[message]");
    public static final LangKey COMMAND_CHANNEL_DESC        = new LangKey("Chat.Command.Channel.Desc", "Manage your chat channels.");
    public static final LangKey COMMAND_CHANNEL_JOIN_DESC  = new LangKey("Chat.Command.Join.Desc", "Join the channel.");
    public static final LangKey COMMAND_CHANNEL_JOIN_USAGE  = new LangKey("Chat.Command.Channel.Join.Usage", "<channel>");
    public static final LangKey COMMAND_CHANNEL_LEAVE_DESC  = new LangKey("Chat.Command.Channel.Leave.Desc", "Leave the channel.");
    public static final LangKey COMMAND_CHANNEL_LEAVE_USAGE = new LangKey("Chat.Command.Channel.Leave.Usage", "<channel>");
    public static final LangKey COMMAND_CHANNEL_SET_DESC  = new LangKey("Chat.Command.Channel.Set.Desc", "Set default channel.");
    public static final LangKey COMMAND_CHANNEL_SET_USAGE = new LangKey("Chat.Command.Channel.Set.Usage", "<channel>");

    public static final LangKey Channel_Join_Success            = new LangKey("Chat.Channel.Join.Success", NO_PREFIX + "&7You joined the &a" + Placeholders.CHANNEL_NAME + " &7chat channel.");
    public static final LangKey Channel_Join_Error_NoPermission = new LangKey("Chat.Channel.Join.Error.NoPermission", NO_PREFIX + "&cYou don't have permissions to join this channel.");
    public static final LangKey Channel_Join_Error_AlreadyIn    = new LangKey("Chat.Channel.Join.Error.AlreadyIn", NO_PREFIX + "&cYou already joined this channel!");
    public static final LangKey Channel_Leave_Success           = new LangKey("Chat.Channel.Leave.Success", NO_PREFIX + "&7You have left the &c" + Placeholders.CHANNEL_NAME + "&7 chat channel.");
    public static final LangKey Channel_Leave_Error_NotIn       = new LangKey("Chat.Channel.Leave.Error.NotIn", NO_PREFIX + "&cYou're are not in the channel!");
    public static final LangKey Channel_Set_Success             = new LangKey("Chat.Channel.Set.Success", NO_PREFIX + "&7Set default channel: &e" + Placeholders.CHANNEL_NAME + "&7.");
    public static final LangKey Channel_Error_Invalid           = new LangKey("Chat.Channel.Error.Invalid", NO_PREFIX + "&cInvalid channel!");

    public static final LangKey COMMAND_CLEAR_CHAT_DESC = new LangKey("Chat.Command.ClearChat.Desc", "Clear chat.");
    public static final LangKey COMMAND_CLEAR_CHAT_DONE = new LangKey("Chat.Command.ClearChat.Done", NO_PREFIX + "&7Chat has been cleared by &a" + Placeholders.PLAYER_DISPLAY_NAME + "&7.");
    public static final LangKey COMMAND_ME_DESC         = new LangKey("Chat.Command.Me.Desc", "Show action in chat.");
    public static final LangKey COMMAND_ME_USAGE             = new LangKey("Chat.Command.Me.Usage", "<action>");
    public static final LangKey COMMAND_REPLY_DESC           = new LangKey("Chat.Command.Reply.Desc", "Quick reply on the latest private message.");
    public static final LangKey COMMAND_REPLY_USAGE          = new LangKey("Chat.Command.Reply.Usage", "<message>");
    public static final LangKey COMMAND_REPLY_ERROR_EMPTY    = new LangKey("Chat.Command.Reply.Error.Empty", NO_PREFIX + "&cYou have not received any private messages.");
    public static final LangKey COMMAND_TELL_DESC            = new LangKey("Chat.Command.Tell.Desc", "Send private message to a player.");
    public static final LangKey COMMAND_TELL_USAGE           = new LangKey("Chat.Command.Tell.Usage", "<player> <message>");
    public static final LangKey COMMAND_SPY_DESC             = new LangKey("Chat.Command.Spy.Desc", "Chat Spy Features");
    public static final LangKey COMMAND_SPY_USAGE            = new LangKey("Chat.Command.Spy.Usage", "[help]");
    public static final LangKey COMMAND_SPY_MODE_DESC        = new LangKey("Chat.Command.Spy.Mode.Desc", "Toggle the specified Spy Mode.");
    public static final LangKey COMMAND_SPY_MODE_USAGE       = new LangKey("Chat.Command.Spy.Mode.Usage", "<type> [player] [state]");
    public static final LangKey COMMAND_SPY_MODE_DONE_OTHERS = new LangKey("Chat.Command.Spy.Mode.Done.Others", "Set &c" + Placeholders.GENERIC_TYPE + " &7Spy Mode for &c" + Placeholders.PLAYER_DISPLAY_NAME + " &7to &f" + Placeholders.GENERIC_STATE);
    public static final LangKey COMMAND_SPY_MODE_DONE_NOTIFY = new LangKey("Chat.Command.Spy.Mode.Done.Notify", "&c" + Placeholders.GENERIC_TYPE + " &7Spy Mode: &f" + Placeholders.GENERIC_STATE);
    public static final LangKey COMMAND_SPY_LOGGER_DESC      = new LangKey("Chat.Command.Spy.Logger.Desc", "Adds or removes specified Spy Logger to a player.");
    public static final LangKey COMMAND_SPY_LOGGER_USAGE     = new LangKey("Chat.Command.Spy.Logger.Usage", "<add | remove> <type> <player>");
    public static final LangKey COMMAND_SPY_LOGGER_DONE      = new LangKey("Chat.Command.Spy.Logger.Done.Add", "Set &c" + Placeholders.GENERIC_TYPE + " &7Spy Logger for &c" + Placeholders.PLAYER_DISPLAY_NAME + " &7to &f" + Placeholders.GENERIC_STATE);
    //public static final LangKey Channel_Speak_NoPermission = new LangKey(this, "&cYou don't have permission to speak in this chat channel!");
}
