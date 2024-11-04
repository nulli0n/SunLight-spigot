package su.nightexpress.sunlight.config;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.inventory.EquipmentSlot;
import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.nightcore.language.entry.LangEnum;
import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.entry.LangText;
import su.nightexpress.nightcore.language.message.OutputType;
import su.nightexpress.sunlight.api.MenuType;
import su.nightexpress.sunlight.command.list.ExperienceCommand;
import su.nightexpress.sunlight.command.list.IgnoreCommands;
import su.nightexpress.sunlight.command.list.WeatherCommands;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.nightcore.language.tag.MessageTags.*;
import static su.nightexpress.sunlight.Placeholders.*;

public class Lang extends CoreLang {

    public static final LangEnum<EquipmentSlot>          EQUIPMENT_SLOT = LangEnum.of("EquipmentSlot", EquipmentSlot.class);
    public static final LangEnum<GameMode>               GAME_MODE      = LangEnum.of("GameMode", GameMode.class);
    public static final LangEnum<ExperienceCommand.Type> EXP_TYPE       = LangEnum.of("ExperienceType", ExperienceCommand.Type.class);
    public static final LangEnum<WeatherCommands.Type>   WEATHER_TYPE   = LangEnum.of("WeatherType", WeatherCommands.Type.class);
    public static final LangEnum<MenuType>               MENU_TYPE      = LangEnum.of("MenuType", MenuType.class);

    public static final LangString COMMAND_ARGUMENT_NAME_SLOT     = LangString.of("Command.Argument.Name.Slot", "slot");
    public static final LangString COMMAND_ARGUMENT_NAME_ENCHANT  = LangString.of("Command.Argument.Name.Enchant", "enchant");
    public static final LangString COMMAND_ARGUMENT_NAME_LEVEL    = LangString.of("Command.Argument.Name.Level", "level");
    public static final LangString COMMAND_ARGUMENT_NAME_TARGET   = LangString.of("Command.Argument.Name.Target", "target");
    public static final LangString COMMAND_ARGUMENT_NAME_TYPE     = LangString.of("Command.Argument.Name.Type", "type");
    public static final LangString COMMAND_ARGUMENT_NAME_TIME     = LangString.of("Command.Argument.Name.Time", "time");
    public static final LangString COMMAND_ARGUMENT_NAME_MODE     = LangString.of("Command.Argument.Name.Mode", "mode");
    public static final LangString COMMAND_ARGUMENT_NAME_IP       = LangString.of("Command.Argument.Name.IPAddress", "address");
    public static final LangString COMMAND_ARGUMENT_NAME_RADIUS   = LangString.of("Command.Argument.Name.Radius", "radius");
    public static final LangString COMMAND_ARGUMENT_NAME_TEXT     = LangString.of("Command.Argument.Name.Text", "text");
    //public static final LangString COMMAND_ARGUMENT_NAME_COMMAND  = LangString.of("Command.Argument.Name.Command", "command");
    public static final LangString COMMAND_ARGUMENT_NAME_POSITION = LangString.of("Command.Argument.Name.Position", "position");
    public static final LangString COMMAND_ARGUMENT_NAME_X        = LangString.of("Command.Argument.Name.X", "x");
    public static final LangString COMMAND_ARGUMENT_NAME_Y        = LangString.of("Command.Argument.Name.Y", "y");
    public static final LangString COMMAND_ARGUMENT_NAME_Z        = LangString.of("Command.Argument.Name.Z", "z");

    public static final LangText CORE_COMMAND_COOLDOWN_DEFAULT = LangText.of("Generic.Command.Cooldown.Default",
        TAG_NO_PREFIX,
        LIGHT_RED.enclose("You have to wait " + LIGHT_ORANGE.enclose(GENERIC_TIME) + " before you can use " + LIGHT_ORANGE.enclose(GENERIC_COMMAND) + " again.")
    );

    public static final LangText CORE_COMMAND_COOLDOWN_ONE_TIME = LangText.of("Generic.Command.Cooldown.OneTime",
        TAG_NO_PREFIX,
        LIGHT_RED.enclose("This command is one-time and you already have used it.")
    );

    public static final LangString COMMAND_AIR_ADD_DESC    = LangString.of("Command.Air.Add.Desc", "Add air ticks.");
    public static final LangString COMMAND_AIR_SET_DESC    = LangString.of("Command.Air.Set.Desc", "Set air ticks.");
    public static final LangString COMMAND_AIR_REMOVE_DESC = LangString.of("Command.Air.Remove.Desc", "Remove air ticks.");

    public static final LangText COMMAND_AIR_ADD_TARGET = LangText.of("Command.Air.Give.Target",
        LIGHT_GRAY.enclose("Added " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " air ticks to " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + ". New air ticks: " + LIGHT_YELLOW.enclose(GENERIC_CURRENT) + "/" + LIGHT_YELLOW.enclose(GENERIC_MAX) + ".")
    );

    public static final LangText COMMAND_AIR_REMOVE_TARGET = LangText.of("Command.Air.Take.Target",
        LIGHT_GRAY.enclose("Removed " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " air ticks from " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + ". New air ticks: " + LIGHT_YELLOW.enclose(GENERIC_CURRENT) + "/" + LIGHT_YELLOW.enclose(GENERIC_MAX) + ".")
    );

    public static final LangText COMMAND_AIR_SET_TARGET = LangText.of("Command.Air.Set.Target",
        LIGHT_GRAY.enclose("Set " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " air ticks for " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + ". New air ticks: " + LIGHT_YELLOW.enclose(GENERIC_CURRENT) + "/" + LIGHT_YELLOW.enclose(GENERIC_MAX) + ".")
    );

    public static final LangText COMMAND_AIR_ADD_NOTIFY = LangText.of("Command.Air.Give.Notify",
        OUTPUT.enclose(OutputType.ACTION_BAR),
        LIGHT_GRAY.enclose("You got " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " air ticks: " + LIGHT_YELLOW.enclose(GENERIC_CURRENT) + "/" + LIGHT_YELLOW.enclose(GENERIC_MAX) + ".")
    );

    public static final LangText COMMAND_AIR_REMOVE_NOTIFY = LangText.of("Command.Air.Take.Notify",
        OUTPUT.enclose(OutputType.ACTION_BAR),
        LIGHT_GRAY.enclose("You lost " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " air ticks: " + LIGHT_YELLOW.enclose(GENERIC_CURRENT) + "/" + LIGHT_YELLOW.enclose(GENERIC_MAX) + ".")
    );

    public static final LangText COMMAND_AIR_SET_NOTIFY = LangText.of("Command.Air.Set.Notify",
        OUTPUT.enclose(OutputType.ACTION_BAR),
        LIGHT_GRAY.enclose("Your air ticks set to " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + ": " + LIGHT_YELLOW.enclose(GENERIC_CURRENT) + "/" + LIGHT_YELLOW.enclose(GENERIC_MAX) + ".")
    );

    
    public static final LangString COMMAND_BROADCAST_DESC = LangString.of("Command.Broadcast.Desc", "Broadcast a message.");

    
    public static final LangString COMMAND_CONDENSE_DESC = LangString.of("Command.Condense.Desc", "Condense items into blocks.");
    
    public static final LangText COMMAND_CONDENSE_ERROR_NOTHING = LangText.of("Command.Condense.Error.Nothing",
        SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_GRAY.enclose("Nothing to condense.")
    );

    public static final LangText COMMAND_CONDENSE_ERROR_NOT_ENOUGH = LangText.of("Command.Condense.Error.NotEnough",
        LIGHT_GRAY.enclose("Not enough items to convert " + LIGHT_RED.enclose(GENERIC_SOURCE) + " to " + LIGHT_RED.enclose(GENERIC_RESULT) + ". Need at least " + LIGHT_RED.enclose(GENERIC_AMOUNT) + ".")
    );

    public static final LangText COMMAND_CONDENSE_DONE = LangText.of("Command.Condense.Done",
        LIGHT_GRAY.enclose("Converted " + LIGHT_YELLOW.enclose("x" + GENERIC_TOTAL + " " + GENERIC_SOURCE) + " to " + LIGHT_YELLOW.enclose("x" + GENERIC_AMOUNT + " " + GENERIC_RESULT) + ".")
    );


