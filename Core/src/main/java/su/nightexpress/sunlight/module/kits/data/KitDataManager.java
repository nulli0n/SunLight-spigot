package su.nightexpress.sunlight.module.kits.data;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.db.statement.condition.Operator;
import su.nightexpress.nightcore.db.statement.condition.Wheres;
import su.nightexpress.nightcore.db.statement.template.SelectStatement;
import su.nightexpress.nightcore.db.table.Table;
import su.nightexpress.sunlight.data.DataHandler;

import java.util.Collection;
import java.util.List;

public class KitDataManager {

    private final DataHandler dataHandler;

    private Table dataTable;

    public KitDataManager(@NotNull DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    public void init() {
        this.dataTable = Table.builder(this.dataHandler.getTablePrefix() + "_kit_data")
            .withColumn(KitDataColumns.PLAYER_ID)
            .withColumn(KitDataColumns.KIT_ID)
            .withColumn(KitDataColumns.COOLDOWN_DATE)
            //.foreignKey(KitDataColumns.PLAYER_ID, this.dataHandler.getUsersTable(), UserColumns.UUID)
            .build();

        this.dataHandler.createTable(this.dataTable);
    }

    @NotNull
    public List<KitData> loadData() {
        return this.dataHandler.selectAny(this.dataTable, SelectStatement.builder(KitDataQueries.KIT_DATA_ROW_MAPPER).build());
    }

    public void addData(@NotNull KitData data) {
        this.dataHandler.insert(this.dataTable, KitDataQueries.KIT_DATA_INSERT_STATEMENT, data);
    }

    public void saveData(@NotNull Collection<KitData> data) {
        Wheres<KitData> wheres = Wheres
            .whereUUID(KitDataColumns.PLAYER_ID, KitData::getPlayerId)
            .and(KitDataColumns.KIT_ID, Operator.EQUALS_IGNORE_CASE, KitData::getKitId);

        this.dataHandler.update(this.dataTable, KitDataQueries.KIT_DATA_UPDATE_STATEMENT, data, wheres);
    }

    /*public void deleteData(@NotNull KitData data) {
        Wheres<Object> wheres = Wheres
            .where(KitDataColumns.PLAYER_ID, Operator.EQUALS, o -> data.getPlayerId())
            .and(KitDataColumns.KIT_ID, Operator.EQUALS_IGNORE_CASE, o -> data.getKitId());

        this.dataHandler.delete(this.dataTable, wheres);
    }*/

    public void deleteData(@NotNull String kitId) {
        Wheres<Object> wheres = Wheres.where(KitDataColumns.KIT_ID, Operator.EQUALS_IGNORE_CASE, o -> kitId);

        this.dataHandler.delete(this.dataTable, wheres);
    }
}
