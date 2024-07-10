package su.nightexpress.sunlight.module.worlds.config;

import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.sunlight.module.worlds.util.Placeholders;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WorldsConfig {

    public static final Set<InventoryType> INVENTORY_SPLIT_TYPES = Set.of(InventoryType.PLAYER, InventoryType.ENDER_CHEST);

    public static final ConfigValue<Boolean> INVENTORY_SPLIT_ENABLED = ConfigValue.create("Inventory_Split.Enabled",
        false,
        "When enabled, players will have different inventory based on world groups."
    );

    public static final ConfigValue<Set<InventoryType>> INVENTORY_SPLIT_INVENTORIES = ConfigValue.forSet("Inventory_Split.Affected_Inventories",
        (raw) -> StringUtil.getEnum(raw, InventoryType.class).orElse(null),
        (cfg, path, set) -> cfg.set(path, set.stream().map(Enum::name).toList()),
        () -> INVENTORY_SPLIT_TYPES,
        "List of Inventory Types that are affected by the Inventory Split feature.",
        "Allowed values: " + INVENTORY_SPLIT_TYPES.stream().map(Enum::name).collect(Collectors.joining(", "))
    );

    public static final ConfigValue<Map<String, Set<String>>> INVENTORY_SPLIT_WORLD_GROUPS = ConfigValue.forMap("Inventory_Split.World_Groups",
        (cfg, path, group) -> cfg.getStringSet(path + "." + group),
        (cfg, path, map) -> map.forEach((group, set) -> cfg.set(path + "." + group, set)),
        () -> {
            Map<String, Set<String>> map = new HashMap<>();
            map.put("survival", Lists.newSet("world", "world_nether", "world_the_end"));
            map.put("creative", Lists.newSet("world_creative"));
            return map;
        },
        "Here you can create custom world groups for Inventory Split feature.",
        "Inventories are saved per each group. Different world group = different player inventory.",
        "All worlds from a group uses the same inventory."
    );

    public static final ConfigValue<Boolean> COMMAND_BLOCKER_ENABLED = ConfigValue.create("Command_Blocker.Enabled",
        false,
        "When enabled, prevents certain commands from being used in certain worlds.");

    public static final ConfigValue<Map<String, Set<String>>> COMMAND_BLOCKER_COMMANDS = ConfigValue.forMap("Command_Blocker.World_Commands",
        (cfg, path, world) -> cfg.getStringSet(path + "." + world),
        (cfg, path, map) -> map.forEach((world, set) -> cfg.set(path + "." + world, set)),
        () -> {
            Map<String, Set<String>> map = new HashMap<>();
            map.put("world_example_1", Lists.newSet("god", "fly"));
            map.put("world_example_2", Lists.newSet("heal", "feed"));
            return map;
        },
        "Here you can define which commands are disabled in which worlds.",
        "This setting can be bypassed with the '" + WorldsPerms.BYPASS_COMMANDS.getName() + "' permission."
    );

    public static final ConfigValue<Set<String>> NO_FLY_WORLDS = ConfigValue.create("NoFlyWorlds",
        Lists.newSet("world1", "world2"),
        "List of worlds where players can not fly without the '" + WorldsPerms.BYPASS_FLY.getName() + "' permission."
    );

    public static final ConfigValue<Boolean> AUTO_RESET_NOTIFICATION_ENABLED = ConfigValue.create("AutoWipe.Notification.Enabled",
        true,
        "Sets whether or not players will get a notification about an upcoming world auto-reset.");

    public static final ConfigValue<Long> AUTO_RESET_NOTIFICATION_THRESHOLD = ConfigValue.create("AutoWipe.Notification.Threshold",
        86400L,
        "Sets minimal threshold (in seconds) for notifications to work.",
        "When next world's auto-reset time left (in seconds) is smaller or equals to this amount, notification will start to appear.",
        "[Default is 86400 (1 day)]");

    public static final ConfigValue<Long> AUTO_RESET_NOTIFICATION_INTERVAL = ConfigValue.create("AutoWipe.Notification.Interval",
        3600L,
        "Sets world's auto-reset notifications interval (in seconds).",
        "[Default is 3600 (1 hour)]");

    public static final ConfigValue<Boolean> UNLOAD_MOVE_PLAYERS_TO_SPAWN_ENABLED = ConfigValue.create("AutoWipe.MovePlayersOut.ToSpawn.Enabled",
        true,
        "When enabled, on world unload, teleports all players in the world to a certain Spawn point from the Spawns module.",
        "Otherwise teleports players to the first available world."
    );

    public static final ConfigValue<String> UNLOAD_MOVE_PLAYERS_TO_SPAWN_NAME = ConfigValue.create("AutoWipe.MovePlayersOut.ToSpawn.Name",
        Placeholders.DEFAULT,
        "Sets Spawn point name for player teleport if enabled.",
        "If invalid Spawn point provided, teleports players to the first available world."
    );

    public static boolean isInventoryAffected(@NotNull InventoryType inventoryType) {
        return INVENTORY_SPLIT_INVENTORIES.get().contains(inventoryType);
    }
}
