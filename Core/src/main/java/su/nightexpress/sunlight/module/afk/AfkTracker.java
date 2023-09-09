package su.nightexpress.sunlight.module.afk;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.TimeUtil;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.module.afk.config.AfkConfig;
import su.nightexpress.sunlight.utils.Point3D;

public class AfkTracker {

    private final SunLight  plugin;
    private final AfkModule module;
    private final Player    player;

    private Point3D lastPosition;
    private int  wakeUpAttempts;
    private long wakeUpTimeout;
    private long sleepCooldown;

    public AfkTracker(@NotNull AfkModule module, @NotNull Player player) {
        this.plugin = module.plugin();
        this.module = module;
        this.player = player;
        this.setLastPosition();
    }

    public void tick() {
        if (this.isMoved()) {
            this.setLastPosition();
            this.onInteract();
        }
        else {
            this.onIdle();
        }
    }

    public void onIdle() {
        if (!this.canIdle()) return;

        SunUser user = plugin.getUserManager().getUserData(this.getPlayer());
        int idleTime = AfkModule.getIdleTime(user) + 1;
        boolean isAfk = AfkModule.isAfk(user);

        AfkModule.setIdleTime(user, idleTime);
        if (!isAfk) {
            int timeToAfk = AfkModule.getTimeToAfk(this.getPlayer());
            if (timeToAfk > 0 && idleTime >= timeToAfk) {
                this.module.enterAfk(this.getPlayer());
            }
        }
        else {
            int timeToKick = AfkModule.getTimeToKick(this.getPlayer());
            if (timeToKick > 0 && idleTime >= timeToKick) {
                this.plugin.runTask(task -> {
                    this.getPlayer().kickPlayer(String.join("\n", AfkConfig.AFK_KICK_MESSAGE.get())
                        .replace(Placeholders.GENERIC_TIME, TimeUtil.formatTime(timeToKick * 1000L)));
                });
            }
        }
    }

    public void onInteract() {
        if (this.module.isAfk(this.getPlayer())) {
            if (System.currentTimeMillis() >= this.wakeUpTimeout && this.wakeUpTimeout != 0L) {
                this.wakeUpAttempts = 0;
                this.wakeUpTimeout = 0L;
            }
            else {
                if (this.wakeUpAttempts >= AfkConfig.WAKE_UP_THRESHOLD.get() - 1) {
                    this.wakeUpAttempts = 0;
                    this.wakeUpTimeout = 0L;
                    this.module.exitAfk(this.getPlayer());
                    return;
                }
            }

            this.wakeUpAttempts++;
            this.wakeUpTimeout = System.currentTimeMillis() + AfkConfig.WAKE_UP_TIMEOUT.get() * 1000L;
        }
        else {
            SunUser user = this.plugin.getUserManager().getUserData(this.getPlayer());
            AfkModule.setIdleTime(user, 0);
        }
    }

    public void clear() {
        this.wakeUpAttempts = 0;
        this.wakeUpTimeout = 0L;
    }

    public void resetSleepCooldown() {
        this.sleepCooldown = 0L;
    }

    public void setSleepCooldown() {
        this.sleepCooldown = System.currentTimeMillis() + AfkConfig.AFK_COOLDOWN.get() * 1000L;
    }

    public void setLastPosition() {
        this.lastPosition = Point3D.of(this.player.getLocation());
    }

    public boolean canIdle() {
        return System.currentTimeMillis() >= this.sleepCooldown;
    }

    public boolean isMoved() {
        Point3D current = Point3D.of(this.getPlayer().getLocation());
        return current.isDifferent(this.lastPosition);
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }


}
