package su.nightexpress.sunlight.module.vanish.config;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import su.nightexpress.nightcore.config.ConfigValue;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.sunlight.Placeholders.*;

public class VanishConfig {

    public static final ConfigValue<Boolean> BAR_INDICATOR_ENABLED = ConfigValue.create("BarIndicator.Enabled",
        true,
        "Sets whether vanish boss bar indicator should be used."
    );

    public static final ConfigValue<String> BAR_INDICATOR_VANISHED_TITLE = ConfigValue.create("BarIndicator.Vanished.Title",
        WHITE.wrap("You're vanished!"),
        "Sets vanish indicator bar title."
    );

    public static final ConfigValue<BarColor> BAR_INDICATOR_VANISHED_COLOR = ConfigValue.create("BarIndicator.Vanished.Color",
        BarColor.class, BarColor.GREEN,
        "Sets vanish indicator bar color."
    );

    public static final ConfigValue<BarStyle> BAR_INDICATOR_VANISHED_STYLE = ConfigValue.create("BarIndicator.Vanished.Style",
        BarStyle.class, BarStyle.SEGMENTED_10,
        "Sets vanish indicator bar style."
    );
}
