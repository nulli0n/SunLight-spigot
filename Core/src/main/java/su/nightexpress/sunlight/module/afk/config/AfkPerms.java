package su.nightexpress.sunlight.module.afk.config;

import su.nightexpress.nightcore.util.wrapper.UniPermission;
import su.nightexpress.sunlight.config.Perms;
import su.nightexpress.sunlight.Placeholders;

public class AfkPerms {

    private static final String PREFIX         = Perms.PREFIX + "afk.";
    private static final String PREFIX_COMMAND = PREFIX + "command.";

    public static final UniPermission MODULE  = new UniPermission(PREFIX + Placeholders.WILDCARD);
    public static final UniPermission COMMAND = new UniPermission(PREFIX_COMMAND + Placeholders.WILDCARD);

    public static final UniPermission COMMAND_AFK        = new UniPermission(PREFIX_COMMAND + "afk");
    public static final UniPermission COMMAND_AFK_OTHERS = new UniPermission(PREFIX_COMMAND + "afk.others");

    static {
        Perms.PLUGIN.addChildren(MODULE);

        MODULE.addChildren(COMMAND);

        COMMAND.addChildren(COMMAND_AFK, COMMAND_AFK_OTHERS);
    }
}
