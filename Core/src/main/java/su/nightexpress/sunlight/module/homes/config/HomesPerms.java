package su.nightexpress.sunlight.module.homes.config;

import su.nightexpress.nightcore.util.wrapper.UniPermission;
import su.nightexpress.sunlight.config.Perms;
import su.nightexpress.sunlight.module.ModuleId;
import su.nightexpress.sunlight.module.homes.util.Placeholders;

public class HomesPerms {

    public static final String PREFIX         = Perms.PREFIX + ModuleId.HOMES + ".";
    public static final String PREFIX_COMMAND = PREFIX + "command.";
    public static final String PREFIX_BYPASS  = PREFIX + "bypass.";
    public static final String PREFIX_AMOUNT  = PREFIX + "amount.";

    public static final UniPermission MODULE  = new UniPermission(PREFIX + Placeholders.WILDCARD);
    public static final UniPermission COMMAND = new UniPermission(PREFIX_COMMAND + Placeholders.WILDCARD);
    public static final UniPermission BYPASS  = new UniPermission(PREFIX_BYPASS + Placeholders.WILDCARD);

    public static final UniPermission COMMAND_HOMES_DELETE        = new UniPermission(PREFIX_COMMAND + "homes.delete");
    public static final UniPermission COMMAND_HOMES_DELETE_OTHERS = new UniPermission(PREFIX_COMMAND + "homes.delete.others");
    public static final UniPermission COMMAND_HOMES_SET           = new UniPermission(PREFIX_COMMAND + "homes.set");
    public static final UniPermission COMMAND_HOMES_SET_OTHERS    = new UniPermission(PREFIX_COMMAND + "homes.set.others");
    public static final UniPermission COMMAND_HOMES_TELEPORT      = new UniPermission(PREFIX_COMMAND + "homes.teleport");
    public static final UniPermission COMMAND_HOMES_LIST          = new UniPermission(PREFIX_COMMAND + "homes.list");
    public static final UniPermission COMMAND_HOMES_LIST_OTHERS   = new UniPermission(PREFIX_COMMAND + "homes.list.others");
    public static final UniPermission COMMAND_HOMES_INVITE        = new UniPermission(PREFIX_COMMAND + "homes.invite");
    public static final UniPermission COMMAND_HOMES_VISIT         = new UniPermission(PREFIX_COMMAND + "homes.visit");
    public static final UniPermission COMMAND_HOMES_VISIT_ALL     = new UniPermission(PREFIX_COMMAND + "homes.visit.all");

    public static final UniPermission BYPASS_UNSAFE              = new UniPermission(PREFIX_BYPASS + "unsafe");
    public static final UniPermission BYPASS_CREATION_WORLDS     = new UniPermission(PREFIX_BYPASS + "creation.worlds");
    public static final UniPermission BYPASS_CREATION_PROTECTION = new UniPermission(PREFIX_BYPASS + "creation.protection");

    static {
        Perms.PLUGIN.addChildren(MODULE);

        MODULE.addChildren(
            COMMAND,
            BYPASS
        );

        COMMAND.addChildren(
            COMMAND_HOMES_DELETE, COMMAND_HOMES_DELETE_OTHERS,
            COMMAND_HOMES_SET, COMMAND_HOMES_SET_OTHERS,
            COMMAND_HOMES_TELEPORT,
            COMMAND_HOMES_INVITE,
            COMMAND_HOMES_LIST, COMMAND_HOMES_LIST_OTHERS,
            COMMAND_HOMES_VISIT, COMMAND_HOMES_VISIT_ALL
        );

        BYPASS.addChildren(
            BYPASS_CREATION_PROTECTION,
            BYPASS_UNSAFE,
            BYPASS_CREATION_WORLDS
        );
    }
}
