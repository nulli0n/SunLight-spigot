package su.nightexpress.sunlight.economy.config;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nightexpress.sunlight.economy.SunLightEconomyPlugin;
import su.nightexpress.sunlight.economy.manager.Currency;

public class EconomyConfig {

    public static Currency CURRENCY;

    public static int BALANCE_TOP_UPDATE_INTERVAL;

    public static void load(@NotNull SunLightEconomyPlugin plugin) {
        JYML cfg = plugin.getConfig();

        String cNameSingular = cfg.getString("Currency.Name.Singular", "$");
        String cNamePlural = cfg.getString("Currency.Name.Plural", "$");
        String cNameFormat = cfg.getString("Currency.Name.Format", "%name%%amount%");
        double cStartValue = cfg.getDouble("Currency.Start_Balance");
        CURRENCY = new Currency(cNameSingular, cNamePlural, cNameFormat, cStartValue);

        BALANCE_TOP_UPDATE_INTERVAL = cfg.getInt("BalanceTop_Update_Interval", 15) * 60;
    }
}
