package su.nightexpress.sunlight.module.nerfphantoms.config;

import org.bukkit.event.entity.CreatureSpawnEvent;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.StringUtil;

import java.util.Set;

public class PhantomsConfig {

    public static final ConfigValue<Boolean> DISABLE_SPAWN_ENABLED = ConfigValue.create("DisableSpawn.Enabled",
        false,
        "Sets whether or not NerfPhantoms feature is enabled.");

    public static final ConfigValue<Set<CreatureSpawnEvent.SpawnReason>> DISABLE_SPAWN_REASONS = ConfigValue.forSet("DisableSpawn.Sources",
        raw -> StringUtil.getEnum(raw, CreatureSpawnEvent.SpawnReason.class).orElse(null),
        (cfg, path, set) -> cfg.set(path, set.stream().map(Enum::name).toList()),
        Lists.newSet(
            CreatureSpawnEvent.SpawnReason.NATURAL,
            CreatureSpawnEvent.SpawnReason.DEFAULT
        ),
        "List of disabled phantom spawn sources.",
        "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/entity/CreatureSpawnEvent.SpawnReason.html"
    );

    public static final ConfigValue<Boolean> DAMAGE_MODIFIER_ENABLED = ConfigValue.create("DamageModifier.Enabled",
        false,
        "Sets whether or not NerfPhantoms feature is enabled.");

    public static final ConfigValue<Double> DAMAGE_MODIFIER_VALUE = ConfigValue.create("DamageModifier.Value",
        1D,
        "Sets phantom's damage modifier to players. PhantomDamage * DamageModifier = Final Damage.");

    public static final ConfigValue<Boolean> HEALTH_MODIFIER_ENABLED = ConfigValue.create("HealthModifier.Enabled",
        false,
        "Sets whether or not NerfPhantoms feature is enabled.");

    public static final ConfigValue<Double> HEALTH_MODIFIER_VALUE = ConfigValue.create("HealthModifier.Value",
        20D,
        "Replaces phantom's health with specified value.");
}
