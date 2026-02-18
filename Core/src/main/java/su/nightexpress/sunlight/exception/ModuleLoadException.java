package su.nightexpress.sunlight.exception;

import org.jetbrains.annotations.NotNull;

public class ModuleLoadException extends RuntimeException {

    public ModuleLoadException(@NotNull String message) {
        super(message);
    }
}
