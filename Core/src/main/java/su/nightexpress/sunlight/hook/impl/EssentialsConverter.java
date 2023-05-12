package su.nightexpress.sunlight.hook.impl;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.UserMap;
import com.earth2me.essentials.Warps;
import org.bukkit.Location;
import org.bukkit.Material;
import su.nexmedia.engine.utils.FileUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.SunLightAPI;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.hook.HookId;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.impl.LegacyHome;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.data.impl.IgnoredUser;
import su.nightexpress.sunlight.data.impl.settings.BasicSettings;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@Deprecated
public class EssentialsConverter {

    private static final String PREFIX = "[Essentials Converter] ";

    public static void convert() {
        SunLight plugin = SunLightAPI.PLUGIN;

        Essentials essentials = (Essentials) plugin.getPluginManager().getPlugin(HookId.ESSENTIALS);
        if (essentials == null) return;

        HomesModule homesModule = plugin.getModuleManager().getModule(HomesModule.class).orElse(null);

        UserMap essUsers = essentials.getUserMap();
        for (File file : FileUtil.getFiles(essentials.getDataFolder() + "/userdata/", false)) {
            //for (UUID essUserId : essUsers.getAllUniqueUsers()) {
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
            String ip = essUser.getLastLoginAddress();
            double balance = essUser.getMoney().doubleValue();

            Map<String, LegacyHome> homes = new HashMap<>();
            if (homesModule != null) {
                for (String eHomeId : essUser.getHomes()) {
                    try {
                        Location eHomeLoc = essUser.getHome(eHomeId);
                        if (eHomeLoc == null || eHomeLoc.getWorld() == null) continue;

                        LegacyHome sunHome = new LegacyHome(eHomeId, name, eHomeId, Material.GRASS_BLOCK, eHomeLoc, new HashSet<>(), false);
                        homes.put(sunHome.getId(), sunHome);

                        plugin.info(PREFIX + "Home converted: " + eHomeId + " / " + uuid + " / " + name);
                    } catch (Exception e) {
                        plugin.error(PREFIX + "Home error: " + eHomeId + " / " + uuid + " / " + name);
                        e.printStackTrace();
                    }
                }
            }

            Map<String, Long> kitCooldowns = new HashMap<>();
            Map<String, Long> commandCooldowns = new HashMap<>();
            Map<UUID, IgnoredUser> ignoredUsers = new HashMap<>();
			/*essUser._getIgnoredPlayers().forEach(ignoredName -> {
				ignoredUsers.put(ignoredName.toLowerCase(), new IgnoredUser(ignoredName));
			});*/

            String nickName = essUser.getNickname();
            if (nickName == null) nickName = name;

            // TODO Homes convert

            SunUser suser = new SunUser(
                plugin, essUserId, name, login, System.currentTimeMillis(), ip, nickName,
                new HashMap<>(),
                ignoredUsers,
                new BasicSettings()
            );

            plugin.getData().addUser(suser);
            plugin.getUserManager().getUsersLoadedMap().put(suser.getId(), suser);

            plugin.info(PREFIX + "User converted: " + uuid + " / " + name);
        }

        WarpsModule warpsModule = plugin.getModuleManager().getModule(WarpsModule.class).orElse(null);
        if (warpsModule != null) {
            Warps warps = essentials.getWarps();
            for (String wId : warps.getList()) {
                try {
                    Location wLoc = warps.getWarp(wId);
                    UUID lastOwner = warps.getLastOwner(wId);

                    // TODO
                    //Warp warp = new Warp(warpsModule, wId, lastOwner, wLoc, WarpType.SERVER);
                    //warpsModule.getWarpsMap().put(warp.getId(), warp);

                    plugin.info(PREFIX + "Warp converted: " + wId);
                } catch (Exception e) {
                    plugin.info(PREFIX + "Warp error: " + wId);
                    //e.printStackTrace();
                }
            }
        }
    }
}
