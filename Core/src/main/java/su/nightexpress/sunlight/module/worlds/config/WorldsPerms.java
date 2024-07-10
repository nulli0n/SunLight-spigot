package su.nightexpress.sunlight.module.worlds.config;

import su.nightexpress.nightcore.util.wrapper.UniPermission;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.config.Perms;

public class WorldsPerms {

    public static final String PREFIX         = Perms.PREFIX + "worlds.";
    public static final String PREFIX_COMMAND = PREFIX + "command.";
    public static final String PREFIX_BYPASS  = PREFIX + "bypass.";

    public static final UniPermission MODULE  = new UniPermission(PREFIX + Placeholders.WILDCARD);
    public static final UniPermission COMMAND = new UniPermission(PREFIX_COMMAND + Placeholders.WILDCARD);
    public static final UniPermission BYPASS  = new UniPermission(PREFIX_BYPASS + Placeholders.WILDCARD);

    public static final UniPermission COMMAND_WORLDS_CREATE = new UniPermission(PREFIX_COMMAND + "createworld");
    public static final UniPermission COMMAND_WORLDS_DELETE = new UniPermission(PREFIX_COMMAND + "deleteworld");
    public static final UniPermission COMMAND_WORLDS_LOAD   = new UniPermission(PREFIX_COMMAND + "loadworld");
    public static final UniPermission COMMAND_WORLDS_UNLOAD = new UniPermission(PREFIX_COMMAND + "unloadworld");
    public static final UniPermission COMMAND_WORLDS_EDITOR = new UniPermission(PREFIX_COMMAND + "editor");

    public static final UniPermission BYPASS_COMMANDS = new UniPermission(PREFIX_BYPASS + "commands");
    public static final UniPermission BYPASS_FLY      = new UniPermission(PREFIX_BYPASS + "fly");

    static {
        Perms.PLUGIN.addChildren(MODULE);

        MODULE.addChildren(
            COMMAND,
            BYPASS
        );

        COMMAND.addChildren(
            COMMAND_WORLDS_CREATE,
            COMMAND_WORLDS_DELETE,
            COMMAND_WORLDS_EDITOR,
            COMMAND_WORLDS_LOAD,
            COMMAND_WORLDS_UNLOAD
        );

        BYPASS.addChildren(
            BYPASS_COMMANDS,
            BYPASS_FLY
        );
    }
}
