package su.nightexpress.sunlight.module.tab.impl;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Colorizer;

public class NameTagFormat {

    private final String id;
    private final int    priority;
    private final String prefix;
    private final String suffix;
    private final String color;

    private int index;

    public NameTagFormat(@NotNull String id, int priority, @NotNull String prefix, @NotNull String suffix, @NotNull String color) {
        this.id = id.toLowerCase();
        this.priority = priority;
        this.prefix = prefix;
        this.suffix = suffix;
        this.color = color;
    }

    @NotNull
    public static NameTagFormat read(@NotNull FileConfig config, @NotNull String path, @NotNull String id) {
        int priority = config.getInt(path + ".Priority");
        String prefix = config.getString(path + ".Prefix", "");
        String suffix = config.getString(path + ".Suffix", "");
        String color = config.getString(path + ".Color", ChatColor.GRAY.name());

        return new NameTagFormat(id, priority, prefix, suffix, color);
    }

    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Priority", this.getPriority());
        config.set(path + ".Prefix", this.getPrefix());
        config.set(path + ".Suffix", this.getSuffix());
        config.set(path + ".Color", this.getColor());
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @NotNull
    public String getId() {
        return id;
    }

    public int getPriority() {
        return priority;
    }

    @NotNull
    public String getPrefix() {
        return prefix;
    }

    @NotNull
    public String getSuffix() {
        return suffix;
    }

    @NotNull
    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "NametagFormat{" +
            "id='" + id + '\'' +
            ", priority=" + priority +
            ", prefix='" + prefix + '\'' +
            ", suffix='" + suffix + '\'' +
            ", color=" + color +
            ", index=" + index +
            '}';
    }
}
