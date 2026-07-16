package su.nightexpress.sunlight.module.homes.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import su.nightexpress.nightcore.user.UserInfo;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.geodata.pos.ExactPos;
import su.nightexpress.nightcore.util.placeholder.PlaceholderResolvable;
import su.nightexpress.nightcore.util.placeholder.PlaceholderResolver;
import su.nightexpress.sunlight.module.homes.HomePlaceholders;
import su.nightexpress.sunlight.module.homes.config.HomesPerms;

@NullMarked
public class Home implements PlaceholderResolvable {

    private final String   id;
    private final UserInfo owner;

    private String        name;
    private String        iconId;
    private ExactPos      blockPos;
    private String        worldName;
    private HomeType      type;
    private Set<UserInfo> invitedPlayers;
    private boolean       favorite;

    private @Nullable World world;
    private boolean         dirty;

    public Home(String id,
                UserInfo owner,
                String name,
                String iconId,
                ExactPos blockPos,
                String worldName,
                HomeType type,
                Set<UserInfo> invitedPlayers,
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


    public static Home createDefault(String id, UserInfo owner, String iconId, World world, ExactPos blockPos) {
        String name = StringUtil.capitalizeUnderscored(id);
        HomeType type = HomeType.PRIVATE;
        Set<UserInfo> invitedPlayers = new HashSet<>();

        return new Home(id, owner, name, iconId, blockPos, world.getName(), type, invitedPlayers, false);
    }

    @Override

    public PlaceholderResolver placeholders() {
        return HomePlaceholders.HOME.resolver(this);
    }

    public boolean isActive() {
        return this.world != null;
    }

    public boolean isInactive() {
        return !this.isActive();
    }

    public boolean isWorld(World world) {
        return this.worldName.equalsIgnoreCase(world.getName());
    }

    public void activate() {
        World world = Bukkit.getWorld(this.worldName);
        if (world != null) {
            this.activate(world);
        }
    }

    public void activate(World world) {
        this.world = world;
    }

    public void deactivate() {
        this.world = null;
    }


    public World getWorld() {
        if (this.world == null) throw new IllegalStateException("Home is not active!");

        return this.world;
    }


    public Location toLocation() {
        if (this.world == null) throw new IllegalStateException("Home is not active!");

        return this.blockPos.toLocation(this.world);
    }

    public void updateLocation(Location location) {
        this.setWorldName(location.getWorld().getName());
        this.setBlockPos(ExactPos.from(location));
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

    public boolean isOwner(Player player) {
        return this.owner.isUser(player);
    }

    public boolean isPublic() {
        return this.type == HomeType.PUBLIC;
    }

    public boolean isPrivate() {
        return this.type == HomeType.PRIVATE;
    }

    public boolean canAccess(Player player) {
        return this.isOwner(player) || this.isPublic() || this.isInvited(player);
    }

    public void addInvitedPlayer(UserInfo userInfo) {
        if (userInfo.equals(this.getOwner())) return;

        this.invitedPlayers.add(userInfo);
    }

    public boolean canVisit(Player player) {
        return player.hasPermission(HomesPerms.COMMAND_HOMES_VISIT_ALL) || this.isPublic() || this.isInvited(
            player) || this.isOwner(player);
    }

    public boolean isInvited(Player player) {
        return this.invitedPlayers.stream().anyMatch(userInfo -> userInfo.isUser(player));
    }

    public boolean isInvited(String name) {
        return this.invitedPlayers.stream().anyMatch(userInfo -> userInfo.isUser(name));
    }

    public boolean isInvited(UUID playerId) {
        return this.invitedPlayers.stream().anyMatch(userInfo -> userInfo.id().equals(playerId));
    }


    public String getId() {
        return this.id;
    }


    public UserInfo getOwner() {
        return this.owner;
    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getIconId() {
        return this.iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = LowerCase.INTERNAL.apply(iconId);
    }


    public String getWorldName() {
        return this.worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }


    public ExactPos getBlockPos() {
        return this.blockPos;
    }

    public void setBlockPos(ExactPos blockPos) {
        this.blockPos = blockPos;
    }


    public HomeType getType() {
        return this.type;
    }

    public void setType(HomeType type) {
        this.type = type;
    }


    public Set<UserInfo> getInvitedPlayers() {
        return this.invitedPlayers;
    }

    public void setInvitedPlayers(Set<UserInfo> invitedPlayers) {
        this.invitedPlayers = invitedPlayers;
    }

    public boolean isFavorite() {
        return this.favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
