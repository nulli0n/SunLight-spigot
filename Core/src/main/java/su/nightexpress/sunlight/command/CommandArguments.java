package su.nightexpress.sunlight.command;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.argument.CommandArgument;
import su.nightexpress.nightcore.command.experimental.builder.ArgumentBuilder;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.sunlight.command.mode.ToggleMode;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.utils.SunUtils;

import java.util.Arrays;
import java.util.function.Predicate;

public class CommandArguments {

    public static final String PLAYER   = "player";
    public static final String TARGET   = "target";
    public static final String AMOUNT   = "amount";
    public static final String TIME     = "time";
    public static final String VALUE    = "value";
    public static final String WORLD    = "world";
    public static final String TYPE     = "type";
    public static final String MODE     = "mode";
    public static final String IP       = "address";
    public static final String ITEM     = "item";
    public static final String RADIUS   = "radius";
    public static final String NAME     = "name";
    public static final String LEVEL    = "level";
    public static final String ENCHANT  = "enchant";
    public static final String TEXT     = "text";
    public static final String POSITION = "position";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";

    @NotNull
    public static ArgumentBuilder<EquipmentSlot> slot(@NotNull String name) {
        return CommandArgument.builder(name, (str, context) -> {
                EquipmentSlot slot = StringUtil.getEnum(str, EquipmentSlot.class).orElse(null);
                if (Version.isAtLeast(Version.MC_1_20_6) && slot.name().equalsIgnoreCase("BODY")) return null; //slot == EquipmentSlot.BODY

                return slot;
            })
            .localized(Lang.COMMAND_ARGUMENT_NAME_SLOT)
            .customFailure(Lang.ERROR_COMMAND_INVALID_SLOT_ARGUMENT)
            .withSamples(tabContext -> Arrays.stream(EntityUtil.EQUIPMENT_SLOTS).map(Enum::name).map(String::toLowerCase).toList())
            ;
    }

    @NotNull
    public static <E extends Enum<E>> ArgumentBuilder<E> enumed(@NotNull String name, @NotNull Class<E> clazz) {
        return CommandArgument.builder(name, (str, context) -> StringUtil.getEnum(str, clazz).orElse(null))
            .localized(Lang.COMMAND_ARGUMENT_NAME_TYPE)
            .customFailure(Lang.ERROR_COMMAND_INVALID_TYPE_ARGUMENT)
            .withSamples(tabContext -> Lists.getEnums(clazz).stream().map(String::toLowerCase).toList())
            ;
    }

    @NotNull
    public static ArgumentBuilder<EntityType> entityType(@NotNull String name) {
        return entityType(name, type -> type != EntityType.UNKNOWN);
    }

    @NotNull
    public static ArgumentBuilder<EntityType> entityType(@NotNull String name, @NotNull Predicate<EntityType> predicate) {
        return CommandArgument.builder(name, (str, context) -> {
                EntityType type = BukkitThing.getEntityType(str);
                return type != null && predicate.test(type) ? type : null;
            })
            .localized(Lang.COMMAND_ARGUMENT_NAME_TYPE)
            .customFailure(Lang.ERROR_COMMAND_INVALID_TYPE_ARGUMENT)
            .withSamples(tabContext -> SunUtils.getEntityTypes(predicate));
    }

    @NotNull
    public static ArgumentBuilder<Material> material(@NotNull String name, @NotNull Predicate<Material> predicate) {
        return CommandArgument.builder(name, (str, context) -> {
                Material type = BukkitThing.getMaterial(str);
                return type != null && predicate.test(type) ? type : null;
            })
            .localized(Lang.COMMAND_ARGUMENT_NAME_MATERIAL)
            .customFailure(Lang.ERROR_COMMAND_INVALID_MATERIAL_ARGUMENT)
            .withSamples(tabContext -> SunUtils.getMaterials(predicate));
    }

    @NotNull
    public static ArgumentBuilder<PotionEffectType> effect(@NotNull String name, @NotNull Predicate<PotionEffectType> predicate) {
        return CommandArgument.builder(name, (str, context) -> {
                PotionEffectType type = BukkitThing.getPotionEffect(str);
                return type != null && predicate.test(type) ? type : null;
            })
            .localized(Lang.COMMAND_ARGUMENT_NAME_TYPE)
            .customFailure(Lang.ERROR_COMMAND_INVALID_TYPE_ARGUMENT)
            .withSamples(tabContext -> SunUtils.getPotionEffects(predicate));
    }

    @NotNull
    public static ArgumentBuilder<ToggleMode> toggleMode(@NotNull String name) {
        return enumed(name, ToggleMode.class)
            .localized(Lang.COMMAND_ARGUMENT_NAME_MODE)
            .customFailure(Lang.ERROR_COMMAND_INVALID_MODE_ARGUMENT)
            ;
    }

    @NotNull
    public static ArgumentBuilder<String> ipAddress(@NotNull String name) {
        return CommandArgument.builder(name, (str, context) -> SunUtils.isInetAddress(str) ? str : null)
            .localized(Lang.COMMAND_ARGUMENT_NAME_IP)
            .customFailure(Lang.ERROR_COMMAND_INVALID_IP_ARGUMENT)
            ;
    }
}
