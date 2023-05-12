package su.nightexpress.sunlight.data;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.data.AbstractUserDataHandler;
import su.nexmedia.engine.api.data.sql.SQLColumn;
import su.nexmedia.engine.api.data.sql.SQLCondition;
import su.nexmedia.engine.api.data.sql.SQLValue;
import su.nexmedia.engine.api.data.sql.column.ColumnType;
import su.nexmedia.engine.api.data.sql.executor.SelectQueryExecutor;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.LocationUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.data.serialize.CooldownSerializer;
import su.nightexpress.sunlight.data.serialize.IgnoredUserSerializer;
import su.nightexpress.sunlight.data.serialize.LegacyHomeSerializer;
import su.nightexpress.sunlight.data.serialize.UserSettingsSerializer;
import su.nightexpress.sunlight.module.homes.impl.Home;
import su.nightexpress.sunlight.module.homes.impl.HomeType;
import su.nightexpress.sunlight.module.homes.impl.LegacyHome;
import su.nightexpress.sunlight.data.impl.IgnoredUser;
import su.nightexpress.sunlight.utils.UserInfo;
import su.nightexpress.sunlight.data.impl.cooldown.CooldownInfo;
import su.nightexpress.sunlight.data.impl.cooldown.CooldownType;
import su.nightexpress.sunlight.data.impl.settings.BasicSettings;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

public class DataHandler extends AbstractUserDataHandler<SunLight, SunUser> {

    private static final SQLColumn USER_IP          = SQLColumn.of("ip", ColumnType.STRING);
    private static final SQLColumn USER_CUSTOM_NAME = SQLColumn.of("nickname", ColumnType.STRING);
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

    private static DataHandler instance;

    private final String tableHomes;

    private final Function<ResultSet, SunUser> functionUser;
    private final Function<ResultSet, Home>    functionHome;

