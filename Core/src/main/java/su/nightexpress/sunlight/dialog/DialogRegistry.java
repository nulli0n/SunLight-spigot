package su.nightexpress.sunlight.dialog;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.sunlight.SunLightPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class DialogRegistry {

    private final SunLightPlugin               plugin;
    private final Map<DialogKey<?>, Dialog<?>> byKey;

    public DialogRegistry(@NotNull SunLightPlugin plugin) {
        this.plugin = plugin;
        this.byKey = new HashMap<>();
    }

    public void clear() {
        this.byKey.clear();
    }

    public <T> void register(@NotNull DialogKey<T> key, @NotNull Supplier<Dialog<T>> supplier) {
        this.register(key, supplier.get());
    }

    public <T> void register(@NotNull DialogKey<T> key, @NotNull Dialog<T> dialog) {
        if (this.byKey.putIfAbsent(key, dialog) == null) {
            this.plugin.injectLang(dialog);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> boolean show(@NotNull Player player, @NotNull DialogKey<T> key, @NotNull T data, @Nullable Runnable callback) {
        Dialog<T> dialog = (Dialog<T>) this.byKey.get(key);

        if (dialog == null) {
            this.plugin.warn("Dialog '%s' not found or disabled.".formatted(key.id()));
            return false;
        }

        dialog.show(player, data, callback);
        return true;
    }
}
