package su.nightexpress.sunlight.module.bans.data;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.db.statement.RowMapper;
import su.nightexpress.nightcore.db.statement.template.InsertStatement;
import su.nightexpress.nightcore.db.statement.template.SelectStatement;
import su.nightexpress.nightcore.db.statement.template.UpdateStatement;
import su.nightexpress.nightcore.util.Enums;
import su.nightexpress.sunlight.module.bans.punishment.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.UUID;

public class BansQueries {

    private static final RowMapper<PunishmentData> PUNISHMENT_DATA_LOADER = resultSet -> {
        try {
            UUID id = UUID.fromString(resultSet.getString(BansDataManager.COLUMN_BAN_ID.getName()));
            PunishmentType type = Enums.get(resultSet.getString(BansDataManager.COLUMN_TYPE.getName()),
                PunishmentType.class);
            if (type == null) return null;

            String reason = resultSet.getString(BansDataManager.COLUMN_REASON.getName());
            String admin = resultSet.getString(BansDataManager.COLUMN_ADMIN.getName());
            long duration = resultSet.getLong(BansDataManager.COLUMN_DURATION.getName());
            long created = resultSet.getLong(BansDataManager.COLUMN_CREATE_DATE.getName());
            long expired = resultSet.getLong(BansDataManager.COLUMN_EXPIRE_DATE.getName());

            return new PunishmentData(id, type, reason, admin, duration, created, expired);
        }
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    };

    public static final RowMapper<InetPunishment> INET_PUNISHMENT_LOADER = resultSet -> {
        try {
            PunishmentData data = PUNISHMENT_DATA_LOADER.map(resultSet);
            if (data == null) return null;

            boolean active = resultSet.getBoolean(BansDataManager.COLUMN_ACTIVE.getName());
            String rawAddress = resultSet.getString(BansDataManager.COLUMN_ADDRESS.getName());

            try {
                InetAddress address = InetAddress.getByName(rawAddress);
                return new InetPunishment(address, data, active);
            }
            catch (UnknownHostException exception) {
                exception.printStackTrace();
                return null;
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    };

    public static final RowMapper<PlayerPunishment> PLAYER_PUNISHMENT_LOADER = resultSet -> {
        try {
            PunishmentData data = PUNISHMENT_DATA_LOADER.map(resultSet);
            if (data == null) return null;

            boolean active = resultSet.getBoolean(BansDataManager.COLUMN_ACTIVE.getName());
            UUID userId = UUID.fromString(resultSet.getString(BansDataManager.COLUMN_PLAYER_ID.getName()));
            String user = resultSet.getString(BansDataManager.COLUMN_PLAYER_NAME.getName());

            return new PlayerPunishment(userId, user, data, active);
        }
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    };

    public static final SelectStatement<PlayerPunishment> SELECT_PLAYER_PUNISHMENT = SelectStatement.builder(
        PLAYER_PUNISHMENT_LOADER).build();

    public static final SelectStatement<InetPunishment> SELECT_INET_PUNISHMENT = SelectStatement.builder(
        INET_PUNISHMENT_LOADER).build();

    private static <T extends AbstractPunishment> InsertStatement.Builder<T> forPunishmentInsert(@NotNull Class<T> type) {
        return InsertStatement.builder(type)
            .setUUID(BansDataManager.COLUMN_BAN_ID, AbstractPunishment::getId)
            .setString(BansDataManager.COLUMN_TYPE, punishment -> punishment.getType().name())
            .setBoolean(BansDataManager.COLUMN_ACTIVE, AbstractPunishment::isActive)
            .setString(BansDataManager.COLUMN_REASON, AbstractPunishment::getReason)
            .setString(BansDataManager.COLUMN_ADMIN, AbstractPunishment::getWho)
            .setLong(BansDataManager.COLUMN_DURATION, AbstractPunishment::getDuration)
            .setLong(BansDataManager.COLUMN_CREATE_DATE, AbstractPunishment::getCreationDate)
            .setLong(BansDataManager.COLUMN_EXPIRE_DATE, AbstractPunishment::getExpirationDate);
    }

    private static <T extends AbstractPunishment> UpdateStatement.Builder<T> forPunishmentUpdate(@NotNull Class<T> type) {
        return UpdateStatement.builder(type)
            .setBoolean(BansDataManager.COLUMN_ACTIVE, AbstractPunishment::isActive);
    }

    public static final InsertStatement<InetPunishment> INSERT_IP = forPunishmentInsert(InetPunishment.class)
        .setString(BansDataManager.COLUMN_ADDRESS, InetPunishment::getRawAddress)
        .build();

    public static final InsertStatement<PlayerPunishment> INSERT_PLAYER = forPunishmentInsert(PlayerPunishment.class)
        .setUUID(BansDataManager.COLUMN_PLAYER_ID, PlayerPunishment::getPlayerId)
        .setString(BansDataManager.COLUMN_PLAYER_NAME, PlayerPunishment::getPlayerName)
        .build();

    public static final UpdateStatement<InetPunishment> UPDATE_IP = forPunishmentUpdate(InetPunishment.class).build();

    public static final UpdateStatement<PlayerPunishment> UPDATE_PLAYER = forPunishmentUpdate(PlayerPunishment.class)
        .setString(BansDataManager.COLUMN_PLAYER_NAME, PlayerPunishment::getPlayerName)
        .build();
}
