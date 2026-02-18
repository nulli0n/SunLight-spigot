package su.nightexpress.sunlight.module.spawns;

import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.placeholder.TypedPlaceholder;

public class SpawnsPlaceholders {

    public static final String SPAWN_ID    = "%spawn_id%";
    public static final String SPAWN_NAME  = "%spawn_name%";
    public static final String SPAWN_WORLD = "%spawn_location_world%";
    public static final String SPAWN_X     = "%spawn_location_x%";
    public static final String SPAWN_Y     = "%spawn_location_y%";
    public static final String SPAWN_Z     = "%spawn_location_z%";

    public static final TypedPlaceholder<Spawn> SPAWN = TypedPlaceholder.builder(Spawn.class)
        .with(SPAWN_ID, Spawn::getId)
        .with(SPAWN_NAME, Spawn::getName)
        .with(SPAWN_WORLD, spawn -> spawn.isActive() ? LangAssets.get(spawn.getWorld()) : spawn.getWorldName())
        .with(SPAWN_X, spawn -> NumberUtil.format(spawn.getBlockPos().getX()))
        .with(SPAWN_Y, spawn -> NumberUtil.format(spawn.getBlockPos().getY()))
        .with(SPAWN_Z, spawn -> NumberUtil.format(spawn.getBlockPos().getZ()))
        .build();
}
