package su.nightexpress.sunlight.module.rtp;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleInfo;
import su.nightexpress.sunlight.module.rtp.command.RTPCommands;
import su.nightexpress.sunlight.module.rtp.config.RTPConfig;
import su.nightexpress.sunlight.module.rtp.config.RTPLang;
import su.nightexpress.sunlight.module.rtp.config.RTPPerms;
import su.nightexpress.sunlight.module.rtp.impl.LocationFinder;
import su.nightexpress.sunlight.module.rtp.listener.RTPListener;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RTPModule extends Module {

    private final Map<UUID, LocationFinder> finderMap;

    public RTPModule(@NotNull SunLightPlugin plugin, @NotNull String id) {
        super(plugin, id);
        this.finderMap = new ConcurrentHashMap<>();
    }

    @Override
    protected void gatherInfo(@NotNull ModuleInfo moduleInfo) {
        moduleInfo.setConfigClass(RTPConfig.class);
        moduleInfo.setLangClass(RTPLang.class);
        moduleInfo.setPermissionsClass(RTPPerms.class);
    }

    @Override
    protected void onModuleLoad() {
        this.loadCommands();

        this.addListener(new RTPListener(this.plugin, this));

        this.addTask(this.plugin.createAsyncTask(this::tickFinders).setSecondsInterval(1));
    }

    @Override
    protected void onModuleUnload() {
        this.finderMap.clear();
    }

    private void loadCommands() {
        RTPCommands.load(this.plugin, this);
    }

    private void tickFinders() {
        this.finderMap.values().removeIf(LocationFinder::isCompleted);
        this.finderMap.values().forEach(LocationFinder::tick);
    }

    @NotNull
    public Map<UUID, LocationFinder> getFinderMap() {
        return this.finderMap;
    }

    @Nullable
    public LocationFinder getFinder(@NotNull Player player) {
        return this.finderMap.get(player.getUniqueId());
    }

    public void stopSearch(@NotNull Player player) {
        this.finderMap.remove(player.getUniqueId());
    }

    public void startSearch(@NotNull Player player) {
        if (this.getFinder(player) != null) {
            RTPLang.ERROR_ALREADY_IN.getMessage().send(player);
            return;
        }

        LocationFinder finder = LocationFinder.create(this.plugin, player);
        if (finder == null) {
            RTPLang.TELEPORT_ERROR_INVALID_RANGE.getMessage().send(player);
            return;
        }

        finder.timeout();

        this.finderMap.put(player.getUniqueId(), finder);
    }
}
