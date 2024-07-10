package su.nightexpress.sunlight.module.ptp.config;

import su.nightexpress.nightcore.config.ConfigValue;

public class PTPConfig {

    public static final ConfigValue<Integer> REQUEST_TIMEOUT = ConfigValue.create("Request_Timeout",
        60,
        "Sets how long (in seconds) teleport request will be valid to accept/decline it."
    );
}
