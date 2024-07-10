package su.nightexpress.sunlight.module.chat.mention;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.sunlight.module.chat.ChatChannel;
import su.nightexpress.sunlight.module.chat.config.ChatPerms;
import su.nightexpress.sunlight.module.chat.util.Placeholders;

import java.util.Set;
import java.util.stream.Collectors;

public class GroupMention implements Mention {

    private final String      id;
    private final String      format;
    private final Set<String> groups;

    public GroupMention(@NotNull String id, @NotNull String format, @NotNull Set<String> groups) {
        this.id = id.toLowerCase();
        this.format = format;
        this.groups = groups.stream().map(String::toLowerCase).collect(Collectors.toSet());
    }

    @NotNull
    public static GroupMention read(@NotNull FileConfig config, @NotNull String path, @NotNull String id) {
        String format = config.getString(path + ".Format", "");
        Set<String> groups = config.getStringSet(path + ".Affected_Groups");
        return new GroupMention(id, format, groups);
    }

    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Format", this.format);
        config.set(path + ".Affected_Groups", this.groups);
    }

    @Override
    public boolean hasPermission(@NotNull Player player) {
        return player.hasPermission(ChatPerms.MENTION) || player.hasPermission(ChatPerms.MENTION_SPECIAL + this.getId());
    }

    @Override
    @NotNull
    public Set<Player> getAffectedPlayers(@NotNull ChatChannel channel) {
        return channel.getPlayers().stream().filter(this::isApplicable).collect(Collectors.toSet());
    }

    @NotNull
    public String getId() {
        return id;
    }

    @Override
    @NotNull
    public String getFormat() {
        return format;
    }

    @NotNull
    public Set<String> getGroups() {
        return groups;
    }

    public boolean isApplicable(@NotNull Player player) {
        if (this.groups.contains(Placeholders.WILDCARD)) return true;

        Set<String> groups = Players.getPermissionGroups(player);
        return this.groups.stream().anyMatch(groups::contains);
    }
}
