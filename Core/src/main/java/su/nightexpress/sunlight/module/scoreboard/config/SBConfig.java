package su.nightexpress.sunlight.module.scoreboard.config;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.configuration.AbstractConfig;
import su.nightexpress.nightcore.configuration.ConfigProperty;
import su.nightexpress.nightcore.configuration.ConfigType;
import su.nightexpress.nightcore.configuration.ConfigTypes;
import su.nightexpress.nightcore.integration.permission.PermissionPlugins;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.module.scoreboard.ScoreboardDefaults;
import su.nightexpress.sunlight.module.scoreboard.board.BoardDefinition;

import java.util.Map;

import static su.nightexpress.nightcore.util.placeholder.CommonPlaceholders.*;

public class SBConfig extends AbstractConfig {

    private static final ConfigType<BoardDefinition> BOARD_DEFINITION_CONFIG_TYPE = ConfigType.of(
        BoardDefinition::read,
        FileConfig::set
    );

    private final ConfigProperty<Map<String, BoardDefinition>> boardDefinitions = this.addProperty(ConfigTypes.forMapWithLowerKeys(BOARD_DEFINITION_CONFIG_TYPE),
        "Boards",
        ScoreboardDefaults.getDefaultBoardDefinitions(),
        "Here you can create your own, custom scoreboard formats.",
        "",
        "[ SETTINGS DESCRIPTION ]",
        "├── Update_Interval:",
        "│     -> Scoreboard update interval (in game ticks).",
        "├── Priority:",
        "│     -> Sets format priority. When multiple formats are available, the one with the highest priority is used.",
        "├── Worlds:",
        "│     -> List of worlds, where this format is available. Add '%s' to allow all worlds.".formatted(SLPlaceholders.WILDCARD),
        "├── Groups:",
        "│     -> List of ranks (permission groups) to which this format is available. Add '%s' to allow all ranks.".formatted(SLPlaceholders.WILDCARD),
        "└── Title + Lines:",
        "      -> Sets the text displayed at top (Title) and body (Lines) of the scoreboard.",
        "      [>] Text Formations: " + SLPlaceholders.URL_WIKI_TEXT,
        "      [>] Placeholders Available:",
        "          - %s - include dynamic text (animation) from the '%s'.".formatted(SLPlaceholders.ANIMATION.apply("[name]"), ScoreboardDefaults.FILE_ANIMATIONS),
        "          - %s - Player name.".formatted(PLAYER_NAME),
        "          - %s - Player display (custom) name.".formatted(PLAYER_DISPLAY_NAME),
        "          - %s - Player prefix.".formatted(PLAYER_PREFIX),
        "          - %s - Player suffix.".formatted(PLAYER_SUFFIX),
        "          - %s - Player world.".formatted(PLAYER_WORLD),
        "          - %s - https://wiki.placeholderapi.com/".formatted(Plugins.PLACEHOLDER_API),
        "",
        "[*] Requires %s OR %s with a compatible Permissions plugin for the feature to work properly.".formatted(PermissionPlugins.LUCK_PERMS, PermissionPlugins.VAULT)
    );

    @NotNull
    public Map<String, BoardDefinition> getBoardDefinitionMap() {
        return this.boardDefinitions.get();
    }
}
