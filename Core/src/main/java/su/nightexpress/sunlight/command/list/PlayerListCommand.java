package su.nightexpress.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.NumberUtil;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;

import java.util.*;
import java.util.stream.Collectors;

import static su.nexmedia.engine.utils.Colors.*;

public class PlayerListCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "playerlist";

    private static final String PLACEHOLDER_RANK = "%rank%";
    private static final String PLACEHOLDER_PLAYERS = "%players%";

    private final LinkedHashMap<String, String> formatGroup;
    private final List<String>                  formatList;

    public PlayerListCommand(@NotNull SunLight plugin, @NotNull JYML cfg, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_PLAYER_LIST);
        this.setDescription(plugin.getMessage(Lang.COMMAND_PLAYER_LIST_DESC));

        this.formatGroup = new JOption<LinkedHashMap<String, String>>("PlayerList.RankNames",
            (cfg2, path, def) -> {
                LinkedHashMap<String, String> map = new LinkedHashMap<>();
                for (String rank : cfg2.getSection(path)) {
                    map.put(rank.toLowerCase(), Colorizer.apply(cfg.getString(path + "." + rank, rank)));
                }
                return map;
            },
            () -> {
                LinkedHashMap<String, String> map = new LinkedHashMap<>();
                map.put("admins", RED + "Admins");
                map.put("vip", GREEN + "VIPs");
                map.put(Placeholders.DEFAULT, DARK_GRAY + "Members");
                return map;
            },
            "Place your ranks in order from highest -> lowest for best results."
        ).setWriter((cfg2, path, map) -> map.forEach((rank, name) -> cfg2.set(path + "." + rank, name))).read(cfg);

        this.formatList = JOption.create("PlayerList.Format", Arrays.asList(
            LIGHT_YELLOW,
            LIGHT_YELLOW + "&lOnline Players:",
            LIGHT_YELLOW,
            PLACEHOLDER_RANK + " (" + Placeholders.GENERIC_AMOUNT + "): " + GRAY + PLACEHOLDER_PLAYERS,
            LIGHT_YELLOW,
            LIGHT_YELLOW + "Total " + ORANGE + Placeholders.GENERIC_TOTAL + LIGHT_YELLOW + " players online.",
            LIGHT_YELLOW),
            "Sets the format for player list.",
            "JSON is supported here: " + Placeholders.ENGINE_URL_LANG_JSON
        ).mapReader(Colorizer::apply).read(cfg);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Set<Player> players = new HashSet<>();
        if (sender instanceof Player player) {
            players.addAll(plugin.getServer().getOnlinePlayers().stream().filter(player::canSee).collect(Collectors.toSet()));
        }
        else players.addAll(plugin.getServer().getOnlinePlayers());

        int onlineTotal = players.size();

        for (String line : this.formatList) {
            if (line.contains(PLACEHOLDER_RANK)) {
                this.formatGroup.forEach((rank, name) -> {
                    Set<Player> ranked = players.stream().filter(player -> PlayerUtil.getPermissionGroups(player).contains(rank)).collect(Collectors.toSet());
                    if (ranked.isEmpty()) return;

                    players.removeAll(ranked);

                    String names = ranked.stream().map(Player::getDisplayName).collect(Collectors.joining(", "));
                    PlayerUtil.sendRichMessage(sender, line
                        .replace(Placeholders.GENERIC_AMOUNT, String.valueOf(ranked.size()))
                        .replace(PLACEHOLDER_RANK, name)
                        .replace(PLACEHOLDER_PLAYERS, names)
                    );
                });
                continue;
            }
            PlayerUtil.sendRichMessage(sender, line.replace(Placeholders.GENERIC_TOTAL, NumberUtil.format(onlineTotal)));
        }
    }
}
