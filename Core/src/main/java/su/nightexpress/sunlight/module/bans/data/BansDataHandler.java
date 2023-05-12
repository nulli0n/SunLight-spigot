package su.nightexpress.sunlight.module.bans.data;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.data.AbstractDataHandler;
import su.nexmedia.engine.api.data.config.DataConfig;
import su.nexmedia.engine.api.data.sql.SQLColumn;
import su.nexmedia.engine.api.data.sql.SQLCondition;
import su.nexmedia.engine.api.data.sql.SQLQueries;
import su.nexmedia.engine.api.data.sql.SQLValue;
import su.nexmedia.engine.api.data.sql.column.ColumnType;
import su.nexmedia.engine.utils.StringUtil;
import su.nexmedia.engine.utils.TimeUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.punishment.Punishment;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

public class BansDataHandler extends AbstractDataHandler<SunLight> {

    private static final SQLColumn COLUMN_PID  = SQLColumn.of("pid", ColumnType.STRING);
    private static final SQLColumn COLUMN_UID  = SQLColumn.of("userId", ColumnType.STRING);
    private static final SQLColumn COLUMN_TYPE = SQLColumn.of("type", ColumnType.STRING);
    private static final SQLColumn COLUMN_USER    = SQLColumn.of("user", ColumnType.STRING);
    private static final SQLColumn COLUMN_REASON  = SQLColumn.of("reason", ColumnType.STRING);
    private static final SQLColumn COLUMN_ADMIN   = SQLColumn.of("admin", ColumnType.STRING);
    private static final SQLColumn COLUMN_CREATED = SQLColumn.of("created", ColumnType.LONG);
    private static final SQLColumn COLUMN_EXPIRED = SQLColumn.of("expired", ColumnType.LONG);

    private final BansModule bansModule;
    private final String     tableBans;
    private final String tableMutes;
    private final String tableWarns;

