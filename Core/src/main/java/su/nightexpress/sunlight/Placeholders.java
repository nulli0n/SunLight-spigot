package su.nightexpress.sunlight;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.text.tag.Tags;

public class Placeholders extends su.nightexpress.nightcore.util.Placeholders {

    @Deprecated public static final String ENGINE_URL                 = "https://github.com/nulli0n/NexEngine-spigot/wiki/";
    @Deprecated public static final String ENGINE_URL_CONFIG_COMMANDS = ENGINE_URL + "Configuration-Tips#command-sections";
    @Deprecated public static final String ENGINE_URL_PLACEHOLDERS    = ENGINE_URL + "Internal-Placeholders";
    @Deprecated public static final String ENGINE_URL_LANG            = ENGINE_URL + "Language-Config";
    @Deprecated public static final String ENGINE_URL_LANG_JSON       = ENGINE_URL + "Language-Config#json-formatting";

    @Deprecated public static final String GENERIC_CONSOLE   = "CONSOLE";

    @NotNull
    public static String listEntry(@NotNull String str) {
        return Tags.LIGHT_YELLOW.enclose("●") + " " + Tags.LIGHT_GRAY.enclose(str);
    }

    @NotNull
    public static String badEntry(@NotNull String str) {
        return Tags.LIGHT_RED.enclose("✘") + " " + Tags.LIGHT_GRAY.enclose(str);
    }

    @NotNull
    public static String goodEntry(@NotNull String str) {
        return Tags.LIGHT_GREEN.enclose("✔") + " " + Tags.LIGHT_GRAY.enclose(str);
    }

    public static final String GENERIC_MESSAGE   = "%message%";
    public static final String GENERIC_FORMAT = "%format%";
    public static final String GENERIC_COMMAND   = "%command%";
    public static final String GENERIC_AMOUNT    = "%amount%";
    public static final String GENERIC_LEVEL     = "%level%";
    public static final String GENERIC_CURRENT   = "%current%";
    public static final String GENERIC_MAX       = "%max%";
    public static final String GENERIC_TICKS     = "%ticks%";
    public static final String GENERIC_SOURCE    = "%source%";
    public static final String GENERIC_TARGET    = "%target%";
    public static final String GENERIC_RESULT    = "%result%";
    public static final String GENERIC_STATE     = "%state%";
    public static final String GENERIC_TYPE      = "%type%";
    public static final String GENERIC_ITEM      = "%item%";
    public static final String GENERIC_TIME      = "%time%";
    public static final String GENERIC_NAME      = "%name%";
    public static final String GENERIC_VALUE     = "%value%";
    public static final String GENERIC_WORLD     = "%world%";
    public static final String GENERIC_TOTAL     = "%total%";
    public static final String GENERIC_GLOBAL = "%global%";
    public static final String GENERIC_RADIUS    = "%radius%";
    public static final String GENERIC_COOLDOWN  = "%cooldown%";
    public static final String GENERIC_AVAILABLE = "%available%";
}
