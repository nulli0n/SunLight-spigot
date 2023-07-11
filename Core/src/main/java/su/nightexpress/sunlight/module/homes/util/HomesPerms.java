package su.nightexpress.sunlight.module.homes.util;

import su.nexmedia.engine.api.server.JPermission;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.module.ModuleId;
import su.nightexpress.sunlight.module.homes.command.admin.HomesAdminCommand;
import su.nightexpress.sunlight.module.homes.command.basic.*;

public class HomesPerms {

    private static final String PREFIX         = Perms.PREFIX + ModuleId.HOMES + ".";
    private static final String PREFIX_COMMAND = PREFIX + "command.";
    private static final String PREFIX_BYPASS  = PREFIX + "bypass.";
    public static final String PREFIX_AMOUNT = PREFIX + "amount.";

    public static final JPermission MODULE  = new JPermission(PREFIX + Placeholders.WILDCARD, "Access to all Homes Module features.");
    public static final JPermission COMMAND = new JPermission(PREFIX_COMMAND + Placeholders.WILDCARD, "Access to all Homes Module commands.");
    public static final JPermission BYPASS  = new JPermission(PREFIX_BYPASS + Placeholders.WILDCARD, "Bypasses all Homes Module restrictions.");

    public static final JPermission COMMAND_HOMES             = new JPermission(PREFIX_COMMAND + HomesCommand.NAME, "Access to the '/homes' command (without sub-commands).");
    public static final JPermission COMMAND_HOMES_DELETE      = new JPermission(PREFIX_COMMAND + HomesCommand.NAME + "." + HomesDeleteCommand.NAME, "Access to the '/homes delete' command.");
    public static final JPermission COMMAND_HOMES_SET         = new JPermission(PREFIX_COMMAND + HomesCommand.NAME + "." + HomesSetCommand.NAME, "Access to the '/homes set' command.");
    public static final JPermission COMMAND_HOMES_TELEPORT    = new JPermission(PREFIX_COMMAND + HomesCommand.NAME + "." + HomesTeleportCommand.NAME, "Access to the '/homes teleport' command.");
    public static final JPermission COMMAND_HOMES_LIST        = new JPermission(PREFIX_COMMAND + HomesCommand.NAME + "." + HomesListCommand.NAME, "Access to the '/homes list' command.");
    public static final JPermission COMMAND_HOMES_LIST_OTHERS = new JPermission(PREFIX_COMMAND + HomesCommand.NAME + "." + HomesListCommand.NAME + ".others", "Access to the '/homes list' command of other players.");
    public static final JPermission COMMAND_HOMES_VISIT       = new JPermission(PREFIX_COMMAND + HomesCommand.NAME + "." + HomesVisitCommand.NAME, "Access to the '/homes visit' command.");
    public static final JPermission COMMAND_HOMES_VISIT_ALL   = new JPermission(PREFIX_COMMAND + HomesCommand.NAME + "." + HomesVisitCommand.NAME + ".all", "Access to the '/homes visit' command for all homes.");
    public static final JPermission COMMAND_HOMES_ADMIN       = new JPermission(PREFIX_COMMAND + HomesAdminCommand.NAME, "Access to the '/homesadmin' command (and all sub-commands).");

    public static final JPermission BYPASS_CREATION_WORLDS     = new JPermission(PREFIX_BYPASS + "creation.worlds", "Allows to create homes in restricted worlds.");
    public static final JPermission BYPASS_CREATION_REGIONS    = new JPermission(PREFIX_BYPASS + "creation.regions", "Allows to create homes in restricted regions.");
    public static final JPermission BYPASS_CREATION_PROTECTION = new JPermission(PREFIX_BYPASS + "creation.protection", "Allows to create homes in protected areas.");

    static {
        Perms.PLUGIN.addChildren(MODULE);

        MODULE.addChildren(COMMAND, BYPASS);

        COMMAND.addChildren(
            COMMAND_HOMES, COMMAND_HOMES_DELETE, COMMAND_HOMES_SET, COMMAND_HOMES_TELEPORT,
            COMMAND_HOMES_LIST, COMMAND_HOMES_LIST_OTHERS, COMMAND_HOMES_VISIT, COMMAND_HOMES_VISIT_ALL,
            COMMAND_HOMES_ADMIN);

        BYPASS.addChildren(BYPASS_CREATION_PROTECTION, BYPASS_CREATION_REGIONS, BYPASS_CREATION_WORLDS);
    }
}
