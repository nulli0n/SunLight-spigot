package su.nightexpress.sunlight.module.warps.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractConfigHolder;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.api.manager.IEditable;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.command.CommandRegister;
import su.nexmedia.engine.hooks.external.VaultHook;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.*;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.module.warps.command.WarpShortcutCommand;
import su.nightexpress.sunlight.module.warps.config.WarpsLang;
import su.nightexpress.sunlight.module.warps.menu.WarpSettingsMenu;
import su.nightexpress.sunlight.module.warps.type.WarpType;
import su.nightexpress.sunlight.module.warps.util.Placeholders;
import su.nightexpress.sunlight.module.warps.util.WarpsPerms;
import su.nightexpress.sunlight.utils.UserInfo;
import su.nightexpress.sunlight.data.impl.cooldown.CooldownInfo;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Warp extends AbstractConfigHolder<SunLight> implements ICleanable, IEditable, Placeholder {

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;

    private final WarpsModule  module;
    private final PlaceholderMap placeholderMap;
    private final Set<Pair<LocalTime, LocalTime>> visitTimes;

    private UserInfo     owner;
    private WarpType     type;
    private String       name;
    private String       description;
    private Location     location;
    private ItemStack    icon;
    private boolean      permissionRequired;
    private String       commandShortcut;
    private int          visitCooldown;
    private double       visitCostMoney;

    private WarpSettingsMenu editor;

    public Warp(@NotNull WarpsModule module, @NotNull JYML cfg) {
        super(module.plugin(), cfg);
        this.module = module;
        this.visitTimes = new HashSet<>();
        this.setIcon(new ItemStack(Material.AIR));

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.WARP_ID, this.getId())
            .add(Placeholders.WARP_NAME, this::getName)
            .add(Placeholders.WARP_DESCRIPTION, () -> this.getDescription().orElse(""))
            .add(Placeholders.WARP_TYPE, () -> plugin.getLangManager().getEnum(this.getType()))
            .add(Placeholders.WARP_PERMISSION_REQUIRED, () -> LangManager.getBoolean(this.isPermissionRequired()))
            .add(Placeholders.WARP_PERMISSION_NODE, this.getPermission())
            .add(Placeholders.WARP_VISIT_COST, () -> NumberUtil.format(this.getVisitCostMoney()))
            .add(Placeholders.WARP_VISIT_COOLDOWN, () -> TimeUtil.formatTime(this.getVisitCooldown() * 1000L))
            .add(Placeholders.WARP_VISIT_TIMES, () -> {
                return String.join("\n", this.getVisitTimes().stream().map(pair -> {
                    return pair.getFirst().format(TIME_FORMATTER) + "-" + pair.getSecond().format(TIME_FORMATTER);
                }).toList());
            })
            .add(Placeholders.WARP_LOCATION_X, () -> NumberUtil.format(this.getLocation().getX()))
            .add(Placeholders.WARP_LOCATION_Y, () -> NumberUtil.format(this.getLocation().getY()))
            .add(Placeholders.WARP_LOCATION_Z, () -> NumberUtil.format(this.getLocation().getZ()))
            .add(Placeholders.WARP_LOCATION_WORLD, () -> {
                return getLocation().getWorld() == null ? "null" : LangManager.getWorld(getLocation().getWorld());
            })
            .add(Placeholders.WARP_OWNER_NAME, () -> this.getOwner().getName())
            .add(Placeholders.WARP_COMMAND_SHORTCUT, () -> this.getCommandShortcut().orElse("-"))
            .add(Placeholders.WARP_ICON, () -> plugin.getLangManager().getEnum(this.getIcon().getType()))
        ;
    }

    @Override
    public boolean load() {
        UUID ownerId;
        try {
            ownerId = UUID.fromString(cfg.getString("Owner.Id", "null"));
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }

        String ownerName = cfg.getString("Owner.Name", "null");
        this.setOwner(new UserInfo(ownerId, ownerName));

        Location location = cfg.getLocation("Location");
        if (location == null) {
            this.module.error("Invalid warp location or world!");
            return false;
        }

        this.setLocation(location);
        this.setIcon(cfg.getItem("Icon"));
        this.setType(cfg.getEnum("Type", WarpType.class, WarpType.SERVER));
        this.setName(cfg.getString("Name", this.getId()));
        this.setDescription(cfg.getString("Description"));
        this.setPermissionRequired(cfg.getBoolean("Permission_Required"));
        this.setVisitCooldown(cfg.getInt("Visit.Cooldown"));
        this.getVisitTimes().addAll(this.cfg.getStringList("Visit.Times").stream().map(raw -> {
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
        this.setVisitCostMoney(cfg.getDouble("Visit.Cost.Money"));
        this.setCommandShortcut(cfg.getString("Command_Shortcut"));
        return true;
    }

    @Override
    public void onSave() {
        cfg.set("Owner.Id", this.getOwner().getId().toString());
        cfg.set("Owner.Name", this.getOwner().getName());
        cfg.set("Type", this.getType().name());
        cfg.set("Name", this.getName());
        cfg.set("Description", this.getDescription().orElse(null));
        cfg.set("Location", this.getLocation());
        cfg.set("Permission_Required", this.isPermissionRequired());
        cfg.set("Visit.Cooldown", this.getVisitCooldown());
        cfg.set("Visit.Times", this.getVisitTimes().stream()
            .map(pair -> pair.mapFirst(t -> t.format(TIME_FORMATTER)))
            .map(pair -> pair.mapSecond(t -> t.format(TIME_FORMATTER)))
            .map(pair -> pair.getFirst() + "-" + pair.getSecond()).toList());
        cfg.set("Visit.Cost.Money", this.getVisitCostMoney());
        cfg.setItem("Icon", this.getIcon());
        cfg.set("Command_Shortcut", this.getCommandShortcut().orElse(null));
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @Override
    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        this.setCommandShortcut(null);
    }

    @NotNull
    @Override
    public WarpSettingsMenu getEditor() {
        if (this.editor == null) {
            this.editor = new WarpSettingsMenu(this);
        }
        return this.editor;
    }

    public void teleport(@NotNull Player player, boolean isForced) {
        if (!isForced && !this.hasPermission(player)) {
            this.plugin.getMessage(WarpsLang.WARP_TELEPORT_ERROR_NO_PERMISSION).replace(this.replacePlaceholders()).send(player);
            return;
        }

        if (!isForced && !this.isVisitTime(player)) {
            this.plugin.getMessage(WarpsLang.WARP_TELEPORT_ERROR_TIME).replace(this.replacePlaceholders()).send(player);
            return;
        }

        // Check cooldown.
        SunUser user = plugin.getUserManager().getUserData(player);
        CooldownInfo cooldownInfo = user.getCooldown(this).orElse(null);
        if (!isForced && cooldownInfo != null) {
            long expireDate = cooldownInfo.getExpireDate();
            this.plugin.getMessage(WarpsLang.WARP_TELEPORT_ERROR_COOLDOWN)
                .replace(Placeholders.GENERIC_COOLDOWN, TimeUtil.formatTimeLeft(expireDate))
                .replace(this.replacePlaceholders())
                .send(player);
            return;
        }

        // Check teleportation costs.
        if (this.hasVisitCost()) {
            if (!this.canAffordVisit(player)) {
                this.plugin.getMessage(WarpsLang.WARP_TELEPORT_ERROR_NOT_ENOUGH_FUNDS).replace(this.replacePlaceholders()).send(player);
                return;
            }
            if (!player.hasPermission(WarpsPerms.BYPASS_VISIT_COST)) {
                double visitCost = this.getVisitCostMoney();
                VaultHook.takeMoney(player, visitCost);
            }
        }

        if (player.teleport(this.getLocation())) {
            this.plugin.getMessage(WarpsLang.WARP_TELEPORT_DONE).replace(this.replacePlaceholders()).send(player);

            if (!isForced && this.hasVisitCooldown()) {
                CooldownInfo info = CooldownInfo.of(this);
                user.addCooldown(info);
                user.saveData(this.plugin);
            }
        }
    }

    public boolean isOwner(@NotNull Player player) {
        return this.getOwner().isUser(player);
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
        return VaultHook.hasEconomy() && this.getVisitCostMoney() > 0D;
    }

    public boolean canAffordVisit(@NotNull Player player) {
        if (!this.hasVisitCost()) return true;
        if (player.hasPermission(WarpsPerms.BYPASS_VISIT_COST)) return true;

        return VaultHook.getBalance(player) >= this.getVisitCostMoney();
    }

    @NotNull
    public WarpsModule getWarpsModule() {
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
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = Colorizer.apply(name);
    }

    @NotNull
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public void setDescription(@Nullable String description) {
        this.description = description == null ? null : Colorizer.apply(description);
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
    public Optional<String> getCommandShortcut() {
        return Optional.ofNullable(commandShortcut);
    }

    public void setCommandShortcut(@Nullable String commandShortcut) {
        this.getCommandShortcut().ifPresent(CommandRegister::unregister);
        this.commandShortcut = commandShortcut;
        this.getCommandShortcut().ifPresent(has -> CommandRegister.register(this.plugin, new WarpShortcutCommand(this, has)));
    }
}