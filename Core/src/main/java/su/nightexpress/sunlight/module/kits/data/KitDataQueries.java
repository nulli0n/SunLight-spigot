package su.nightexpress.sunlight.module.kits.data;

import su.nightexpress.nightcore.db.statement.RowMapper;
import su.nightexpress.nightcore.db.statement.template.InsertStatement;
import su.nightexpress.nightcore.db.statement.template.UpdateStatement;

import java.sql.SQLException;
import java.util.UUID;

public class KitDataQueries {

    public static final RowMapper<KitData> KIT_DATA_ROW_MAPPER = resultSet -> {
        try {
            UUID playerId = KitDataColumns.PLAYER_ID.readOrThrow(resultSet);
            String kitId = KitDataColumns.KIT_ID.readOrThrow(resultSet);
            long cooldownDate = KitDataColumns.COOLDOWN_DATE.readOrThrow(resultSet);

            return new KitData(playerId, kitId, cooldownDate);
        }
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    };

    public static final InsertStatement<KitData> KIT_DATA_INSERT_STATEMENT = InsertStatement.<KitData>builder()
        .setUUID(KitDataColumns.PLAYER_ID, KitData::getPlayerId)
        .setString(KitDataColumns.KIT_ID, KitData::getKitId)
        .setLong(KitDataColumns.COOLDOWN_DATE, KitData::getCooldownDate)
        .build();

    public static final UpdateStatement<KitData> KIT_DATA_UPDATE_STATEMENT = UpdateStatement.<KitData>builder()
        .setString(KitDataColumns.KIT_ID, KitData::getKitId)
        .setLong(KitDataColumns.COOLDOWN_DATE, KitData::getCooldownDate)
        .build();
}
