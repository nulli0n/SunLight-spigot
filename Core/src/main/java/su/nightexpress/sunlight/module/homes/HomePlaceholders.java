package su.nightexpress.sunlight.module.homes;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.placeholder.TypedPlaceholder;
import su.nightexpress.sunlight.module.homes.config.HomesLang;
import su.nightexpress.sunlight.module.homes.impl.Home;

public class HomePlaceholders {

    public static final String HOME_ID              = "%home_id%";
    public static final String HOME_NAME            = "%home_name%";
    public static final String HOME_OWNER           = "%home_owner%";
    public static final String HOME_TYPE            = "%home_type%";
    public static final String HOME_PUBLIC          = "%home_public%";
    public static final String HOME_INVITED_PLAYERS = "%home_invited_players%";
    public static final String HOME_FAVORITE        = "%home_favorite%";
    public static final String HOME_X               = "%home_location_x%";
    public static final String HOME_Y               = "%home_location_y%";
    public static final String HOME_Z               = "%home_location_z%";
    public static final String HOME_WORLD           = "%home_location_world%";

    @NotNull
    public static final TypedPlaceholder<Home> HOME = TypedPlaceholder.builder(Home.class)
        .with(HOME_ID, Home::getId)
        .with(HOME_NAME, Home::getName)
        .with(HOME_OWNER, home -> home.getOwner().name())
        .with(HOME_TYPE, home -> HomesLang.ENUM_HOME_TYPE.getLocalized(home.getType()))
        .with(HOME_PUBLIC, home -> CoreLang.STATE_YES_NO.get(home.isPublic()))
        .with(HOME_INVITED_PLAYERS, home -> String.valueOf(home.getInvitedPlayers().size()))
        .with(HOME_FAVORITE, home -> CoreLang.STATE_YES_NO.get(home.isFavorite()))
        .with("%home_is_default%", home -> CoreLang.STATE_YES_NO.get(home.isFavorite())) // old
        .with("%home_is_respawn_point%", home -> CoreLang.STATE_YES_NO.get(home.isFavorite())) // old
        .with(HOME_X, home -> NumberUtil.format(home.getBlockPos().getX()))
        .with(HOME_Y, home -> NumberUtil.format(home.getBlockPos().getY()))
        .with(HOME_Z, home -> NumberUtil.format(home.getBlockPos().getZ()))
        .with(HOME_WORLD, home -> home.isActive() ? LangAssets.get(home.getWorld()) : home.getWorldName())
        .build();
}
