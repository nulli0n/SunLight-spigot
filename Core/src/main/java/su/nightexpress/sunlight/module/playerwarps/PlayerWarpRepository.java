package su.nightexpress.sunlight.module.playerwarps;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.sunlight.module.playerwarps.category.WarpCategory;
import su.nightexpress.sunlight.module.playerwarps.featuring.FeaturedSlot;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerWarpRepository {

    private final Map<String, PlayerWarp>    byIdMap;
    private final Map<UUID, Set<PlayerWarp>> byOwnerMap;

    private final Set<PlayerWarp>  featuredWarps;
    private final List<PlayerWarp> popularWarps;

    public PlayerWarpRepository() {
        this.byIdMap = new HashMap<>();
        this.byOwnerMap = new HashMap<>();

        this.featuredWarps = new HashSet<>();
        this.popularWarps = new ArrayList<>();
    }

    public void add(@NonNull PlayerWarp warp) {
        this.byIdMap.put(warp.getId(), warp);
        this.byOwnerMap.computeIfAbsent(warp.getOwnerId(), k -> new HashSet<>()).add(warp);
    }

    public void remove(@NonNull PlayerWarp warp) {
        this.byIdMap.remove(warp.getId());
        this.byOwnerMap.getOrDefault(warp.getOwnerId(), Collections.emptySet()).remove(warp);

        this.featuredWarps.remove(warp);
        this.popularWarps.remove(warp);
    }

    public void clear() {
        this.byIdMap.clear();
        this.byOwnerMap.clear();
    }

    public int size() {
        return this.byIdMap.size();
    }

    public boolean hasWarp(@NonNull String id) {
        return this.byIdMap.containsKey(LowerCase.INTERNAL.apply(id));
    }

    public boolean isFeatured(@NonNull FeaturedSlot slot, int slotIndex) {
        return this.getFeaturedWarps().stream().anyMatch(warp -> warp.isFeatured(slot, slotIndex));
    }

    public void updateFeaturedWarps() {
        this.featuredWarps.clear();

        this.stream().filter(PlayerWarp::isFeatured).forEach(this.featuredWarps::add);
    }

    public void updatePopularWarps(int limit) {
        this.popularWarps.clear();

        this.stream().sorted(Comparator.comparingLong(PlayerWarp::getTotalVisits).reversed()).limit(limit).forEach(this.popularWarps::add);
    }

    public int countWarps(@NonNull WarpCategory category) {
        return this.getByCategory(category).size();
    }

    public int countOwnedWarps(@NonNull UUID playerId) {
        return this.getByOwner(playerId).size();
    }

    @NonNull
    public Stream<PlayerWarp> stream() {
        return this.byIdMap.values().stream();
    }

    @NonNull
    public Map<String, PlayerWarp> getByIdMap() {
        return this.byIdMap;
    }

    @NonNull
    public Map<UUID, Set<PlayerWarp>> getByOwnerMap() {
        return this.byOwnerMap;
    }

    @Nullable
    public PlayerWarp getById(@NonNull String id) {
        return this.byIdMap.get(LowerCase.INTERNAL.apply(id));
    }

    @NonNull
    public Set<PlayerWarp> getByOwner(@NonNull UUID playerId) {
        return Set.copyOf(this.byOwnerMap.getOrDefault(playerId, Collections.emptySet()));
    }

    @NonNull
    public Set<PlayerWarp> getAll() {
        return Set.copyOf(this.byIdMap.values());
    }

    @NonNull
    public Set<PlayerWarp> getByCategory(@NonNull WarpCategory category) {
        return this.stream().filter(category::isWarpOfThis).collect(Collectors.toSet());
    }

    @NonNull
    public Set<PlayerWarp> getFeaturedWarps() {
        return this.featuredWarps;
    }

    @NonNull
    public List<PlayerWarp> getPopularWarps() {
        return this.popularWarps;
    }
}
