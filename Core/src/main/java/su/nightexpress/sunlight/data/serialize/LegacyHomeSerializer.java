package su.nightexpress.sunlight.data.serialize;

import com.google.gson.*;
import org.bukkit.Location;
import org.bukkit.Material;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.LocationUtil;
import su.nightexpress.sunlight.module.homes.impl.LegacyHome;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class LegacyHomeSerializer implements JsonSerializer<LegacyHome>, JsonDeserializer<LegacyHome> {

    @Override
    public JsonElement serialize(LegacyHome src, Type type, JsonSerializationContext contex) {

        JsonObject object = new JsonObject();
        object.addProperty("id", src.getId());
        object.addProperty("owner", src.getOwner());
        object.addProperty("name", Colorizer.plain(src.getName()));
        object.addProperty("iconMaterial", src.getIconMaterial().name());
        object.addProperty("location", LocationUtil.serialize(src.getLocation()));
        object.add("invitedPlayers", contex.serialize(src.getInvitedPlayers()));
        object.addProperty("isRespawnPoint", src.isRespawnPoint());

        return object;
    }

    @Override
    public LegacyHome deserialize(JsonElement json, Type type, JsonDeserializationContext contex)
        throws JsonParseException {

        JsonObject object = json.getAsJsonObject();

        String id = object.get("id").getAsString();
        String name = object.get("name") == null ? id : object.get("name").getAsString();
        String owner = object.get("owner") == null ? "" : object.get("owner").getAsString();
        Material iconMaterial = object.get("iconMaterial") == null ? null : Material.getMaterial(object.get("iconMaterial").getAsString());
        if (iconMaterial == null) iconMaterial = Material.GRASS_BLOCK;

        Location location = LocationUtil.deserialize((object.get("location") == null ? object.get("loc") : object.get("location")).getAsString());
        if (location == null) return null;

        Set<String> invitedPlayers = new HashSet<>();
        JsonArray jInvites = (object.get("invitedPlayers") == null ? new JsonArray() : object.get("invitedPlayers")).getAsJsonArray();
        for (JsonElement e : jInvites) {
            invitedPlayers.add(e.getAsString());
        }
        boolean isRespawnPoint = (object.get("isRespawnPoint") == null ? object.get("respawn") : object.get("isRespawnPoint")).getAsBoolean();

        return new LegacyHome(id, owner, name, iconMaterial, location, invitedPlayers, isRespawnPoint);
    }
}
