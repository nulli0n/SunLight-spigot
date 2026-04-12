package su.nightexpress.sunlight.module.kits.dialog;

import su.nightexpress.nightcore.ui.dialog.wrap.DialogKey;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.model.Kit;

public class KitDialogKeys {

    public static final DialogKey<KitsModule> KIT_CREATION    = new DialogKey<>("kit_creation");
    public static final DialogKey<Kit>        KIT_NAME        = new DialogKey<>("kit_name");
    public static final DialogKey<Kit>        KIT_DESCRIPTION = new DialogKey<>("kit_description");
    public static final DialogKey<Kit>        KIT_PRIORITY    = new DialogKey<>("kit_priority");
    public static final DialogKey<Kit>        KIT_COST        = new DialogKey<>("kit_cost");
    public static final DialogKey<Kit>        KIT_COOLDOWN    = new DialogKey<>("kit_cooldown");
    public static final DialogKey<Kit>        KIT_COMMANDS    = new DialogKey<>("kit_commands");
}
