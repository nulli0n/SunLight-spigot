package su.nightexpress.sunlight.module.tab;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.module.tab.format.TabLayoutFormat;
import su.nightexpress.sunlight.module.tab.format.TabNameFormat;
import su.nightexpress.sunlight.utils.DynamicText;

import java.util.*;

import static su.nightexpress.nightcore.util.placeholder.CommonPlaceholders.*;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;

public class TabDefaults {

    private static final String PLACEHOLDER_AFK = "%sunlight_afk_mode%";

    private static final String DEF_ANIMATION_1 = "foot_1";
    private static final String DEF_ANIMATION_2 = "foot_2";
    private static final String DEF_ANIMATION_3 = "foot_3";

    @NotNull
    public static Map<String, Integer> getDefaultPlayerListRankOrders() {
        Map<String, Integer> map = new LinkedHashMap<>();

        map.put(SLPlaceholders.DEFAULT, 0);
        map.put("admin", 100);
        map.put("owner", 10_000);

        return map;
    }

    @NotNull
    public static Map<String, TabLayoutFormat> getDefaultPlayerListFormats() {
        Map<String, TabLayoutFormat> map = new LinkedHashMap<>();

        TabLayoutFormat defaultFormat = new TabLayoutFormat(
            1,
            Lists.newSet(SLPlaceholders.WILDCARD),
            Lists.newSet(SLPlaceholders.WILDCARD),
            Lists.newList(
                GRADIENT_3.with("#FFAA00", "#FF8833", "#FF5500").and(BOLD).wrap("YourServerName"),
                GRAY.wrap("%server_time_d MMMM%, %server_time_HH:mm:ss%"),
                " "
            ),
            Lists.newList(
                " ",
                SLPlaceholders.ANIMATION.apply(DEF_ANIMATION_1),
                " ",
                SLPlaceholders.ANIMATION.apply(DEF_ANIMATION_2),
                SLPlaceholders.ANIMATION.apply(DEF_ANIMATION_3)
            )
        );

        map.put("default", defaultFormat);

        return map;
    }

    @NotNull
    public static Map<String, TabNameFormat> getDefaultListNameFormats() {
        Map<String, TabNameFormat> map = new LinkedHashMap<>();

        map.put("default", new TabNameFormat(1, Set.of(SLPlaceholders.WILDCARD), AQUA.wrap("[Member]") + " " + GRAY.wrap(PLAYER_DISPLAY_NAME) + PLACEHOLDER_AFK));
        map.put("admin", new TabNameFormat(100, Set.of("admin"), RED.wrap("[Admin]") + " " + GRAY.wrap(PLAYER_DISPLAY_NAME) + PLACEHOLDER_AFK));
        map.put("owner", new TabNameFormat(10_000, Set.of("owner"), PURPLE.wrap("[Owner]") + " " + GRAY.wrap(PLAYER_DISPLAY_NAME) + PLACEHOLDER_AFK));

        return map;
    }

    @NotNull
    public static List<DynamicText> getDefaultAnimations() {
        List<DynamicText> list = new ArrayList<>();

        list.add(new DynamicText(DEF_ANIMATION_1, Lists.newList(
            DARK_GRAY.wrap(" ".repeat(16) + BLUE.wrap(UNDERLINED.wrap("[Discord]")) + " [Store] [Website]" + " ".repeat(16)),
            DARK_GRAY.wrap(" ".repeat(16) + "[Discord] " + GREEN.wrap(UNDERLINED.wrap("[Store]")) + " [Website]" + " ".repeat(16)),
            DARK_GRAY.wrap(" ".repeat(16) + "[Discord] [Store] " + ORANGE.wrap(UNDERLINED.wrap("[Website]")) + " ".repeat(16))
        ), 5000));

        list.add(new DynamicText(DEF_ANIMATION_2, Lists.newList(
            GRAY.wrap(" ".repeat(16) + "Join our friendly discord community:" + " ".repeat(16)),
            GRAY.wrap(" ".repeat(16) + "Purchase ranks & crate keys at our store:" + " ".repeat(16)),
            GRAY.wrap(" ".repeat(16) + "Learn first about new updates at our site:" + " ".repeat(16))
        ), 5000));

        list.add(new DynamicText(DEF_ANIMATION_3, Lists.newList(
            GRAY.wrap(" ".repeat(16) + BLUE.wrap("mysupercraft.com/discord") + " ".repeat(16)),
            GRAY.wrap(" ".repeat(16) + GREEN.wrap("store.mysupercraft.com") + " ".repeat(16)),
            GRAY.wrap(" ".repeat(16) + ORANGE.wrap("www.mysupercraft.com") + " ".repeat(16))
        ), 5000));

        return list;
    }
}
