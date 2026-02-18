package su.nightexpress.sunlight.module.chat.mention;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.sunlight.SLPlaceholders;

import java.util.Set;

public class GroupMention implements ChatMention, Writeable {

    private final String      format;
    private final Set<String> ranks;

    public GroupMention(@NotNull String format, @NotNull Set<String> ranks) {
        this.format = format;
        this.ranks = ranks;
    }

    @NotNull
    public static GroupMention read(@NotNull FileConfig config, @NotNull String path) {
        String format = config.getString(path + ".Format", "");
        Set<String> groups = Lists.modify(config.getStringSet(path + ".Included-Ranks"), String::toLowerCase);

        return new GroupMention(format, groups);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Format", this.format);
        config.set(path + ".Included-Ranks", this.ranks);
    }

    @Override
    public boolean isApplicable(@NotNull Player player) {
        if (this.ranks.contains(SLPlaceholders.WILDCARD)) return true;

        Set<String> groups = Players.getInheritanceGroups(player);
        return this.ranks.stream().anyMatch(groups::contains);
    }

    @Override
    @NotNull
    public String getFormat() {
        return this.format;
    }

    @NotNull
    public Set<String> getRanks() {
        return this.ranks;
    }
}
