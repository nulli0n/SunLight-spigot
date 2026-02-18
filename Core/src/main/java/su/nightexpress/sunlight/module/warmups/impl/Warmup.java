package su.nightexpress.sunlight.module.warmups.impl;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.bridge.bossbar.NightBarColor;
import su.nightexpress.nightcore.bridge.bossbar.NightBarOverlay;
import su.nightexpress.nightcore.bridge.bossbar.NightBossBar;
import su.nightexpress.nightcore.util.BossBarUtils;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.nightcore.util.text.night.NightMessage;
import su.nightexpress.nightcore.util.time.TimeFormats;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.module.warmups.WarmupsModule;
import su.nightexpress.sunlight.module.warmups.config.WarmupsConfig;

public abstract class Warmup {

    protected final WarmupsModule module;
    protected final Player        player;
    protected final int           value;
    protected final long          initTime;
    protected final long          finishTime;
    protected final Location      originLocation;

    protected NightBossBar indicator;
    protected long         particleStep;

    public Warmup(@NotNull WarmupsModule module, @NotNull Player player, int countdown) {
        this.module = module;
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
    protected abstract NightBarColor getIndicatorColor();

    @NotNull
    protected abstract NightBarOverlay getIndicatorStyle();

    private void createIndicator() {
        this.indicator = BossBarUtils.createBossBar("", this.getIndicatorColor(), this.getIndicatorStyle());
        this.indicator.addViewer(this.player);
        this.indicator.setProgress(1F);
        //this.indicator.setVisible(true);
        this.updateIndicator();
    }

    public void updateIndicator() {
        if (this.indicator == null) return;

        long current = System.currentTimeMillis();
        float currentDiff = current - this.initTime;
        float finishDiff = this.finishTime - this.initTime;

        float diff = currentDiff / finishDiff;

        float progress = Math.max(0, 1F - diff);
        long timeleft = Math.max(0, (this.finishTime - current));

        String title = this.getIndicatorTitle().replace(SLPlaceholders.GENERIC_TIME, TimeFormats.toSeconds(timeleft));

        this.indicator.setProgress(progress);
        this.indicator.setName(NightMessage.parse(title));
    }

    private void removeIndicator() {
        if (this.indicator == null) return;

        this.indicator.removeViewers();
        this.indicator.setProgress(0F);
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
