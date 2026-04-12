package su.nightexpress.sunlight.module.warps.dialog;

import su.nightexpress.nightcore.ui.dialog.wrap.DialogKey;
import su.nightexpress.sunlight.module.warps.Warp;

public class WarpsDialogKeys {

    private WarpsDialogKeys() {
    }

    public static final DialogKey<Warp> WARP_NAME        = new DialogKey<>("warp_name");
    public static final DialogKey<Warp> WARP_DESCRIPTION = new DialogKey<>("warp_description");
    public static final DialogKey<Warp> WARP_COMMAND     = new DialogKey<>("warp_command");
    public static final DialogKey<Warp> WARP_SLOTS       = new DialogKey<>("warp_slots");

}
