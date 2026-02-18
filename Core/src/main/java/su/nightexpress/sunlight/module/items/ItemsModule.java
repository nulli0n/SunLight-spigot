package su.nightexpress.sunlight.module.items;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.hook.placeholder.PlaceholderRegistry;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleContext;
import su.nightexpress.sunlight.module.items.command.ItemCommandProvider;
import su.nightexpress.sunlight.module.items.command.LoreCommandsProvider;

public class ItemsModule extends Module {

    private final ItemsSettings settings;

    public ItemsModule(@NotNull ModuleContext context) {
        super(context);
        this.settings = new ItemsSettings();
    }

    @Override
    protected void loadModule(@NotNull FileConfig config) {
        this.settings.load(config);
        this.plugin.injectLang(ItemsLang.class);
    }

    @Override
    protected void unloadModule() {

    }

    @Override
    protected void registerPermissions(@NotNull PermissionTree root) {
        root.merge(ItemsPerms.MODULE);
    }

    @Override
    protected void registerCommands() {
        this.commandRegistry.addProvider("item", new ItemCommandProvider(this.plugin, this, this.settings, this.userManager));
        this.commandRegistry.addProvider("lore", new LoreCommandsProvider(this.plugin, this));
    }

    @Override
    public void registerPlaceholders(@NotNull PlaceholderRegistry registry) {

    }
}
