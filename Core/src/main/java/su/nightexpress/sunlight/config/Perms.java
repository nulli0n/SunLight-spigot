package su.nightexpress.sunlight.config;

import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

public class Perms {

    public static final PermissionTree ROOT         = PermissionTree.root("sunlight");
    public static final PermissionTree ROOT_COMMAND = ROOT.branch("command");
    public static final PermissionTree ROOT_BYPASS  = ROOT.branch("bypass");

    public static final Permission COMMAND_RELOAD = ROOT_COMMAND.permission("reload");

    public static final Permission BYPASS_COMMAND_COOLDOWN = ROOT_BYPASS.permission("command.cooldown");

    @NotNull
    public static PermissionTree detached(@NotNull String name) {
        return ROOT.detached(name);
    }
}
