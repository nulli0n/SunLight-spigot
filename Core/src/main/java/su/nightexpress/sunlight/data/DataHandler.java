package su.nightexpress.sunlight.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.db.AbstractUserDataManager;
import su.nightexpress.nightcore.db.sql.column.Column;
import su.nightexpress.nightcore.db.sql.column.ColumnType;
import su.nightexpress.nightcore.db.sql.query.impl.DeleteQuery;
import su.nightexpress.nightcore.db.sql.query.impl.SelectQuery;
import su.nightexpress.nightcore.db.sql.query.type.ValuedQuery;
import su.nightexpress.nightcore.db.sql.util.WhereOperator;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.core.cooldown.CooldownInfo;
import su.nightexpress.sunlight.core.user.IgnoredUser;
import su.nightexpress.sunlight.data.serialize.CooldownSerializer;
import su.nightexpress.sunlight.data.serialize.IgnoredUserSerializer;
import su.nightexpress.sunlight.data.serialize.UserInfoSerializer;
import su.nightexpress.sunlight.data.serialize.UserSettingsSerializer;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.data.user.UserSettings;
import su.nightexpress.sunlight.module.homes.impl.Home;
import su.nightexpress.sunlight.utils.UserInfo;

import java.sql.ResultSet;
import java.util.*;
import java.util.function.Function;

public class DataHandler extends AbstractUserDataManager<SunLightPlugin, SunUser> {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting()
        .registerTypeAdapter(UserInfo.class, new UserInfoSerializer())
        .registerTypeAdapter(UserSettings.class, new UserSettingsSerializer())
        .registerTypeAdapter(IgnoredUser.class, new IgnoredUserSerializer())
        .registerTypeAdapter(CooldownInfo.class, new CooldownSerializer())
        .create();

    static final Column    COLUMN_USER_ADDRESS = Column.of("ip", ColumnType.STRING);
    static final Column USER_COOLDOWNS      = Column.of("cooldowns", ColumnType.STRING);
    static final Column USER_IGNORE_LIST    = Column.of("ignoredUsers", ColumnType.STRING);
    static final Column USER_SETTINGS       = Column.of("settings", ColumnType.STRING);

    static final Column HOME_ID               = Column.of("homeId", ColumnType.STRING);
    static final Column HOME_OWNER_ID         = Column.of("ownerId", ColumnType.STRING);
    static final Column HOME_OWNER_NAME       = Column.of("ownerName", ColumnType.STRING);
    static final Column HOME_NAME             = Column.of("name", ColumnType.STRING);
    static final Column HOME_ICON             = Column.of("icon", ColumnType.STRING);
    static final Column HOME_LOCATION         = Column.of("location", ColumnType.STRING);
    static final Column HOME_TYPE             = Column.of("type", ColumnType.STRING);
    static final Column HOME_INVITED_PLAYERS  = Column.of("invitedPlayers", ColumnType.STRING);
    static final Column HOME_IS_DEFAULT       = Column.of("isDefault", ColumnType.BOOLEAN);
    static final Column HOME_IS_RESPAWN_POINT = Column.of("isRespawnPoint", ColumnType.BOOLEAN);

    private final String tableHomes;

    public DataHandler(@NotNull SunLightPlugin plugin) {
        super(plugin);
        this.tableHomes = this.getTablePrefix() + "_homes";
    }

    @Override
    public void onSynchronize() {
        this.plugin.getUserManager().getLoaded().forEach(user -> {
            if (user.isAutoSavePlanned() || !user.isAutoSyncReady()) return;

            SunUser sync = this.getUser(user.getId());
            if (sync == null) return;

            user.getSettings().getValues().clear();
            user.getSettings().getValues().putAll(sync.getSettings().getValues());
            user.getCooldowns().clear();
            user.getCooldowns().putAll(sync.getCooldowns());
            user.getIgnoredUsers().clear();
            user.getIgnoredUsers().putAll(sync.getIgnoredUsers());
        });
    }

    @Override
    @NotNull
    protected GsonBuilder registerAdapters(@NotNull GsonBuilder builder) {
        return builder;
    }

