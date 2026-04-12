package su.nightexpress.sunlight.module.homes.dialog;

import su.nightexpress.nightcore.ui.dialog.wrap.DialogKey;
import su.nightexpress.sunlight.module.homes.impl.Home;

public class HomeDialogKeys {

    private HomeDialogKeys() {

    }

    public static final DialogKey<Home> HOME_NAME               = new DialogKey<>("home_name");
    public static final DialogKey<Home> HOME_INVITE_PLAYER_NAME = new DialogKey<>("home_invite_player_name");
    public static final DialogKey<Home> HOME_DELETION           = new DialogKey<>("home_deletion");

}
