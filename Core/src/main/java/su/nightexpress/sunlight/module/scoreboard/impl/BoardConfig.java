package su.nightexpress.sunlight.module.scoreboard.impl;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BoardConfig {

    private final String       id;
    private final int          updateInterval;
    private final int          priority;
    private final Set<String>  worlds;
    private final Set<String>  groups;
    private final String       title;
    private final List<String> lines;

    public BoardConfig(@NotNull String id, int updateInterval, int priority,
                       @NotNull Set<String> worlds, @NotNull Set<String> groups,
                       @NotNull String title, @NotNull List<String> lines) {
        this.id = id.toLowerCase();
        this.updateInterval = Math.max(1, updateInterval);
        this.priority = priority;
        this.worlds = worlds;
        this.groups = groups.stream().map(String::toLowerCase).collect(Collectors.toSet());
        this.title = title;
        this.lines = lines;
    }

    @NotNull
    public static BoardConfig read(@NotNull JYML cfg, @NotNull String path, @NotNull String id) {
        int updateInterval = cfg.getInt(path + ".Update_Interval", 20);
        int priority = cfg.getInt(path + ".Priority");
        Set<String> worlds = cfg.getStringSet(path + ".Worlds");
        Set<String> groups = cfg.getStringSet(path + ".Groups");
        String title = cfg.getString(path + ".Title", "");
        List<String> lines = cfg.getStringList(path + ".Lines");

        return new BoardConfig(id, updateInterval, priority, worlds, groups, title, lines);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".Update_Interval", this.getUpdateInterval());
        cfg.set(path + ".Priority", this.getPriority());
        cfg.set(path + ".Worlds", this.getWorlds());
        cfg.set(path + ".Groups", this.getGroups());
        cfg.set(path + ".Title", this.getTitle());
        cfg.set(path + ".Lines", this.getLines());
    }

    @NotNull
    public String getId() {
        return id;
    }

    public int getUpdateInterval() {
        return updateInterval;
    }

    public int getPriority() {
        return priority;
    }

    @NotNull
    public Set<String> getWorlds() {
        return worlds;
    }

    @NotNull
    public Set<String> getGroups() {
        return groups;
    }

    @NotNull
    public String getTitle() {
        return title;
    }

    @NotNull
    public List<String> getLines() {
        return lines;
    }
}
