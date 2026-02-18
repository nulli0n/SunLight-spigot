package su.nightexpress.sunlight.module.afk;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.nightcore.util.geodata.pos.BlockPos;
import su.nightexpress.sunlight.module.afk.core.AfkSettings;

public class ActivityTracker {

    private final AfkSettings settings;

    private BlockPos lastPos;

    private int idleTime;
    private int idleThreshold;

    private long afkEnterTimestamp;

    private int  wakeUpThreshold;
    private long wakeUpEndTimestamp;

    public ActivityTracker(@NotNull AfkSettings settings) {
        this.settings = settings;
        this.resetCounters();
    }

    public void resetCounters() {
        this.resetIdleCounter();
        this.resetIdleThreshold();
        this.resetWakeUpCounter();
        this.resetAfkTimestamp();
    }

    public void tick() {
        if (this.isAfk()) {
            if (this.isWakingUp() && this.isWakeUpTimeout()) {
                this.resetWakeUpCounter();
            }
        }

        if (!this.isAfk() && this.idleThreshold > 0) {
            this.idleThreshold--;
            return;
        }

        this.countIdleTime();
    }

    public void updatePosition(@NotNull BlockPos newPos) {
        if (this.lastPos == null) {
            this.lastPos = newPos;
            return;
        }

        if (!newPos.equals(this.lastPos)) {
            this.lastPos = newPos;
            this.countActivity(ActivityType.MOVEMENT);
        }
    }

    public void countActivity(@NotNull ActivityType type) {
        this.countActivity(this.settings.getActivityPoints(type));
    }

    public void countActivity(int amount) {
        if (amount <= 0) return;

        if (!this.isAfk()) {
            this.resetIdleCounter();
            this.resetIdleThreshold();
            return;
        }

        if (this.isWakingUp() && this.isWakeUpTimeout()) {
            this.resetWakeUpCounter();
        }

        this.wakeUpEndTimestamp = TimeUtil.createFutureTimestamp(this.settings.wakeUpTimer.get());
        this.wakeUpThreshold -= amount;
    }

    public void countIdleTime() {
        this.idleTime++;
    }

    private void resetWakeUpCounter() {
        this.wakeUpThreshold = this.settings.wakeUpThreshold.get();
        this.wakeUpEndTimestamp = 0L;
    }

    private void resetIdleCounter() {
        this.idleTime = 0;
    }

    private void resetIdleThreshold() {
        this.idleThreshold = this.settings.idleThreshold.get();
    }

    public void setAfkTimestamp() {
        this.afkEnterTimestamp = System.currentTimeMillis();
    }

    public void resetAfkTimestamp() {
        this.afkEnterTimestamp = 0L;
    }

    public boolean isAfk() {
        return this.afkEnterTimestamp > 0L;
    }

    public boolean isWakingUp() {
        return this.wakeUpEndTimestamp > 0L;
    }

    public boolean isWakeUpTimeout() {
        return TimeUtil.isPassed(this.wakeUpEndTimestamp);
    }

    public boolean isEnoughActivity() {
        return this.wakeUpThreshold <= 0;
    }

    public long getAfkEnterTimestamp() {
        return this.afkEnterTimestamp;
    }

    public int getIdleTime() {
        return this.idleTime;
    }
}
