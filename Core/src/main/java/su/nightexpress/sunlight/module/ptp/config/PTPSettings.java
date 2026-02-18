package su.nightexpress.sunlight.module.ptp.config;

import su.nightexpress.nightcore.configuration.AbstractConfig;
import su.nightexpress.nightcore.configuration.ConfigProperty;
import su.nightexpress.nightcore.configuration.ConfigTypes;

public class PTPSettings extends AbstractConfig {

    private final ConfigProperty<Integer> requestTimeout = this.addProperty(ConfigTypes.INT, "Request_Timeout",
        60,
        "Sets how long (in seconds) teleport request will be valid to accept/decline it."
    );

    public int getRequestTimeout() {
        return this.requestTimeout.get();
    }
}
