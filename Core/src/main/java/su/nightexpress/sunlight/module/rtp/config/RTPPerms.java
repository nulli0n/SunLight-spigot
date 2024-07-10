package su.nightexpress.sunlight.module.rtp.config;

import su.nightexpress.nightcore.util.wrapper.UniPermission;
import su.nightexpress.sunlight.config.Perms;
import su.nightexpress.sunlight.Placeholders;

public class RTPPerms {

    private static final String PREFIX = Perms.PREFIX + "rtp.";
    private static final String PREFIX_COMMAND = PREFIX + "command.";

    public static final UniPermission MODULE  = new UniPermission(PREFIX + Placeholders.WILDCARD);
    public static final UniPermission COMMAND = new UniPermission(PREFIX_COMMAND + Placeholders.WILDCARD);

    public static final UniPermission COMMAND_RTP = new UniPermission(PREFIX_COMMAND + "rtp");

    static {
        Perms.PLUGIN.addChildren(MODULE);

        MODULE.addChildren(COMMAND);

        COMMAND.addChildren(COMMAND_RTP);
    }
}
