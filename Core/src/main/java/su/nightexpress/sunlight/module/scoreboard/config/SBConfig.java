package su.nightexpress.sunlight.module.scoreboard.config;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.sunlight.module.scoreboard.impl.BoardConfig;
import su.nightexpress.sunlight.utils.DynamicText;

import java.util.*;

import static su.nightexpress.sunlight.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class SBConfig {

    private static final String DEF_ANIMATION_1 = "store";
    
    public static final String FILE_ANIMATIONS = "animations.yml";

    public static final ConfigValue<Map<String, BoardConfig>> BOARD_CONFIGS = ConfigValue.forMap("Boards",
        (cfg, path, id) -> BoardConfig.read(cfg, path + "." + id, id),
        (cfg, path, map) -> map.forEach((id, conf) -> conf.write(cfg, path + "." + id)),
        () -> Map.of(DEFAULT, new BoardConfig(DEFAULT,
            10,
            1,
            Lists.newSet(WILDCARD),
            Lists.newSet(WILDCARD),
            GRADIENT.enclose("#84CCFB", "#C9E5FD", BOLD.enclose("SampleSMP")),
            Lists.newList(
                GRAY.enclose("      %server_time_MM/dd/yyyy%"),
                " ",
                BLUE.enclose(PLAYER_DISPLAY_NAME),
                BLUE.enclose(" ▎ " + GRAY.enclose("Rank:") + " " + WHITE.enclose("%vault_rank%")),
                BLUE.enclose(" ▎ " + GRAY.enclose("Balance:") + " " + "$%vault_eco_balance_formatted%"),
                BLUE.enclose(" ▎ " + GRAY.enclose("Kills:") + " " + WHITE.enclose("%statistic_player_kills%")),
                BLUE.enclose(" ▎ " + GRAY.enclose("Deaths:") + " " + WHITE.enclose("%statistic_deaths%")),
                " ",
                GREEN.enclose("Location"),
                GREEN.enclose(" ▎ " + GRAY.enclose("Biome:") + " " + WHITE.enclose("%player_biome_capitalized%")),
                GREEN.enclose(" ▎ " + GRAY.enclose("World:") + " " + WHITE.enclose("%player_world%")),
                " ",
                DynamicText.PLACEHOLDER.apply(DEF_ANIMATION_1)
            ))),
        "Individual per-player scoreboard format based on player's rank and world.",
        "[You must have " + Plugins.VAULT + " with compatible Permissions plugins installed for this feature to work properly]",
        "",
        "If multiple scoreboards are available for a player, the one with the greatest priority will be used.",
        "If no scoreboard is available for a player, the one labeled '" + DEFAULT + "' will be used (if present).",
        "",
        "Add '" + WILDCARD + "' to the 'Groups' list to make scoreboard available for any rank.",
        "Add '" + WILDCARD + "' to the 'Worlds' list to make scoreboard available in any world.",
        "",
        "Use '" + DynamicText.PLACEHOLDER.apply("[name]") + "' placeholder to include dynamic text (animation) from the " + FILE_ANIMATIONS + " config file.",
        "Text and Color Formations: " + WIKI_TEXT_URL,
        "You can use " + Plugins.PLACEHOLDER_API + " placeholders."
    );

    @NotNull
    public static List<DynamicText> getDefaultAnimations() {
        List<DynamicText> list = new ArrayList<>();

        list.add(new DynamicText(DEF_ANIMATION_1, Lists.newList(
            GRAY.enclose("play.servermc.com"),
            GRAY.enclose(WHITE.enclose("play") + ".servermc.com"),
            GRAY.enclose("play.servermc.com"),
            GRAY.enclose("play." + WHITE.enclose("servermc") + ".com"),
            GRAY.enclose("play.servermc.com"),
            GRAY.enclose("play.servermc." + WHITE.enclose("com")),
            GRAY.enclose("play.servermc.com"),
            GRAY.enclose("play.servermc.com"),
            GRAY.enclose("play.servermc.com"),
            GRAY.enclose("play.servermc.com"),
            GRAY.enclose("play.servermc.com"),
            GRAY.enclose("play.servermc.com")
        ), 500));
        
        return list;
    }
}
