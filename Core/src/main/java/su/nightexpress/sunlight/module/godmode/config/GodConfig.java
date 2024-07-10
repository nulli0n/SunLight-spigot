package su.nightexpress.sunlight.module.godmode.config;

import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.util.Lists;

import java.util.Set;

public class GodConfig {

    public static final ConfigValue<Set<String>> DISABLED_WORLDS = ConfigValue.create("Settings.Disabled_Worlds",
        Lists.newSet("world_name", "other_world"),
        "Worlds, where GodMode is not available.",
        "This setting can be bypasses with the '" + GodPerms.BYPASS_WORLDS.getName() + "' permission."
    );

    public static final ConfigValue<Boolean> OUT_DAMAGE_PLAYERS = ConfigValue.create("Settings.OutgoingDamage.Players",
        false,
        "Sets whether or not players in GodMode can damage other players."
    );

    public static final ConfigValue<Boolean> OUT_DAMAGE_MOBS = ConfigValue.create("Settings.OutgoingDamage.Mobs",
        true,
        "Sets whether or not players in GodMode can damage mobs."
    );
}