    @Override
    @NotNull
    protected Function<ResultSet, SunUser> createUserFunction() {
        return DataQueries.USER_LOAD;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.createTable(this.tableHomes, Arrays.asList(
            HOME_ID, HOME_OWNER_ID, HOME_OWNER_NAME, HOME_NAME,
            HOME_ICON, HOME_LOCATION, HOME_TYPE, HOME_INVITED_PLAYERS,
            HOME_IS_DEFAULT, HOME_IS_RESPAWN_POINT
        ));
    }

    @Override
    protected void addUpsertQueryData(@NotNull ValuedQuery<?, SunUser> query) {
        query.setValue(COLUMN_USER_ADDRESS, SunUser::getInetAddress);
        query.setValue(USER_IGNORE_LIST, user -> GSON.toJson(user.getIgnoredUsers()));
        query.setValue(USER_COOLDOWNS, user -> GSON.toJson(user.getCooldowns()));
        query.setValue(USER_SETTINGS, user -> GSON.toJson(user.getSettings()));
    }

    @Override
    protected void addSelectQueryData(@NotNull SelectQuery<SunUser> query) {
        query.column(COLUMN_USER_ADDRESS);
        query.column(USER_IGNORE_LIST);
        query.column(USER_COOLDOWNS);
        query.column(USER_SETTINGS);
    }

    @Override
    protected void addTableColumns(@NotNull List<Column> columns) {
        columns.add(COLUMN_USER_ADDRESS);
        columns.add(USER_IGNORE_LIST);
        columns.add(USER_COOLDOWNS);
        columns.add(USER_SETTINGS);
    }

    @NotNull
    public List<Home> getHomes() {
        return this.select(this.tableHomes, new SelectQuery<>(HomesQueries.HOME_LOADER).all());
    }

    @NotNull
    public List<Home> getHomes(@NotNull UUID id) {
        return this.select(this.tableHomes, new SelectQuery<>(HomesQueries.HOME_LOADER).all().where(HOME_OWNER_ID, WhereOperator.EQUAL, id.toString()));
    }

    @Nullable
    public Home getHome(@NotNull UUID userId, @NotNull String homeId) {
        return this.selectFirst(this.tableHomes, new SelectQuery<>(HomesQueries.HOME_LOADER).all()
            .where(HOME_OWNER_ID, WhereOperator.EQUAL, userId.toString())
            .where(HOME_ID, WhereOperator.EQUAL, homeId)
        );
    }

    @Nullable
    public Home getHome(@NotNull String userName, @NotNull String homeId) {
        return this.selectFirst(this.tableHomes, new SelectQuery<>(HomesQueries.HOME_LOADER).all()
            .where(HOME_OWNER_NAME, WhereOperator.EQUAL, userName)
            .where(HOME_ID, WhereOperator.EQUAL, homeId)
        );
    }

    public void addHome(@NotNull Home home) {
        this.insert(this.tableHomes, HomesQueries.HOME_INSERT, home);
    }

    public void saveHomes(@NotNull Collection<Home> homes) {
        this.update(this.tableHomes, HomesQueries.HOME_UPDATE, homes);
    }

    public void deleteHome(@NotNull Home home) {
        this.delete(this.tableHomes, HomesQueries.HOME_DELETE, home);
    }

    public void deleteHomes(@NotNull UUID userId) {
        this.delete(this.tableHomes, HomesQueries.HOME_DELETE_OWNER_ID, userId);
    }

    public void deleteHomes(@NotNull String userName) {
        this.delete(this.tableHomes, HomesQueries.HOME_DELETE_OWNER_NAME, userName);
    }

    public void deleteHome(@NotNull UUID ownerId, @NotNull String homeId) {
        this.delete(this.tableHomes, new DeleteQuery<>().where(HOME_OWNER_ID, WhereOperator.EQUAL, o -> ownerId.toString()).where(HOME_ID, WhereOperator.EQUAL, o -> homeId), new Object());
    }

    @NotNull
    public Map<String, Set<UserInfo>> getPlayerIPs() {
        Map<String, Set<UserInfo>> map = new HashMap<>();

        this.getUsers().forEach(user -> {
            map.computeIfAbsent(user.getInetAddress(), k -> new HashSet<>()).add(new UserInfo(user));
        });

        return map;
    }
}
