package su.nightexpress.sunlight.module.tab.impl;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.utils.Colorizer;

public class NametagFormat {

    private final String    id;
    private final int       priority;
    private final String    prefix;
    private final String    suffix;
    private final ChatColor color;

    private int index;

    public NametagFormat(@NotNull String id, int priority, @NotNull String prefix, @NotNull String suffix, @NotNull ChatColor color) {
        this.id = id.toLowerCase();
        this.priority = priority;
        this.prefix = Colorizer.apply(prefix);
        this.suffix = Colorizer.apply(suffix);
        if (!color.isColor()) {
            this.color = ChatColor.GRAY;
        }
        else {
            this.color = color;
        }
    }

    @NotNull
    public static NametagFormat read(@NotNull JYML cfg, @NotNull String path, @NotNull String id) {
        int priority = cfg.getInt(path + ".Priority");
        String prefix = cfg.getString(path + ".Prefix", "");
        String suffix = cfg.getString(path + ".Suffix", "");
        ChatColor color = cfg.getEnum(path + ".Color", ChatColor.class, ChatColor.GRAY);

        return new NametagFormat(id, priority, prefix, suffix, color);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".Priority", this.getPriority());
        cfg.set(path + ".Prefix", this.getPrefix());
        cfg.set(path + ".Suffix", this.getSuffix());
        cfg.set(path + ".Color", this.getColor().name());
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
    public ChatColor getColor() {
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
