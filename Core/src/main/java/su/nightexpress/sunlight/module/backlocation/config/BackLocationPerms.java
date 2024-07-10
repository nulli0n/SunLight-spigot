package su.nightexpress.sunlight.module.backlocation.config;

import su.nightexpress.nightcore.util.wrapper.UniPermission;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.config.Perms;

public class BackLocationPerms {

    public static final String PREFIX         = Perms.PREFIX + "backlocation.";
    public static final String PREFIX_COMMAND = PREFIX + "command.";
    public static final String PREFIX_BYPASS  = PREFIX + "bypass.";

    public static final UniPermission MODULE  = new UniPermission(PREFIX + Placeholders.WILDCARD);
    public static final UniPermission COMMAND = new UniPermission(PREFIX_COMMAND + Placeholders.WILDCARD);
    public static final UniPermission BYPASS  = new UniPermission(PREFIX_BYPASS + Placeholders.WILDCARD);

    public static final UniPermission COMMAND_BACK             = new UniPermission(PREFIX_COMMAND + "back");
    public static final UniPermission COMMAND_BACK_OTHERS      = new UniPermission(PREFIX_COMMAND + "back.others");
    public static final UniPermission COMMAND_DEATHBACK        = new UniPermission(PREFIX_COMMAND + "deathback");
    public static final UniPermission COMMAND_DEATHBACK_OTHERS = new UniPermission(PREFIX_COMMAND + "deathback.others");

    public static final UniPermission BYPASS_PREVIOUS_WORLDS = new UniPermission(PREFIX_BYPASS + "previous.worlds");
    public static final UniPermission BYPASS_PREVIOUS_CAUSES = new UniPermission(PREFIX_BYPASS + "previous.causes");
    public static final UniPermission BYPASS_DEATH_WORLDS    = new UniPermission(PREFIX_BYPASS + "death.worlds");

    static {
        Perms.PLUGIN.addChildren(MODULE);

        MODULE.addChildren(COMMAND, BYPASS);

        COMMAND.addChildren(
            COMMAND_BACK, COMMAND_BACK_OTHERS,
            COMMAND_DEATHBACK, COMMAND_DEATHBACK_OTHERS
        );

        BYPASS.addChildren(
            BYPASS_PREVIOUS_CAUSES, BYPASS_PREVIOUS_WORLDS,
            BYPASS_DEATH_WORLDS
        );
    }
}
