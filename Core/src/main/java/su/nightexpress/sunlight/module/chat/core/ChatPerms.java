package su.nightexpress.sunlight.module.chat.core;

import org.bukkit.permissions.Permission;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.config.Perms;

public class ChatPerms {

    public static final PermissionTree ROOT           = Perms.detached("chat");
    public static final PermissionTree COMMAND        = ROOT.branch("command");
    public static final PermissionTree BYPASS         = ROOT.branch("bypass");
    public static final PermissionTree MENTION        = ROOT.branch("mention");
    public static final PermissionTree CHANNEL_LISTEN = ROOT.branch("channel.hear");
    public static final PermissionTree CHANNEL_SPEAK  = ROOT.branch("channel.speak");

    public static final Permission COMMAND_CHANNEL_ROOT  = COMMAND.permission("channel.root");
    public static final Permission COMMAND_CHANNEL_JOIN  = COMMAND.permission("channel.join");
    public static final Permission COMMAND_CHANNEL_LEAVE = COMMAND.permission("channel.leave");

    public static final Permission COMMAND_CLEARCHAT = COMMAND.permission("clearchat");
    public static final Permission COMMAND_ME        = COMMAND.permission("me");

    public static final Permission COMMAND_MENTIONS        = COMMAND.permission("mentions");
    public static final Permission COMMAND_MENTIONS_OTHERS = COMMAND.permission("mentions.others");
    public static final Permission COMMAND_MENTIONS_ROOT   = COMMAND.permission("mentions.root");

    public static final Permission COMMAND_SPY_LOGGER         = COMMAND.permission("spy.logger");
    public static final Permission COMMAND_SPY_CHAT           = COMMAND.permission("spy.chat");
    public static final Permission COMMAND_SPY_CHAT_OTHERS    = COMMAND.permission("spy.chat.others");
    public static final Permission COMMAND_SPY_COMMAND        = COMMAND.permission("spy.command");
    public static final Permission COMMAND_SPY_COMMAND_OTHERS = COMMAND.permission("spy.command.others");
    public static final Permission COMMAND_SPY_SOCIAL         = COMMAND.permission("spy.social");
    public static final Permission COMMAND_SPY_SOCIAL_OTEHRS  = COMMAND.permission("spy.social.others");

    public static final Permission COMMAND_CONVERSATIONS_ROOT   = COMMAND.permission("conversations.root");
    public static final Permission COMMAND_CONVERSATIONS_TOGGLE = COMMAND.permission("conversations");
    public static final Permission COMMAND_CONVERSATIONS_OTHERS = COMMAND.permission("conversations.others");
    public static final Permission COMMAND_REPLY                = COMMAND.permission("reply");
    public static final Permission COMMAND_TELL                 = COMMAND.permission("tell");

    public static final Permission BYPASS_CONVERSATIONS_DISABLED = BYPASS.permission("conversations.disabled");
    public static final Permission BYPASS_MENTION_COOLDOWN       = BYPASS.permission("mention.cooldown");
    public static final Permission BYPASS_MENTION_AMOUNT         = BYPASS.permission("mention.amount");
    public static final Permission BYPASS_CHANNEL_COOLDOWN       = BYPASS.permission("channel.cooldown");
    public static final Permission BYPASS_ANTI_CAPS              = BYPASS.permission("anticaps");
    public static final Permission BYPASS_ANTI_FLOOD             = BYPASS.permission("antiflood");
    public static final Permission BYPASS_PROFANITY_FILTER       = BYPASS.permission("profanity.filter");
    public static final Permission BYPASS_SPY                    = BYPASS.permission("spy.logger");
}
