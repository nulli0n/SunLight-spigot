package su.nightexpress.sunlight.data.serialize;

import com.google.gson.*;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.sunlight.core.cooldown.CooldownInfo;
import su.nightexpress.sunlight.core.cooldown.CooldownType;

import java.lang.reflect.Type;

public class CooldownSerializer implements JsonSerializer<CooldownInfo>, JsonDeserializer<CooldownInfo> {

    @Override
    public CooldownInfo deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = element.getAsJsonObject();

        CooldownType cooldownType = StringUtil.getEnum(object.get("type").getAsString(), CooldownType.class).orElse(null);
        if (cooldownType == null) return null;

        String objectId = object.get("objectId").getAsString();
        long expireDate = object.get("expireDate").getAsLong();

        return new CooldownInfo(cooldownType, objectId, expireDate);
    }

    @Override
    public JsonElement serialize(CooldownInfo info, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("type", info.getType().name());
        object.addProperty("objectId", info.getObjectId());
        object.addProperty("expireDate", info.getExpireDate());
        return object;
    }
}
