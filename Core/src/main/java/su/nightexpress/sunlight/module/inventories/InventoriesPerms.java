package su.nightexpress.sunlight.module.inventories;

import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.config.Perms;

public class InventoriesPerms {

    public static final PermissionTree MODULE = Perms.detached("inventories");
    public static final PermissionTree COMMAND = MODULE.branch("command");
}
