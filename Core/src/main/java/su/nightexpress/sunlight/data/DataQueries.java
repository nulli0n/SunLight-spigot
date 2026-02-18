package su.nightexpress.sunlight.data;

import su.nightexpress.nightcore.db.statement.RowMapper;
import su.nightexpress.nightcore.db.statement.template.InsertStatement;
import su.nightexpress.nightcore.db.statement.template.SelectStatement;
import su.nightexpress.nightcore.db.statement.template.UpdateStatement;
import su.nightexpress.nightcore.user.UserInfo;
import su.nightexpress.nightcore.user.UserTemplate;
import su.nightexpress.sunlight.command.CommandKey;
import su.nightexpress.sunlight.user.SunUser;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class DataQueries {

    public static final RowMapper<InetAddress> INET_MAPPER = resultSet -> {
        try {
            return UserColumns.INET_ADDRESS.read(resultSet).map(string -> {
                try {
                    return InetAddress.getByName(string);
                }
                catch (UnknownHostException exception) {
                    exception.printStackTrace();
                    return null;
                }
            }).orElse(null);
        }
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    };

    public static final RowMapper<UserInfo> PROFILE_MAPPER = resultSet -> {
        try {
            UUID uuid = UserColumns.UUID.read(resultSet).orElseThrow();
            String name = UserColumns.NAME.read(resultSet).orElseThrow();

            return new UserInfo(uuid, name);
        }
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    };

    public static final RowMapper<SunUser> USER_MAPPER = (resultSet) -> {
        try {
            UUID uuid = UserColumns.UUID.readOrThrow(resultSet);
            String name = UserColumns.NAME.readOrThrow(resultSet);
            long dateCreated = UserColumns.DATE_CREATED.readOrThrow(resultSet);
            long lastOnline = UserColumns.LAST_ONLINE.readOrThrow(resultSet);
            InetAddress latestAddress = INET_MAPPER.map(resultSet);
            Map<CommandKey, Long> commandCooldowns = UserColumns.COMMAND_COOLDOWNS.readOrThrow(resultSet);
            Map<String, Object> properties = UserColumns.PROPERTIES.readOrThrow(resultSet);

            return new SunUser(uuid, name, dateCreated, lastOnline, latestAddress, commandCooldowns, properties);
        }
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    };

    public static final UpdateStatement<SunUser> UPDATE_USER = UpdateStatement.builder(SunUser.class)
        .setUUID(UserColumns.UUID, UserTemplate::getId)
        .setString(UserColumns.NAME, UserTemplate::getName)
        .setLong(UserColumns.LAST_ONLINE, SunUser::getLastOnline)
        .setString(UserColumns.INET_ADDRESS, user -> user.getLatestAddress().map(InetAddress::getHostAddress).orElse("0.0.0.0"))
        .setString(UserColumns.COMMAND_COOLDOWNS, user -> DataHandler.GSON.toJson(user.getCommandCooldowns()))
        .setString(UserColumns.PROPERTIES, user -> DataHandler.GSON.toJson(user.getPropertiesToSave()))
        .build();

    public static final UpdateStatement<SunUser> UPDATE_USER_TINY = UpdateStatement.builder(SunUser.class)
        .setString(UserColumns.NAME, UserTemplate::getName)
        .setString(UserColumns.INET_ADDRESS, user -> user.getLatestAddress().map(InetAddress::getHostAddress).orElse("0.0.0.0"))
        .setLong(UserColumns.LAST_ONLINE, SunUser::getLastOnline)
        .build();

    public static final InsertStatement<SunUser> INSERT_USER = InsertStatement.builder(SunUser.class)
        .setUUID(UserColumns.UUID, UserTemplate::getId)
        .setString(UserColumns.NAME, UserTemplate::getName)
        .setLong(UserColumns.DATE_CREATED, SunUser::getDateCreated)
        .setLong(UserColumns.LAST_ONLINE, SunUser::getLastOnline)
        .setString(UserColumns.INET_ADDRESS, user -> user.getLatestAddress().map(InetAddress::getHostAddress).orElse("0.0.0.0"))
        .setString(UserColumns.COMMAND_COOLDOWNS, user -> DataHandler.GSON.toJson(user.getCommandCooldowns()))
        .setString(UserColumns.PROPERTIES, user -> DataHandler.GSON.toJson(user.getPropertiesToSave()))
        .build();

    public static final SelectStatement<SunUser> SELECT_USER = SelectStatement.builder(USER_MAPPER).build();

    public static final SelectStatement<UserInfo> SELECT_PROFILE = SelectStatement.builder(PROFILE_MAPPER).column(UserColumns.NAME, UserColumns.UUID).build();

    public static final SelectStatement<InetAddress> SELECT_INET = SelectStatement.builder(INET_MAPPER).column(UserColumns.INET_ADDRESS).build();
}
