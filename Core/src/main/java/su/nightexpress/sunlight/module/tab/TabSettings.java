package su.nightexpress.sunlight.module.tab;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.configuration.AbstractConfig;
import su.nightexpress.nightcore.configuration.ConfigProperty;
import su.nightexpress.nightcore.configuration.ConfigType;
import su.nightexpress.nightcore.configuration.ConfigTypes;
import su.nightexpress.nightcore.integration.permission.PermissionPlugins;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.module.tab.format.TabLayoutFormat;
import su.nightexpress.sunlight.module.tab.format.TabNameFormat;

import java.util.Map;

import static su.nightexpress.nightcore.util.placeholder.CommonPlaceholders.*;

public class TabSettings extends AbstractConfig {

    private static final ConfigType<TabLayoutFormat> TAB_LAYOUT_FORMAT_CONFIG_TYPE = ConfigType.of(
        TabLayoutFormat::read,
        FileConfig::set
    );

    private static final ConfigType<TabNameFormat> TAB_NAME_FORMAT_CONFIG_TYPE = ConfigType.of(
        TabNameFormat::read,
        FileConfig::set
    );

    private final ConfigProperty<Long> playerListUpdateInterval = this.addProperty(ConfigTypes.LONG, "PlayerList.Update-Interval",
        20L,
        "Sets the frequently (in game ticks) of selecting and displaying the most suitable format (based on rank, priority, and other parameters) for the player list and player name to the client (the player).",
        "[Asynchronous]",
        "[1 second = 20 ticks]",
        "[Default is 20 ticks]"
    );

    private final ConfigProperty<Long> playerListSortInterval = this.addProperty(ConfigTypes.LONG, "PlayerList.Sort-Interval",
        100L,
        "Sets the frequency (in game ticks) of sorting the player list based on the values in 'Rank-Order'.",
        "[Asynchronous]",
        "[1 second = 20 ticks]",
        "[Default is 100 ticks]"
    );

    private final ConfigProperty<Map<String, Integer>> playerListRankOrders = this.addProperty(ConfigTypes.forMapWithLowerKeys(ConfigTypes.INT),
        "PlayerList.Rank-Order",
        TabDefaults.getDefaultPlayerListRankOrders(),
        "Sets the display order of players in the player list based on their rank.",
        "Players with the highest values appear at the top of the list.",
        "For players belonging to multiple groups (ranks), the highest available value is used.",
        "For players who do not belong to any group (rank) listed here, the '%s' value is used if present; otherwise, it defaults to 0.".formatted(DEFAULT),
        "",
        "[*] Requires %s OR %s with a compatible Permissions plugin for the feature to work.".formatted(PermissionPlugins.LUCK_PERMS, PermissionPlugins.VAULT)
    );

    private final ConfigProperty<Map<String, TabLayoutFormat>> playerListLayoutFormats = this.addProperty(ConfigTypes.forMapWithLowerKeys(TAB_LAYOUT_FORMAT_CONFIG_TYPE),
        "PlayerList.Layout-Format",
        TabDefaults.getDefaultPlayerListFormats(),
        "Here you can create your own, custom player list formats.",
        "",
        "[ SETTINGS DESCRIPTION ]",
        "├── Priority:",
        "│     -> Sets format priority. When multiple formats are available, the one with the highest priority is used.",
        "├── Worlds:",
        "│     -> List of worlds, where this format is available. Add '%s' to allow all worlds.".formatted(SLPlaceholders.WILDCARD),
        "├── Groups:",
        "│     -> List of ranks (permission groups) to which this format is available. Add '%s' to allow all ranks.".formatted(SLPlaceholders.WILDCARD),
        "└── Header + Footer:",
        "      -> Sets the text displayed at top (Header) and bottom (Footer) of the player list.",
        "      [>] Text Formations: " + SLPlaceholders.URL_WIKI_TEXT,
        "      [>] Placeholders Available:",
        "          - %s - include dynamic text (animation) from the '%s'.".formatted(SLPlaceholders.ANIMATION.apply("[name]"), TabFiles.FILE_ANIMATIONS),
        "          - %s - Player name.".formatted(PLAYER_NAME),
        "          - %s - Player display (custom) name.".formatted(PLAYER_DISPLAY_NAME),
        "          - %s - Player prefix.".formatted(PLAYER_PREFIX),
        "          - %s - Player suffix.".formatted(PLAYER_SUFFIX),
        "          - %s - Player world.".formatted(PLAYER_WORLD),
        "          - %s - https://wiki.placeholderapi.com/".formatted(Plugins.PLACEHOLDER_API),
        "",
        "[*] Requires %s OR %s with a compatible Permissions plugin for the feature to work properly.".formatted(PermissionPlugins.LUCK_PERMS, PermissionPlugins.VAULT)
    );

    private final ConfigProperty<Map<String, TabNameFormat>> playerListNameFormats = this.addProperty(ConfigTypes.forMapWithLowerKeys(TAB_NAME_FORMAT_CONFIG_TYPE),
        "PlayerList.Name-Format",
        TabDefaults.getDefaultListNameFormats(),
        "Here you can create your own, custom player name formats for the player list.",
        "",
        "[ SETTINGS DESCRIPTION ]",
        "├── Priority:",
        "│     -> Sets format priority. When multiple formats are available, the one with the highest priority is used.",
        "├── Ranks:",
        "│     -> List of ranks (permission groups) to which this format is available. Add '%s' to allow all ranks.".formatted(SLPlaceholders.WILDCARD),
        "└── Format:",
        "      -> Sets the text displayed in the player list for the player.",
        "      [>] Text Formations: " + SLPlaceholders.URL_WIKI_TEXT,
        "      [>] Placeholders Available:",
        "          - %s - include dynamic text (animation) from the '%s'.".formatted(SLPlaceholders.ANIMATION.apply("[name]"), TabFiles.FILE_ANIMATIONS),
        "          - %s - Player name.".formatted(PLAYER_NAME),
        "          - %s - Player display (custom) name.".formatted(PLAYER_DISPLAY_NAME),
        "          - %s - Player prefix.".formatted(PLAYER_PREFIX),
        "          - %s - Player suffix.".formatted(PLAYER_SUFFIX),
        "          - %s - Player world.".formatted(PLAYER_WORLD),
        "          - %s - https://wiki.placeholderapi.com/".formatted(Plugins.PLACEHOLDER_API),
        "",
        "[*] Requires %s OR %s with a compatible Permissions plugin for the feature to work properly.".formatted(PermissionPlugins.LUCK_PERMS, PermissionPlugins.VAULT)
    );

    public long getPlayerListUpdateInterval() {
        return this.playerListUpdateInterval.get();
    }

    public long getPlayerListSortInterval() {
        return this.playerListSortInterval.get();
    }

    @NotNull
    public Map<String, Integer> getPlayerListRankOrderMap() {
        return this.playerListRankOrders.get();
    }

    @NotNull
    public Map<String, TabLayoutFormat> getPlayerListLayoutFormatsMap() {
        return this.playerListLayoutFormats.get();
    }

    @NotNull
    public Map<String, TabNameFormat> getPlayerListNameFormatsMap() {
        return this.playerListNameFormats.get();
    }
}
