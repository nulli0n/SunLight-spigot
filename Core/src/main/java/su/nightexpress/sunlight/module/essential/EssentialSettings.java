package su.nightexpress.sunlight.module.essential;

import org.bukkit.Material;
import org.bukkit.World;
import org.jspecify.annotations.NonNull;
import su.nightexpress.nightcore.configuration.AbstractConfig;
import su.nightexpress.nightcore.configuration.ConfigProperty;
import su.nightexpress.nightcore.configuration.ConfigTypes;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.Plugins;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.*;
import static su.nightexpress.sunlight.module.essential.EssentialPlaceholders.*;

public class EssentialSettings extends AbstractConfig {

    public final ConfigProperty<List<String>> broadcastFormat = this.addProperty(ConfigTypes.STRING_LIST,
        "Broadcast.Format",
        List.of("", YELLOW.and(BOLD).wrap("BROADCAST:"), "", GRAY.wrap(GENERIC_MESSAGE), ""),
        "Text Formations: " + URL_WIKI_TEXT
    );

    public final ConfigProperty<String> disposalTitle = this.addProperty(ConfigTypes.STRING, "Disposal.Title", BLACK
        .wrap("Disposal"));

    public final ConfigProperty<Integer> disposalSize = this.addProperty(ConfigTypes.INT, "Disposal.InventorySize", 36);


    private final ConfigProperty<Boolean> invulnerabilityEnabled = this.addProperty(ConfigTypes.BOOLEAN,
        "Invulnerability.Enabled",
        true,
        "Controls whether the Invulnerability feature is available to use.",
        "[*] Note: The Invulnerability feature uses vanilla 'invulnerable' NBT tag. Ensure that no other plugins depends on that tag to avoid possible conflicts/issues."
    );

    private final ConfigProperty<Set<String>> invulnerabilityDisabledWorlds = this.addProperty(
        ConfigTypes.STRING_SET_LOWER_CASE, "Invulnerability.Disabled-In-Worlds",
        Set.of("world_name", "other_world"),
        "List of worlds where the Invulnerability is disabled.",
        "[*] Note: It will reset player's 'invulnerable' NBT tag to 'false' when they are in specified worlds."
    );

    private final ConfigProperty<Boolean> invulnerabilityAllowDamagePlayers = this.addProperty(ConfigTypes.BOOLEAN,
        "Invulnerability.Allow-Damage-Players",
        false,
        "Controls whether players with Invulnerability can damage other players."
    );

    private final ConfigProperty<Boolean> invulnerabilityAllowDamageMobs = this.addProperty(ConfigTypes.BOOLEAN,
        "Invulnerability.Allow-Damage-Mobs",
        true,
        "Controls whether players with Invulnerability can damage mobs."
    );

    private final ConfigProperty<Boolean> invulnerabilityLockFoodLevel = this.addProperty(ConfigTypes.BOOLEAN,
        "Invulnerability.Lock-Food-Level",
        true,
        "Controls whether players with Invulnerability will keep their food level.");

    private final ConfigProperty<Boolean> invulnerabilityLockOxygenLevel = this.addProperty(ConfigTypes.BOOLEAN,
        "Invulnerability.Lock-Oxygen-Level",
        true,
        "Controls whether players with Invulnerability will keep their oxygen level when underwater.");


    public final ConfigProperty<Boolean> foodSaturationEnabled = this.addProperty(ConfigTypes.BOOLEAN,
        "FoodLevel.Restore.Saturation.Included",
        true,
        "Controls whether the 'foodlevel' command should also give some saturation."
    );

    public final ConfigProperty<Double> foodSaturationAmount = this.addProperty(ConfigTypes.DOUBLE,
        "FoodLevel.Restore.Saturation.Amount",
        5D,
        "Sets saturation amount given by the 'foodlevel' command."
    );

