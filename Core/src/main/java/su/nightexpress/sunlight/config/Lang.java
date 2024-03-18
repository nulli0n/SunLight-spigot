package su.nightexpress.sunlight.config;

import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.lang.LangKey;
import su.nexmedia.engine.lang.EngineLang;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightAPI;
import su.nightexpress.sunlight.command.ignore.IgnoreCommand;
import su.nightexpress.sunlight.command.list.CondenseCommand;
import su.nightexpress.sunlight.command.teleport.TeleportAcceptCommand;
import su.nightexpress.sunlight.command.teleport.TeleportCommand;
import su.nightexpress.sunlight.command.teleport.TeleportDeclineCommand;
import su.nightexpress.sunlight.command.time.TimeShowCommand;

import static su.nexmedia.engine.utils.Colors.*;
import static su.nightexpress.sunlight.Placeholders.*;

public class Lang extends EngineLang {

    private static final String ACTION_BAR = "<! type:\"action_bar\" !>";
    private static final String NO_PREFIX = "<! prefix:\"false\" !>";

    public static final LangKey GENERIC_COMMAND_COOLDOWN_DEFAULT  = LangKey.of("Generic.Command.Cooldown.Default", NO_PREFIX + RED + "You have to wait " + ORANGE + Placeholders.GENERIC_TIME + RED + " before you can use " + ORANGE + Placeholders.GENERIC_COMMAND + RED + " again.");
    public static final LangKey GENERIC_COMMAND_COOLDOWN_ONE_TIME = LangKey.of("Generic.Command.Cooldown.OneTime", NO_PREFIX + RED + "This command is one-time and you already have used it.");

