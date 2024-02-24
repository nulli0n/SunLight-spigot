package su.nightexpress.sunlight.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.lang.LangMessage;
import su.nightexpress.sunlight.SunLightAPI;
import su.nightexpress.sunlight.config.Lang;

public class FairTeleport {
    public static void fairTeleport(@NotNull Player player, @NotNull Location location, @NotNull LangMessage successMessage) {
        double beforeX = player.getLocation().getX();
        double beforeY = player.getLocation().getY();
        double beforeZ = player.getLocation().getZ();
        SunLightAPI.PLUGIN.getMessage(Lang.ERROR_TELEPORT_WARN).send(player);
        new BukkitRunnable() {
            @Override
            public void run() {
                if(player.getLocation().getX() == beforeX && player.getLocation().getY() == beforeY && player.getLocation().getZ() == beforeZ){
                    double beforeX = player.getLocation().getX();
                    double beforeY = player.getLocation().getY();
                    double beforeZ = player.getLocation().getZ();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if(player.getLocation().getX() == beforeX && player.getLocation().getY() == beforeY && player.getLocation().getZ() == beforeZ){
                                double beforeX = player.getLocation().getX();
                                double beforeY = player.getLocation().getY();
                                double beforeZ = player.getLocation().getZ();
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        if(player.getLocation().getX() == beforeX && player.getLocation().getY() == beforeY && player.getLocation().getZ() == beforeZ){
                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    if(player.getLocation().getX() == beforeX && player.getLocation().getY() == beforeY && player.getLocation().getZ() == beforeZ){
                                                        double beforeX = player.getLocation().getX();
                                                        double beforeY = player.getLocation().getY();
                                                        double beforeZ = player.getLocation().getZ();
                                                        new BukkitRunnable() {
                                                            @Override
                                                            public void run() {
                                                                if(player.getLocation().getX() == beforeX && player.getLocation().getY() == beforeY && player.getLocation().getZ() == beforeZ){
                                                                    double beforeX = player.getLocation().getX();
                                                                    double beforeY = player.getLocation().getY();
                                                                    double beforeZ = player.getLocation().getZ();
                                                                    new BukkitRunnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            if(player.getLocation().getX() == beforeX && player.getLocation().getY() == beforeY && player.getLocation().getZ() == beforeZ){
                                                                                player.teleport(location);
                                                                                successMessage.send(player);
                                                                            }else{
                                                                                SunLightAPI.PLUGIN.getMessage(Lang.ERROR_TELEPORT_MOVED).send(player);
                                                                            }
                                                                        }
                                                                    }.runTaskLater(SunLightAPI.PLUGIN, 10);
                                                                }else{
                                                                    SunLightAPI.PLUGIN.getMessage(Lang.ERROR_TELEPORT_MOVED).send(player);
                                                                }
                                                            }
                                                        }.runTaskLater(SunLightAPI.PLUGIN, 10);
                                                    }else{
                                                        SunLightAPI.PLUGIN.getMessage(Lang.ERROR_TELEPORT_MOVED).send(player);
                                                    }
                                                }
                                            }.runTaskLater(SunLightAPI.PLUGIN, 10);
                                        }else{
                                            SunLightAPI.PLUGIN.getMessage(Lang.ERROR_TELEPORT_MOVED).send(player);
                                        }
                                    }
                                }.runTaskLater(SunLightAPI.PLUGIN, 10);
                            }else{
                                SunLightAPI.PLUGIN.getMessage(Lang.ERROR_TELEPORT_MOVED).send(player);
                            }
                        }
                    }.runTaskLater(SunLightAPI.PLUGIN, 10);
                }else{
                    SunLightAPI.PLUGIN.getMessage(Lang.ERROR_TELEPORT_MOVED).send(player);
                }
            }
        }.runTaskLater(SunLightAPI.PLUGIN, 10);
    }
}
