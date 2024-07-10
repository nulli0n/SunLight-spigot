package su.nightexpress.sunlight.config;

import su.nightexpress.nightcore.util.wrapper.UniPermission;
import su.nightexpress.sunlight.Placeholders;

public class Perms {

    public static final String PREFIX         = "sunlight.";
    public static final String PREFIX_COMMAND = PREFIX + "command.";
    public static final String PREFIX_BYPASS  = PREFIX + "bypass.";

    public static final UniPermission PLUGIN  = new UniPermission(PREFIX + Placeholders.WILDCARD);
    public static final UniPermission COMMAND = new UniPermission(PREFIX_COMMAND + Placeholders.WILDCARD);
    public static final UniPermission BYPASS  = new UniPermission(PREFIX_BYPASS + Placeholders.WILDCARD);

    public static final UniPermission BYPASS_COMMAND_COOLDOWN           = new UniPermission(PREFIX_BYPASS + "command.cooldown");
    public static final UniPermission BYPASS_TELEPORT_REQUESTS_DISABLED = new UniPermission(PREFIX_BYPASS + "teleport.requests.disabled");
    public static final UniPermission BYPASS_IGNORE_PM                  = new UniPermission(PREFIX_BYPASS + "ignore.pm");
    public static final UniPermission BYPASS_IGNORE_TELEPORTS           = new UniPermission(PREFIX_BYPASS + "ignore.teleports");


    static {
        PLUGIN.addChildren(COMMAND, BYPASS);

        BYPASS.addChildren(
            BYPASS_COMMAND_COOLDOWN,
            BYPASS_TELEPORT_REQUESTS_DISABLED,
            BYPASS_IGNORE_PM, BYPASS_IGNORE_TELEPORTS
        );
    }
}
