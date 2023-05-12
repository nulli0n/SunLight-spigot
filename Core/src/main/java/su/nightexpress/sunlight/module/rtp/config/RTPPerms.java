package su.nightexpress.sunlight.module.rtp.config;

import su.nexmedia.engine.api.server.JPermission;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;

public class RTPPerms {

    private static final String PREFIX = Perms.PREFIX + "rtp.";
    private static final String PREFIX_COMMAND = PREFIX + "command.";

    public static final JPermission MODULE = new JPermission(PREFIX + Placeholders.WILDCARD);
    public static final JPermission COMMAND = new JPermission(PREFIX_COMMAND + Placeholders.WILDCARD);

    public static final JPermission COMMAND_RTP = new JPermission(PREFIX_COMMAND + "rtp");

    static {
        Perms.PLUGIN.addChildren(MODULE);

        MODULE.addChildren(COMMAND);

        COMMAND.addChildren(COMMAND_RTP);
    }
}
