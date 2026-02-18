package su.nightexpress.sunlight.module.scoreboard;

import su.nightexpress.sunlight.user.property.UserProperty;

public class ScoreboardProperties {

    public static final UserProperty<Boolean> SCOREBOARD = UserProperty.create("scoreboard", Boolean.class, true, true);
}
