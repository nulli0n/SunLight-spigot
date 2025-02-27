package su.nightexpress.sunlight.module.warmups.impl;

import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.sunlight.module.warmups.config.WarmupsConfig;
import su.nightexpress.sunlight.module.warmups.config.WarmupsLang;
import su.nightexpress.sunlight.utils.SunUtils;

public class TeleportWarmup extends Warmup {

    private final Location destination;
    private final Runnable action;

    public TeleportWarmup(@NotNull Player player, int countdown, @NotNull Location destination, @Nullable Runnable action) {
        super(player, countdown);
        this.destination = destination.clone();
        this.action = action;
    }

    @Override
    protected void onInit() {
        WarmupsLang.WARMUP_TELEPORT_NOTIFY.getMessage().send(this.player);
    }

    @Override
    protected void onComplete() {
        SunUtils.teleport(this.player, this.destination, this.action);
    }

    @Override
    protected void onCancel(boolean silent) {
        if (!silent) WarmupsLang.WARMUP_TELEPORT_CANCEL.getMessage().send(this.player);
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
    protected BarColor getIndicatorColor() {
        return WarmupsConfig.BAR_INDICATOR_TELEPORT_COLOR.get();
    }

    @Override
    @NotNull
    protected BarStyle getIndicatorStyle() {
        return WarmupsConfig.BAR_INDICATOR_TELEPORT_STYLE.get();
    }
}
