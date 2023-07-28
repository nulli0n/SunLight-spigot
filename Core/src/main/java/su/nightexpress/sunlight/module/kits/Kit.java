package su.nightexpress.sunlight.module.kits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractConfigHolder;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.integration.VaultHook;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.*;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.data.impl.cooldown.CooldownInfo;
import su.nightexpress.sunlight.module.kits.config.KitsConfig;
import su.nightexpress.sunlight.module.kits.config.KitsLang;
import su.nightexpress.sunlight.module.kits.editor.KitSettingsEditor;
import su.nightexpress.sunlight.module.kits.menu.KitPreviewMenu;
import su.nightexpress.sunlight.module.kits.config.KitsPerms;
import su.nightexpress.sunlight.module.kits.util.KitsUtils;
import su.nightexpress.sunlight.module.kits.util.Placeholders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Kit extends AbstractConfigHolder<SunLight> implements Placeholder {

    private final KitsModule module;

    private String       name;
    private List<String> description;
    private boolean      isPermission;
    private int          cooldown;
    private double       cost;
    private int          priority;
    private ItemStack    icon;
    private List<String> commands;
    private ItemStack[]  items;
    private ItemStack[]  armor;
    private ItemStack[] extras;

    private KitPreviewMenu    preview;
    private KitSettingsEditor editor;

    private final PlaceholderMap placeholderMap;

    /*enum ContentType {
        ITEMS, ARMOR, EXTRA
    }*/

    public Kit(@NotNull KitsModule module, @NotNull String id) {
        this(module, JYML.loadOrExtract(module.plugin(), module.getLocalPath() + KitsModule.DIR_KITS, id + ".yml"));

        this.setName(StringUtil.capitalizeFully(id.replace("_", " ")));
        this.setPermissionRequired(true);
        this.setCooldown(86400);
        this.setIcon(new ItemStack(Material.LEATHER_CHESTPLATE));
        ItemStack[] items = new ItemStack[36];
        items[0] = new ItemStack(Material.GOLDEN_SWORD);
        items[1] = new ItemStack(Material.COOKED_BEEF, 16);
        items[2] = new ItemStack(Material.GOLDEN_APPLE, 4);
        this.setItems(items);
        this.setArmor(new ItemStack[4]);
        this.setExtras(new ItemStack[1]);
    }

    public Kit(@NotNull KitsModule module, @NotNull JYML cfg) {
        super(module.plugin(), cfg);
        this.module = module;

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.KIT_ID, this::getId)
            .add(Placeholders.KIT_NAME, this::getName)
            .add(Placeholders.KIT_DESCRIPTION, () -> String.join("\n", this.getDescription()))
            .add(Placeholders.KIT_PERMISSION_REQUIRED, () -> LangManager.getBoolean(this.isPermissionRequired()))
            .add(Placeholders.KIT_PERMISSION_NODE, this::getPermission)
            .add(Placeholders.KIT_COOLDOWN, () -> this.getCooldown() >= 0 ? TimeUtil.formatTime(this.getCooldown() * 1000L) : LangManager.getPlain(Lang.OTHER_ONE_TIMED))
            .add(Placeholders.KIT_COST_MONEY, () -> NumberUtil.format(this.getCost()))
            .add(Placeholders.KIT_PRIORITY, () -> String.valueOf(this.getPriority()))
            .add(Placeholders.KIT_COMMANDS, () -> String.join("\n", this.getCommands()));
    }

    @Override
    public boolean load() {
        this.setName(cfg.getString("Name", this.getId()));
        this.setDescription(cfg.getStringList("Description"));
        this.setPermissionRequired(cfg.getBoolean("Permission_Required"));
        this.setCooldown(cfg.getInt("Cooldown"));
        this.setCost(cfg.getDouble("Cost.Money"));
        this.setPriority(cfg.getInt("Priority"));
        this.setIcon(cfg.getItem("Icon"));
        this.setCommands(cfg.getStringList("Commands"));
        this.setItems(ItemUtil.decompress(cfg.getStringList("Items")));
        this.setArmor(ItemUtil.decompress(cfg.getStringList("Armor")));
        this.setExtras(ItemUtil.decompress(cfg.getStringList("Extras")));
        return true;
    }

    @Override
    public void onSave() {
        cfg.set("Name", this.getName());
        cfg.set("Description", this.getDescription());
        cfg.set("Cooldown", this.getCooldown());
        cfg.set("Permission_Required", this.isPermissionRequired());
        cfg.set("Cost.Money", this.getCost());
        cfg.set("Priority", this.getPriority());
        cfg.setItem("Icon", this.getIcon());
        cfg.set("Commands", this.getCommands());
        cfg.set("Items", ItemUtil.compress(this.getItems()));
        cfg.set("Armor", ItemUtil.compress(this.getArmor()));
        cfg.set("Extras", ItemUtil.compress(this.getExtras()));
    }

    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        if (this.preview != null) {
            this.preview.clear();
            this.preview = null;
        }
        this.commands.clear();
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    public KitSettingsEditor getEditor() {
        if (this.editor == null) {
            this.editor = new KitSettingsEditor(this);
        }
        return this.editor;
    }

    @NotNull
    public KitsModule getModule() {
        return this.module;
    }

    @NotNull
    public KitPreviewMenu getPreview() {
        if (this.preview == null) {
            this.preview = new KitPreviewMenu(this);
        }
        return preview;
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
        this.name = Colorizer.apply(name);
    }

    @NotNull
    public List<String> getDescription() {
        return description;
    }

    public void setDescription(@NotNull List<String> description) {
        this.description = Colorizer.apply(description);
    }

    public boolean isPermissionRequired() {
        return this.isPermission;
    }

    public void setPermissionRequired(boolean isPermission) {
        this.isPermission = isPermission;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public boolean isCooldownExpirable() {
        return this.getCooldown() >= 0;
    }

    public boolean hasCooldown() {
        return this.getCooldown() != 0;
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

    public void setIcon(@NotNull ItemStack icon) {
        ItemMeta meta = icon.getItemMeta();
        if (meta != null) {
            meta.addItemFlags(ItemFlag.values());
            icon.setItemMeta(meta);
        }
        this.icon = new ItemStack(icon);
    }

    @NotNull
    public List<String> getCommands() {
        return this.commands;
    }

    public void setCommands(@NotNull List<String> commands) {
        this.commands = commands;
    }

    @NotNull
    public ItemStack[] getItems() {
        return this.items;
    }

    public void setItems(ItemStack[] items) {
        this.items = this.fineItems(Arrays.copyOf(items, 36));
    }

    @NotNull
    public ItemStack[] getArmor() {
        return this.armor;
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = this.fineItems(Arrays.copyOf(armor, 4));
    }

    @NotNull
    public ItemStack[] getExtras() {
        return extras;
    }

    public void setExtras(ItemStack[] extras) {
        this.extras = this.fineItems(Arrays.copyOf(extras, 1));
    }

    private ItemStack[] fineItems(ItemStack[] array) {
        for (int count = 0; count < array.length; count++) {
            if (array[count] == null) {
                array[count] = new ItemStack(Material.AIR);
            }
        }
        return array;
    }

    private ItemStack[] getItems(@NotNull Player player, @NotNull ItemStack[] from) {
        ItemStack[] array = new ItemStack[from.length];
        for (int index = 0; index < array.length; index++) {
            ItemStack item = new ItemStack(from[index]);
            if (KitsConfig.BIND_ITEMS_TO_PLAYERS.get()) {
                KitsUtils.setItemOwner(item, player);
            }
            array[index] = item;
        }
        return array;
    }

    @NotNull
    private ItemStack[] fuseItems(ItemStack[] kitItems, ItemStack[] inventory, @NotNull List<ItemStack> left) {
        for (int index = 0; index < inventory.length; index++) {
            ItemStack itemInv = inventory[index];
            ItemStack itemKit = kitItems[index];
            if (itemKit == null || itemKit.getType().isAir()) continue;

            if (itemInv == null || itemInv.getType().isAir()) {
                inventory[index] = new ItemStack(itemKit);
            }
            else left.add(itemKit);
        }
        return inventory;
    }

    public boolean hasPermission(@NotNull Player player) {
        if (!this.isPermissionRequired()) return true;
        return player.hasPermission(KitsPerms.PREFIX_KIT + this.getId());
    }

    public boolean canAfford(@NotNull Player player) {
        if (!EngineUtils.hasVault()) return true;
        if (!VaultHook.hasEconomy() || player.hasPermission(KitsPerms.BYPASS_COST_MONEY)) return true;

        return VaultHook.getBalance(player) >= this.getCost();
    }

    public boolean isOnCooldown(@NotNull Player player) {
        SunUser user = plugin.getUserManager().getUserData(player);
        return user.getCooldown(this).isPresent();
    }

    public boolean isAvailable(@NotNull Player player) {
        return this.hasPermission(player) && this.canAfford(player) && !this.isOnCooldown(player);
    }

    public void give(@NotNull Player player, boolean force) {
        SunLight plugin = this.module.plugin();
        SunUser user = plugin.getUserManager().getUserData(player);

        // Check kit permission.
        if (!force && !this.hasPermission(player)) {
            plugin.getMessage(KitsLang.KIT_ERROR_NO_PERMISSION).replace(this.replacePlaceholders()).send(player);
            return;
        }

        // Check kit cooldown.
        CooldownInfo cooldownInfo = user.getCooldown(this).orElse(null);
        if (!force && cooldownInfo != null) {
            long expireDate = cooldownInfo.getExpireDate();
            plugin.getMessage(cooldownInfo.isPermanent() ? KitsLang.KIT_ERROR_COOLDOWN_ONE_TIMED : KitsLang.KIT_ERROR_COOLDOWN_EXPIRABLE)
                .replace(Placeholders.GENERIC_COOLDOWN, TimeUtil.formatTimeLeft(expireDate))
                .send(player);
            return;
        }

        // Check kit money cost.
        double kitCost = (!VaultHook.hasEconomy() || player.hasPermission(KitsPerms.BYPASS_COST_MONEY) || force) ? 0D : this.getCost();
        double userBalance = kitCost > 0 ? VaultHook.getBalance(player) : 0D;
        if (userBalance < kitCost) {
            plugin.getMessage(KitsLang.KIT_ERROR_NOT_ENOUGH_FUNDS).replace(this.replacePlaceholders()).send(player);
            return;
        }
        else if (kitCost > 0) VaultHook.takeMoney(player, kitCost);


        // Give kit content.
        PlayerInventory inventory = player.getInventory();
        List<ItemStack> left = new ArrayList<>();
        inventory.setStorageContents(this.fuseItems(this.getItems(player, this.getItems()), inventory.getStorageContents(), left));
        inventory.setArmorContents(this.fuseItems(this.getItems(player, this.getArmor()), inventory.getArmorContents(), left));
        inventory.setExtraContents(this.fuseItems(this.getItems(player, this.getExtras()), inventory.getExtraContents(), left));
        left.stream().filter(item -> item != null && !item.getType().isAir()).forEach(item -> PlayerUtil.addItem(player, item));
        this.getCommands().forEach(command -> PlayerUtil.dispatchCommand(player, command));
        //player.getInventory().setStorageContents(this.getItems(player, this.getItems()));
        //player.getInventory().setArmorContents(this.getItems(player, this.getArmor()));
        //player.getInventory().setExtraContents(this.getItems(player, this.getExtras()));

        if (!force && this.hasCooldown() && !player.hasPermission(KitsPerms.BYPASS_COOLDOWN)) {
            user.addCooldown(CooldownInfo.of(this));
            user.saveData(this.plugin);
        }

        plugin.getMessage(KitsLang.KIT_GET).replace(this.replacePlaceholders()).send(player);
    }

}