    public static final LangString COMMAND_CONTAINER_DESC = LangString.of("Command.Container.Type.Desc", "Open virtual " + GENERIC_TYPE + ".");

    public static final LangText COMMAND_CONTAINER_NOTIFY = LangText.of("Command.Container.Notify",
        OUTPUT.enclose(OutputType.ACTION_BAR),
        LIGHT_GRAY.enclose("You opened virtual " + LIGHT_YELLOW.enclose(GENERIC_TYPE) + ".")
    );

    public static final LangText COMMAND_CONTAINER_TARGET = LangText.of("Command.Container.Target",
        LIGHT_GRAY.enclose("Opened virtual " + LIGHT_YELLOW.enclose(GENERIC_TYPE) + " for " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + ".")
    );



    public static final LangString COMMAND_DISPOSAL_DESC = LangString.of("Command.Disposal.Desc", "Open a disposal menu.");

    public static final LangText COMMAND_DISPOSAL_TARGET = LangText.of("Command.Disposal.Target",
        LIGHT_GRAY.enclose("Opened disposal for " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + ".")
    );

    public static final LangText COMMAND_DISPOSAL_NOTIFY = LangText.of("Command.Disposal.Notify",
        OUTPUT.enclose(OutputType.ACTION_BAR) + SOUND.enclose(Sound.BLOCK_CHEST_OPEN),
        LIGHT_GRAY.enclose("You opened disposal.")
    );


    public static final LangString COMMAND_DIMENSION_DESC = LangString.of("Command.Dimension.Desc", "Teleport to a world.");

    public static final LangText COMMAND_DIMENSION_TARGET = LangText.of("Command.Dimension.Target",
        LIGHT_GRAY.enclose("Teleported " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + " to the " + LIGHT_YELLOW.enclose(GENERIC_WORLD) + ".")
    );

    public static final LangText COMMAND_DIMENSION_NOTIFY = LangText.of("Command.Dimension.Notify",
        OUTPUT.enclose(OutputType.ACTION_BAR) + SOUND.enclose(Sound.ENTITY_ENDERMAN_TELEPORT),
        LIGHT_GRAY.enclose("You travelled to the " + LIGHT_YELLOW.enclose(GENERIC_WORLD) + ".")
    );


    public static final LangString COMMAND_ENCHANT_DESC = LangString.of("Command.Enchant.Desc", "(Dis)Enchant item in a slot.");

    public static final LangText COMMAND_ENCHANT_ERROR_NO_ITEM = LangText.of("Command.Enchant.Error.NoItem",
        LIGHT_RED.enclose("No item to enchant!"));

    public static final LangText COMMAND_ENCHANT_ENCHANTED_TARGET = LangText.of("Command.Enchant.Enchanted.Target",
        SOUND.enclose(Sound.BLOCK_ENCHANTMENT_TABLE_USE),
        LIGHT_GRAY.enclose("Enchanted " + LIGHT_YELLOW.enclose(GENERIC_ITEM) + " with " +
            LIGHT_YELLOW.enclose(GENERIC_NAME + " " + GENERIC_AMOUNT) + " in " +
            LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + "'s " + LIGHT_YELLOW.enclose(GENERIC_TYPE) + " slot!")
    );


    public static final LangText COMMAND_ENCHANT_DISENCHANTED_TARGET = LangText.of("Command.Enchant.Disenchanted.Target",
        SOUND.enclose(Sound.BLOCK_GRINDSTONE_USE),
        LIGHT_GRAY.enclose("Enchantment " + LIGHT_YELLOW.enclose(GENERIC_NAME) + " have been removed from " +
            LIGHT_YELLOW.enclose(GENERIC_ITEM) + " in " +
            LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + "'s " + LIGHT_YELLOW.enclose(GENERIC_TYPE) + " slot.")
    );


    public static final LangText COMMAND_ENCHANT_ENCHANTED_NOTIFY = LangText.of("Command.Enchant.Enchanted.Notify",
        SOUND.enclose(Sound.BLOCK_ENCHANTMENT_TABLE_USE),
        LIGHT_GRAY.enclose("Your " + LIGHT_YELLOW.enclose(GENERIC_ITEM) + " have been enchanted with " +
            LIGHT_YELLOW.enclose(GENERIC_NAME + " " + GENERIC_AMOUNT) + "!")
    );


    public static final LangText COMMAND_ENCHANT_DISENCHANTED_NOTIFY = LangText.of("Command.Enchant.Disenchanted.Notify",
        SOUND.enclose(Sound.BLOCK_GRINDSTONE_USE),
        LIGHT_GRAY.enclose("Your " + LIGHT_YELLOW.enclose(GENERIC_ITEM) + " have been disenchanted from " +
            LIGHT_YELLOW.enclose(GENERIC_NAME) + "!")
    );


    public static final LangString COMMAND_EXPERIENCE_ADD_DESC    = LangString.of("Command.Experience.Add.Desc", "Add xp points or levels.");
    public static final LangString COMMAND_EXPERIENCE_REMOVE_DESC = LangString.of("Command.Experience.Remove.Desc", "Remove xp points or levels.");
    public static final LangString COMMAND_EXPERIENCE_SET_DESC    = LangString.of("Command.Experience.Set.Desc", "Set xp points or levels.");
    public static final LangString COMMAND_EXPERIENCE_VIEW_DESC   = LangString.of("Command.Experience.View.Desc", "View experience status.");

    public static final LangText COMMAND_EXPERIENCE_ADD_INFO = LangText.of("Command.Experience.Add.Info",
        TAG_NO_PREFIX,
        " ",
        LIGHT_GRAY.enclose("Added " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " " + GENERIC_TYPE + " to " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + "."),
        LIGHT_GRAY.enclose("New level: " + LIGHT_YELLOW.enclose(GENERIC_LEVEL)),
        LIGHT_GRAY.enclose("Total experience: " + LIGHT_YELLOW.enclose(GENERIC_TOTAL)),
        " "
    );

    public static final LangText COMMAND_EXPERIENCE_REMOVE_INFO = LangText.of("Command.Experience.Remove.Info",
        TAG_NO_PREFIX,
        " ",
        LIGHT_GRAY.enclose("Removed " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " " + GENERIC_TYPE + " off " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + "."),
        LIGHT_GRAY.enclose("New level: " + LIGHT_YELLOW.enclose(GENERIC_LEVEL)),
        LIGHT_GRAY.enclose("Total experience: " + LIGHT_YELLOW.enclose(GENERIC_TOTAL)),
        " "
    );

    public static final LangText COMMAND_EXPERIENCE_SET_INFO = LangText.of("Command.Experience.Set.Info",
        TAG_NO_PREFIX,
        " ",
        LIGHT_GRAY.enclose("Set " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " " + GENERIC_TYPE + " for " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + "."),
        LIGHT_GRAY.enclose("New level: " + LIGHT_YELLOW.enclose(GENERIC_LEVEL)),
        LIGHT_GRAY.enclose("Total experience: " + LIGHT_YELLOW.enclose(GENERIC_TOTAL)),
        " "
    );

    public static final LangText COMMAND_EXPERIENCE_VIEW_INFO = LangText.of("Command.Experience.Info.Info",
        TAG_NO_PREFIX,
        " ",
        LIGHT_YELLOW.enclose(BOLD.enclose("Experience of " + PLAYER_DISPLAY_NAME + ":")),
        LIGHT_GRAY.enclose("Level: " + LIGHT_YELLOW.enclose(GENERIC_LEVEL)),
        LIGHT_GRAY.enclose("Total experience: " + LIGHT_YELLOW.enclose(GENERIC_TOTAL)),
        LIGHT_GRAY.enclose("To next level: " + LIGHT_YELLOW.enclose(GENERIC_MAX)),
        " "
    );

