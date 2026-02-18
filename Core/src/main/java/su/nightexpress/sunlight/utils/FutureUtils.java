package su.nightexpress.sunlight.utils;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class FutureUtils {

    @NonNull
    public static <T> CompletableFuture<T> loggable(@NonNull CompletableFuture<T> future) {
        return future.whenComplete(FutureUtils::printStacktrace);
    }

    public static <T> void printStacktrace(@NonNull T object, @Nullable Throwable throwable) {
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }
}
