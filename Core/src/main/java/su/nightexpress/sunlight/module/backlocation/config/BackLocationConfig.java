package su.nightexpress.sunlight.module.backlocation.config;

import org.bukkit.event.player.PlayerTeleportEvent;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.sunlight.module.backlocation.command.BackCommand;
import su.nightexpress.sunlight.module.backlocation.command.DeathBackCommand;

import java.util.Set;

public class BackLocationConfig {

    public static final ConfigValue<Boolean> PREVIOUS_ENABLED = ConfigValue.create("Location.Previous.Enabled",
        true,
        "Sets whether or not Previous Location feature is enabled.",
        "It will track player teleports to store previous location.",
        "Players can teleport to previous location using the '" + BackCommand.NODE + "' command.");

    public static final ConfigValue<Integer> PREVIOUS_EXPIRE_TIME = ConfigValue.create("Location.Previous.Expire_Time",
        3600,
        "Sets for how long (in seconds) previous location will last.");

    public static final ConfigValue<Double> PREVIOUS_MIN_DISTANCE_DIFFERENCE = ConfigValue.create("Location.Previous.Min_Distance_Difference",
        10D,
        "Sets minimal distance between current and destination locations to save it.");

    public static final ConfigValue<Set<String>> PREVIOUS_DISABLED_WORLDS = ConfigValue.create("Location.Previous.Disabled_Worlds",
        Lists.newSet("my_custom_world"),
        "When Previous Location stored in any of the worlds listed below, players won't be able to use it without the '" + BackLocationPerms.BYPASS_PREVIOUS_WORLDS.getName() + "' permission."
    );

    public static final ConfigValue<Set<PlayerTeleportEvent.TeleportCause>> PREVIOUS_DISABLED_CAUSES = ConfigValue.forSet("Location.Previous.Ignored_Causes",
        str -> StringUtil.getEnum(str, PlayerTeleportEvent.TeleportCause.class).orElse(null),
        (cfg, path, set) -> cfg.set(path, set.stream().map(Enum::name).toList()),
        Lists.newSet(
            PlayerTeleportEvent.TeleportCause.ENDER_PEARL
        ),
        "List of teleport causes that are ignored by Previous Location.",
        "This setting can be bypassed with '" + BackLocationPerms.BYPASS_PREVIOUS_CAUSES.getName() + "' permission",
        "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/player/PlayerTeleportEvent.TeleportCause.html");



    public static final ConfigValue<Boolean> DEATH_ENABLED = ConfigValue.create("Location.Death.Enabled",
        true,
        "Sets whether or not Death Location feature is enabled.",
        "It will track player deaths to store death locations.",
        "Players can teleport to death locations using the '" + DeathBackCommand.NODE + "' command."
    );

    public static final ConfigValue<Boolean> DEATH_RESET_ON_USE = ConfigValue.create("Location.Death.ResetOnUse",
        true,
        "Sets whether or not death location should be removed after use.");

    public static final ConfigValue<Integer> DEATH_EXPIRE_TIME = ConfigValue.create("Location.Death.Expire_Time",
        3600,
        "Sets for how long (in seconds) death location will last.");

    public static final ConfigValue<Set<String>> DEATH_DISABLED_WORLDS = ConfigValue.create("Location.Death.Disabled_Worlds",
        Lists.newSet("my_custom_world"),
        "When Death Location stored in any of the worlds listed below, players won't be able to use it without the '" + BackLocationPerms.BYPASS_DEATH_WORLDS.getName() + "' permission."
    );
}
