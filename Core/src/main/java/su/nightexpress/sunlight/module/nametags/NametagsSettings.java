package su.nightexpress.sunlight.module.nametags;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.configuration.AbstractConfig;
import su.nightexpress.nightcore.configuration.ConfigProperty;
import su.nightexpress.nightcore.configuration.ConfigType;
import su.nightexpress.nightcore.configuration.ConfigTypes;
import su.nightexpress.nightcore.integration.permission.PermissionPlugins;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.sunlight.SLPlaceholders;

import java.util.Map;

import static su.nightexpress.nightcore.util.placeholder.CommonPlaceholders.*;

public class NametagsSettings extends AbstractConfig {

    private static final ConfigType<NameTagFormat> NAME_TAG_FORMAT_CONFIG_TYPE = ConfigType.of(
        NameTagFormat::read,
        FileConfig::set
    );

    private final ConfigProperty<Long> nametagUpdateInterval = this.addProperty(ConfigTypes.LONG, "Nametags.Update_Interval",
        100L,
        "Sets player nametag update interval (in game ticks).",
        "[Asynchronous, Packet-Based]",
        "[1 second = 20 ticks]",
        "[Default is 100 ticks]"
    );

    private final ConfigProperty<Map<String, NameTagFormat>> nametagFormats = this.addProperty(ConfigTypes.forMapWithLowerKeys(NAME_TAG_FORMAT_CONFIG_TYPE),
        "Nametags.Groups",
        NametagsDefaults.getDefaultNameTagFormats(),
        "Here you can create your own, custom player nametag formats.",
        "[*] Requires %s OR %s with a compatible Permissions plugin for the feature to work properly.".formatted(PermissionPlugins.LUCK_PERMS, PermissionPlugins.VAULT),
        "[*] You can NOT change the name of the player.",
        "[*] The game does NOT support RGB colors for tag names yet.",
        "",
        "[ SETTINGS DESCRIPTION ]",
        "├── Priority:",
        "│     -> Sets format priority. When multiple formats are available, the one with the highest priority is used.",
        "├── Ranks:",
        "│     -> List of ranks (permission groups) to which this format is available. Add '%s' to allow all ranks.".formatted(SLPlaceholders.WILDCARD),
        "├── Prefix + Suffix:",
        "│     -> Sets the text displayed before (Prefix) and after (Suffix) the player name of the tag.",
        "│     [>] Text Formations: " + SLPlaceholders.URL_WIKI_TEXT,
        "│     [>] Placeholders Available:",
        "│         - %s - Player name.".formatted(PLAYER_NAME),
        "│         - %s - Player display (custom) name.".formatted(PLAYER_DISPLAY_NAME),
        "│         - %s - Player prefix.".formatted(PLAYER_PREFIX),
        "│         - %s - Player suffix.".formatted(PLAYER_SUFFIX),
        "│         - %s - Player world.".formatted(PLAYER_WORLD),
        "│         - %s - https://wiki.placeholderapi.com/".formatted(Plugins.PLACEHOLDER_API),
        "├── Condition:",
        "│     -> Boolean expression with placeholders to control availability (optional).",
        "└── Color:",
        "      -> Color of the player name. List of available colors: https://minecraft.wiki/w/Formatting_codes#Color_codes"
    );

    public long getNameTagUpdateInterval() {
        return this.nametagUpdateInterval.get();
    }

    @NotNull
    public Map<String, NameTagFormat> getNameTagFormatsMap() {
        return this.nametagFormats.get();
    }
}
