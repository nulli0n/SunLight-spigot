package su.nightexpress.sunlight.economy.Hooks.impl;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.UserMap;
import su.nexmedia.engine.utils.FileUtil;
import su.nightexpress.sunlight.economy.Hooks.HookId;
import su.nightexpress.sunlight.economy.SunLightEconomyPlugin;
import su.nightexpress.sunlight.economy.data.EconomyUser;

import java.io.File;
import java.util.UUID;

@Deprecated
public class EssentialsConverter {

    private static final String PREFIX = "[Essentials Converter] ";

    public static void convert(SunLightEconomyPlugin plugin) {
        Essentials essentials = (Essentials) plugin.getPluginManager().getPlugin(HookId.ESSENTIALS);
        if (essentials == null) return;

        UserMap essUsers = essentials.getUserMap();
        for (File file : FileUtil.getFiles(essentials.getDataFolder() + "/userdata/", false)) {
            UUID essUserId = UUID.fromString(file.getName().replace(".yml", ""));
            User essUser;
            try {
                essUser = essUsers.load(essUserId.toString());
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            String uuid = essUserId.toString();
            String name = essUser.getName();
            if (name == null) name = "null";

            long login = essUser.getLastLogout();
            double balance = essUser.getMoney().doubleValue();

            EconomyUser economyUser = new EconomyUser(plugin, essUserId, essUser.getName(), login,
                    System.currentTimeMillis(), balance);

            plugin.getData().addUser(economyUser);
            plugin.getUserManager().getUsersLoadedMap().put(economyUser.getId(), economyUser);

            plugin.info(PREFIX + "User economy converted: " + uuid + " / " + name);
            plugin.info(PREFIX + "User balance: " + balance);
        }
    }
}
