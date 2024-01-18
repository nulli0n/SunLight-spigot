package su.nightexpress.sunlight.module.afk.config;

import su.nexmedia.engine.api.server.JPermission;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;

public class AfkPerms {

    private static final String PREFIX = Perms.PREFIX + "afk.";
    private static final String PREFIX_COMMAND = PREFIX + "command.";

    public static final JPermission MODULE = new JPermission(PREFIX + Placeholders.WILDCARD, "Access to all AFK module functions.");
    public static final JPermission COMMAND = new JPermission(PREFIX_COMMAND + Placeholders.WILDCARD, "Access to all AFK module commands.");

    public static final JPermission COMMAND_AFK = new JPermission(PREFIX_COMMAND + "afk");
    public static final JPermission COMMAND_AFK_OTHERS = new JPermission(PREFIX_COMMAND + "afk.others");

    static {
        Perms.PLUGIN.addChildren(MODULE);

        MODULE.addChildren(COMMAND);

        COMMAND.addChildren(COMMAND_AFK, COMMAND_AFK_OTHERS);
    }
}
