package su.nightexpress.sunlight.module.bans.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.database.AbstractDataHandler;
import su.nightexpress.nightcore.database.DatabaseConfig;
import su.nightexpress.nightcore.database.sql.SQLColumn;
import su.nightexpress.nightcore.database.sql.SQLCondition;
import su.nightexpress.nightcore.database.sql.SQLQueries;
import su.nightexpress.nightcore.database.sql.SQLValue;
import su.nightexpress.nightcore.database.sql.column.ColumnType;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.punishment.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

public class BansDataHandler extends AbstractDataHandler<SunLightPlugin> {

    private static final SQLColumn COLUMN_BAN_ID      = SQLColumn.of("banId", ColumnType.STRING);
    private static final SQLColumn COLUMN_REASON      = SQLColumn.of("reason", ColumnType.STRING);
    private static final SQLColumn COLUMN_ADMIN       = SQLColumn.of("admin", ColumnType.STRING);
    private static final SQLColumn COLUMN_CREATE_DATE = SQLColumn.of("createDate", ColumnType.LONG);
    private static final SQLColumn COLUMN_EXPIRE_DATE = SQLColumn.of("expireDate", ColumnType.LONG);

    private static final SQLColumn COLUMN_ADDRESS     = SQLColumn.of("address", ColumnType.STRING);
    private static final SQLColumn COLUMN_PLAYER_ID   = SQLColumn.of("playerId", ColumnType.STRING);
    private static final SQLColumn COLUMN_PLAYER_NAME = SQLColumn.of("playerName", ColumnType.STRING);

    private final BansModule module;

    private final String tableBannedPlayers;
    private final String tableBannedIPs;
    private final String tableMutedPlayers;
    private final String tableWarnedPlayers;

    private static final Function<ResultSet, LegacyPunishment> PUNISHMENT_FUNCTION_OLD = resultSet -> {
        try {
            PunishmentType type = StringUtil.getEnum(resultSet.getString("type"), PunishmentType.class).orElse(null);
            if (type == null) return null;

            UUID id = UUID.fromString(resultSet.getString("pid"));
            UUID userId = null;
            try {
                userId = UUID.fromString(resultSet.getString("userId"));
            }
            catch (IllegalArgumentException ignored) {}

            String user = resultSet.getString("user");
            String reason = resultSet.getString("reason");
            String admin = resultSet.getString("admin");
            long created = resultSet.getLong("created");
            long expired = resultSet.getLong("expired");

            return new LegacyPunishment(id, userId, type, user, reason, admin, created, expired);
        }
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    };

    private static final Function<ResultSet, PunishedIP> BANNED_IP_FUNCTION = resultSet -> {
        try {
            UUID id = UUID.fromString(resultSet.getString(COLUMN_BAN_ID.getName()));
            String address = resultSet.getString(COLUMN_ADDRESS.getName());
            String reason = resultSet.getString(COLUMN_REASON.getName());
            String admin = resultSet.getString(COLUMN_ADMIN.getName());
            long created = resultSet.getLong(COLUMN_CREATE_DATE.getName());
            long expired = resultSet.getLong(COLUMN_EXPIRE_DATE.getName());

            return new PunishedIP(id, address, reason, admin, created, expired);
        }
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    };

    private static final Function<ResultSet, PunishedPlayer> BANNED_PLAYER_FUNCTION = resultSet -> {
        try {
            UUID id = UUID.fromString(resultSet.getString(COLUMN_BAN_ID.getName()));
            UUID userId = UUID.fromString(resultSet.getString(COLUMN_PLAYER_ID.getName()));
            String user = resultSet.getString(COLUMN_PLAYER_NAME.getName());
            String reason = resultSet.getString(COLUMN_REASON.getName());
            String admin = resultSet.getString(COLUMN_ADMIN.getName());
            long created = resultSet.getLong(COLUMN_CREATE_DATE.getName());
            long expired = resultSet.getLong(COLUMN_EXPIRE_DATE.getName());

            return new PunishedPlayer(id, userId, user, reason, admin, created, expired);
        }
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    };

