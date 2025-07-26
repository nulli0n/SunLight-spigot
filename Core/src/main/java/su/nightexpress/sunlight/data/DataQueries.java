package su.nightexpress.sunlight.data;

import com.google.gson.reflect.TypeToken;
import su.nightexpress.sunlight.core.cooldown.CooldownInfo;
import su.nightexpress.sunlight.core.cooldown.CooldownType;
import su.nightexpress.sunlight.core.user.IgnoredUser;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.data.user.UserSettings;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

public class DataQueries {

    public static final Function<ResultSet, SunUser> USER_LOAD = (resultSet) -> {
        try {
            UUID uuid = UUID.fromString(resultSet.getString(DataHandler.COLUMN_USER_ID.getName()));
            String name = resultSet.getString(DataHandler.COLUMN_USER_NAME.getName());
            long dateCreated = resultSet.getLong(DataHandler.COLUMN_USER_DATE_CREATED.getName());
            long lastOnline = resultSet.getLong(DataHandler.COLUMN_USER_LAST_ONLINE.getName());

            String ip = resultSet.getString(DataHandler.COLUMN_USER_ADDRESS.getName());
            //String customName = resultSet.getString(USER_CUSTOM_NAME.getName());

            Map<CooldownType, Set<CooldownInfo>> cooldowns = DataHandler.GSON.fromJson(resultSet.getString(DataHandler.USER_COOLDOWNS.getName()), new TypeToken<Map<CooldownType, Set<CooldownInfo>>>(){}.getType());
            if (cooldowns == null) cooldowns = new HashMap<>();

            Map<UUID, IgnoredUser> ignoredUsers = DataHandler.GSON.fromJson(resultSet.getString(DataHandler.USER_IGNORE_LIST.getName()), new TypeToken<Map<UUID, IgnoredUser>>() {}.getType());
            if (ignoredUsers == null) ignoredUsers = new HashMap<>();
            else ignoredUsers.values().removeIf(Objects::isNull);

            UserSettings settings = DataHandler.GSON.fromJson(resultSet.getString(DataHandler.USER_SETTINGS.getName()), new TypeToken<UserSettings>(){}.getType());
            if (settings == null) settings = new UserSettings();

            return new SunUser(uuid, name, dateCreated, lastOnline, ip, cooldowns, ignoredUsers, settings);
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    };
}
