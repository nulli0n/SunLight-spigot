package su.nightexpress.sunlight.module.homes.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.NumberUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.SunLightAPI;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.config.HomesLang;
import su.nightexpress.sunlight.module.homes.event.PlayerHomeTeleportEvent;
import su.nightexpress.sunlight.module.homes.menu.HomeMenu;
import su.nightexpress.sunlight.module.homes.util.Placeholders;
import su.nightexpress.sunlight.utils.UserInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Home implements ICleanable, Placeholder {

    private static final ItemStack DEFAULT_ICON = new ItemStack(Material.PLAYER_HEAD);

    static {
        ItemUtil.setSkullTexture(DEFAULT_ICON, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThkYWExZTNlZDk0ZmYzZTMzZTFkNGM2ZTQzZjAyNGM0N2Q3OGE1N2JhNGQzOGU3NWU3YzkyNjQxMDYifX19");
    }

    private final SunLight plugin;
    private final String id;
    private final UserInfo owner;
    private final PlaceholderMap placeholderMap;

    private String    name;
    private ItemStack icon;
    private Location  location;
    private HomeType  type;
    private Set<UserInfo> invitedPlayers;
    private boolean isDefault;
    private boolean   isRespawnPoint;

    private HomeMenu editor;

    public Home(@NotNull LegacyHome legacyHome, @NotNull UUID owner, @NotNull String ownerName) {
        this(SunLightAPI.PLUGIN, legacyHome.getId(), new UserInfo(owner, ownerName),
            legacyHome.getName(), new ItemStack(legacyHome.getIconMaterial()), legacyHome.getLocation(),
            legacyHome.isPublic() ? HomeType.PUBLIC : HomeType.PRIVATE,  new HashSet<>(), false, legacyHome.isRespawnPoint()
        );
    }

    public Home(@NotNull SunLight plugin, @NotNull String id, @NotNull UserInfo owner, @NotNull Location location) {
        this(plugin, id, owner,
            StringUtil.capitalizeUnderscored(id), DEFAULT_ICON, location,
            HomeType.PRIVATE, new HashSet<>(), false, false
        );
    }

    public Home(
        @NotNull SunLight plugin,
        @NotNull String id,
        @NotNull UserInfo owner,
        @NotNull String name,
        @NotNull ItemStack icon,
        @NotNull Location location,
        @NotNull HomeType type,
        @NotNull Set<UserInfo> invitedPlayers,
        boolean isDefault,
        boolean isRespawnPoint) {
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

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.HOME_ID, this::getId)
            .add(Placeholders.HOME_NAME, this::getName)
            .add(Placeholders.HOME_OWNER, () -> this.getOwner().getName())
            .add(Placeholders.HOME_TYPE, () -> plugin.getLangManager().getEnum(this.getType()))
            .add(Placeholders.HOME_INVITED_PLAYERS, () -> String.join(",", this.getInvitedPlayers().stream().map(UserInfo::getName).toList()))
            .add(Placeholders.HOME_IS_DEFAULT, () -> LangManager.getBoolean(this.isDefault()))
            .add(Placeholders.HOME_IS_RESPAWN_POINT, () -> LangManager.getBoolean(this.isRespawnPoint()))
            .add(Placeholders.HOME_ICON_MATERIAL, () -> ItemUtil.getItemName(this.getIcon()))
            .add(Placeholders.HOME_LOCATION_X, () -> NumberUtil.format(this.getLocation().getX()))
            .add(Placeholders.HOME_LOCATION_Y, () -> NumberUtil.format(this.getLocation().getY()))
            .add(Placeholders.HOME_LOCATION_Z, () -> NumberUtil.format(this.getLocation().getZ()))
            .add(Placeholders.HOME_LOCATION_WORLD, () -> {
                return getLocation().getWorld() == null ? "null" : LangManager.getWorld(getLocation().getWorld());
            });
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    public HomeMenu getEditor() {
        if (this.editor == null) {
            HomesModule homeManager = plugin.getModuleManager().getModule(HomesModule.class).orElse(null);
            if (homeManager == null) throw new IllegalStateException("The module is disabled!");

            this.editor = new HomeMenu(homeManager, this);
        }
        return editor;
    }

    @Override
    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        this.placeholderMap.clear();
    }

    public void save() {
        this.plugin.runTaskAsync(task -> this.plugin.getData().saveHome(this));
    }

    public boolean isOwner(@NotNull Player player) {
        return this.getOwner().getId().equals(player.getUniqueId());
    }

    public boolean canAccess(@NotNull Player player) {
        return this.isOwner(player) || this.isPublic() || this.isInvitedPlayer(player);
    }

    public boolean teleport(@NotNull Player player) {
        PlayerHomeTeleportEvent event = new PlayerHomeTeleportEvent(player, this);
        plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        if (!player.teleport(this.getLocation())) {
            return false;
        }

        this.plugin.getMessage(this.isOwner(player) ? HomesLang.HOME_TELEPORT_SUCCESS : HomesLang.HOME_VISIT_SUCCESS)
            .replace(this.replacePlaceholders())
            .send(player);
        return true;
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
        this.name = Colorizer.legacyHex(name);
    }

    @NotNull
    public ItemStack getIcon() {
        return new ItemStack(icon);
    }

    public void setIcon(@NotNull ItemStack icon) {
        if (icon.getType().isAir()) {
            icon = new ItemStack(Material.GRASS_BLOCK);
        }
        this.icon = new ItemStack(icon);
        this.icon.setAmount(1);

        ItemUtil.mapMeta(this.icon, meta -> {
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

    public boolean isPublic() {
        return this.getType() == HomeType.PUBLIC;
    }

    public boolean isPrivate() {
        return this.getType() == HomeType.PRIVATE;
    }

    @NotNull
    public Set<UserInfo> getInvitedPlayers() {
        return this.invitedPlayers;
    }

    public void setInvitedPlayers(@NotNull Set<UserInfo> invitedPlayers) {
        this.invitedPlayers = invitedPlayers;
    }

    public void addInvitedPlayer(@NotNull UserInfo userInfo) {
        if (userInfo.equals(this.getOwner())) return;

        this.getInvitedPlayers().add(userInfo);
    }

    public boolean isInvitedPlayer(@NotNull Player player) {
        return this.getInvitedPlayers().stream().anyMatch(userInfo -> userInfo.isUser(player));
    }

    public boolean isInvitedPlayer(@NotNull String name) {
        return this.getInvitedPlayers().stream().anyMatch(userInfo -> userInfo.isUser(name));
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
