package su.nightexpress.sunlight.module.homes.util;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.impl.Home;
import su.nightexpress.sunlight.module.homes.impl.HomeType;
import su.nightexpress.sunlight.utils.UserInfo;

import java.util.*;
import java.util.stream.Collectors;

public class HomesCache {

    private final SunLightPlugin plugin;
    private final HomesModule module;

    private final Map<String, Set<String>>              privateByName;
    private final Map<String, Set<String>>              publicByName;
    private final Map<String, Map<String, Set<String>>> invitedByName;

    public HomesCache(@NotNull SunLightPlugin plugin, @NotNull HomesModule module) {
        this.plugin = plugin;
        this.module = module;
        this.privateByName = new HashMap<>();
        this.publicByName = new HashMap<>();
        this.invitedByName = new HashMap<>();
    }

    public void initialize() {
        List<Home> homes = this.plugin.getData().getHomes();
        homes.forEach(home -> {
            if (!this.plugin.getData().isUserExists(home.getOwner().getId())) {
                this.plugin.getData().deleteHomes(home.getOwner().getId());
                this.module.info("Deleted homes data of invalid user '" + home.getOwner().getName() + "'.");
                return;
            }
            this.add(home);
        });
    }

    public void clear() {
        this.publicByName.clear();
        this.invitedByName.clear();
    }

    public void add(@NotNull Home home) {
        this.remove(home);

        this.getUserHomes().computeIfAbsent(home.getOwner().getName(), k -> new HashSet<>()).add(home.getId());

        if (home.getType() == HomeType.PUBLIC) {
            this.addPublic(home);
        }

        if (!home.getInvitedPlayers().isEmpty()) {
            this.addInvited(home);
        }
    }




    public void addPublic(@NotNull Home home) {
        this.getPublicHomes().computeIfAbsent(home.getOwner().getName(), k -> new HashSet<>()).add(home.getId());
    }

    public void addInvited(@NotNull Home home) {
        this.getInvitesHomes().computeIfAbsent(home.getOwner().getName(), k -> new HashMap<>())
            .put(home.getId(), home.getInvitedPlayers().stream().map(UserInfo::getName).collect(Collectors.toSet()));
    }




    public void remove(@NotNull Home home) {
        this.remove(home.getOwner().getName(), home.getId());
    }

    public void remove(@NotNull String userName, @NotNull String homeId) {
        this.privateByName.getOrDefault(userName, Collections.emptySet()).remove(homeId);
        this.removePublic(userName, homeId);
        this.removeInvited(userName, homeId);
    }

    public void removePublic(@NotNull Home home) {
        this.removePublic(home.getOwner().getName(), home.getId());
    }

    public void removePublic(@NotNull String ownerName, @NotNull String homeId) {
        this.getPublicHomes(ownerName).remove(homeId);
    }

    public void removeInvited(@NotNull Home home) {
        this.removeInvited(home.getOwner().getName(), home.getId());
    }

    public void removeInvited(@NotNull String ownerName, @NotNull String homeId) {
        this.getInvitesHomes().getOrDefault(ownerName, Collections.emptyMap()).remove(homeId);
    }




    @NotNull
    public Map<String, Set<String>> getUserHomes() {
        return privateByName;
    }

    @NotNull
    public Set<String> getUserHomes(@NotNull String ownerName) {
        return this.getUserHomes().getOrDefault(ownerName, Collections.emptySet());
    }

    @NotNull
    public Map<String, Set<String>> getPublicHomes() {
        return publicByName;
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
        return invitedByName;
    }

    @NotNull
    public Map<String, Set<String>> getInvitesHomes(@NotNull String ownerName) {
        return this.getInvitesHomes().getOrDefault(ownerName, Collections.emptyMap());
    }
}
