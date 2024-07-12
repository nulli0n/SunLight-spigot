package su.nightexpress.sunlight.module.afk;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.afk.config.AfkConfig;
import su.nightexpress.sunlight.module.afk.config.AfkLang;
import su.nightexpress.sunlight.module.afk.event.PlayerAfkEvent;
import su.nightexpress.sunlight.utils.pos.BlockPos;

public class AfkState {

    private final SunLightPlugin plugin;
    private final AfkModule      module;
    private final Player    player;

    private BlockPos latestPosition;

    private int  activityPoints;
    private long wakeUpTimeout;

    private boolean afk;
    private long afkSince;
    private long idleTime;
    private long idleCooldown;

    public AfkState(@NotNull SunLightPlugin plugin, @NotNull AfkModule module, @NotNull Player player) {
        this.plugin = plugin;
        this.module = module;
        this.player = player;
        this.updateLatestPosition();
    }

    public void tick() {
        if (!this.player.isOnline()) return;

        if (this.isMoved()) {
            this.updateLatestPosition();
            this.onActivity(AfkConfig.WAKE_UP_ACTIVITY_POINTS_MOVEMENT.get());
        }
        else {
            this.onIdle();
        }
    }

    public void reset() {
        this.afk = false;
        this.afkSince = 0L;
        this.idleTime = 0;
        this.activityPoints = 0;
        this.wakeUpTimeout = 0;
        this.idleCooldown = 0;
    }

    public void exitAfk() {
        if (!this.isAfk()) return;

        this.afk = false;

        String time = TimeUtil.formatTime(System.currentTimeMillis() - this.getAfkSince());

        AfkConfig.WAKE_UP_COMMANDS.get().forEach(command -> {
            command = command.replace(Placeholders.GENERIC_TIME, time);
            if (Plugins.hasPlaceholderAPI()) command = PlaceholderAPI.setPlaceholders(player, command);
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), Placeholders.forPlayer(player).apply(command));
        });

        this.reset();
        this.setIdleCooldown();
        this.updateLatestPosition();

        PlayerAfkEvent event = new PlayerAfkEvent(this.player, this);
        this.plugin.getPluginManager().callEvent(event);

        AfkLang.AFK_EXIT.getMessage()
            .replace(Placeholders.forPlayer(player))
            .replace(Placeholders.GENERIC_TIME, time)
            .broadcast();
    }

    public void enterAfk() {
        if (this.isAfk()) return;

        this.afk = true;
        this.afkSince = System.currentTimeMillis();
        this.idleCooldown = 0;

        AfkConfig.AFK_COMMANDS.get().forEach(command -> {
            if (Plugins.hasPlaceholderAPI()) command = PlaceholderAPI.setPlaceholders(player, command);
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), Placeholders.forPlayer(player).apply(command));
        });

        this.updateLatestPosition();

        PlayerAfkEvent event = new PlayerAfkEvent(this.player, this);
        this.plugin.getPluginManager().callEvent(event);

        AfkLang.AFK_ENTER.getMessage().replace(Placeholders.forPlayer(player)).broadcast();
    }

    public void onIdle() {
        if (!this.canIdle()) return;

        this.idleTime++;

        if (this.canBeKicked()) {
            this.exitAfk();
            this.plugin.runTask(task -> this.module.kick(this.player)); // Sync to the main thread.
            return;
        }

        if (!this.isAfk()) {
            int timeToAfk = AfkModule.getTimeToAfk(this.player);
            if (timeToAfk > 0 && this.idleTime >= timeToAfk) {
                this.plugin.runTask(task -> this.enterAfk()); // Sync to the main thread.
            }
        }
    }

    public void onActivity(int amount) {
        if (this.isAfk()) {
            if (System.currentTimeMillis() >= this.wakeUpTimeout && this.wakeUpTimeout != 0L) {
                this.activityPoints = 0;
                this.wakeUpTimeout = System.currentTimeMillis() + AfkConfig.WAKE_UP_TIMEOUT.get() * 1000L;
            }
            this.activityPoints += amount;

            if (this.activityPoints >= AfkConfig.WAKE_UP_THRESHOLD.get()) {
                this.activityPoints = 0;
                this.wakeUpTimeout = 0L;
                this.plugin.runTask(task -> this.exitAfk()); // Sync to the main thread.
            }
        }
        else {
            this.idleTime = 0;
        }
    }

    public void updateLatestPosition() {
        this.latestPosition = BlockPos.from(this.player.getLocation());
    }

    public void setIdleCooldown() {
        this.idleCooldown = System.currentTimeMillis() + AfkConfig.AFK_COOLDOWN.get() * 1000L;
    }

    public boolean canIdle() {
        return System.currentTimeMillis() >= this.idleCooldown;
    }

    public boolean canBeKicked() {
        int timeToKick = AfkModule.getTimeToKick(this.player);
        return timeToKick > 0 && this.idleTime >= timeToKick;
    }

    public boolean isMoved() {
        BlockPos current = BlockPos.from(this.player.getLocation());
        return !current.equals(this.latestPosition);
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    public boolean isAfk() {
        return afk;
    }

    public long getAfkSince() {
        return afkSince;
    }

    public long getIdleTime() {
        return idleTime;
    }
}
