package su.nightexpress.sunlight.module.warps.util;

import su.nexmedia.engine.api.server.JPermission;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.module.warps.command.basic.WarpsCommand;

public class WarpsPerms {

    private static final String PREFIX         = Perms.PREFIX + "warps.";
    private static final String PREFIX_COMMAND = PREFIX + "command.";
    private static final String PREFIX_BYPASS  = PREFIX + "bypass.";
    private static final String PREFIX_EDITOR  = PREFIX + "editor.";
    public static final String  PREFIX_WARP    = PREFIX + "warp.";

    public static final JPermission MODULE  = new JPermission(PREFIX + Placeholders.WILDCARD, "Access to all Warps Module features.");
    public static final JPermission COMMAND = new JPermission(PREFIX_COMMAND + Placeholders.WILDCARD, "Access to all Warps Module commands.");
    public static final JPermission BYPASS  = new JPermission(PREFIX_BYPASS + Placeholders.WILDCARD, "Bypass all Warps Module restrictions.");
    public static final JPermission EDITOR  = new JPermission(PREFIX_EDITOR + Placeholders.WILDCARD, "Access to all Warps Module Editor functions.");
    public static final JPermission WARP    = new JPermission(PREFIX_WARP + Placeholders.WILDCARD, "Access to all warps.");

    public static final JPermission COMMAND_WARPS                 = new JPermission(PREFIX_COMMAND + WarpsCommand.NAME, "Access to the '/" + WarpsCommand.NAME + "' command (without sub-commands).");
    public static final JPermission COMMAND_WARPS_CREATE          = new JPermission(PREFIX_COMMAND + WarpsCommand.NAME + ".create", "Access to the '/" + WarpsCommand.NAME + " create' command.");
    public static final JPermission COMMAND_WARPS_CREATE_OTHERS   = new JPermission(PREFIX_COMMAND + WarpsCommand.NAME + ".create.others", "Access to the '/" + WarpsCommand.NAME + " create' command on other's warps.");
    public static final JPermission COMMAND_WARPS_DELETE          = new JPermission(PREFIX_COMMAND + WarpsCommand.NAME + ".delete", "Access to the '/" + WarpsCommand.NAME + " delete' command.");
    public static final JPermission COMMAND_WARPS_DELETE_OTHERS   = new JPermission(PREFIX_COMMAND + WarpsCommand.NAME + ".delete.others", "Access to the '/" + WarpsCommand.NAME + " delete' command on other's warps.");
    public static final JPermission COMMAND_WARPS_TELEPORT        = new JPermission(PREFIX_COMMAND + WarpsCommand.NAME + ".teleport", "Access to the '/" + WarpsCommand.NAME + " teleport' command.");
    public static final JPermission COMMAND_WARPS_TELEPORT_OTHERS = new JPermission(PREFIX_COMMAND + WarpsCommand.NAME + ".teleport.others", "Access to the '/" + WarpsCommand.NAME + " teleport' command on other players.");
    public static final JPermission COMMAND_WARPS_LIST            = new JPermission(PREFIX_COMMAND + WarpsCommand.NAME + ".list", "Access to the '/" + WarpsCommand.NAME + " list' command.");
    public static final JPermission COMMAND_WARPS_LIST_OTHERS     = new JPermission(PREFIX_COMMAND + WarpsCommand.NAME + ".list.others", "Access to the '/" + WarpsCommand.NAME + " list' command on other players.");

    public static final JPermission EDITOR_OTHERS           = new JPermission(PREFIX_EDITOR + "others", "Allows to edit other's warps.");
    public static final JPermission EDITOR_VISIT_COST       = new JPermission(PREFIX_EDITOR + "visit.cost", "Allows to set warp's visit cost.");
    public static final JPermission EDITOR_VISIT_COOLDOWN   = new JPermission(PREFIX_EDITOR + "visit.cooldown", "Allows to set warp's visit cooldown.");
    public static final JPermission EDITOR_VISIT_TIMES      = new JPermission(PREFIX_EDITOR + "visit.times", "Allows to set warp's visit times.");
    public static final JPermission EDITOR_COMMAND_SHORTCUT = new JPermission(PREFIX_EDITOR + "command.shortcut", "Allows to set warp's command shortcut.");
    public static final JPermission EDITOR_TYPE             = new JPermission(PREFIX_EDITOR + "type", "Allows to change warp's type.");
    public static final JPermission EDITOR_PERMISSION       = new JPermission(PREFIX_EDITOR + "permission", "Allows to change warp permission requirement.");

    public static final JPermission BYPASS_DESCRIPTION_SIZE = new JPermission(PREFIX_BYPASS + "description.size", "Bypasses the description limit.");
    public static final JPermission BYPASS_CREATION_SAFE    = new JPermission(PREFIX_BYPASS + "creation.safe", "Bypasses 'safe' location check for warp creation.");
    public static final JPermission BYPASS_CREATION_WORLD     = new JPermission(PREFIX_BYPASS + "creation.world", "Bypasses 'world' check for warp creation.");
    public static final JPermission BYPASS_VISIT_COST        = new JPermission(PREFIX_BYPASS + "visit.cost", "Bypasses warp visit costs.");
    public static final JPermission BYPASS_VISIT_TIME        = new JPermission(PREFIX_BYPASS + "visit.time", "Bypasses warp visit times.");
    public static final JPermission BYPASS_VISIT_COOLDOWN    = new JPermission(PREFIX_BYPASS + "visit.cooldown", "Bypasses warp visit cooldowns.");

    static {
        Perms.PLUGIN.addChildren(MODULE);

        MODULE.addChildren(COMMAND, BYPASS, EDITOR, WARP);

        COMMAND.addChildren(COMMAND_WARPS,
            COMMAND_WARPS_CREATE, COMMAND_WARPS_CREATE_OTHERS,
            COMMAND_WARPS_DELETE, COMMAND_WARPS_DELETE_OTHERS,
            COMMAND_WARPS_LIST, COMMAND_WARPS_TELEPORT, COMMAND_WARPS_TELEPORT_OTHERS
        );

        EDITOR.addChildren(EDITOR_COMMAND_SHORTCUT,
            EDITOR_OTHERS, EDITOR_TYPE, EDITOR_PERMISSION,
            EDITOR_VISIT_COOLDOWN, EDITOR_VISIT_COST, EDITOR_VISIT_TIMES
        );

        BYPASS.addChildren(BYPASS_DESCRIPTION_SIZE,
            BYPASS_CREATION_SAFE, BYPASS_CREATION_WORLD,
            BYPASS_VISIT_COOLDOWN, BYPASS_VISIT_COST, BYPASS_VISIT_TIME
        );
    }
}
