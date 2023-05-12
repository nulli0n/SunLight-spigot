package su.nightexpress.sunlight.economy.data;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.data.AbstractUserDataHandler;
import su.nexmedia.engine.api.data.sql.SQLColumn;
import su.nexmedia.engine.api.data.sql.SQLCondition;
import su.nexmedia.engine.api.data.sql.SQLValue;
import su.nexmedia.engine.api.data.sql.column.ColumnType;
import su.nexmedia.engine.api.data.sql.executor.SelectQueryExecutor;
import su.nightexpress.sunlight.economy.SunLightEconomyPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

public class EconomyDataHandler extends AbstractUserDataHandler<SunLightEconomyPlugin, EconomyUser> {

    private static EconomyDataHandler INSTANCE;

    private final Function<ResultSet, EconomyUser> functionUser;

    private static final SQLColumn COL_BALANCE = SQLColumn.of("balance", ColumnType.DOUBLE);

    protected EconomyDataHandler(@NotNull SunLightEconomyPlugin plugin) {
        super(plugin, plugin);

        this.functionUser = resultSet -> {
            try {
                UUID uuid = UUID.fromString(resultSet.getString(COLUMN_USER_ID.getName()));
                String name = resultSet.getString(COLUMN_USER_NAME.getName());
                long lastOnline = resultSet.getLong(COLUMN_USER_LAST_ONLINE.getName());
                long dateCreated = resultSet.getLong(COLUMN_USER_DATE_CREATED.getName());
                double balance = resultSet.getDouble(COL_BALANCE.getName());

                return new EconomyUser(plugin, uuid, name, lastOnline, dateCreated, balance);
            }
            catch (SQLException e) {
                return null;
            }
        };
    }

    public static EconomyDataHandler getInstance(@NotNull SunLightEconomyPlugin plugin) throws SQLException {
        if (INSTANCE == null) {
            INSTANCE = new EconomyDataHandler(plugin);
        }
        return INSTANCE;
    }

    @Override
    protected void onShutdown() {
        super.onShutdown();
        INSTANCE = null;
    }

    @Override
    public void onSynchronize() {
        this.dataHolder.getUserManager().getUsersLoaded().forEach(this::updateUserBalance);
    }

    private void updateUserBalance(@NotNull EconomyUser user) {
        Function<ResultSet, Double> function = (resultSet -> {
            try {
                return resultSet.getDouble(COL_BALANCE.getName());
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            return -1D;
        });

        Double balance = this.load(this.tableUsers, function,
            Collections.singletonList(COL_BALANCE),
            Collections.singletonList(SQLCondition.equal(COLUMN_USER_ID.toValue(user.getId().toString()))))
            .orElse(null);
        if (balance == null) return;

        user.setBalanceRaw(balance);
    }

    @Override
    @NotNull
    protected List<SQLColumn> getExtraColumns() {
        return Collections.singletonList(COL_BALANCE);
    }

    @Override
    @NotNull
    protected List<SQLValue> getSaveColumns(@NotNull EconomyUser user) {
        return Collections.singletonList(COL_BALANCE.toValue(user.getBalance()));
    }

    @Override
    @NotNull
    protected Function<ResultSet, EconomyUser> getFunctionToUser() {
        return this.functionUser;
    }

    @NotNull
    public Map<String, Double> getUserBalance() {
        //Map<String, Double> map = new HashMap<>();
        //String sql = "SELECT `name`, `balance` FROM " + this.tableUsers;

        Map<String, Double> map = new HashMap<>();
        Function<ResultSet, Void> function = (resultSet -> {
            try {
                String name = resultSet.getString(COLUMN_USER_NAME.getName());
                double balance = resultSet.getDouble(COL_BALANCE.getName());
                map.put(name, balance);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });

        SelectQueryExecutor.builder(this.tableUsers, function).columns(COLUMN_USER_NAME, COL_BALANCE)
            .execute(this.getConnector());

        return map;//this.load(this.tableUsers, function,
            //Arrays.asList(COLUMN_USER_NAME, COL_BALANCE), Collections.emptyList()).orElse(Collections.emptyMap());

        /*try (Statement statement = this.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String name = resultSet.getString(COL_USER_NAME);
                double balance = resultSet.getDouble(COL_BALANCE);
                map.put(name, balance);
            }
            return map;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return map;
        }*/
    }
}
