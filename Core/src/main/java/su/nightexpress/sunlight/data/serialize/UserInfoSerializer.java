package su.nightexpress.sunlight.data.serialize;

import com.google.gson.*;
import su.nightexpress.sunlight.utils.UserInfo;

import java.lang.reflect.Type;
import java.util.UUID;

public class UserInfoSerializer implements JsonSerializer<UserInfo>, JsonDeserializer<UserInfo> {

    @Override
    public UserInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();

        UUID id = UUID.fromString(object.get("id").getAsString());
        String name = object.get("name").getAsString();

        return new UserInfo(id, name);
    }

    @Override
    public JsonElement serialize(UserInfo userInfo, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        object.addProperty("id", userInfo.getId().toString());
        object.addProperty("name", userInfo.getName());

        return object;
    }
}
