package su.nightexpress.sunlight.module.tab.impl;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.Placeholders;

public class TabNameFormat {

    private final int    priority;
    private final String format;

    public TabNameFormat(int priority, @NotNull String format) {
        this.priority = priority;
        this.format = Colorizer.apply(format);
    }

    public static TabNameFormat read(@NotNull JYML cfg, @NotNull String path) {
        int priority = cfg.getInt(path + ".Priority");
        String format = cfg.getString(path + ".Format", Placeholders.PLAYER_DISPLAY_NAME);

        return new TabNameFormat(priority, format);
    }

    public void write (@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".Priority", this.getPriority());
        cfg.set(path + ".Format", this.getFormat());
    }

    public int getPriority() {
        return priority;
    }

    @NotNull
    public String getFormat() {
        return format;
    }
}
