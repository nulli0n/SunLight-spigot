package su.nightexpress.sunlight.module.extras.config;

import org.bukkit.Material;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.module.extras.chestsort.SortRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ExtrasConfig {

    public static final ConfigValue<Boolean> KEEP_INVENTORY_ENABLED = ConfigValue.create("KeepInventory.Enabled",
        true,
        "Sets whether or not Keep Inventory feature is enabled.");

    public static final ConfigValue<List<String>> KEEP_INVENTORY_ITEMS_RANKS = ConfigValue.create("KeepInventory.Items_Ranks",
        Lists.newList("vip", "admin"),
        "Players from the following permission groups will keep their items on death.");

    public static final ConfigValue<List<String>> KEEP_INVENTORY_XP_RANKS = ConfigValue.create("KeepInventory.Exp_Ranks",
        Lists.newList("vip", "admin"),
        "Players from the following permission groups will keep their XP on death.");

    public static final ConfigValue<Boolean> JOIN_COMMANDS_ENABLED = ConfigValue.create("JoinCommands.Enabled",
        false,
        "Sets whether or not Join Commands feature is enabled.");

    public static final ConfigValue<List<String>> JOIN_COMMANDS_FIRST = ConfigValue.create("JoinCommands.FirstJoin",
        Lists.newList("broadcast Welcome new player: " + Placeholders.PLAYER_NAME + "!"),
        "List of commands to execute when player joined server for the first time.",
        "Use '" + Players.PLAYER_COMMAND_PREFIX + "' prefix to run command by a player.",
        "Use '" + Placeholders.PLAYER_NAME + "' for a player name.",
        "You can use " + Plugins.PLACEHOLDER_API + " here.");

    public static final ConfigValue<List<String>> JOIN_COMMANDS_DEFAULT = ConfigValue.create("JoinCommands.Default",
        Lists.newList(),
        "List of commands to execute when player joins the server and has played before.",
        "Use '" + Players.PLAYER_COMMAND_PREFIX + "' prefix to run command by a player.",
        "Use '" + Placeholders.PLAYER_NAME + "' for a player name.",
        "You can use " + Plugins.PLACEHOLDER_API + " here.");

    public static final ConfigValue<Boolean> ANVIL_COLORS_ENABLED = ConfigValue.create("Anvil_Colors.Enabled",
        true,
        "Sets whether or not Anvil Colors feature is enabled.",
        "Players with '" + ExtrasPerms.ANVILS_COLOR.getName() + "' permission will be able to use colors on anvils.");

    public static final ConfigValue<Boolean> SIGN_COLORS_ENABLED = ConfigValue.create("Sign_Colors.Enabled",
        true,
        "Sets whether or not Sign Colors feature is enabled.",
        "Players with '" + ExtrasPerms.SIGNS_COLOR.getName() + "' permission will be able to use colors on signs.");

    public static final ConfigValue<Boolean> PHYSIC_EXPLOSIONS_ENABLED = ConfigValue.create("Physic_Explosions.Enabled",
        true,
        "Sets whether or not Physic Explosions feature is enabled.",
        "This will make blocks flying away on explosions.");

    public static final ConfigValue<Boolean> CHAIRS_ENABLED = ConfigValue.create("Chairs.Enabled",
        true,
        "Sets whether or not Chairs feature is enabled.");

    public static final ConfigValue<Boolean> CHAIRS_ALLOW_STAIRS = ConfigValue.create("Chairs.Allow_Stairs",
        true,
        "Sets whether or not Stair blocks can be used as chairs."
    );

    public static final ConfigValue<Boolean> CHAIRS_ALLOW_SLABS = ConfigValue.create("Chairs.Allow_Slabs",
        true,
        "Sets whether or not Slab blocks can be used as chairs."
    );

    public static final ConfigValue<Boolean> CHAIRS_ALLOW_CARPETS = ConfigValue.create("Chairs.Allow_Carpets",
        true,
        "Sets whether or not Carpet blocks can be used as chairs."
    );

    public static final ConfigValue<Set<Material>> CHAIRS_CUSTOM_BLOCKS = ConfigValue.forSet("Chairs.Allowed_Blocks",
        BukkitThing::getMaterial,
        (cfg, path, set) -> cfg.set(path, set.stream().map(BukkitThing::toString).toList()),
        () -> Lists.newSet(Material.PLAYER_HEAD, Material.PUMPKIN, Material.MELON),
        "Here you can add your own chairs blocks."
    );

    public static final ConfigValue<Boolean> CHEST_SORT_ENABLED = ConfigValue.create("ChestSort.Enabled",
        true,
        "Sets whether or not Chest Sort feature is enabled.");

    public static final ConfigValue<List<SortRule>> CHEST_SORT_RULES = ConfigValue.create("ChestSort.Rules",
        (cfg, path, def) -> {
            return cfg.getStringList(path).stream().map(str -> StringUtil.getEnum(str, SortRule.class).orElse(null))
                .filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new));
        },
        (cfg, path, list) -> cfg.set(path, list.stream().map(Enum::name).toList()),
        () -> Lists.newList(SortRule.values()),
        "List of sorting order & rules.",
        "Available values: " + StringUtil.inlineEnum(SortRule.class, ", ")
    );

    public static final ConfigValue<Boolean> ANTI_FARM_ENDERMITE_MINECART = ConfigValue.create("AntiFarm.Endermite_Minecart",
        false,
        "Sets whether or not Enderman farms using minecart with endermite will be disabled.");

    public static final ConfigValue<Boolean> ANTI_FARM_AUTO_FISHING = ConfigValue.create("AntiFarm.AutoFishing",
        false,
        "Sets whether or not Auto Fishing farms using note blocks (and not only them) will be disabled.");
}
