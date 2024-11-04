package su.nightexpress.sunlight.command;

import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.wrapper.UniPermission;
import su.nightexpress.sunlight.api.MenuType;
import su.nightexpress.sunlight.command.list.WeatherCommands;
import su.nightexpress.sunlight.command.mode.ModifyMode;
import su.nightexpress.sunlight.config.Perms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class CommandPerms {

    public static final String PREFIX = Perms.PREFIX + "command.";

    @NotNull
    public static UniPermission create(@NotNull String name) {
        return new UniPermission(PREFIX + name);
    }

    @NotNull
    public static UniPermission createOthers(@NotNull String name) {
        return new UniPermission(PREFIX + name + ".others");
    }

    @NotNull
    public static UniPermission create(@NotNull String name, @NotNull String... childrens) {
        StringBuilder builder = new StringBuilder(PREFIX + name);
        for (String child : childrens) {
            if (!builder.isEmpty()) builder.append(".");
            builder.append(child);
        }
        return new UniPermission(builder.toString());
    }

    @NotNull
    public static UniPermission createOthers(@NotNull String name, @NotNull String... childrens) {
        List<String> childs = new ArrayList<>(Arrays.asList(childrens));
        childs.add("others");
        return create(name, childs.toArray(new String[0]));
    }

    public static final UniPermission RELOAD = create("reload");

    public static final Function<ModifyMode, UniPermission> AIR_MODE        = mode -> create("air", mode.name().toLowerCase());
    public static final Function<ModifyMode, UniPermission> AIR_MODE_OTHERS = mode -> createOthers("air", mode.name().toLowerCase());

    public static final UniPermission BROADCAST = create("broadcast");

    public static final UniPermission CONDENSE = create("condense");

    public static final Function<MenuType, UniPermission> CONTAINER_TYPE        = type -> create("container", type.name().toLowerCase());
    public static final Function<MenuType, UniPermission> CONTAINER_TYPE_OTHERS = type -> createOthers("container", type.name().toLowerCase());


    public static final UniPermission DIMENSION        = create("dimension");
    public static final UniPermission DIMENSION_OTHERS = createOthers("dimension");
    public static final UniPermission DISPOSAL         = create("disposal");
    public static final UniPermission DISPOSAL_OTHERS  = createOthers("disposal");

    public static final UniPermission ENDERCHEST_CLEAR         = create("enderchest", "clear");
    public static final UniPermission ENDERCHEST_CLEAR_OTHERS  = createOthers("enderchest", "clear");
    public static final UniPermission ENDERCHEST_COPY          = create("enderchest", "copy");
    public static final UniPermission ENDERCHEST_COPY_OTHERS   = createOthers("enderchest", "copy");
    public static final UniPermission ENDERCHEST_FILL          = create("enderchest", "fill");
    public static final UniPermission ENDERCHEST_OPEN          = create("enderchest", "open");
    public static final UniPermission ENDERCHEST_OPEN_OTHERS   = createOthers("enderchest", "open");
    public static final UniPermission ENDERCHEST_REPAIR        = create("enderchest", "repair");
    public static final UniPermission ENDERCHEST_REPAIR_OTHERS = createOthers("enderchest", "repair");

    public static final UniPermission ENCHANT                  = create("enchant");
    public static final UniPermission ENCHANT_OTHERS           = createOthers("enchant");

    public static final Function<ModifyMode, UniPermission> EXPERIENCE_MODE        = mode -> create("experience", mode.name().toLowerCase());
    public static final Function<ModifyMode, UniPermission> EXPERIENCE_MODE_OTHERS = mode -> createOthers("experience", mode.name().toLowerCase());
    public static final UniPermission                       EXPERIENCE_VIEW        = create("experience", "view");
    public static final UniPermission                       EXPERIENCE_VIEW_OTHERS = createOthers("experience", "view");

    public static final UniPermission FIRE_SET          = create("fire.set");
    public static final UniPermission FIRE_SET_OTHERS   = createOthers("fire.set", "others");
    public static final UniPermission FIRE_RESET        = create("fire.reset");
    public static final UniPermission FIRE_RESET_OTHERS = createOthers("fire.reset", "others");

    public static final UniPermission FLY              = create("fly");
    public static final UniPermission FLY_OTHERS       = createOthers("fly");
    public static final UniPermission FLY_SPEED        = create("flyspeed");
    public static final UniPermission FLY_SPEED_OTHERS = createOthers("flyspeed");

    public static final Function<ModifyMode, UniPermission> FOOD_LEVEL_MODE           = mode -> create("foodlevel", mode.name().toLowerCase());
    public static final Function<ModifyMode, UniPermission> FOOD_LEVEL_MODE_OTHERS    = mode -> createOthers("foodlevel", mode.name().toLowerCase());
    public static final UniPermission                       FOOD_LEVEL_RESTORE        = create("foodlevel", "restore");
    public static final UniPermission                       FOOD_LEVEL_RESTORE_OTHERS = createOthers("foodlevel", "restore");

    public static final Function<GameMode, UniPermission> GAMEMODE_TYPE   = mode -> create("gamemode", mode.name().toLowerCase());
    public static final Function<GameMode, UniPermission> GAMEMODE_TYPE_OTHERS   = mode -> createOthers("gamemode", mode.name().toLowerCase());

    public static final UniPermission HAT = create("hat");

    public static final Function<ModifyMode, UniPermission> HEALTH_MODE           = mode -> create("health", mode.name().toLowerCase());
    public static final Function<ModifyMode, UniPermission> HEALTH_MODE_OTHERS    = mode -> createOthers("health", mode.name().toLowerCase());
    public static final UniPermission                       HEALTH_RESTORE        = create("health", "restore");
    public static final UniPermission                       HEALTH_RESTORE_OTHERS = createOthers("health", "restore");

    public static final UniPermission IGNORE_ADD        = create("ignore.add");
    public static final UniPermission IGNORE_LIST        = create("ignore.list");
    public static final UniPermission IGNORE_LIST_OTHERS = createOthers("ignore.list");
    public static final UniPermission IGNORE_REMOVE        = create("ignore.remove");

    public static final UniPermission INVENTORY_CLEAR         = create("inventory", "clear");
    public static final UniPermission INVENTORY_CLEAR_OTHERS  = createOthers("inventory", "clear");
    public static final UniPermission INVENTORY_COPY          = create("inventory", "copy");
    public static final UniPermission INVENTORY_COPY_OTHERS   = createOthers("inventory", "copy");
    public static final UniPermission INVENTORY_FILL          = create("inventory", "fill");
    public static final UniPermission INVENTORY_OPEN          = create("inventory", "open");
    public static final UniPermission INVENTORY_REPAIR        = create("inventory", "repair");
    public static final UniPermission INVENTORY_REPAIR_OTHERS = createOthers("inventory", "repair");

    public static final UniPermission ITEM_AMOUNT      = create("item", "amount");
    public static final UniPermission ITEM_DAMAGE      = create("item", "damage");
    public static final UniPermission ITEM_UNBREAKABLE = create("item", "unbreakable");
    public static final UniPermission ITEM_ENCHANT     = create("item", "enchant");
    public static final UniPermission ITEM_GET         = create("item", "get");
    public static final UniPermission ITEM_GIVE        = create("item", "give");
    public static final UniPermission ITEM_MODEL       = create("item", "model");
    public static final UniPermission ITEM_NAME        = create("item", "name");
    public static final UniPermission ITEM_LORE        = create("item", "lore");
    public static final UniPermission ITEM_POTION      = create("item", "potion");
    public static final UniPermission ITEM_REPAIR      = create("item", "repair");
    public static final UniPermission ITEM_SPAWN       = create("item", "spawn");
    public static final UniPermission ITEM_TAKE        = create("item", "take");

    public static final UniPermission MOB_KILL  = create("mob", "kill");
    public static final UniPermission MOB_CLEAR = create("mob", "clear");
    public static final UniPermission MOB_SPAWN = create("mob", "spawn");

    public static final UniPermission NEAR = create("near");

    public static final UniPermission NICK_SET           = create("nick", "set");
    public static final UniPermission NICK_CHANGE        = create("nick", "change");
    public static final UniPermission NICK_CLEAR         = create("nick", "clear");
    public static final UniPermission NICK_CLEAR_OTHERS  = createOthers("nick", "clear");
    public static final UniPermission NICK_COLORS        = create("nick", "colors");
    public static final UniPermission NICK_BYPASS_WORDS  = create("nick", "bypass.words");
    public static final UniPermission NICK_BYPASS_REGEX  = create("nick", "bypass.regex");
    public static final UniPermission NICK_BYPASS_LENGTH = create("nick", "bypass.length");


    public static final UniPermission PLAYER_INFO = create("playerinfo");

    public static final UniPermission SKULL_CUSTOM        = create("skull", "custom");
    public static final UniPermission SKULL_PLAYER        = create("skull", "player");
    public static final UniPermission SKULL_PLAYER_OTHERS = createOthers("skull", "player");

    public static final UniPermission SMITE        = create("smite");
    public static final UniPermission SMITE_OTHERS = createOthers("smite");

    public static final UniPermission                       SPAWNER      = create("spawner");
    public static final Function<EntityType, UniPermission> SPAWNER_TYPE = type -> create("spawner", "type." + BukkitThing.toString(type));

    public static final UniPermission SPEED        = create("speed");
    public static final UniPermission SPEED_OTHERS = createOthers("speed");

    public static final UniPermission STAFF = create("staff");

    public static final UniPermission SUDO_CHAT    = create("sudo", "chat");
    public static final UniPermission SUDO_COMMAND = create("sudo", "command");
    public static final UniPermission SUDO_BYPASS  = create("sudo", "bypass");

    public static final UniPermission SUICIDE = create("suicide");

    public static final UniPermission TELEPORT_LOCATION        = create("teleport", "location");
    public static final UniPermission TELEPORT_LOCATION_OTHERS = createOthers("teleport", "location");
    public static final UniPermission TELEPORT_SUMMON          = create("teleport", "summon");
    public static final UniPermission TELEPORT_TO              = create("teleport", "to");
    public static final UniPermission TELEPORT_SEND            = create("teleport", "send");
    public static final UniPermission TELEPORT_TOP             = create("teleport", "top");
    public static final UniPermission TELEPORT_TOP_OTHERS      = createOthers("teleport", "top");

    public static final UniPermission TIME_SHOW                  = create("time", "show");
    public static final UniPermission TIME_SET                   = create("time", "set");
    public static final UniPermission TIME_PERSONAL_SET          = create("time", "personal.set");
    public static final UniPermission TIME_PERSONAL_SET_OTHERS   = createOthers("time", "personal.set");
    public static final UniPermission TIME_PERSONAL_RESET        = create("time", "personal.reset");
    public static final UniPermission TIME_PERSONAL_RESET_OTHERS = createOthers("time", "personal.reset");

    public static final Function<WeatherCommands.Type, UniPermission> WEATHER_TYPE = type -> create("weather", type.name().toLowerCase());

    static {
        Perms.COMMAND.addChildren(
            RELOAD,

            BROADCAST,

            CONDENSE,

            DIMENSION, DIMENSION_OTHERS,
            DISPOSAL, DISPOSAL_OTHERS,

            ENDERCHEST_CLEAR, ENDERCHEST_CLEAR_OTHERS,
            ENDERCHEST_COPY, ENDERCHEST_COPY_OTHERS,
            ENDERCHEST_FILL,
            ENDERCHEST_OPEN, ENDERCHEST_OPEN_OTHERS,
            ENDERCHEST_REPAIR, ENDERCHEST_REPAIR_OTHERS,

            ENCHANT, ENCHANT_OTHERS,
            EXPERIENCE_VIEW, EXPERIENCE_VIEW_OTHERS,

            FIRE_SET, FIRE_SET_OTHERS,
            FIRE_RESET, FIRE_RESET_OTHERS,
            FLY, FLY_OTHERS,
            FLY_SPEED, FLY_SPEED_OTHERS,

            FOOD_LEVEL_RESTORE, FOOD_LEVEL_RESTORE_OTHERS,

            HAT,
            HEALTH_RESTORE, HEALTH_RESTORE_OTHERS,

            IGNORE_ADD,
            IGNORE_LIST, IGNORE_LIST_OTHERS,
            IGNORE_REMOVE,

            INVENTORY_CLEAR, INVENTORY_CLEAR_OTHERS,
            INVENTORY_COPY, INVENTORY_COPY_OTHERS,
            INVENTORY_FILL,
            INVENTORY_OPEN,
            INVENTORY_REPAIR, INVENTORY_REPAIR_OTHERS,

            ITEM_AMOUNT, ITEM_DAMAGE, ITEM_ENCHANT,
            ITEM_GET, ITEM_GIVE, ITEM_LORE, ITEM_MODEL, ITEM_NAME,
            ITEM_POTION, ITEM_SPAWN, ITEM_TAKE, ITEM_UNBREAKABLE,

            MOB_CLEAR, MOB_KILL, MOB_SPAWN,

            NEAR,

            NICK_COLORS,
            NICK_BYPASS_LENGTH, NICK_BYPASS_REGEX, NICK_BYPASS_WORDS,
            NICK_CHANGE,
            NICK_CLEAR, NICK_CLEAR_OTHERS,
            NICK_SET,

            PLAYER_INFO,

            STAFF,

            SKULL_CUSTOM, SKULL_PLAYER, SKULL_PLAYER_OTHERS,
            SMITE, SMITE_OTHERS,
            SPAWNER,
            SPEED, SPEED_OTHERS,
            SUDO_CHAT, SUDO_COMMAND, SUDO_BYPASS,
            SUICIDE,

            TELEPORT_LOCATION, TELEPORT_LOCATION_OTHERS,
            TELEPORT_SEND, TELEPORT_SUMMON,
            TELEPORT_TO,
            TELEPORT_TOP, TELEPORT_TOP_OTHERS,

            TIME_SET, TIME_SHOW,
            TIME_PERSONAL_SET, TIME_PERSONAL_SET_OTHERS,
            TIME_PERSONAL_RESET, TIME_PERSONAL_RESET_OTHERS
        );

        for (MenuType menuType : MenuType.values()) {
            Perms.COMMAND.addChildren(
                CONTAINER_TYPE.apply(menuType),
                CONTAINER_TYPE_OTHERS.apply(menuType)
            );
        }

        for (GameMode mode : GameMode.values()) {
            Perms.COMMAND.addChildren(
                GAMEMODE_TYPE.apply(mode),
                GAMEMODE_TYPE_OTHERS.apply(mode)
            );
        }

        for (ModifyMode mode : ModifyMode.values()) {
            Perms.COMMAND.addChildren(
                AIR_MODE.apply(mode),
                AIR_MODE_OTHERS.apply(mode),
                EXPERIENCE_MODE.apply(mode),
                EXPERIENCE_MODE_OTHERS.apply(mode),
                HEALTH_MODE.apply(mode),
                HEALTH_MODE_OTHERS.apply(mode),
                FOOD_LEVEL_MODE.apply(mode),
                FOOD_LEVEL_MODE_OTHERS.apply(mode)
            );
        }

        for (WeatherCommands.Type type : WeatherCommands.Type.values()) {
            Perms.COMMAND.addChildren(WEATHER_TYPE.apply(type));
        }
    }
}
