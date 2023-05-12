package su.nightexpress.sunlight.economy.data;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.data.AbstractUserManager;
import su.nightexpress.sunlight.economy.SunLightEconomyPlugin;

import java.util.UUID;

public class EconomyUserManager extends AbstractUserManager<SunLightEconomyPlugin, EconomyUser> {

    public EconomyUserManager(@NotNull SunLightEconomyPlugin plugin) {
        super(plugin, plugin);
    }

    @Override
    @NotNull
    protected EconomyUser createData(@NotNull UUID uuid, @NotNull String name) {
        return new EconomyUser(plugin, uuid, name);
    }
}
