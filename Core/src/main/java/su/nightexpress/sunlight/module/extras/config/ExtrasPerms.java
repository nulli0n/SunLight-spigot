package su.nightexpress.sunlight.module.extras.config;

import su.nightexpress.nightcore.util.wrapper.UniPermission;
import su.nightexpress.sunlight.config.Perms;
import su.nightexpress.sunlight.Placeholders;

public class ExtrasPerms {

    private static final String PREFIX = Perms.PREFIX + "extras.";
    private static final String PREFIX_COMMAND = PREFIX + "command.";

    public static final UniPermission MODULE  = new UniPermission(PREFIX + Placeholders.WILDCARD, "Access to all Extras module functions.");
    public static final UniPermission COMMAND = new UniPermission(PREFIX_COMMAND + Placeholders.WILDCARD, "Access to all Extras module commands.");

    public static final UniPermission COMMAND_CHAIRS            = new UniPermission(PREFIX_COMMAND + "chairs");
    public static final UniPermission COMMAND_CHAIRS_OTHERS     = new UniPermission(PREFIX_COMMAND + "chairs.others");
    public static final UniPermission COMMAND_SIT               = new UniPermission(PREFIX_COMMAND + "sit");
    public static final UniPermission COMMAND_SIT_OTHERS        = new UniPermission(PREFIX_COMMAND + "sit.others");
    public static final UniPermission COMMAND_CHEST_SORT        = new UniPermission(PREFIX_COMMAND + "chestsort");
    public static final UniPermission COMMAND_CHEST_SORT_OTHERS = new UniPermission(PREFIX_COMMAND + "chestsort.others");

    public static final UniPermission SIGNS_COLOR  = new UniPermission(PREFIX + "signs.color");
    public static final UniPermission ANVILS_COLOR = new UniPermission(PREFIX + "anvils.color");

    static {
        Perms.PLUGIN.addChildren(MODULE);

        MODULE.addChildren(COMMAND, SIGNS_COLOR, ANVILS_COLOR);

        COMMAND.addChildren(
            COMMAND_CHAIRS, COMMAND_CHAIRS_OTHERS,
            COMMAND_CHEST_SORT, COMMAND_CHEST_SORT_OTHERS,
            COMMAND_SIT, COMMAND_SIT_OTHERS
        );
    }
}
