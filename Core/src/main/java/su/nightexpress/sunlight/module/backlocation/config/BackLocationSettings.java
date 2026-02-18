package su.nightexpress.sunlight.module.backlocation.config;

import su.nightexpress.nightcore.configuration.AbstractConfig;
import su.nightexpress.nightcore.configuration.ConfigProperty;
import su.nightexpress.nightcore.configuration.ConfigTypes;

import java.util.EnumSet;
import java.util.Set;

import static org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class BackLocationSettings extends AbstractConfig {

    public final ConfigProperty<Boolean> cacheTeleports = this.addProperty(ConfigTypes.BOOLEAN, "Location.Previous.Enabled",
        true,
        "Controls whether module will track player teleportations to cache their pre-teleport locations.",
        "Disabling this setting will prevent players from accessing the /back command and their pre-teleport locations.");

    public final ConfigProperty<Integer> teleportCacheExpireTime = this.addProperty(ConfigTypes.INT, "Location.Previous.Expire_Time",
        3600,
        "Sets cache time (in seconds) for pre-teleport locations.");

    public final ConfigProperty<Integer> teleportMinDistanceDifference = this.addProperty(ConfigTypes.INT, "Location.Previous.Min_Distance_Difference",
        10,
        "Sets min. teleport distance to cache the pre-teleport location.",
        "This might be useful to prevent location cache override by accidient or small teleports.",
        "Teleporting to another world will ignore this setting."
    );

    public final ConfigProperty<Set<String>> teleportWorldBlacklist = this.addProperty(ConfigTypes.STRING_SET, "Location.Previous.Disabled_Worlds",
        Set.of("my_custom_world"),
        "Sets worlds to where players can't teleport back to the cached location unless they have the '%s' permission.".formatted(BackLocationPerms.BYPASS_PREVIOUS_WORLDS.getName())
    );

    public final ConfigProperty<EnumSet<TeleportCause>> ignoredTeleportCauses = this.addProperty(ConfigTypes.forEnumSet(TeleportCause.class),
        "Location.Previous.Ignored_Causes",
        EnumSet.of(
            TeleportCause.ENDER_PEARL
        ),
        "If a teleport was caused by any of the listed reasons, the pre-teleport location won't be cached.",
        "This might be useful to prevent location cache override in special cases, such as Ender Pearl teleports.",
        "Players with the '%s' permission will bypass this setting".formatted(BackLocationPerms.BYPASS_PREVIOUS_CAUSES.getName()),
        "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/player/PlayerTeleportEvent.TeleportCause.html");



    public final ConfigProperty<Boolean> cacheDeaths = this.addProperty(ConfigTypes.BOOLEAN, "Location.Death.Enabled",
        true,
        "Controls whether module will track player deaths to cache their death locations.",
        "Disabling this setting will prevent players from accessing the /deathback command and their death locations.");

    public final ConfigProperty<Boolean> resetDeathCacheOnUse = this.addProperty(ConfigTypes.BOOLEAN, "Location.Death.ResetOnUse",
        true,
        "Controls whether players can teleport to their death location only once per death.");

    public final ConfigProperty<Integer> deathCacheExpireTime = this.addProperty(ConfigTypes.INT, "Location.Death.Expire_Time",
        3600,
        "Sets cache time (in seconds) for death locations.");

    public final ConfigProperty<Set<String>> deathWorldBlacklist = this.addProperty(ConfigTypes.STRING_SET, "Location.Death.Disabled_Worlds",
        Set.of("my_custom_world"),
        "Sets worlds to where players can't teleport back to the death location unless they have the '%s' permission.".formatted(BackLocationPerms.BYPASS_DEATH_WORLDS.getName())
    );
}
