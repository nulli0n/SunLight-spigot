package su.nightexpress.sunlight.module.spawns.dialog;

import su.nightexpress.nightcore.ui.dialog.wrap.DialogKey;
import su.nightexpress.sunlight.module.spawns.Spawn;

public class SpawnsDialogKeys {

    private SpawnsDialogKeys() {
    }


    public static final DialogKey<Spawn> SPAWN_NAME          = new DialogKey<>("spawn_name");
    public static final DialogKey<Spawn> SPAWN_PRIORITY      = new DialogKey<>("spawn_priority");
    public static final DialogKey<Spawn> SPAWN_LOGIN_RULES   = new DialogKey<>("spawn_login_rules");
    public static final DialogKey<Spawn> SPAWN_RESPAWN_RULES = new DialogKey<>("spawn_respawn_rules");
}
