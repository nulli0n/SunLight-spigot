package su.nightexpress.sunlight.module;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ModuleFactory<T extends Module> {

    @NotNull T load(@NotNull ModuleContext context);
}
