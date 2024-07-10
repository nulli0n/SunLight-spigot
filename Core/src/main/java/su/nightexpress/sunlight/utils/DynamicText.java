package su.nightexpress.sunlight.utils;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.placeholder.Placeholder;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class DynamicText implements Placeholder {

    public static final Function<String, String> PLACEHOLDER = id -> "%animation:" + id + "%";

    private final String         id;
    private final String[]       lines;
    private final int            interval;
    private final PlaceholderMap placeholders;

    public DynamicText(@NotNull String id, @NotNull List<String> messages, int interval) {
        this.id = id.toLowerCase();
        this.lines = messages.toArray(new String[0]);
        this.interval = Math.max(1, interval);

        this.placeholders = new PlaceholderMap().add(PLACEHOLDER.apply(this.id), this::getMessage);
    }

    @NotNull
    public static DynamicText read(@NotNull FileConfig config, @NotNull String path, @NotNull String id) {
        int interval = config.getInt(path + ".Update_Interval_MS");
        List<String> messages = config.getStringList(path + ".Texts");

        return new DynamicText(id, messages, interval);
    }

    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Update_Interval_MS", this.interval);
        config.set(path + ".Texts", Arrays.asList(this.lines));
    }

    @NotNull
    @Override
    public PlaceholderMap getPlaceholders() {
        return placeholders;
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @NotNull
    public String getMessage() {
        return this.lines[(int) (System.currentTimeMillis() % (this.lines.length * this.interval) / this.interval)];
    }

    @NotNull
    @Deprecated
    public final String replace(@NotNull String text) {
        return text.replace(PLACEHOLDER.apply(this.id), this.getMessage());
    }
}
