package su.nightexpress.sunlight.exception;

import org.jetbrains.annotations.NotNull;

public class ModuleLoadException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ModuleLoadException(@NotNull String message) {
        super(message);
    }
}
