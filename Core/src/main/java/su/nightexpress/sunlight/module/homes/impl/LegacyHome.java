package su.nightexpress.sunlight.module.homes.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.Placeholders;

import java.util.Set;

public class LegacyHome {

    private final String id;
    private     String   owner;
    private           String   name;
    private Material    iconMaterial;
    private Location    location;
    private Set<String> invitedPlayers;
    private boolean     isRespawnPoint;

    public LegacyHome(
        @NotNull String id,
        @NotNull String owner,
        @NotNull String name,
        @NotNull Material iconMaterial,
        @NotNull Location location,
        @NotNull Set<String> invitedPlayers,
        boolean isRespawnPoint) {
        this.id = id.toLowerCase();
        this.setOwner(owner);
        this.setName(name);
        this.setIconMaterial(iconMaterial);
        this.setLocation(location);
        this.setInvitedPlayers(invitedPlayers);
        this.setRespawnPoint(isRespawnPoint);
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @NotNull
    public String getOwner() {
        return owner;
    }

    public void setOwner(@NotNull String owner) {
        this.owner = owner;
    }

    public boolean isOwner(@NotNull Player player) {
        return this.getOwner().equalsIgnoreCase(player.getName());
    }

    @NotNull
    public Material getIconMaterial() {
        return iconMaterial;
    }

    public void setIconMaterial(@NotNull Material iconMaterial) {
        if (iconMaterial.isAir()) iconMaterial = Material.GRASS_BLOCK;
        this.iconMaterial = iconMaterial;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = Colorizer.apply(name);
    }

    @NotNull
    public Location getLocation() {
        return this.location;
    }

    public void setLocation(@NotNull Location location) {
        this.location = location;
    }

    public boolean isPublic() {
        return this.getInvitedPlayers().contains(Placeholders.WILDCARD);
    }

    @NotNull
    public Set<String> getInvitedPlayers() {
        return this.invitedPlayers;
    }

    public void setInvitedPlayers(@NotNull Set<String> invitedPlayers) {
        this.invitedPlayers = invitedPlayers;
    }

    public boolean isRespawnPoint() {
        return this.isRespawnPoint;
    }

    public void setRespawnPoint(boolean isRespawnPoint) {
        this.isRespawnPoint = isRespawnPoint;
    }
}