package su.nightexpress.sunlight.module.ptp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.Players;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TeleportRequest {

    private final UUID targetId;
    private final UUID senderId;
    private final Mode mode;

    private long expireDate;

    public TeleportRequest(@NotNull Player sender, @NotNull Player target, @NotNull Mode mode, int timeOut) {
        this(sender.getUniqueId(), target.getUniqueId(), mode, timeOut);
    }

    public TeleportRequest(@NotNull UUID senderId, @NotNull UUID targetId, @NotNull Mode mode, int timeOut) {
        this.senderId = senderId;
        this.targetId = targetId;
        this.mode = mode;
        this.expireDate = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(timeOut, TimeUnit.SECONDS);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= this.expireDate;
    }

    public void setExpired() {
        this.expireDate = System.currentTimeMillis();
    }

    public boolean isSender(@NotNull String name) {
        return Players.getPlayer(name) == this.getSender();
        //return this.senderInfo.getName().equalsIgnoreCase(name);
    }

    public boolean isTarget(@NotNull String name) {
        return Players.getPlayer(name) == this.getTarget();
        //return this.targetInfo.getName().equalsIgnoreCase(name);
    }

    @Nullable
    public Player getSender() {
        return Bukkit.getPlayer(this.senderId);
    }

    @Nullable
    public Player getTarget() {
        return Bukkit.getPlayer(this.targetId);
    }

    @NotNull
    public UUID getSenderId() {
        return this.senderId;
    }

    @NotNull
    public UUID getTargetId() {
        return this.targetId;
    }

    @NotNull
    public Mode getMode() {
        return this.mode;
    }

    public long getExpireDate() {
        return this.expireDate;
    }
}

