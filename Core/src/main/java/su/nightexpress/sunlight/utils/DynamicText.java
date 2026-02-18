package su.nightexpress.sunlight.utils;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;

import java.util.Arrays;
import java.util.List;

public class DynamicText {

    private final String         id;
    private final String[]       lines;
    private final int            interval;

    public DynamicText(@NotNull String id, @NotNull List<String> messages, int interval) {
        this.id = id.toLowerCase();
        this.lines = messages.toArray(new String[0]);
        this.interval = Math.max(1, interval);
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
    public String getId() {
        return this.id;
    }

    @NotNull
    public String getMessage() {
        return this.lines[(int) (System.currentTimeMillis() % (this.lines.length * this.interval) / this.interval)];
    }
}
