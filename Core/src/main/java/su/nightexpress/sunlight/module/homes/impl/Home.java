package su.nightexpress.sunlight.module.homes.impl;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.user.UserInfo;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.geodata.pos.BlockPos;
import su.nightexpress.nightcore.util.placeholder.PlaceholderResolvable;
import su.nightexpress.nightcore.util.placeholder.PlaceholderResolver;
import su.nightexpress.sunlight.module.homes.HomePlaceholders;
import su.nightexpress.sunlight.module.homes.config.HomesPerms;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Home implements PlaceholderResolvable {

    private final String   id;
    private final UserInfo owner;

    private String        name;
    private String        iconId;
    private BlockPos      blockPos;
    private String        worldName;
    private HomeType      type;
    private Set<UserInfo> invitedPlayers;
    private boolean       favorite;

    private World world;
    private boolean dirty;

    public Home(@NotNull String id,
                @NotNull UserInfo owner,
                @NotNull String name,
                @NotNull String iconId,
                @NotNull BlockPos blockPos,
                @NotNull String worldName,
                @NotNull HomeType type,
                @NotNull Set<UserInfo> invitedPlayers,
                boolean favorite
    ) {
        this.id = id.toLowerCase();
        this.owner = owner;
        this.setName(name);
        this.setIconId(iconId);
        this.setBlockPos(blockPos);
        this.setWorldName(worldName);
        this.setType(type);
        this.setInvitedPlayers(invitedPlayers);
        this.setFavorite(favorite);
    }

    @NotNull
    public static Home createDefault(@NotNull String id, @NotNull UserInfo owner, @NotNull String iconId, @NotNull World world, @NotNull BlockPos blockPos) {
        String name = StringUtil.capitalizeUnderscored(id);
        HomeType type = HomeType.PRIVATE;
        Set<UserInfo> invitedPlayers = new HashSet<>();

        return new Home(id, owner, name, iconId, blockPos, world.getName(), type, invitedPlayers, false);
    }

    @Override
    @NotNull
    public PlaceholderResolver placeholders() {
        return HomePlaceholders.HOME.resolver(this);
    }

    public boolean isActive() {
        return this.world != null;
    }

    public boolean isInactive() {
        return !this.isActive();
    }

    public boolean isWorld(@NotNull World world) {
        return this.worldName.equalsIgnoreCase(world.getName());
    }

    public void activate() {
        World world = Bukkit.getWorld(this.worldName);
        if (world != null) {
            this.activate(world);
        }
    }

    public void activate(@NotNull World world) {
        this.world = world;
    }

    public void deactivate() {
        this.world = null;
    }

    @NotNull
    public World getWorld() {
        if (this.world == null) throw new IllegalStateException("Home is not active!");

        return this.world;
    }

    @NotNull
    public Location toLocation() {
        if (this.world == null) throw new IllegalStateException("Home is not active!");

        return this.blockPos.toLocation(this.world);
    }

    public void updateLocation(@NotNull Location location) {
        this.setWorldName(location.getWorld().getName());
        this.setBlockPos(BlockPos.from(location));
    }

    public void markDirty() {
        this.dirty = true;
    }

    public void markClean() {
        this.dirty = false;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public boolean isOwner(@NotNull Player player) {
        return this.owner.isUser(player);
    }

    public boolean isPublic() {
        return this.type == HomeType.PUBLIC;
    }

    public boolean isPrivate() {
        return this.type == HomeType.PRIVATE;
    }

    public boolean canAccess(@NotNull Player player) {
        return this.isOwner(player) || this.isPublic() || this.isInvited(player);
    }

    public void addInvitedPlayer(@NotNull UserInfo userInfo) {
        if (userInfo.equals(this.getOwner())) return;

        this.invitedPlayers.add(userInfo);
    }

    public boolean canVisit(@NotNull Player player) {
        return player.hasPermission(HomesPerms.COMMAND_HOMES_VISIT_ALL) || this.isPublic() || this.isInvited(player) || this.isOwner(player);
    }

    public boolean isInvited(@NotNull Player player) {
        return this.invitedPlayers.stream().anyMatch(userInfo -> userInfo.isUser(player));
    }

    public boolean isInvited(@NotNull String name) {
        return this.invitedPlayers.stream().anyMatch(userInfo -> userInfo.isUser(name));
    }

    public boolean isInvited(@NotNull UUID playerId) {
        return this.invitedPlayers.stream().anyMatch(userInfo -> userInfo.id().equals(playerId));
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @NotNull
    public UserInfo getOwner() {
        return this.owner;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public String getIconId() {
        return this.iconId;
    }

    public void setIconId(@NotNull String iconId) {
        this.iconId = LowerCase.INTERNAL.apply(iconId);
    }

    @NotNull
    public String getWorldName() {
        return this.worldName;
    }

    public void setWorldName(@NotNull String worldName) {
        this.worldName = worldName;
    }

    @NotNull
    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    public void setBlockPos(@NotNull BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    @NotNull
    public HomeType getType() {
        return this.type;
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

    public boolean isFavorite() {
        return this.favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
