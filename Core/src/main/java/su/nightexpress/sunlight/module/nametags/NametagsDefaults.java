package su.nightexpress.sunlight.module.nametags;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.utils.ConditionExpression;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;

public class NametagsDefaults {

    private static final String PLAYER_LEVEL = "%player_level%";

    @NotNull
    public static Map<String, NameTagFormat> getDefaultNameTagFormats() {
        Map<String, NameTagFormat> map = new LinkedHashMap<>();

        map.put("default", new NameTagFormat(1, Set.of("default"), AQUA.wrap("Member "), GRAY.wrap("Lv. ") + AQUA.wrap(PLAYER_LEVEL), "white", "", ConditionExpression.of("", "Nametags.Groups.default")));
        map.put("admin", new NameTagFormat(100, Set.of("admin"), RED.wrap("Admin "), GRAY.wrap("Lv. ") + RED.wrap(PLAYER_LEVEL), "white", "", ConditionExpression.of("", "Nametags.Groups.admin")));
        map.put("owner", new NameTagFormat(10_000, Set.of("owner"), PURPLE.wrap("Owner "), GRAY.wrap("Lv. ") + PURPLE.wrap(PLAYER_LEVEL), "white", "", ConditionExpression.of("", "Nametags.Groups.owner")));

        return map;
    }
}