    public final ConfigProperty<Boolean> healthClearEffects = this.addProperty(ConfigTypes.BOOLEAN,
        "Health.Restore.ClearEffects",
        true,
        "Controls whether the 'health restore' command should remove all potion effects from player."
    );

    public final ConfigProperty<Integer> nearRadius = this.addProperty(ConfigTypes.INT, "Near.Radius", 200,
        "Sets default command radius."
    );

    public final ConfigProperty<List<String>> nearFormatNormal = this.addProperty(ConfigTypes.STRING_LIST,
        "Near.Format.Normal",
        Lists.newList(
            DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(32)),
            GRAY.wrap("There are " + SOFT_YELLOW.and(UNDERLINED).wrap(GENERIC_AMOUNT) + " players around you:"),
            " ",
            GENERIC_ENTRY,
            DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(32))
        ),
        "[*] Text Formations Guide: " + URL_WIKI_TEXT,
        "[*] Placeholders: '%s', '%s', '%s'".formatted(GENERIC_RADIUS, GENERIC_AMOUNT, Plugins.PLACEHOLDER_API)
    );

    public final ConfigProperty<List<String>> nearFormatOthers = this.addProperty(ConfigTypes.STRING_LIST,
        "Near.Format.Others",
        Lists.newList(
            DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(32)),
            GRAY.wrap("There are " + SOFT_YELLOW.and(UNDERLINED).wrap(GENERIC_AMOUNT) + " players around " + WHITE.wrap(
                PLAYER_DISPLAY_NAME) + ":"),
            " ",
            GENERIC_ENTRY,
            DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(32))
        ),
        "[*] Text Formations Guide: " + URL_WIKI_TEXT,
        "[*] Placeholders: '%s', '%s', '%s'".formatted(GENERIC_RADIUS, GENERIC_AMOUNT, Plugins.PLACEHOLDER_API)
    );

    public final ConfigProperty<String> nearEntryFormat = this.addProperty(ConfigTypes.STRING, "Near.Format.Entry",
        HEAD_HAT.apply(PLAYER_NAME) + " " + GRAY.wrap(PLAYER_PREFIX + SUGGEST_COMMAND.with("/ptp request " +
            PLAYER_NAME).wrap(SHOW_TEXT.with(GRAY.wrap("Click to send teleport request.")).wrap(WHITE.wrap(
                PLAYER_DISPLAY_NAME))) + PLAYER_SUFFIX + " " + DARK_GRAY.wrap("(" + GRAY.wrap(GENERIC_DISTANCE +
                    " blocks " + GENERIC_DIRECTION) + ")")),
        "Player entry format.",
        "[*] Text Formations Guide: " + URL_WIKI_TEXT,
        "[*] Placeholders: '%s', '%s', '%s'".formatted(GENERIC_DISTANCE, GENERIC_DIRECTION, Plugins.PLACEHOLDER_API)
    );

    public final ConfigProperty<Integer> nickMinLength = this.addProperty(ConfigTypes.INT, "Nick.Length.Min", 3);

    public final ConfigProperty<Integer> nickMaxLength = this.addProperty(ConfigTypes.INT, "Nick.Length.Max", 16);

    public final ConfigProperty<Set<String>> nickBannedWords = this.addProperty(ConfigTypes.STRING_SET,
        "Nick.Banned-Words", Lists.newSet("admin", "ass", "shit"));

    public final ConfigProperty<String> nickRegex = this.addProperty(ConfigTypes.STRING, "Nick.Regex-Pattern",
        "[a-zA-Zа-яА-Я0-9_\\s]*");

    public final ConfigProperty<List<String>> playerInfoFormat = this.addProperty(ConfigTypes.STRING_LIST,
        "PlayerInfo.Format",
        Lists.newList(
            DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(12)) + " " + GRAY.wrap(SOFT_YELLOW.wrap(PLAYER_NAME) +
                " Info") + " " + DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(12)),
            " ",
            DARK_GRAY.wrap("» ") + SPRITE_GUI.apply("server_list/ping_5") + GRAY.wrap(" Status: ") + GREEN.wrap(
                PLAYER_STATUS),
            DARK_GRAY.wrap("» ") + SPRITE_ITEMS.apply("item/clock_12") + GRAY.wrap(" Since: ") + WHITE.wrap(
                PLAYER_LAST_SEEN),
            " ",
            DARK_GRAY.wrap("» ") + GRAY.wrap("UUID: ") + COPY_TO_CLIPBOARD.with(PLAYER_UUID).wrap(SHOW_TEXT.with(GRAY
                .wrap("Click to copy!")).wrap(WHITE.wrap(PLAYER_UUID))),
            DARK_GRAY.wrap("» ") + GRAY.wrap("Name: ") + COPY_TO_CLIPBOARD.with(PLAYER_NAME).wrap(SHOW_TEXT.with(GRAY
                .wrap("Click to copy!")).wrap(WHITE.wrap(PLAYER_NAME))),
            DARK_GRAY.wrap("» ") + GRAY.wrap("Nickname: ") + COPY_TO_CLIPBOARD.with(PLAYER_DISPLAY_NAME).wrap(SHOW_TEXT
                .with(GRAY.wrap("Click to copy!")).wrap(WHITE.wrap(PLAYER_DISPLAY_NAME))),
            DARK_GRAY.wrap("» ") + GRAY.wrap("IP: ") + COPY_TO_CLIPBOARD.with(PLAYER_INET_ADDRESS).wrap(SHOW_TEXT.with(
                GRAY.wrap("Click to copy!")).wrap(WHITE.wrap(PLAYER_INET_ADDRESS))),
            " ",
            DARK_GRAY.wrap("» ") + SPRITE_ITEMS.apply("item/compass_15") + GRAY.wrap(" Location: ") + WHITE.wrap(RED
                .wrap(LOCATION_X) + " " + GREEN.wrap(LOCATION_Y) + " " + BLUE.wrap(LOCATION_Z) + " @ " +
                LOCATION_WORLD) + " " + RUN_COMMAND.with("/tppos " + LOCATION_X + " " + LOCATION_Y + " " + LOCATION_Z +
                    " " + LOCATION_WORLD).wrap(SOFT_YELLOW.wrap("[Teleport]")),

            DARK_GRAY.wrap("» ") + SPRITE_ITEM.apply(Material.NETHER_STAR) + GRAY.wrap(" Game Mode: ") + WHITE.wrap(
                PLAYER_GAME_MODE),
            DARK_GRAY.wrap("» ") + SPRITE_ITEM.apply(Material.FEATHER) + GRAY.wrap(" Can Fly: ") + PLAYER_CAN_FLY,
            DARK_GRAY.wrap("» ") + SPRITE_ITEM.apply(Material.EXPERIENCE_BOTTLE) + GRAY.wrap(
                " XP Level: ") + PLAYER_LEVEL,
            DARK_GRAY.wrap("» ") + SPRITE_GUI.apply("hud/food_full") + GRAY.wrap(" Hunger / Saturation: ") + WHITE.wrap(
                PLAYER_FOOD_LEVEL) + GRAY.wrap(" / ") + SOFT_ORANGE.wrap(PLAYER_SATURATION),
            DARK_GRAY.wrap("» ") + SPRITE_GUI.apply("hud/heart/full") + GRAY.wrap(" Health / Max. Health: ") + WHITE
                .wrap(PLAYER_HEALTH + "❤") + GRAY.wrap(" / ") + SOFT_RED.wrap(PLAYER_MAX_HEALTH + "❤"),
            DARK_GRAY.wrap("» ") + SPRITE_GUI.apply("mob_effect/invisibility") + GRAY.wrap(" Vanished: ") + WHITE.wrap(
                PLAYER_VANISHED),
            " ",
            DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(32))
        ),
        Plugins.PLACEHOLDER_API + " is supported here.",
        "[*] Text Formation Guide: " + URL_WIKI_TEXT
    );

    public final ConfigProperty<List<String>> staffRanks = this.addProperty(ConfigTypes.STRING_LIST,
        "Staff.IncludedRanks",
        List.of("admin", "moderator", "helper"),
        "Player ranks considered as staff."
    );

    public final ConfigProperty<List<String>> staffFormat = this.addProperty(ConfigTypes.STRING_LIST,
        "Staff.Format.List",
        List.of(
            DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(32)),
            GRAY.wrap("There are " + SOFT_RED.and(UNDERLINED).wrap(GENERIC_AMOUNT) + " staff players online:"),
            " ",
            GENERIC_ENTRY,
            DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(32))
        ),
        "[*] Text Formations Guide: " + URL_WIKI_TEXT,
        "[*] Placeholders: '%s'".formatted(GENERIC_AMOUNT)
    );

    public final ConfigProperty<String> staffEntryFormat = this.addProperty(ConfigTypes.STRING, "Staff.Format.Entry",
        HEAD_HAT.apply(PLAYER_NAME) + " " + GRAY.wrap(PLAYER_PREFIX + SUGGEST_COMMAND.with("/msg " + PLAYER_NAME + " ")
            .wrap(SHOW_TEXT.with(GRAY.wrap("Click to write private message.")).wrap(WHITE.wrap(
                PLAYER_DISPLAY_NAME))) + PLAYER_SUFFIX),
        "[*] Text Formations Guide: " + URL_WIKI_TEXT,
        "[*] Placeholders: '%s', '%s', '%s'".formatted(PLAYER_PREFIX, PLAYER_SUFFIX, Plugins.PLACEHOLDER_API)
    );

    public final ConfigProperty<Map<String, Long>> timeAliases = this.addProperty(ConfigTypes.forMapWithLowerKeys(
        ConfigTypes.LONG), "Time.Aliases",
        Map.of(
            "day", 1000L,
            "noon", 6000L,
            "sunset", 12000L,
            "night", 13000L,
            "midnight", 18000L,
            "sunrise", 23000L
        )
    );

    public final ConfigProperty<List<String>> timeDisplayFormat = this.addProperty(ConfigTypes.STRING_LIST,
        "Time.Show.Format", List.of(
            DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(12)) + " " + GRAY.wrap("Time Info") + " " + DARK_GRAY.and(
                STRIKETHROUGH).wrap("-".repeat(12)),
            DARK_GRAY.wrap("» ") + GRAY.wrap("World: " + WHITE.wrap(GENERIC_WORLD)),
            DARK_GRAY.wrap("» ") + GRAY.wrap("World Time: " + SOFT_YELLOW.wrap(GENERIC_TIME) + " (" + WHITE.wrap(
                GENERIC_TICKS + " ticks") + ")"),
            DARK_GRAY.wrap("» ") + GRAY.wrap("Server Time: " + SOFT_BLUE.wrap(GENERIC_GLOBAL)),
            DARK_GRAY.and(STRIKETHROUGH).wrap("-".repeat(32))
        ));


    public boolean isInvulnerabilityEnabled() {
        return this.invulnerabilityEnabled.get();
    }

    public boolean isInvulnerabilityAllowedInThisWorld(@NonNull World world) {
        return !this.invulnerabilityDisabledWorlds.get().contains(LowerCase.INTERNAL.apply(world.getName()));
    }

    public boolean isInvulnerabilityAllowsDamagePlayers() {
        return this.invulnerabilityAllowDamagePlayers.get();
    }

    public boolean isInvulnerabilityAllowsDamageMobs() {
        return this.invulnerabilityAllowDamageMobs.get();
    }

    public boolean isInvulnerabilityLocksFoodLevel() {
        return this.invulnerabilityLockFoodLevel.get();
    }

    public boolean isInvulnerabilityLocksOxygenLevel() {
        return this.invulnerabilityLockOxygenLevel.get();
    }
}
