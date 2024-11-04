package su.nightexpress.sunlight.module.warps.impl;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.integration.VaultHook;
import su.nightexpress.nightcore.manager.AbstractFileData;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Pair;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.nightcore.util.placeholder.Placeholder;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;
import su.nightexpress.nightcore.util.text.tag.Tags;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.core.cooldown.CooldownInfo;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.module.warps.command.WarpShortcutCommand;
import su.nightexpress.sunlight.module.warps.config.WarpsConfig;
import su.nightexpress.sunlight.module.warps.config.WarpsLang;
import su.nightexpress.sunlight.module.warps.config.WarpsPerms;
import su.nightexpress.sunlight.module.warps.event.PlayerWarpTeleportEvent;
import su.nightexpress.sunlight.module.warps.type.WarpType;
import su.nightexpress.sunlight.module.warps.util.Placeholders;
import su.nightexpress.sunlight.utils.Teleporter;
import su.nightexpress.sunlight.utils.UserInfo;
import su.nightexpress.sunlight.utils.pos.BlockEyedPos;

import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Warp extends AbstractFileData<SunLightPlugin> implements Placeholder {

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;

    private final WarpsModule                     module;
    private final PlaceholderMap                  placeholderMap;
    private final Set<Pair<LocalTime, LocalTime>> visitTimes;

    private UserInfo     owner;
    private WarpType     type;
    private String       name;
    private String       description;
    private String       worldName;
    private BlockEyedPos blockPos;
    private ItemStack    icon;
    private boolean      permissionRequired;
    private String       commandShortcut;
    private int          visitCooldown;
    private double       visitCostMoney;

    public Warp(@NotNull SunLightPlugin plugin, @NotNull WarpsModule module, @NotNull File file) {
        super(plugin, file);
        this.module = module;
        this.visitTimes = new HashSet<>();

        this.placeholderMap = Placeholders.forWarp(this);
    }

    @Override
    protected boolean onLoad(@NotNull FileConfig config) {
        String locationStr = config.getString("Location");
        if (locationStr != null) {
            String[] split = locationStr.split(",");
            if (split.length != 6) return false;

            String world = split[5];
            BlockEyedPos pos = BlockEyedPos.deserialize(locationStr);
            config.remove("Location");
            config.set("World", world);
            pos.write(config, "BlockPos");
        }



        UUID ownerId;
        try {
            ownerId = UUID.fromString(String.valueOf(config.getString("Owner.Id")));
        }
        catch (IllegalArgumentException exception) {
            exception.printStackTrace();
            return false;
        }

        String ownerName = String.valueOf(config.getString("Owner.Name"));
        this.setOwner(new UserInfo(ownerId, ownerName));

        this.blockPos = BlockEyedPos.read(config, "BlockPos");
        this.worldName = config.getString("World");

        this.setIcon(config.getItem("Icon"));
        this.setType(config.getEnum("Type", WarpType.class, WarpType.SERVER));
        this.setName(config.getString("Name", this.getId()));
        this.setDescription(config.getString("Description"));
        this.setPermissionRequired(config.getBoolean("Permission_Required"));
        this.setVisitCooldown(config.getInt("Visit.Cooldown"));
        this.getVisitTimes().addAll(config.getStringList("Visit.Times").stream().map(raw -> {
            String[] split = raw.split("-");
            if (split.length < 2) return null;

            try {
                LocalTime start = LocalTime.parse(split[0], TIME_FORMATTER);
                LocalTime end = LocalTime.parse(split[1], TIME_FORMATTER);
                return Pair.of(start, end);
            }
            catch (DateTimeParseException e) {
                return null;
            }
        }).filter(Objects::nonNull).toList());
        this.setVisitCostMoney(config.getDouble("Visit.Cost.Money"));
        this.setCommandShortcut(config.getString("Command_Shortcut"));
        return true;
    }

    @Override
    protected void onSave(@NotNull FileConfig config) {
        config.set("Owner.Id", this.getOwner().getId().toString());
        config.set("Owner.Name", this.getOwner().getName());
        config.set("World", this.worldName);
        this.blockPos.write(config, "BlockPos");
        config.set("Type", this.getType().name());
        config.set("Name", this.getName());
        config.set("Description", this.getDescription());
        config.set("Permission_Required", this.isPermissionRequired());
        config.set("Visit.Cooldown", this.getVisitCooldown());
        config.set("Visit.Times", this.getVisitTimes().stream()
            .map(pair -> pair.getFirst().format(TIME_FORMATTER) + "-" + pair.getSecond().format(TIME_FORMATTER))
            .toList());
        config.set("Visit.Cost.Money", this.getVisitCostMoney());
        config.setItem("Icon", this.getIcon());
        config.set("Command_Shortcut", this.getCommandShortcut());
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    public boolean teleport(@NotNull Player player, boolean isForced) {
        if (!this.isValid()) {
            WarpsLang.WARP_TELEPORT_ERROR_DISABLED.getMessage().replace(this.replacePlaceholders()).send(player);
            return false;
        }

        if (!isForced && !this.hasPermission(player)) {
            WarpsLang.WARP_TELEPORT_ERROR_NO_PERMISSION.getMessage().replace(this.replacePlaceholders()).send(player);
            return false;
        }

        if (!isForced && !this.isVisitTime(player)) {
            WarpsLang.WARP_TELEPORT_ERROR_TIME.getMessage().replace(this.replacePlaceholders()).send(player);
            return false;
        }

        // Check cooldown.
        SunUser user = plugin.getUserManager().getUserData(player);
        CooldownInfo cooldownInfo = user.getCooldown(this).orElse(null);
        if (!isForced && cooldownInfo != null) {
            long expireDate = cooldownInfo.getExpireDate();
            WarpsLang.WARP_TELEPORT_ERROR_COOLDOWN.getMessage()
                .replace(Placeholders.GENERIC_COOLDOWN, TimeUtil.formatDuration(expireDate))
                .replace(this.replacePlaceholders())
                .send(player);
            return false;
        }

        // Check teleportation costs.
        if (this.hasVisitCost()) {
            if (!this.canAffordVisit(player)) {
                WarpsLang.WARP_TELEPORT_ERROR_NOT_ENOUGH_FUNDS.getMessage().replace(this.replacePlaceholders()).send(player);
                return false;
            }
            if (!player.hasPermission(WarpsPerms.BYPASS_VISIT_COST) && !this.isOwner(player)) {
                double visitCost = this.getVisitCostMoney();
                VaultHook.takeMoney(player, visitCost);
            }
        }

        PlayerWarpTeleportEvent event = new PlayerWarpTeleportEvent(player, this);
        this.plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        Teleporter teleporter = new Teleporter(player, this.getLocation()).centered().validateFloor();
        if (!teleporter.teleport()) return false;

        WarpsLang.WARP_TELEPORT_DONE.getMessage().replace(this.replacePlaceholders()).send(player);

        if (!isForced && this.hasVisitCooldown() && !this.isOwner(player)) {
            CooldownInfo info = CooldownInfo.of(this);
            user.addCooldown(info);
            this.plugin.getUserManager().scheduleSave(user);
        }

        return true;
    }

    public boolean isValid() {
        return this.getWorld() != null;
    }

    public World getWorld() {
        return this.worldName == null ? null : this.plugin.getServer().getWorld(this.worldName);
    }

    public Location getLocation() {
        World world = this.getWorld();
        if (world == null) return null;

        return this.blockPos.toLocation(world);
    }

    public void setLocation(@NotNull Location location) {
        World locWorld = location.getWorld();
        if (locWorld == null) return;

        this.worldName = locWorld.getName();
        this.blockPos = BlockEyedPos.from(location);
    }

    public boolean isOwner(@NotNull Player player) {
        return this.getOwner().isUser(player);
    }

    public boolean canEdit(@NotNull Player player) {
        return this.isOwner(player) || player.hasPermission(WarpsPerms.EDITOR_OTHERS);
    }

    public boolean isOnCooldown(@NotNull Player player) {
        SunUser user = plugin.getUserManager().getUserData(player);
        return user.getCooldown(this).isPresent();
    }

    public boolean isAvailable(@NotNull Player player) {
        if (!this.hasPermission(player)) return false;
        if (!this.isVisitTime(player)) return false;
        if (this.isOnCooldown(player)) return false;

        return this.canAffordVisit(player);
    }

    public boolean isVisitTime() {
        if (!this.hasVisitTimes()) return true;

        LocalTime now = LocalTime.now();
        return this.getVisitTimes().stream().anyMatch(pair -> {
            return now.isAfter(pair.getFirst()) && now.isBefore(pair.getSecond());
        });
    }

    public boolean isVisitTime(@NotNull Player player) {
        if (this.isOwner(player)) return true;
        if (player.hasPermission(WarpsPerms.BYPASS_VISIT_TIME)) return true;
        return this.isVisitTime();
    }

    public boolean hasVisitTimes() {
        return !this.getVisitTimes().isEmpty();
    }

    @Nullable
    public LocalTime getNearestVisitTime() {
        LocalTime now = LocalTime.now();
        return this.getVisitTimes().stream()
            .filter(pair -> now.isBefore(pair.getFirst()) || now.isAfter(pair.getSecond()))
            .map(Pair::getFirst).min(LocalTime::compareTo).orElse(null);
    }

    @Nullable
    public LocalTime getNearestCloseTime() {
        LocalTime now = LocalTime.now();
        return this.getVisitTimes().stream()
            .filter(pair -> now.isAfter(pair.getFirst()) && now.isBefore(pair.getSecond()))
            .map(Pair::getSecond).findFirst().orElse(null);
    }

    @NotNull
    public String getPermission() {
        return WarpsPerms.PREFIX_WARP + this.getId();
    }

    public boolean hasPermission(@NotNull Player player) {
        return !this.isPermissionRequired() || this.isOwner(player) || player.hasPermission(this.getPermission());
    }

    public boolean hasVisitCooldown() {
        return this.getVisitCooldown() > 0L;
    }

    public boolean isVisitOneTimed() {
        return this.getVisitCooldown() < 0L;
    }

    public boolean hasVisitCost() {
        return this.visitCostMoney > 0D && Plugins.hasVault() && VaultHook.hasEconomy();
    }

    public boolean canAffordVisit(@NotNull Player player) {
        if (!this.hasVisitCost()) return true;
        if (this.isOwner(player)) return true;
        if (player.hasPermission(WarpsPerms.BYPASS_VISIT_COST)) return true;

        return VaultHook.getBalance(player) >= this.getVisitCostMoney();
    }

    @NotNull
    public WarpsModule getModule() {
        return this.module;
    }

    @NotNull
    public UserInfo getOwner() {
        return owner;
    }

    public void setOwner(@NotNull UserInfo owner) {
        this.owner = owner;
    }

    @NotNull
    public WarpType getType() {
        return type;
    }

    public void setType(@NotNull WarpType type) {
        this.type = type;
    }

    @NotNull
    public String getWorldName() {
        return worldName;
    }

    @NotNull
    public BlockEyedPos getBlockPos() {
        return blockPos;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @Nullable
    public String getDescription() {
        return this.description;
    }

    @NotNull
    public List<String> getDescriptionFormatted() {
        if (this.description == null) return Collections.emptyList();

        List<String> description = new ArrayList<>();
        for (String line : WarpsConfig.WARP_DESCRIPTION_FORMAT.get()) {
            if (line.contains(Placeholders.GENERIC_ENTRY)) {
                for (String desc : Tags.LINE_BREAK.split(this.description)) {
                    description.add(line.replace(Placeholders.GENERIC_ENTRY, desc));
                }
            }
            else description.add(line);
        }

        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    public boolean isPermissionRequired() {
        return this.permissionRequired;
    }

    public void setPermissionRequired(boolean isPermission) {
        this.permissionRequired = isPermission;
    }

    @NotNull
    public Set<Pair<LocalTime, LocalTime>> getVisitTimes() {
        return visitTimes;
    }

    public int getVisitCooldown() {
        return visitCooldown;
    }

    public void setVisitCooldown(int visitCooldown) {
        this.visitCooldown = visitCooldown;
    }

    public double getVisitCostMoney() {
        return this.visitCostMoney;
    }

    public void setVisitCostMoney(double visitCostMoney) {
        this.visitCostMoney = visitCostMoney;
    }

    @NotNull
    public ItemStack getIcon() {
        return new ItemStack(this.icon);
    }

    public void setIcon(@NotNull ItemStack icon) {
        this.icon = new ItemStack(icon);
        ItemUtil.editMeta(this.icon, meta -> {
            meta.setDisplayName(null);
            meta.setLore(null);
            meta.addItemFlags(ItemFlag.values());
        });
    }

    @Nullable
    public String getCommandShortcut() {
        return this.commandShortcut;
    }

    public void setCommandShortcut(@Nullable String commandShortcut) {
        WarpShortcutCommand.unregister(this.plugin, this);
        this.commandShortcut = commandShortcut;
        WarpShortcutCommand.register(this.plugin, this);
    }
}