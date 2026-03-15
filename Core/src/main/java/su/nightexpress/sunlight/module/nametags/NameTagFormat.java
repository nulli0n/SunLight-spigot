package su.nightexpress.sunlight.module.nametags;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.sunlight.SLPlaceholders;

import java.util.Set;

public class NameTagFormat implements Writeable {

    private final int         priority;
    private final Set<String> ranks;
    private final String      prefix;
    private final String      suffix;
    private final String      color;

    public NameTagFormat(int priority, @NotNull Set<String> ranks, @NotNull String prefix, @NotNull String suffix, @NotNull String color) {
        this.priority = priority;
        this.ranks = ranks;
        this.prefix = prefix;
        this.suffix = suffix;
        this.color = color;
    }

    @NotNull
    public static NameTagFormat read(@NotNull FileConfig config, @NotNull String path) {
        int priority = config.getInt(path + ".Priority");
        Set<String> ranks = Lists.modify(config.getStringSet(path + ".Ranks"), LowerCase.INTERNAL::apply);
        String prefix = config.getString(path + ".Prefix", "");
        String suffix = config.getString(path + ".Suffix", "");
        String color = config.getString(path + ".Color", "white");

        return new NameTagFormat(priority, ranks, prefix, suffix, color);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Priority", this.priority);
        config.set(path + ".Ranks", this.ranks);
        config.set(path + ".Prefix", this.prefix);
        config.set(path + ".Suffix", this.suffix);
        config.set(path + ".Color", this.color);
    }

    public boolean isRankAvailable(@NotNull Player player) {
        if (this.ranks.contains(SLPlaceholders.WILDCARD)) return true;

        Set<String> playerRanks = Players.getInheritanceGroups(player);
        return playerRanks.stream().anyMatch(this.ranks::contains);
    }

    public int getPriority() {
        return this.priority;
    }

    @NotNull
    public Set<String> getRanks() {
        return this.ranks;
    }

    @NotNull
    public String getPrefix() {
        return this.prefix;
    }

    @NotNull
    public String getSuffix() {
        return this.suffix;
    }

    @NotNull
    public String getColor() {
        return this.color;
    }
}
