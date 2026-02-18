package su.nightexpress.sunlight.module.essential;

import su.nightexpress.sunlight.user.property.UserProperty;

public class EssentialProperties {

    public static final UserProperty<String> CUSTOM_NAME = UserProperty.create("custom_name", String.class, "", true);
}
