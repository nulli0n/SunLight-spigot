package su.nightexpress.sunlight.module;

import java.nio.file.Path;

import org.jspecify.annotations.NonNull;

@FunctionalInterface
public interface ModuleContextProvider {

    @NonNull
    ModuleContext createModuleContext(@NonNull String id, @NonNull Path path, @NonNull ModuleDefinition definition);
}
