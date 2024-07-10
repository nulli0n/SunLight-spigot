package su.nightexpress.sunlight.module.vanish.config;

import su.nightexpress.nightcore.util.wrapper.UniPermission;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.config.Perms;

public class VanishPerms {

    public static final String PREFIX         = Perms.PREFIX + "vanish.";
    public static final String PREFIX_COMMAND = PREFIX + "command.";
    public static final String PREFIX_BYPASS  = PREFIX + "bypass.";

    public static final UniPermission MODULE  = new UniPermission(PREFIX + Placeholders.WILDCARD);
    public static final UniPermission COMMAND = new UniPermission(PREFIX_COMMAND + Placeholders.WILDCARD);
    public static final UniPermission BYPASS  = new UniPermission(PREFIX_BYPASS + Placeholders.WILDCARD);

    public static final UniPermission COMMAND_VANISH        = new UniPermission(PREFIX_COMMAND + "vanish");
    public static final UniPermission COMMAND_VANISH_OTHERS = new UniPermission(PREFIX_COMMAND + "vanish.others");

    public static final UniPermission BYPASS_SEE = new UniPermission(PREFIX_BYPASS + "see");

    static {
        Perms.PLUGIN.addChildren(MODULE);

        MODULE.addChildren(COMMAND, BYPASS);

        COMMAND.addChildren(
            COMMAND_VANISH, COMMAND_VANISH_OTHERS
        );

        BYPASS.addChildren(
            BYPASS_SEE
        );
    }
}
