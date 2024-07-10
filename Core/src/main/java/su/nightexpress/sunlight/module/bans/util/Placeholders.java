package su.nightexpress.sunlight.module.bans.util;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.bans.punishment.PunishData;
import su.nightexpress.sunlight.module.bans.punishment.PunishedIP;
import su.nightexpress.sunlight.module.bans.punishment.PunishedPlayer;
import su.nightexpress.sunlight.utils.SunUtils;

public class Placeholders extends su.nightexpress.sunlight.Placeholders {

    public static final String PUNISHMENT_TYPE            = "%punishment_type%";
    public static final String PUNISHMENT_TARGET          = "%punishment_user%";
    public static final String PUNISHMENT_REASON          = "%punishment_reason%";
    public static final String PUNISHMENT_PUNISHER        = "%punishment_punisher%";
    public static final String PUNISHMENT_CREATION_DATE   = "%punishment_date_created%";
    public static final String PUNISHMENT_EXPIRATION_DATE = "%punishment_date_expired%";
    public static final String PUNISHMENT_EXPIRES_IN      = "%punishment_expires_in%";

    @NotNull
    public static PlaceholderMap forPunishData(@NotNull PunishData punishData) {
        return new PlaceholderMap()
            .add(PUNISHMENT_REASON, punishData.getReason())
            .add(PUNISHMENT_PUNISHER, () -> SunUtils.getSenderName(punishData.getAdmin()))
            .add(PUNISHMENT_EXPIRES_IN, () -> !punishData.isPermanent() ? TimeUtil.formatDuration(punishData.getExpireDate()) : Lang.OTHER_NEVER.getString())
            .add(PUNISHMENT_CREATION_DATE, () -> SunUtils.formatDate(punishData.getCreateDate()))
            .add(PUNISHMENT_EXPIRATION_DATE, () -> !punishData.isPermanent() ? SunUtils.formatDate(punishData.getExpireDate()) : Lang.OTHER_NEVER.getString())
        ;
    }

    @NotNull
    public static PlaceholderMap forPunishedPlayer(@NotNull PunishedPlayer punishedPlayer) {
        return new PlaceholderMap()
            .add(PUNISHMENT_TARGET, () -> SunUtils.getSenderName(punishedPlayer.getPlayerName()))
            ;
    }

    @NotNull
    public static PlaceholderMap forPunishedIP(@NotNull PunishedIP punishedIP) {
        return new PlaceholderMap()
            .add(PUNISHMENT_TARGET, punishedIP.getAddress())
            ;
    }
}
