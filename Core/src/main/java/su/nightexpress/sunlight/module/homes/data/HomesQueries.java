package su.nightexpress.sunlight.module.homes.data;

import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;
import org.bukkit.World;
import su.nightexpress.nightcore.db.statement.RowMapper;
import su.nightexpress.nightcore.db.statement.template.InsertStatement;
import su.nightexpress.nightcore.db.statement.template.UpdateStatement;
import su.nightexpress.nightcore.user.UserInfo;
import su.nightexpress.nightcore.util.Enums;
import su.nightexpress.nightcore.util.LocationUtil;
import su.nightexpress.nightcore.util.geodata.pos.BlockPos;
import su.nightexpress.sunlight.data.DataHandler;
import su.nightexpress.sunlight.module.homes.impl.Home;
import su.nightexpress.sunlight.module.homes.impl.HomeType;

import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

public class HomesQueries {

    public static final RowMapper<Home> OLD_HOME_ROW_MAPPER = resultSet -> {
        try {
            String id = HomeColumns.ID.readOrThrow(resultSet);
            UUID ownerId = HomeColumns.OWNER_ID.readOrThrow(resultSet);
            String ownerName = HomeColumns.OWNER_NAME.readOrThrow(resultSet);
            String name = HomeColumns.NAME.readOrThrow(resultSet);
            String iconId = HomeColumns.ICON_ID.readOrThrow(resultSet);

            Location location = LocationUtil.deserialize(HomeColumns.LOCATION.readOrThrow(resultSet));
            if (location == null) return null;

            World world = location.getWorld();
            if (world == null) return null;

            HomeType type = Enums.parse(HomeColumns.TYPE.readOrThrow(resultSet), HomeType.class).orElse(HomeType.PRIVATE);
            Set<UserInfo> invitedPlayers = DataHandler.GSON.fromJson(HomeColumns.INVITED_PLAYERS.readOrThrow(resultSet), new TypeToken<Set<UserInfo>>() {}.getType());
            boolean favorite = HomeColumns.FAVORITE.readOrThrow(resultSet);

            UserInfo owner = new UserInfo(ownerId, ownerName);
            return new Home(id, owner, name, iconId, BlockPos.from(location), location.getWorld().getName(), type, invitedPlayers, favorite);
        }
        catch (SQLException | NoSuchElementException exception) {
            exception.printStackTrace();
            return null;
        }
    };

    public static final RowMapper<Home> HOME_ROW_MAPPER = resultSet -> {
        try {
            String id = HomeColumns.ID.readOrThrow(resultSet);
            UUID ownerId = HomeColumns.OWNER_ID.readOrThrow(resultSet);
            String ownerName = HomeColumns.OWNER_NAME.readOrThrow(resultSet);
            String name = HomeColumns.NAME.readOrThrow(resultSet);
            String iconId = HomeColumns.ICON_ID.readOrThrow(resultSet);

            BlockPos blockPos = BlockPos.deserialize(HomeColumns.POSITION.readOrThrow(resultSet));
            String worldName = HomeColumns.WORLD.readOrThrow(resultSet);

            HomeType type = Enums.parse(HomeColumns.TYPE.readOrThrow(resultSet), HomeType.class).orElse(HomeType.PRIVATE);
            Set<UserInfo> invitedPlayers = DataHandler.GSON.fromJson(HomeColumns.INVITED_PLAYERS.readOrThrow(resultSet), new TypeToken<Set<UserInfo>>() {}.getType());
            boolean favorite = HomeColumns.FAVORITE.readOrThrow(resultSet);

            UserInfo owner = new UserInfo(ownerId, ownerName);
            return new Home(id, owner, name, iconId, blockPos, worldName, type, invitedPlayers, favorite);
        }
        catch (SQLException | NoSuchElementException exception) {
            exception.printStackTrace();
            return null;
        }
    };

    public static final InsertStatement<Home> HOME_INSERT = InsertStatement.<Home>builder()
        .setString(HomeColumns.ID, Home::getId)
        .setUUID(HomeColumns.OWNER_ID, home -> home.getOwner().id())
        .setString(HomeColumns.OWNER_NAME, home -> home.getOwner().name())
        .setString(HomeColumns.NAME, Home::getName)
        .setString(HomeColumns.ICON_ID, Home::getIconId)
        .setString(HomeColumns.POSITION, home -> home.getBlockPos().serialize())
        .setString(HomeColumns.WORLD, Home::getWorldName)
        .setString(HomeColumns.TYPE, home -> home.getType().name())
        .setString(HomeColumns.INVITED_PLAYERS, home -> DataHandler.GSON.toJson(home.getInvitedPlayers()))
        .setBoolean(HomeColumns.FAVORITE, Home::isFavorite)
        .build();


    public static final UpdateStatement<Home> HOME_UPDATE = UpdateStatement.<Home>builder()
        .setString(HomeColumns.ID, Home::getId)
        .setUUID(HomeColumns.OWNER_ID, home -> home.getOwner().id())
        .setString(HomeColumns.OWNER_NAME, home -> home.getOwner().name())
        .setString(HomeColumns.NAME, Home::getName)
        .setString(HomeColumns.ICON_ID, Home::getIconId)
        .setString(HomeColumns.POSITION, home -> home.getBlockPos().serialize())
        .setString(HomeColumns.WORLD, Home::getWorldName)
        .setString(HomeColumns.TYPE, home -> home.getType().name())
        .setString(HomeColumns.INVITED_PLAYERS, home -> DataHandler.GSON.toJson(home.getInvitedPlayers()))
        .setBoolean(HomeColumns.FAVORITE, Home::isFavorite)
        .build();
}
