package su.nightexpress.sunlight.module.homes.repository;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.homes.impl.Home;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GlobalHomeRepository {

    private final Map<UUID, UserHomeRepository> homesByOwnerMap;

    public GlobalHomeRepository() {
        this.homesByOwnerMap = new ConcurrentHashMap<>();
    }

    public void clear() {
        this.homesByOwnerMap.values().forEach(UserHomeRepository::clear);
        this.homesByOwnerMap.clear();
    }

    @NotNull
    public UserHomeRepository getUserRepository(@NotNull UUID playerId) {
        return this.homesByOwnerMap.computeIfAbsent(playerId, k -> new UserHomeRepository());
    }

    public synchronized void add(@NotNull Home home) {
        UUID ownerId = home.getOwner().id();
        this.getUserRepository(ownerId).add(home);

    }

    public synchronized void remove(@NotNull Home home) {
        UUID ownerId = home.getOwner().id();
        this.getUserRepository(ownerId).remove(home);
    }

    @NotNull
    public Set<Home> getAll() {
        return this.getAll(home -> true);
    }

    @NotNull
    public Set<Home> getAll(@NotNull Predicate<Home> predicate) {
        return this.homesByOwnerMap.values().stream().flatMap(repository -> repository.getAll().stream()).collect(Collectors.toSet());
    }

    @NotNull
    @Deprecated
    public Set<Home> getAvailableForVisit(@NotNull Player player) {
        return this.getAll(home -> home.canVisit(player));
    }
}
