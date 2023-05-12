package su.nightexpress.sunlight.module.kits.util;

import su.nexmedia.engine.api.server.JPermission;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.module.ModuleId;
import su.nightexpress.sunlight.module.kits.command.kits.KitsCommand;

public class KitsPerms {

    private static final String PREFIX          = Perms.PREFIX + ModuleId.KITS + ".";
    private static final String PREFIX_COMMAND  = PREFIX + "command.";
    private static final String PREFIX_BYPASS   = PREFIX + "bypass.";
    public static final String PREFIX_KIT       = PREFIX + "kit.";

    public static final JPermission MODULE  = new JPermission(PREFIX + Placeholders.WILDCARD, "Access to all the Kits module feautes.");
    public static final JPermission COMMAND = new JPermission(PREFIX_COMMAND + Placeholders.WILDCARD, "Access to all the Kits module commands.");
    public static final JPermission BYPASS  = new JPermission(PREFIX_BYPASS + Placeholders.WILDCARD, "Bypasses all the Kits module restrictions.");
    public static final JPermission KIT     = new JPermission(PREFIX_KIT + Placeholders.WILDCARD, "Access to all kits from the Kits module.");

    public static final JPermission COMMAND_KITS                = new JPermission(PREFIX_COMMAND + KitsCommand.NAME, "Access to the '/" + KitsCommand.NAME + "' command (without sub-commands).");
    public static final JPermission COMMAND_KITS_EDITOR         = new JPermission(PREFIX_COMMAND + KitsCommand.NAME + ".editor", "Access to the '/" + KitsCommand.NAME + " editor' command.");
    public static final JPermission COMMAND_KITS_PREVIEW        = new JPermission(PREFIX_COMMAND + KitsCommand.NAME + ".preview", "Access to the '/" + KitsCommand.NAME + " preview' command.");
    public static final JPermission COMMAND_KITS_PREVIEW_OTHERS = new JPermission(PREFIX_COMMAND + KitsCommand.NAME + ".preview.others", "Access to the '/" + KitsCommand.NAME + " preview' command on other players.");
    public static final JPermission COMMAND_KITS_GET            = new JPermission(PREFIX_COMMAND + KitsCommand.NAME + ".get", "Access to the '/" + KitsCommand.NAME + " get' command.");
    public static final JPermission COMMAND_KITS_GIVE           = new JPermission(PREFIX_COMMAND + KitsCommand.NAME + ".give", "Access to the '/" + KitsCommand.NAME + " give' command.");
    public static final JPermission COMMAND_KITS_LIST           = new JPermission(PREFIX_COMMAND + KitsCommand.NAME + ".list", "Access to the '/" + KitsCommand.NAME + " list' command.");
    public static final JPermission COMMAND_KITS_LIST_OTHERS    = new JPermission(PREFIX_COMMAND + KitsCommand.NAME + ".list.others", "Access to the '/" + KitsCommand.NAME + " list' command on other players.");

    public static final JPermission BYPASS_COST_MONEY = new JPermission(PREFIX_BYPASS + "cost.money", "Bypasses kit money costs.");
    public static final JPermission BYPASS_COOLDOWN   = new JPermission(PREFIX_BYPASS + "cooldown", "Bypasses kit cooldowns.");

    static {
        Perms.PLUGIN.addChildren(MODULE);

        MODULE.addChildren(COMMAND, BYPASS, KIT);

        COMMAND.addChildren(COMMAND_KITS, COMMAND_KITS_EDITOR,
            COMMAND_KITS_PREVIEW, COMMAND_KITS_PREVIEW_OTHERS,
            COMMAND_KITS_GET, COMMAND_KITS_GIVE,
            COMMAND_KITS_LIST, COMMAND_KITS_LIST_OTHERS);

        BYPASS.addChildren(BYPASS_COOLDOWN, BYPASS_COST_MONEY);
    }
}
