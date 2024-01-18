package su.nightexpress.sunlight.module.tab.impl;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.utils.Colorizer;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TabListFormat {

    //private final String      id;
    private final int         priority;
    private final Set<String> worlds;
    private final Set<String> groups;
    private final String      header;
    private final String      footer;

    public TabListFormat(/*@NotNull String id, */
        int priority,
        @NotNull Set<String> worlds, @NotNull Set<String> groups,
        @NotNull List<String> header, @NotNull List<String> footer) {
        //this.id = id.toLowerCase();
        this.priority = priority;
        this.worlds = worlds;
        this.groups = groups.stream().map(String::toLowerCase).collect(Collectors.toSet());
        this.header = Colorizer.apply(String.join("\n", header));
        this.footer = Colorizer.apply(String.join("\n", footer));
    }

    @NotNull
    public static TabListFormat read(@NotNull JYML cfg, @NotNull String path) {
        int priority = cfg.getInt(path + ".Priority");
        Set<String> worlds = cfg.getStringSet(path + ".Worlds");
        Set<String> groups = cfg.getStringSet(path + ".Groups");

        List<String> header = cfg.getStringList(path + ".Header");
        List<String> footer = cfg.getStringList(path + ".Footer");

        return new TabListFormat(priority, worlds, groups, header, footer);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".Priority", this.getPriority());
        cfg.set(path + ".Worlds", this.getWorlds());
        cfg.set(path + ".Groups", this.getGroups());
        cfg.set(path + ".Header", Arrays.asList(this.getHeader().split("\n")));
        cfg.set(path + ".Footer", Arrays.asList(this.getFooter().split("\n")));
    }

    /*@NotNull
    public String getId() {
        return id;
    }*/

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
    public String getHeader() {
        return header;
    }

    @NotNull
    public String getFooter() {
        return footer;
    }
}
