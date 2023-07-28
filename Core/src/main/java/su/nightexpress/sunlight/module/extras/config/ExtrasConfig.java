package su.nightexpress.sunlight.module.extras.config;

import org.bukkit.event.entity.CreatureSpawnEvent;
import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.EngineUtils;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.module.extras.impl.chestsort.SortRule;

import java.util.*;
import java.util.stream.Collectors;

public class ExtrasConfig {

    public static final JOption<Boolean> KEEP_INVENTORY_ENABLED = JOption.create("KeepInventory.Enabled", true,
        "Sets whether or not Keep Inventory feature is enabled.");

    public static final JOption<List<String>> KEEP_INVENTORY_ITEMS_RANKS = JOption.create("KeepInventory.Items_Ranks",
        Arrays.asList("vip", "admin"),
        "List of permission groups that will save their items on death.");

    public static final JOption<List<String>> KEEP_INVENTORY_EXP_RANKS = JOption.create("KeepInventory.Exp_Ranks",
        Arrays.asList("vip", "admin"),
        "List of permission groups that will save their XP on death.");

    public static final JOption<Boolean> JOIN_COMMANDS_ENABLED = JOption.create("JoinCommands.Enabled", false,
        "Sets whether or not Join Commands feature is enabled.");

    public static final JOption<List<String>> JOIN_COMMANDS_FIRST = JOption.create("JoinCommands.FirstJoin",
        Collections.singletonList("broadcast Welcome new player: " + Placeholders.PLAYER_NAME + "!"),
        "List of commands to execute when player joined server for the first time.",
        "Use '" + Placeholders.PLAYER_NAME + "' for a player name.",
        "You can use " + EngineUtils.PLACEHOLDER_API + " here.");

    public static final JOption<List<String>> JOIN_COMMANDS_DEFAULT = JOption.create("JoinCommands.Default",
        Collections.emptyList(),
        "List of commands to execute when player joins the server.",
        "Use '" + Placeholders.PLAYER_NAME + "' for a player name.",
        "You can use " + EngineUtils.PLACEHOLDER_API + " here.");

    public static final JOption<Boolean> ANVIL_COLORS_ENABLED = JOption.create("Anvil_Colors.Enabled", true,
        "Sets whether or not Anvil Colors feature is enabled.",
        "Players with '" + ExtrasPerms.ANVILS_COLOR.getName() + "' permission will be able to use colors on anvils.");

    public static final JOption<Boolean> SIGN_COLORS_ENABLED = JOption.create("Sign_Colors.Enabled", true,
        "Sets whether or not Sign Colors feature is enabled.",
        "Players with '" + ExtrasPerms.SIGNS_COLOR.getName() + "' permission will be able to use colors on signs.");

    public static final JOption<Boolean> PHYSIC_EXPLOSIONS_ENABLED = JOption.create("Physic_Explosions.Enabled", true,
        "Sets whether or not Physic Explosions feature is enabled.",
        "This will make blocks flying away on explosions.");

    public static final JOption<Boolean> CHAIRS_ENABLED = JOption.create("Chairs.Enabled", true,
        "Sets whether or not Chairs feature is enabled.");

    public static final JOption<Boolean> CHAIRS_ALLOW_STAIRS = JOption.create("Chairs.Allow_Stairs", true,
        "Sets whether or not Stair blocks can be used as chairs.");

    public static final JOption<Boolean> CHAIRS_ALLOW_SLABS = JOption.create("Chairs.Allow_Stairs", true,
        "Sets whether or not Slab blocks can be used as chairs.");

    public static final JOption<Boolean> CHAIRS_ALLOW_CARPETS = JOption.create("Chairs.Allow_Carpets", true,
        "Sets whether or not Carpet blocks can be used as chairs.");

    public static final JOption<Boolean> CHEST_SORT_ENABLED = JOption.create("ChestSort.Enabled", true,
        "Sets whether or not Chest Sort feature is enabled.");

    public static final JOption<List<SortRule>> CHEST_SORT_RULES = new JOption<>("ChestSort.Rules",
        (cfg, path, def) -> {
            return cfg.getStringList(path).stream().map(str -> StringUtil.getEnum(str, SortRule.class).orElse(null))
                .filter(Objects::nonNull).collect(Collectors.toCollection(ArrayList::new));
        },
        Arrays.asList(SortRule.values()),
        "List of sorting order & rules.",
        "Available values: " + String.join(", ", CollectionsUtil.getEnumsList(SortRule.class))
    ).setWriter((cfg, path, list) -> cfg.set(path, list.stream().map(Enum::name).toList()));

    public static final JOption<Boolean> ANTI_FARM_ENDERMITE_MINECART = JOption.create("AntiFarm.Endermite_Minecart", true,
        "Sets whether or not Enderman farms using minecart with endermite will be disabled.");

    public static final JOption<Boolean> ANTI_FARM_AUTO_FISHING = JOption.create("AntiFarm.AutoFishing", true,
        "Sets whether or not Auto Fishing farms using note blocks (and not only them) will be disabled.");

    public static final JOption<Boolean> NERF_PHANTOMS_ENABLED = JOption.create("NerfPhantoms.Enabled",
        false,
        "Sets whether or not NerfPhantoms feature is enabled.");

    public static final JOption<Set<CreatureSpawnEvent.SpawnReason>> NERF_PHANTOMS_DISABLE_SPAWN = JOption.forSet("NerfPhantoms.Disable_Spawn",
        raw -> StringUtil.getEnum(raw, CreatureSpawnEvent.SpawnReason.class).orElse(null),
        Set.of(CreatureSpawnEvent.SpawnReason.NATURAL, CreatureSpawnEvent.SpawnReason.DEFAULT),
        "Sets a list of SpawnReasons to disable phantoms spawn from.",
        "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/entity/CreatureSpawnEvent.SpawnReason.html"
    ).setWriter((cfg, path, set) -> cfg.set(path, set.stream().map(Enum::name).toList()));

    public static final JOption<Double> NERF_PHANTOMS_DAMAGE_MODIFIER = JOption.create("NerfPhantoms.Damage_Modifier", 1D,
        "Sets phantom's damage modifier to players. PhantomDamage * DamageModifier = Final Damage.");

    public static final JOption<Double> NERF_PHANTOMS_HEALTH = JOption.create("NerfPhantoms.Health", 20D,
        "Replaces phantom's health with specified value.");
}