    public BansDataHandler(@NotNull SunLightPlugin plugin, @NotNull BansModule module, @NotNull DatabaseConfig config) {
        super(plugin, config);
        this.module = module;

        this.tableBannedPlayers = this.getTablePrefix() + "_banned_players";
        this.tableBannedIPs = this.getTablePrefix() + "_banned_ips";
        this.tableMutedPlayers = this.getTablePrefix() + "_muted_players";
        this.tableWarnedPlayers = this.getTablePrefix() + "_warned_players";
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        /*List<SQLColumn> columnsOld = Arrays.asList(
            COLUMN_PID_OLD, COLUMN_UID_OLD, COLUMN_TYPE_OLD, COLUMN_USER_OLD, COLUMN_REASON_OLD, COLUMN_ADMIN_OLD, COLUMN_CREATED_OLD, COLUMN_EXPIRED_OLD
        );

        this.createTable(this.tableBans_old, columnsOld);
        this.createTable(this.tableMutes_old, columnsOld);
        this.createTable(this.tableWarns_old, columnsOld);

        this.addColumn(this.tableBans_old, COLUMN_UID_OLD.toValue("null"));
        this.addColumn(this.tableMutes_old, COLUMN_UID_OLD.toValue("null"));
        this.addColumn(this.tableWarns_old, COLUMN_UID_OLD.toValue("null"));*/

        List<SQLColumn> columnsPlayers = Arrays.asList(
            COLUMN_BAN_ID, COLUMN_PLAYER_ID, COLUMN_PLAYER_NAME, COLUMN_REASON, COLUMN_ADMIN, COLUMN_CREATE_DATE, COLUMN_EXPIRE_DATE
        );

        List<SQLColumn> columnsIPs = Arrays.asList(
            COLUMN_BAN_ID, COLUMN_ADDRESS, COLUMN_REASON, COLUMN_ADMIN, COLUMN_CREATE_DATE, COLUMN_EXPIRE_DATE
        );

        this.createTable(this.tableBannedIPs, columnsIPs);
        this.createTable(this.tableBannedPlayers, columnsPlayers);
        this.createTable(this.tableMutedPlayers, columnsPlayers);
        this.createTable(this.tableWarnedPlayers, columnsPlayers);

        this.transferOldData();
    }

    private void transferOldData() {
        for (PunishmentType type : PunishmentType.values()) {
            String oldTable = this.getOldTable(type);
            if (!SQLQueries.hasTable(this.getConnector(), oldTable)) continue;

            List<LegacyPunishment> punishments = this.load(oldTable, PUNISHMENT_FUNCTION_OLD, Collections.emptyList(), Collections.emptyList(), -1);
            punishments.forEach(punishment -> {
                PunishData punishData = this.toNewData(punishment);
                if (punishData == null || (punishData instanceof PunishedIP && type != PunishmentType.BAN)) return;

                this.addData(punishData, type);
            });

            this.delete(oldTable);
        }
    }

    @Nullable
    private PunishData toNewData(@NotNull LegacyPunishment punishment) {
        UUID id = punishment.getId();
        String reason = punishment.getReason();
        String admin = punishment.getAdmin();
        long createDate = punishment.getCreatedDate();
        long expireDate = punishment.getExpireDate();

        if (punishment.isIp()) {
            return new PunishedIP(id, punishment.getUser(), reason, admin, createDate, expireDate);
        }

        UUID playerId = punishment.getUserId();
        if (playerId == null) {
            SunUser user = plugin.getUserManager().getOrFetch(punishment.getUser());
            if (user == null) {
                return null;
            }
            playerId = user.getId();
        }

        return new PunishedPlayer(id, playerId, punishment.getUser(), reason, admin, createDate, expireDate);
    }

    @Override
    public void onSynchronize() {
        this.module.getPunishedIPMap().clear();
        this.module.getPunishedPlayerMap().clear();

        for (PunishmentType type : PunishmentType.values()) {
            List<PunishData> list = this.getPunishmentDatas(type);

            list.forEach(bannedData -> {
                this.module.cachePunishmentData(bannedData, type);
            });
        }
    }

    @Override
    public void onSave() {

    }

    @Override
    public void onPurge() {
        List<String> tables = new ArrayList<>();
        tables.add(this.tableBannedIPs);

        for (PunishmentType type : PunishmentType.values()) {
            tables.add(this.getPlayerDataTable(type));
        }

        for (String table : tables) {
            if (!SQLQueries.hasTable(this.getConnector(), table)) continue;

            LocalDateTime deadline = TimeUtil.getCurrentDateTime().minusDays(this.getConfig().getPurgePeriod());
            long deadlineMs = TimeUtil.toEpochMillis(deadline);
            this.delete(table,
                SQLCondition.smaller(COLUMN_EXPIRE_DATE.toValue(deadlineMs)),
                SQLCondition.greater(COLUMN_EXPIRE_DATE.toValue(0))
            );
        }
    }

    @NotNull
    public List<PunishData> getPunishmentDatas(@NotNull PunishmentType type) {
        List<PunishData> list = new ArrayList<>();

        if (type == PunishmentType.BAN) {
            list.addAll(this.load(this.tableBannedIPs, BANNED_IP_FUNCTION, Collections.emptyList(), Collections.emptyList(), -1));
        }

        list.addAll(this.load(this.getPlayerDataTable(type), BANNED_PLAYER_FUNCTION, Collections.emptyList(), Collections.emptyList(), -1));

        return list;
    }

