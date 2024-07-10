package su.nightexpress.sunlight.module.spawns.config;

import su.nightexpress.nightcore.util.wrapper.UniPermission;
import su.nightexpress.sunlight.config.Perms;
import su.nightexpress.sunlight.module.ModuleId;
import su.nightexpress.sunlight.module.spawns.util.Placeholders;

public class SpawnsPerms {

    public static final String PREFIX         = Perms.PREFIX + ModuleId.SPAWNS + ".";
    public static final String PREFIX_SPAWN   = PREFIX + "spawn.";
    public static final String PREFIX_COMMAND = PREFIX + "command.";
    public static final String PREFIX_BYPASS  = PREFIX + "bypass.";

    public static final UniPermission MODULE  = new UniPermission(PREFIX + Placeholders.WILDCARD);
    public static final UniPermission COMMAND = new UniPermission(PREFIX_COMMAND + Placeholders.WILDCARD);
    public static final UniPermission BYPASS  = new UniPermission(PREFIX_BYPASS + Placeholders.WILDCARD);
    public static final UniPermission SPAWN   = new UniPermission(PREFIX_SPAWN + Placeholders.WILDCARD);

    public static final UniPermission COMMAND_SPAWNS_CREATE          = new UniPermission(PREFIX_COMMAND + "spawns.create");
    public static final UniPermission COMMAND_SPAWNS_DELETE          = new UniPermission(PREFIX_COMMAND + "spawns.delete");
    public static final UniPermission COMMAND_SPAWNS_TELEPORT        = new UniPermission(PREFIX_COMMAND + "spawns.teleport");
    public static final UniPermission COMMAND_SPAWNS_TELEPORT_OTHERS = new UniPermission(PREFIX_COMMAND + "spawns.teleport.others");
    public static final UniPermission COMMAND_SPAWNS_EDITOR          = new UniPermission(PREFIX_COMMAND + "spawns.editor");

    static {
        Perms.PLUGIN.addChildren(MODULE);

        MODULE.addChildren(
            COMMAND,
            BYPASS
        );

        COMMAND.addChildren(
            COMMAND_SPAWNS_CREATE,
            COMMAND_SPAWNS_DELETE,
            COMMAND_SPAWNS_EDITOR,
            COMMAND_SPAWNS_TELEPORT, COMMAND_SPAWNS_TELEPORT_OTHERS
        );
    }
}