    private static final Function<ResultSet, Punishment> PUNISHMENT_FUNCTION = resultSet -> {
        try {
            PunishmentType type = StringUtil.getEnum(resultSet.getString(COLUMN_TYPE.getName()), PunishmentType.class).orElse(null);
            if (type == null) return null;

            UUID id = UUID.fromString(resultSet.getString(COLUMN_PID.getName()));
            UUID userId = null;
            try {
                userId = UUID.fromString(resultSet.getString(COLUMN_UID.getName()));
            }
            catch (IllegalArgumentException ignored) {}

            String user = resultSet.getString(COLUMN_USER.getName());
            String reason = resultSet.getString(COLUMN_REASON.getName());
            String admin = resultSet.getString(COLUMN_ADMIN.getName());
            long created = resultSet.getLong(COLUMN_CREATED.getName());
            long expired = resultSet.getLong(COLUMN_EXPIRED.getName());

            return new Punishment(id, userId, type, user, reason, admin, created, expired);
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    };

    public BansDataHandler(@NotNull BansModule bansModule, @NotNull DataConfig config) {
        super(bansModule.plugin(), config);
        this.bansModule = bansModule;

        this.tableBans = this.getTablePrefix() + "bans";
        this.tableMutes = this.getTablePrefix() + "mutes";
        this.tableWarns = this.getTablePrefix() + "warns";
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        List<SQLColumn> columns = Arrays.asList(
            COLUMN_PID, COLUMN_UID, COLUMN_TYPE, COLUMN_USER, COLUMN_REASON, COLUMN_ADMIN, COLUMN_CREATED, COLUMN_EXPIRED
        );

        this.createTable(this.tableBans, columns);
        this.createTable(this.tableMutes, columns);
        this.createTable(this.tableWarns, columns);

        this.addColumn(this.tableBans, COLUMN_UID.toValue("null"));
        this.addColumn(this.tableMutes, COLUMN_UID.toValue("null"));
        this.addColumn(this.tableWarns, COLUMN_UID.toValue("null"));
    }

    @Override
    protected void onShutdown() {
        super.onShutdown();
    }

    @Override
    public void onSynchronize() {
        this.bansModule.getPunishmentMap().clear();
        for (PunishmentType type : PunishmentType.values()) {
            this.getPunishments(type).forEach(punishment -> {
                this.bansModule.getPunishments(punishment.getUser(), type).add(punishment);
            });
        }
    }

    @Override
    public void onSave() {

    }

    @Override
    public void onPurge() {
        for (PunishmentType type : PunishmentType.values()) {
            if (!SQLQueries.hasTable(this.getConnector(), this.getTable(type))) continue;

            LocalDateTime deadline = LocalDateTime.now().minusDays(this.getConfig().purgePeriod);
            long deadlineMs = TimeUtil.toEpochMillis(deadline);
            this.delete(this.getTable(type), SQLCondition.smaller(COLUMN_EXPIRED.toValue(deadlineMs)));
        }
    }

    @NotNull
    public Set<Punishment> getPunishments(@NotNull String user, @NotNull PunishmentType type) {
        return this.getPunishments(user).computeIfAbsent(type, k -> new HashSet<>());
    }

    @NotNull
    public Map<PunishmentType, Set<Punishment>> getPunishments(@NotNull String user) {
        SQLCondition condition = SQLCondition.equal(COLUMN_USER.asLowerCase().toValue(user));

        Map<PunishmentType, Set<Punishment>> map = new HashMap<>();
        for (PunishmentType type : PunishmentType.values()) {
            map.put(type, new HashSet<>(this.load(this.getTable(type), PUNISHMENT_FUNCTION, Collections.emptyList(), Collections.singletonList(condition), -1)));
        }
        return map;
    }

    @NotNull
    public List<Punishment> getPunishments(@NotNull PunishmentType type) {
        return this.load(this.getTable(type), PUNISHMENT_FUNCTION, Collections.emptyList(), Collections.emptyList(), -1);
    }

    @NotNull
    private String getTable(@NotNull PunishmentType punishmentType) {
        return switch (punishmentType) {
            case BAN -> tableBans;
            case MUTE -> tableMutes;
            case WARN -> tableWarns;
        };
    }

    public void savePunishment(@NotNull Punishment punishment) {
        List<SQLValue> values = Arrays.asList(
            COLUMN_TYPE.toValue(punishment.getType().name()),
            COLUMN_USER.toValue(punishment.getUser()),
            COLUMN_UID.toValue(String.valueOf(punishment.getUserId())),
            COLUMN_REASON.toValue(punishment.getReason()),
            COLUMN_ADMIN.toValue(punishment.getAdmin()),
            COLUMN_CREATED.toValue(punishment.getCreatedDate()),
            COLUMN_EXPIRED.toValue(punishment.getExpireDate())
        );

        SQLCondition condition = SQLCondition.equal(COLUMN_PID.toValue(punishment.getId()));
        this.update(this.getTable(punishment.getType()), values, condition);
    }

    public void addPunishment(@NotNull Punishment punishment) {
        List<SQLValue> values = Arrays.asList(
            COLUMN_PID.toValue(punishment.getId()),
            COLUMN_UID.toValue(String.valueOf(punishment.getUserId())),
            COLUMN_TYPE.toValue(punishment.getType().name()),
            COLUMN_USER.toValue(punishment.getUser()),
            COLUMN_REASON.toValue(punishment.getReason()),
            COLUMN_ADMIN.toValue(punishment.getAdmin()),
            COLUMN_CREATED.toValue(punishment.getCreatedDate()),
            COLUMN_EXPIRED.toValue(punishment.getExpireDate())
        );
        this.insert(this.getTable(punishment.getType()), values);
    }

    public void deletePunishment(@NotNull Punishment punishment) {
        String table = this.getTable(punishment.getType());
        SQLCondition condition = SQLCondition.equal(COLUMN_PID.toValue(punishment.getId()));
        this.delete(table, condition);
    }
}
