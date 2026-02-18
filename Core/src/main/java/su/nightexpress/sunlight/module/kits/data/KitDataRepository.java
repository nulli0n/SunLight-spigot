package su.nightexpress.sunlight.module.kits.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.LowerCase;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class KitDataRepository {

    private final Map<UUID, Map<String, KitData>> dataMapById;

    public KitDataRepository() {
        this.dataMapById = new ConcurrentHashMap<>();
    }

    public synchronized void clear() {
        this.dataMapById.clear();
    }

    public synchronized void add(@NotNull KitData data) {
        this.getUserDataMap(data.getPlayerId()).put(data.getKitId(), data);
    }

    public synchronized void remove(@NotNull UUID playerId, @NotNull String kitId) {
        this.getUserDataMap(playerId).remove(LowerCase.INTERNAL.apply(kitId));
    }

    @NotNull
    public Map<String, KitData> getUserDataMap(@NotNull UUID playerId) {
        return this.dataMapById.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>());
    }

    @Nullable
    public KitData getKitData(@NotNull UUID playerId, @NotNull String kitId) {
        return this.getUserDataMap(playerId).get(LowerCase.INTERNAL.apply(kitId));
    }

    @NotNull
    public Optional<KitData> kitData(@NotNull UUID playerId, @NotNull String kitId) {
        return Optional.ofNullable(this.getKitData(playerId, kitId));
    }

    @NotNull
    public Set<KitData> getAll() {
        return this.dataMapById.values().stream().flatMap(dataMap -> dataMap.values().stream()).collect(Collectors.toSet());
    }
}
