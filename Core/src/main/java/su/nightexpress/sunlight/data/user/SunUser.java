package su.nightexpress.sunlight.data.user;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.database.AbstractUser;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.cooldown.CommandCooldown;
import su.nightexpress.sunlight.core.cooldown.CooldownInfo;
import su.nightexpress.sunlight.core.cooldown.CooldownType;
import su.nightexpress.sunlight.core.user.IgnoredUser;
import su.nightexpress.sunlight.core.user.settings.SettingRegistry;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.kits.Kit;
import su.nightexpress.sunlight.module.warps.impl.Warp;
import su.nightexpress.sunlight.utils.UserInfo;

import java.util.*;

public class SunUser extends AbstractUser<SunLightPlugin> {

    @Deprecated // TODO Move in UserCooldowns class
    private final Map<CooldownType, Set<CooldownInfo>> cooldowns;
    private final Map<UUID, IgnoredUser> ignoredUsers;
    private final UserSettings           settings;

    private String inetAddress;
    private boolean newlyCreated;

    @NotNull
    public static SunUser create(@NotNull SunLightPlugin plugin, @NotNull UUID uuid, @NotNull String name) {
        long dateCreated = System.currentTimeMillis();
        long lastOnline = System.currentTimeMillis();
        String inetAddress = "";
        Map<CooldownType, Set<CooldownInfo>> cooldowns = new HashMap<>();
        Map<UUID, IgnoredUser> ignoredUsers = new HashMap<>();
        UserSettings settings = new UserSettings();

        SunUser user = new SunUser(plugin, uuid, name, dateCreated, lastOnline, inetAddress, cooldowns, ignoredUsers, settings);
        user.newlyCreated = true;

        return user;
    }

    public SunUser(@NotNull SunLightPlugin plugin,
                   @NotNull UUID uuid,
                   @NotNull String name,
                   long dateCreated,
                   long lastOnline,
                   @NotNull String inetAddress,
                   @NotNull Map<CooldownType, Set<CooldownInfo>> cooldowns,
                   @NotNull Map<UUID, IgnoredUser> ignoredUsers,
                   @NotNull UserSettings settings
    ) {
        super(plugin, uuid, name, dateCreated, lastOnline);

        this.setInetAddress(inetAddress);
        this.cooldowns = new HashMap<>(cooldowns);
        this.ignoredUsers = new HashMap<>(ignoredUsers);
        this.settings = settings;
    }

    @Override
    public void onUnload() {
        super.onUnload();

        ChatModule chatModule = this.plugin.getModuleManager().getModule(ChatModule.class).orElse(null);
        if (chatModule != null) {
            chatModule.clearChatData(this.getId());
        }
    }

    public void setNewlyCreated(boolean newlyCreated) {
        this.newlyCreated = newlyCreated;
    }

    public boolean isNewlyCreated() {
        return newlyCreated;
    }

    public boolean hasPlayedBefore() {
        return !this.newlyCreated;
    }

    public boolean hasCustomName() {
        return !this.getCustomName().isEmpty();
    }

    @NotNull
    public String getCustomName() {
        return this.settings.get(SettingRegistry.CUSTOM_NAME);
    }

    public void setCustomName(@Nullable String name) {
        String customName = "";
        if (name != null && !name.isBlank()) {
            customName = name;
        }
        this.settings.set(SettingRegistry.CUSTOM_NAME, customName);
    }

    public void updatePlayerName() {
        Player player = this.getPlayer();
        if (player == null) return;

        if (this.hasCustomName()) {
            String customName = this.getCustomName();
            String noTags = NightMessage.stripTags(customName);

            if (!noTags.equalsIgnoreCase(customName)) {
                customName = NightMessage.asLegacy(customName);
            }

            player.setDisplayName(customName);
        }
        else player.setDisplayName(null);
    }

    @NotNull
    public String getInetAddress() {
        return this.inetAddress;
    }

    public void setInetAddress(@NotNull String ipAddress) {
        this.inetAddress = ipAddress;
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

    // TODO Dedicated class
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
    public UserSettings getSettings() {
        return this.settings;
    }
}
