package su.nightexpress.sunlight.module.tab.config;

import org.bukkit.ChatColor;
import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.utils.Placeholders;
import su.nightexpress.sunlight.module.tab.impl.NametagFormat;
import su.nightexpress.sunlight.module.tab.impl.TabListFormat;
import su.nightexpress.sunlight.module.tab.impl.TabNameFormat;

import java.util.*;

public class TabConfig {

    public static final JOption<Long> TABLIST_UPDATE_INTERVAL = JOption.create("Tablist.Update_Interval", 1L,
        "How often (in ticks) tab list should be updated?",
        "1 second = 20 ticks.",
        "This will update both Tablist Format and Tablist Names.");

    public static final JOption<Map<String, TabListFormat>> TABLIST_FORMAT_MAP = new JOption<>("Tablist.Format",
        (cfg, path, def) -> {
            Map<String, TabListFormat> map = new HashMap<>();
            for (String id : cfg.getSection(path)) {
                TabListFormat format = TabListFormat.read(cfg, path + "." + id);
                map.put(id, format);
            }
            return map;
        },
        Map.of(
            Placeholders.DEFAULT, new TabListFormat(1, Set.of(Placeholders.WILDCARD), Set.of(Placeholders.WILDCARD),
                Arrays.asList(
                    "<gradient:#84CCFB>&lSampleSMP.com</gradient:#C9E5FD>",
                    "#d2d2d2%server_time_d MMMM%, %server_time_HH:mm:ss%",
                    "&7"
                ),
                Arrays.asList(
                    "&7",
                    "%animation:foot_1%",
                    "&7",
                    "%animation:foot_2%",
                    "%animation:foot_3%"
                )
            )
        ),
        "Player tab list format based on their permission group and other conditions.",
        "If player has multiple groups, format with the highest priority will be used.",
        "Format '" + Placeholders.DEFAULT + "' will be used if no other formats are available. It's safe to remove it.",
        "Put '" + Placeholders.WILDCARD + "' to 'Groups' and/or 'Worlds' options to include all possible worlds/groups.",
        "To insert animation, use '%animation:[name]%' format. Where [name] is animation name from animations config.",
        "You can use " + Hooks.PLACEHOLDER_API + " here."
    ).setWriter((cfg, path, map) -> map.forEach((id, format) -> format.write(cfg, path + "." + id)));

    public static final JOption<Map<String, TabNameFormat>> TABLIST_NAME_FORMAT = new JOption<>("Tablist.Player_Names",
        (cfg, path, def) -> {
            Map<String, TabNameFormat> map = new HashMap<>();
            for (String rank : cfg.getSection(path)) {
                TabNameFormat format = TabNameFormat.read(cfg, path + "." + rank);
                map.put(rank, format);
            }
            return map;
        },
        Map.of(
            "admin", new TabNameFormat(100, "#ff2525[Admin] #a8a8a8" + Placeholders.Player.DISPLAY_NAME),
            "vip", new TabNameFormat(10, "#ccff82[VIP] #a8a8a8" + Placeholders.Player.DISPLAY_NAME),
            Placeholders.DEFAULT, new TabNameFormat(1, "#b8fff9[Member] #a8a8a8" + Placeholders.Player.DISPLAY_NAME)
        ),
        "Player tab name format based on their permission group.",
        "If player has multiple groups, format with the highest priority will be used.",
        "Format '" + Placeholders.DEFAULT + "' will be used if no other formats are available. It's safe to remove it.",
        "You can use " + Hooks.PLACEHOLDER_API + " here.",
        "IMPORTANT: To make tab sort players by ranks, you have to setup 'Nametags.Groups' section below with the same groups as here!"
    ).setWriter((cfg, path, map) -> map.forEach((id, name) -> name.write(cfg, path + "." + id)));

    public static final JOption<Long> NAMETAG_UPDATE_INTERVAL = JOption.create("Nametags.Update_Interval", 300L,
        "How often (in ticks) player nametags should be updated?",
        "1 second = 20 ticks.");

    public static final JOption<Map<String, NametagFormat>> NAMETAG_FORMAT = new JOption<>("Nametags.Groups",
        (cfg, path, def) -> {
            Map<String, NametagFormat> map = new HashMap<>();

            List<NametagFormat> list = new ArrayList<>();
            for (String rank : cfg.getSection(path)) {
                list.add(NametagFormat.read(cfg, path + "." + rank, rank));
            }
            list.sort(Comparator.comparingInt(NametagFormat::getPriority).reversed());

            for (int index = 0; index < list.size(); index++) {
                NametagFormat tag = list.get(index);
                tag.setIndex(index);
                map.put(tag.getId(), tag);
            }

            return map;
        },
        Map.of(
            "admin", new NametagFormat("admin", 100, "#ff2525Admin ", " #ff2525$%vault_eco_balance_formatted%", ChatColor.GRAY),
            "vip", new NametagFormat("vip", 10, "#ccff82VIP ", " #ccff82$%vault_eco_balance_formatted%", ChatColor.GRAY),
            "default", new NametagFormat("default", 1, "#b8fff9Member ", " #b8fff9$%vault_eco_balance_formatted%", ChatColor.GRAY)
        ),
        "Player nametag format based on their permission group.",
        "If player has multiple groups, format with the highest priority will be used.",
        "Format '" + Placeholders.DEFAULT + "' will be used if no other formats are available. It's safe to remove it.",
        "You can use " + Hooks.PLACEHOLDER_API + " for 'Prefix' and 'Suffix' options.",
        "List of available colors: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/ChatColor.html"
    ).setWriter((cfg, path, map) -> map.forEach((id, tag) -> tag.write(cfg, path + "." + id)));
}
