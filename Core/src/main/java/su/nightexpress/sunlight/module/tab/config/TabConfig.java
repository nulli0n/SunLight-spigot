package su.nightexpress.sunlight.module.tab.config;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.module.tab.impl.NameTagFormat;
import su.nightexpress.sunlight.module.tab.impl.TabListFormat;
import su.nightexpress.sunlight.module.tab.impl.TabNameFormat;
import su.nightexpress.sunlight.utils.DynamicText;

import java.util.*;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.sunlight.Placeholders.*;

public class TabConfig {

    private static final String DEF_ANIMATION_1 = "foot_1";
    private static final String DEF_ANIMATION_2 = "foot_2";
    private static final String DEF_ANIMATION_3 = "foot_3";

    private static final String PLACEHOLDER_BALANCE = "%vault_eco_balance_formatted%";

    public static final String FILE_ANIMATIONS = "animations.yml";

    public static final ConfigValue<Long> TABLIST_UPDATE_INTERVAL = ConfigValue.create("Tablist.Update_Interval",
        1L,
        "Sets tablist update interval (in ticks).",
        "[Asynchronous]",
        "[1 second = 20 ticks]",
        "[Default is 1]"
    );

    public static final ConfigValue<Map<String, TabListFormat>> TABLIST_FORMAT_MAP = ConfigValue.forMap("Tablist.Format",
        (cfg, path, def) -> TabListFormat.read(cfg, path + "." + def),
        (cfg, path, map) -> map.forEach((id, format) -> format.write(cfg, path + "." + id)),
        () -> Map.of(
            DEFAULT, new TabListFormat(1,
                Lists.newSet(Placeholders.WILDCARD),
                Lists.newSet(Placeholders.WILDCARD),
                Lists.newList(
                    GRADIENT.enclose("#84CCFB", "#C9E5FD", BOLD.enclose("SampleSMP.com")),
                    LIGHT_GRAY.enclose("%server_time_d MMMM%, %server_time_HH:mm:ss%"),
                    " "
                ),
                Lists.newList(
                    " ",
                    DynamicText.PLACEHOLDER.apply(DEF_ANIMATION_1),
                    " ",
                    DynamicText.PLACEHOLDER.apply(DEF_ANIMATION_2),
                    DynamicText.PLACEHOLDER.apply(DEF_ANIMATION_3)
                )
            )
        ),
        "Individual per-player tablist format based on player's rank and world.",
        "[You must have " + Plugins.VAULT + " with compatible Permissions plugins installed for this feature to work properly]",
        "",
        "If multiple tablists are available for a player, the one with the greatest priority will be used.",
        "If no tablist is available for a player, the one labeled '" + DEFAULT + "' will be used (if present).",
        "",
        "Add '" + WILDCARD + "' to the 'Groups' list to make tablist available for any rank.",
        "Add '" + WILDCARD + "' to the 'Worlds' list to make tablist available in any world.",
        "",
        "Use '" + DynamicText.PLACEHOLDER.apply("[name]") + "' placeholder to include dynamic text (animation) from the " + FILE_ANIMATIONS + " config file.",
        "Text and Color Formations: " + WIKI_TEXT_URL,
        "You can use " + Plugins.PLACEHOLDER_API + " placeholders."
    );

    public static final ConfigValue<Map<String, TabNameFormat>> TABLIST_NAME_FORMAT = ConfigValue.forMap("Tablist.Player_Names",
        (cfg, path, rank) -> TabNameFormat.read(cfg, path + "." + rank),
        (cfg, path, map) -> map.forEach((id, name) -> name.write(cfg, path + "." + id)),
        () -> Map.of(
            "admin", new TabNameFormat(100, LIGHT_RED.enclose("[Admin]") + " " + LIGHT_GRAY.enclose(PLAYER_DISPLAY_NAME)),
            "vip", new TabNameFormat(10, LIGHT_GREEN.enclose("[VIP]") + " " + LIGHT_GRAY.enclose(PLAYER_DISPLAY_NAME)),
            DEFAULT, new TabNameFormat(1, LIGHT_CYAN.enclose("[Member]") + " " + LIGHT_GRAY.enclose(PLAYER_DISPLAY_NAME))
        ),
        "Tablist name format based on player's rank.",
        "[You must have " + Plugins.VAULT + " with compatible Permissions plugins installed for this feature to work properly]",
        "",
        "=".repeat(20) + "[ IMPORTANT! ]" + "=".repeat(20),
        "This option does NOT affect player sorting in tablist!",
        "To make tablist sorted by player ranks you MUST setup 'Nametags' option!",
        "This is just how the game works.",
        "",
        "If multiple name formats are available for a player, the one with the greatest priority will be used.",
        "If no name format is available for a player, the one labeled '" + DEFAULT + "' will be used (if present).",
        "",
        "Use '" + DynamicText.PLACEHOLDER.apply("[name]") + "' placeholder to include dynamic text (animation) from the " + FILE_ANIMATIONS + " config file.",
        "Text and Color Formations: " + WIKI_TEXT_URL,
        "You can use " + Plugins.PLACEHOLDER_API + " placeholders."
    );

