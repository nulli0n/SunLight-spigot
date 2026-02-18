package su.nightexpress.sunlight.module.items;

import org.bukkit.Sound;
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.*;

public class ItemsLang implements LangContainer {

    public static final TextLocale COMMAND_ITEM_ROOT_DESC        = LangEntry.builder("Command.Item.Root.Desc").text("Item commands.");
    public static final TextLocale COMMAND_ITEM_AMOUNT_DESC      = LangEntry.builder("Command.Item.Amount.Desc").text("Change stack size of item in hand.");
    public static final TextLocale COMMAND_ITEM_DAMAGE_DESC      = LangEntry.builder("Command.Item.Damage.Desc").text("Change damage value of item in hand.");
    public static final TextLocale COMMAND_ITEM_ENCHANT_DESC     = LangEntry.builder("Command.Item.Enchant.Desc").text("Enchant item in hand with a specific enchantment.");
    public static final TextLocale COMMAND_ITEM_DISENCHANT_DESC  = LangEntry.builder("Command.Item.Disenchant.Desc").text("Remove enchantment(s) from item in hand.");
    public static final TextLocale COMMAND_ITEM_GET_DESC         = LangEntry.builder("Command.Item.Get.Desc").text("Get specific item.");
    public static final TextLocale COMMAND_ITEM_GIVE_DESC        = LangEntry.builder("Command.Item.Give.Desc").text("Give specific item to a player.");
    public static final TextLocale COMMAND_ITEM_LORE_ROOT_DESC   = LangEntry.builder("Command.Item.Lore.Root.Desc").text("Item lore commands.");
    public static final TextLocale COMMAND_ITEM_LORE_ADD_DESC    = LangEntry.builder("Command.Item.Lore.Add.Desc").text("Add a lore line for item in hand.");
    public static final TextLocale COMMAND_ITEM_LORE_INSERT_DESC = LangEntry.builder("Command.Item.Lore.Insert.Desc").text("Insert a lore line at specific position for item in hand.");
    public static final TextLocale COMMAND_ITEM_LORE_REMOVE_DESC = LangEntry.builder("Command.Item.Lore.Remove.Desc").text("Remove a lore line from item in hand.");
    public static final TextLocale COMMAND_ITEM_LORE_CLEAR_DESC  = LangEntry.builder("Command.Item.Lore.Clear.Desc").text("Clear lore of item in hand.");
    public static final TextLocale COMMAND_ITEM_MODEL_DESC       = LangEntry.builder("Command.Item.Model.Desc").text("Change model data of item in hand.");
    public static final TextLocale COMMAND_ITEM_NAME_DESC        = LangEntry.builder("Command.Item.Name.Desc").text("Rename item in hand.");
    public static final TextLocale COMMAND_ITEM_POTION_ADD_DESC  = LangEntry.builder("Command.Item.Potion.Add.Desc").text("Add effect to a potion.");
    public static final TextLocale COMMAND_ITEM_REPAIR_DESC      = LangEntry.builder("Command.Item.Repair.Desc").text("Repair item in hand.");
    public static final TextLocale COMMAND_ITEM_SPAWN_DESC       = LangEntry.builder("Command.Item.Spawn.Desc").text("Spawn item at specific coords.");
    public static final TextLocale COMMAND_ITEM_UNBREAKABLE_DESC = LangEntry.builder("Command.Item.Unbreakable.Desc").text("Set unbreakable flag for item in hand.");

