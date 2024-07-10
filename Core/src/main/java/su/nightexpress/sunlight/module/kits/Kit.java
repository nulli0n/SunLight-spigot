package su.nightexpress.sunlight.module.kits;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.integration.VaultHook;
import su.nightexpress.nightcore.manager.AbstractFileData;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.nightcore.util.placeholder.Placeholder;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.core.cooldown.CooldownInfo;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.module.kits.config.KitsLang;
import su.nightexpress.sunlight.module.kits.config.KitsPerms;
import su.nightexpress.sunlight.module.kits.util.KitsUtils;
import su.nightexpress.sunlight.module.kits.util.Placeholders;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Kit extends AbstractFileData<SunLightPlugin> implements Placeholder {

    private final KitsModule module;
    private final PlaceholderMap placeholderMap;

    private String       name;
    private List<String> description;
    private boolean      permissionRequired;
    private int          cooldown;
    private double       cost;
    private int          priority;
    private ItemStack    icon;
    private List<String> commands;
    private ItemStack[]  items;
    private ItemStack[]  armor;
    private ItemStack[]  extras;

    /*enum ContentType {
        ITEMS, ARMOR, EXTRA
    }*/

    public Kit(@NotNull SunLightPlugin plugin, @NotNull KitsModule module, @NotNull File file) {
        super(plugin, file);
        this.module = module;
        this.placeholderMap = Placeholders.forKit(this);
    }

    @Override
    protected boolean onLoad(@NotNull FileConfig config) {
        this.setName(config.getString("Name", this.getId()));
        this.setDescription(config.getStringList("Description"));
        this.setPermissionRequired(config.getBoolean("Permission_Required"));
        this.setCooldown(config.getInt("Cooldown"));
        this.setCost(config.getDouble("Cost.Money"));
        this.setPriority(config.getInt("Priority"));
        this.setIcon(config.getItem("Icon"));
        this.setCommands(config.getStringList("Commands"));
        this.setItems(config.getItemsEncoded("Items"));
        this.setArmor(config.getItemsEncoded("Armor"));
        this.setExtras(config.getItemsEncoded("Extras"));

        return true;
    }

    @Override
    protected void onSave(@NotNull FileConfig config) {
        config.set("Name", this.getName());
        config.set("Description", this.getDescription());
        config.set("Cooldown", this.getCooldown());
        config.set("Permission_Required", this.isPermissionRequired());
        config.set("Cost.Money", this.getCost());
        config.set("Priority", this.getPriority());
        config.setItem("Icon", this.getIcon());
        config.set("Commands", this.getCommands());
        config.setItemsEncoded("Items", Arrays.asList(this.getItems()));
        config.setItemsEncoded("Armor", Arrays.asList(this.getArmor()));
        config.setItemsEncoded("Extras", Arrays.asList(this.getExtras()));
    }

    public boolean give(@NotNull Player player, boolean force) {
        // Check kit permission.
        if (!force && !this.hasPermission(player)) {
            KitsLang.KIT_ERROR_NO_PERMISSION.getMessage().replace(this.replacePlaceholders()).send(player);
            return false;
        }

        // Check kit cooldown.
        SunUser user = plugin.getUserManager().getUserData(player);
        CooldownInfo cooldownInfo = user.getCooldown(this).orElse(null);
        if (!force && cooldownInfo != null) {
            long expireDate = cooldownInfo.getExpireDate();
            (cooldownInfo.isPermanent() ? KitsLang.KIT_ERROR_COOLDOWN_ONE_TIMED : KitsLang.KIT_ERROR_COOLDOWN_EXPIRABLE).getMessage()
                .replace(Placeholders.GENERIC_COOLDOWN, TimeUtil.formatDuration(expireDate))
                .send(player);
            return false;
        }

        // Check kit money cost.
        if (!force && this.hasCost(player) && Plugins.hasVault() && VaultHook.hasEconomy()) {
            double cost = this.getCost();
            double balance = VaultHook.getBalance(player);
            if (balance < cost) {
                KitsLang.KIT_ERROR_NOT_ENOUGH_FUNDS.getMessage().replace(this.replacePlaceholders()).send(player);
                return false;
            }
            VaultHook.takeMoney(player, cost);
        }


        // Give kit content.
        PlayerInventory inventory = player.getInventory();
        List<ItemStack> left = new ArrayList<>();
        inventory.setStorageContents(KitsUtils.fuseItems(KitsUtils.bindToPlayer(player, this.getItems()), inventory.getStorageContents(), left));
        inventory.setArmorContents(KitsUtils.fuseItems(KitsUtils.bindToPlayer(player, this.getArmor()), inventory.getArmorContents(), left));
        inventory.setExtraContents(KitsUtils.fuseItems(KitsUtils.bindToPlayer(player, this.getExtras()), inventory.getExtraContents(), left));
        left.stream().filter(item -> item != null && !item.getType().isAir()).forEach(item -> Players.addItem(player, item));
        this.getCommands().forEach(command -> Players.dispatchCommand(player, command));

        if (!force && this.hasCooldown() && !player.hasPermission(KitsPerms.BYPASS_COOLDOWN)) {
            user.addCooldown(CooldownInfo.of(this));
            this.plugin.getUserManager().scheduleSave(user);
        }

        KitsLang.KIT_GET.getMessage().replace(this.replacePlaceholders()).send(player);
        return true;
    }

    public boolean hasPermission(@NotNull Player player) {
        if (!this.isPermissionRequired()) return true;

        return player.hasPermission(KitsPerms.KIT) || player.hasPermission(this.getPermission());
    }

    public boolean canAfford(@NotNull Player player) {
        if (!this.hasCost(player)) return true;
        if (!Plugins.hasVault()) return true;
        if (!VaultHook.hasEconomy()) return true;

        return VaultHook.getBalance(player) >= this.getCost();
    }

    public boolean isOnCooldown(@NotNull Player player) {
        SunUser user = plugin.getUserManager().getUserData(player);
        return user.getCooldown(this).isPresent();
    }

    public boolean isAvailable(@NotNull Player player) {
        return this.hasPermission(player) && this.canAfford(player) && !this.isOnCooldown(player);
    }

    public boolean isCooldownExpirable() {
        return !this.isOneTimed();
    }

    public boolean isOneTimed() {
        return this.cooldown < 0;
    }

    public boolean hasCooldown() {
        return this.cooldown != 0;
    }

    public boolean hasCost() {
        return this.cost > 0D;
    }

    public boolean hasCooldown(@NotNull Player player) {
        return this.hasCooldown() && !player.hasPermission(KitsPerms.BYPASS_COOLDOWN);
    }

    public boolean hasCost(@NotNull Player player) {
        return this.hasCost() && !player.hasPermission(KitsPerms.BYPASS_COST);
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    public KitsModule getModule() {
        return this.module;
    }

    @NotNull
    public String getPermission() {
        return KitsPerms.PREFIX_KIT + this.getId();
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public List<String> getDescription() {
        return description;
    }

    public void setDescription(@NotNull List<String> description) {
        this.description = description;
    }

    public boolean isPermissionRequired() {
        return this.permissionRequired;
    }

    public void setPermissionRequired(boolean isPermission) {
        this.permissionRequired = isPermission;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public double getCost() {
        return this.cost;
    }

    public void setCost(double cost) {
        this.cost = Math.max(0, cost);
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @NotNull
    public ItemStack getIcon() {
        return new ItemStack(this.icon);
    }

    public void setIcon(@NotNull ItemStack item) {
        ItemUtil.editMeta(item, meta -> meta.addItemFlags(ItemFlag.values()));
        this.icon = new ItemStack(item);
    }

    @NotNull
    public List<String> getCommands() {
        return this.commands;
    }

    public void setCommands(@NotNull List<String> commands) {
        this.commands = new ArrayList<>(commands);
    }

    @NotNull
    public ItemStack[] getItems() {
        return this.items;
    }

    public void setItems(ItemStack[] items) {
        this.items = KitsUtils.filterNulls(Arrays.copyOf(items, 36));
    }

    @NotNull
    public ItemStack[] getArmor() {
        return this.armor;
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = KitsUtils.filterNulls(Arrays.copyOf(armor, 4));
    }

    @NotNull
    public ItemStack[] getExtras() {
        return extras;
    }

    public void setExtras(ItemStack[] extras) {
        this.extras = KitsUtils.filterNulls(Arrays.copyOf(extras, 1));
    }


}