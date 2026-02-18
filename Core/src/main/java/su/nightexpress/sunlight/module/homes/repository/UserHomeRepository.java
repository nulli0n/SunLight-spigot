package su.nightexpress.sunlight.module.homes.repository;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.sunlight.module.homes.impl.Home;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UserHomeRepository {

    private final Map<String, Home> homByIdMap;

    public UserHomeRepository() {
        this.homByIdMap = new ConcurrentHashMap<>();
    }

    void clear() {
        this.homByIdMap.clear();
    }

    synchronized void add(@NotNull Home home) {
        this.homByIdMap.put(home.getId(), home);
    }

    synchronized void remove(@NotNull Home home) {
        this.remove(home.getId());
    }

    synchronized void remove(@NotNull String id) {
        this.homByIdMap.remove(LowerCase.INTERNAL.apply(id));
    }

    @NotNull
    public Set<Home> getAll() {
        return Set.copyOf(this.homByIdMap.values());
    }

    @NotNull
    public Set<Home> getAll(@NotNull Predicate<Home> predicate) {
        return this.homByIdMap.values().stream().filter(predicate).collect(Collectors.toSet());
    }

    @Nullable
    public Home getById(@NotNull String id) {
        return this.homByIdMap.get(LowerCase.INTERNAL.apply(id));
    }
}
