package su.nightexpress.sunlight.data.serialize;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import su.nightexpress.sunlight.core.user.IgnoredUser;
import su.nightexpress.sunlight.utils.UserInfo;

import java.lang.reflect.Type;

public class IgnoredUserSerializer implements JsonSerializer<IgnoredUser>, JsonDeserializer<IgnoredUser> {

    @Override
    public IgnoredUser deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = element.getAsJsonObject();
        if (!object.has("userInfo")) return null;

        UserInfo userInfo = context.deserialize(object.get("userInfo"), new TypeToken<UserInfo>(){}.getType());
        boolean ignoreChat = object.get("ignoreChat").getAsBoolean();
        boolean ignoreConversation = object.get("ignoreConversation").getAsBoolean();
        boolean ignoreTeleports = object.get("ignoreTeleports").getAsBoolean();

        return new IgnoredUser(userInfo, ignoreChat, ignoreConversation, ignoreTeleports);
    }

    @Override
    public JsonElement serialize(IgnoredUser ignoredUser, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.add("userInfo", context.serialize(ignoredUser.getUserInfo()));
        object.addProperty("ignoreChat", ignoredUser.isHideChatMessages());
        object.addProperty("ignoreConversation", ignoredUser.isDenyConversations());
        object.addProperty("ignoreTeleports", ignoredUser.isDenyTeleports());
        return object;
    }
}
