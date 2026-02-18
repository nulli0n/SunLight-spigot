package su.nightexpress.sunlight.module.tab.format;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.sunlight.SLPlaceholders;

import java.util.Set;

public class TabNameFormat implements Writeable {

    private final int         priority;
    private final Set<String> ranks;
    private final String      format;

    public TabNameFormat(int priority, @NotNull Set<String> ranks, @NotNull String format) {
        this.priority = priority;
        this.ranks = ranks;
        this.format = format;
    }

    @NotNull
    public static TabNameFormat read(@NotNull FileConfig config, @NotNull String path) {
        int priority = config.getInt(path + ".Priority");
        Set<String> ranks = Lists.modify(config.getStringSet(path + ".Ranks"), String::toLowerCase);
        String format = config.getString(path + ".Format", SLPlaceholders.PLAYER_DISPLAY_NAME);

        return new TabNameFormat(priority, ranks, format);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Priority", this.priority);
        config.set(path + ".Ranks", this.ranks);
        config.set(path + ".Format", this.format);
    }

    public boolean isAvailable(@NotNull Player player) {
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
    public String getFormat() {
        return this.format;
    }
}
