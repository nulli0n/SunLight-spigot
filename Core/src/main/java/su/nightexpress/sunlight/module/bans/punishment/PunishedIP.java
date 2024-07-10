package su.nightexpress.sunlight.module.bans.punishment;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.bans.util.Placeholders;
import su.nightexpress.sunlight.utils.SunUtils;

import java.util.UUID;

public class PunishedIP extends PunishData {

    private final String address;

    public PunishedIP(@NotNull UUID id, @NotNull String address, @NotNull String reason, @NotNull String admin, long createDate, long expireDate) {
        super(id, reason, admin, createDate, expireDate);
        this.address = address;

        this.placeholderMap.add(Placeholders.forPunishedIP(this));
    }

    @Override
    public boolean isApplicable(@NotNull Player player) {
        return SunUtils.getRawAddress(player).equalsIgnoreCase(this.getAddress());
    }

    @NotNull
    public String getAddress() {
        return address;
    }
}
