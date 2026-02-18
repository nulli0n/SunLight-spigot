package su.nightexpress.sunlight.module.rtp.config;

import org.bukkit.permissions.Permission;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.config.Perms;

public class RTPPerms {

    public static final PermissionTree MODULE  = Perms.detached("rtp");
    public static final PermissionTree COMMAND = MODULE.branch("command");
    public static final PermissionTree BYPASS  = MODULE.branch("bypass");

    public static final Permission COMMAND_RTP = COMMAND.permission("rtp");
}
