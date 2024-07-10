package su.nightexpress.sunlight.module.warps.util;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.warps.config.WarpsLang;
import su.nightexpress.sunlight.module.warps.impl.Warp;
import su.nightexpress.sunlight.utils.SunUtils;

public class Placeholders extends su.nightexpress.sunlight.Placeholders {

    public static final String WARP_ID                  = "%warp_id%";
    public static final String WARP_NAME                = "%warp_name%";
    public static final String WARP_DESCRIPTION         = "%warp_description%";
    public static final String WARP_TYPE                = "%warp_type%";
    public static final String WARP_PERMISSION_REQUIRED = "%warp_permission_required%";
    public static final String WARP_PERMISSION_NODE     = "%warp_permission_node%";
    public static final String WARP_VISIT_COST          = "%warp_visit_cost%";
    public static final String WARP_VISIT_COOLDOWN      = "%warp_visit_cooldown%";
    public static final String WARP_VISIT_TIMES         = "%warp_visit_times%";
    public static final String WARP_LOCATION_WORLD      = "%warp_location_world%";
    public static final String WARP_LOCATION_X          = "%warp_location_x%";
    public static final String WARP_LOCATION_Y          = "%warp_location_y%";
    public static final String WARP_LOCATION_Z          = "%warp_location_z%";
    public static final String WARP_OWNER_NAME          = "%warp_owner_name%";
    public static final String WARP_COMMAND_SHORTCUT    = "%warp_command_shortcut%";

    @NotNull
    public static PlaceholderMap forWarp(@NotNull Warp warp) {
        return new PlaceholderMap()
            .add(WARP_ID, warp.getId())
            .add(WARP_NAME, warp::getName)
            .add(WARP_DESCRIPTION, () -> warp.getDescription() == null ? "" : warp.getDescription())
            .add(WARP_VISIT_COST, () -> warp.hasVisitCost() ? NumberUtil.format(warp.getVisitCostMoney()) : WarpsLang.OTHER_DISABLED.getString())
            .add(WARP_VISIT_COOLDOWN, () -> warp.hasVisitCooldown() ? TimeUtil.formatTime(warp.getVisitCooldown() * 1000L) : WarpsLang.OTHER_DISABLED.getString())
            .add(WARP_VISIT_TIMES, () -> {
                return String.join("\n", warp.getVisitTimes().stream().map(pair -> {
                    return SunUtils.formatTime(pair.getFirst()) + " - " + SunUtils.formatTime(pair.getSecond());
                }).toList());
            })
            .add(WARP_LOCATION_X, () -> NumberUtil.format(warp.getBlockPos().getX()))
            .add(WARP_LOCATION_Y, () -> NumberUtil.format(warp.getBlockPos().getY()))
            .add(WARP_LOCATION_Z, () -> NumberUtil.format(warp.getBlockPos().getZ()))
            .add(WARP_LOCATION_WORLD, () -> warp.isValid() ? LangAssets.get(warp.getWorld()) : warp.getWorldName())
            .add(WARP_OWNER_NAME, () -> warp.getOwner().getName())
            .add(WARP_TYPE, () -> WarpsLang.WARP_TYPE.getLocalized(warp.getType()))
            .add(WARP_PERMISSION_REQUIRED, () -> Lang.getYesOrNo(warp.isPermissionRequired()))
            .add(WARP_PERMISSION_NODE, warp.getPermission())
            .add(WARP_COMMAND_SHORTCUT, () -> warp.getCommandShortcut() == null ? "-" : warp.getCommandShortcut())
            ;
    }
}
