package su.nightexpress.sunlight.utils;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.data.user.SunUser;

import java.util.Objects;
import java.util.UUID;

public class UserInfo {

    private final UUID   id;
    private final String name;

    public UserInfo(@NotNull Player player) {
        this(player.getUniqueId(), player.getName());
    }

    public UserInfo(@NotNull SunUser user) {
        this(user.getId(), user.getName());
    }

    public UserInfo(@NotNull UUID uuid, @NotNull String name) {
        this.id = uuid;
        this.name = name;
    }

    @NotNull
    public UUID getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public boolean isUser(@NotNull Player player) {
        return player.getUniqueId().equals(this.getId());
    }

    public boolean isUser(@NotNull String name) {
        return this.getName().equalsIgnoreCase(name);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof UserInfo info && info.getId().equals(this.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }
}
