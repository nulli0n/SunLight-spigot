package su.nightexpress.sunlight;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.text.tag.Tags;

import java.util.function.Function;

public class SLPlaceholders extends su.nightexpress.nightcore.util.Placeholders {

    @NotNull
    @Deprecated
    public static String listEntry(@NotNull String str) {
        return Tags.LIGHT_YELLOW.enclose("●") + " " + Tags.LIGHT_GRAY.enclose(str);
    }

    public static final Function<String, String> ANIMATION = id -> "%animation:" + id + "%";

    public static final String GENERIC_MESSAGE     = "%message%";
    public static final String GENERIC_FORMAT      = "%format%";
    public static final String GENERIC_COMMAND     = "%command%";
    public static final String GENERIC_CATEGORY    = "%category%";
    public static final String GENERIC_TEXT        = "%text%";
    public static final String GENERIC_AMOUNT      = "%amount%";
    public static final String GENERIC_LEVEL       = "%level%";
    public static final String GENERIC_CURRENT     = "%current%";
    public static final String GENERIC_MAX         = "%max%";
    public static final String GENERIC_TICKS       = "%ticks%";
    public static final String GENERIC_SOURCE      = "%source%";
    public static final String GENERIC_TARGET      = "%target%";
    public static final String GENERIC_RESULT      = "%result%";
    public static final String GENERIC_STATE       = "%state%";
    public static final String GENERIC_STATUS      = "%status%";
    public static final String GENERIC_TYPE        = "%type%";
    public static final String GENERIC_MODE        = "%mode%";
    public static final String GENERIC_ITEM        = "%item%";
    public static final String GENERIC_TIME        = "%time%";
    public static final String GENERIC_NAME        = "%name%";
    public static final String GENERIC_VALUE       = "%value%";
    public static final String GENERIC_PAGE = "%page%";
    public static final String GENERIC_WORLD       = "%world%";
    public static final String GENERIC_TOTAL       = "%total%";
    public static final String GENERIC_GLOBAL      = "%global%";
    public static final String GENERIC_RADIUS      = "%radius%";
    public static final String GENERIC_ADDRESS     = "%address%";
    public static final String GENERIC_DIRECTION   = "%direction%";
    public static final String GENERIC_DISTANCE    = "%distance%";
    public static final String GENERIC_COOLDOWN    = "%cooldown%";
    public static final String GENERIC_AVAILABLE   = "%available%";
    public static final String GENERIC_ENCHANTMENT = "%enchantment%";
    public static final String GENERIC_SLOT        = "%slot%";
    public static final String GENERIC_OLD_VALUE   = "%oldvalue%";
    public static final String GENERIC_NEW_VALUE   = "%newvalue%";
}