    protected DataHandler(@NotNull SunLight plugin) {
        super(plugin, plugin);
        this.tableHomes = this.getTablePrefix() + "_homes";

        this.functionUser = (resultSet) -> {
            try {
                UUID uuid = UUID.fromString(resultSet.getString(COLUMN_USER_ID.getName()));
                String name = resultSet.getString(COLUMN_USER_NAME.getName());
                long lastOnline = resultSet.getLong(COLUMN_USER_LAST_ONLINE.getName());
                long dateCreated = resultSet.getLong(COLUMN_USER_DATE_CREATED.getName());

                String ip = resultSet.getString(USER_IP.getName());
                String customName = resultSet.getString(USER_CUSTOM_NAME.getName());

                Map<CooldownType, Set<CooldownInfo>> cooldowns = this.gson.fromJson(resultSet.getString(USER_COOLDOWNS.getName()), new TypeToken<Map<CooldownType, Set<CooldownInfo>>>(){}.getType());
                if (cooldowns == null) cooldowns = new HashMap<>();

                Map<UUID, IgnoredUser> ignoredUsers = gson.fromJson(resultSet.getString(USER_IGNORE_LIST.getName()), new TypeToken<Map<UUID, IgnoredUser>>() {}.getType());
                if (ignoredUsers == null) ignoredUsers = new HashMap<>();
                else ignoredUsers.values().removeIf(Objects::isNull);

                BasicSettings settings = gson.fromJson(resultSet.getString(USER_SETTINGS.getName()), new TypeToken<BasicSettings>(){}.getType());
                if (settings == null) settings = new BasicSettings();

                return new SunUser(
                    plugin, uuid, name, lastOnline, dateCreated, ip, customName,
                    cooldowns, ignoredUsers, settings
                );
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
                ItemStack icon = ItemUtil.fromBase64(resultSet.getString(HOME_ICON.getName()));
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

    @NotNull
    public static DataHandler getInstance(@NotNull SunLight plugin) {
        if (instance == null) {
            instance = new DataHandler(plugin);
        }
        return instance;
    }

    @Override
    protected void onShutdown() {
        super.onShutdown();
        instance = null;
    }

    @Override
    public void onSynchronize() {
        this.plugin.getUserManager().getUsersLoaded().forEach(user -> {
            SunUser sync = this.getUser(user.getId());
            if (sync == null) return;

            user.getSettings().getSettings().clear();
            user.getSettings().getSettings().putAll(sync.getSettings().getSettings());
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
            .registerTypeAdapter(LegacyHome.class, new LegacyHomeSerializer())
            .registerTypeAdapter(BasicSettings.class, new UserSettingsSerializer())
            .registerTypeAdapter(IgnoredUser.class, new IgnoredUserSerializer())
            .registerTypeAdapter(CooldownInfo.class, new CooldownSerializer())
            ;
    }

    @Override
    protected void createUserTable() {
        super.createUserTable();

        this.addColumn(this.tableUsers, USER_SETTINGS.toValue("{}"), USER_COOLDOWNS.toValue("{}"));
        this.dropColumn(this.tableUsers,
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

        if (this.hasColumn(this.tableUsers, SQLColumn.of("homes", ColumnType.STRING))) {
            this.plugin.runTask(task -> {
                this.plugin.info("Start converting user homes into new storage format, this may take a while...");
                this.convertHomes();
                this.plugin.info("Homes converted!");
            });
        }
    }

    private void convertHomes() {
        //String sql = "SELECT " + COL_USER_UUID + ", " + COL_USER_NAME + ", homes FROM " + this.tableUsers;
        Set<Home> homesNew = new HashSet<>();
        Function<ResultSet, Void> function = resultSet -> {
            try {
                UUID owner = UUID.fromString(resultSet.getString(COLUMN_USER_ID.getName()));
                String ownerName = resultSet.getString(COLUMN_USER_NAME.getName());
                Map<String, LegacyHome> homes = gson.fromJson(resultSet.getString("homes"), new TypeToken<Map<String, LegacyHome>>(){}.getType());
                homes.values().stream().filter(Objects::nonNull).forEach(legacyHome -> {
                    homesNew.add(new Home(legacyHome, owner, ownerName));
                });
            }
            catch (SQLException | IllegalArgumentException e) {
                e.printStackTrace();
            }
            return null;
        };

        SelectQueryExecutor.builder(this.tableUsers, function)
            .columns(COLUMN_USER_ID, COLUMN_USER_NAME, SQLColumn.of("homes", ColumnType.STRING))
            .execute(this.getConnector());

        homesNew.forEach(this::addHome); // Do this out of query, otherwise it will 'stuck'.

        this.dropColumn(this.tableUsers, SQLColumn.of("homes", ColumnType.STRING));

        /*DataQueries.executeQuery(this.getConnector(), sql, this.tableUsers, Collections.emptySet(), (resultSet -> {

        }));

        homesNew.forEach(this::addHome); // Do this out of query, otherwise it will 'stuck'.
        this.removeColumn(this.tableUsers, "homes");*/
    }

    @Override
    @NotNull
    protected List<SQLColumn> getExtraColumns() {
        return Arrays.asList(USER_IP, USER_CUSTOM_NAME, USER_IGNORE_LIST, USER_COOLDOWNS, USER_SETTINGS);
    }

    @Override
    @NotNull
    protected List<SQLValue> getSaveColumns(@NotNull SunUser user) {
        return Arrays.asList(
            USER_IP.toValue(user.getIp()),
            USER_CUSTOM_NAME.toValue(user.getCustomName().orElse("null")),
            USER_IGNORE_LIST.toValue(this.gson.toJson(user.getIgnoredUsers())),
            USER_COOLDOWNS.toValue(this.gson.toJson(user.getCooldowns())),
            USER_SETTINGS.toValue(this.gson.toJson(user.getSettings()))
        );
    }

    @Override
    @NotNull
    protected Function<ResultSet, SunUser> getFunctionToUser() {
        return this.functionUser;
    }

    @NotNull
    public List<Home> getHomes() {
        return SelectQueryExecutor.builder(this.tableHomes, this.functionHome).all().execute(this.getConnector());
    }

    @NotNull
    public List<Home> getHomes(@NotNull UUID id) {
        return SelectQueryExecutor.builder(this.tableHomes, this.functionHome).all()
            .where(SQLCondition.of(HOME_OWNER_ID.toValue(id.toString()), SQLCondition.Type.EQUAL))
            .execute(this.getConnector());
    }

    @Nullable
    public Home getHome(@NotNull UUID userId, @NotNull String homeId) {
        return SelectQueryExecutor.builder(this.tableHomes, this.functionHome).all()
            .where(
                SQLCondition.of(HOME_OWNER_ID.toValue(userId.toString()), SQLCondition.Type.EQUAL),
                SQLCondition.of(HOME_ID.toValue(homeId), SQLCondition.Type.EQUAL))
            .execute(this.getConnector()).stream().findFirst().orElse(null);
    }

    @Nullable
    public Home getHome(@NotNull String userName, @NotNull String homeId) {
        return SelectQueryExecutor.builder(this.tableHomes, this.functionHome).all()
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
            HOME_ICON.toValue(String.valueOf(ItemUtil.toBase64(home.getIcon()))),
            HOME_LOCATION.toValue(String.valueOf(LocationUtil.serialize(home.getLocation()))),
            HOME_TYPE.toValue(home.getType().name()),
            HOME_INVITED_PLAYERS.toValue(this.gson.toJson(home.getInvitedPlayers())),
            HOME_IS_DEFAULT.toValue(String.valueOf(home.isDefault() ? 1 : 0)),
            HOME_IS_RESPAWN_POINT.toValue(String.valueOf(home.isRespawnPoint() ? 1 : 0))
        ));
    }

    public void saveHome(@NotNull Home home) {
        this.update(this.tableHomes,
            Arrays.asList(
                HOME_OWNER_ID.toValue(home.getOwner().getId().toString()),
                HOME_OWNER_NAME.toValue(home.getOwner().getName()),
                HOME_NAME.toValue(home.getName()),
                HOME_ICON.toValue(String.valueOf(ItemUtil.toBase64(home.getIcon()))),
                HOME_LOCATION.toValue(String.valueOf(LocationUtil.serialize(home.getLocation()))),
                HOME_TYPE.toValue(home.getType().name()),
                HOME_INVITED_PLAYERS.toValue(this.gson.toJson(home.getInvitedPlayers())),
                HOME_IS_DEFAULT.toValue(String.valueOf(home.isDefault() ? 1 : 0)),
                HOME_IS_RESPAWN_POINT.toValue(String.valueOf(home.isRespawnPoint() ? 1 : 0))
            ),
            SQLCondition.of(HOME_ID.toValue(home.getId()), SQLCondition.Type.EQUAL),
            SQLCondition.of(HOME_OWNER_ID.toValue(home.getOwner().getId().toString()), SQLCondition.Type.EQUAL)
        );
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
