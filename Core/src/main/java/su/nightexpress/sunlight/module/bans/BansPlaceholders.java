package su.nightexpress.sunlight.module.bans;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.util.placeholder.TypedPlaceholder;
import su.nightexpress.nightcore.util.time.TimeFormatType;
import su.nightexpress.nightcore.util.time.TimeFormats;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.module.bans.punishment.AbstractPunishment;
import su.nightexpress.sunlight.module.bans.punishment.InetPunishment;
import su.nightexpress.sunlight.module.bans.punishment.PlayerPunishment;
import su.nightexpress.sunlight.SLUtils;

public class BansPlaceholders extends SLPlaceholders {

    public static final String GENERIC_REASON   = "%reason%";
    public static final String GENERIC_EXECUTOR = "%executor%";

    public static final String PUNISHMENT_TYPE            = "%punishment_type%";
    public static final String PUNISHMENT_TARGET          = "%punishment_user%";
    public static final String PUNISHMENT_REASON          = "%punishment_reason%";
    public static final String PUNISHMENT_WHO             = "%punishment_punisher%";
    public static final String PUNISHMENT_DURATION        = "%punishment_duration%";
    public static final String PUNISHMENT_CREATION_DATE   = "%punishment_date_created%";
    public static final String PUNISHMENT_EXPIRATION_DATE = "%punishment_date_expired%";
    public static final String PUNISHMENT_EXPIRES_IN      = "%punishment_expires_in%";

    @NotNull
    private static final TypedPlaceholder<AbstractPunishment> PUNISHMENT = TypedPlaceholder.builder(AbstractPunishment.class)
        .with(PUNISHMENT_REASON, AbstractPunishment::getReason)
        .with(PUNISHMENT_WHO, punishment -> SLUtils.getSenderName(punishment.getWho())) // TODO Better
        .with(PUNISHMENT_DURATION, punishment -> TimeFormats.formatAmount(punishment.getDuration(), TimeFormatType.LITERAL))
        .with(PUNISHMENT_EXPIRES_IN, punishment -> !punishment.isPermanent() ? TimeFormats.formatDuration(punishment.getExpirationDate(), TimeFormatType.LITERAL) : CoreLang.OTHER_NEVER.text())
        .with(PUNISHMENT_CREATION_DATE, punishment -> TimeFormats.formatDateTime(punishment.getCreationDate()))
        .with(PUNISHMENT_EXPIRATION_DATE, punishment -> !punishment.isPermanent() ? TimeFormats.formatDateTime(punishment.getExpirationDate()) : CoreLang.OTHER_NEVER.text())
        .build();

    @NotNull
    public static final TypedPlaceholder<PlayerPunishment> PLAYER_PUNISHMENT = TypedPlaceholder.builder(PlayerPunishment.class)
        .include(PUNISHMENT)
        .with(PUNISHMENT_TARGET, PlayerPunishment::getPlayerName)
        .build();

    @NotNull
    public static final TypedPlaceholder<InetPunishment> INET_PUNISHMENT = TypedPlaceholder.builder(InetPunishment.class)
        .include(PUNISHMENT)
        .with(PUNISHMENT_TARGET, InetPunishment::getRawAddress)
        .build();
}
