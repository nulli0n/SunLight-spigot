package su.nightexpress.sunlight.module;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ModuleRegistration {

    private final ModuleFactory<?>        factory;
    private final Supplier<LoadCondition> condition;

    public ModuleRegistration(@NotNull ModuleFactory<?> factory, @NotNull Supplier<LoadCondition> condition) {
        this.factory = factory;
        this.condition = condition;
    }

    public ModuleRegistration(@NotNull ModuleFactory<?> factory) {
        this(factory, LoadCondition::success);
    }

    @NotNull
    public ModuleFactory<?> getFactory() {
        return this.factory;
    }

    @NotNull
    public Supplier<LoadCondition> getCondition() {
        return this.condition;
    }
}
