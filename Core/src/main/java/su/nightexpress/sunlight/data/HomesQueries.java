package su.nightexpress.sunlight.data;

import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import su.nightexpress.nightcore.db.sql.query.impl.DeleteQuery;
import su.nightexpress.nightcore.db.sql.query.impl.InsertQuery;
import su.nightexpress.nightcore.db.sql.query.impl.UpdateQuery;
import su.nightexpress.nightcore.db.sql.util.WhereOperator;
import su.nightexpress.nightcore.util.ItemNbt;
import su.nightexpress.nightcore.util.LocationUtil;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.sunlight.SunLightAPI;
import su.nightexpress.sunlight.module.homes.impl.Home;
import su.nightexpress.sunlight.module.homes.impl.HomeType;
import su.nightexpress.sunlight.utils.UserInfo;

import java.sql.ResultSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

public class HomesQueries {

    public static final Function<ResultSet, Home> HOME_LOADER = resultSet -> {
        try {
            String id = resultSet.getString(DataHandler.HOME_ID.getName());
            UUID ownerId = UUID.fromString(resultSet.getString(DataHandler.HOME_OWNER_ID.getName()));
            String ownerName = resultSet.getString(DataHandler.HOME_OWNER_NAME.getName());
            String name = resultSet.getString(DataHandler.HOME_NAME.getName());
            ItemStack icon = ItemNbt.decompress(resultSet.getString(DataHandler.HOME_ICON.getName()));
            if (icon == null) icon = new ItemStack(Material.GRASS_BLOCK);

            Location location = LocationUtil.deserialize(resultSet.getString(DataHandler.HOME_LOCATION.getName()));
            if (location == null) return null;

            HomeType type = StringUtil.getEnum(resultSet.getString(DataHandler.HOME_TYPE.getName()), HomeType.class).orElse(HomeType.PRIVATE);
            Set<UserInfo> invitedPlayers = DataHandler.GSON.fromJson(resultSet.getString(DataHandler.HOME_INVITED_PLAYERS.getName()), new TypeToken<Set<UserInfo>>() {
            }.getType());
            boolean isDefault = resultSet.getBoolean(DataHandler.HOME_IS_DEFAULT.getName());
            boolean isRespawnPoint = resultSet.getBoolean(DataHandler.HOME_IS_RESPAWN_POINT.getName());

            UserInfo owner = new UserInfo(ownerId, ownerName);
            return new Home(SunLightAPI.getPlugin(), id, owner, name, icon, location, type, invitedPlayers, isDefault, isRespawnPoint);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    };

    public static final InsertQuery<Home> HOME_INSERT = new InsertQuery<Home>()
        .setValue(DataHandler.HOME_ID, Home::getId)
        .setValue(DataHandler.HOME_OWNER_ID, home -> home.getOwner().getId().toString())
        .setValue(DataHandler.HOME_OWNER_NAME, home -> home.getOwner().getName())
        .setValue(DataHandler.HOME_NAME, Home::getName)
        .setValue(DataHandler.HOME_ICON, home -> String.valueOf(ItemNbt.compress(home.getIcon())))
        .setValue(DataHandler.HOME_LOCATION, home -> String.valueOf(LocationUtil.serialize(home.getLocation())))
        .setValue(DataHandler.HOME_TYPE, home -> home.getType().name())
        .setValue(DataHandler.HOME_INVITED_PLAYERS, home -> DataHandler.GSON.toJson(home.getInvitedPlayers()))
        .setValue(DataHandler.HOME_IS_DEFAULT, home -> String.valueOf(home.isDefault() ? 1 : 0))
        .setValue(DataHandler.HOME_IS_RESPAWN_POINT, home -> String.valueOf(home.isRespawnPoint() ? 1 : 0));

    public static final UpdateQuery<Home> HOME_UPDATE = new UpdateQuery<Home>()
        .setValue(DataHandler.HOME_OWNER_ID, home -> home.getOwner().getId().toString())
        .setValue(DataHandler.HOME_OWNER_NAME, home -> home.getOwner().getName())
        .setValue(DataHandler.HOME_NAME, Home::getName)
        .setValue(DataHandler.HOME_ICON, home -> String.valueOf(ItemNbt.compress(home.getIcon())))
        .setValue(DataHandler.HOME_LOCATION, home -> String.valueOf(LocationUtil.serialize(home.getLocation())))
        .setValue(DataHandler.HOME_TYPE, home -> home.getType().name())
        .setValue(DataHandler.HOME_INVITED_PLAYERS, home -> DataHandler.GSON.toJson(home.getInvitedPlayers()))
        .setValue(DataHandler.HOME_IS_DEFAULT, home -> String.valueOf(home.isDefault() ? 1 : 0))
        .setValue(DataHandler.HOME_IS_RESPAWN_POINT, home -> String.valueOf(home.isRespawnPoint() ? 1 : 0))
        .where(DataHandler.HOME_ID, WhereOperator.EQUAL, Home::getId)
        .where(DataHandler.HOME_OWNER_ID, WhereOperator.EQUAL, home -> home.getOwner().getId().toString());

    public static final DeleteQuery<UUID> HOME_DELETE_OWNER_ID = new DeleteQuery<UUID>()
        .where(DataHandler.HOME_OWNER_ID, WhereOperator.EQUAL, Object::toString);

    public static final DeleteQuery<String> HOME_DELETE_OWNER_NAME = new DeleteQuery<String>()
        .where(DataHandler.HOME_OWNER_NAME, WhereOperator.EQUAL, Object::toString);

    public static final DeleteQuery<Home> HOME_DELETE = new DeleteQuery<Home>()
        .where(DataHandler.HOME_ID, WhereOperator.EQUAL, Home::getId)
        .where(DataHandler.HOME_OWNER_ID, WhereOperator.EQUAL, home -> home.getOwner().getId().toString());
}