    public static final LangKey COMMAND_AIR_DESC          = LangKey.of("Command.Air.Desc", "Manage [player's] air ticks.");
    public static final LangKey COMMAND_AIR_USAGE         = LangKey.of("Command.Air.Usage", "<action> <amount> [player] [-max] [-s]");
    public static final LangKey COMMAND_AIR_ADD_TARGET    = LangKey.of("Command.Air.Give.Target", LIGHT_YELLOW + "Added " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " air ticks to " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ". New air ticks: " + ORANGE + Placeholders.GENERIC_CURRENT + LIGHT_YELLOW + "/" + ORANGE + Placeholders.GENERIC_MAX + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_AIR_REMOVE_TARGET = LangKey.of("Command.Air.Take.Target", LIGHT_YELLOW + "Removed " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " air ticks from " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ". New air ticks: " + ORANGE + Placeholders.GENERIC_CURRENT + LIGHT_YELLOW + "/" + ORANGE + Placeholders.GENERIC_MAX + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_AIR_SET_TARGET    = LangKey.of("Command.Air.Set.Target", LIGHT_YELLOW + "Set " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " air ticks for " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ". New air ticks: " + ORANGE + Placeholders.GENERIC_CURRENT + LIGHT_YELLOW + "/" + ORANGE + Placeholders.GENERIC_MAX + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_AIR_ADD_NOTIFY    = LangKey.of("Command.Air.Give.Notify", ACTION_BAR + LIGHT_YELLOW + "You got " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " air ticks: " + ORANGE + Placeholders.GENERIC_CURRENT + LIGHT_YELLOW + "/" + ORANGE + Placeholders.GENERIC_MAX + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_AIR_REMOVE_NOTIFY = LangKey.of("Command.Air.Take.Notify", ACTION_BAR + LIGHT_YELLOW + "You lost " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " air ticks: " + ORANGE + Placeholders.GENERIC_CURRENT + LIGHT_YELLOW + "/" + ORANGE + Placeholders.GENERIC_MAX + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_AIR_SET_NOTIFY    = LangKey.of("Command.Air.Set.Notify", ACTION_BAR + LIGHT_YELLOW + "Your air ticks set to " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + ": " + ORANGE + Placeholders.GENERIC_CURRENT + LIGHT_YELLOW + "/" + ORANGE + Placeholders.GENERIC_MAX + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_ANVIL_DESC   = LangKey.of("Command.Anvil.Desc", "Open portable anvil.");
    public static final LangKey COMMAND_ANVIL_USAGE  = LangKey.of("Command.Anvil.Usage", "[player] [-s]");
    public static final LangKey COMMAND_ANVIL_TARGET = LangKey.of("Command.Anvil.Target", LIGHT_YELLOW + "Opened portable anvil for " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_ANVIL_NOTIFY = LangKey.of("Command.Anvil.Notify", "<! type:\"action_bar\" !>" + LIGHT_YELLOW + "You opened portable anvil.");

    public static final LangKey COMMAND_BACK_DESC          = LangKey.of("Command.Back.Desc", "Return [player] to previous location.");
    public static final LangKey COMMAND_BACK_USAGE         = LangKey.of("Command.Back.Usage", "[player] [-s]");
    public static final LangKey COMMAND_BACK_ERROR_NOTHING = LangKey.of("Command.Back.Error.Nothing",
        "<! type:\"action_bar\" sound:\"" + Sound.ENTITY_VILLAGER_NO.name() + "\" !>" + RED + "You don't have back location.");
    public static final LangKey COMMAND_BACK_NOTIFY        = LangKey.of("Command.Back.Notify",
        "<! type:\"action_bar\" sound:\"" + Sound.ENTITY_ENDERMAN_TELEPORT.name() + "\" !>" + LIGHT_YELLOW + "You returned to your previous location.");
    public static final LangKey COMMAND_BACK_TARGET        = LangKey.of("Command.Back.Target", LIGHT_YELLOW + "Player " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + " teleported to his previous location.");

    @Deprecated public static final LangKey Command_Broadcast_Desc      = new LangKey("Command.Broadcast.Desc", "Broadcast a message.");
    @Deprecated public static final LangKey Command_Broadcast_Usage     = new LangKey("Command.Broadcast.Usage", "<message>");
    @Deprecated public static final LangKey Command_Broadcast_Format    = new LangKey("Command.Broadcast.Format", NO_PREFIX + "&6[&eBroadcast&6] &c%msg%");


    public static final LangKey Command_CText_Invalid          = new LangKey("Command.CText.Invalid", NO_PREFIX + "&7TXT file &c%file% &7not found!");

    public static final LangKey COMMAND_CONDENSE_DESC             = LangKey.of("Command.Condense.Desc", "Condense items into blocks.");
    public static final LangKey COMMAND_CONDENSE_USAGE            = LangKey.of("Command.Condense.Usage", "");
    public static final LangKey COMMAND_CONDENSE_ERROR_NOTHING    = LangKey.of("Command.Condense.Error.Nothing",
        "<! type:\"action_bar\" sound:\"" + Sound.ENTITY_VILLAGER_NO.name() + "\" !>" + RED + "Nothing to condense.");
    public static final LangKey COMMAND_CONDENSE_ERROR_NOT_ENOUGH = LangKey.of("Command.Condense.Error.NotEnough", LIGHT_YELLOW + "Not enough items to convert " + RED + CondenseCommand.PLACEHOLDER_SOURCE + LIGHT_YELLOW + " to " + RED + CondenseCommand.PLACEHOLDER_RESULT + LIGHT_YELLOW + ". Need at least " + RED + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_CONDENSE_DONE             = LangKey.of("Command.Condense.Done", LIGHT_YELLOW + "Converted " + ORANGE + "x" + CondenseCommand.PLACEHOLDER_TOTAL + " " + CondenseCommand.PLACEHOLDER_SOURCE + LIGHT_YELLOW + " to " + ORANGE + "x" + Placeholders.GENERIC_AMOUNT + " " + CondenseCommand.PLACEHOLDER_RESULT + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_DEATH_BACK_DESC        = LangKey.of("Command.DeathBack.Desc", "Return [player] to death location.");
    public static final LangKey COMMAND_DEATH_BACK_USAGE        = LangKey.of("Command.DeathBack.Usage", "[player] [-s]");
    public static final LangKey COMMAND_DEATH_BACK_ERROR_NOTHING = LangKey.of("Command.DeathBack.Error.Nothing",
        "<! type:\"action_bar\" sound:\"" + Sound.ENTITY_VILLAGER_NO.name() + "\" !>" + RED + "You don't have death location.");
    public static final LangKey COMMAND_DEATH_BACK_NOTIFY        = LangKey.of("Command.DeathBack.Notify",
        "<! type:\"action_bar\" sound:\"" + Sound.ENTITY_ENDERMAN_TELEPORT.name() + "\" !>" + LIGHT_YELLOW + "You returned to your death location.");
    public static final LangKey COMMAND_DEATH_BACK_TARGET        = LangKey.of("Command.DeathBack.Target", LIGHT_YELLOW + "Player " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + " teleported to his death location.");

    public static final LangKey COMMAND_DISPOSAL_USAGE = LangKey.of("Command.Disposal.Usage", "[player] [-s]");
    public static final LangKey COMMAND_DISPOSAL_DESC   = LangKey.of("Command.Disposal.Desc", "Open a disposal menu.");
    public static final LangKey COMMAND_DISPOSAL_TARGET = LangKey.of("Command.Disposal.Target", LIGHT_YELLOW + "Opened disposal for " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_DISPOSAL_NOTIFY = LangKey.of("Command.Disposal.Notify", "<! type:\"action_bar\" sound:\"" + Sound.ITEM_ARMOR_EQUIP_LEATHER.name() + "\" !>" + LIGHT_YELLOW + "You opened disposal.");

    public static final LangKey COMMAND_DIMENSION_USAGE = LangKey.of("Command.Dimension.Usage", "<world> [player] [-s]");
    public static final LangKey COMMAND_DIMENSION_DESC   = LangKey.of("Command.Dimension.Desc", "Teleport [player] to a world.");
    public static final LangKey COMMAND_DIMENSION_TARGET = LangKey.of("Command.Dimension.Target", LIGHT_YELLOW + "Teleported " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + " to the " + ORANGE + Placeholders.GENERIC_WORLD + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_DIMENSION_NOTIFY = LangKey.of("Command.Dimension.Notify", "<! type:\"action_bar\" sound:\"" + Sound.ENTITY_ENDERMAN_TELEPORT.name() + "\" !>" + LIGHT_YELLOW + "You travelled to the " + ORANGE + Placeholders.GENERIC_WORLD + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_ENCHANT_DESC  = LangKey.of("Command.Enchant.Desc", "(Dis)Enchant [player's] item in specified slot.");
    public static final LangKey COMMAND_ENCHANT_USAGE = LangKey.of("Command.Enchant.Usage", "<slot> <enchantment> <level> [player] [-s]");

    public static final LangKey COMMAND_ENCHANT_ENCHANTED_TARGET = LangKey.of("Command.Enchant.Enchanted.Target",
        "<! sound:\"" + Sound.BLOCK_ENCHANTMENT_TABLE_USE.name() + "\" !>" +
            LIGHT_YELLOW + "Enchanted " + ORANGE + Placeholders.GENERIC_ITEM + LIGHT_YELLOW + " with " +
            ORANGE + Placeholders.GENERIC_NAME + " " + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " in " +
            ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + "'s " + ORANGE + Placeholders.GENERIC_TYPE + LIGHT_YELLOW + " slot!");

    public static final LangKey COMMAND_ENCHANT_DISENCHANTED_TARGET = LangKey.of("Command.Enchant.Disenchanted.Target",
        "<! sound:\"" + Sound.BLOCK_GRINDSTONE_USE.name() + "\" !>" +
            LIGHT_YELLOW + "Enchantment " + ORANGE + Placeholders.GENERIC_NAME + LIGHT_YELLOW + " have been removed from " +
            ORANGE + Placeholders.GENERIC_ITEM + LIGHT_YELLOW + " in " +
            ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + "'s " + ORANGE + Placeholders.GENERIC_TYPE + LIGHT_YELLOW + " slot.");

    public static final LangKey COMMAND_ENCHANT_ENCHANTED_NOTIFY = LangKey.of("Command.Enchant.Enchanted.Notify",
        "<! sound:\"" + Sound.BLOCK_ENCHANTMENT_TABLE_USE.name() + "\" !>" +
            LIGHT_YELLOW + "Your " + ORANGE + Placeholders.GENERIC_ITEM + LIGHT_YELLOW + " have been enchanted with " +
            ORANGE + Placeholders.GENERIC_NAME + " " + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + "!");

    public static final LangKey COMMAND_ENCHANT_DISENCHANTED_NOTIFY = LangKey.of("Command.Enchant.Disenchanted.Notify",
        "<! sound:\"" + Sound.BLOCK_GRINDSTONE_USE.name() + "\" !>" +
            LIGHT_YELLOW + "Your " + ORANGE + Placeholders.GENERIC_ITEM + LIGHT_YELLOW + " have been disenchanted from " +
            ORANGE + Placeholders.GENERIC_NAME + " " + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + "!");

    public static final LangKey COMMAND_ENCHANTING_DESC   = LangKey.of("Command.Enchanting.Desc", "Open portable enchanting table.");
    public static final LangKey COMMAND_ENCHANTING_USAGE  = LangKey.of("Command.Enchanting.Usage", "[player] [-s]");
    public static final LangKey COMMAND_ENCHANTING_NOTIFY = LangKey.of("Command.Enchanting.Notify", ACTION_BAR + LIGHT_YELLOW + "You opened enchanting table.");
    public static final LangKey COMMAND_ENCHANTING_TARGET = LangKey.of("Command.Enchanting.Target", LIGHT_YELLOW + "Opened enchanting table for " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_ENCHANTMENT_SEED_DESC   = LangKey.of("Command.EnchantmentSeed.Desc", "Regenerate [player's] enchantment seed.");
    public static final LangKey COMMAND_ENCHANTMENT_SEED_USAGE  = LangKey.of("Command.EnchantmentSeed.Usage", "[player] [-s]");
    public static final LangKey COMMAND_ENCHANTMENT_SEED_NOTIFY = LangKey.of("Command.EnchantmentSeed.Notify", LIGHT_YELLOW + "Your enchantment seed has been updated. New enchantment offers are available.");
    public static final LangKey COMMAND_ENCHANTMENT_SEED_TARGET = LangKey.of("Command.EnchantmentSeed.Target", LIGHT_YELLOW + "Updated enchantment seed for " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_EXP_DESC          = LangKey.of("Command.Exp.Desc", "Manage [player's] exp points.");
    public static final LangKey COMMAND_EXP_USAGE         = LangKey.of("Command.Exp.Usage", "<action> <amount> [player] [-s] [-l]");
    public static final LangKey COMMAND_EXP_ADD_TARGET    = LangKey.of("Command.Exp.Give.Target", LIGHT_YELLOW + "Added " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " exp/levels to " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ". XP: " + ORANGE + Placeholders.GENERIC_TOTAL + LIGHT_YELLOW + ", Level: " + ORANGE + Placeholders.GENERIC_LEVEL + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_EXP_REMOVE_TARGET = LangKey.of("Command.Exp.Take.Target", LIGHT_YELLOW + "Removed " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " exp/levels from " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ". XP: " + ORANGE + Placeholders.GENERIC_TOTAL + LIGHT_YELLOW + ", Level: " + ORANGE + Placeholders.GENERIC_LEVEL + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_EXP_SET_TARGET    = LangKey.of("Command.Exp.Set.Target", LIGHT_YELLOW + "Set " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " exp/levels for " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ". XP: " + ORANGE + Placeholders.GENERIC_TOTAL + LIGHT_YELLOW + ", Level" + ORANGE + Placeholders.GENERIC_LEVEL + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_EXP_ADD_NOTIFY    = LangKey.of("Command.Exp.Give.Notify", LIGHT_YELLOW + "You got " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " exp/levels: " + ORANGE + Placeholders.GENERIC_TOTAL + LIGHT_YELLOW + " XP, " + ORANGE + Placeholders.GENERIC_LEVEL + LIGHT_YELLOW + "Level(s).");
    public static final LangKey COMMAND_EXP_REMOVE_NOTIFY = LangKey.of("Command.Exp.Take.Notify", LIGHT_YELLOW + "You lost " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " exp/levels: " + ORANGE + Placeholders.GENERIC_TOTAL + LIGHT_YELLOW + " XP, " + ORANGE + Placeholders.GENERIC_LEVEL + LIGHT_YELLOW + "Level(s).");
    public static final LangKey COMMAND_EXP_SET_NOTIFY    = LangKey.of("Command.Exp.Set.Notify", LIGHT_YELLOW + "Your exp/level set to " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + ": " + ORANGE + Placeholders.GENERIC_TOTAL + LIGHT_YELLOW + " XP, " + ORANGE + Placeholders.GENERIC_LEVEL + LIGHT_YELLOW + "Level(s).");

    public static final LangKey COMMAND_ENDERCHEST_DESC  = LangKey.of("Command.Enderchest.Desc", "Player Enderchest tools.");
    public static final LangKey COMMAND_ENDERCHEST_USAGE = LangKey.of("Command.Enderchest.Usage", "[help]");

    public static final LangKey COMMAND_ENDERCHEST_CLEAR_DESC        = LangKey.of("Command.Enderchest.Clear.Desc", "Clear [player's] ender chest.");
    public static final LangKey COMMAND_ENDERCHEST_CLEAR_USAGE       = LangKey.of("Command.Enderchest.Clear.Usage", "[player] [-s]");
    public static final LangKey COMMAND_ENDERCHEST_CLEAR_DONE_TARGET = LangKey.of("Command.Enderchest.Clear.Done.Target", "<! sound:\"" + Sound.BLOCK_FIRE_EXTINGUISH.name() + "\" !>" + LIGHT_YELLOW + "Cleared " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + "'s ender chest!");
    public static final LangKey COMMAND_ENDERCHEST_CLEAR_DONE_NOTIFY = LangKey.of("Command.Enderchest.Clear.Done.Notify", "<! type:\"action_bar\" sound:\"" + Sound.BLOCK_FIRE_EXTINGUISH.name() + "\" !>" + LIGHT_YELLOW + "Your ender chest has been cleared!");

    public static final LangKey COMMAND_ENDERCHEST_COPY_DESC          = LangKey.of("Command.Enderchest.Copy.Desc", "Copy player's ender chest.");
    public static final LangKey COMMAND_ENDERCHEST_COPY_USAGE         = LangKey.of("Command.Enderchest.Copy.Usage", "<from> [to]");
    public static final LangKey COMMAND_ENDERCHEST_COPY_DONE_EXECUTOR = LangKey.of("Command.Enderchest.Copy.Done.Executor", "<! sound:\"" + Sound.ITEM_ARMOR_EQUIP_LEATHER.name() + "\" !>" + LIGHT_YELLOW + "Copied " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + "'s ender chest.");
    public static final LangKey COMMAND_ENDERCHEST_COPY_DONE_OTHERS   = LangKey.of("Command.Enderchest.Copy.Done.Others", "<! sound:\"" + Sound.ITEM_ARMOR_EQUIP_LEATHER.name() + "\" !>" + LIGHT_YELLOW + "Copied " + ORANGE + Placeholders.GENERIC_SOURCE + LIGHT_YELLOW + "'s ender chest to " + ORANGE + Placeholders.GENERIC_TARGET + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_ENDERCHEST_FILL_DESC          = LangKey.of("Command.Enderchest.Fill.Desc", "Fill player's ender chest with specified item(s).");
    public static final LangKey COMMAND_ENDERCHEST_FILL_USAGE         = LangKey.of("Command.Enderchest.Fill.Usage", "<player> <item...>");
    public static final LangKey COMMAND_ENDERCHEST_FILL_DONE_EXECUTOR = LangKey.of("Command.Enderchest.Fill.Done.Executor", "<! sound:\"" + Sound.ENTITY_ITEM_PICKUP.name() + "\" !>" + LIGHT_YELLOW + "Filled " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + "'s ender chest with " + ORANGE + Placeholders.GENERIC_ITEM + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_ENDERCHEST_OPEN_DESC          = LangKey.of("Command.Enderchest.Open.Desc", "Open mutable [player's] ender chest.");
    public static final LangKey COMMAND_ENDERCHEST_OPEN_USAGE         = LangKey.of("Command.Enderchest.Open.Usage", "[player]");
    public static final LangKey COMMAND_ENDERCHEST_OPEN_DONE_EXECUTOR = LangKey.of("Command.Enderchest.Open.Done.Executor", "<! sound:\"" + Sound.BLOCK_CHEST_OPEN.name() + "\" !>" + LIGHT_YELLOW + "Opened " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + "'s ender chest.");

    public static final LangKey COMMAND_ENDERCHEST_REPAIR_DESC   = LangKey.of("Command.Enderchest.Repair.Desc", "Repair all items in [player's] ender chest.");
    public static final LangKey COMMAND_ENDERCHEST_REPAIR_USAGE  = LangKey.of("Command.Enderchest.Repair.Usage", "[player] [-s]");
    public static final LangKey COMMAND_ENDERCHEST_REPAIR_NOTIFY = LangKey.of("Command.Enderchest.Repair.Notify", "<! sound:\"" + Sound.BLOCK_ANVIL_USE.name() + "\" !>" + LIGHT_YELLOW + "All items in your ender chest has been repaired!");
    public static final LangKey COMMAND_ENDERCHEST_REPAIR_TARGET = LangKey.of("Command.Enderchest.Repair.Target", "<! sound:\"" + Sound.BLOCK_ANVIL_USE.name() + "\" !>" + LIGHT_YELLOW + "Repaired all items in " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + "'s ender chest!");

    public static final LangKey COMMAND_EXTINGUISH_DESC   = LangKey.of("Command.Extinguish.Desc", "Extinguish [a player].");
    public static final LangKey COMMAND_EXTINGUISH_USAGE  = LangKey.of("Command.Extinguish.Usage", "[player] [-s]");
    public static final LangKey COMMAND_EXTINGUISH_NOTIFY = LangKey.of("Command.Extinguish.Notify", "<! type:\"action_bar\" sound:\"" + Sound.BLOCK_FIRE_EXTINGUISH.name() + "\" !>" + LIGHT_YELLOW + "You have been extinguished.");
    public static final LangKey COMMAND_EXTINGUISH_TARGET = LangKey.of("Command.Extinguish.Target", "<! sound:\"" + Sound.BLOCK_FIRE_EXTINGUISH.name() + "\" !>" + LIGHT_YELLOW + "Player " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + " have been extinguished.");

    public static final LangKey COMMAND_EQUIP_DESC  = LangKey.of("Command.Equip.Desc", "Equip hand item to specified armor slot.");
    public static final LangKey COMMAND_EQUIP_USAGE = LangKey.of("Command.Equip.Usage", "<slot>");
    public static final LangKey COMMAND_EQUIP_DONE  = LangKey.of("Command.Equip.Done", "<! sound:\"" + Sound.ITEM_ARMOR_EQUIP_LEATHER.name() + "\" !>" + LIGHT_YELLOW + "Equipped " + ORANGE + Placeholders.GENERIC_ITEM + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_FEED_DESC   = LangKey.of("Command.Feed.Desc", "Fill [player's] food level.");
    public static final LangKey COMMAND_FEED_USAGE  = LangKey.of("Command.Feed.Usage", "[player] [-sat] [-s]");
    public static final LangKey COMMAND_FEED_NOTIFY = LangKey.of("Command.Feed.Done.Self", "<! sound:\"" + Sound.ENTITY_GENERIC_EAT.name() + "\" !>" + LIGHT_YELLOW + "You food level has been restored!");
    public static final LangKey COMMAND_FEED_TARGET = LangKey.of("Command.Feed.Done.Others", "<! sound:\"" + Sound.ENTITY_GENERIC_EAT.name() + "\" !>" + LIGHT_YELLOW + "Restored " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + "'s food level!");

    public static final LangKey COMMAND_FLY_DESC   = LangKey.of("Command.Fly.Desc", "Toggle [player's] fly.");
    public static final LangKey COMMAND_FLY_USAGE  = LangKey.of("Command.Fly.Usage", "[player] [-on] [-off] [-s]");
    public static final LangKey COMMAND_FLY_TARGET = LangKey.of("Command.Fly.Target", LIGHT_YELLOW + "Set flying for " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + " to " + ORANGE + Placeholders.GENERIC_STATE + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_FLY_NOTIFY = LangKey.of("Command.Fly.Notify", LIGHT_YELLOW + "Flying " + ORANGE + Placeholders.GENERIC_STATE + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_FLY_SPEED_DESC        = LangKey.of("Command.FlySpeed.Desc", "Change [player's] fly speed.");
    public static final LangKey COMMAND_FLY_SPEED_USAGE       = LangKey.of("Command.FlySpeed.Usage", "<speed> [player] [-s]");
    public static final LangKey COMMAND_FLY_SPEED_DONE_NOTIFY = LangKey.of("Command.FlySpeed.Done.Notify", LIGHT_YELLOW + "Your fly speed has been set to " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_FLY_SPEED_DONE_TARGET = LangKey.of("Command.FlySpeed.Done.Target", "Set fly speed to " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " for " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_FIRE_DESC          = LangKey.of("Command.Fire.Desc", "Manage [player's] fire ticks.");
    public static final LangKey COMMAND_FIRE_USAGE         = LangKey.of("Command.Fire.Usage", "<action> <amount> [player]");
    public static final LangKey COMMAND_FIRE_ADD_TARGET    = LangKey.of("Command.Fire.Give.Target", LIGHT_YELLOW + "Added " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " fire ticks to " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ". New fire ticks: " + ORANGE + Placeholders.GENERIC_TOTAL + GRAY + " (" + Placeholders.GENERIC_TIME + "s)");
    public static final LangKey COMMAND_FIRE_REMOVE_TARGET = LangKey.of("Command.Fire.Take.Target", LIGHT_YELLOW + "Removed " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " fire ticks from " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ". New fire ticks: " + ORANGE + Placeholders.GENERIC_TOTAL + GRAY + " (" + Placeholders.GENERIC_TIME + "s)");
    public static final LangKey COMMAND_FIRE_SET_TARGET    = LangKey.of("Command.Fire.Set.Target", LIGHT_YELLOW + "Set " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " fire ticks for " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ". New fire ticks: " + ORANGE + Placeholders.GENERIC_TOTAL + GRAY + " (" + Placeholders.GENERIC_TIME + "s)");

    public static final LangKey COMMAND_FOOD_DESC          = LangKey.of("Command.Food.Desc", "Manage [player's] food level.");
    public static final LangKey COMMAND_FOOD_USAGE         = LangKey.of("Command.Food.Usage", "<action> <amount> [player] [-s]");
    public static final LangKey COMMAND_FOOD_ADD_TARGET    = LangKey.of("Command.Food.Give.Target", LIGHT_YELLOW + "Added " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " food points to " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ". New food level: " + ORANGE + Placeholders.GENERIC_CURRENT + LIGHT_YELLOW + "/" + ORANGE + Placeholders.GENERIC_MAX + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_FOOD_REMOVE_TARGET = LangKey.of("Command.Food.Take.Target", LIGHT_YELLOW + "Removed " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " food points from " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ". New food level: " + ORANGE + Placeholders.GENERIC_CURRENT + LIGHT_YELLOW + "/" + ORANGE + Placeholders.GENERIC_MAX + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_FOOD_SET_TARGET    = LangKey.of("Command.Food.Set.Target", LIGHT_YELLOW + "Set " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " food points for " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ". New food level: " + ORANGE + Placeholders.GENERIC_CURRENT + LIGHT_YELLOW + "/" + ORANGE + Placeholders.GENERIC_MAX + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_FOOD_ADD_NOTIFY    = LangKey.of("Command.Food.Give.Notify", ACTION_BAR + LIGHT_YELLOW + "You got " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " food points: " + ORANGE + Placeholders.GENERIC_CURRENT + LIGHT_YELLOW + "/" + ORANGE + Placeholders.GENERIC_MAX + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_FOOD_REMOVE_NOTIFY = LangKey.of("Command.Food.Take.Notify", ACTION_BAR + LIGHT_YELLOW + "You lost " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " food points: " + ORANGE + Placeholders.GENERIC_CURRENT + LIGHT_YELLOW + "/" + ORANGE + Placeholders.GENERIC_MAX + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_FOOD_SET_NOTIFY    = LangKey.of("Command.Food.Set.Notify", ACTION_BAR + LIGHT_YELLOW + "Your food level has been set to " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + "/" + ORANGE + Placeholders.GENERIC_MAX + ".");

    public static final LangKey COMMAND_FOOD_GOD_DESC   = LangKey.of("Command.FoodGod.Desc", "Toggle [player's] Food God.");
    public static final LangKey COMMAND_FOOD_GOD_USAGE  = LangKey.of("Command.FoodGod.Usage", "[player] [-on] [-off] [-s]");
    public static final LangKey COMMAND_FOOD_GOD_TARGET = LangKey.of("Command.FoodGod.Target", LIGHT_YELLOW + "Set Food God for " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + " to " + ORANGE + Placeholders.GENERIC_STATE + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_FOOD_GOD_NOTIFY = LangKey.of("Command.FoodGod.Notify", LIGHT_YELLOW + "Food God " + ORANGE + Placeholders.GENERIC_STATE + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_GRINDSTONE_DESC   = LangKey.of("Command.Grindstone.Desc", "Open portable grindstone.");
    public static final LangKey COMMAND_GRINDSTONE_USAGE  = LangKey.of("Command.Grindstone.Usage", "[player] [-s]");
    public static final LangKey COMMAND_GRINDSTONE_NOTIFY = LangKey.of("Command.Grindstone.Notify", ACTION_BAR + LIGHT_YELLOW + "You opened grindstone.");
    public static final LangKey COMMAND_GRINDSTONE_TARGET = LangKey.of("Command.Grindstone.Target", LIGHT_YELLOW + "Opened grindstone for " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_GAME_MODE_DESC  = LangKey.of("Command.GameMode.Desc", "Change [player's] gamemode.");
    public static final LangKey COMMAND_GAME_MODE_USAGE  = LangKey.of("Command.GameMode.Usage", "<gamemode> [player] [-s]");
    public static final LangKey COMMAND_GAME_MODE_NOTIFY = LangKey.of("Command.GameMode.Notify", LIGHT_YELLOW + "Your game mode has been set to " + ORANGE + Placeholders.GENERIC_TYPE + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_GAME_MODE_TARGET = LangKey.of("Command.GameMode.Target", LIGHT_YELLOW + "Set " + ORANGE + Placeholders.GENERIC_TYPE + LIGHT_YELLOW + " game mode for " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_GOD_DESC              = LangKey.of("Command.God.Desc", "Toggle [player's] God Mode.");
    public static final LangKey COMMAND_GOD_USAGE             = LangKey.of("Command.God.Usage", "[player] [-on] [-off] [-s]");
    public static final LangKey COMMAND_GOD_DAMAGE_NOTIFY_OUT = LangKey.of("Command.God.Notify.DamageDisabled", ACTION_BAR + RED + "You can't inflict damage in God Mode!");
    public static final LangKey COMMAND_GOD_TOGGLE_NOTIFY     = LangKey.of("Command.God.Toggle.Notify", LIGHT_YELLOW + "Your God Mode has been " + ORANGE + Placeholders.GENERIC_STATE + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_GOD_TOGGLE_TARGET     = LangKey.of("Command.God.Toggle.Target", LIGHT_YELLOW + "Set God Mode " + ORANGE + Placeholders.GENERIC_STATE + LIGHT_YELLOW + " for " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_GOD_NOTIFY_BAD_WORLD  = LangKey.of("Command.God.Notify.BadWorld", RED + "Warning: " + LIGHT_YELLOW + "God Mode is disabled in this world!");

    public static final LangKey COMMAND_HEAL_DESC        = LangKey.of("Command.Heal.Desc", "Restore [player's] health.");
    public static final LangKey COMMAND_HEAL_USAGE       = LangKey.of("Command.Heal.Usage", "[player] [-eff] [-s]");
    public static final LangKey COMMAND_HEAL_NOTIFY = LangKey.of("Command.Heal.Notfiy", LIGHT_YELLOW + "You have been healed!");
    public static final LangKey COMMAND_HEAL_TARGET = LangKey.of("Command.Heal.Target", LIGHT_YELLOW + "Player " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + " has been healed.");

    public static final LangKey COMMAND_HEALTH_DESC          = LangKey.of("Command.Health.Desc", "Manage [player's] health.");
    public static final LangKey COMMAND_HEALTH_USAGE         = LangKey.of("Command.Health.Usage", "<action> <amount> [player] [-s]");
    public static final LangKey COMMAND_HEALTH_ADD_TARGET    = LangKey.of("Command.Health.Add.Target", LIGHT_YELLOW + "Added " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " health to " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ". New health: " + ORANGE + Placeholders.GENERIC_CURRENT + LIGHT_YELLOW + "/" + ORANGE + Placeholders.GENERIC_MAX + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_HEALTH_REMOVE_TARGET = LangKey.of("Command.Health.Remove.Target", LIGHT_YELLOW + "Removed " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " health from " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ". New health: " + ORANGE + Placeholders.GENERIC_CURRENT + LIGHT_YELLOW + "/" + ORANGE + Placeholders.GENERIC_MAX + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_HEALTH_SET_TARGET    = LangKey.of("Command.Health.Set.Target", LIGHT_YELLOW + "Set " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " health for " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ". New health: " + ORANGE + Placeholders.GENERIC_CURRENT + LIGHT_YELLOW + "/" + ORANGE + Placeholders.GENERIC_MAX + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_HEALTH_ADD_NOTIFY    = LangKey.of("Command.Health.Add.Notify", ACTION_BAR + LIGHT_YELLOW + "You got " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " health: " + ORANGE + Placeholders.GENERIC_CURRENT + LIGHT_YELLOW + "/" + ORANGE + Placeholders.GENERIC_MAX + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_HEALTH_REMOVE_NOTIFY = LangKey.of("Command.Health.Remove.Notify", ACTION_BAR + LIGHT_YELLOW + "You lost " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " health: " + ORANGE + Placeholders.GENERIC_CURRENT + LIGHT_YELLOW + "/" + ORANGE + Placeholders.GENERIC_MAX + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_HEALTH_SET_NOTIFY    = LangKey.of("Command.Health.Set.Notify", ACTION_BAR + LIGHT_YELLOW + "Your health has been set to " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + "/" + ORANGE + Placeholders.GENERIC_MAX + ".");

    // I - Commands --------------------------------------------------------
    public static final LangKey COMMAND_IGNORE_DESC                 = LangKey.of("Command.Ignore.Desc", "Manage your player blacklist.");
    public static final LangKey COMMAND_IGNORE_USAGE                = LangKey.of("Command.Ignore.Usage", "[help]");
    public static final LangKey COMMAND_IGNORE_ADD_DESC             = LangKey.of("Command.Ignore.Add.Desc", "Add player to your blacklist.");
    public static final LangKey COMMAND_IGNORE_ADD_USAGE            = LangKey.of("Command.Ignore.Add.Usage", "<player>");
    public static final LangKey COMMAND_IGNORE_ADD_DONE             = LangKey.of("Command.Ignore.Add.Done", LIGHT_YELLOW + "Player " + RED + Placeholders.PLAYER_NAME + LIGHT_YELLOW + " added to the blacklist. You can change settings or unblock him using " + RED + "/" + IgnoreCommand.NAME + " list");
    public static final LangKey COMMAND_IGNORE_ADD_ERROR_ALREADY_IN = LangKey.of("Command.Ignore.Add.Error.AlreadyIn", LIGHT_YELLOW + "Player is already blacklisted.");
    public static final LangKey COMMAND_IGNORE_REMOVE_DESC          = LangKey.of("Command.Ignore.Remove.Desc", "Remove player from blacklist.");
    public static final LangKey COMMAND_IGNORE_REMOVE_USAGE         = LangKey.of("Command.Ignore.Remove.Usage", "<player>");
    public static final LangKey COMMAND_IGNORE_REMOVE_DONE          = LangKey.of("Command.Ignore.Remove.Done", LIGHT_YELLOW + "Player " + ORANGE + Placeholders.PLAYER_NAME + LIGHT_YELLOW + " has ben removed from the blacklist.");
    public static final LangKey COMMAND_IGNORE_REMOVE_ERROR_NOT_IN  = LangKey.of("Command.Ignore.Remove.Error.NotIn", LIGHT_YELLOW + "Player is not blacklisted.");
    public static final LangKey COMMAND_IGNORE_LIST_DESC            = LangKey.of("Command.Ignore.List.Desc", "View your player blacklist.");
    public static final LangKey COMMAND_IGNORE_LIST_USAGE           = LangKey.of("Command.Ignore.List.Usage", "");

    public static final LangKey COMMAND_INVENTORY_DESC  = LangKey.of("Command.Inventory.Desc", "Player Inventory management tools.");
    public static final LangKey COMMAND_INVENTORY_USAGE = LangKey.of("Command.Inventory.Usage", "[help]");

    public static final LangKey COMMAND_INVENTORY_CLEAR_DESC        = LangKey.of("Command.Inventory.Clear.Desc", "Clear your own or other player's inventory.");
    public static final LangKey COMMAND_INVENTORY_CLEAR_USAGE       = LangKey.of("Command.Inventory.Clear.Usage", "[player] [-s]");
    public static final LangKey COMMAND_INVENTORY_CLEAR_DONE_TARGET = LangKey.of("Command.Inventory.Clear.Done.Target", "<! sound:\"" + Sound.BLOCK_FIRE_EXTINGUISH.name() + "\" !>" + LIGHT_YELLOW + "Cleared " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + "'s inventory!");
    public static final LangKey COMMAND_INVENTORY_CLEAR_DONE_NOTIFY = LangKey.of("Command.Inventory.Clear.Done.Notify", "<! sound:\"" + Sound.BLOCK_FIRE_EXTINGUISH.name() + "\" !>" + LIGHT_YELLOW + "Your inventory has been cleared!");

    public static final LangKey COMMAND_INVENTORY_COPY_DESC        = LangKey.of("Command.Inventory.Copy.Desc", "Copy player's inventory.");
    public static final LangKey COMMAND_INVENTORY_COPY_USAGE       = LangKey.of("Command.Inventory.Copy.Usage", "<from> [to]");
    public static final LangKey COMMAND_INVENTORY_COPY_DONE_NOTIFY = LangKey.of("Command.Inventory.Copy.Done.Notify", "<! sound:\"" + Sound.ITEM_ARMOR_EQUIP_LEATHER.name() + "\" !>" + LIGHT_YELLOW + "Copied " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + "'s inventory.");
    public static final LangKey COMMAND_INVENTORY_COPY_DONE_TARGET = LangKey.of("Command.Inventory.Copy.Done.Target", "<! sound:\"" + Sound.ITEM_ARMOR_EQUIP_LEATHER.name() + "\" !>" + LIGHT_YELLOW + "Copied " + ORANGE + Placeholders.GENERIC_SOURCE + LIGHT_YELLOW + "'s inventory to " + ORANGE + Placeholders.GENERIC_TARGET + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_INVENTORY_FILL_DESC  = LangKey.of("Command.Inventory.Fill.Desc", "Fill player's inventory with specified item(s).");
    public static final LangKey COMMAND_INVENTORY_FILL_USAGE = LangKey.of("Command.Inventory.Fill.Usage", "<player> <item...>");
    public static final LangKey COMMAND_INVENTORY_FILL_DONE  = LangKey.of("Command.Inventory.Fill.Done", "<! sound:\"" + Sound.ENTITY_ITEM_PICKUP.name() + "\" !>" + LIGHT_YELLOW + "Filled " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + "'s inventory with " + ORANGE + Placeholders.GENERIC_ITEM + LIGHT_YELLOW  +".");

    public static final LangKey COMMAND_INVENTORY_OPEN_DESC  = LangKey.of("Command.Inventory.Open.Desc", "Open mutable player's inventory.");
    public static final LangKey COMMAND_INVENTORY_OPEN_USAGE = LangKey.of("Command.Inventory.Open.Usage", "<player>");
    public static final LangKey COMMAND_INVENTORY_OPEN_DONE  = LangKey.of("Command.Inventory.Open.Done", "<! sound:\"" + Sound.BLOCK_CHEST_OPEN.name() + "\" !>" + LIGHT_YELLOW + "Opened " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + "'s inventory.");

    public static final LangKey COMMAND_INVENTORY_REPAIR_DESC   = LangKey.of("Command.Inventory.Repair.Desc", "Repair all items in [player's] inventory.");
    public static final LangKey COMMAND_INVENTORY_REPAIR_USAGE  = LangKey.of("Command.Inventory.Repair.Usage", "[player] [-s]");
    public static final LangKey COMMAND_INVENTORY_REPAIR_NOTIFY = LangKey.of("Command.Inventory.Repair.Notify", "<! sound:\"" + Sound.BLOCK_ANVIL_USE.name() + "\" !>" + LIGHT_YELLOW + "All items in your inventory has been repaired!");
    public static final LangKey COMMAND_INVENTORY_REPAIR_TARGET = LangKey.of("Command.Inventory.Repair.Target", "<! sound:\"" + Sound.BLOCK_ANVIL_USE.name() + "\" !>" + LIGHT_YELLOW + "Repaired all items in " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + "'s inventory!");

    public static final LangKey COMMAND_ITEM_DESC           = LangKey.of("Command.Item.Desc", "Item management tools.");
    public static final LangKey COMMAND_ITEM_USAGE          = LangKey.of("Command.Item.Usage", "[help]");
    public static final LangKey COMMAND_ITEM_ERROR_MATERIAL = LangKey.of("Command.Item.Error.Material", ORANGE + Placeholders.GENERIC_TYPE + RED + " is not a valid material!");
    public static final LangKey COMMAND_ITEM_ERROR_EMPTY_HAND = LangKey.of("Command.Item.Error.EmptyHand", RED + "You must hold an item!");

    public static final LangKey COMMAND_ITEM_AMOUNT_DESC  = LangKey.of("Command.Item.Amount.Desc", "Change item amount.");
    public static final LangKey COMMAND_ITEM_AMOUNT_USAGE = LangKey.of("Command.Item.Amount.Usage", "[amount]");
    public static final LangKey COMMAND_ITEM_AMOUNT_DONE  = LangKey.of("Command.Item.Amount.Done", "<! sound:\"" + Sound.ENTITY_ITEM_PICKUP.name() + "\" !>" + LIGHT_YELLOW + "Set " + ORANGE + Placeholders.GENERIC_ITEM + LIGHT_YELLOW + " amount to x" + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_ITEM_DAMAGE_DESC  = LangKey.of("Command.Item.Damage.Desc", "Change item's damage.");
    public static final LangKey COMMAND_ITEM_DAMAGE_USAGE = LangKey.of("Command.Item.Damage.Usage", "[amount]");
    public static final LangKey COMMAND_ITEM_DAMAGE_DONE  = LangKey.of("Command.Item.Damage.Done", "<! sound:\"" + Sound.BLOCK_ANVIL_USE.name() + "\" !>" + LIGHT_YELLOW + "Set " + ORANGE + Placeholders.GENERIC_ITEM + LIGHT_YELLOW + " damage to " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_ITEM_DAMAGE_ERROR_BAD_ITEM  = LangKey.of("Command.Item.Damage.Error.NotDamageable", "<! sound:\"" + Sound.ENTITY_VILLAGER_NO.name() + "\" !>" + ORANGE + Placeholders.GENERIC_ITEM + RED + " can not be damaged.");

    public static final LangKey COMMAND_ITEM_ENCHANT_DESC              = LangKey.of("Command.Item.Enchant.Desc", "(Dis)Enchant an item in hand.");
    public static final LangKey COMMAND_ITEM_ENCHANT_USAGE             = LangKey.of("Command.Item.Enchant.Usage", "<enchantment> <level>");
    public static final LangKey COMMAND_ITEM_ENCHANT_DONE_ENCHANTED    = LangKey.of("Command.Item.Enchant.Done.Enchanted", "<! sound:\"" + Sound.BLOCK_ENCHANTMENT_TABLE_USE.name() + "\" !>" + ORANGE + Placeholders.GENERIC_ITEM + LIGHT_YELLOW + " enchanted with " + ORANGE + Placeholders.GENERIC_NAME + " " + Placeholders.GENERIC_LEVEL + "!");
    public static final LangKey COMMAND_ITEM_ENCHANT_DONE_DISENCHANTED = LangKey.of("Command.Item.Enchant.Done.Disenchanted", "<! sound:\"" + Sound.BLOCK_ENCHANTMENT_TABLE_USE.name() + "\" !>" + LIGHT_YELLOW + "Enchantment " + ORANGE + Placeholders.GENERIC_NAME + LIGHT_YELLOW + " removed from " + ORANGE + Placeholders.GENERIC_ITEM + LIGHT_YELLOW + "!");

    public static final LangKey COMMAND_ITEM_FLAG_DESC         = LangKey.of("Command.Item.Flag.Desc", "Add or remove item flags.");
    public static final LangKey COMMAND_ITEM_FLAG_USAGE        = LangKey.of("Command.Item.Flag.Usage", "[help]");
    public static final LangKey COMMAND_ITEM_FLAG_ADD_DESC     = LangKey.of("Command.Item.Flag.Add.Desc", "Add specified flag to the item.");
    public static final LangKey COMMAND_ITEM_FLAG_ADD_USAGE    = LangKey.of("Command.Item.Flag.Add.Usage", "<flag>");
    public static final LangKey COMMAND_ITEM_FLAG_ADD_DONE     = LangKey.of("Command.Item.Flag.Add.Done", "<! sound:\"" + Sound.BLOCK_ANVIL_USE.name() + "\" !>" + LIGHT_YELLOW + "Added " + ORANGE + Placeholders.GENERIC_NAME + LIGHT_YELLOW + " flag to the item!");
    public static final LangKey COMMAND_ITEM_FLAG_REMOVE_DESC  = LangKey.of("Command.Item.Flag.Remove.Desc", "Remove specified flag from the item.");
    public static final LangKey COMMAND_ITEM_FLAG_REMOVE_USAGE = LangKey.of("Command.Item.Flag.Remove.Usage", "<flag>");
    public static final LangKey COMMAND_ITEM_FLAG_REMOVE_DONE  = LangKey.of("Command.Item.Flag.Remove.Done", "<! sound:\"" + Sound.BLOCK_ANVIL_USE.name() + "\" !>" + LIGHT_YELLOW + "Removed " + ORANGE + Placeholders.GENERIC_NAME + LIGHT_YELLOW + " flag from the item.");
    public static final LangKey COMMAND_ITEM_FLAG_CLEAR_DESC   = LangKey.of("Command.Item.Flag.Clear.Desc", "Clear item flags.");
    public static final LangKey COMMAND_ITEM_FLAG_CLEAR_DONE   = LangKey.of("Command.Item.Flag.Clear.Done", "<! sound:\"" + Sound.BLOCK_ANVIL_USE.name() + "\" !>" + LIGHT_YELLOW + "Item flags removed!");

    public static final LangKey COMMAND_ITEM_GET_DESC  = LangKey.of("Command.Item.Get.Desc", "Get specified item stack.");
    public static final LangKey COMMAND_ITEM_GET_USAGE = LangKey.of("Command.Item.Get.Usage", "<material> [amount] [-name <name>] [-lore <text>] [-ench <enchant:level] [-model <modelData>]");
    public static final LangKey COMMAND_ITEM_GET_DONE  = LangKey.of("Command.Item.Get.Done", "<! sound:\"" + Sound.ENTITY_ITEM_PICKUP.name() + "\" !>" + LIGHT_YELLOW + "You got " + ORANGE + "x" + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " of " + ORANGE + Placeholders.GENERIC_TYPE + LIGHT_YELLOW + "!");

    public static final LangKey COMMAND_ITEM_GIVE_DESC   = LangKey.of("Command.Item.Give.Desc", "Give specified item stack to a player.");
    public static final LangKey COMMAND_ITEM_GIVE_USAGE  = LangKey.of("Command.Item.Give.Usage", "<player> <material> [amount] [-name <name>] [-lore <text>] [-ench <enchant:level] [-model <modelData>]");
    public static final LangKey COMMAND_ITEM_GIVE_DONE   = LangKey.of("Command.Item.Give.Done", "<! sound:\"" + Sound.ENTITY_ITEM_PICKUP.name() + "\" !>" + LIGHT_YELLOW + "You gave " + ORANGE + "x" + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " of " + ORANGE + Placeholders.GENERIC_TYPE + LIGHT_YELLOW + " to " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + "!");
    public static final LangKey COMMAND_ITEM_GIVE_NOTIFY = LangKey.of("Command.Item.Give.Notify", "<! sound:\"" + Sound.ENTITY_ITEM_PICKUP.name() + "\" !>" + LIGHT_YELLOW + "You got " + ORANGE + "x" + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " of " + ORANGE + Placeholders.GENERIC_TYPE + LIGHT_YELLOW + "!");

    public static final LangKey COMMAND_ITEM_TAKE_DESC             = LangKey.of("Command.Item.Take.Desc", "Take specified item stack from a player.");
    public static final LangKey COMMAND_ITEM_TAKE_USAGE            = LangKey.of("Command.Item.Take.Usage", "<player> <material> [amount] [-name <name>] [-lore <text>] [-ench <enchant:level] [-model <modelData>]");
    public static final LangKey COMMAND_ITEM_TAKE_DONE             = LangKey.of("Command.Item.Take.Done", "<! sound:\"" + Sound.ENTITY_GLOW_ITEM_FRAME_REMOVE_ITEM.name() + "\" !>" + LIGHT_YELLOW + "You took " + ORANGE + "x" +  Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " of " + ORANGE + Placeholders.GENERIC_TYPE + LIGHT_YELLOW + " from " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + "!");
    public static final LangKey COMMAND_ITEM_TAKE_ERROR_NOT_ENOUGH = LangKey.of("Command.Item.Take.Error.NotEnough", "<! sound:\"" + Sound.ENTITY_VILLAGER_NO.name() + "\" !>" + RED + "Could not take item(s). Player don't have enough " + ORANGE + Placeholders.GENERIC_TYPE + RED + " (" + Placeholders.GENERIC_AMOUNT + "/" + Placeholders.GENERIC_TOTAL + ")!");
    public static final LangKey COMMAND_ITEM_TAKE_NOTIFY           = LangKey.of("Command.Item.Take.Notify", "<! sound:\"" + Sound.ENTITY_GLOW_ITEM_FRAME_REMOVE_ITEM.name() + "\" !>" + ORANGE + "x" + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " of " + ORANGE + Placeholders.GENERIC_TYPE + LIGHT_YELLOW + " has been taken from your inventory!");

    public static final LangKey COMMAND_ITEM_LORE_DESC         = LangKey.of("Command.Item.Lore.Desc", "Change item lore.");
    public static final LangKey COMMAND_ITEM_LORE_USAGE        = LangKey.of("Command.Item.Lore.Usage", "[help]");
    public static final LangKey COMMAND_ITEM_LORE_ADD_DESC     = LangKey.of("Command.Item.Lore.Add.Desc", "Add a new line to item lore.");
    public static final LangKey COMMAND_ITEM_LORE_ADD_USAGE    = LangKey.of("Command.Item.Lore.Add.Usage", "<text> [-pos <position>]");
    public static final LangKey COMMAND_ITEM_LORE_ADD_DONE     = LangKey.of("Command.Item.Lore.Add.Done", "<! sound:\"" + Sound.BLOCK_ANVIL_USE.name() + "\" !>" + LIGHT_YELLOW + "Added a new line to item lore!");
    public static final LangKey COMMAND_ITEM_LORE_REMOVE_DESC  = LangKey.of("Command.Item.Lore.Remove.Desc", "Remove a line from item lore.");
    public static final LangKey COMMAND_ITEM_LORE_REMOVE_USAGE = LangKey.of("Command.Item.Lore.Remove.Usage", "<position>");
    public static final LangKey COMMAND_ITEM_LORE_REMOVE_DONE  = LangKey.of("Command.Item.Lore.Remove.Done", "<! sound:\"" + Sound.BLOCK_ANVIL_USE.name() + "\" !>" + LIGHT_YELLOW + "Removed the line from item lore.");
    public static final LangKey COMMAND_ITEM_LORE_CLEAR_DESC   = LangKey.of("Command.Item.Lore.Clear.Desc", "Clear item lore.");
    public static final LangKey COMMAND_ITEM_LORE_CLEAR_DONE   = LangKey.of("Command.Item.Lore.Clear.Done", "<! sound:\"" + Sound.BLOCK_ANVIL_USE.name() + "\" !>" + LIGHT_YELLOW + "Item lore cleared!");

    public static final LangKey COMMAND_ITEM_MODEL_DESC  = LangKey.of("Command.Item.Model.Desc", "Change item model data.");
    public static final LangKey COMMAND_ITEM_MODEL_USAGE = LangKey.of("Command.Item.Model.Usage", "<model data>");
    public static final LangKey COMMAND_ITEM_MODEL_DONE  = LangKey.of("Command.Item.Model.Done", "<! sound:\"" + Sound.BLOCK_ANVIL_USE.name() + "\" !>" + LIGHT_YELLOW + "Set " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " as " + ORANGE + Placeholders.GENERIC_ITEM + LIGHT_YELLOW + " model data!");

    public static final LangKey COMMAND_ITEM_NAME_DESC         = LangKey.of("Command.Item.Name.Desc", "Change item display name.");
    public static final LangKey COMMAND_ITEM_NAME_USAGE        = LangKey.of("Command.Item.Name.Usage", "[name]");
    public static final LangKey COMMAND_ITEM_NAME_DONE_CHANGED = LangKey.of("Command.Item.Name.Done.Changed", "<! sound:\"" + Sound.BLOCK_ANVIL_USE.name() + "\" !>" + LIGHT_YELLOW + "Item renamed!");
    public static final LangKey COMMAND_ITEM_NAME_DONE_RESET   = LangKey.of("Command.Item.Name.Done.Reset", "<! sound:\"" + Sound.BLOCK_ANVIL_USE.name() + "\" !>" + LIGHT_YELLOW + "Item name restored to default.");

    public static final LangKey COMMAND_ITEM_POTION_ERROR_NOT_A_POTION   = LangKey.of("Command.Item.Potion.Error.NotAPotion", RED + "You must hold a potion!");
    public static final LangKey COMMAND_ITEM_POTION_ERROR_INVALID_EFFECT = LangKey.of("Command.Item.Potion.Error.InvalidEffect", RED + "Invalid effect!");
    public static final LangKey COMMAND_ITEM_POTION_ADD_DESC             = LangKey.of("Command.Item.Potion.Add.Desc", "Add effect to a potion.");
    public static final LangKey COMMAND_ITEM_POTION_ADD_USAGE            = LangKey.of("Command.Item.Potion.Add.Usage", "<effect> <amplifier> <duration>");
    public static final LangKey COMMAND_ITEM_POTION_ADD_DONE             = LangKey.of("Command.Item.Potion.Add.Done", LIGHT_YELLOW + "Potion effect added!");

    public static final LangKey COMMAND_ITEM_SPAWN_DESC  = LangKey.of("Command.Item.Spawn.Desc", "Spawn specified item stack.");
    public static final LangKey COMMAND_ITEM_SPAWN_USAGE = LangKey.of("Command.Item.Spawn.Usage", "<material> <amount> [world] [x] [y] [z] [-name <name>] [-lore <text>] [-ench <enchant:level] [-model <modelData>]");
    public static final LangKey COMMAND_ITEM_SPAWN_DONE  = LangKey.of("Command.Item.Spawn.Done", "<! sound:\"" + Sound.ENTITY_ITEM_PICKUP.name() + "\" !>" + LIGHT_YELLOW + "Created " + ORANGE + "x" + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " " + ORANGE + Placeholders.GENERIC_TYPE + LIGHT_YELLOW + " at " + ORANGE + Placeholders.LOCATION_X + ", " + Placeholders.LOCATION_Y + ", " + Placeholders.LOCATION_Z + LIGHT_YELLOW + " in " + ORANGE + Placeholders.LOCATION_WORLD + LIGHT_YELLOW + "!");

    public static final LangKey COMMAND_ITEM_UNBREAKABLE_DESC           = LangKey.of("Command.Item.Unbreakable.Desc", "Makes item (un)breakable.");
    public static final LangKey COMMAND_ITEM_UNBREAKABLE_USAGE          = LangKey.of("Command.Item.Unbreakable.Usage", "");
    public static final LangKey COMMAND_ITEM_UNBREAKABLE_DONE           = LangKey.of("Command.Item.Unbreakable.Done", "<! sound:\"" + Sound.BLOCK_ANVIL_USE.name() + "\" !>" + LIGHT_YELLOW + "Set " + ORANGE + Placeholders.GENERIC_ITEM + LIGHT_YELLOW + " Unbreakable: " + ORANGE + Placeholders.GENERIC_STATE + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_ITEM_UNBREAKABLE_ERROR_BAD_ITEM = LangKey.of("Command.Item.Unbreakable.Error.NotDamageable", "<! sound:\"" + Sound.ENTITY_VILLAGER_NO.name() + "\" !>" + ORANGE + Placeholders.GENERIC_ITEM + RED + " can not be (un)breakable.");

    public static final LangKey COMMAND_KILL_DESC   = LangKey.of("Command.Kill.Desc", "Kill [player].");
    public static final LangKey COMMAND_KILL_USAGE  = LangKey.of("Command.Kill.Usage", "[player] [-s]");
    public static final LangKey COMMAND_KILL_NOTIFY = LangKey.of("Command.Kill.Done.Self", LIGHT_YELLOW + "You got killed by " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + "!");
    public static final LangKey COMMAND_KILL_TARGET = LangKey.of("Command.Kill.Done.Others", LIGHT_YELLOW + "Killed " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + "!");

    public static final LangKey COMMAND_LOOM_DESC   = LangKey.of("Command.Loom.Desc", "Open portable loom.");
    public static final LangKey COMMAND_LOOM_USAGE  = LangKey.of("Command.Loom.Usage", "[player] [-s]");
    public static final LangKey COMMAND_LOOM_NOTIFY = LangKey.of("Command.Loom.Notify", ACTION_BAR + LIGHT_YELLOW + "You opened loom.");
    public static final LangKey COMMAND_LOOM_TARGET = LangKey.of("Command.Loom.Target", LIGHT_YELLOW + "Opened loom for " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_MOB_DESC  = LangKey.of("Command.Mob.Desc", "Mob management tools.");
    public static final LangKey COMMAND_MOB_USAGE = LangKey.of("Command.Mob.Usage", "[help]");

    public static final LangKey COMMAND_MOB_KILL_DESC       = LangKey.of("Command.Mob.Kill.Desc", "Kill specified mob types.");
    public static final LangKey COMMAND_MOB_KILL_USAGE      = LangKey.of("Command.Mob.Kill.Usage", "<type> [-r <radius>] [-w <world>] [-lim <limit>]");
    public static final LangKey COMMAND_MOB_KILL_ERROR_TYPE = LangKey.of("Command.Mob.Kill.Error.Type", RED + "Entity " + ORANGE + Placeholders.GENERIC_TYPE + RED + " can not be killed!");
    public static final LangKey COMMAND_MOB_KILL_DONE       = LangKey.of("Command.Mob.Kill.Done",
        "<! prefix:\"false\" sound:\"" + Sound.ENTITY_ZOMBIE_DEATH.name() + "\" !>" +
            LIGHT_YELLOW + "Killed " + ORANGE + "x" + GENERIC_AMOUNT + " " + GENERIC_TYPE + LIGHT_YELLOW + " in " + ORANGE + GENERIC_WORLD + LIGHT_YELLOW + " within " + ORANGE + GENERIC_RADIUS + LIGHT_YELLOW + " blocks."
    );

    public static final LangKey COMMAND_MOB_CLEAR_DESC       = LangKey.of("Command.Mob.Clear.Desc", "Kill all living entities.");
    public static final LangKey COMMAND_MOB_CLEAR_USAGE      = LangKey.of("Command.Mob.Clear.Usage", "[radius] [-w <world>]");
    public static final LangKey COMMAND_MOB_CLEAR_DONE       = LangKey.of("Command.Mob.Clear.Done",
        "<! sound:\"" + Sound.ENTITY_ZOMBIE_DEATH.name() + "\" !>" +
            LIGHT_YELLOW + "Killed " + ORANGE + GENERIC_AMOUNT + LIGHT_YELLOW + " mobs in " + ORANGE + GENERIC_WORLD + LIGHT_YELLOW + " within " + ORANGE + GENERIC_RADIUS + LIGHT_YELLOW + " blocks."
    );

    public static final LangKey COMMAND_MOB_SPAWN_DESC       = LangKey.of("Command.Mob.Spawn.Desc", "Spawn specified mob.");
    public static final LangKey COMMAND_MOB_SPAWN_USAGE      = LangKey.of("Command.Mob.Spawn.Usage", "<type> [amount] [-name <name>]");
    public static final LangKey COMMAND_MOB_SPAWN_ERROR_TYPE = LangKey.of("Command.Mob.Spawn.Error.Type", RED + "This Entity Type is invalid or can not be spawned!");
    public static final LangKey COMMAND_MOB_SPAWN_DONE       = LangKey.of("Command.Mob.Spawn.Done", LIGHT_YELLOW + "Created " + ORANGE + "x" + Placeholders.GENERIC_AMOUNT + " " + Placeholders.GENERIC_TYPE + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_NEAR_DESC       = LangKey.of("Command.Near.Desc", "Show nearest players.");
    public static final LangKey COMMAND_NEAR_USAGE         = LangKey.of("Command.Near.Usage", "");
    public static final LangKey COMMAND_NEAR_ERROR_NOTHING = LangKey.of("Command.Near.Error.Nothing", LIGHT_YELLOW + "There are no players in radius of " + ORANGE + Placeholders.GENERIC_RADIUS + LIGHT_YELLOW + " blocks.");

    // N - Commands --------------------------------------------------------
    public static final LangKey COMMAND_NICK_DESC                   = LangKey.of("Command.Nick.Desc", "Custom nick management.");
    public static final LangKey COMMAND_NICK_USAGE                  = LangKey.of("Command.Nick.Usage", "[help]");
    public static final LangKey COMMAND_NICK_CLEAR_DESC             = LangKey.of("Command.Nick.Clear.Desc", "Remove [player's] custom nick.");
    public static final LangKey COMMAND_NICK_CLEAR_USAGE            = LangKey.of("Command.Nick.Clear.Usage", "[player] [-s]");
    public static final LangKey COMMAND_NICK_CLEAR_TARGET           = LangKey.of("Command.Nick.Clear.Target", LIGHT_YELLOW + "Removed custom nick for " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_NICK_CLEAR_NOTIFY           = LangKey.of("Command.Nick.Clear.Notify", LIGHT_YELLOW + "You custom nick has been removed.");
    public static final LangKey COMMAND_NICK_SET_DESC               = LangKey.of("Command.Nick.Set.Desc", "Set custom nick for a player.");
    public static final LangKey COMMAND_NICK_SET_USAGE              = LangKey.of("Command.Nick.Set.Usage", "<player> <nick> [-s]");
    public static final LangKey COMMAND_NICK_SET_TARGET             = LangKey.of("Command.Nick.Set.Target", LIGHT_YELLOW + "Set " + ORANGE + Placeholders.GENERIC_NAME + LIGHT_YELLOW + " as custom nick for " + ORANGE + Placeholders.PLAYER_NAME + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_NICK_SET_NOTIFY             = LangKey.of("Command.Nick.Set.Notify", LIGHT_YELLOW + "Your custom nick has been set to " + ORANGE + Placeholders.GENERIC_NAME + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_NICK_CHANGE_DESC            = LangKey.of("Command.Nick.Change.Desc", "Change your custom nick.");
    public static final LangKey COMMAND_NICK_CHANGE_USAGE           = LangKey.of("Command.Nick.Change.Usage", "<nick>");
    public static final LangKey COMMAND_NICK_CHANGE_DONE            = LangKey.of("Command.Nick.Change.Done", LIGHT_YELLOW + "Set custom nick to " + ORANGE + Placeholders.GENERIC_NAME + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_NICK_CHANGE_ERROR_BAD_WORDS = LangKey.of("Command.Nick.Error.BadWords", RED + "Nick contains forbidden words.");
    public static final LangKey COMMAND_NICK_CHANGE_ERROR_REGEX     = LangKey.of("Command.Nick.Error.Regex", RED + "Nick contains disallowed characters.");
    public static final LangKey COMMAND_NICK_CHANGE_ERROR_TOO_LONG  = LangKey.of("Command.Nick.Error.TooLong", RED + "Nick can be no longer than " + ORANGE + Placeholders.GENERIC_AMOUNT + RED + " characters.");
    public static final LangKey COMMAND_NICK_CHANGE_ERROR_TOO_SHORT = LangKey.of("Command.Nick.Error.TooShort", RED + "Nick must contain at least " + ORANGE + Placeholders.GENERIC_AMOUNT + RED + " characters.");

    public static final LangKey COMMAND_NO_PHANTOM_DESC          = LangKey.of("Command.NoPhantom.Desc", "Toggle phantom spawns around [player].");
    public static final LangKey COMMAND_NO_PHANTOM_USAGE         = LangKey.of("Command.NoPhantom.Usage", "[player] [-on] [-off] [-s]");
    public static final LangKey COMMAND_NO_PHANTOM_TOGGLE_NOTIFY = LangKey.of("Command.NoPhantom.Toggle.Notify", LIGHT_YELLOW + "Anti-Phantom mode " + ORANGE + Placeholders.GENERIC_STATE + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_NO_PHANTOM_TOGGLE_TARGET = LangKey.of("Command.NoPhantom.Toggle.Target", LIGHT_YELLOW + "Set Anti-Phantom mode " + ORANGE + Placeholders.GENERIC_STATE + LIGHT_YELLOW + " for " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_NO_MOB_TARGET_DESC          = LangKey.of("Command.NoMobTarget.Desc", "Toggle mob targetting [for player].");
    public static final LangKey COMMAND_NO_MOB_TARGET_USAGE         = LangKey.of("Command.NoMobTarget.Usage", "[player] [-on] [-off] [-s]");
    public static final LangKey COMMAND_NO_MOB_TARGET_TOGGLE_NOTIFY = LangKey.of("Command.NoMobTarget.Toggle.Notify", LIGHT_YELLOW + "Anti-MobTarget mode " + ORANGE + Placeholders.GENERIC_STATE + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_NO_MOB_TARGET_TOGGLE_TARGET = LangKey.of("Command.NoMobTarget.Toggle.Target", LIGHT_YELLOW + "Set Anti-MobTarget mode " + ORANGE + Placeholders.GENERIC_STATE + LIGHT_YELLOW + " for " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_PLAYER_INFO_DESC  = LangKey.of("Command.PlayerInfo.Desc", "Show player info.");
    public static final LangKey COMMAND_PLAYER_INFO_USAGE = LangKey.of("Command.PlayerInfo.Usage", "<player>");
    public static final LangKey COMMAND_PLAYER_LIST_DESC  = LangKey.of("Command.PlayerList.Desc", "Show online player list.");

    public static final LangKey COMMAND_SKULL_DESC  = LangKey.of("Command.Skull.Desc", "Get [player's] head.");
    public static final LangKey COMMAND_SKULL_USAGE = LangKey.of("Command.Skull.Usage", "<name>");
    public static final LangKey COMMAND_SKULL_DONE  = LangKey.of("Command.Skull.Done", LIGHT_YELLOW + "You got " + ORANGE + Placeholders.PLAYER_NAME + LIGHT_YELLOW + "'s head.");

    // S - Commands --------------------------------------------------------
    public static final LangKey Command_Spawner_Desc = new LangKey("Command.Spawner.Desc", "Change spawner type.");
    public static final LangKey Command_Spawner_Usage           = new LangKey("Command.Spawner.Usage", "<type>");
    public static final LangKey Command_Spawner_Done            = new LangKey("Command.Spawner.Done", NO_PREFIX + "&7Spawner type changed to &e%type%&7.");
    public static final LangKey Command_Spawner_Error_Type      = new LangKey("Command.Spawner.Error.Type", NO_PREFIX + "&7This type can not be spawned.");
    public static final LangKey Command_Spawner_Error_Block     = new LangKey("Command.Spawner.Error.Block", NO_PREFIX + "&cYou must look at &espawner");

    public static final LangKey COMMAND_SPEED_DESC        = LangKey.of("Command.Speed.Desc", "Change [player's] walk speed.");
    public static final LangKey COMMAND_SPEED_USAGE       = LangKey.of("Command.Speed.Usage", "<speed> [player] [-s]");
    public static final LangKey COMMAND_SPEED_DONE_NOTIFY = LangKey.of("Command.Speed.Done.Notify", LIGHT_YELLOW + "Your walk speed has been set to " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_SPEED_DONE_TARGET = LangKey.of("Command.Speed.Done.Target", "Set walk speed to " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " for " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_SUDO_DESC         = LangKey.of("Command.Sudo.Desc", "Force player to execute a command or send a chat message.");
    public static final LangKey COMMAND_SUDO_USAGE        = LangKey.of("Command.Sudo.Usage", "<player> <command> [-c]");
    public static final LangKey COMMAND_SUDO_DONE_COMMAND = LangKey.of("Command.Sudo.Done.Command", LIGHT_YELLOW + "Forced " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + " to perform: " + ORANGE + "/" + Placeholders.GENERIC_COMMAND + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_SUDO_DONE_CHAT    = LangKey.of("Command.Sudo.Done.Chat", LIGHT_YELLOW + "Forced " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + " to write: " + ORANGE + Placeholders.GENERIC_COMMAND + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_SUICIDE_DESC = LangKey.of("Command.Suicide.Desc", "Commit suicide.");
    public static final LangKey COMMAND_SUICIDE_DONE = LangKey.of("Command.Suicide.Done", "<! prefix:\"false\" !>" + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + " commited suicide.");

    public static final LangKey COMMAND_TELEPORT_DESC                    = LangKey.of("Command.Teleport.Desc", "Teleportation tools.");
    public static final LangKey COMMAND_TELEPORT_USAGE                   = LangKey.of("Command.Teleport.Usage", "[help]");
    public static final LangKey COMMAND_TELEPORT_ERROR_REQUESTS_COOLDOWN = LangKey.of("Command.Teleport.Error.Requests.Cooldown", "<! sound:\"" + Sound.ENTITY_VILLAGER_NO.name() + "\" !>" + RED + "You can send teleport request again in " + ORANGE + Placeholders.GENERIC_TIME);
    public static final LangKey COMMAND_TELEPORT_ERROR_REQUESTS_DISABLED = LangKey.of("Command.Teleport.Error.Requests.Disabled", "<! sound:\"" + Sound.ENTITY_VILLAGER_NO.name() + "\" !>" + RED + "You can't send teleport requests to " + ORANGE + Placeholders.PLAYER_NAME + RED + ".");

    public static final LangKey COMMAND_TELEPORT_ACCEPT_DESC          = LangKey.of("Command.Teleport.Accept.Desc", "Accept incoming teleport request/invite.");
    public static final LangKey COMMAND_TELEPORT_ACCEPT_USAGE         = LangKey.of("Command.Teleport.Accept.Usage", "[player]");
    public static final LangKey COMMAND_TELEPORT_ACCEPT_NOTIFY_SENDER = LangKey.of("Command.Teleport.Accept.Notify.Sender", "<! sound:\"" + Sound.ENTITY_ENDERMAN_TELEPORT.name() + "\" !>" + LIGHT_YELLOW + "You accepted teleport request from " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + "!");
    public static final LangKey COMMAND_TELEPORT_ACCEPT_NOTIFY_TARGET = LangKey.of("Command.Teleport.Accept.Notify.Target", "<! sound:\"" + Sound.ENTITY_ENDERMAN_TELEPORT.name() + "\" !>" + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + " accepted your teleport request!");
    public static final LangKey COMMAND_TELEPORT_ACCEPT_ERROR_NOTHING = LangKey.of("Command.Teleport.Accept.Error.Nothing", "<! sound:\"" + Sound.ENTITY_VILLAGER_NO.name() + "\" !>" + LIGHT_YELLOW + "Nothing to accept (or request is not valid anymore).");

    public static final LangKey COMMAND_TELEPORT_DECLINE_DESC          = LangKey.of("Command.Teleport.Decline.Desc", "Decline incoming teleport request/invite.");
    public static final LangKey COMMAND_TELEPORT_DECLINE_USAGE         = LangKey.of("Command.Teleport.Decline.Usage", "[player]");
    public static final LangKey COMMAND_TELEPORT_DECLINE_NOTIFY_SENDER = LangKey.of("Command.Teleport.Decline.Notify.Sender", "<! sound:\"" + Sound.ENTITY_VILLAGER_NO.name() + "\" !>" + LIGHT_YELLOW + "You declined teleport request of " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_TELEPORT_DECLINE_NOTIFY_TARGET = LangKey.of("Command.Teleport.Decline.Notify.Target", "<! sound:\"" + Sound.ENTITY_VILLAGER_NO.name() + "\" !>" + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + " declines your teleport request.");
    public static final LangKey COMMAND_TELEPORT_DECLINE_ERROR_NOTHING = LangKey.of("Command.Teleport.Decline.Error.Nothing", "<! sound:\"" + Sound.ENTITY_VILLAGER_NO.name() + "\" !>" + LIGHT_YELLOW + "Nothing to decline (or request is not valid anymore).");

    public static final LangKey COMMAND_TELEPORT_INVITE_DESC          = LangKey.of("Command.Teleport.Invite.Desc", "Prompt player to teleport to you.");
    public static final LangKey COMMAND_TELEPORT_INVITE_USAGE         = LangKey.of("Command.Teleport.Invite.Usage", "<player>");
    public static final LangKey COMMAND_TELEPORT_INVITE_NOTIFY_SENDER = LangKey.of("Command.Teleport.Invite.Notify.Sender", "<! sound:\"" + Sound.ENTITY_ENDER_PEARL_THROW.name() + "\" !>" + LIGHT_YELLOW + "Sent teleport request to " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_TELEPORT_INVITE_NOTIFY_TARGET = LangKey.of("Command.Teleport.Invite.Notify.Target",
        "<! prefix:\"false\" sound:\"" + Sound.ENTITY_ENDER_PEARL_THROW.name() + "\" !>" +
            "\n" + LIGHT_YELLOW +
            "\n" + LIGHT_YELLOW + "&lTeleport Invite:" +
            "\n" + LIGHT_YELLOW +
            "\n" + LIGHT_YELLOW + "Player " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + " invites you to teleport to him." +
            "\n" + LIGHT_YELLOW + "Type " + GREEN + "/" + TeleportCommand.NAME + " " + TeleportAcceptCommand.NAME + " " + Placeholders.PLAYER_NAME + LIGHT_YELLOW + " to accept and teleport." +
            "\n" + LIGHT_YELLOW + "or " + RED + "/" + TeleportCommand.NAME + " " + TeleportDeclineCommand.NAME + " " + Placeholders.PLAYER_NAME + LIGHT_YELLOW + " to decline." +
            "\n" + LIGHT_YELLOW +
            "\n" + LIGHT_YELLOW + "             <? show_text:\"" + GRAY + "You will be teleported to " + GREEN + Placeholders.PLAYER_DISPLAY_NAME + GRAY + ".\" run_command:\"/" + TeleportCommand.NAME + " " + TeleportAcceptCommand.NAME + " " + Placeholders.PLAYER_NAME + "\"?>" + GREEN + "&l[Accept]</> " + LIGHT_YELLOW + "        <? show_text:\"" + GRAY + "You won't be teleported to " + RED + Placeholders.PLAYER_DISPLAY_NAME + GRAY + ".\" run_command:\"/" + TeleportCommand.NAME + " " + TeleportDeclineCommand.NAME + " " + Placeholders.PLAYER_NAME + "\"?>" + RED + "&l[Decline]</>" +
            "\n" + LIGHT_YELLOW +
            "\n" + LIGHT_YELLOW);

    public static final LangKey COMMAND_TELEPORT_LOCATION_DESC        = LangKey.of("Command.Teleport.Location.Desc", "Teleport to specified coordinates.");
    public static final LangKey COMMAND_TELEPORT_LOCATION_USAGE       = LangKey.of("Command.Teleport.Location.Usage", "<x> <y> <z> [player] [-w <world>] [-s]");
    public static final LangKey COMMAND_TELEPORT_LOCATION_DONE_TARGET = LangKey.of("Command.Teleport.Location.Done.Target", "<! sound:\"" + Sound.ENTITY_ENDERMAN_TELEPORT.name() + "\" !>" + LIGHT_YELLOW + "Player " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + " teleported to " + ORANGE + Placeholders.LOCATION_X + ", " + Placeholders.LOCATION_Y + ", " + Placeholders.LOCATION_Z + LIGHT_YELLOW + " in " + ORANGE + Placeholders.LOCATION_WORLD + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_TELEPORT_LOCATION_DONE_NOTIFY = LangKey.of("Command.Teleport.Location.Done.Notify", "<! sound:\"" + Sound.ENTITY_ENDERMAN_TELEPORT.name() + "\" !>" + LIGHT_YELLOW + "You were teleported to " + ORANGE + Placeholders.LOCATION_X + ", " + Placeholders.LOCATION_Y + ", " + Placeholders.LOCATION_Z + LIGHT_YELLOW + " in " + ORANGE + Placeholders.LOCATION_WORLD + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_TELEPORT_REQUEST_DESC          = LangKey.of("Command.Teleport.Request.Desc", "Send teleport request to a player.");
    public static final LangKey COMMAND_TELEPORT_REQUEST_USAGE         = LangKey.of("Command.Teleport.Request.Usage", "<player>");
    public static final LangKey COMMAND_TELEPORT_REQUEST_NOTIFY_SENDER = LangKey.of("Command.Teleport.Request.Notify.Sender", "<! sound:\"" + Sound.ENTITY_ENDER_PEARL_THROW.name() + "\" !>" + LIGHT_YELLOW + "Sent teleport request to " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_TELEPORT_REQUEST_NOTIFY_TARGET = LangKey.of("Command.Teleport.Request.Notify.Target",
        "<! prefix:\"false\" sound:\"" + Sound.ENTITY_ENDER_PEARL_THROW.name() + "\" !>" +
            "\n" + LIGHT_YELLOW +
            "\n" + LIGHT_YELLOW + "&lTeleport Request:" +
            "\n" + LIGHT_YELLOW +
            "\n" + LIGHT_YELLOW + "Player " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + " wants to be teleported to you." +
            "\n" + LIGHT_YELLOW + "Type " + GREEN + "/" + TeleportCommand.NAME + " " + TeleportAcceptCommand.NAME + " " + Placeholders.PLAYER_NAME + LIGHT_YELLOW + " to accept and teleport him." +
            "\n" + LIGHT_YELLOW + "or " + RED + "/" + TeleportCommand.NAME + " " + TeleportDeclineCommand.NAME + " " + Placeholders.PLAYER_NAME + LIGHT_YELLOW + " to decline." +
            "\n" + LIGHT_YELLOW +
            "\n" + LIGHT_YELLOW + "             <? show_text:\"" + GREEN + Placeholders.PLAYER_DISPLAY_NAME + GRAY + " will be teleported to you.\" run_command:\"/" + TeleportCommand.NAME + " " + TeleportAcceptCommand.NAME + " " + Placeholders.PLAYER_NAME + "\"?>" + GREEN + "&l[Accept]</> " + LIGHT_YELLOW + "        <? show_text:\"" + RED + Placeholders.PLAYER_DISPLAY_NAME + GRAY + " won't be teleported to you.\" run_command:\"/" + TeleportCommand.NAME + " " + TeleportDeclineCommand.NAME + " " + Placeholders.PLAYER_NAME + "\"?>" + RED + "&l[Decline]</>" +
            "\n" + LIGHT_YELLOW +
            "\n" + LIGHT_YELLOW);

    public static final LangKey COMMAND_TELEPORT_TOGGLE_DESC  = LangKey.of("Command.Teleport.Toggle.Desc", "Toggle teleport requests/invites.");
    public static final LangKey COMMAND_TELEPORT_TOGGLE_USAGE = LangKey.of("Command.Teleport.Toggle.Usage", "[player] [-on] [-off] [-s]");
    public static final LangKey COMMAND_TELEPORT_TOGGLE_TARGET = LangKey.of("Command.Teleport.Toggle.Target", LIGHT_YELLOW + "Teleport requests " + ORANGE + Placeholders.GENERIC_STATE + LIGHT_YELLOW + " for " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_TELEPORT_TOGGLE_NOTIFY = LangKey.of("Command.Teleport.Toggle.Notify", LIGHT_YELLOW + "Teleport requests " + ORANGE + Placeholders.GENERIC_STATE + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_TELEPORT_SUMMON_DESC   = LangKey.of("Command.Teleport.Summon.Desc", "Summon player at your location.");
    public static final LangKey COMMAND_TELEPORT_SUMMON_USAGE  = LangKey.of("Command.Teleport.Summon.Usage", "<player> [-s]");
    public static final LangKey COMMAND_TELEPORT_SUMMON_TARGET = LangKey.of("Command.Teleport.Summon.Target", "<! sound:\"" + Sound.ENTITY_ENDERMAN_TELEPORT.name() + "\" !>" + LIGHT_YELLOW + "Summoned " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + " to your location.");
    public static final LangKey COMMAND_TELEPORT_SUMMON_NOTIFY = LangKey.of("Command.Teleport.Summon.Notify", "<! sound:\"" + Sound.ENTITY_ENDERMAN_TELEPORT.name() + "\" !>" + LIGHT_YELLOW + "You were summoned by " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_TELEPORT_TO_DESC          = LangKey.of("Command.Teleport.To.Desc", "Teleport to specified player.");
    public static final LangKey COMMAND_TELEPORT_TO_USAGE       = LangKey.of("Command.Teleport.To.Usage", "<player>");
    public static final LangKey COMMAND_TELEPORT_TO_DONE        = LangKey.of("Command.Teleport.To.Done", "<! sound:\"" + Sound.ENTITY_ENDERMAN_TELEPORT.name() + "\" !>" + LIGHT_YELLOW + "Teleporting to " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_TELEPORT_SEND_DESC   = LangKey.of("Command.Teleport.Send.Desc", "Teleport one player to another.");
    public static final LangKey COMMAND_TELEPORT_SEND_USAGE  = LangKey.of("Command.Teleport.Send.Usage", "<player> <target> [-s]");
    public static final LangKey COMMAND_TELEPORT_SEND_TARGET = LangKey.of("Command.Teleport.Send.Target", "<! sound:\"" + Sound.ENTITY_ENDERMAN_TELEPORT.name() + "\" !>" + LIGHT_YELLOW + "Player " + ORANGE + Placeholders.GENERIC_SOURCE + LIGHT_YELLOW + " teleported to " + ORANGE + Placeholders.GENERIC_TARGET + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_TELEPORT_SEND_NOTIFY = LangKey.of("Command.Teleport.Send.Notify", "<! sound:\"" + Sound.ENTITY_ENDERMAN_TELEPORT.name() + "\" !>" + LIGHT_YELLOW + "You have been teleported to " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_TELEPORT_TOP_DESC   = LangKey.of("Command.Teleport.Top.Desc", "Teleport [player] on the highest block above.");
    public static final LangKey COMMAND_TELEPORT_TOP_USAGE  = LangKey.of("Command.Teleport.Top.Usage", "[player] [-s]");
    public static final LangKey COMMAND_TELEPORT_TOP_TARGET = LangKey.of("Command.Teleport.Top.Target", "<! sound:\"" + Sound.ENTITY_ENDERMAN_TELEPORT.name() + "\" !>" + LIGHT_YELLOW + "Player " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + " teleported on the highest block.");
    public static final LangKey COMMAND_TELEPORT_TOP_NOTIFY = LangKey.of("Command.Teleport.Top.Notify", "<! sound:\"" + Sound.ENTITY_ENDERMAN_TELEPORT.name() + "\" !>" + LIGHT_YELLOW + "You were teleported on the highest block.");

    public static final LangKey COMMAND_TIME_DESC  = LangKey.of("Command.Time.Desc", "Time management tools.");
    public static final LangKey COMMAND_TIME_USAGE = LangKey.of("Command.Time.Usage", "[help]");

    public static final LangKey COMMAND_TIME_SHOW_DESC  = LangKey.of("Command.Time.Show.Desc", "Display personal and current world time.");
    public static final LangKey COMMAND_TIME_SHOW_USAGE = LangKey.of("Command.Time.Show.Usage", "[-w <world>]");
    public static final LangKey COMMAND_TIME_SHOW_INFO  = LangKey.of("Command.Time.Show.Info",
        "<! prefix:\"false\" !>" +
            "\n" + LIGHT_YELLOW +
            "\n" + LIGHT_YELLOW + "&lTime Info:" +
            "\n" + LIGHT_YELLOW +
            "\n" + LIGHT_YELLOW + "▪ " + ORANGE + Placeholders.GENERIC_WORLD + " Time: " + LIGHT_YELLOW +  TimeShowCommand.PLACEHOLDER_WORLD_TIME + GRAY + " (" + TimeShowCommand.PLACEHOLDER_WORLD_TICKS + " ticks)" +
            "\n" + LIGHT_YELLOW + "▪ " + ORANGE + "Personal Time: " + LIGHT_YELLOW + TimeShowCommand.PLACEHOLDER_PLAYER_TIME + GRAY + " (" + TimeShowCommand.PLACEHOLDER_PLAYER_TICKS + " ticks)" +
            "\n" + LIGHT_YELLOW + "▪ " + ORANGE + "Server Time: " + LIGHT_YELLOW + TimeShowCommand.PLACEHOLDER_SERVER_TIME +
            "\n" + LIGHT_YELLOW);

    public static final LangKey COMMAND_TIME_SET_DESC  = LangKey.of("Command.Time.Set.Desc", "Set world's time.");
    public static final LangKey COMMAND_TIME_SET_USAGE = LangKey.of("Command.Time.Set.Usage", "<ticks> [-w <world>]");
    public static final LangKey COMMAND_TIME_SET_DONE  = LangKey.of("Command.Time.Set.Done", LIGHT_YELLOW + "Set time to " + ORANGE + Placeholders.GENERIC_TIME + GRAY + " (" + Placeholders.GENERIC_TOTAL + " ticks)" + LIGHT_YELLOW + " in world " + ORANGE + Placeholders.GENERIC_WORLD + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_TIME_PERSONAL_DESC  = LangKey.of("Command.Time.Personal.Desc", "Set [player's] personal time.");
    public static final LangKey COMMAND_TIME_PERSONAL_USAGE = LangKey.of("Command.Time.Personal.Usage", "<ticks> [player] [-r] [-s]");
    public static final LangKey COMMAND_TIME_PERSONAL_TARGET  = LangKey.of("Command.Time.Personal.Target", LIGHT_YELLOW + "Set time to " + ORANGE + Placeholders.GENERIC_TIME + GRAY + " (" + Placeholders.GENERIC_TOTAL + " ticks)" + LIGHT_YELLOW + " for " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_TIME_PERSONAL_NOTIFY  = LangKey.of("Command.Time.Personal.Notify", LIGHT_YELLOW + "Your personal time has been set to " + ORANGE + Placeholders.GENERIC_TIME + GRAY + " (" + Placeholders.GENERIC_TOTAL + " ticks)" + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_SMITE_DESC   = LangKey.of("Command.Smite.Desc", "Smite [player] with lightning.");
    public static final LangKey COMMAND_SMITE_USAGE  = LangKey.of("Command.Smite.Usage", "[player] [-s]");
    public static final LangKey COMMAND_SMITE_TARGET = LangKey.of("Command.Smite.Target", LIGHT_YELLOW + "Smited " + ORANGE + Placeholders.PLAYER_NAME + LIGHT_YELLOW + "!");
    public static final LangKey COMMAND_SMITE_NOTIFY = LangKey.of("Command.Smite.Notify", LIGHT_YELLOW + "You have been smited!");

    public static final LangKey COMMAND_VANISH_DESC   = LangKey.of("Command.Vanish.Desc", "Toggle [player's] vanish mode.");
    public static final LangKey COMMAND_VANISH_USAGE  = LangKey.of("Command.Vanish.Usage", "[player] [-on] [-off] [-s]");
    public static final LangKey COMMAND_VANISH_NOTIFY = LangKey.of("Command.Vanish.Notify", LIGHT_YELLOW + "Vanish " + ORANGE + Placeholders.GENERIC_STATE + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_VANISH_TARGET = LangKey.of("Command.Vanish.Target", LIGHT_YELLOW + "Set vanish " + ORANGE + Placeholders.GENERIC_STATE + LIGHT_YELLOW + " for " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_WEATHER_DESC  = LangKey.of("Command.Weather.Desc", "Change world's weather.");
    public static final LangKey COMMAND_WEATHER_USAGE = LangKey.of("Command.Weather.Usage", "<type> [-w <world>]");
    public static final LangKey COMMAND_WEATHER_SET   = LangKey.of("Command.Weather.Set", LIGHT_YELLOW + "Set " + ORANGE + Placeholders.GENERIC_TYPE + LIGHT_YELLOW + " weather in " + ORANGE + Placeholders.GENERIC_WORLD + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_WORKBENCH_DESC   = LangKey.of("Command.Workbench.Desc", "Open portable workbench.");
    public static final LangKey COMMAND_WORKBENCH_USAGE  = LangKey.of("Command.Workbench.Usage", "[player] [-s]");
    public static final LangKey COMMAND_WORKBENCH_TARGET = LangKey.of("Command.Workbench.Target", LIGHT_YELLOW + "Opened portable workbench for " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_WORKBENCH_NOTIFY = LangKey.of("Command.Workbench.Notify", ACTION_BAR + LIGHT_YELLOW + "You opened portable workbench.");

    // X - Commands --------------------------------------------------------

    // Y - Commands --------------------------------------------------------

    // Z - Commands --------------------------------------------------------

    public static final LangKey OTHER_ENABLED  = LangKey.of("Other.Enabled", GREEN + "Enabled");
    public static final LangKey OTHER_DISABLED = LangKey.of("Other.Disabled", RED + "Disabled");

    public static final LangKey             ERROR_ENCHANTMENT_INVALID = LangKey.of("Error.Enchantment.Invalid", RED + "Invalid enchantment!");
    public static final LangKey ERROR_PLAYER_NO_ITEM = LangKey.of("Error.Player.NoItem", RED + "Player don't have an item!");
    public static final LangKey ERROR_MATERIAL_INVALID = LangKey.of("Error.Material.Invalid", "&cNo valid material(s) provided!");

    @NotNull
    public static LangKey getEnabled(boolean value) {
        return (value ? OTHER_ENABLED : OTHER_DISABLED);
    }

    @NotNull
    public static String getEnable(boolean value) {
        return SunLightAPI.PLUGIN.getMessage(value ? OTHER_ENABLED : OTHER_DISABLED).getLocalized();
    }
}
