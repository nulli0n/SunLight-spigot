package su.nightexpress.sunlight.module.kits.util;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.kits.Kit;
import su.nightexpress.sunlight.module.kits.config.KitsLang;

public class Placeholders extends su.nightexpress.sunlight.Placeholders {

    public static final String KIT_ID                  = "%kit_id%";
    public static final String KIT_NAME                = "%kit_name%";
    public static final String KIT_DESCRIPTION         = "%kit_description%";
    public static final String KIT_PERMISSION_REQUIRED = "%kit_permission_required%";
    public static final String KIT_PERMISSION_NODE     = "%kit_permission_node%";
    public static final String KIT_COOLDOWN            = "%kit_cooldown%";
    public static final String KIT_COST                = "%kit_cost_money%";
    public static final String KIT_PRIORITY            = "%kit_priority%";
    public static final String KIT_COMMANDS            = "%kit_commands%";

    @NotNull
    public static PlaceholderMap forKit(@NotNull Kit kit) {
        return new PlaceholderMap()
            .add(KIT_ID, kit::getId)
            .add(KIT_NAME, kit::getName)
            .add(KIT_DESCRIPTION, () -> String.join("\n", kit.getDescription()))
            .add(KIT_PERMISSION_REQUIRED, () -> Lang.getYesOrNo(kit.isPermissionRequired()))
            .add(KIT_PERMISSION_NODE, kit::getPermission)
            .add(KIT_COOLDOWN, () -> kit.hasCooldown() ? (kit.isOneTimed() ? KitsLang.OTHER_ONE_TIMED.getString() : TimeUtil.formatTime(kit.getCooldown() * 1000L)) : KitsLang.OTHER_DISABLED.getString())
            .add(KIT_COST, () -> kit.hasCost() ? NumberUtil.format(kit.getCost()) : KitsLang.OTHER_DISABLED.getString())
            .add(KIT_PRIORITY, () -> String.valueOf(kit.getPriority()))
            .add(KIT_COMMANDS, () -> String.join("\n", kit.getCommands().stream().map(Placeholders::listEntry).toList()));
    }
}
