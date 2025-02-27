package su.nightexpress.sunlight.module.warmups.impl;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.nightcore.util.time.TimeFormats;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.module.warmups.config.WarmupsConfig;

public abstract class Warmup {

    protected final Player   player;
    protected final int      value;
    protected final long     initTime;
    protected final long     finishTime;
    protected final Location originLocation;

    protected BossBar indicator;
    protected long    particleStep;

    public Warmup(@NotNull Player player, int countdown) {
        this.player = player;
        this.value = countdown;
        this.initTime = System.currentTimeMillis();
        this.finishTime = TimeUtil.createFutureTimestamp(countdown);
        this.originLocation = player.getLocation().clone();
    }

    @NotNull
    public abstract WarmupType getType();

    @NotNull
    protected abstract String getIndicatorTitle();

    @NotNull
    protected abstract BarColor getIndicatorColor();

    @NotNull
    protected abstract BarStyle getIndicatorStyle();

    private void createIndicator() {
        this.indicator = Bukkit.createBossBar("", this.getIndicatorColor(), this.getIndicatorStyle());
        this.indicator.addPlayer(this.player);
        this.indicator.setProgress(1D);
        this.indicator.setVisible(true);
        this.updateIndicator();
    }

    public void updateIndicator() {
        if (this.indicator == null) return;

        long current = System.currentTimeMillis();
        double currentDiff = current - this.initTime;
        double finishDiff = this.finishTime - this.initTime;

        double diff = currentDiff / finishDiff;

        double progress = Math.max(0, 1D - diff);
        long timeleft = Math.max(0, (this.finishTime - current));

        String title = this.getIndicatorTitle().replace(Placeholders.GENERIC_TIME, TimeFormats.toSeconds(timeleft));

        this.indicator.setProgress(progress);
        this.indicator.setTitle(NightMessage.asLegacy(title));
    }

    private void removeIndicator() {
        if (this.indicator == null) return;

        this.indicator.removeAll();
        this.indicator.setProgress(0D);
        this.indicator = null;
    }

    protected abstract void onInit();

    protected abstract void onComplete();

    protected abstract void onCancel(boolean silent);

    public void onTick() {
        if (WarmupsConfig.WARMUP_PARTICLES_ENABLED.get()) {
            this.playParticles();
        }
        this.updateIndicator();
    }

    private void playParticles() {
        if (this.particleStep == 30) {
            this.particleStep = 0;
        }

        Location location = this.originLocation.clone().add(0, 0.05D * this.particleStep, 0);

        double x = Math.PI / WarmupsConfig.EFFECT_X.get() * this.particleStep;
        double z = this.particleStep * WarmupsConfig.EFFECT_Z1.get() % WarmupsConfig.EFFECT_Z2.get();
        double y = WarmupsConfig.EFFECT_Y.get();

        Location left = getPointOnCircle(location, true, x, y, z);
        Location right = getPointOnCircle(location, true, x - Math.PI, y, z);

        WarmupsConfig.WARMUP_PARTICLES_LEFT.get().play(player, left, 0, 1);
        WarmupsConfig.WARMUP_PARTICLES_RIGHT.get().play(player, right, 0, 1);

        this.particleStep++;
    }

    public boolean isMoved() {
        return this.player.getLocation().distance(this.originLocation) > WarmupsConfig.WARMUP_MOVEMENT_THRESHOLD.get();
    }

    public boolean isCompleted() {
        return TimeUtil.isPassed(this.finishTime);
    }

    public void init() {
        if (WarmupsConfig.BAR_INDICATOR_ENABLED.get()) {
            this.createIndicator();
        }

        this.onInit();
    }

    public void complete() {
        this.clear();
        this.onComplete();
    }

    public void cancel(boolean silent) {
        this.clear();
        this.onCancel(silent);
    }

    protected void clear() {
        this.removeIndicator();
    }

    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    public int getValue() {
        return this.value;
    }

    @NotNull
    private static Location getPointOnCircle(@NotNull Location location, boolean doCopy, double x, double z, double y) {
        return (doCopy ? location.clone() : location).add(Math.cos(x) * z, y, Math.sin(x) * z);
    }
}
