package su.nightexpress.sunlight.module.chat.config;

import su.nightexpress.nightcore.util.wrapper.UniPermission;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.config.Perms;
import su.nightexpress.sunlight.module.chat.module.spy.SpyType;

import java.util.function.Function;

public class ChatPerms {

    public static final String PREFIX         = Perms.PREFIX + "chat.";
    public static final String PREFIX_COMMAND = PREFIX + "command.";
    public static final String PREFIX_BYPASS  = PREFIX + "bypass.";
    public static final String PREFIX_MENTION = PREFIX + "mention.";

    public static final String MENTION_PLAYER  = PREFIX_MENTION + "player.";
    public static final String MENTION_SPECIAL = PREFIX_MENTION + "special.";
    public static final String CHANNEL_HEAR    = PREFIX + "channel.hear.";
    public static final String CHANNEL_SPEAK   = PREFIX + "channel.speak.";

    public static final UniPermission MODULE  = new UniPermission(PREFIX + Placeholders.WILDCARD);
    public static final UniPermission COMMAND = new UniPermission(PREFIX_COMMAND + Placeholders.WILDCARD);
    public static final UniPermission BYPASS  = new UniPermission(PREFIX_BYPASS + Placeholders.WILDCARD);
    public static final UniPermission MENTION = new UniPermission(PREFIX_MENTION + Placeholders.WILDCARD);

    public static final UniPermission COLOR = new UniPermission(PREFIX + "color");
//    public static final UniPermission FONT  = new UniPermission(PREFIX + "font");
//    public static final UniPermission STYLE = new UniPermission(PREFIX + "style");


    public static final UniPermission COMMAND_CHANNEL_JOIN  = new UniPermission(PREFIX_COMMAND + "channel.join");
    public static final UniPermission COMMAND_CHANNEL_LEAVE = new UniPermission(PREFIX_COMMAND + "channel.leave");
    public static final UniPermission COMMAND_CHANNEL_SET   = new UniPermission(PREFIX_COMMAND + "channel.set");

    public static final UniPermission COMMAND_CLEARCHAT       = new UniPermission(PREFIX_COMMAND + "clearchat");
    public static final UniPermission COMMAND_ME              = new UniPermission(PREFIX_COMMAND + "me");
    public static final UniPermission COMMAND_MENTIONS        = new UniPermission(PREFIX_COMMAND + "mentions");
    public static final UniPermission COMMAND_MENTIONS_OTHERS = new UniPermission(PREFIX_COMMAND + "mentions.others");
    public static final UniPermission COMMAND_REPLY           = new UniPermission(PREFIX_COMMAND + "reply");
    public static final UniPermission COMMAND_TELL            = new UniPermission(PREFIX_COMMAND + "tell");

    public static final UniPermission COMMAND_SPY_LOGGER       = new UniPermission(PREFIX_COMMAND + "chatspy.logger");

    public static final Function<SpyType, UniPermission> COMMAND_SPY_MODE = spyType ->
        new UniPermission(PREFIX_COMMAND + "spymode." + spyType.name().toLowerCase());

    public static final Function<SpyType, UniPermission> COMMAND_SPY_MODE_OTHERS = spyType ->
        new UniPermission(PREFIX_COMMAND + "spymode." + spyType.name().toLowerCase() + ".others");

    public static final UniPermission COMMAND_TOGGLE_PM        = new UniPermission(PREFIX_COMMAND + "togglepm");
    public static final UniPermission COMMAND_TOGGLE_PM_OTHERS = new UniPermission(PREFIX_COMMAND + "togglepm.others");

    public static final UniPermission BYPASS_PM_DISABLED            = new UniPermission(PREFIX_BYPASS + "pm.disabled");
    public static final UniPermission BYPASS_MENTION_COOLDOWN       = new UniPermission(PREFIX_BYPASS + "mention.cooldown");
    public static final UniPermission BYPASS_MENTION_AMOUNT         = new UniPermission(PREFIX_BYPASS + "mention.amount");
    public static final UniPermission BYPASS_CHANNEL_DISTANCE_HEAR  = new UniPermission(PREFIX_BYPASS + "channel.distance.hear");
    public static final UniPermission BYPASS_CHANNEL_DISTANCE_SPEAK = new UniPermission(PREFIX_BYPASS + "channel.distance.speak");
    public static final UniPermission BYPASS_COOLDOWN_MESSAGE       = new UniPermission(PREFIX_BYPASS + "cooldown.message");
    public static final UniPermission BYPASS_COOLDOWN_COMMAND       = new UniPermission(PREFIX_BYPASS + "cooldown.command");
    public static final UniPermission BYPASS_ANTICAPS               = new UniPermission(PREFIX_BYPASS + "anticaps");
    public static final UniPermission BYPASS_ANTISPAM               = new UniPermission(PREFIX_BYPASS + "antispam");
    public static final UniPermission BYPASS_RULES                  = new UniPermission(PREFIX_BYPASS + "rules");

    static {
        Perms.PLUGIN.addChildren(MODULE);

        MODULE.addChildren(
            COMMAND,
            BYPASS,
            MENTION,
            COLOR
        );

        COMMAND.addChildren(
            COMMAND_CHANNEL_JOIN,
            COMMAND_CHANNEL_LEAVE,
            COMMAND_CHANNEL_SET,
            COMMAND_ME,
            COMMAND_MENTIONS, COMMAND_MENTIONS_OTHERS,
            COMMAND_REPLY,
            COMMAND_TELL,
            COMMAND_SPY_LOGGER,
            COMMAND_CLEARCHAT,
            COMMAND_TOGGLE_PM, COMMAND_TOGGLE_PM_OTHERS
        );

        BYPASS.addChildren(
            BYPASS_PM_DISABLED,
            BYPASS_MENTION_COOLDOWN, BYPASS_MENTION_AMOUNT,
            BYPASS_CHANNEL_DISTANCE_HEAR,
            BYPASS_CHANNEL_DISTANCE_SPEAK,
            BYPASS_COOLDOWN_COMMAND,
            BYPASS_COOLDOWN_MESSAGE,
            BYPASS_ANTICAPS,
            BYPASS_ANTISPAM,
            BYPASS_RULES
        );

        for (SpyType spyType : SpyType.values()) {
            COMMAND.addChildren(COMMAND_SPY_MODE.apply(spyType));
        }
    }
}
