package su.nightexpress.sunlight.module.chat.config;

import su.nexmedia.engine.api.server.JPermission;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.module.chat.command.ClearchatCommand;
import su.nightexpress.sunlight.module.chat.command.MeCommand;
import su.nightexpress.sunlight.module.chat.command.channel.ChatChannelCommand;
import su.nightexpress.sunlight.module.chat.command.pm.TogglePMCommand;
import su.nightexpress.sunlight.module.chat.command.spy.ChatSpyCommand;
import su.nightexpress.sunlight.module.chat.command.spy.LoggerSubCommand;
import su.nightexpress.sunlight.module.chat.command.spy.ModeSubCommand;
import su.nightexpress.sunlight.module.chat.command.pm.ReplyCommand;
import su.nightexpress.sunlight.module.chat.command.pm.TellCommand;

public class ChatPerms {

    private static final String PREFIX = Perms.PREFIX + "chat.";
    private static final String PREFIX_COMMAND = PREFIX + "command.";
    private static final String PREFIX_BYPASS  = PREFIX + "bypass.";
    private static final String PREFIX_MENTION = PREFIX + "mention.";

    public static final String MENTION_PLAYER  = PREFIX_MENTION + "player.";
    public static final String MENTION_SPECIAL = PREFIX_MENTION + "special.";
    public static final String CHANNEL_HEAR    = PREFIX + "channel.hear.";
    public static final String CHANNEL_SPEAK   = PREFIX + "channel.speak.";
    public static final String COMMAND_CHANNEL = PREFIX_COMMAND + "channel.";

    public static final JPermission CHAT         = new JPermission(PREFIX + Placeholders.WILDCARD, "Full access to the Chat Module.");
    public static final JPermission CHAT_COMMAND = new JPermission(PREFIX_COMMAND + Placeholders.WILDCARD, "Access to all commands of the Chat Module.");
    public static final JPermission CHAT_BYPASS  = new JPermission(PREFIX_BYPASS + Placeholders.WILDCARD, "Bypasses all the Chat restrictions.");
    public static final JPermission MENTION      = new JPermission(PREFIX_MENTION + Placeholders.WILDCARD, "Allows to mention anyone.");
    public static final JPermission COLOR        = new JPermission(PREFIX + "color", "Allows to use colors in messages and commands.");

    public static final JPermission COMMAND_CLEARCHAT   = new JPermission(PREFIX_COMMAND + ClearchatCommand.NAME);
    public static final JPermission COMMAND_CHATCHANNEL = new JPermission(PREFIX_COMMAND + ChatChannelCommand.NAME, "Access to the 'chatchannel' command.");
    public static final JPermission COMMAND_ME              = new JPermission(PREFIX_COMMAND + MeCommand.NAME, "Access to the 'me' command.");
    public static final JPermission COMMAND_REPLY           = new JPermission(PREFIX_COMMAND + ReplyCommand.NAME, "Access to the 'reply' command.");
    public static final JPermission COMMAND_TELL            = new JPermission(PREFIX_COMMAND + TellCommand.NAME, "Access to the 'tell' command.");
    public static final JPermission COMMAND_SPY             = new JPermission(PREFIX_COMMAND + ChatSpyCommand.NAME, "Access to the 'chatspy' command (without sub-commands).");
    public static final JPermission COMMAND_SPY_MODE        = new JPermission(PREFIX_COMMAND + ModeSubCommand.NAME, "Access to the 'mode' sub-command of the 'chatspy' command.");
    public static final JPermission COMMAND_SPY_MODE_OTHERS = new JPermission(PREFIX_COMMAND + ModeSubCommand.NAME + ".others", "Access to the 'mode' sub-command of the 'chatspy' command on other players.");
    public static final JPermission COMMAND_SPY_LOGGER      = new JPermission(PREFIX_COMMAND + LoggerSubCommand.NAME, "Access to the 'logger' sub-command of the 'chatspy' command.");
    public static final JPermission COMMAND_TOGGLE_PM = new JPermission(PREFIX_COMMAND + TogglePMCommand.NAME, "Access to the 'togglepm' sub-command.");
    public static final JPermission COMMAND_TOGGLE_PM_OTHERS = new JPermission(PREFIX_COMMAND + TogglePMCommand.NAME + ".others", "Access to the 'togglepm' sub-command on other players.");

    public static final JPermission BYPASS_PM_DISABLED = new JPermission(PREFIX_BYPASS + "pm.disabled", "Allows to send Private Messages even if target player disabled them.");
    public static final JPermission BYPASS_MENTION_COOLDOWN       = new JPermission(PREFIX_BYPASS + "mention.cooldown", "Bypasses Mentions cooldown.");
    public static final JPermission BYPASS_MENTION_AMOUNT         = new JPermission(PREFIX_BYPASS + "mention.amount", "Bypasses Mentions maximum limit.");
    public static final JPermission BYPASS_CHANNEL_DISTANCE_HEAR  = new JPermission(PREFIX_BYPASS + "channel.distance.hear", "Bypasses channel distance when receiving message from a channel.");
    public static final JPermission BYPASS_CHANNEL_DISTANCE_SPEAK = new JPermission(PREFIX_BYPASS + "channel.distance.speak", "Bypasses channel distance when sending message in a channel.");
    public static final JPermission BYPASS_COOLDOWN_MESSAGE       = new JPermission(PREFIX_BYPASS + "cooldown.message", "Bypasses the channel messages cooldown.");
    public static final JPermission BYPASS_COOLDOWN_COMMAND       = new JPermission(PREFIX_BYPASS + "cooldown.command", "Bypasses the 'AntiSpam' commands cooldown.");
    public static final JPermission BYPASS_ANTICAPS               = new JPermission(PREFIX_BYPASS + "anticaps", "Bypasses the AntiCaps auto-moderaition.");
    public static final JPermission BYPASS_ANTISPAM               = new JPermission(PREFIX_BYPASS + "antispam", "Bypasses the AntiSpam auto-moderation.");
    public static final JPermission BYPASS_RULES                  = new JPermission(PREFIX_BYPASS + "rules", "Bypasses all the custom rules.");

    static {
        Perms.PLUGIN.addChildren(CHAT);

        CHAT.addChildren(CHAT_COMMAND, CHAT_BYPASS, MENTION, COLOR);

        CHAT_COMMAND.addChildren(COMMAND_CHATCHANNEL, COMMAND_ME, COMMAND_REPLY, COMMAND_TELL, COMMAND_SPY,
            COMMAND_SPY_MODE, COMMAND_SPY_MODE_OTHERS, COMMAND_SPY_LOGGER,
            COMMAND_CLEARCHAT);

        CHAT_BYPASS.addChildren(
            BYPASS_PM_DISABLED,
            BYPASS_MENTION_COOLDOWN, BYPASS_MENTION_AMOUNT,
            BYPASS_CHANNEL_DISTANCE_HEAR, BYPASS_CHANNEL_DISTANCE_SPEAK,
            BYPASS_COOLDOWN_COMMAND, BYPASS_COOLDOWN_MESSAGE, BYPASS_ANTICAPS, BYPASS_ANTISPAM, BYPASS_RULES);
    }
}
