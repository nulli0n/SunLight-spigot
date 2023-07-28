package su.nightexpress.sunlight.data.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.data.AbstractUser;
import su.nexmedia.engine.utils.Colorizer;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandCooldown;
import su.nightexpress.sunlight.data.impl.cooldown.CooldownInfo;
import su.nightexpress.sunlight.data.impl.cooldown.CooldownType;
import su.nightexpress.sunlight.data.impl.settings.BasicSettings;
import su.nightexpress.sunlight.module.kits.Kit;
import su.nightexpress.sunlight.module.warps.impl.Warp;
import su.nightexpress.sunlight.utils.UserInfo;

import java.util.*;

public class SunUser extends AbstractUser<SunLight> {

    private final Map<CooldownType, Set<CooldownInfo>> cooldowns;
    private final Map<UUID, IgnoredUser> ignoredUsers;
    private final BasicSettings          settings;

    private String ipAddress;
    private String customName;

    public SunUser(@NotNull SunLight plugin, @NotNull UUID uuid, @NotNull String name) {
        this(plugin, uuid, name, System.currentTimeMillis(), System.currentTimeMillis(),
            "N/A",
            null,
            new HashMap<>(), // Cooldowns
            new HashMap<>(), // Ignore List
            new BasicSettings()
        );
    }

    public SunUser(@NotNull SunLight plugin, @NotNull UUID uuid, @NotNull String name,
                   long lastOnline, long dateCreated,
        @NotNull String ipAddress,
        @Nullable String customName,
        @NotNull Map<CooldownType, Set<CooldownInfo>> cooldowns,
        @NotNull Map<UUID, IgnoredUser> ignoredUsers,
        @NotNull BasicSettings settings
        ) {
        super(plugin, uuid, name, lastOnline, dateCreated);

        this.setIp(ipAddress);
        this.setCustomName(customName);
        this.cooldowns = new HashMap<>(cooldowns);
        this.ignoredUsers = new HashMap<>(ignoredUsers);
        this.settings = settings;
    }

    @NotNull
    public Optional<String> getCustomName() {
        return Optional.ofNullable(this.customName);
    }

    public void setCustomName(@Nullable String customName) {
        this.customName = customName == null || customName.equalsIgnoreCase("null") ? null : Colorizer.apply(customName);
    }

    public void updatePlayerName() {
        this.getCustomName().ifPresent(name -> {
            Player player = this.getPlayer();
            if (player != null) {
                player.setDisplayName(name);
            }
        });
    }

    @NotNull
    public String getIp() {
        return this.ipAddress;
    }

    public void setIp(@NotNull String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @NotNull
    public Map<CooldownType, Set<CooldownInfo>> getCooldowns() {
        this.cooldowns.values().forEach(set -> set.removeIf(CooldownInfo::isExpired));
        return cooldowns;
    }

    @NotNull
    public Set<CooldownInfo> getCooldowns(@NotNull CooldownType type) {
        return this.getCooldowns().computeIfAbsent(type, k -> new HashSet<>());
    }

    public void addCooldown(@NotNull CooldownInfo cooldown) {
        Set<CooldownInfo> infos = this.getCooldowns(cooldown.getType());
        infos.removeIf(has -> has.isSimilar(cooldown));
        infos.add(cooldown);
    }

    public boolean removeCooldown(@NotNull Kit kit) {
        return this.removeCooldown(CooldownType.KIT, kit.getId());
    }

    public boolean removeCooldown(@NotNull Warp warp) {
        return this.removeCooldown(CooldownType.WARP, warp.getId());
    }

    public boolean removeCooldown(@NotNull CommandCooldown cooldown) {
        return this.removeCooldown(CooldownType.COMMAND, cooldown.getId());
    }

    public boolean removeCooldown(@NotNull CooldownType type, @NotNull String object) {
        return this.getCooldowns(type).removeIf(cooldownInfo -> cooldownInfo.isSimilar(object));
    }

    @NotNull
    public Optional<CooldownInfo> getCooldown(@NotNull CommandCooldown cooldown) {
        return this.getCooldown(CooldownType.COMMAND, cooldown.getId());
    }

    @NotNull
    public Optional<CooldownInfo> getCooldown(@NotNull Kit kit) {
        return this.getCooldown(CooldownType.KIT, kit.getId());
    }

    @NotNull
    public Optional<CooldownInfo> getCooldown(@NotNull Warp warp) {
        return this.getCooldown(CooldownType.WARP, warp.getId());
    }

    @NotNull
    public Optional<CooldownInfo> getCooldown(@NotNull CooldownType type, @NotNull String object) {
        return this.getCooldowns(type).stream().filter(has -> has.isSimilar(object)).findFirst();
    }

    @NotNull
    public Map<UUID, IgnoredUser> getIgnoredUsers() {
        return this.ignoredUsers;
    }

    public boolean isIgnoredUser(@NotNull Player player) {
        return this.getIgnoredUser(player.getUniqueId()) != null;
    }

    public boolean isIgnoredUser(@NotNull SunUser user) {
        return this.getIgnoredUser(user.getId()) != null;
    }

    public boolean isIgnoredUser(@NotNull UUID playerId) {
        return this.getIgnoredUser(playerId) != null;
    }

    public boolean addIgnoredUser(@NotNull Player player) {
        return this.addIgnoredUser(new UserInfo(player));
    }

    public boolean addIgnoredUser(@NotNull SunUser user) {
        return this.addIgnoredUser(new UserInfo(user));
    }

    public boolean addIgnoredUser(@NotNull UserInfo userInfo) {
        if (this.isIgnoredUser(userInfo.getId()) || userInfo.getId().equals(this.getId())) return false;

        IgnoredUser ignoredUser = new IgnoredUser(userInfo);
        this.getIgnoredUsers().put(userInfo.getId(), ignoredUser);
        return true;
    }

    public boolean removeIgnoredUser(@NotNull Player player) {
        return this.removeIgnoredUser(player.getUniqueId());
    }

    public boolean removeIgnoredUser(@NotNull SunUser user) {
        return this.removeIgnoredUser(user.getId());
    }

    public boolean removeIgnoredUser(@NotNull UUID id) {
        return this.getIgnoredUsers().remove(id) != null;
    }

    @Nullable
    public IgnoredUser getIgnoredUser(@NotNull Player player) {
        return this.getIgnoredUser(player.getUniqueId());
    }

    @Nullable
    public IgnoredUser getIgnoredUser(@NotNull SunUser user) {
        return this.getIgnoredUsers().get(user.getId());
    }

    @Nullable
    public IgnoredUser getIgnoredUser(@NotNull UUID playerId) {
        return this.getIgnoredUsers().get(playerId);
    }

    @Nullable
    public IgnoredUser getIgnoredUser(@NotNull String name) {
        return this.getIgnoredUsers().values().stream()
            .filter(ignoredUser -> ignoredUser.getUserInfo().getName().equalsIgnoreCase(name))
            .findFirst().orElse(null);
    }

    @NotNull
    public BasicSettings getSettings() {
        return this.settings;
    }
}
