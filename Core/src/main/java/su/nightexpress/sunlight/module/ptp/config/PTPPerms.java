package su.nightexpress.sunlight.module.ptp.config;

import su.nightexpress.nightcore.util.wrapper.UniPermission;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.config.Perms;

public class PTPPerms {

    public static final String PREFIX = Perms.PREFIX + "ptp.";
    public static final String PREFIX_COMMAND = PREFIX + "command.";

    public static final UniPermission MODULE  = new UniPermission(PREFIX + Placeholders.WILDCARD);
    public static final UniPermission COMMAND = new UniPermission(PREFIX_COMMAND + Placeholders.WILDCARD);

    public static final UniPermission COMMAND_ACCEPT        = new UniPermission(PREFIX_COMMAND + "accept");
    public static final UniPermission COMMAND_DECLINE       = new UniPermission(PREFIX_COMMAND + "decline");
    public static final UniPermission COMMAND_INVITE        = new UniPermission(PREFIX_COMMAND + "invite");
    public static final UniPermission COMMAND_REQUEST       = new UniPermission(PREFIX_COMMAND + "request");
    public static final UniPermission COMMAND_TOGGLE        = new UniPermission(PREFIX_COMMAND + "toggle");
    public static final UniPermission COMMAND_TOGGLE_OTHERS = new UniPermission(PREFIX_COMMAND + "toggle.others");

    static {
        Perms.PLUGIN.addChildren(MODULE);

        MODULE.addChildren(COMMAND);

        COMMAND.addChildren(
            COMMAND_ACCEPT, COMMAND_DECLINE,
            COMMAND_INVITE, COMMAND_REQUEST,
            COMMAND_TOGGLE, COMMAND_TOGGLE_OTHERS
        );
    }
}
