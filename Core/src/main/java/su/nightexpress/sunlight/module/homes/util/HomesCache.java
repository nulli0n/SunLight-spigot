package su.nightexpress.sunlight.module.homes.util;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.impl.Home;
import su.nightexpress.sunlight.module.homes.impl.HomeType;
import su.nightexpress.sunlight.utils.UserInfo;

import java.util.*;
import java.util.stream.Collectors;

public class HomesCache {

    private final HomesModule              module;
    private final Map<String, Set<String>> userHomes;
    private final Map<String, Set<String>>              publicHomes;
    private final Map<String, Map<String, Set<String>>> invitesHomes;

    public HomesCache(@NotNull HomesModule module) {
        this.module = module;
        this.userHomes = new HashMap<>();
        this.publicHomes = new HashMap<>();
        this.invitesHomes = new HashMap<>();
    }

    public void initialize() {
        List<Home> homes = this.module.plugin().getData().getHomes();
        homes.forEach(home -> {
            if (!this.module.plugin().getData().isUserExists(home.getOwner().getId())) {
                this.module.plugin().getData().deleteHomes(home.getOwner().getId());
                this.module.info("Deleted homes data of invalid user '" + home.getOwner().getName() + "'.");
                return;
            }
            this.cache(home);
        });
    }

    public void clear() {
        this.publicHomes.clear();
        this.invitesHomes.clear();
    }

    public void cache(@NotNull Home home) {
        this.uncache(home);

        this.getUserHomes().computeIfAbsent(home.getOwner().getName(), k -> new HashSet<>()).add(home.getId());

        if (home.getType() == HomeType.PUBLIC) {
            this.cachePublicHome(home);
        }

        if (!home.getInvitedPlayers().isEmpty()) {
            this.cacheInviteHome(home);
        }
    }

    public void uncache(@NotNull Home home) {
        this.uncache(home.getOwner().getName(), home.getId());
    }

    public void uncache(@NotNull String userName, @NotNull String homeId) {
        this.getUserHomes().getOrDefault(userName, Collections.emptySet()).remove(homeId);
        this.uncachePublicHome(userName, homeId);
        this.uncacheInvitesHome(userName, homeId);
    }

    @NotNull
    public Map<String, Set<String>> getUserHomes() {
        return userHomes;
    }

    @NotNull
    public Set<String> getUserHomes(@NotNull String ownerName) {
        return this.getUserHomes().getOrDefault(ownerName, Collections.emptySet());
    }

    @NotNull
    public Map<String, Set<String>> getPublicHomes() {
        return publicHomes;
    }

    @NotNull
    public Set<String> getPublicHomes(@NotNull String ownerName) {
        return this.getPublicHomes().getOrDefault(ownerName, Collections.emptySet());
    }

    @NotNull
    public List<String> getInviteOwnersHomes(@NotNull String visitor) {
        return this.getInvitesHomes().entrySet().stream()
            .filter(entry -> entry.getValue().values().stream().anyMatch(invites -> invites.contains(visitor)))
            .map(Map.Entry::getKey).toList();
    }

    @NotNull
    public List<String> getInvitesHomes(@NotNull String ownerName, @NotNull String visitor) {
        return this.getInvitesHomes(ownerName).entrySet().stream()
            .filter(entry -> entry.getValue().stream().anyMatch(invites -> invites.contains(visitor)))
            .map(Map.Entry::getKey).toList();
    }

    @NotNull
    public Map<String, Map<String, Set<String>>> getInvitesHomes() {
        return invitesHomes;
    }

    @NotNull
    public Map<String, Set<String>> getInvitesHomes(@NotNull String ownerName) {
        return this.getInvitesHomes().getOrDefault(ownerName, Collections.emptyMap());
    }

    public void cachePublicHome(@NotNull Home home) {
        this.getPublicHomes().computeIfAbsent(home.getOwner().getName(), k -> new HashSet<>()).add(home.getId());
    }

    public void cacheInviteHome(@NotNull Home home) {
        this.getInvitesHomes().computeIfAbsent(home.getOwner().getName(), k -> new HashMap<>())
            .put(home.getId(), home.getInvitedPlayers().stream().map(UserInfo::getName).collect(Collectors.toSet()));
    }

    public void uncachePublicHome(@NotNull Home home) {
        this.uncachePublicHome(home.getOwner().getName(), home.getId());
    }

    public void uncachePublicHome(@NotNull String ownerName, @NotNull String homeId) {
        this.getPublicHomes(ownerName).remove(homeId);
    }

    public void uncacheInvitesHome(@NotNull Home home) {
        this.uncacheInvitesHome(home.getOwner().getName(), home.getId());
    }

    public void uncacheInvitesHome(@NotNull String ownerName, @NotNull String homeId) {
        this.getInvitesHomes().getOrDefault(ownerName, Collections.emptyMap()).remove(homeId);
    }
}
