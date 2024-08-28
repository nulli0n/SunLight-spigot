package su.nightexpress.sunlight.core.cooldown;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.command.cooldown.CommandCooldown;
import su.nightexpress.sunlight.module.kits.Kit;
import su.nightexpress.sunlight.module.warps.impl.Warp;

public class CooldownInfo {

    private final CooldownType type;
    private final String objectId;
    private final long   expireDate;

    public CooldownInfo(@NotNull CooldownType type, @NotNull String objectId, long expireDate) {
        this.type = type;
        this.objectId = objectId.toLowerCase();
        this.expireDate = expireDate;
    }

    @NotNull
    public static CooldownInfo of(@NotNull Kit kit) {
        long expireDate = kit.getCooldown() < 0 ? -1L : System.currentTimeMillis() + kit.getCooldown() * 1000L;
        return new CooldownInfo(CooldownType.KIT, kit.getId(), expireDate);
    }

    @NotNull
    public static CooldownInfo of(@NotNull Warp warp) {
        return new CooldownInfo(CooldownType.WARP, warp.getId(), System.currentTimeMillis() + warp.getVisitCooldown() * 1000L);
    }

    @NotNull
    public static CooldownInfo of(@NotNull CommandCooldown command, int cooldown) {
        return new CooldownInfo(CooldownType.COMMAND, command.getId(), System.currentTimeMillis() + cooldown * 1000L);
    }

    public boolean isSimilar(@NotNull CooldownInfo other) {
        return this.isSimilar(other.getObjectId());
    }

    public boolean isSimilar(@NotNull String otherId) {
        return this.getObjectId().equalsIgnoreCase(otherId);
    }

    public boolean isPermanent() {
        return this.getExpireDate() < 0L;
    }

    public boolean isExpired() {
        return !this.isPermanent() && System.currentTimeMillis() >= this.getExpireDate();
    }

    @NotNull
    public CooldownType getType() {
        return type;
    }

    @NotNull
    public String getObjectId() {
        return objectId;
    }

    public long getExpireDate() {
        return expireDate;
    }
}
