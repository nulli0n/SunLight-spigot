package su.nightexpress.sunlight.module.bans.punishment;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.nightcore.util.placeholder.PlaceholderResolvable;

import java.util.UUID;

public abstract class AbstractPunishment implements PlaceholderResolvable {

    protected final PunishmentData data;

    protected boolean active;
    protected boolean dirty;

    public AbstractPunishment(@NotNull PunishmentData data, boolean active) {
        this.data = data;
        this.active = active;
    }

    public abstract boolean isApplicable(@NotNull Player player);

    public boolean isNewer(@NotNull AbstractPunishment other) {
        return this.getCreationDate() > other.getCreationDate();
    }

    public boolean isLonger(@NotNull AbstractPunishment other) {
        if (this.isPermanent() && other.isPermanent()) return false;

        return this.isPermanent() || this.getExpirationDate() > other.getExpirationDate();
    }

    public void markDirty() {
        this.dirty = true;
    }

    public void markClean() {
        this.dirty = false;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public boolean isValid() {
        return !this.isExpired() && this.isActive();
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean isExpired() {
        return !this.isPermanent() && TimeUtil.isPassed(this.getExpirationDate());
    }

    public boolean isPermanent() {
        return this.getExpirationDate() < 0;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @NotNull
    public abstract String getName();

    @NotNull
    public UUID getId() {
        return this.data.id();
    }

    @NotNull
    public PunishmentType getType() {
        return this.data.type();
    }

    @NotNull
    public String getReason() {
        return this.data.reason();
    }

    @NotNull
    public String getWho() {
        return this.data.who();
    }

    public long getDuration() {
        return this.data.duration();
    }

    public long getRemainingDuration() {
        return this.getExpirationDate() - System.currentTimeMillis();
    }

    public long getCreationDate() {
        return this.data.creationTimestamp();
    }

    public long getExpirationDate() {
        return this.data.expirationTimestamp();
    }
}
