package su.nightexpress.sunlight.module.scoreboard.impl;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.sunlight.Placeholders;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BoardConfig {

    private final String       id;
    private final int          updateInterval;
    private final int          priority;
    private final Set<String>  worlds;
    private final Set<String>  ranks;
    private final String       title;
    private final List<String> lines;

    public BoardConfig(@NotNull String id,
                       int updateInterval,
                       int priority,
                       @NotNull Set<String> worlds,
                       @NotNull Set<String> ranks,
                       @NotNull String title,
                       @NotNull List<String> lines
    ) {
        this.id = id.toLowerCase();
        this.updateInterval = Math.max(1, updateInterval);
        this.priority = priority;
        this.worlds = worlds.stream().map(String::toLowerCase).collect(Collectors.toSet());
        this.ranks = ranks.stream().map(String::toLowerCase).collect(Collectors.toSet());
        this.title = title;
        this.lines = lines;
    }

    @NotNull
    public static BoardConfig read(@NotNull FileConfig config, @NotNull String path, @NotNull String id) {
        int updateInterval = config.getInt(path + ".Update_Interval", 20);
        int priority = config.getInt(path + ".Priority");
        Set<String> worlds = config.getStringSet(path + ".Worlds");
        Set<String> groups = config.getStringSet(path + ".Groups");
        String title = config.getString(path + ".Title", "");
        List<String> lines = config.getStringList(path + ".Lines");

        return new BoardConfig(id, updateInterval, priority, worlds, groups, title, lines);
    }

    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Update_Interval", this.getUpdateInterval());
        config.set(path + ".Priority", this.getPriority());
        config.set(path + ".Worlds", this.getWorlds());
        config.set(path + ".Groups", this.getRanks());
        config.set(path + ".Title", this.getTitle());
        config.set(path + ".Lines", this.getLines());
    }

    public boolean isGoodWorld(@NotNull World world) {
        return this.isGoodWorld(world.getName());
    }

    public boolean isGoodWorld(@NotNull String name) {
        return this.worlds.contains(Placeholders.WILDCARD) || this.worlds.contains(name.toLowerCase());
    }

    public boolean isGoodRank(@NotNull String rank) {
        return this.ranks.contains(Placeholders.WILDCARD) || this.ranks.contains(rank.toLowerCase());
    }

    public boolean isGoodRank(@NotNull Set<String> playerRanks) {
        return playerRanks.stream().anyMatch(this::isGoodRank);
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
    public Set<String> getRanks() {
        return ranks;
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
