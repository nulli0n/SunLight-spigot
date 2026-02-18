package su.nightexpress.sunlight.module.spawns.model;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.sunlight.SLPlaceholders;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SpawnRule implements Writeable {

    private boolean     enabled;
    private Set<String> ranks;

    public SpawnRule(boolean enabled, @NotNull Set<String> ranks) {
        this.setEnabled(enabled);
        this.setRanks(ranks);
    }

    @NotNull
    public static SpawnRule read(@NotNull FileConfig config, @NotNull String path) {
        boolean enabled = config.getBoolean(path + ".Enabled");
        Set<String> ranks = Lists.modify(config.getStringSet(path + ".Ranks"), LowerCase.INTERNAL::apply);

        return new SpawnRule(enabled, ranks);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Enabled", this.enabled);
        config.set(path + ".Ranks", this.ranks);
    }

    public boolean isApplicable(@NotNull Player player) {
        if (!this.enabled) return false;
        if (this.ranks.contains(SLPlaceholders.WILDCARD)) return true;

        Set<String> playerRanks = Players.getInheritanceGroups(player);
        return playerRanks.stream().anyMatch(this.ranks::contains);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @NotNull
    public Set<String> getRanks() {
        return this.ranks;
    }

    public void setRanks(@NotNull Collection<String> ranks) {
        this.ranks = new HashSet<>(ranks);
    }
}
