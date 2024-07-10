package su.nightexpress.sunlight.module.tab.impl;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.sunlight.Placeholders;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TabListFormat {

    private final int         priority;
    private final Set<String> worlds;
    private final Set<String> ranks;
    private final String      header;
    private final String      footer;

    public TabListFormat(
        int priority,
        @NotNull Set<String> worlds,
        @NotNull Set<String> ranks,
        @NotNull List<String> header,
        @NotNull List<String> footer
    ) {
        this.priority = priority;
        this.worlds = worlds;
        this.ranks = ranks.stream().map(String::toLowerCase).collect(Collectors.toSet());
        this.header = String.join("\n", header);
        this.footer = String.join("\n", footer);
    }

    @NotNull
    public static TabListFormat read(@NotNull FileConfig config, @NotNull String path) {
        int priority = config.getInt(path + ".Priority");
        Set<String> worlds = config.getStringSet(path + ".Worlds");
        Set<String> ranks = config.getStringSet(path + ".Groups");

        List<String> header = config.getStringList(path + ".Header");
        List<String> footer = config.getStringList(path + ".Footer");

        return new TabListFormat(priority, worlds, ranks, header, footer);
    }

    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Priority", this.getPriority());
        config.set(path + ".Worlds", this.getWorlds());
        config.set(path + ".Groups", this.getRanks());
        config.set(path + ".Header", Arrays.asList(this.getHeader().split("\n")));
        config.set(path + ".Footer", Arrays.asList(this.getFooter().split("\n")));
    }


    public boolean isGoodWorld(@NotNull World world) {
        return this.isGoodWorld(world.getName());
    }

    public boolean isGoodWorld(@NotNull String name) {
        return this.worlds.contains(Placeholders.WILDCARD) || this.worlds.contains(name);
    }

    public boolean isGoodRank(@NotNull String rank) {
        return this.ranks.contains(Placeholders.WILDCARD) || this.ranks.contains(rank.toLowerCase());
    }

    public boolean isGoodRank(@NotNull Set<String> playerRanks) {
        return playerRanks.stream().anyMatch(this::isGoodRank);
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
    public String getHeader() {
        return header;
    }

    @NotNull
    public String getFooter() {
        return footer;
    }
}
