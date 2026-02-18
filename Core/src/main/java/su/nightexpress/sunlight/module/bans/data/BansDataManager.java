package su.nightexpress.sunlight.module.bans.data;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.db.column.Column;
import su.nightexpress.nightcore.db.statement.condition.Operator;
import su.nightexpress.nightcore.db.statement.condition.Wheres;
import su.nightexpress.nightcore.db.table.Table;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.data.DataHandler;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.punishment.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class BansDataManager {

    static final Column<UUID>    COLUMN_BAN_ID      = Column.uuidType("banId").primaryKey().build();
    static final Column<String>  COLUMN_TYPE        = Column.stringType("type", 32).build();
    static final Column<String>  COLUMN_REASON      = Column.mediumText("reason").build();
    static final Column<String>  COLUMN_ADMIN       = Column.stringType("admin", 64).build();
    static final Column<Long>    COLUMN_DURATION    = Column.longType("duration").build();
    static final Column<Long>    COLUMN_CREATE_DATE = Column.longType("createDate").build();
    static final Column<Long>    COLUMN_EXPIRE_DATE = Column.longType("expireDate").build();
    static final Column<Boolean> COLUMN_ACTIVE      = Column.booleanType("active").build();

    static final Column<String> COLUMN_ADDRESS     = Column.stringType("address", 16).build();
    static final Column<UUID>   COLUMN_PLAYER_ID   = Column.uuidType("playerId").build();
    static final Column<String> COLUMN_PLAYER_NAME = Column.stringType("playerName", 32).build();

    //private final SunLightPlugin plugin;
    private final DataHandler dataHandler;
    private final BansModule module;

    private Table  tableBannedPlayers;
    private Table tableBannedIPs;

    public BansDataManager(@NotNull SunLightPlugin plugin, @NotNull DataHandler dataHandler, @NotNull BansModule module) {
        //this.plugin = plugin;
        this.dataHandler = dataHandler;
        this.module = module;
    }

    public void init(@NotNull String tablePrefix) {
        this.tableBannedPlayers = Table.builder(tablePrefix + "_banned_players")
            .withColumn(
                COLUMN_BAN_ID,
                COLUMN_TYPE,
                COLUMN_PLAYER_ID,
                COLUMN_PLAYER_NAME,
                COLUMN_REASON,
                COLUMN_ADMIN,
                COLUMN_DURATION,
                COLUMN_CREATE_DATE,
                COLUMN_EXPIRE_DATE,
                COLUMN_ACTIVE
            )
            .build();

        this.tableBannedIPs = Table.builder(tablePrefix + "_banned_inets")
            .withColumn(
                COLUMN_BAN_ID,
                COLUMN_TYPE,
                COLUMN_ADDRESS,
                COLUMN_REASON,
                COLUMN_ADMIN,
                COLUMN_DURATION,
                COLUMN_CREATE_DATE,
                COLUMN_EXPIRE_DATE,
                COLUMN_ACTIVE
            )
            .build();

        this.dataHandler.createTable(this.tableBannedPlayers);
        this.dataHandler.createTable(this.tableBannedIPs);

        this.purgeOldEntries();

        this.dataHandler.addTableSync(this.tableBannedPlayers, this::syncPlayerPunishments);
        this.dataHandler.addTableSync(this.tableBannedIPs, this::syncInetPunishments);
    }

    private void syncPlayerPunishments(@NotNull ResultSet resultSet) {
        try {
            PlayerPunishment punishment = BansQueries.PLAYER_PUNISHMENT_LOADER.map(resultSet);
            if (punishment == null) return;

            PunishmentType type = punishment.getType();
            PunishmentRepository repository = this.module.getPunishmentRepository(type);
            repository.removePlayerPunishment(punishment);
            repository.addPlayerPunishment(punishment);
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private void syncInetPunishments(@NotNull ResultSet resultSet) {
        try {
            InetPunishment punishment = BansQueries.INET_PUNISHMENT_LOADER.map(resultSet);
            if (punishment == null) return;

            PunishmentType type = punishment.getType();
            PunishmentRepository repository = this.module.getPunishmentRepository(type);
            repository.removeInetPunishment(punishment);
            repository.addInetPunishment(punishment);
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void purgeOldEntries() {
        List<Table> tables = List.of(this.tableBannedIPs, this.tableBannedPlayers);

        for (Table table : tables) {
            LocalDateTime deadline = TimeUtil.getCurrentDateTime().minusDays(this.dataHandler.getConfig().getPurgePeriod());
            long deadlineMs = TimeUtil.toEpochMillis(deadline);

            this.dataHandler.delete(table, Wheres.where(COLUMN_EXPIRE_DATE, Operator.SMALLER, o -> deadlineMs).and(COLUMN_EXPIRE_DATE, Operator.GREATER, o -> 0));
        }
    }

    @NotNull
    public List<PlayerPunishment> getPlayerPunishments() {
        return this.dataHandler.selectAny(this.tableBannedPlayers, BansQueries.SELECT_PLAYER_PUNISHMENT);
    }

    @NotNull
    public List<InetPunishment> getInetPunishments() {
        return this.dataHandler.selectAny(this.tableBannedIPs, BansQueries.SELECT_INET_PUNISHMENT);
    }

    public void insertPunishment(@NotNull InetPunishment bannedIp) {
        this.dataHandler.insert(this.tableBannedIPs, BansQueries.INSERT_IP, bannedIp);
    }

    public void insertPunishment(@NotNull PlayerPunishment bannedPlayer) {
        this.dataHandler.insert(this.tableBannedPlayers, BansQueries.INSERT_PLAYER, bannedPlayer);
    }

    public void updateInetPunishments(@NotNull Collection<InetPunishment> punishments) {
        if (punishments.isEmpty()) return;

        this.dataHandler.update(this.tableBannedIPs, BansQueries.UPDATE_IP, punishments, Wheres.whereUUID(COLUMN_BAN_ID, AbstractPunishment::getId));
    }

    public void updatePlayerPunishments(@NotNull Collection<PlayerPunishment> punishments) {
        if (punishments.isEmpty()) return;

        this.dataHandler.update(this.tableBannedPlayers, BansQueries.UPDATE_PLAYER, punishments, Wheres.whereUUID(COLUMN_BAN_ID, AbstractPunishment::getId));
    }

    public void deleteInetPunishment(@NotNull InetPunishment inetPunishment) {
        this.dataHandler.delete(this.tableBannedIPs, inetPunishment, Wheres.whereUUID(COLUMN_BAN_ID, AbstractPunishment::getId));
    }

    public void deletePlayerPunishment(@NotNull PlayerPunishment playerPunishment) {
        this.dataHandler.delete(this.tableBannedPlayers, playerPunishment, Wheres.whereUUID(COLUMN_BAN_ID, AbstractPunishment::getId));
    }
}
