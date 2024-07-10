package su.nightexpress.sunlight.data;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.database.AbstractUserDataHandler;
import su.nightexpress.nightcore.database.sql.SQLColumn;
import su.nightexpress.nightcore.database.sql.SQLCondition;
import su.nightexpress.nightcore.database.sql.SQLValue;
import su.nightexpress.nightcore.database.sql.column.ColumnType;
import su.nightexpress.nightcore.database.sql.executor.SelectQueryExecutor;
import su.nightexpress.nightcore.database.sql.query.UpdateEntity;
import su.nightexpress.nightcore.database.sql.query.UpdateQuery;
import su.nightexpress.nightcore.util.ItemNbt;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.LocationUtil;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.core.cooldown.CooldownInfo;
import su.nightexpress.sunlight.core.cooldown.CooldownType;
import su.nightexpress.sunlight.core.user.IgnoredUser;
import su.nightexpress.sunlight.data.serialize.CooldownSerializer;
import su.nightexpress.sunlight.data.serialize.IgnoredUserSerializer;
import su.nightexpress.sunlight.data.serialize.UserInfoSerializer;
import su.nightexpress.sunlight.data.serialize.UserSettingsSerializer;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.data.user.UserSettings;
import su.nightexpress.sunlight.module.homes.impl.Home;
import su.nightexpress.sunlight.module.homes.impl.HomeType;
import su.nightexpress.sunlight.utils.UserInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

public class DataHandler extends AbstractUserDataHandler<SunLightPlugin, SunUser> {

    private static final SQLColumn USER_IP          = SQLColumn.of("ip", ColumnType.STRING);
    private static final SQLColumn USER_COOLDOWNS   = SQLColumn.of("cooldowns", ColumnType.STRING);
    private static final SQLColumn USER_IGNORE_LIST = SQLColumn.of("ignoredUsers", ColumnType.STRING);
    private static final SQLColumn USER_SETTINGS    = SQLColumn.of("settings", ColumnType.STRING);

    private static final SQLColumn HOME_ID               = SQLColumn.of("homeId", ColumnType.STRING);
    private static final SQLColumn HOME_OWNER_ID         = SQLColumn.of("ownerId", ColumnType.STRING);
    private static final SQLColumn HOME_OWNER_NAME       = SQLColumn.of("ownerName", ColumnType.STRING);
    private static final SQLColumn HOME_NAME             = SQLColumn.of("name", ColumnType.STRING);
    private static final SQLColumn HOME_ICON             = SQLColumn.of("icon", ColumnType.STRING);
    private static final SQLColumn HOME_LOCATION         = SQLColumn.of("location", ColumnType.STRING);
    private static final SQLColumn HOME_TYPE             = SQLColumn.of("type", ColumnType.STRING);
    private static final SQLColumn HOME_INVITED_PLAYERS  = SQLColumn.of("invitedPlayers", ColumnType.STRING);
    private static final SQLColumn HOME_IS_DEFAULT       = SQLColumn.of("isDefault", ColumnType.BOOLEAN);
    private static final SQLColumn HOME_IS_RESPAWN_POINT = SQLColumn.of("isRespawnPoint", ColumnType.BOOLEAN);

    private final String tableHomes;

    private final Function<ResultSet, SunUser> userFunction;
    private final Function<ResultSet, Home>    functionHome;

