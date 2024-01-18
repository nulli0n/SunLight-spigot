package su.nightexpress.sunlight.module.spawns.config;

import su.nexmedia.engine.api.config.JOption;

public class SpawnsConfig {

    public static final JOption<Boolean> RESPECT_PLAYER_BED_HOME = JOption.create("Global.Respect_Player_Beds",
        true,
        "Sets whether or not player's beds should be respected on player deaths, so",
        "they won't be teleported to spawn(s) set as respawn points.");
}
