package su.nightexpress.sunlight.module.scoreboard.config;

import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.utils.EngineUtils;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.module.scoreboard.impl.BoardConfig;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SBConfig {

    public static final JOption<Map<String, BoardConfig>> BOARD_CONFIGS = new JOption<>("Boards",
        (cfg, path, def) -> {
            Map<String, BoardConfig> map = new HashMap<>();
            for (String id : cfg.getSection(path)) {
                BoardConfig boardConfig = BoardConfig.read(cfg, path + "." + id, id);
                map.put(boardConfig.getId(), boardConfig);
            }
            return map;
        },
        Map.of(Placeholders.DEFAULT, new BoardConfig(Placeholders.DEFAULT, 10, 1,
            Set.of(Placeholders.WILDCARD), Set.of(Placeholders.WILDCARD),
            "<gradient:#84CCFB>&lSampleSMP</gradient:#C9E5FD>", Arrays.asList(
            "#adadad      %server_time_MM/dd/yyyy%",
            "&7",
            "#25baff%player_displayname%",
            " #25baff▎ #adadadRank: &f%vault_rank%",
            " #25baff▎ #adadadBalance: &2$&a%vault_eco_balance_formatted%",
            " #25baff▎ #adadadKills: &f%statistic_player_kills%",
            " #25baff▎ #adadadDeaths: &f%statistic_deaths%",
            "&7",
            "#4ee82cLocation",
            " #4ee82c▎ #adadadBiome: &f%player_biome_capitalized%",
            " #4ee82c▎ #adadadWorld: &f%player_world%",
            "&7",
            "%animation:store%"
        ))),
        "List of custom scoreboards based on player permission group and other conditions.",
        "If player has multiple groups, scoreboard with the highest priority will be used.",
        "Put '" + Placeholders.WILDCARD + "' to 'Groups' and/or 'Worlds' options to include all possible worlds/groups.",
        "To insert animation, use '%animation:[name]%' format. Where [name] is animation name from animations config.",
        "You can use " + EngineUtils.PLACEHOLDER_API + " here."
    ).setWriter((cfg, path, map) -> map.forEach((id, conf) -> conf.write(cfg, path + "." + id)));
}
