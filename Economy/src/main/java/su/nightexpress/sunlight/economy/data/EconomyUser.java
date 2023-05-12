package su.nightexpress.sunlight.economy.data;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.data.AbstractUser;
import su.nightexpress.sunlight.economy.SunLightEconomyPlugin;
import su.nightexpress.sunlight.economy.config.EconomyConfig;

import java.util.UUID;

public class EconomyUser extends AbstractUser<SunLightEconomyPlugin> {

    private double balance;

    public EconomyUser(@NotNull SunLightEconomyPlugin plugin, @NotNull UUID uuid, @NotNull String name) {
        this(plugin, uuid, name, System.currentTimeMillis(), System.currentTimeMillis(),

            EconomyConfig.CURRENCY.getDefaultBalance()
        );
    }

    public EconomyUser(@NotNull SunLightEconomyPlugin plugin, @NotNull UUID uuid, @NotNull String name,
                       long lastOnline, long dateCreated,
                       double balance) {
        super(plugin, uuid, name, lastOnline, dateCreated);
        this.setBalance(balance);
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.setBalanceRaw(balance);
        this.saveData(this.plugin);
    }

    public void setBalanceRaw(double balance) {
        this.balance = balance;
    }
}
