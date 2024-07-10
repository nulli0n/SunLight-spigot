package su.nightexpress.sunlight.data.serialize;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import su.nightexpress.sunlight.core.user.settings.SettingValue;
import su.nightexpress.sunlight.core.user.settings.Setting;
import su.nightexpress.sunlight.data.user.UserSettings;
import su.nightexpress.sunlight.core.user.settings.SettingRegistry;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class UserSettingsSerializer implements JsonSerializer<UserSettings>, JsonDeserializer<UserSettings> {

    @Override
    public UserSettings deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();

        Map<String, String> settings = context.deserialize(object.get("settings"), new TypeToken<Map<String, String>>(){}.getType());
        if (settings == null) settings = new HashMap<>();

        Map<String, SettingValue<?>> settingsReal = new HashMap<>();
        settings.forEach((name, valueRaw) -> {
            Setting<?> setting = SettingRegistry.getByName(name);
            if (setting == null) return;

            settingsReal.put(setting.getName(), SettingValue.of(setting.fromString(valueRaw)));
        });

        return new UserSettings(settingsReal);
    }

    @Override
    public JsonElement serialize(UserSettings settings, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        Map<String, String> settingsRaw = new HashMap<>();

        settings.getValues().forEach((name, value) -> {
            Setting<?> setting = SettingRegistry.getByName(name);
            if (setting == null || !setting.isPersistent()) return;

            settingsRaw.put(setting.getName(), setting.toString(value.getValue()));
        });

        object.add("settings", context.serialize(settingsRaw));
        return object;
    }
}
