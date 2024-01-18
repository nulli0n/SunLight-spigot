package su.nightexpress.sunlight.data.impl.settings;

public class DefaultSettings {

    public static final UserSetting<Boolean> FOOD_GOD          = UserSetting.asBoolean("food_god", false, true);
    public static final UserSetting<Boolean> GOD_MODE          = UserSetting.asBoolean("god_mode", false, false);
    public static final UserSetting<Boolean> ANTI_PHANTOM      = UserSetting.asBoolean("anti_phantom", false, true);
    public static final UserSetting<Boolean> ACCEPT_PM         = UserSetting.asBoolean("accept_pm", true, true);
    public static final UserSetting<Boolean> TELEPORT_REQUESTS = UserSetting.asBoolean("teleport_requests", true, true);
    public static final UserSetting<Boolean> VANISH            = UserSetting.asBoolean("vanish", false, false);
    public static final UserSetting<Boolean> NO_MOB_TARGET = UserSetting.asBoolean("no_mob_target", false, false);
    public static final UserSetting<Boolean> MENTIONS = UserSetting.asBoolean("mentions", true, true);

}
