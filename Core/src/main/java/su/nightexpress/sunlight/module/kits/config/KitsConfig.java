package su.nightexpress.sunlight.module.kits.config;

import su.nexmedia.engine.api.config.JOption;

public class KitsConfig {

    public static final JOption<Boolean> BIND_ITEMS_TO_PLAYERS = JOption.create("Bind_Items_To_Players", false,
        "When 'true', binds all kit items to a player, who used that kit.",
        "Items that are bound to certain player can not be used/picked up by others.");
}
