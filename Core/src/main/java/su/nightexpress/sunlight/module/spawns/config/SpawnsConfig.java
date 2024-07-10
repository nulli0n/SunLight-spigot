package su.nightexpress.sunlight.module.spawns.config;

import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.sunlight.module.spawns.util.Placeholders;

public class SpawnsConfig {

    public static final ConfigValue<Boolean> RESPECT_PLAYER_RESPAWN_LOCATION = ConfigValue.create("Global.Respect_Player_Beds",
        true,
        "When enabled, players with respawn location (a bed or an respawn anchor) won't teleported to a spawn on death."
    );

    public static final ConfigValue<String> DEFAULT_SPAWN = ConfigValue.create("Settings.Default_Spawn",
        Placeholders.DEFAULT,
        "Sets spawn used by default.",
        "If no other spawn is set, specified or available, the default one will be used.",
        "Examples:",
        "- When player types /spawn command without spawn name.",
        "",
        "[*] Players always has access to the default spawn."
    );

    public static final ConfigValue<Boolean> NEWBIE_TELEPORT_ENABLED = ConfigValue.create("Teleport_New_Players.Enabled",
        true,
        "Sets whether or not players joined the first time should be teleported to a certain spawn."
    );

    public static final ConfigValue<String> NEWBIE_TELEPORT_TARGET = ConfigValue.create("Teleport_New_Players.SpawnId",
        Placeholders.DEFAULT,
        "Sets spawn ID for new players teleportation.",
        "Spawn permission check will be ignored for new players."
    );
}
