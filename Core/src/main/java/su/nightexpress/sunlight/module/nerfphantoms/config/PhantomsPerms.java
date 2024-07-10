package su.nightexpress.sunlight.module.nerfphantoms.config;

import su.nightexpress.nightcore.util.wrapper.UniPermission;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.config.Perms;

public class PhantomsPerms {

    public static final String PREFIX         = Perms.PREFIX + "nerfphantoms.";
    public static final String PREFIX_COMMAND = PREFIX + "command.";

    public static final UniPermission MODULE  = new UniPermission(PREFIX + Placeholders.WILDCARD);
    public static final UniPermission COMMAND = new UniPermission(PREFIX_COMMAND + Placeholders.WILDCARD);

    public static final UniPermission COMMAND_NO_PHANTOM        = new UniPermission(PREFIX_COMMAND + "nophantom");
    public static final UniPermission COMMAND_NO_PHANTOM_OTHERS = new UniPermission(PREFIX_COMMAND + "nophantom.others");

    static {
        Perms.PLUGIN.addChildren(MODULE);

        MODULE.addChildren(COMMAND);

        COMMAND.addChildren(
            COMMAND_NO_PHANTOM,
            COMMAND_NO_PHANTOM_OTHERS
        );
    }
}