    public static final MessageLocale ERROR_ITEM_NOT_DAMAGEABLE = LangEntry.builder("Error.ItemNotDamageable").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap(SOFT_RED.wrap(GENERIC_ITEM) + " doesn't have a durability.")
    );

    public static final MessageLocale ERROR_ITEM_NOT_STACKABLE = LangEntry.builder("Error.ItemNotStackable").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap(SOFT_RED.wrap(GENERIC_ITEM) + " can not be stacked.")
    );

    public static final MessageLocale ERROR_ITEM_HAS_NO_LORE = LangEntry.builder("Error.ItemHasNoLore").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap(SOFT_RED.wrap(GENERIC_ITEM) + " has no lore.")
    );

    public static final MessageLocale ITEM_SET_AMOUNT_FEEDBACK = LangEntry.builder("Command.Item.Amount.Done").chatMessage(
        Sound.ENTITY_ITEM_PICKUP,
        GRAY.wrap("You have changed " + WHITE.wrap(GENERIC_ITEM) + "'s stack size to " + SOFT_YELLOW.wrap(GENERIC_AMOUNT + "x") + ".")
    );

    public static final MessageLocale ITEM_SET_DAMAGE_FEEDBACK = LangEntry.builder("Command.Item.Damage.Done").chatMessage(
        Sound.ENTITY_ITEM_BREAK,
        GRAY.wrap("You have set " + WHITE.wrap(GENERIC_ITEM) + "'s damage value to " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + ".")
    );

    public static final MessageLocale ITEM_ADD_ENCHANT_INCOMPATIBLE = LangEntry.builder("Command.Item.Enchant.Error.NotCompatible").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap(SOFT_RED.wrap(GENERIC_ENCHANTMENT) + " is not compatible with " + WHITE.wrap(GENERIC_ITEM) + ".")
    );

    public static final MessageLocale ITEM_ADD_ENCHANT_LEVEL_OVERFLOW = LangEntry.builder("Command.Item.Enchant.Error.Overpowered").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("Max. level for " + SOFT_RED.wrap(GENERIC_ENCHANTMENT) + " enchantment is " + SOFT_RED.wrap(GENERIC_LEVEL) + ".")
    );

    public static final MessageLocale ITEM_ADD_ENCHANT_FEEDBACK = LangEntry.builder("Command.Item.Enchant.Done.Enchanted").chatMessage(
        Sound.BLOCK_ENCHANTMENT_TABLE_USE,
        GRAY.wrap("You have enchanted " + WHITE.wrap(GENERIC_ITEM) + " with " + SOFT_PURPLE.wrap(GENERIC_ENCHANTMENT + " " + GENERIC_LEVEL) + ".")
    );

    public static final MessageLocale ITEM_DISENCHANT_SINGLE_NOTHING = LangEntry.builder("Item.Disenchant.Single.Nothing").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap(WHITE.wrap(GENERIC_ITEM) + " does not contains " + SOFT_RED.wrap(GENERIC_ENCHANTMENT) + " enchantment.")
    );

    public static final MessageLocale ITEM_DISENCHANT_SINGLE_FEEDBACK = LangEntry.builder("Item.Disenchant.Single.Feedback").chatMessage(
        Sound.BLOCK_ENCHANTMENT_TABLE_USE,
        GRAY.wrap("You have removed " + SOFT_PURPLE.wrap(GENERIC_ENCHANTMENT + " " + GENERIC_LEVEL) + " from " + WHITE.wrap(GENERIC_ITEM) + ".")
    );

    public static final MessageLocale ITEM_DISENCHANT_FULL_NOTHING = LangEntry.builder("Item.Disenchant.Full.Nothing").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap(WHITE.wrap(GENERIC_ITEM) + " is not enchanted.")
    );

    public static final MessageLocale ITEM_DISENCHANT_FULL_FEEDBACK = LangEntry.builder("Item.Disenchant.Full.Feedback").chatMessage(
        Sound.BLOCK_ENCHANTMENT_TABLE_USE,
        GRAY.wrap("You have removed all enchantments from " + WHITE.wrap(GENERIC_ITEM) + ".")
    );

    public static final MessageLocale ITEM_GET_FEEDBACK = LangEntry.builder("Command.Item.Get.Done").chatMessage(
        Sound.ENTITY_ITEM_PICKUP,
        GRAY.wrap("You have got " + WHITE.wrap(GENERIC_AMOUNT + " x " + GENERIC_ITEM) + ".")
    );

    public static final MessageLocale ITEM_GIVE_FEEDBACK = LangEntry.builder("Command.Item.Give.Done").chatMessage(
        Sound.ENTITY_ITEM_PICKUP,
        GRAY.wrap("You gave " + WHITE.wrap(GENERIC_AMOUNT + " x " + GENERIC_ITEM) + " to " + SOFT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + ".")
    );

    public static final MessageLocale ITEM_GIVE_NOTIFY = LangEntry.builder("Command.Item.Give.Notify").chatMessage(
        Sound.ENTITY_ITEM_PICKUP,
        GRAY.wrap("You have been given " + WHITE.wrap(GENERIC_AMOUNT + " x " + GENERIC_ITEM) + ".")
    );

    public static final MessageLocale ITEM_LORE_ADD_FEEDBACK = LangEntry.builder("Command.Item.Lore.Add.Done").chatMessage(
        Sound.BLOCK_ANVIL_USE,
        GRAY.wrap("You have added " + WHITE.wrap(GENERIC_TEXT) + " to " + WHITE.wrap(GENERIC_ITEM) + "'s lore.")
    );

    public static final MessageLocale ITEM_LORE_REMOVE_FEEDBACK = LangEntry.builder("Command.Item.Lore.Remove.Done").chatMessage(
        Sound.BLOCK_ANVIL_USE,
        GRAY.wrap("You have removed " + WHITE.wrap(GENERIC_TEXT) + " from " + WHITE.wrap(GENERIC_ITEM) + "'s lore.")
    );

    public static final MessageLocale ITEM_LORE_CLEAR_FEEDBACK = LangEntry.builder("Command.Item.Lore.Clear.Done").chatMessage(
        Sound.BLOCK_ANVIL_USE,
        GRAY.wrap("You have cleared " + WHITE.wrap(GENERIC_ITEM) + "'s lore.")
    );

    public static final MessageLocale COMMAND_ITEM_MODEL_DONE = LangEntry.builder("Command.Item.Model.Done").chatMessage(
        Sound.BLOCK_ANVIL_USE,
        GRAY.wrap("Set " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + " as " + SOFT_YELLOW.wrap(GENERIC_ITEM) + "'s model data!")
    );

    public static final MessageLocale ITEM_RENAME_FEEDBACK = LangEntry.builder("Command.Item.Name.Done.Changed").chatMessage(
        Sound.BLOCK_ANVIL_USE,
        GRAY.wrap("You have renamed " + WHITE.wrap(GENERIC_ITEM) + " to " + WHITE.wrap(GENERIC_NAME) + ".")
    );

    public static final MessageLocale ERROR_ITEM_NOT_POTION = LangEntry.builder("Error.ItemNotAPotion").chatMessage(
        SOFT_RED.wrap("You must hold a potion!")
    );

    public static final MessageLocale COMMAND_ITEM_POTION_ADD_DONE = LangEntry.builder("Command.Item.Potion.Add.Done").chatMessage(
        GRAY.wrap("Potion effect added!")
    );

    public static final MessageLocale ITEM_REPAIRED_FEEDBACK = LangEntry.builder("Command.Item.Repair.Done").chatMessage(
        Sound.BLOCK_ANVIL_USE,
        GRAY.wrap("You have repaired " + WHITE.wrap(GENERIC_ITEM) + "!")
    );

    public static final MessageLocale ITEM_DROP_FEEDBACK = LangEntry.builder("Command.Item.Spawn.Done").chatMessage(
        Sound.ENTITY_ITEM_PICKUP,
        GRAY.wrap("You have spawned " + WHITE.wrap(GENERIC_AMOUNT + " x " + GENERIC_ITEM) + " at " + RED.wrap(LOCATION_X) + " " + GREEN.wrap(LOCATION_Y) + " " + BLUE.wrap(LOCATION_Z) + " @ " + WHITE.wrap(LOCATION_WORLD) + ".")
    );

    public static final MessageLocale ITEM_SET_UNBREAKABLE_FEEDBACK = LangEntry.builder("Command.Item.Unbreakable.Done").chatMessage(
        Sound.BLOCK_ANVIL_USE,
        GRAY.wrap("You have toggled " + WHITE.wrap(GENERIC_ITEM) + "'s unbreakable tag to " + WHITE.wrap(GENERIC_STATE) + ".")
    );
}
