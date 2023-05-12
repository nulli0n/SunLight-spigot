package su.nightexpress.sunlight.module;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.afk.AfkModule;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.extras.ExtrasModule;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.rtp.RTPModule;
import su.nightexpress.sunlight.module.scoreboard.ScoreboardModule;
import su.nightexpress.sunlight.module.spawns.SpawnsModule;
import su.nightexpress.sunlight.module.tab.TabModule;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.module.worlds.WorldsModule;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class ModuleManager extends AbstractManager<SunLight> {

    public static final String DIR_MODULES = "/modules/";

    private final Map<String, Module> modules = new LinkedHashMap<>();

    public ModuleManager(@NotNull SunLight plugin) {
        super(plugin);
    }

    @Override
    protected void onLoad() {
        this.register(ModuleId.BANS, id -> new BansModule(this.plugin, id));
        this.register(ModuleId.WORLDS, id -> new WorldsModule(this.plugin, id));
        this.register(ModuleId.SPAWNS, id -> new SpawnsModule(this.plugin, id));
        this.register(ModuleId.HOMES, id -> new HomesModule(this.plugin, id));
        this.register(ModuleId.KITS, id -> new KitsModule(this.plugin, id));
        this.register(ModuleId.WARPS, id -> new WarpsModule(this.plugin, id));
        this.register(ModuleId.CHAT, id -> new ChatModule(this.plugin, id));
        //this.register(this.menuManager = new MenuManager(plugin));
        this.register(ModuleId.TAB, id -> new TabModule(this.plugin, id));
        this.register(ModuleId.SCOREBOARD, id -> new ScoreboardModule(this.plugin, id));
        this.register(ModuleId.RTP, id -> new RTPModule(this.plugin, id));
        this.register(ModuleId.EXTRAS, id -> new ExtrasModule(this.plugin, id));
        this.register(ModuleId.AFK, id -> new AfkModule(this.plugin, id));

        this.plugin.getConfig().saveChanges();
    }

    @Override
    protected void onShutdown() {
        this.getModules().forEach(Module::shutdown);
        this.modules.clear();
    }

    private <T extends Module> boolean register(@NotNull String id, @NotNull Function<String, T> supplier) {
        id = id.toLowerCase();

        // Add missing module info to the config.yml
        this.plugin.getConfig().addMissing("Modules." + id, true);
        if (!this.plugin.getConfig().getBoolean("Modules." + id)) {
            return false;
        }

        if (this.getModule(id) != null) {
            this.plugin.error("Could not register " + id + " module! Module with such id is already registered!");
            return false;
        }

        // Init module.
        T module = supplier.apply(id);

        if (!module.canLoad()) {
            this.plugin.error("Module can not be loaded: " + module.getName());
            return false;
        }

        long loadTook = System.currentTimeMillis();
        module.setup();
        loadTook = System.currentTimeMillis() - loadTook;

        this.plugin.info("Loaded module: " + module.getName() + " in " + loadTook + " ms.");
        this.modules.put(module.getId(), module);
        return true;
    }

    /*public void unregister(@NotNull Module module) {
        String id = module.getId();
        if (this.modules.remove(id) != null) {
            this.plugin.info("Unloaded module: " + module.getName());
        }
        module.shutdown();
    }*/

    @NotNull
    public <T extends Module> Optional<T> getModule(@NotNull Class<T> clazz) {
        for (Module module : this.getModules()) {
            if (clazz.isAssignableFrom(module.getClass())) {
                return Optional.of(clazz.cast(module));
            }
        }
        return Optional.empty();
    }

    @Nullable
    public Module getModule(@NotNull String id) {
        return this.modules.get(id.toLowerCase());
    }

    @NotNull
    public Collection<Module> getModules() {
        return this.modules.values();
    }
}
