package su.nightexpress.sunlight.module.homes.impl;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.placeholder.Placeholder;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.homes.config.HomesConfig;
import su.nightexpress.sunlight.module.homes.config.HomesLang;
import su.nightexpress.sunlight.module.homes.config.HomesPerms;
import su.nightexpress.sunlight.module.homes.event.PlayerHomeTeleportEvent;
import su.nightexpress.sunlight.module.homes.util.Placeholders;
import su.nightexpress.sunlight.utils.SunUtils;
import su.nightexpress.sunlight.utils.Teleporter;
import su.nightexpress.sunlight.utils.UserInfo;

import java.util.HashSet;
import java.util.Set;

public class Home implements Placeholder {

    private final SunLightPlugin plugin;
    private final String         id;
    private final UserInfo       owner;
    private final PlaceholderMap placeholderMap;

    private String        name;
    private ItemStack     icon;
    private Location      location;
    private HomeType      type;
    private Set<UserInfo> invitedPlayers;
    private boolean       isDefault;
    private boolean       isRespawnPoint;

    public Home(@NotNull SunLightPlugin plugin, @NotNull String id, @NotNull UserInfo owner, @NotNull Location location) {
        this(plugin, id, owner,
            StringUtil.capitalizeUnderscored(id),
            HomesConfig.getDefaultIcon(),
            location,
            HomeType.PRIVATE,
            new HashSet<>(),
            false,
            false
        );
    }

    public Home(
        @NotNull SunLightPlugin plugin,
        @NotNull String id,
        @NotNull UserInfo owner,
        @NotNull String name,
        @NotNull ItemStack icon,
        @NotNull Location location,
        @NotNull HomeType type,
        @NotNull Set<UserInfo> invitedPlayers,
        boolean isDefault,
        boolean isRespawnPoint
    ) {
        this.plugin = plugin;
        this.id = id.toLowerCase();
        this.owner = owner;
        this.setName(name);
        this.setIcon(icon);
        this.setLocation(location);
        this.setType(type);
        this.setInvitedPlayers(invitedPlayers);
        this.setDefault(isDefault);
        this.setRespawnPoint(isRespawnPoint);

        this.placeholderMap = Placeholders.forHome(this);
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    public boolean teleport(@NotNull Player player) {
        if (!player.hasPermission(HomesPerms.BYPASS_UNSAFE)) {
            if (!this.isOwner(player) && !SunUtils.isSafeLocation(this.getLocation())) {
                HomesLang.HOME_VISIT_ERROR_UNSAFE.getMessage().send(player);
                return false;
            }
        }

        PlayerHomeTeleportEvent event = new PlayerHomeTeleportEvent(player, this);
        plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        Teleporter teleporter = new Teleporter(player, this.getLocation()).centered().validateFloor();
        if (!teleporter.teleport()) {
            return false;
        }

        (this.isOwner(player) ? HomesLang.HOME_TELEPORT_SUCCESS : HomesLang.HOME_VISIT_SUCCESS).getMessage()
            .replace(this.replacePlaceholders())
            .send(player);
        return true;
    }

    public boolean isOwner(@NotNull Player player) {
        return this.getOwner().getId().equals(player.getUniqueId());
    }

    public boolean isPublic() {
        return this.getType() == HomeType.PUBLIC;
    }

    public boolean isPrivate() {
        return this.getType() == HomeType.PRIVATE;
    }

    public boolean canAccess(@NotNull Player player) {
        return this.isOwner(player) || this.isPublic() || this.isInvitedPlayer(player);
    }

    public void addInvitedPlayer(@NotNull UserInfo userInfo) {
        if (userInfo.equals(this.getOwner())) return;

        this.invitedPlayers.add(userInfo);
    }

    public boolean canVisit(@NotNull Player player) {
        return player.hasPermission(HomesPerms.COMMAND_HOMES_VISIT_ALL) || this.isPublic() || this.isInvitedPlayer(player) || this.isOwner(player);
    }

    public boolean isInvitedPlayer(@NotNull Player player) {
        return this.getInvitedPlayers().stream().anyMatch(userInfo -> userInfo.isUser(player));
    }

    public boolean isInvitedPlayer(@NotNull String name) {
        return this.getInvitedPlayers().stream().anyMatch(userInfo -> userInfo.isUser(name));
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @NotNull
    public UserInfo getOwner() {
        return owner;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public ItemStack getIcon() {
        return new ItemStack(icon);
    }

    public void setIcon(@NotNull ItemStack icon) {
        if (icon.getType().isAir()) {
            icon = HomesConfig.getDefaultIcon();
        }
        this.icon = new ItemStack(icon);
        this.icon.setAmount(1);

        ItemUtil.editMeta(this.icon, meta -> {
            meta.setDisplayName(null);
            meta.setLore(null);
            meta.addItemFlags(ItemFlag.values());
        });
    }

    @NotNull
    public Location getLocation() {
        return this.location;
    }

    public void setLocation(@NotNull Location location) {
        this.location = location;
    }

    @NotNull
    public HomeType getType() {
        return type;
    }

    public void setType(@NotNull HomeType type) {
        this.type = type;
    }

    @NotNull
    public Set<UserInfo> getInvitedPlayers() {
        return this.invitedPlayers;
    }

    public void setInvitedPlayers(@NotNull Set<UserInfo> invitedPlayers) {
        this.invitedPlayers = invitedPlayers;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean isRespawnPoint() {
        return this.isRespawnPoint;
    }

    public void setRespawnPoint(boolean isRespawnPoint) {
        this.isRespawnPoint = isRespawnPoint;
    }
}
