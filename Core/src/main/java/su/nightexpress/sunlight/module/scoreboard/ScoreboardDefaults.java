package su.nightexpress.sunlight.module.scoreboard;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.module.scoreboard.board.BoardDefinition;
import su.nightexpress.sunlight.utils.ConditionExpression;
import su.nightexpress.sunlight.utils.DynamicText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static su.nightexpress.nightcore.util.Placeholders.*;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;

public class ScoreboardDefaults {

    public static final String FILE_ANIMATIONS = "animations.yml";

    private static final String DEF_ANIMATION_1 = "store";

    @NotNull
    public static Map<String, BoardDefinition> getDefaultBoardDefinitions() {
        return Map.of("default", new BoardDefinition(
            10,
            1,
            Lists.newSet(WILDCARD),
            Lists.newSet(WILDCARD),
            GRADIENT_3.with("#FFAA00", "#FF8833", "#FF5500").and(BOLD).wrap("YourServerName"),
            Lists.newList(
                GRAY.wrap("      %server_time_MM/dd/yyyy%"),
                " ",
                BLUE.wrap(PLAYER_DISPLAY_NAME),
                BLUE.wrap(" ▎ " + GRAY.wrap("Rank:") + " " + WHITE.wrap("%vault_rank%")),
                BLUE.wrap(" ▎ " + GRAY.wrap("Balance:") + " " + "$%vault_eco_balance_formatted%"),
                BLUE.wrap(" ▎ " + GRAY.wrap("Kills:") + " " + WHITE.wrap("%statistic_player_kills%")),
                BLUE.wrap(" ▎ " + GRAY.wrap("Deaths:") + " " + WHITE.wrap("%statistic_deaths%")),
                " ",
                GREEN.wrap("Location"),
                GREEN.wrap(" ▎ " + GRAY.wrap("Biome:") + " " + WHITE.wrap("%player_biome_capitalized%")),
                GREEN.wrap(" ▎ " + GRAY.wrap("World:") + " " + WHITE.wrap("%player_world%")),
                " ",
                SLPlaceholders.ANIMATION.apply(DEF_ANIMATION_1)
            ),
            "",
            ConditionExpression.of("", "Boards.default")
        ));
    }
    
    @NotNull
    public static List<DynamicText> getDefaultAnimations() {
        List<DynamicText> list = new ArrayList<>();

        list.add(new DynamicText(DEF_ANIMATION_1, Lists.newList(
            GRAY.wrap("play.servermc.com"),
            GRAY.wrap(WHITE.wrap("play") + ".servermc.com"),
            GRAY.wrap("play.servermc.com"),
            GRAY.wrap("play." + WHITE.wrap("servermc") + ".com"),
            GRAY.wrap("play.servermc.com"),
            GRAY.wrap("play.servermc." + WHITE.wrap("com")),
            GRAY.wrap("play.servermc.com"),
            GRAY.wrap("play.servermc.com"),
            GRAY.wrap("play.servermc.com"),
            GRAY.wrap("play.servermc.com"),
            GRAY.wrap("play.servermc.com"),
            GRAY.wrap("play.servermc.com")
        ), 500));

        return list;
    }
}
