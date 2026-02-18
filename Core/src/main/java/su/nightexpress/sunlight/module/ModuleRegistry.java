package su.nightexpress.sunlight.module;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.manager.SimpleManager;
import su.nightexpress.nightcore.util.LowerCase;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ModuleRegistry {

    //private final SunLightPlugin plugin;
    private final Map<String, Module>   byId;
    private final Map<Class<?>, Module> byType;

    public ModuleRegistry(/*@NotNull SunLightPlugin plugin*/) {
        //this.plugin = plugin;
        this.byId = new HashMap<>();
        this.byType = new HashMap<>();
    }

    public void reload() {
        this.getModules().forEach(SimpleManager::setup);
    }

    public boolean register(@NotNull Module module) throws IllegalStateException {
        if (this.isPresent(module.getId())) throw new IllegalStateException("Module with such ID is already registered!");
        if (this.isPresent(module.getClass())) throw new IllegalStateException("Module of such type is already registered!");

        this.byId.put(module.getId(), module);
        this.byType.put(module.getClass(), module);

        module.init();
        module.setup();
        return true;
    }

    public void clear() {
        this.getModules().forEach(Module::shutdown);
        this.byId.clear();
        this.byType.clear();
    }

    public boolean isPresent(@NotNull String id) {
        return this.byId(id).isPresent();
    }

    public <T extends Module> boolean isPresent(@NotNull Class<T> type) {
        return this.byType(type).isPresent();
    }

    @NotNull
    public Optional<Module> byId(@NotNull String id) {
        return Optional.ofNullable(this.getById(id));
    }

    @NotNull
    public <T extends Module> Optional<T> byType(@NotNull Class<T> type) {
        return Optional.ofNullable(this.byType.get(type)).map(type::cast);
    }

    @Nullable
    public Module getById(@NotNull String id) {
        return this.byId.get(LowerCase.INTERNAL.apply(id));
    }

    @NotNull
    public Set<Module> getModules() {
        return Set.copyOf(this.byType.values());
    }
}
