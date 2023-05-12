package su.nightexpress.sunlight;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.sunlight.data.impl.SunUser;

import java.util.function.UnaryOperator;

public class Placeholders extends su.nexmedia.engine.utils.Placeholders {

    public static final String GENERIC_CONSOLE = "CONSOLE";
    public static final String GENERIC_MESSAGE = "%message%";
    public static final String GENERIC_COMMAND = "%command%";
    public static final String GENERIC_AMOUNT = "%amount%";
    public static final String GENERIC_LEVEL = "%level%";
    public static final String GENERIC_CURRENT = "%current%";
    public static final String GENERIC_MAX = "%max%";
    public static final String GENERIC_SOURCE = "%source%";
    public static final String GENERIC_TARGET = "%target%";
    public static final String GENERIC_STATE = "%state%";
    public static final String GENERIC_TYPE = "%type%";
    public static final String GENERIC_ITEM = "%item%";
    public static final String GENERIC_TIME = "%time%";
    public static final String GENERIC_NAME = "%name%";
    public static final String GENERIC_VALUE = "%value%";
    public static final String GENERIC_WORLD = "%world%";
    public static final String GENERIC_TOTAL = "%total%";
    public static final String GENERIC_RADIUS = "%radius%";
    public static final String GENERIC_COOLDOWN = "%cooldown%";
    public static final String GENERIC_AVAILABLE = "%available%";

    public static final String ENGINE_URL                 = "https://github.com/nulli0n/NexEngine-spigot/wiki/";
    public static final String ENGINE_URL_CONFIG_COMMANDS = ENGINE_URL + "Configuration-Tips#command-sections";
    public static final String ENGINE_URL_PLACEHOLDERS    = ENGINE_URL + "Internal-Placeholders";
    public static final String ENGINE_URL_LANG            = ENGINE_URL + "Language-Config";
    public static final String ENGINE_URL_LANG_JSON       = ENGINE_URL + "Language-Config#json-formatting";

    @NotNull
    @Deprecated
    public static UnaryOperator<String> replacer(@Nullable org.bukkit.entity.Player player, @Nullable SunUser user) {
        if (player != null) return Player.replacer(player);
        if (user != null) return str -> str
            .replace(Player.NAME, user.getName())
            .replace(Player.DISPLAY_NAME, user.getName());

        return str -> str;
    }
}
