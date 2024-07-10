package su.nightexpress.sunlight.module.tab.impl;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.sunlight.Placeholders;

public class TabNameFormat {

    private final int    priority;
    private final String format;

    public TabNameFormat(int priority, @NotNull String format) {
        this.priority = priority;
        this.format = format;
    }

    @NotNull
    public static TabNameFormat read(@NotNull FileConfig config, @NotNull String path) {
        int priority = config.getInt(path + ".Priority");
        String format = config.getString(path + ".Format", Placeholders.PLAYER_DISPLAY_NAME);

        return new TabNameFormat(priority, format);
    }

    public void write (@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Priority", this.getPriority());
        config.set(path + ".Format", this.getFormat());
    }

    public int getPriority() {
        return priority;
    }

    @NotNull
    public String getFormat() {
        return format;
    }
}
