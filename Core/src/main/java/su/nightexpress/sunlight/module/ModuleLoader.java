package su.nightexpress.sunlight.module;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.sunlight.SLConfigTypes;
import su.nightexpress.sunlight.SLFiles;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.exception.ModuleLoadException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModuleLoader {

    private final SunLightPlugin plugin;
    private final ModuleRegistry moduleRegistry;

    private final Map<String, ModuleRegistration> registrationMap;
    private final Map<String, ModuleDefinition>   definitionMap;

    public ModuleLoader(@NotNull SunLightPlugin plugin, @NotNull ModuleRegistry moduleRegistry) {
        this.plugin = plugin;
        this.moduleRegistry = moduleRegistry;

        this.registrationMap = new HashMap<>();
        this.definitionMap = new LinkedHashMap<>();
    }

    public <T extends Module> void register(@NotNull String id, @NotNull ModuleDefinition definition, @NotNull ModuleFactory<T> factory) {
        this.register(id, definition, factory, LoadCondition::success);
    }

    public <T extends Module> void register(@NotNull String id,
                                            @NotNull ModuleDefinition definition,
                                            @NotNull ModuleFactory<T> factory,
                                            @NotNull Supplier<LoadCondition> condition) {
        this.definitionMap.put(id, definition);
        this.registrationMap.put(id, new ModuleRegistration(factory, condition));
    }

    public void loadAll() {
        FileConfig config = FileConfig.load(this.plugin.getDataFolder().getAbsolutePath(), SLFiles.FILE_MODULES);
        FileConfig pluginConfig = this.plugin.getConfig();

        this.definitionMap.forEach((id, defaultDefinition) -> {
            // ========== MIGRATION FROM THE CONFIG.YML - START ==========
            if (pluginConfig.contains("Modules." + id)) {
                boolean oldValue = pluginConfig.getBoolean("Modules." + id);

                config.set("Modules." + id + ".Enabled", oldValue);
                pluginConfig.remove("Modules." + id);
            }
            // ========== MIGRATION FROM THE CONFIG.YML - END ==========

            ModuleDefinition definition = config.get(SLConfigTypes.MODULE_DEFINITION, "Modules." + id, defaultDefinition);

            try {
                this.loadModule(id, definition);
            }
            catch (ModuleLoadException exception) {
                this.plugin.error("Fatal error when trying to load module '%s': %s".formatted(id, exception.getMessage()));
            }
        });

        config.saveChanges();

        pluginConfig.remove("Modules");
        pluginConfig.saveChanges();
    }

    private boolean loadModule(@NotNull String id, @NotNull ModuleDefinition definition) throws ModuleLoadException {
        if (!definition.enabled()) return false;

        ModuleRegistration registration = this.registrationMap.remove(id);
        if (registration == null) {
            throw new ModuleLoadException("No loader present");
        }

        if (this.moduleRegistry.isPresent(id)) {
            throw new ModuleLoadException("Module with ID '%s' is already registered");
        }

        LoadCondition condition = registration.getCondition().get();
        if (!condition.isSuccess()) {
            this.plugin.error("Module '%s' can not be loaded: '%s'".formatted(id, condition.reason().orElse(null)));
            return false;
        }

        Path path = Paths.get(this.plugin.getDataFolder() + SLFiles.DIR_MODULES, id);
        ModuleContext context = this.plugin.createModuleContext(id, path, definition);
        Module module = registration.getFactory().load(context);

        return this.moduleRegistry.register(module);
    }
}
