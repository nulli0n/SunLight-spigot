package su.nightexpress.sunlight.module.socials;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.exception.ModuleLoadException;
import su.nightexpress.sunlight.hook.placeholder.PlaceholderRegistry;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleContext;

public class SocialsModule extends Module {

    public SocialsModule(@NotNull ModuleContext context) {
        super(context);
    }

    @Override
    protected void loadModule(@NotNull FileConfig config) throws ModuleLoadException {

    }

    @Override
    protected void unloadModule() {

    }

    @Override
    protected void registerPermissions(@NotNull PermissionTree root) {

    }

    @Override
    protected void registerCommands() {

    }

    @Override
    public void registerPlaceholders(@NotNull PlaceholderRegistry registry) {

    }
}
