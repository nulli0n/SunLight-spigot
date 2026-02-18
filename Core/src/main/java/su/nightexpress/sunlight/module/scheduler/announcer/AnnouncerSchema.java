package su.nightexpress.sunlight.module.scheduler.announcer;

import su.nightexpress.nightcore.configuration.ConfigProperty;
import su.nightexpress.nightcore.configuration.ConfigTypes;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.sunlight.SLPlaceholders;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AnnouncerSchema {

    public static final ConfigProperty<Integer> INTERVAL = ConfigProperty.of(ConfigTypes.INT, "Interval", 120,
        "Interval (in seconds) between messages."
    );

    public static final ConfigProperty<Boolean> RANDOM_ORDER = ConfigProperty.of(ConfigTypes.BOOLEAN, "RandomOrder", false,
        "Whether messages should go in random order. Otherwise the same as defined in the config."
    );

    public static final ConfigProperty<Set<String>> RANKS = ConfigProperty.of(ConfigTypes.STRING_SET_LOWER_CASE, "Ranks", Set.of(SLPlaceholders.WILDCARD),
        "Player ranks (permission group names) that are allowed to see this announcer messages."
    );

    public static final ConfigProperty<Map<String, List<String>>> TEXTS = ConfigProperty.of(ConfigTypes.forMapWithLowerKeys(ConfigTypes.STRING_LIST), "Texts", Collections.emptyMap(),
        "Announcer messages.",
        "Placeholders Available:",
        "%s - Player name.".formatted(CommonPlaceholders.PLAYER_NAME),
        "%s - Player display (custom) name.".formatted(CommonPlaceholders.PLAYER_DISPLAY_NAME),
        "%s - Player prefix.".formatted(CommonPlaceholders.PLAYER_PREFIX),
        "%s - Player suffix.".formatted(CommonPlaceholders.PLAYER_SUFFIX),
        "%s - https://wiki.placeholderapi.com/".formatted(Plugins.PLACEHOLDER_API)
    );
}
