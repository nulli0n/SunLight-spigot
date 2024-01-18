package su.nightexpress.sunlight.data.serialize;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import su.nightexpress.sunlight.data.impl.settings.SettingValue;
import su.nightexpress.sunlight.data.impl.settings.UserSetting;
import su.nightexpress.sunlight.data.impl.settings.BasicSettings;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class UserSettingsSerializer implements JsonSerializer<BasicSettings>, JsonDeserializer<BasicSettings> {

    @Override
    public BasicSettings deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();

        Map<String, String> settings = context.deserialize(object.get("settings"), new TypeToken<Map<String, String>>(){}.getType());
        if (settings == null) settings = new HashMap<>();

        Map<UserSetting<?>, SettingValue<?>> settingsReal = new HashMap<>();
        settings.forEach((name, valueRaw) -> {
            UserSetting<?> setting = UserSetting.getByName(name);
            if (setting == null) return;

            settingsReal.put(setting, SettingValue.of(setting.parse(valueRaw)));
        });
        return new BasicSettings(settingsReal);
    }

    @Override
    public JsonElement serialize(BasicSettings settings, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        Map<String, String> settingsRaw = new HashMap<>();
        settings.getSettings().forEach((setting, value) -> {
            if (setting.isPersistent()) {
                settingsRaw.put(setting.getName(), String.valueOf(value.getValue()));
            }
        });

        object.add("settings", context.serialize(settingsRaw));
        return object;
    }
}
