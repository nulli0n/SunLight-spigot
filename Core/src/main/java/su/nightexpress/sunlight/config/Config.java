package su.nightexpress.sunlight.config;

import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.sunlight.utils.SunUtils;

public class Config {

    public static final String DIR_MENU = "/menu/";

    public static final ConfigValue<String> GENERAL_DATE_FORMAT = ConfigValue.create("General.Date_Format",
        "dd/MM/yyyy HH:mm:ss",
        "Sets the global date format used in various plugin modules."
    ).whenRead(SunUtils::setDateFormatter);

    public static final ConfigValue<String> GENERAL_TIME_FORMAT = ConfigValue.create("General.Time_Format",
        "HH:mm:ss",
        "Sets the global time format used in various plugin modules."
    ).whenRead(SunUtils::setTimeFormatter);

    public static final ConfigValue<String> CONSOLE_NAME = ConfigValue.create("General.ConsoleName",
        "Console",
        "Sets name for the console command sender.",
        "Used in some messages when you send private messages, execute bans, and other actions from console."
    );
}
