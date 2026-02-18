package su.nightexpress.sunlight.module.spawns.config;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.configuration.AbstractConfig;
import su.nightexpress.nightcore.configuration.ConfigProperty;
import su.nightexpress.nightcore.configuration.ConfigTypes;
import su.nightexpress.sunlight.SLPlaceholders;

public class SpawnsSettings extends AbstractConfig {

    private final ConfigProperty<Boolean> overridePlayerRespawnLocation = this.addProperty(ConfigTypes.BOOLEAN, "Settings.Override-Player-Respawn-Location",
        false,
        ""
    );

    private final ConfigProperty<String> defaultSpawnId = this.addProperty(ConfigTypes.STRING, "Settings.Default_Spawn",
        SLPlaceholders.DEFAULT,
        "Sets spawn used by default.",
        "If no other spawn is set, specified or available, the default one will be used.",
        "Examples:",
        "- When player types /spawn command without spawn name.",
        "",
        "[*] Players always has access to the default spawn."
    );

    private final ConfigProperty<Boolean> newPlayersSpawnEnabled = this.addProperty(ConfigTypes.BOOLEAN, "Teleport_New_Players.Enabled",
        true,
        "Sets whether or not players joined the first time should be teleported to a certain spawn."
    );

    private final ConfigProperty<String> newPlayersSpawnId = this.addProperty(ConfigTypes.STRING, "Teleport_New_Players.SpawnId",
        SLPlaceholders.DEFAULT,
        "Sets spawn ID for new players teleportation.",
        "Spawn permission check will be ignored for new players."
    );

    public boolean isOverridePlayerRespawnLocation() {
        return this.overridePlayerRespawnLocation.get();
    }

    @NotNull
    public String getDefaultSpawnId() {
        return this.defaultSpawnId.get();
    }

    public boolean isNewPlayersSpawnEnabled() {
        return this.newPlayersSpawnEnabled.get();
    }

    @NotNull
    public String getNewPlayersSpawnId() {
        return this.newPlayersSpawnId.get();
    }
}
