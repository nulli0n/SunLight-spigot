package su.nightexpress.sunlight.actions.parameter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ParameterResult {

    private final Map<String, Object> values;

    public ParameterResult(@NotNull Map<String, Object> values) {
        this.values = values;
    }

    @Nullable
    public Object getValue(@NotNull String id) {
        return this.values.get(id.toLowerCase());
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public <T> T getValueOrDefault(@NotNull String id, @NotNull T def) {
        T value = (T) this.getValue(id);
        return value == null ? def : value;
    }

    public boolean hasValue(@NotNull String id) {
        return this.getValue(id) != null;
    }
}
