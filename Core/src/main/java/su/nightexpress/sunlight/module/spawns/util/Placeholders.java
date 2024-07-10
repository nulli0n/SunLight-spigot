package su.nightexpress.sunlight.module.spawns.util;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.spawns.config.SpawnsPerms;
import su.nightexpress.sunlight.module.spawns.impl.Spawn;

public class Placeholders extends su.nightexpress.sunlight.Placeholders {

    public static final String SPAWN_ID                       = "%spawn_id%";
    public static final String SPAWN_DISPLAY_NAME             = "%spawn_name%";
    public static final String SPAWN_LOCATION_WORLD           = "%spawn_location_world%";
    public static final String SPAWN_LOCATION_X               = "%spawn_location_x%";
    public static final String SPAWN_LOCATION_Y               = "%spawn_location_y%";
    public static final String SPAWN_LOCATION_Z               = "%spawn_location_z%";
    public static final String SPAWN_PERMISSION_REQUIRED      = "%spawn_permission_required%";
    public static final String SPAWN_PERMISSION_NODE          = "%spawn_permission_node%";
    //public static final String SPAWN_IS_DEFAULT               = "%spawn_is_default%";
    public static final String SPAWN_PRIORITY                 = "%spawn_priority%";
    public static final String SPAWN_LOGIN_TELEPORT_ENABLED   = "%spawn_login_teleport_enabled%";
    //public static final String SPAWN_LOGIN_TELEPORT_NEWBIES   = "%spawn_login_teleport_newbies%";
    public static final String SPAWN_LOGIN_TELEPORT_GROUPS    = "%spawn_login_teleport_groups%";
    public static final String SPAWN_RESPAWN_TELEPORT_ENABLED = "%spawn_respawn_teleport_enabled%";
    public static final String SPAWN_RESPAWN_TELEPORT_GROUPS  = "%spawn_respawn_teleport_groups%";

    @NotNull
    public static PlaceholderMap forSpawn(@NotNull Spawn spawn) {
        return new PlaceholderMap()
            .add(Placeholders.SPAWN_ID, spawn::getId)
            .add(Placeholders.SPAWN_DISPLAY_NAME, spawn::getName)
            ;
    }

    @NotNull
    public static PlaceholderMap forSpawnEditor(@NotNull Spawn spawn) {
        return new PlaceholderMap()
            .add(Placeholders.SPAWN_LOCATION_WORLD, () -> spawn.isValid() ? LangAssets.get(spawn.getWorld()) : spawn.getWorldName())
            .add(Placeholders.SPAWN_LOCATION_X, () -> NumberUtil.format(spawn.getBlockPos().getX()))
            .add(Placeholders.SPAWN_LOCATION_Y, () -> NumberUtil.format(spawn.getBlockPos().getY()))
            .add(Placeholders.SPAWN_LOCATION_Z, () -> NumberUtil.format(spawn.getBlockPos().getZ()))
            .add(Placeholders.SPAWN_PERMISSION_REQUIRED, () -> Lang.getYesOrNo(spawn.isPermissionRequired()))
            .add(Placeholders.SPAWN_PERMISSION_NODE, () -> SpawnsPerms.PREFIX_SPAWN + spawn.getId())
            .add(Placeholders.SPAWN_PRIORITY, () -> String.valueOf(spawn.getPriority()))
            .add(Placeholders.SPAWN_LOGIN_TELEPORT_ENABLED, () -> Lang.getYesOrNo(spawn.isLoginTeleport()))
            .add(Placeholders.SPAWN_RESPAWN_TELEPORT_ENABLED, () -> Lang.getYesOrNo(spawn.isDeathTeleport()))
            .add(Placeholders.SPAWN_LOGIN_TELEPORT_GROUPS, () -> String.join("\n", spawn.getLoginGroups().stream().map(Placeholders::listEntry).toList()))
            .add(Placeholders.SPAWN_RESPAWN_TELEPORT_GROUPS, () -> String.join("\n", spawn.getRespawnGroups().stream().map(Placeholders::listEntry).toList()))
            ;
    }
}
