package su.nightexpress.sunlight.module.rtp.config;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.configuration.AbstractConfig;
import su.nightexpress.nightcore.configuration.ConfigProperty;
import su.nightexpress.nightcore.configuration.ConfigType;
import su.nightexpress.nightcore.configuration.ConfigTypes;
import su.nightexpress.sunlight.module.rtp.RTPDefaults;
import su.nightexpress.sunlight.module.rtp.model.LookupRange;

import java.util.Map;

public class RTPSettings extends AbstractConfig {

    private static final ConfigType<LookupRange> RANGE_INFO_CONFIG_TYPE = ConfigType.of(
        LookupRange::read,
        FileConfig::set
    );

    private final ConfigProperty<Boolean> fallbackEnabled = this.addProperty(ConfigTypes.BOOLEAN, "World-Fallback.Enabled",
        true,
        "Controls whether to fallback to specified world if RTP is not configured for the player's world.");

    private final ConfigProperty<String> fallbackWorld = this.addProperty(ConfigTypes.STRING, "World-Fallback.World",
        "world",
        "World to fallback to if enabled.");

    private final ConfigProperty<Integer> lookupMaxAttempts = this.addProperty(ConfigTypes.INT, "Lookup.Max-Attempts",
        5,
        "Max amount of attempts to lookup for a valid location.");

    private final ConfigProperty<Boolean> lookupGeneratedChunksOnly = this.addProperty(ConfigTypes.BOOLEAN, "Lookup.Generated_Chunks_Only",
        true,
        "Sets whether to lookup generated chunks only.",
        "[*] Your world must be pre-generated for this feature to work."
    );

    private final ConfigProperty<Boolean> lookupLoadedChunksOnly = this.addProperty(ConfigTypes.BOOLEAN, "Lookup.Loaded_Chunks_Only",
        false,
        "Sets whether to lookup loaded chunks only.");

    private final ConfigProperty<Map<String, LookupRange>> lookupRanges = this.addProperty(ConfigTypes.forMapWithLowerKeys(RANGE_INFO_CONFIG_TYPE),
        "Lookup.Ranges",
        RTPDefaults.getDefaultRangeMap(),
        "Per-world RTP range configuration."
    );

    public boolean isFallbackEnabled() {
        return this.fallbackEnabled.get();
    }

    @NotNull
    public String getFallbackWorld() {
        return this.fallbackWorld.get();
    }

    public int getLookupMaxAttempts() {
        return this.lookupMaxAttempts.get();
    }

    public boolean isLookupGeneratedChunksOnly() {
        return this.lookupGeneratedChunksOnly.get();
    }

    public boolean isLookupLoadedChunksOnly() {
        return this.lookupLoadedChunksOnly.get();
    }

    @NotNull
    public Map<String, LookupRange> getLookupRangesMap() {
        return this.lookupRanges.get();
    }
}
