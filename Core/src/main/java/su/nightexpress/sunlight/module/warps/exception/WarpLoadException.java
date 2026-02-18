package su.nightexpress.sunlight.module.warps.exception;

import org.jspecify.annotations.NonNull;

public class WarpLoadException extends RuntimeException {

    public WarpLoadException(@NonNull String message) {
        super(message);
    }
}
