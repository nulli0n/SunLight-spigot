package su.nightexpress.sunlight.module.worlds.util;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;
import su.nightexpress.sunlight.config.Config;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.worlds.config.WorldsLang;
import su.nightexpress.sunlight.module.worlds.impl.WorldData;
import su.nightexpress.sunlight.module.worlds.impl.WrappedWorld;

public class Placeholders extends su.nightexpress.sunlight.Placeholders {

    public static final String WORLD_ID           = "%world_id%";
    public static final String WORLD_IS_AUTO_LOAD = "%world_auto_load%";
    public static final String WORLD_IS_LOADED    = "%world_is_loaded%";
    public static final String WORLD_IS_CREATED   = "%world_is_created%";
    public static final String WORLD_GENERATOR    = "%world_generator%";
    public static final String WORLD_ENVIRONMENT  = "%world_environment%";
    public static final String WORLD_STRUCTURES   = "%world_structures%";

    public static final String WORLD_DIFFICULTY          = "%world_difficulty%";
    public static final String WORLD_AUTO_RESET_ENABLED  = "%world_auto_wipe_enabled%";
    public static final String WORLD_AUTO_RESET_INTERVAL = "%world_auto_wipe_interval%";
    public static final String WORLD_LAST_RESET_DATE     = "%world_auto_wipe_last_wipe%";
    public static final String WORLD_NEXT_RESET_DATE     = "%world_auto_wipe_next_wipe%";

    @NotNull
    public static PlaceholderMap forWrapped(@NotNull WrappedWorld wrappedWorld) {
        boolean isCustom = wrappedWorld.isCustom();
        WorldData worldData = wrappedWorld.getData();
        World world = wrappedWorld.getWorld();

        return new PlaceholderMap()
            .add(WORLD_ID, () -> isCustom ? worldData.getId() : world.getName())
            .add(WORLD_IS_LOADED, () -> Lang.getYesOrNo(!isCustom || worldData.isLoaded()))
            .add(WORLD_IS_CREATED, () -> Lang.getYesOrNo(!isCustom || worldData.hasWorldFiles()))
            .add(WORLD_IS_AUTO_LOAD, () -> Lang.getYesOrNo(!isCustom || worldData.isAutoLoad()))
            .add(WORLD_DIFFICULTY, () -> WorldsLang.DIFFICULTY.getLocalized(world.getDifficulty()))
            .add(WORLD_AUTO_RESET_ENABLED, () -> Lang.getYesOrNo(isCustom && worldData.isAutoReset()))
            .add(WORLD_AUTO_RESET_INTERVAL, () -> TimeUtil.formatTime(worldData.getResetInterval() * 1000L))
            .add(WORLD_LAST_RESET_DATE, () -> {
                return worldData.getLastResetDate() <= 0L ? Lang.OTHER_NEVER.getString() : Config.GENERAL_DATE_FORMAT.get().format(worldData.getLastResetDate());
            })
            .add(WORLD_NEXT_RESET_DATE, () -> {
                return worldData.getNextWipe() <= 0L ? Lang.OTHER_NEVER.getString() : Config.GENERAL_DATE_FORMAT.get().format(worldData.getNextWipe());
            })
            ;
    }

    @NotNull
    public static PlaceholderMap forGeneration(@NotNull WorldData worldData) {
        return new PlaceholderMap()
            .add(WORLD_GENERATOR, () -> worldData.getGenerator() == null ? Placeholders.DEFAULT : worldData.getGenerator())
            .add(WORLD_ENVIRONMENT, () -> StringUtil.capitalizeUnderscored(worldData.getEnvironment().name()))
            .add(WORLD_STRUCTURES, () -> Lang.getYesOrNo(worldData.isGenerateStructures()))
            ;
    }
}
