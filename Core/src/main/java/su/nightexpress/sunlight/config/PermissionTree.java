package su.nightexpress.sunlight.config;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionTree {

    private final String                      name;
    private final String                      prefix;
    private final Map<String, PermissionTree> branches;
    private final Map<String, Permission>     permissions;

    private PermissionTree(@NotNull String name, @NotNull String prefix) {
        this.name = name;
        this.prefix = prefix;
        this.branches = new HashMap<>();
        this.permissions = new HashMap<>();
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    @NotNull
    public String getPrefix() {
        return this.prefix;
    }

    @NotNull
    public static PermissionTree root(@NotNull String name) {
        return new PermissionTree(name, name);
    }

    @NotNull
    public PermissionTree detached(@NotNull String name) {
        return new PermissionTree(name, this.childrenNode(name));
    }

    @NotNull
    public PermissionTree branch(@NotNull String prefix) {
        PermissionTree tree = this.detached(prefix);
        this.merge(tree);
        return tree;
    }

    public void merge(@NotNull PermissionTree other) {
        this.branches.put(other.name, other);
    }

    @NotNull
    public Permission permission(@NotNull String name) {
        Permission permission = this.children(name);

        this.permissions.put(permission.getName(), permission);
        return permission;
    }

    @NotNull
    public Permission getRoot() {
        return new Permission(this.childrenNode("*"));
    }

    @NotNull
    public Permission children(@NotNull String name) {
        return new Permission(this.childrenNode(name));
    }

    @NotNull
    public String childrenNode(@NotNull String name) {
        return this.prefix + "." + name;
    }

    public boolean hasChildAccess(@NotNull CommandSender sender, @NotNull String name) {
        return sender.hasPermission(this.childrenNode("*")) || sender.hasPermission(this.childrenNode(name));
    }

    @NotNull
    public List<Permission> toList() {
        List<Permission> accumulated = new ArrayList<>();

        accumulated.add(this.getRoot());
        accumulated.addAll(this.permissions.values());

        this.branches.values().forEach(branch -> {
            accumulated.addAll(branch.toList());
        });

        return accumulated;
    }

    @NotNull
    public Permission accumulate() {
        Permission root = this.getRoot();

        this.permissions.values().forEach(permission -> {
            permission.addParent(root, true);
        });
        this.branches.values().forEach(branch -> {
            branch.accumulate().addParent(root, true);
        });

        return root;
    }
}
