package su.nightexpress.sunlight.module.playerwarps.dialog;

import su.nightexpress.sunlight.dialog.DialogKey;
import su.nightexpress.sunlight.module.playerwarps.PlayerWarp;
import su.nightexpress.sunlight.module.playerwarps.dialog.impl.PlayerWarpFeaturingDialog;
import su.nightexpress.sunlight.module.playerwarps.menu.WarpsListData;

public class PlayerWarpsDialogKeys {

    public static final DialogKey<PlayerWarp>                     WARP_NAME        = new DialogKey<>("pwarp_name");
    public static final DialogKey<PlayerWarp>                     WARP_DESCRIPTION = new DialogKey<>("pwarp_description");
    public static final DialogKey<PlayerWarp>                     WARP_PRICE       = new DialogKey<>("pwarp_price");
    public static final DialogKey<WarpsListData>                  WARP_SEARCH      = new DialogKey<>("pwarp_search");
    public static final DialogKey<PlayerWarpFeaturingDialog.Data> WARP_FEATURING   = new DialogKey<>("pwarp_featuring");
    public static final DialogKey<PlayerWarp>                     WARP_CATEGORY    = new DialogKey<>("pwarp_category");
}
