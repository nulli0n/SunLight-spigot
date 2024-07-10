package su.nightexpress.sunlight.config;

import su.nightexpress.nightcore.config.ConfigValue;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class Config {

    public static final String DIR_MENU = "/menu/";

    public static final ConfigValue<SimpleDateFormat> GENERAL_DATE_FORMAT = ConfigValue.create("General.Date_Format",
        (cfg, path, def) -> new SimpleDateFormat(cfg.getString(path, def.toPattern())),
        (cfg, path, format) -> cfg.set(path, format.toPattern()),
        () -> new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"),
        "Sets the global date format used in various plugin modules."
    );

    public static final ConfigValue<DateTimeFormatter> GENERAL_TIME_FORMAT = ConfigValue.create("General.Time_Format",
        (cfg, path, def) -> DateTimeFormatter.ofPattern(cfg.getString(path, "HH:mm:ss")),
        (cfg, path, format) -> cfg.set(path, "HH:mm:ss"),
        () -> DateTimeFormatter.ofPattern("HH:mm:ss"),
        "Sets the global time format used in various plugin modules."
    );

    public static final ConfigValue<String> CONSOLE_NAME = ConfigValue.create("General.ConsoleName",
        "Console",
        "Sets name for the console command sender.",
        "Used in some messages when you send private messages, execute bans, and other actions from console."
    );
}
