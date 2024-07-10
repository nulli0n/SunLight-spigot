package su.nightexpress.sunlight.module.kits.config;

import su.nightexpress.nightcore.config.ConfigValue;

public class KitsConfig {

    public static final ConfigValue<Boolean> BIND_ITEMS_TO_PLAYERS = ConfigValue.create("Bind_Items_To_Players",
        false,
        "When enabled, all items a player receives from a kit will be bound to that player.",
        "Players can not use/pick up items that are bound to other players."
    );

    public static final ConfigValue<Boolean> GUI_HIDE_NO_PERMISSION = ConfigValue.create("GUI.Hide_No_Permission",
        false,
        "Sets whether or not kits with permission requirement enabled should be hidden from",
        "players that don't have permission to those kits."
    );
}
