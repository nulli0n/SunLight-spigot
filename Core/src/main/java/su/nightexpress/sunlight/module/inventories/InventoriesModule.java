package su.nightexpress.sunlight.module.inventories;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.exception.ModuleLoadException;
import su.nightexpress.sunlight.hook.placeholder.PlaceholderRegistry;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleContext;
import su.nightexpress.sunlight.module.inventories.command.ContainerCommandProvider;
import su.nightexpress.sunlight.module.inventories.command.EnderchestCommandsProvider;
import su.nightexpress.sunlight.module.inventories.command.InventoryCommandProvider;
import su.nightexpress.sunlight.nms.SunNMS;

public class InventoriesModule extends Module {

    private final SunNMS internals;

    public InventoriesModule(@NotNull ModuleContext context, @Nullable SunNMS internals) {
        super(context);
        this.internals = internals;
    }

    @Override
    protected void loadModule(@NotNull FileConfig config) throws ModuleLoadException {

    }

    @Override
    protected void unloadModule() {

    }

    @Override
    protected void registerPermissions(@NotNull PermissionTree root) {
        root.merge(InventoriesPerms.MODULE);
    }

    @Override
    protected void registerCommands() {
        if (this.internals != null) {
            this.commandRegistry.addProvider("container", new ContainerCommandProvider(this.plugin, this, this.internals));
        }

        this.commandRegistry.addProvider("enderchest", new EnderchestCommandsProvider(this.plugin, this, this.userManager, this.internals));
        this.commandRegistry.addProvider("inventory", new InventoryCommandProvider(this.plugin, this, this.userManager, this.internals));
    }

    @Override
    public void registerPlaceholders(@NotNull PlaceholderRegistry registry) {

    }
}