    public static final LangText COMMAND_EXPERIENCE_ADD_NOTIFY = LangText.of("Command.Experience.Add.Notify",
        TAG_NO_PREFIX,
        " ",
        LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " " + GENERIC_TYPE + " have been added to your experience!"),
        LIGHT_GRAY.enclose("New level: " + LIGHT_YELLOW.enclose(GENERIC_LEVEL)),
        LIGHT_GRAY.enclose("Total experience: " + LIGHT_YELLOW.enclose(GENERIC_TOTAL)),
        " "
    );

    public static final LangText COMMAND_EXPERIENCE_REMOVE_NOTIFY = LangText.of("Command.Experience.Remove.Notify",
        TAG_NO_PREFIX,
        " ",
        LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " " + GENERIC_TYPE + " have been removed off your experience!"),
        LIGHT_GRAY.enclose("New level: " + LIGHT_YELLOW.enclose(GENERIC_LEVEL)),
        LIGHT_GRAY.enclose("Total experience: " + LIGHT_YELLOW.enclose(GENERIC_TOTAL)),
        " "
    );

    public static final LangText COMMAND_EXPERIENCE_SET_NOTIFY = LangText.of("Command.Experience.Set.Notify",
        TAG_NO_PREFIX,
        " ",
        LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " " + GENERIC_TYPE + " have been set as your experience!"),
        LIGHT_GRAY.enclose("New level: " + LIGHT_YELLOW.enclose(GENERIC_LEVEL)),
        LIGHT_GRAY.enclose("Total experience: " + LIGHT_YELLOW.enclose(GENERIC_TOTAL)),
        " "
    );


    public static final LangString COMMAND_ENDERCHEST_CLEAR_DESC  = LangString.of("Command.Enderchest.Clear.Desc", "Clear ender chest.");
    public static final LangString COMMAND_ENDERCHEST_COPY_DESC   = LangString.of("Command.Enderchest.Copy.Desc", "Copy player's ender chest.");
    public static final LangString COMMAND_ENDERCHEST_FILL_DESC   = LangString.of("Command.Enderchest.Fill.Desc", "Fill ender chest with specified item.");
    public static final LangString COMMAND_ENDERCHEST_OPEN_DESC   = LangString.of("Command.Enderchest.Open.Desc", "Open ender chest.");
    public static final LangString COMMAND_ENDERCHEST_REPAIR_DESC = LangString.of("Command.Enderchest.Repair.Desc", "Repair all ender chest items.");

    public static final LangText COMMAND_ENDERCHEST_CLEAR_DONE_OTHERS = LangText.of("Command.Enderchest.Clear.Done.Target",
        SOUND.enclose(Sound.BLOCK_FIRE_EXTINGUISH),
        LIGHT_GRAY.enclose("Cleared " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + "'s ender chest!")
    );

    public static final LangText COMMAND_ENDERCHEST_CLEAR_DONE_NOTIFY = LangText.of("Command.Enderchest.Clear.Done.Notify",
        SOUND.enclose(Sound.BLOCK_FIRE_EXTINGUISH),
        LIGHT_GRAY.enclose("Your ender chest has been cleared!")
    );


    public static final LangText COMMAND_ENDERCHEST_COPY_DONE_EXECUTOR = LangText.of("Command.Enderchest.Copy.Done.Executor",
        SOUND.enclose(Sound.ITEM_ARMOR_EQUIP_LEATHER),
        LIGHT_GRAY.enclose("Copied " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + "'s ender chest.")
    );

    public static final LangText COMMAND_ENDERCHEST_COPY_DONE_OTHERS = LangText.of("Command.Enderchest.Copy.Done.Others",
        SOUND.enclose(Sound.ITEM_ARMOR_EQUIP_LEATHER),
        LIGHT_GRAY.enclose("Copied " + LIGHT_YELLOW.enclose(GENERIC_SOURCE) + "'s ender chest content to " + LIGHT_YELLOW.enclose(GENERIC_TARGET) + "'s ender chest.")
    );


    public static final LangText COMMAND_ENDERCHEST_FILL_DONE_EXECUTOR = LangText.of("Command.Enderchest.Fill.Done.Executor",
        SOUND.enclose(Sound.ENTITY_ITEM_PICKUP),
        LIGHT_GRAY.enclose("Filled " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + "'s ender chest with " + LIGHT_YELLOW.enclose(GENERIC_ITEM) + ".")
    );


    public static final LangText COMMAND_ENDERCHEST_OPEN_DONE_EXECUTOR = LangText.of("Command.Enderchest.Open.Done.Executor",
        SOUND.enclose(Sound.BLOCK_ENDER_CHEST_OPEN),
        LIGHT_GRAY.enclose("Opened " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + "'s ender chest.")
    );


    public static final LangText COMMAND_ENDERCHEST_REPAIR_NOTIFY = LangText.of("Command.Enderchest.Repair.Notify",
        SOUND.enclose(Sound.BLOCK_ANVIL_USE),
        LIGHT_GRAY.enclose("Repaired all ender chest items!")
    );

    public static final LangText COMMAND_ENDERCHEST_REPAIR_TARGET = LangText.of("Command.Enderchest.Repair.Target",
        SOUND.enclose(Sound.BLOCK_ANVIL_USE),
        LIGHT_GRAY.enclose("Repaired all items in " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + "'s ender chest!")
    );



