package su.nightexpress.sunlight.module.extras.config;

import su.nexmedia.engine.api.server.JPermission;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;

public class ExtrasPerms {

    private static final String PREFIX = Perms.PREFIX + "extras.";
    private static final String PREFIX_COMMAND = PREFIX + "command.";

    public static final JPermission MODULE = new JPermission(PREFIX + Placeholders.WILDCARD, "Access to all Extras module functions.");
    public static final JPermission COMMAND = new JPermission(PREFIX_COMMAND + Placeholders.WILDCARD, "Access to all Extras module commands.");

    public static final JPermission COMMAND_CHAIRS            = new JPermission(PREFIX_COMMAND + "chairs");
    public static final JPermission COMMAND_CHAIRS_OTHERS     = new JPermission(PREFIX_COMMAND + "chairs.others");
    public static final JPermission COMMAND_SIT               = new JPermission(PREFIX_COMMAND + "sit");
    public static final JPermission COMMAND_SIT_OTHERS        = new JPermission(PREFIX_COMMAND + "sit.others");
    public static final JPermission COMMAND_CHEST_SORT        = new JPermission(PREFIX_COMMAND + "chestsort");
    public static final JPermission COMMAND_CHEST_SORT_OTHERS = new JPermission(PREFIX_COMMAND + "chestsort.others");

    public static final JPermission SIGNS_COLOR  = new JPermission(PREFIX + "signs.color");
    public static final JPermission ANVILS_COLOR = new JPermission(PREFIX + "anvils.color");

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
