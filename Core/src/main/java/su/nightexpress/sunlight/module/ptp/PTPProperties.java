package su.nightexpress.sunlight.module.ptp;

import su.nightexpress.sunlight.user.property.UserProperty;

public class PTPProperties {

    public static final UserProperty<Boolean> TELEPORT_REQUESTS = UserProperty.create("teleport_requests", Boolean.class, true, true);
}
