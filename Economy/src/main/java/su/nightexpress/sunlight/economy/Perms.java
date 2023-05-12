package su.nightexpress.sunlight.economy;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import su.nexmedia.engine.utils.Placeholders;

public class Perms {

    private static final String PREFIX = "sunlight.economy.";

    private static final Permission GLOBAL = new Permission(PREFIX + Placeholders.WILDCARD, PermissionDefault.OP);
    private static final Permission GLOBAL_COMMAND = new Permission(PREFIX + "command", PermissionDefault.OP);

    public static final Permission COMMAND_RELOAD = new Permission(PREFIX + "command.reload", "Access to the reload command.", PermissionDefault.OP);

    static {
        GLOBAL_COMMAND.addParent(GLOBAL, true);

        COMMAND_RELOAD.addParent(GLOBAL_COMMAND, true);
    }
}
