package su.nightexpress.sunlight.module.warmups.config;

import su.nightexpress.nightcore.util.wrapper.UniPermission;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.config.Perms;

public class WarmupsPerms {

    public static final String PREFIX         = Perms.PREFIX + "warmups.";
    public static final String PREFIX_COMMAND = PREFIX + "command.";
    public static final String PREFIX_BYPASS  = PREFIX + "bypass.";

    public static final UniPermission MODULE = new UniPermission(PREFIX + Placeholders.WILDCARD);
    public static final UniPermission BYPASS = new UniPermission(PREFIX_BYPASS + Placeholders.WILDCARD);

    public static final UniPermission BYPASS_TELEPORT = new UniPermission(PREFIX_BYPASS + "teleport");
    public static final UniPermission BYPASS_COMMAND  = new UniPermission(PREFIX_BYPASS + "command");

    static {
        Perms.PLUGIN.addChildren(MODULE);

        MODULE.addChildren(BYPASS);

        BYPASS.addChildren(
            BYPASS_TELEPORT,
            BYPASS_COMMAND
        );
    }
}
