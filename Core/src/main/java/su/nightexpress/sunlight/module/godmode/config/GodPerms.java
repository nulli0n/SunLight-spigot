package su.nightexpress.sunlight.module.godmode.config;

import su.nightexpress.nightcore.util.wrapper.UniPermission;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.config.Perms;

public class GodPerms {

    public static final String PREFIX         = Perms.PREFIX + "godmode.";
    public static final String PREFIX_COMMAND = PREFIX + "command.";
    public static final String PREFIX_BYPASS  = PREFIX + "bypass.";

    public static final UniPermission MODULE  = new UniPermission(PREFIX + Placeholders.WILDCARD);
    public static final UniPermission COMMAND = new UniPermission(PREFIX_COMMAND + Placeholders.WILDCARD);
    public static final UniPermission BYPASS  = new UniPermission(PREFIX_BYPASS + Placeholders.WILDCARD);

    public static final UniPermission COMMAND_GOD             = new UniPermission(PREFIX_COMMAND + "god");
    public static final UniPermission COMMAND_GOD_OTHERS      = new UniPermission(PREFIX_COMMAND + "god.others");
    public static final UniPermission COMMAND_FOOD_GOD        = new UniPermission(PREFIX_COMMAND + "foodgod");
    public static final UniPermission COMMAND_FOOD_GOD_OTHERS = new UniPermission(PREFIX_COMMAND + "foodgod.others");

    public static final UniPermission BYPASS_WORLDS = new UniPermission(PREFIX_BYPASS + "worlds");

    static {
        Perms.PLUGIN.addChildren(MODULE);

        MODULE.addChildren(COMMAND, BYPASS);

        COMMAND.addChildren(
            COMMAND_GOD, COMMAND_GOD_OTHERS,
            COMMAND_FOOD_GOD, COMMAND_FOOD_GOD_OTHERS
        );

        BYPASS.addChildren(
            BYPASS_WORLDS
        );
    }
}
