package su.nightexpress.sunlight.module.kits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.FileUtil;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleInfo;
import su.nightexpress.sunlight.module.kits.command.KitCommands;
import su.nightexpress.sunlight.module.kits.config.KitsConfig;
import su.nightexpress.sunlight.module.kits.config.KitsLang;
import su.nightexpress.sunlight.module.kits.editor.KitSettingsEditor;
import su.nightexpress.sunlight.module.kits.editor.KitListEditor;
import su.nightexpress.sunlight.module.kits.listener.KitBindListener;
import su.nightexpress.sunlight.module.kits.menu.KitPreviewMenu;
import su.nightexpress.sunlight.module.kits.menu.KitsMenu;
import su.nightexpress.sunlight.module.kits.config.KitsPerms;
import su.nightexpress.sunlight.module.kits.util.KitsUtils;

import java.io.File;
import java.util.*;

public class KitsModule extends Module {

    public static final String DIR_KITS = "/kits/";

    private final Map<String, Kit> kitMap;

    private KitsMenu          kitsMenu;
    private KitListEditor     kitListEditor;
    private KitSettingsEditor kitSettingsEditor;
    private KitPreviewMenu    previewMenu;

    public KitsModule(@NotNull SunLightPlugin plugin, @NotNull String id) {
        super(plugin, id);
        this.kitMap = new HashMap<>();
    }

    @Override
    protected void gatherInfo(@NotNull ModuleInfo moduleInfo) {
        moduleInfo.setConfigClass(KitsConfig.class);
        moduleInfo.setLangClass(KitsLang.class);
        moduleInfo.setPermissionsClass(KitsPerms.class);
    }

    @Override
    protected void onModuleLoad() {
        KitsUtils.loadKeys(this.plugin);

        this.loadCommands();
        this.loadUI();
        this.loadKits();

        if (KitsUtils.isBindEnabled()) {
            this.addListener(new KitBindListener(this.plugin, this));
        }
    }

    @Override
    protected void onModuleUnload() {
        if (this.kitListEditor != null) this.kitListEditor.clear();
        if (this.kitsMenu != null) this.kitsMenu.clear();
        if (this.kitSettingsEditor != null) this.kitSettingsEditor.clear();
        if (this.previewMenu != null) this.previewMenu.clear();

        this.kitMap.clear();
    }

    private void loadCommands() {
        KitCommands.load(this.plugin, this);
    }

    private void loadUI() {
        this.kitsMenu = new KitsMenu(this.plugin, this);
        this.kitListEditor = new KitListEditor(this.plugin, this);
        this.kitSettingsEditor = new KitSettingsEditor(this.plugin, this);
        this.previewMenu = new KitPreviewMenu(this.plugin, this);
    }

    private void loadKits() {
        for (File file : FileUtil.getFiles(this.getAbsolutePath() + DIR_KITS, false)) {
            Kit kit = new Kit(this.plugin, this, file);
            this.loadKit(kit);
        }
        this.info("Loaded " + this.getKits().size() + " kits.");
    }

    private boolean loadKit(@NotNull Kit kit) {
        if (!kit.load()) {
            this.error("Kit not loaded: '" + kit.getFile().getName() + "'!");
            return false;
        }

        this.kitMap.put(kit.getId(), kit);
        return true;
    }

    public boolean createKit(@NotNull String id) {
        id = StringUtil.lowerCaseUnderscoreStrict(id);

        if (this.isKitExists(id)) return false;

        File file = new File(this.getAbsolutePath() + DIR_KITS, id + ".yml");
        Kit kit = new Kit(this.plugin, this, file);
        kit.setName(StringUtil.capitalizeUnderscored(id));
        kit.setPermissionRequired(true);
        kit.setCooldown(86400);
        kit.setIcon(new ItemStack(Material.LEATHER_CHESTPLATE));
        ItemStack[] items = new ItemStack[36];
        items[0] = new ItemStack(Material.GOLDEN_SWORD);
        items[1] = new ItemStack(Material.COOKED_BEEF, 16);
        items[2] = new ItemStack(Material.GOLDEN_APPLE, 4);
        kit.setItems(items);
        kit.setArmor(new ItemStack[4]);
        kit.setExtras(new ItemStack[1]);
        kit.save();

        this.loadKit(kit);
        return true;
    }

    public void deleteKit(@NotNull Kit kit) {
        if (kit.getFile().delete()) {
            this.kitMap.remove(kit.getId());
        }
    }

    public void openKitsMenu(@NotNull Player player) {
        this.kitsMenu.open(player);
    }

    public void openEditor(@NotNull Player player) {
        this.kitListEditor.open(player, this);
    }

    public void openKitSettings(@NotNull Player player, @NotNull Kit kit) {
        this.kitSettingsEditor.open(player, kit);
    }

    public void openKitPreview(@NotNull Player player, @NotNull Kit kit) {
        this.previewMenu.open(player, kit);
    }

    public boolean isKitExists(@NotNull String id) {
        return this.getKitById(id) != null;
    }

    @Nullable
    public Kit getKitById(@NotNull String id) {
        return this.kitMap.get(id.toLowerCase());
    }

    @NotNull
    public Map<String, Kit> getKitMap() {
        return this.kitMap;
    }

    @NotNull
    public Collection<Kit> getKits() {
        return this.kitMap.values().stream().toList();
    }

    @NotNull
    public Collection<Kit> getKits(@NotNull Player player) {
        return this.kitMap.values().stream().filter(kit -> kit.hasPermission(player)).toList();
    }

    @NotNull
    public List<String> getKitIds() {
        return new ArrayList<>(this.kitMap.keySet());
    }

    @NotNull
    public List<String> getKitIds(@NotNull Player player) {
        return this.getKits(player).stream().map(Kit::getId).toList();
    }
}
