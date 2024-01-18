package su.nightexpress.sunlight.config;

import su.nexmedia.engine.api.config.JOption;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class Config {

    public static final JOption<SimpleDateFormat>  GENERAL_DATE_FORMAT = new JOption<>("General.Date_Format",
        (cfg, path, def) -> new SimpleDateFormat(cfg.getString(path, def.toPattern())),
        new SimpleDateFormat("dd/MM/yyyy HH:mm"),
        "Sets the global date format used in various plugin modules."
    ).setWriter((cfg, path, format) -> cfg.set(path, format.toPattern()));

    public static final JOption<DateTimeFormatter> GENERAL_TIME_FORMAT = new JOption<>("General.Time_Format",
        (cfg, path, def) -> DateTimeFormatter.ofPattern(cfg.getString(path, "HH:mm:ss")),
        DateTimeFormatter.ofPattern("HH:mm:ss"),
        "Sets the global time format used in various plugin modules."
    ).setWriter((cfg, path, format) -> cfg.set(path, "HH:mm:ss"));
}
