package su.nightexpress.sunlight.module.warmups;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleInfo;
import su.nightexpress.sunlight.module.warmups.config.WarmupsConfig;
import su.nightexpress.sunlight.module.warmups.config.WarmupsLang;
import su.nightexpress.sunlight.module.warmups.config.WarmupsPerms;
import su.nightexpress.sunlight.module.warmups.impl.TeleportWarmup;
import su.nightexpress.sunlight.module.warmups.impl.Warmup;
import su.nightexpress.sunlight.api.type.TeleportType;
import su.nightexpress.sunlight.module.warmups.listener.WarmupsListener;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WarmupsModule extends Module {

    private final Map<UUID, Warmup> warmupByIdMap;

    public WarmupsModule(@NotNull SunLightPlugin plugin, @NotNull String id) {
        super(plugin, id);
        this.warmupByIdMap = new ConcurrentHashMap<>();
    }

    @Override
    protected void gatherInfo(@NotNull ModuleInfo moduleInfo) {
        moduleInfo.setConfigClass(WarmupsConfig.class);
        moduleInfo.setLangClass(WarmupsLang.class);
        moduleInfo.setPermissionsClass(WarmupsPerms.class);
    }

    @Override
    protected void onModuleLoad() {
        this.addListener(new WarmupsListener(this.plugin, this));
        this.addAsyncTask(this::tickWarmups, WarmupsConfig.WARMUP_TICK_INTERVAL.get());
    }

    @Override
    protected void onModuleUnload() {
        this.getWarmups().forEach(warmup -> warmup.cancel(true));
        this.warmupByIdMap.clear();
    }

    private void tickWarmups() {
        this.getWarmups().removeIf(warmup -> {
            this.tickWarmup(warmup);
            return warmup.isCompleted();
        });
    }

    private void tickWarmup(@NotNull Warmup warmup) {
        if (WarmupsConfig.WARMUP_CANCEL_ON_MOVE.get() && warmup.isMoved()) {
            this.cancelWarmup(warmup.getPlayer());
            return;
        }

        warmup.onTick();

        if (warmup.isCompleted()) {
            this.plugin.runTask(task -> warmup.complete()); // Back to the main thread.
            this.warmupByIdMap.remove(warmup.getPlayer().getUniqueId());
        }
    }

    @Nullable
    public Warmup getWarmup(@NotNull Player player) {
        return this.warmupByIdMap.get(player.getUniqueId());
    }

    public boolean hasWarmup(@NotNull Player player) {
        return this.getWarmup(player) != null;
    }

    @NotNull
    public Set<Warmup> getWarmups() {
        return new HashSet<>(this.warmupByIdMap.values());
    }

    public boolean canHandleTeleport(@NotNull TeleportType type) {
        return WarmupsConfig.TELEPORT_HANDLED_TYPES.get().contains(type);
    }

    public void cancelWarmup(@NotNull Player player) {
        this.cancelWarmup(player, false);
    }

    public void cancelWarmup(@NotNull Player player, boolean silent) {
        Warmup warmup = this.warmupByIdMap.remove(player.getUniqueId());
        if (warmup == null) return;

        warmup.cancel(silent);
    }

    public void addWarmup(@NotNull Player player, @NotNull Warmup warmup) {
        if (warmup.getValue() <= 0) {
            warmup.complete();
            return;
        }

        this.cancelWarmup(player, true);

        warmup.init();

        this.warmupByIdMap.put(player.getUniqueId(), warmup);
    }

    public void handleTeleport(@NotNull Player player, @NotNull Location destination, @NotNull TeleportType type, @Nullable Runnable action) {
        int value = WarmupsConfig.TELEPORT_WARMUPS_BY_RANK.get().getSmallest(player);
        Warmup warmup = new TeleportWarmup(player, value, destination, action);

        this.addWarmup(player, warmup);
    }
}