    public static final ConfigValue<Long> NAMETAG_UPDATE_INTERVAL = ConfigValue.create("Nametags.Update_Interval",
        300L,
        "Sets player nametag update interval (in ticks).",
        "[Asynchronous, Packet-Based]",
        "[1 second = 20 ticks]",
        "[Default is 300]"
    );

    public static final ConfigValue<Map<String, NameTagFormat>> NAMETAG_FORMAT = ConfigValue.create("Nametags.Groups",
        (cfg, path, def) -> {
            Map<String, NameTagFormat> map = new HashMap<>();

            List<NameTagFormat> list = new ArrayList<>();
            for (String rank : cfg.getSection(path)) {
                list.add(NameTagFormat.read(cfg, path + "." + rank, rank));
            }
            list.sort(Comparator.comparingInt(NameTagFormat::getPriority).reversed());

            int index = 'A';
            for (NameTagFormat format : list) {
                format.setIndex(index++);
                map.put(format.getId(), format);
            }

            return map;
        },
        (cfg, path, map) -> map.forEach((id, tag) -> tag.write(cfg, path + "." + id)),
        () -> Map.of(
            "admin", new NameTagFormat("admin", 100, LIGHT_RED.enclose("Admin "), " " + LIGHT_RED.enclose(PLACEHOLDER_BALANCE), ChatColor.GRAY.name()),
            "vip", new NameTagFormat("vip", 10, LIGHT_GREEN.enclose("VIP "), " " + LIGHT_GREEN.enclose(PLACEHOLDER_BALANCE), ChatColor.GRAY.name()),
            "default", new NameTagFormat("default", 1, LIGHT_CYAN.enclose("Member "), " " + LIGHT_CYAN.enclose(PLACEHOLDER_BALANCE), ChatColor.GRAY.name())
        ),
        "Player nametag format based on their permission group.",
        "[You must have " + Plugins.VAULT + " with compatible Permissions plugins installed for this feature to work properly]",
        "",
        "If multiple tag formats are available for a player, the one with the greatest priority will be used.",
        "If no tag format is available for a player, the one labeled '" + DEFAULT + "' will be used (if present).",
        "",
        "You can use " + Plugins.PLACEHOLDER_API + " placeholders for 'Prefix' and 'Suffix' options.",
        "",
        "The game does not support RGB colors for tag names yet!",
        "List of available colors: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/ChatColor.html"
    );

    @NotNull
    public static List<DynamicText> getDefaultAnimations() {
        List<DynamicText> list = new ArrayList<>();

        list.add(new DynamicText(DEF_ANIMATION_1, Lists.newList(
            DARK_GRAY.enclose(" ".repeat(16) + LIGHT_BLUE.enclose(UNDERLINED.enclose("[Discord]")) + " [Store] [Website]" + " ".repeat(16)),
            DARK_GRAY.enclose(" ".repeat(16) + "[Discord] " + LIGHT_GREEN.enclose(UNDERLINED.enclose("[Store]")) + " [Website]" + " ".repeat(16)),
            DARK_GRAY.enclose(" ".repeat(16) + "[Discord] [Store] " + LIGHT_ORANGE.enclose(UNDERLINED.enclose("[Website]")) + " ".repeat(16))
        ), 5000));

        list.add(new DynamicText(DEF_ANIMATION_2, Lists.newList(
            LIGHT_GRAY.enclose(" ".repeat(16) + "Join our friendly discord community:" + " ".repeat(16)),
            LIGHT_GRAY.enclose(" ".repeat(16) + "Purchase ranks & crate keys at our store:" + " ".repeat(16)),
            LIGHT_GRAY.enclose(" ".repeat(16) + "Learn first about new updates at our site:" + " ".repeat(16))
        ), 5000));

        list.add(new DynamicText(DEF_ANIMATION_3, Lists.newList(
            LIGHT_GRAY.enclose(" ".repeat(16) + LIGHT_BLUE.enclose("mysupercraft.com/discord") + " ".repeat(16)),
            LIGHT_GRAY.enclose(" ".repeat(16) + LIGHT_GREEN.enclose("store.mysupercraft.com") + " ".repeat(16)),
            LIGHT_GRAY.enclose(" ".repeat(16) + LIGHT_ORANGE.enclose("www.mysupercraft.com") + " ".repeat(16))
        ), 5000));

        return list;
    }
}
