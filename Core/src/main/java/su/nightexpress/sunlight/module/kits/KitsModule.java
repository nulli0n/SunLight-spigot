package su.nightexpress.sunlight.module.kits;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.editor.EditorManager;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.kits.command.kits.KitsCommand;
import su.nightexpress.sunlight.module.kits.config.KitsConfig;
import su.nightexpress.sunlight.module.kits.config.KitsLang;
import su.nightexpress.sunlight.module.kits.editor.EditorLocales;
import su.nightexpress.sunlight.module.kits.editor.KitsEditor;
import su.nightexpress.sunlight.module.kits.listener.KitListener;
import su.nightexpress.sunlight.module.kits.menu.KitsMenu;
import su.nightexpress.sunlight.module.kits.config.KitsPerms;

import java.util.*;

public class KitsModule extends Module {

    public static final String DIR_KITS = "/kits/";

    private final Map<String, Kit> kits;

    private KitsMenu      kitsMenu;
    private KitsEditor    kitsEditor;

    public KitsModule(@NotNull SunLight plugin, @NotNull String id) {
        super(plugin, id);
        this.kits = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        this.plugin.registerPermissions(KitsPerms.class);
        this.plugin.getLangManager().loadMissing(KitsLang.class);
        this.plugin.getLangManager().loadEditor(EditorLocales.class);
        this.plugin.getLang().setComments("Kits", "-".repeat(50), "Kits Module Lang", "-".repeat(50));
        this.plugin.getLang().saveChanges();
        this.plugin.getConfigManager().extractResources(this.getLocalPath() + DIR_KITS);
        this.getConfig().initializeOptions(KitsConfig.class);

        for (JYML kitConfig : JYML.loadAll(this.getAbsolutePath() + DIR_KITS, false)) {
            Kit kit = new Kit(this, kitConfig);
            if (kit.load()) {
                this.kits.put(kit.getId(), kit);
            }
            else this.error("Kit not loaded: '" + kitConfig.getFile().getName() + "'!");
        }
        this.info("Loaded " + this.getKits().size() + " kits.");

        this.kitsMenu = new KitsMenu(this);
        this.plugin.getCommandRegulator().register(KitsCommand.NAME, (cfg1, aliases) -> new KitsCommand(this, aliases));

        this.addListener(new KitListener(this));
    }

    @Override
    protected void onShutdown() {
        if (this.kitsEditor != null) {
            this.kitsEditor.clear();
            this.kitsEditor = null;
        }
        if (this.kitsMenu != null) {
            this.kitsMenu.clear();
            this.kitsMenu = null;
        }
        this.kits.values().forEach(Kit::clear);
        this.kits.clear();
    }

    @NotNull
    public KitsMenu getKitsMenu() {
        return this.kitsMenu;
    }

    @NotNull
    public KitsEditor getEditor() {
        if (this.kitsEditor == null) {
            this.kitsEditor = new KitsEditor(this);
        }
        return kitsEditor;
    }

    public boolean isKitExists(@NotNull String id) {
        return this.getKitById(id) != null;
    }

    @Nullable
    public Kit getKitById(@NotNull String id) {
        return this.kits.get(id.toLowerCase());
    }

    @NotNull
    public Collection<Kit> getKits() {
        return this.kits.values().stream().toList();
    }

    @NotNull
    public Collection<Kit> getKits(@NotNull Player player) {
        return this.kits.values().stream().filter(kit -> kit.hasPermission(player)).toList();
    }

    @NotNull
    public List<String> getKitIds() {
        return new ArrayList<>(this.kits.keySet());
    }

    @NotNull
    public List<String> getKitIds(@NotNull Player player) {
        return this.getKits(player).stream().map(Kit::getId).toList();
    }

    public void delete(@NotNull Kit kit) {
        if (kit.getFile().delete()) {
            kit.clear();
            this.kits.remove(kit.getId());
        }
    }

    public boolean create(@NotNull Player player, @NotNull String id) {
        if (this.isKitExists(id)) {
            EditorManager.error(player, this.plugin.getMessage(KitsLang.EDITOR_ERROR_ALREADY_EXISTS).getLocalized());
            return false;
        }

        Kit kit = new Kit(this, id);
        kit.save();
        kit.load();
        this.kits.put(kit.getId(), kit);
        return true;
    }
}
