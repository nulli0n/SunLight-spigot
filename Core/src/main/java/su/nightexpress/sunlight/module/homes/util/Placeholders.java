package su.nightexpress.sunlight.module.homes.util;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.homes.config.HomesLang;
import su.nightexpress.sunlight.module.homes.impl.Home;
import su.nightexpress.sunlight.utils.UserInfo;

public class Placeholders extends su.nightexpress.sunlight.Placeholders {

    public static final String HOME_ID               = "%home_id%";
    public static final String HOME_NAME             = "%home_name%";
    public static final String HOME_OWNER            = "%home_owner%";
    public static final String HOME_TYPE             = "%home_type%";
    public static final String HOME_INVITED_PLAYERS  = "%home_invited_players%";
    public static final String HOME_IS_RESPAWN_POINT = "%home_is_respawn_point%";
    public static final String HOME_IS_DEFAULT       = "%home_is_default%";
    public static final String HOME_LOCATION_X       = "%home_location_x%";
    public static final String HOME_LOCATION_Y       = "%home_location_y%";
    public static final String HOME_LOCATION_Z       = "%home_location_z%";
    public static final String HOME_LOCATION_WORLD   = "%home_location_world%";

    @NotNull
    public static PlaceholderMap forHome(@NotNull Home home) {
        return new PlaceholderMap()
            .add(HOME_ID, home::getId)
            .add(HOME_NAME, home::getName)
            .add(HOME_OWNER, () -> home.getOwner().getName())
            .add(HOME_TYPE, () -> HomesLang.HOME_TYPE.getLocalized(home.getType()))
            .add(HOME_INVITED_PLAYERS, () -> String.join(",", home.getInvitedPlayers().stream().map(UserInfo::getName).toList()))
            .add(HOME_IS_DEFAULT, () -> Lang.getYesOrNo(home.isDefault()))
            .add(HOME_IS_RESPAWN_POINT, () -> Lang.getYesOrNo(home.isRespawnPoint()))
            .add(HOME_LOCATION_X, () -> NumberUtil.format(home.getLocation().getX()))
            .add(HOME_LOCATION_Y, () -> NumberUtil.format(home.getLocation().getY()))
            .add(HOME_LOCATION_Z, () -> NumberUtil.format(home.getLocation().getZ()))
            .add(HOME_LOCATION_WORLD, () -> {
                return home.getLocation().getWorld() == null ? "null" : LangAssets.get(home.getLocation().getWorld());
            });
    }
}
