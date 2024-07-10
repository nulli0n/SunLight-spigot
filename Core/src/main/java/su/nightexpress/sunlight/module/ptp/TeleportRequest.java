package su.nightexpress.sunlight.module.ptp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.sunlight.utils.UserInfo;

import java.util.concurrent.TimeUnit;

public class TeleportRequest {

    private final UserInfo targetInfo;
    private final UserInfo senderInfo;
    private final Mode     mode;

    private long expireDate;

    public TeleportRequest(@NotNull Player sender, @NotNull Player target, @NotNull Mode mode, int timeOut) {
        this(new UserInfo(sender), new UserInfo(target), mode, timeOut);
    }

    public TeleportRequest(@NotNull UserInfo senderInfo, @NotNull UserInfo targetInfo, @NotNull Mode mode, int timeOut) {
        this.senderInfo = senderInfo;
        this.targetInfo = targetInfo;
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
        return this.senderInfo.getName().equalsIgnoreCase(name);
    }

    public boolean isTarget(@NotNull String name) {
        return this.targetInfo.getName().equalsIgnoreCase(name);
    }

    @Nullable
    public Player getSender() {
        return Bukkit.getPlayer(this.senderInfo.getId());
    }

    @Nullable
    public Player getTarget() {
        return Bukkit.getPlayer(this.targetInfo.getId());
    }

    @NotNull
    public UserInfo getSenderInfo() {
        return this.senderInfo;
    }

    @NotNull
    public UserInfo getTargetInfo() {
        return this.targetInfo;
    }

    @NotNull
    public Mode getMode() {
        return this.mode;
    }

    public long getExpireDate() {
        return this.expireDate;
    }
}

