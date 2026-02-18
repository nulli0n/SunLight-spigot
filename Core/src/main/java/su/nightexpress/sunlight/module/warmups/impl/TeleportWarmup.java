package su.nightexpress.sunlight.module.warmups.impl;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.bridge.bossbar.NightBarColor;
import su.nightexpress.nightcore.bridge.bossbar.NightBarOverlay;
import su.nightexpress.sunlight.module.warmups.WarmupsModule;
import su.nightexpress.sunlight.module.warmups.config.WarmupsConfig;
import su.nightexpress.sunlight.module.warmups.config.WarmupsLang;

public class TeleportWarmup extends Warmup {

    private final Location destination;
    private final Runnable callback;

    public TeleportWarmup(@NotNull WarmupsModule module, @NotNull Player player, int countdown, @NotNull Location destination, @Nullable Runnable callback) {
        super(module, player, countdown);
        this.destination = destination.clone();
        this.callback = callback;
    }

    @Override
    protected void onInit() {
        this.module.sendPrefixed(WarmupsLang.WARMUP_TELEPORT_NOTIFY, this.player);
    }

    @Override
    protected void onComplete() {
        this.callback.run();
    }

    @Override
    protected void onCancel(boolean silent) {
        if (!silent) this.module.sendPrefixed(WarmupsLang.WARMUP_TELEPORT_CANCEL, this.player);
    }

    @Override
    @NotNull
    public WarmupType getType() {
        return WarmupType.TELEPORT;
    }

    @Override
    @NotNull
    protected String getIndicatorTitle() {
        return WarmupsConfig.BAR_INDICATOR_TELEPORT_TITLE.get();
    }

    @Override
    @NotNull
    protected NightBarColor getIndicatorColor() {
        return WarmupsConfig.BAR_INDICATOR_TELEPORT_COLOR.get();
    }

    @Override
    @NotNull
    protected NightBarOverlay getIndicatorStyle() {
        return WarmupsConfig.BAR_INDICATOR_TELEPORT_STYLE.get();
    }

    @NotNull
    public Location getDestination() {
        return this.destination;
    }
}