    @NotNull
    public Map<PunishmentType, Set<PunishedPlayer>> getPlayerPunishments(@NotNull UUID playerId) {
        SQLCondition condition = SQLCondition.equal(COLUMN_PLAYER_ID.toValue(playerId));

        return this.getPlayerPunishments(condition);
    }

    @NotNull
    public Map<PunishmentType, Set<PunishedPlayer>> getPlayerPunishments(@NotNull String playerName) {
        SQLCondition condition = SQLCondition.equal(COLUMN_PLAYER_NAME.asLowerCase().toValue(playerName));

        return this.getPlayerPunishments(condition);
    }

    @NotNull
    private Map<PunishmentType, Set<PunishedPlayer>> getPlayerPunishments(@NotNull SQLCondition condition) {
        Map<PunishmentType, Set<PunishedPlayer>> map = new HashMap<>();
        for (PunishmentType type : PunishmentType.values()) {
            map.put(type, new HashSet<>(this.load(this.getPlayerDataTable(type), BANNED_PLAYER_FUNCTION, Collections.emptyList(), Collections.singletonList(condition), -1)));
        }
        return map;
    }

    @NotNull
    private String getOldTable(@NotNull PunishmentType punishmentType) {
        return switch (punishmentType) {
            case BAN -> this.getTablePrefix() + "_bans";
            case MUTE -> this.getTablePrefix() + "_mutes";
            case WARN -> this.getTablePrefix() + "_warns";
        };
    }

    @NotNull
    private String getDataTable(@NotNull PunishData punishData, @NotNull PunishmentType type) {
        return switch (type) {
            case BAN -> punishData instanceof PunishedIP ? this.tableBannedIPs : this.tableBannedPlayers;
            case MUTE -> this.tableMutedPlayers;
            case WARN -> this.tableWarnedPlayers;
        };
    }

    @NotNull
    private String getPlayerDataTable(@NotNull PunishmentType type) {
        return switch (type) {
            case BAN -> this.tableBannedPlayers;
            case MUTE -> this.tableMutedPlayers;
            case WARN -> this.tableWarnedPlayers;
        };
    }

    public void addData(@NotNull PunishData punishData, @NotNull PunishmentType type) {
        this.addData(punishData, this.getDataTable(punishData, type));
    }

    public void addData(@NotNull PunishData punishData, @NotNull String table) {
        List<SQLValue> values = Lists.newList(
            COLUMN_BAN_ID.toValue(punishData.getId()),
            COLUMN_REASON.toValue(punishData.getReason()),
            COLUMN_ADMIN.toValue(punishData.getAdmin()),
            COLUMN_CREATE_DATE.toValue(punishData.getCreateDate()),
            COLUMN_EXPIRE_DATE.toValue(punishData.getExpireDate())
        );

        if (punishData instanceof PunishedIP punishedIP) {
            values.add(COLUMN_ADDRESS.toValue(punishedIP.getAddress()));
        }
        else if (punishData instanceof PunishedPlayer punishedPlayer) {
            values.add(COLUMN_PLAYER_ID.toValue(punishedPlayer.getPlayerId()));
            values.add(COLUMN_PLAYER_NAME.toValue(punishedPlayer.getPlayerName()));
        }

        this.insert(table, values);
    }

    public void saveData(@NotNull PunishData punishData, @NotNull PunishmentType type) {
        List<SQLValue> values = Lists.newList(
            COLUMN_REASON.toValue(punishData.getReason()),
            COLUMN_ADMIN.toValue(punishData.getAdmin()),
            COLUMN_CREATE_DATE.toValue(punishData.getCreateDate()),
            COLUMN_EXPIRE_DATE.toValue(punishData.getExpireDate())
        );

        if (punishData instanceof PunishedIP punishedIP) {
            values.add(COLUMN_ADDRESS.toValue(punishedIP.getAddress()));
        }
        else if (punishData instanceof PunishedPlayer punishedPlayer) {
            values.add(COLUMN_PLAYER_ID.toValue(punishedPlayer.getPlayerId()));
            values.add(COLUMN_PLAYER_NAME.toValue(punishedPlayer.getPlayerName()));
        }

        SQLCondition condition = SQLCondition.equal(COLUMN_BAN_ID.toValue(punishData.getId()));

        this.update(this.getDataTable(punishData, type), values, condition);
    }

    public void deleteData(@NotNull PunishData punishData, @NotNull PunishmentType type) {
        String table = this.getDataTable(punishData, type);
        SQLCondition condition = SQLCondition.equal(COLUMN_BAN_ID.toValue(punishData.getId()));
        this.delete(table, condition);
    }
}