    public DataHandler(@NotNull SunLightPlugin plugin) {
        super(plugin);
        this.tableHomes = this.getTablePrefix() + "_homes";

        this.userFunction = (resultSet) -> {
            try {
                UUID uuid = UUID.fromString(resultSet.getString(COLUMN_USER_ID.getName()));
                String name = resultSet.getString(COLUMN_USER_NAME.getName());
                long dateCreated = resultSet.getLong(COLUMN_USER_DATE_CREATED.getName());
                long lastOnline = resultSet.getLong(COLUMN_USER_LAST_ONLINE.getName());

                String ip = resultSet.getString(USER_IP.getName());
                //String customName = resultSet.getString(USER_CUSTOM_NAME.getName());

                Map<CooldownType, Set<CooldownInfo>> cooldowns = this.gson.fromJson(resultSet.getString(USER_COOLDOWNS.getName()), new TypeToken<Map<CooldownType, Set<CooldownInfo>>>(){}.getType());
                if (cooldowns == null) cooldowns = new HashMap<>();

                Map<UUID, IgnoredUser> ignoredUsers = gson.fromJson(resultSet.getString(USER_IGNORE_LIST.getName()), new TypeToken<Map<UUID, IgnoredUser>>() {}.getType());
                if (ignoredUsers == null) ignoredUsers = new HashMap<>();
                else ignoredUsers.values().removeIf(Objects::isNull);

                UserSettings settings = gson.fromJson(resultSet.getString(USER_SETTINGS.getName()), new TypeToken<UserSettings>(){}.getType());
                if (settings == null) settings = new UserSettings();

                return new SunUser(plugin, uuid, name, dateCreated, lastOnline, ip, cooldowns, ignoredUsers, settings);
            }
            catch (SQLException ex) {
                ex.printStackTrace();
                return null;
            }
        };

        this.functionHome = resultSet -> {
            try {
                String id = resultSet.getString(HOME_ID.getName());
                UUID ownerId = UUID.fromString(resultSet.getString(HOME_OWNER_ID.getName()));
                String ownerName = resultSet.getString(HOME_OWNER_NAME.getName());
                String name = resultSet.getString(HOME_NAME.getName());
                ItemStack icon = ItemNbt.decompress(resultSet.getString(HOME_ICON.getName()));
                if (icon == null) icon = new ItemStack(Material.GRASS_BLOCK);

                Location location = LocationUtil.deserialize(resultSet.getString(HOME_LOCATION.getName()));
                if (location == null) return null;

                HomeType type = StringUtil.getEnum(resultSet.getString(HOME_TYPE.getName()), HomeType.class).orElse(HomeType.PRIVATE);
                Set<UserInfo> invitedPlayers = this.gson.fromJson(resultSet.getString(HOME_INVITED_PLAYERS.getName()), new TypeToken<Set<UserInfo>>(){}.getType());
                boolean isDefault = resultSet.getBoolean(HOME_IS_DEFAULT.getName());
                boolean isRespawnPoint = resultSet.getBoolean(HOME_IS_RESPAWN_POINT.getName());

                UserInfo owner = new UserInfo(ownerId, ownerName);
                return new Home(plugin, id, owner, name, icon, location, type, invitedPlayers, isDefault, isRespawnPoint);
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        };
    }

    @Override
    public void onSynchronize() {
        this.plugin.getUserManager().getLoaded().forEach(user -> {
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
        return super.registerAdapters(builder)
            .registerTypeAdapter(UserInfo.class, new UserInfoSerializer())
            .registerTypeAdapter(UserSettings.class, new UserSettingsSerializer())
            .registerTypeAdapter(IgnoredUser.class, new IgnoredUserSerializer())
            .registerTypeAdapter(CooldownInfo.class, new CooldownSerializer())
            ;
    }

    @Override
    protected void createUserTable() {
        super.createUserTable();

        this.addColumn(this.tableUsers, USER_SETTINGS.toValue("{}"), USER_COOLDOWNS.toValue("{}"));
        this.dropColumn(this.tableUsers,
            SQLColumn.of("nickname", ColumnType.STRING),
            SQLColumn.of("kitCooldowns", ColumnType.STRING),
            SQLColumn.of("commandCooldowns", ColumnType.STRING),
            SQLColumn.of("settingsBool", ColumnType.STRING),
            SQLColumn.of("settingsNum", ColumnType.STRING)
        );

        this.createTable(this.tableHomes, Arrays.asList(
            HOME_ID, HOME_OWNER_ID, HOME_OWNER_NAME, HOME_NAME,
            HOME_ICON, HOME_LOCATION, HOME_TYPE, HOME_INVITED_PLAYERS,
            HOME_IS_DEFAULT, HOME_IS_RESPAWN_POINT
        ));
    }

    @Override
    @NotNull
    protected List<SQLColumn> getExtraColumns() {
        return Arrays.asList(USER_IP, USER_IGNORE_LIST, USER_COOLDOWNS, USER_SETTINGS);
    }

    @Override
    @NotNull
    protected List<SQLValue> getSaveColumns(@NotNull SunUser user) {
        return Arrays.asList(
            USER_IP.toValue(user.getInetAddress()),
            USER_IGNORE_LIST.toValue(this.gson.toJson(user.getIgnoredUsers())),
            USER_COOLDOWNS.toValue(this.gson.toJson(user.getCooldowns())),
            USER_SETTINGS.toValue(this.gson.toJson(user.getSettings()))
        );
    }

    @Override
    @NotNull
    protected Function<ResultSet, SunUser> getUserFunction() {
        return this.userFunction;
    }

    @NotNull
    public List<Home> getHomes() {
        return SelectQueryExecutor.builder(this.tableHomes, this.functionHome).execute(this.getConnector());
    }

    @NotNull
    public List<Home> getHomes(@NotNull UUID id) {
        return SelectQueryExecutor.builder(this.tableHomes, this.functionHome)
            .where(SQLCondition.of(HOME_OWNER_ID.toValue(id.toString()), SQLCondition.Type.EQUAL))
            .execute(this.getConnector());
    }

    @Nullable
    public Home getHome(@NotNull UUID userId, @NotNull String homeId) {
        return SelectQueryExecutor.builder(this.tableHomes, this.functionHome)
            .where(
                SQLCondition.of(HOME_OWNER_ID.toValue(userId.toString()), SQLCondition.Type.EQUAL),
                SQLCondition.of(HOME_ID.toValue(homeId), SQLCondition.Type.EQUAL))
            .execute(this.getConnector()).stream().findFirst().orElse(null);
    }

    @Nullable
    public Home getHome(@NotNull String userName, @NotNull String homeId) {
        return SelectQueryExecutor.builder(this.tableHomes, this.functionHome)
            .where(
                SQLCondition.of(HOME_OWNER_NAME.toValue(userName), SQLCondition.Type.EQUAL),
                SQLCondition.of(HOME_ID.toValue(homeId), SQLCondition.Type.EQUAL))
            .execute(this.getConnector()).stream().findFirst().orElse(null);
    }

    public void addHome(@NotNull Home home) {
        this.insert(this.tableHomes, Arrays.asList(
            HOME_ID.toValue(home.getId()),
            HOME_OWNER_ID.toValue(home.getOwner().getId().toString()),
            HOME_OWNER_NAME.toValue(home.getOwner().getName()),
            HOME_NAME.toValue(home.getName()),
            HOME_ICON.toValue(String.valueOf(ItemNbt.compress(home.getIcon()))),
            HOME_LOCATION.toValue(String.valueOf(LocationUtil.serialize(home.getLocation()))),
            HOME_TYPE.toValue(home.getType().name()),
            HOME_INVITED_PLAYERS.toValue(this.gson.toJson(home.getInvitedPlayers())),
            HOME_IS_DEFAULT.toValue(String.valueOf(home.isDefault() ? 1 : 0)),
            HOME_IS_RESPAWN_POINT.toValue(String.valueOf(home.isRespawnPoint() ? 1 : 0))
        ));
    }

//    @Deprecated
//    public void saveHome(@NotNull Home home) {
//        this.update(this.tableHomes,
//            Arrays.asList(
//                HOME_OWNER_ID.toValue(home.getOwner().getId().toString()),
//                HOME_OWNER_NAME.toValue(home.getOwner().getName()),
//                HOME_NAME.toValue(home.getName()),
//                HOME_ICON.toValue(String.valueOf(ItemNbt.compress(home.getIcon()))),
//                HOME_LOCATION.toValue(String.valueOf(LocationUtil.serialize(home.getLocation()))),
//                HOME_TYPE.toValue(home.getType().name()),
//                HOME_INVITED_PLAYERS.toValue(this.gson.toJson(home.getInvitedPlayers())),
//                HOME_IS_DEFAULT.toValue(String.valueOf(home.isDefault() ? 1 : 0)),
//                HOME_IS_RESPAWN_POINT.toValue(String.valueOf(home.isRespawnPoint() ? 1 : 0))
//            ),
//            SQLCondition.of(HOME_ID.toValue(home.getId()), SQLCondition.Type.EQUAL),
//            SQLCondition.of(HOME_OWNER_ID.toValue(home.getOwner().getId().toString()), SQLCondition.Type.EQUAL)
//        );
//    }

    @NotNull
    public UpdateEntity createHomeUpdateEntity(@NotNull Home home) {
        return createUpdateEntity(
            Lists.newList(
                HOME_OWNER_ID.toValue(home.getOwner().getId().toString()),
                HOME_OWNER_NAME.toValue(home.getOwner().getName()),
                HOME_NAME.toValue(home.getName()),
                HOME_ICON.toValue(String.valueOf(ItemNbt.compress(home.getIcon()))),
                HOME_LOCATION.toValue(String.valueOf(LocationUtil.serialize(home.getLocation()))),
                HOME_TYPE.toValue(home.getType().name()),
                HOME_INVITED_PLAYERS.toValue(this.gson.toJson(home.getInvitedPlayers())),
                HOME_IS_DEFAULT.toValue(String.valueOf(home.isDefault() ? 1 : 0)),
                HOME_IS_RESPAWN_POINT.toValue(String.valueOf(home.isRespawnPoint() ? 1 : 0))
            ),
            Lists.newList(
                SQLCondition.of(HOME_ID.toValue(home.getId()), SQLCondition.Type.EQUAL),
                SQLCondition.of(HOME_OWNER_ID.toValue(home.getOwner().getId().toString()), SQLCondition.Type.EQUAL)
            )
        );
    }

    public void saveHomes(@NotNull Collection<Home> homes) {
        this.executeUpdate(UpdateQuery.create(this.tableHomes, homes.stream().map(this::createHomeUpdateEntity).toList()));
    }

    public void deleteHome(@NotNull Home home) {
        this.deleteHome(home.getOwner().getId(), home.getId());
    }

    public void deleteHomes(@NotNull UUID userId) {
        this.delete(this.tableHomes, SQLCondition.of(HOME_OWNER_ID.toValue(userId.toString()), SQLCondition.Type.EQUAL));
    }

    public void deleteHomes(@NotNull String userName) {
        this.delete(this.tableHomes, SQLCondition.of(HOME_OWNER_NAME.toValue(userName), SQLCondition.Type.EQUAL));
    }

    public void deleteHome(@NotNull UUID userId, @NotNull String homeId) {
        this.delete(this.tableHomes,
            SQLCondition.of(HOME_OWNER_ID.toValue(userId.toString()), SQLCondition.Type.EQUAL),
            SQLCondition.of(HOME_ID.toValue(homeId), SQLCondition.Type.EQUAL)
        );
    }
}