//    public static final LangString COMMAND_EXTINGUISH_DESC = LangString.of("Command.Extinguish.Desc", "Extinguish [a player].");
//
//    public static final LangText COMMAND_EXTINGUISH_NOTIFY = LangText.of("Command.Extinguish.Notify",
//        "<! type:\"action_bar\" sound:\"" + Sound.BLOCK_FIRE_EXTINGUISH.name() + "\" !>" + LIGHT_YELLOW + "You have been extinguished.");
//
//    public static final LangText COMMAND_EXTINGUISH_TARGET = LangText.of("Command.Extinguish.Target",
//        "<! sound:\"" + Sound.BLOCK_FIRE_EXTINGUISH.name() + "\" !>" + LIGHT_YELLOW + "Player " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + LIGHT_YELLOW + " have been extinguished.");



    public static final LangString COMMAND_HAT_DESC = LangString.of("Command.Hat.Desc", "Put item in head.");

    public static final LangText COMMAND_HAT_DONE = LangText.of("Command.Hat.Done",
        SOUND.enclose(Sound.ITEM_ARMOR_EQUIP_LEATHER),
        LIGHT_GRAY.enclose("Enjoy your new hat!")
    );



    public static final LangString COMMAND_FLY_DESC = LangString.of("Command.Fly.Desc", "Toggle fly.");

    public static final LangText COMMAND_FLY_TARGET = LangText.of("Command.Fly.Target",
        LIGHT_GRAY.enclose("Set flying for " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + " to " + LIGHT_YELLOW.enclose(GENERIC_STATE) + ".")
    );

    public static final LangText COMMAND_FLY_NOTIFY = LangText.of("Command.Fly.Notify",
        LIGHT_GRAY.enclose("Flying " + LIGHT_YELLOW.enclose(GENERIC_STATE) + ".")
    );


    public static final LangString COMMAND_FLY_SPEED_DESC = LangString.of("Command.FlySpeed.Desc", "Change fly speed.");

    public static final LangText COMMAND_FLY_SPEED_DONE_NOTIFY = LangText.of("Command.FlySpeed.Done.Notify",
        LIGHT_GRAY.enclose("Your fly speed has been set to " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + ".")
    );

    public static final LangText COMMAND_FLY_SPEED_DONE_TARGET = LangText.of("Command.FlySpeed.Done.Target",
        LIGHT_GRAY.enclose("Set fly speed to " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " for " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + ".")
    );



    public static final LangString COMMAND_FIRE_SET_DESC   = LangString.of("Command.Fire.Set.Desc", "Set fire ticks.");
    public static final LangString COMMAND_FIRE_RESET_DESC = LangString.of("Command.Fire.Reset.Desc", "Reset fire ticks.");

    public static final LangText COMMAND_FIRE_RESET_INFO = LangText.of("Command.Fire.Reset.Info",
        SOUND.enclose(Sound.BLOCK_FIRE_EXTINGUISH),
        LIGHT_GRAY.enclose("Reset " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + "'s fire ticks value, aka extinguished.")
    );

    public static final LangText COMMAND_FIRE_RESET_TARGET = LangText.of("Command.Fire.Reset.Target",
        SOUND.enclose(Sound.BLOCK_FIRE_EXTINGUISH),
        LIGHT_GRAY.enclose("You have been extinguished.")
    );

    public static final LangText COMMAND_FIRE_SET_INFO = LangText.of("Command.Fire.Set.Info",
        LIGHT_GRAY.enclose("Set " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + "'s fire ticks to " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + ", aka ignited for: " + LIGHT_YELLOW.enclose(GENERIC_TIME) + "s.")
    );

    public static final LangText COMMAND_FIRE_SET_TARGET = LangText.of("Command.Fire.Set.Target",
        LIGHT_GRAY.enclose("You have been ignited for " + LIGHT_YELLOW.enclose(GENERIC_TIME) + "s.")
    );



    public static final LangString COMMAND_FOOD_LEVEL_ADD_DESC     = LangString.of("Command.Food.Add.Desc", "Add food points.");
    public static final LangString COMMAND_FOOD_LEVEL_SET_DESC     = LangString.of("Command.Food.Set.Desc", "Set food level.");
    public static final LangString COMMAND_FOOD_LEVEL_REMOVE_DESC  = LangString.of("Command.Food.Remove.Desc", "Remove food points.");
    public static final LangString COMMAND_FOOD_LEVEL_RESTORE_DESC = LangString.of("Command.Food.Restore.Desc", "Restore food level.");

    public static final LangText COMMAND_FOOD_LEVEL_ADD_TARGET = LangText.of("Command.Food.Give.Target",
        LIGHT_GRAY.enclose("Added " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " food points to " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + ". New food level: " + LIGHT_YELLOW.enclose(GENERIC_CURRENT) + "."));

    public static final LangText COMMAND_FOOD_LEVEL_REMOVE_TARGET = LangText.of("Command.Food.Take.Target",
        LIGHT_GRAY.enclose("Removed " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " food points from " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + ". New food level: " + LIGHT_YELLOW.enclose(GENERIC_CURRENT) + "."));

    public static final LangText COMMAND_FOOD_LEVEL_SET_TARGET = LangText.of("Command.Food.Set.Target",
        LIGHT_GRAY.enclose("Set " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + "'s food level to " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + "."));

    public static final LangText COMMAND_FOOD_LEVEL_ADD_NOTIFY = LangText.of("Command.Food.Give.Notify",
        LIGHT_GRAY.enclose("Your food level has been increased by " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + "."));

    public static final LangText COMMAND_FOOD_LEVEL_REMOVE_NOTIFY = LangText.of("Command.Food.Take.Notify",
        LIGHT_GRAY.enclose("Your food level has been decreased by " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + "."));

    public static final LangText COMMAND_FOOD_LEVEL_SET_NOTIFY = LangText.of("Command.Food.Set.Notify",
        LIGHT_GRAY.enclose("Your food level has been set to " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + "."));

    public static final LangText COMMAND_FOOD_LEVEL_RESTORE_NOTIFY = LangText.of("Command.Food.Restore.Notify",
        SOUND.enclose(Sound.ENTITY_GENERIC_EAT),
        LIGHT_GRAY.enclose("You food level has been restored!"));

    public static final LangText COMMAND_FOOD_LEVEL_RESTORE_INFO = LangText.of("Command.Food.Restore.Info",
        SOUND.enclose(Sound.ENTITY_GENERIC_EAT),
        LIGHT_GRAY.enclose("Restored " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + "'s food level!"));



    public static final LangString COMMAND_GAME_MODE_TYPE_DESC = LangString.of("Command.GameMode.Type.Desc", "Set gamemode to " + GENERIC_TYPE + ".");

    public static final LangText COMMAND_GAME_MODE_NOTIFY = LangText.of("Command.GameMode.Notify",
        LIGHT_GRAY.enclose("Your game mode has been set to " + LIGHT_YELLOW.enclose(GENERIC_TYPE) + ".")
    );

    public static final LangText COMMAND_GAME_MODE_TARGET = LangText.of("Command.GameMode.Target",
        LIGHT_GRAY.enclose("Set " + LIGHT_YELLOW.enclose(GENERIC_TYPE) + " game mode for " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + ".")
    );


    public static final LangString COMMAND_HEALTH_ADD_DESC     = LangString.of("Command.Health.Add.Desc", "Add health points.");
    public static final LangString COMMAND_HEALTH_SET_DESC     = LangString.of("Command.Health.Set.Desc", "Set health points.");
    public static final LangString COMMAND_HEALTH_REMOVE_DESC  = LangString.of("Command.Health.Remove.Desc", "Remove health points.");
    public static final LangString COMMAND_HEALTH_RESTORE_DESC = LangString.of("Command.Health.Restore.Desc", "Restore health.");

    public static final LangText COMMAND_HEALTH_ADD_INFO = LangText.of("Command.Health.Add.Target",
        LIGHT_GRAY.enclose("Added " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " health to " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + ". New health: " + LIGHT_YELLOW.enclose(GENERIC_CURRENT) + "/" + LIGHT_YELLOW.enclose(GENERIC_MAX) + ".")
    );

    public static final LangText COMMAND_HEALTH_REMOVE_INFO = LangText.of("Command.Health.Remove.Target",
        LIGHT_GRAY.enclose("Removed " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " health from " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + ". New health: " + LIGHT_YELLOW.enclose(GENERIC_CURRENT) + "/" + LIGHT_YELLOW.enclose(GENERIC_MAX) + ".")
    );

    public static final LangText COMMAND_HEALTH_SET_INFO = LangText.of("Command.Health.Set.Target",
        LIGHT_GRAY.enclose("Set " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " health for " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + ". New health: " + LIGHT_YELLOW.enclose(GENERIC_CURRENT) + "/" + LIGHT_YELLOW.enclose(GENERIC_MAX) + ".")
    );

    public static final LangText COMMAND_HEALTH_ADD_NOTIFY = LangText.of("Command.Health.Add.Notify",
        OUTPUT.enclose(OutputType.ACTION_BAR),
        LIGHT_GRAY.enclose("You got " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " health: " + LIGHT_YELLOW.enclose(GENERIC_CURRENT) + "/" + LIGHT_YELLOW.enclose(GENERIC_MAX) + ".")
    );

    public static final LangText COMMAND_HEALTH_REMOVE_NOTIFY = LangText.of("Command.Health.Remove.Notify",
        OUTPUT.enclose(OutputType.ACTION_BAR),
        LIGHT_GRAY.enclose("You lost " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " health: " + LIGHT_YELLOW.enclose(GENERIC_CURRENT) + "/" + LIGHT_YELLOW.enclose(GENERIC_MAX) + ".")
    );

    public static final LangText COMMAND_HEALTH_SET_NOTIFY = LangText.of("Command.Health.Set.Notify",
        OUTPUT.enclose(OutputType.ACTION_BAR),
        LIGHT_GRAY.enclose("Your health has been set to " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + "/" + LIGHT_YELLOW.enclose(GENERIC_MAX) + ".")
    );

    public static final LangText COMMAND_HEALTH_RESTORE_NOTIFY = LangText.of("Command.Health.Restore.Notfiy",
        LIGHT_GRAY.enclose("You have been healed!")
    );

    public static final LangText COMMAND_HEALTH_RESTORE_INFO = LangText.of("Command.Health.Restore.Info",
        LIGHT_GRAY.enclose("Player " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + " has been healed.")
    );



    public static final LangString COMMAND_IGNORE_ADD_DESC    = LangString.of("Command.Ignore.Add.Desc", "Add player to the blacklist.");
    public static final LangString COMMAND_IGNORE_LIST_DESC   = LangString.of("Command.Ignore.List.Desc", "Open player blacklist.");
    public static final LangString COMMAND_IGNORE_REMOVE_DESC = LangString.of("Command.Ignore.Remove.Desc", "Remove player from the blacklist.");

    public static final LangText COMMAND_IGNORE_ADD_DONE = LangText.of("Command.Ignore.Add.Done",
        TAG_NO_PREFIX,
        LIGHT_YELLOW.enclose(BOLD.enclose("Player Blocked!")),
        LIGHT_GRAY.enclose("Player " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + " added to the blacklist."),
        LIGHT_GRAY.enclose("You can change settings or unblock them using " + LIGHT_YELLOW.enclose("/" + IgnoreCommands.DEF_LIST_ALIAS))
    );

    public static final LangText COMMAND_IGNORE_ADD_ERROR_ALREADY_IN = LangText.of("Command.Ignore.Add.Error.AlreadyIn",
        LIGHT_GRAY.enclose("Player is already blocked.")
    );

    public static final LangText COMMAND_IGNORE_REMOVE_DONE = LangText.of("Command.Ignore.Remove.Done",
        LIGHT_GRAY.enclose("Player " + LIGHT_YELLOW.enclose(PLAYER_NAME) + " has been unblocked.")
    );

    public static final LangText COMMAND_IGNORE_REMOVE_ERROR_NOT_IN = LangText.of("Command.Ignore.Remove.Error.NotIn",
        LIGHT_GRAY.enclose("Player is not blocked.")
    );


    public static final LangString COMMAND_INVENTORY_CLEAR_DESC  = LangString.of("Command.Inventory.Clear.Desc", "Clear inventory.");
    public static final LangString COMMAND_INVENTORY_COPY_DESC   = LangString.of("Command.Inventory.Copy.Desc", "Copy player's inventory.");
    public static final LangString COMMAND_INVENTORY_FILL_DESC   = LangString.of("Command.Inventory.Fill.Desc", "Fill inventory with specified items.");
    public static final LangString COMMAND_INVENTORY_OPEN_DESC   = LangString.of("Command.Inventory.Open.Desc", "Open player's inventory.");
    public static final LangString COMMAND_INVENTORY_REPAIR_DESC = LangString.of("Command.Inventory.Repair.Desc", "Repair all inventory items.");

    public static final LangText COMMAND_INVENTORY_CLEAR_DONE_TARGET = LangText.of("Command.Inventory.Clear.Done.Target",
        SOUND.enclose(Sound.BLOCK_FIRE_EXTINGUISH),
        LIGHT_GRAY.enclose("Cleared " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + "'s inventory!")
    );

    public static final LangText COMMAND_INVENTORY_CLEAR_DONE_NOTIFY = LangText.of("Command.Inventory.Clear.Done.Notify",
        SOUND.enclose(Sound.BLOCK_FIRE_EXTINGUISH),
        LIGHT_GRAY.enclose("Your inventory has been cleared!")
    );

    public static final LangText COMMAND_INVENTORY_COPY_DONE_INFO = LangText.of("Command.Inventory.Copy.Done.Notify",
        SOUND.enclose(Sound.ITEM_ARMOR_EQUIP_LEATHER),
        LIGHT_GRAY.enclose("Copied " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + "'s inventory.")
    );

    public static final LangText COMMAND_INVENTORY_COPY_DONE_OTHERS = LangText.of("Command.Inventory.Copy.Done.Target",
        SOUND.enclose(Sound.ITEM_ARMOR_EQUIP_LEATHER),
        LIGHT_GRAY.enclose("Copied " + LIGHT_YELLOW.enclose(GENERIC_SOURCE) + "'s inventory to " + LIGHT_YELLOW.enclose(GENERIC_TARGET) + "'s inventory.")
    );

    public static final LangText COMMAND_INVENTORY_FILL_DONE = LangText.of("Command.Inventory.Fill.Done",
        SOUND.enclose(Sound.ENTITY_ITEM_PICKUP),
        LIGHT_GRAY.enclose("Filled " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + "'s inventory with " + LIGHT_YELLOW.enclose(GENERIC_ITEM) + ".")
    );

    public static final LangText COMMAND_INVENTORY_OPEN_DONE = LangText.of("Command.Inventory.Open.Done",
        SOUND.enclose(Sound.BLOCK_CHEST_OPEN),
        LIGHT_GRAY.enclose("Opened " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + "'s inventory.")
    );

    public static final LangText COMMAND_INVENTORY_REPAIR_NOTIFY = LangText.of("Command.Inventory.Repair.Notify",
        SOUND.enclose(Sound.BLOCK_ANVIL_USE),
        LIGHT_GRAY.enclose("Repaired all inventory items!")
    );

    public static final LangText COMMAND_INVENTORY_REPAIR_TARGET = LangText.of("Command.Inventory.Repair.Target",
        SOUND.enclose(Sound.BLOCK_ANVIL_USE),
        LIGHT_GRAY.enclose("Repaired all items in " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + "'s inventory!")
    );


    public static final LangString COMMAND_ITEM_AMOUNT_DESC      = LangString.of("Command.Item.Amount.Desc", "Change item amount.");
    public static final LangString COMMAND_ITEM_DAMAGE_DESC      = LangString.of("Command.Item.Damage.Desc", "Change item's damage.");
    public static final LangString COMMAND_ITEM_ENCHANT_DESC     = LangString.of("Command.Item.Enchant.Desc", "(Dis)Enchant an item in hand.");
    public static final LangString COMMAND_ITEM_GET_DESC         = LangString.of("Command.Item.Get.Desc", "Get specified item stack.");
    public static final LangString COMMAND_ITEM_GIVE_DESC        = LangString.of("Command.Item.Give.Desc", "Give specified item stack to a player.");
    public static final LangString COMMAND_ITEM_TAKE_DESC        = LangString.of("Command.Item.Take.Desc", "Take item off a player.");
    public static final LangString COMMAND_ITEM_LORE_ADD_DESC    = LangString.of("Command.Item.Lore.Add.Desc", "Add a lore.");
    public static final LangString COMMAND_ITEM_LORE_REMOVE_DESC = LangString.of("Command.Item.Lore.Remove.Desc", "Remove a lore.");
    public static final LangString COMMAND_ITEM_LORE_CLEAR_DESC  = LangString.of("Command.Item.Lore.Clear.Desc", "Clear lore.");
    public static final LangString COMMAND_ITEM_MODEL_DESC       = LangString.of("Command.Item.Model.Desc", "Change item model data.");
    public static final LangString COMMAND_ITEM_NAME_DESC        = LangString.of("Command.Item.Name.Desc", "Change item display name.");
    public static final LangString COMMAND_ITEM_POTION_ADD_DESC  = LangString.of("Command.Item.Potion.Add.Desc", "Add effect to a potion.");
    public static final LangString COMMAND_ITEM_REPAIR_DESC      = LangString.of("Command.Item.Repair.Desc", "Repair item.");
    public static final LangString COMMAND_ITEM_SPAWN_DESC       = LangString.of("Command.Item.Spawn.Desc", "Spawn item in world.");
    public static final LangString COMMAND_ITEM_UNBREAKABLE_DESC = LangString.of("Command.Item.Unbreakable.Desc", "Toggle unbreakable state.");

    public static final LangText COMMAND_ITEM_AMOUNT_DONE = LangText.of("Command.Item.Amount.Done",
        SOUND.enclose(Sound.ENTITY_ITEM_PICKUP),
        LIGHT_GRAY.enclose("Set " + LIGHT_YELLOW.enclose(GENERIC_ITEM) + " stack amount to x" + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + ".")
    );

    public static final LangText COMMAND_ITEM_DAMAGE_DONE = LangText.of("Command.Item.Damage.Done",
        SOUND.enclose(Sound.BLOCK_ANVIL_USE),
        LIGHT_GRAY.enclose("Set " + LIGHT_YELLOW.enclose(GENERIC_ITEM) + " damage to " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + ".")
    );

    public static final LangText COMMAND_ITEM_ENCHANT_DONE_ENCHANTED = LangText.of("Command.Item.Enchant.Done.Enchanted",
        SOUND.enclose(Sound.BLOCK_ENCHANTMENT_TABLE_USE),
        LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose(GENERIC_ITEM) + " enchanted with " + LIGHT_YELLOW.enclose(GENERIC_NAME + " " + GENERIC_LEVEL) + "!")
    );

    public static final LangText COMMAND_ITEM_ENCHANT_DONE_DISENCHANTED = LangText.of("Command.Item.Enchant.Done.Disenchanted",
        SOUND.enclose(Sound.BLOCK_ENCHANTMENT_TABLE_USE),
        LIGHT_GRAY.enclose("Enchantment " + LIGHT_YELLOW.enclose(GENERIC_NAME) + " have been removed from " + LIGHT_YELLOW.enclose(GENERIC_ITEM) + "!")
    );

    public static final LangText COMMAND_ITEM_GET_DONE = LangText.of("Command.Item.Get.Done",
        SOUND.enclose(Sound.ENTITY_ITEM_PICKUP),
        LIGHT_GRAY.enclose("You got " + LIGHT_YELLOW.enclose("x" + GENERIC_AMOUNT + " " + GENERIC_TYPE) + "!")
    );

    public static final LangText COMMAND_ITEM_GIVE_DONE = LangText.of("Command.Item.Give.Done",
        SOUND.enclose(Sound.ENTITY_ITEM_PICKUP),
        LIGHT_GRAY.enclose("Added " + LIGHT_YELLOW.enclose("x" + GENERIC_AMOUNT + " " + GENERIC_TYPE) + " to " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + "!")
    );

    public static final LangText COMMAND_ITEM_GIVE_NOTIFY = LangText.of("Command.Item.Give.Notify",
        SOUND.enclose(Sound.ENTITY_ITEM_PICKUP),
        LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("x" + GENERIC_AMOUNT + " " + GENERIC_TYPE) + " have been added to your inventory!")
    );

    public static final LangText COMMAND_ITEM_TAKE_DONE = LangText.of("Command.Item.Take.Done",
        SOUND.enclose(Sound.ENTITY_GLOW_ITEM_FRAME_REMOVE_ITEM),
        LIGHT_GRAY.enclose("Removed " + LIGHT_YELLOW.enclose("x" + GENERIC_AMOUNT) + " of " + LIGHT_YELLOW.enclose(GENERIC_TYPE) + " from " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + "!")
    );

    public static final LangText COMMAND_ITEM_TAKE_ERROR_NOT_ENOUGH = LangText.of("Command.Item.Take.Error.NotEnough",
        SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_GRAY.enclose("Could not take item(s). Player don't have enough " + LIGHT_RED.enclose(GENERIC_TYPE) + " (" + WHITE.enclose(GENERIC_AMOUNT) + "/" + WHITE.enclose(GENERIC_TOTAL) + ")!")
    );

    public static final LangText COMMAND_ITEM_TAKE_NOTIFY = LangText.of("Command.Item.Take.Notify",
        SOUND.enclose(Sound.ENTITY_GLOW_ITEM_FRAME_REMOVE_ITEM),
        LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("x" + GENERIC_AMOUNT) + " of " + LIGHT_YELLOW.enclose(GENERIC_TYPE) + " has been taken from your inventory!")
    );

    public static final LangText COMMAND_ITEM_LORE_ADD_DONE = LangText.of("Command.Item.Lore.Add.Done",
        SOUND.enclose(Sound.BLOCK_ANVIL_USE),
        LIGHT_GRAY.enclose("Lore added!")
    );

    public static final LangText COMMAND_ITEM_LORE_REMOVE_DONE = LangText.of("Command.Item.Lore.Remove.Done",
        SOUND.enclose(Sound.BLOCK_ANVIL_USE),
        LIGHT_GRAY.enclose("Lore removed!")
    );

    public static final LangText COMMAND_ITEM_LORE_CLEAR_DONE = LangText.of("Command.Item.Lore.Clear.Done",
        SOUND.enclose(Sound.BLOCK_ANVIL_USE),
        LIGHT_GRAY.enclose("Lore cleared!")
    );

    public static final LangText COMMAND_ITEM_MODEL_DONE = LangText.of("Command.Item.Model.Done",
        SOUND.enclose(Sound.BLOCK_ANVIL_USE),
        LIGHT_GRAY.enclose("Set " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " as " + LIGHT_YELLOW.enclose(GENERIC_ITEM) + "'s model data!")
    );

    public static final LangText COMMAND_ITEM_NAME_DONE = LangText.of("Command.Item.Name.Done.Changed",
        SOUND.enclose(Sound.BLOCK_ANVIL_USE),
        LIGHT_GRAY.enclose("Item renamed!")
    );

    public static final LangText ERROR_ITEM_NOT_POTION = LangText.of("Error.ItemNotAPotion",
        LIGHT_RED.enclose("You must hold a potion!")
    );

    public static final LangText COMMAND_ITEM_POTION_ADD_DONE = LangText.of("Command.Item.Potion.Add.Done",
        LIGHT_GRAY.enclose("Potion effect added!")
    );

    public static final LangText COMMAND_ITEM_REPAIR_DONE = LangText.of("Command.Item.Repair.Done",
        SOUND.enclose(Sound.BLOCK_ANVIL_USE),
        LIGHT_GRAY.enclose("Item " + LIGHT_YELLOW.enclose(GENERIC_ITEM) + " repaired!")
    );

    public static final LangText COMMAND_ITEM_SPAWN_DONE = LangText.of("Command.Item.Spawn.Done",
        SOUND.enclose(Sound.ENTITY_ITEM_PICKUP),
        LIGHT_GRAY.enclose("Spawned " + LIGHT_YELLOW.enclose("x" + GENERIC_AMOUNT) + " " + LIGHT_YELLOW.enclose(GENERIC_TYPE) + " at " + LIGHT_YELLOW.enclose(LOCATION_X + ", " + LOCATION_Y + ", " + LOCATION_Z) + " in " + LIGHT_YELLOW.enclose(LOCATION_WORLD) + "!")
    );

    public static final LangText COMMAND_ITEM_UNBREAKABLE_DONE = LangText.of("Command.Item.Unbreakable.Done",
        SOUND.enclose(Sound.BLOCK_ANVIL_USE),
        LIGHT_GRAY.enclose("Set " + LIGHT_YELLOW.enclose(GENERIC_ITEM) + " Unbreakable: " + LIGHT_YELLOW.enclose(GENERIC_STATE) + ".")
    );



    public static final LangString COMMAND_MOB_KILL_DESC = LangString.of("Command.Mob.Kill.Desc", "Kill specific mob.");
    public static final LangString COMMAND_MOB_CLEAR_DESC = LangString.of("Command.Mob.Clear.Desc", "Kill all mobs.");
    public static final LangString COMMAND_MOB_SPAWN_DESC = LangString.of("Command.Mob.Spawn.Desc", "Spawn a mob.");

    public static final LangText COMMAND_MOB_KILL_DONE = LangText.of("Command.Mob.Kill.Done",
        SOUND.enclose(Sound.ENTITY_ZOMBIE_DEATH),
        LIGHT_GRAY.enclose("Killed " + LIGHT_YELLOW.enclose("x" + GENERIC_AMOUNT + " " + GENERIC_TYPE) + " in " + LIGHT_YELLOW.enclose(GENERIC_WORLD) + " within " + LIGHT_YELLOW.enclose(GENERIC_RADIUS) + " blocks.")
    );

    public static final LangText COMMAND_MOB_CLEAR_DONE = LangText.of("Command.Mob.Clear.Done",
        SOUND.enclose(Sound.ENTITY_ZOMBIE_DEATH),
        LIGHT_GRAY.enclose("Killed " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " mobs in " + LIGHT_YELLOW.enclose(GENERIC_WORLD) + " within " + LIGHT_YELLOW.enclose(GENERIC_RADIUS) + " blocks.")
    );

    public static final LangText COMMAND_MOB_SPAWN_DONE = LangText.of("Command.Mob.Spawn.Done",
        LIGHT_GRAY.enclose("Created " + LIGHT_YELLOW.enclose("x" + GENERIC_AMOUNT + " " + GENERIC_TYPE) + ".")
    );



    public static final LangString COMMAND_NEAR_DESC = LangString.of("Command.Near.Desc", "Show nearest players.");

    public static final LangText COMMAND_NEAR_NOTHING = LangText.of("Command.Near.Error.Nothing",
        LIGHT_GRAY.enclose("There are no players in a " + LIGHT_YELLOW.enclose(GENERIC_RADIUS) + " block radius.")
    );



    public static final LangString COMMAND_NICK_CLEAR_DESC = LangString.of("Command.Nick.Clear.Desc", "Remove custom name.");
    public static final LangString COMMAND_NICK_SET_DESC = LangString.of("Command.Nick.Set.Desc", "Set player's custom name.");
    public static final LangString COMMAND_NICK_CHANGE_DESC = LangString.of("Command.Nick.Change.Desc", "Set custom name.");

    public static final LangText COMMAND_NICK_CLEAR_TARGET = LangText.of("Command.Nick.Clear.Target",
        LIGHT_GRAY.enclose("Removed " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + "'s custom name."));

    public static final LangText COMMAND_NICK_CLEAR_NOTIFY = LangText.of("Command.Nick.Clear.Notify",
        LIGHT_GRAY.enclose("You custom name has been removed."));

    public static final LangText COMMAND_NICK_SET_TARGET = LangText.of("Command.Nick.Set.Target",
        LIGHT_GRAY.enclose("Set " + LIGHT_YELLOW.enclose(PLAYER_NAME) + "'s custom name to " + LIGHT_YELLOW.enclose(GENERIC_NAME) + "."));

    public static final LangText COMMAND_NICK_SET_NOTIFY = LangText.of("Command.Nick.Set.Notify",
        LIGHT_GRAY.enclose("You got a new custom name: " + LIGHT_YELLOW.enclose(GENERIC_NAME) + "."));

    public static final LangText COMMAND_NICK_CHANGE_DONE = LangText.of("Command.Nick.Change.Done",
        LIGHT_GRAY.enclose("You changed your custom name to " + LIGHT_YELLOW.enclose(GENERIC_NAME) + "."));

    public static final LangText COMMAND_NICK_CHANGE_ERROR_BAD_WORDS = LangText.of("Command.Nick.Error.BadWords",
        LIGHT_RED.enclose("This name is not allowed."));

    public static final LangText COMMAND_NICK_CHANGE_ERROR_REGEX = LangText.of("Command.Nick.Error.Regex",
        LIGHT_RED.enclose("Name contains forbidden characters."));

    public static final LangText COMMAND_NICK_CHANGE_ERROR_TOO_LONG = LangText.of("Command.Nick.Error.TooLong",
        LIGHT_GRAY.enclose("Name can't be no longer than " + LIGHT_RED.enclose(GENERIC_AMOUNT) + " characters."));

    public static final LangText COMMAND_NICK_CHANGE_ERROR_TOO_SHORT = LangText.of("Command.Nick.Error.TooShort",
        LIGHT_RED.enclose("Name can't be shorted than " + LIGHT_RED.enclose(GENERIC_AMOUNT) + " characters."));



    public static final LangString COMMAND_PLAYER_INFO_DESC = LangString.of("Command.PlayerInfo.Desc", "Show player info.");



    public static final LangString COMMAND_SKULL_CUSTOM_DESC = LangString.of("Command.Skull.Custom.Desc", "Create custom head.");
    public static final LangString COMMAND_SKULL_PLAYER_DESC = LangString.of("Command.Skull.Player.Desc", "Get player head.");

    public static final LangText COMMAND_SKULL_PLAYER_DONE = LangText.of("Command.Skull.Player.Done",
        LIGHT_GRAY.enclose("You got " + LIGHT_YELLOW.enclose(PLAYER_NAME) + "'s head.")
    );

    public static final LangText COMMAND_SKULL_CUSTOM_DONE = LangText.of("Command.Skull.Custom.Done",
        LIGHT_GRAY.enclose("Created custom head.")
    );



    public static final LangString COMMAND_STAFF_DESC = LangString.of("Command.Staff.Desc", "Show online staff.");

    public static final LangText COMMAND_STAFF_EMPTY = LangText.of("Command.Staff.Empty",
        LIGHT_GRAY.enclose("There is no staff online.")
    );



    public static final LangString COMMAND_SPAWNER_DESC = LangString.of("Command.Spawner.Desc", "Change spawner type.");

    public static final LangText COMMAND_SPAWNER_DONE = LangText.of("Command.Spawner.Done",
        LIGHT_GRAY.enclose("Spawner type changed to " + LIGHT_YELLOW.enclose(GENERIC_TYPE) + ".")
    );

    public static final LangText COMMAND_SPAWNER_ERROR_TYPE = LangText.of("Command.Spawner.Error.Type",
        LIGHT_RED.enclose("This type can not be set for spawner.")
    );

    public static final LangText COMMAND_SPAWNER_ERROR_BLOCK = LangText.of("Command.Spawner.Error.Block",
        LIGHT_RED.enclose("You must look at Spawner block!")
    );



    public static final LangString COMMAND_SPEED_DESC = LangString.of("Command.Speed.Desc", "Change walk speed.");

    public static final LangText COMMAND_SPEED_DONE_NOTIFY = LangText.of("Command.Speed.Done.Notify",
        LIGHT_GRAY.enclose("Your walk speed has been set to " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + ".")
    );

    public static final LangText COMMAND_SPEED_DONE_TARGET = LangText.of("Command.Speed.Done.Target",
        LIGHT_GRAY.enclose("Set walk speed to " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " for " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + LIGHT_YELLOW + ".")
    );



    public static final LangString COMMAND_SUDO_COMMAND_DESC = LangString.of("Command.Sudo.Command.Desc", "Force player to execute a command.");
    public static final LangString COMMAND_SUDO_CHAT_DESC    = LangString.of("Command.Sudo.Chat.Desc", "Force player to send a chat message.");

    public static final LangText COMMAND_SUDO_DONE_COMMAND = LangText.of("Command.Sudo.Done.Command",
        LIGHT_GRAY.enclose("Forced " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + " to perform: " + LIGHT_YELLOW.enclose("/" + GENERIC_COMMAND))
    );

    public static final LangText COMMAND_SUDO_DONE_CHAT = LangText.of("Command.Sudo.Done.Chat",
        LIGHT_GRAY.enclose("Forced " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + " to say: " + LIGHT_YELLOW.enclose(GENERIC_COMMAND))
    );



    public static final LangString COMMAND_SUICIDE_DESC = LangString.of("Command.Suicide.Desc", "Commit suicide.");

    public static final LangText COMMAND_SUICIDE_DONE = LangText.of("Command.Suicide.Done",
        TAG_NO_PREFIX,
        LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + " commited suicide.")
    );


    public static final LangString COMMAND_TELEPORT_LOCATION_DESC = LangString.of("Command.Teleport.Location.Desc", "Teleport to coordinates.");
    public static final LangString COMMAND_TELEPORT_SUMMON_DESC   = LangString.of("Command.Teleport.Summon.Desc", "Summon player to you.");
    public static final LangString COMMAND_TELEPORT_TO_DESC       = LangString.of("Command.Teleport.To.Desc", "Teleport to a player.");
    public static final LangString COMMAND_TELEPORT_SEND_DESC     = LangString.of("Command.Teleport.Send.Desc", "Teleport one player to another.");
    public static final LangString COMMAND_TELEPORT_TOP_DESC      = LangString.of("Command.Teleport.Top.Desc", "Teleport to the highest block.");

    public static final LangText COMMAND_TELEPORT_LOCATION_DONE = LangText.of("Command.Teleport.Location.Done.Target",
        SOUND.enclose(Sound.ENTITY_ENDERMAN_TELEPORT),
        LIGHT_GRAY.enclose("Player " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + " teleported to " + LIGHT_YELLOW.enclose(LOCATION_X + ", " + LOCATION_Y + ", " + LOCATION_Z) + " in " + LIGHT_YELLOW.enclose(LOCATION_WORLD) + ".")
    );

    public static final LangText COMMAND_TELEPORT_LOCATION_NOTIFY = LangText.of("Command.Teleport.Location.Done.Notify",
        SOUND.enclose(Sound.ENTITY_ENDERMAN_TELEPORT),
        LIGHT_GRAY.enclose("Teleport to " + LIGHT_YELLOW.enclose(LOCATION_X + ", " + LOCATION_Y + ", " + LOCATION_Z) + " in " + LIGHT_YELLOW.enclose(LOCATION_WORLD) + ".")
    );

    public static final LangText COMMAND_TELEPORT_SUMMON_DONE = LangText.of("Command.Teleport.Summon.Target",
        SOUND.enclose(Sound.ENTITY_ENDERMAN_TELEPORT),
        LIGHT_GRAY.enclose("You summonned " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + ".")
    );

    public static final LangText COMMAND_TELEPORT_SUMMON_NOTIFY = LangText.of("Command.Teleport.Summon.Notify",
        SOUND.enclose(Sound.ENTITY_ENDERMAN_TELEPORT),
        LIGHT_GRAY.enclose("You were summoned by " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + ".")
    );

    public static final LangText COMMAND_TELEPORT_TO_DONE = LangText.of("Command.Teleport.To.Done",
        SOUND.enclose(Sound.ENTITY_ENDERMAN_TELEPORT),
        LIGHT_GRAY.enclose("Teleport to " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + "...")
    );

    public static final LangText COMMAND_TELEPORT_SEND_DONE = LangText.of("Command.Teleport.Send.Target",
        SOUND.enclose(Sound.ENTITY_ENDERMAN_TELEPORT),
        LIGHT_GRAY.enclose("Teleported " + LIGHT_YELLOW.enclose(GENERIC_SOURCE) + " to " + LIGHT_YELLOW.enclose(GENERIC_TARGET) + ".")
    );

    public static final LangText COMMAND_TELEPORT_SEND_NOTIFY = LangText.of("Command.Teleport.Send.Notify",
        SOUND.enclose(Sound.ENTITY_ENDERMAN_TELEPORT),
        LIGHT_GRAY.enclose("You have been teleported to " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + ".")
    );

    public static final LangText COMMAND_TELEPORT_TOP_DONE = LangText.of("Command.Teleport.Top.Target",
        SOUND.enclose(Sound.ENTITY_ENDERMAN_TELEPORT),
        LIGHT_GRAY.enclose("Teleported " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + " on the highest block.")
    );

    public static final LangText COMMAND_TELEPORT_TOP_NOTIFY = LangText.of("Command.Teleport.Top.Notify",
        SOUND.enclose(Sound.ENTITY_ENDERMAN_TELEPORT),
        LIGHT_GRAY.enclose("Teleport on the highest block...")
    );


    public static final LangString COMMAND_TIME_SHOW_DESC           = LangString.of("Command.Time.Show.Desc", "Display current world time.");
    public static final LangString COMMAND_TIME_SET_DESC            = LangString.of("Command.Time.Set.Desc", "Set world's time.");
    public static final LangString COMMAND_TIME_PERSONAL_SET_DESC   = LangString.of("Command.Time.Personal.Set.Desc", "Set personal time.");
    public static final LangString COMMAND_TIME_PERSONAL_RESET_DESC = LangString.of("Command.Time.Personal.Reset.Desc", "Reset personal time.");

    public static final LangText COMMAND_TIME_SHOW_INFO = LangText.of("Command.Time.Show.Print",
        TAG_NO_PREFIX,
        " ",
        LIGHT_YELLOW.enclose(BOLD.enclose("Time Info:")),
        LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("▪ ") + GENERIC_WORLD + " Time: " + LIGHT_YELLOW.enclose(GENERIC_TIME) + " (" + WHITE.enclose(GENERIC_TICKS + " ticks") + ")"),
        LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("▪ ") + "Server Time: " + LIGHT_YELLOW.enclose(GENERIC_GLOBAL)),
        " "
    );

    public static final LangText COMMAND_TIME_SET_DONE = LangText.of("Command.Time.Set.Done",
        LIGHT_GRAY.enclose("Time set to " + LIGHT_YELLOW.enclose(GENERIC_TIME) + " (" + WHITE.enclose(GENERIC_TOTAL + " ticks") + ")" + " in world " + LIGHT_YELLOW.enclose(GENERIC_WORLD) + ".")
    );

    public static final LangText COMMAND_TIME_PERSONAL_SET_DONE = LangText.of("Command.Time.Personal.Set.Target",
        LIGHT_GRAY.enclose("Set " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + "'s time to " + LIGHT_YELLOW.enclose(GENERIC_TIME) + " (" + WHITE.enclose(GENERIC_TOTAL + " ticks") + ")" + ".")
    );

    public static final LangText COMMAND_TIME_PERSONAL_SET_NOTIFY = LangText.of("Command.Time.Personal.Set.Notify",
        LIGHT_GRAY.enclose("Your personal time has been set to " + LIGHT_YELLOW.enclose(GENERIC_TIME) + " (" + WHITE.enclose(GENERIC_TOTAL + " ticks") + ")" + ".")
    );

    public static final LangText COMMAND_TIME_PERSONAL_RESET_DONE = LangText.of("Command.Time.Personal.Reset.Target",
        LIGHT_GRAY.enclose("Reset " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + "'s time.")
    );

    public static final LangText COMMAND_TIME_PERSONAL_RESET_NOTIFY = LangText.of("Command.Time.Personal.Reset.Notify",
        LIGHT_GRAY.enclose("Your personal time has been reset.")
    );



    public static final LangString COMMAND_SMITE_DESC = LangString.of("Command.Smite.Desc", "Smite player with lightning.");

    public static final LangText COMMAND_SMITE_TARGET = LangText.of("Command.Smite.Target",
        LIGHT_GRAY.enclose("Smited " + LIGHT_YELLOW.enclose(PLAYER_NAME) + "!")
    );

    public static final LangText COMMAND_SMITE_NOTIFY = LangText.of("Command.Smite.Notify",
        LIGHT_GRAY.enclose("You have been smited!")
    );



    public static final LangString COMMAND_WEATHER_TYPE_DESC = LangString.of("Command.Weather.Type.Desc", "Set weather to " + GENERIC_TYPE + ".");

    public static final LangText COMMAND_WEATHER_SET = LangText.of("Command.Weather.Set",
        LIGHT_GRAY.enclose("Set " + LIGHT_YELLOW.enclose(GENERIC_TYPE) + " weather in " + LIGHT_YELLOW.enclose(GENERIC_WORLD) + ".")
    );



    public static final LangText ERROR_EMPTY_HAND = LangText.of("Error.EmptyHand",
        SOUND.enclose(Sound.BLOCK_ANVIL_PLACE),
        LIGHT_RED.enclose("You must hold an item!"));

    public static final LangText ERROR_ITEM_NOT_DAMAGEABLE = LangText.of("Error.ItemNotDamageable",
        SOUND.enclose(Sound.BLOCK_ANVIL_PLACE),
        LIGHT_GRAY.enclose("Item " + LIGHT_RED.enclose(GENERIC_ITEM) + " doesn't have a durability.")
    );

    public static final LangText ERROR_COMMAND_INVALID_SLOT_ARGUMENT = LangText.of("Error.Command.Argument.InvalidSlot",
        LIGHT_GRAY.enclose(LIGHT_RED.enclose(GENERIC_VALUE) + " is not a valid equipment slot!"));

    public static final LangText ERROR_COMMAND_INVALID_TYPE_ARGUMENT = LangText.of("Error.Command.Argument.InvalidType",
        LIGHT_GRAY.enclose(LIGHT_RED.enclose(GENERIC_VALUE) + " is not a valid type!"));

    public static final LangText ERROR_COMMAND_INVALID_TIME_ARGUMENT = LangText.of("Error.Command.Argument.InvalidTime",
        LIGHT_GRAY.enclose(LIGHT_RED.enclose(GENERIC_VALUE) + " is not a valid time!"));

    public static final LangText ERROR_COMMAND_INVALID_MODE_ARGUMENT = LangText.of("Error.Command.Argument.InvalidMode",
        LIGHT_GRAY.enclose(LIGHT_RED.enclose(GENERIC_VALUE) + " is not a valid mode!"));

    public static final LangText ERROR_COMMAND_INVALID_IP_ARGUMENT = LangText.of("Error.Command.Argument.InvalidIPAddress",
        LIGHT_GRAY.enclose(LIGHT_RED.enclose(GENERIC_VALUE) + " is not an IP address!"));



    public static final LangString EDITOR_INPUT_GENERIC_VALUE = LangString.of("Editor.Input.Generic.Value",
        LIGHT_GRAY.enclose("Enter " + LIGHT_GREEN.enclose("[Value]")));

    public static final LangString EDITOR_INPUT_GENERIC_SECONDS = LangString.of("Editor.Input.Generic.Seconds",
        LIGHT_GRAY.enclose("Enter " + LIGHT_GREEN.enclose("[Seconds Amount]")));

    public static final LangString EDITOR_INPUT_GENERIC_NAME = LangString.of("Editor.Input.Generic.Name",
        LIGHT_GRAY.enclose("Enter " + LIGHT_GREEN.enclose("[Name]")));
}
