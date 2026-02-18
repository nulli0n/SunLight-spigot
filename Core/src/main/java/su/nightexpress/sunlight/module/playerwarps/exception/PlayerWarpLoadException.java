package su.nightexpress.sunlight.module.playerwarps.exception;

import org.jspecify.annotations.NonNull;

public class PlayerWarpLoadException extends RuntimeException {

    public PlayerWarpLoadException(@NonNull String message) {
        super(message);
    }
}
