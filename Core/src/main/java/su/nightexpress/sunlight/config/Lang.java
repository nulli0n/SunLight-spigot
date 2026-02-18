package su.nightexpress.sunlight.config;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.inventory.EquipmentSlot;
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.EnumLocale;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.locale.message.MessageData;
import su.nightexpress.sunlight.utils.Direction;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.*;

public class Lang implements LangContainer {

    public static final EnumLocale<EquipmentSlot>  EQUIPMENT_SLOT  = LangEntry.builder("EquipmentSlot").enumeration(EquipmentSlot.class);
    public static final EnumLocale<GameMode>       GAME_MODE       = LangEntry.builder("GameMode").enumeration(GameMode.class);
    public static final EnumLocale<Direction>      DIRECTION       = LangEntry.builder("Direction").enumeration(Direction.class);

    public static final TextLocale COMMAND_ARGUMENT_NAME_SLOT        = LangEntry.builder("Command.Argument.Name.Slot").text("slot");
    public static final TextLocale COMMAND_ARGUMENT_NAME_ENCHANT     = LangEntry.builder("Command.Argument.Name.Enchant").text("enchant");
    public static final TextLocale COMMAND_ARGUMENT_NAME_LEVEL       = LangEntry.builder("Command.Argument.Name.Level").text("level");
    public static final TextLocale COMMAND_ARGUMENT_NAME_TARGET      = LangEntry.builder("Command.Argument.Name.Target").text("target");
    public static final TextLocale COMMAND_ARGUMENT_NAME_TYPE        = LangEntry.builder("Command.Argument.Name.Type").text("type");
    public static final TextLocale COMMAND_ARGUMENT_NAME_ENTITY_TYPE = LangEntry.builder("Command.ArgumentName.EntityType").text("mob type");
    public static final TextLocale COMMAND_ARGUMENT_NAME_TIME        = LangEntry.builder("Command.Argument.Name.Time").text("time");
    public static final TextLocale COMMAND_ARGUMENT_NAME_MODE        = LangEntry.builder("Command.Argument.Name.Mode").text("mode");
    public static final TextLocale COMMAND_ARGUMENT_NAME_STATE        = LangEntry.builder("Command.Argument.Name.State").text("state");
    public static final TextLocale COMMAND_ARGUMENT_NAME_INET_ADDRESS = LangEntry.builder("Command.Argument.Name.IPAddress").text("ip");
    public static final TextLocale COMMAND_ARGUMENT_NAME_RADIUS       = LangEntry.builder("Command.Argument.Name.Radius").text("radius");
    public static final TextLocale COMMAND_ARGUMENT_NAME_TEXT        = LangEntry.builder("Command.Argument.Name.Text").text("text");
    public static final TextLocale COMMAND_ARGUMENT_NAME_COMMAND     = LangEntry.builder("Command.Argument.Name.Command").text("command");
    public static final TextLocale COMMAND_ARGUMENT_NAME_POSITION    = LangEntry.builder("Command.Argument.Name.Position").text("position");
    public static final TextLocale COMMAND_ARGUMENT_NAME_X           = LangEntry.builder("Command.Argument.Name.X").text("x");
    public static final TextLocale COMMAND_ARGUMENT_NAME_Y           = LangEntry.builder("Command.Argument.Name.Y").text("y");
    public static final TextLocale COMMAND_ARGUMENT_NAME_Z           = LangEntry.builder("Command.Argument.Name.Z").text("z");
    public static final TextLocale COMMAND_ARGUMENT_NAME_OWNER       = LangEntry.builder("Command.ArgumentName.Owner").text("owner");


    public static final MessageLocale COMMAND_COOLDOWN_DEFAULT = LangEntry.builder("Generic.Command.Cooldown.Default").message(
        MessageData.CHAT_NO_PREFIX,
        SOFT_RED.wrap("You have to wait " + WHITE.wrap(GENERIC_TIME) + " before you can use " + WHITE.wrap(GENERIC_COMMAND) + " again.")
    );

    public static final MessageLocale COMMAND_COOLDOWN_ONE_TIME = LangEntry.builder("Generic.Command.Cooldown.OneTime").message(
        MessageData.CHAT_NO_PREFIX,
        SOFT_RED.wrap("This command is one-time and you already have used it.")
    );



    public static final MessageLocale TELEPORT_UNSAFE_FEEDBACK = LangEntry.builder("Teleport.Unsafe.Feedback").chatMessage(
        GRAY.wrap("Could not teleport " + WHITE.wrap(PLAYER_DISPLAY_NAME) + " due to invalid or unsafe location.")
    );

    public static final MessageLocale TELEPORT_UNSAFE_NOTIFY = LangEntry.builder("Teleport.Unsafe.Notify").chatMessage(
        GRAY.wrap("Teleportation was cancelled due to invalid or unsafe location.")
    );

    public static final MessageLocale TELEPORT_NO_OFFLINE_HANDLER_FEEDBACK = LangEntry.builder("Teleport.NoOfflineHandler.Feedback").chatMessage(
        GRAY.wrap("Could not teleport " + WHITE.wrap(PLAYER_DISPLAY_NAME) + " due to no offline player handler available.")
    );



    public static final MessageLocale COMMAND_SYNTAX_INVALID_SLOT = LangEntry.builder("Command.Syntax.InvalidEquipmentSlot").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_INPUT) + " is not a valid equipment slot!"));

    public static final MessageLocale COMMAND_SYNTAX_INVALID_INET_ADDRESS = LangEntry.builder("Command.Syntax.InvalidInetAddress").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_INPUT) + " is not a valid inet address!"));

    public static final MessageLocale COMMAND_SYNTAX_INVALID_ENTITY_TYPE = LangEntry.builder("Command.Syntax.InvalidEntityType").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_INPUT) + " is not a valid entity type!"));

    public static final MessageLocale ERROR_COMMAND_INVALID_TYPE_ARGUMENT = LangEntry.builder("Error.Command.Argument.InvalidType").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_INPUT) + " is not a valid type!"));

    public static final MessageLocale COMMAND_SYNTAX_INVALID_MOB_EFFECT = LangEntry.builder("Command.Syntax.InvalidMobEffect").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_INPUT) + " is not a valid potion effect!"));

    /*public static final MessageLocale ERROR_COMMAND_INVALID_TIME_ARGUMENT = LangEntry.builder("Error.Command.Argument.InvalidTime").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_INPUT) + " is not a valid time!"));*/

    public static final MessageLocale COMMAND_SYNTAX_INVALID_MODE = LangEntry.builder("Error.Command.Argument.InvalidMode").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_INPUT) + " is not a valid mode!"));

    public static final MessageLocale ERROR_COMMAND_INVALID_IP_ARGUMENT = LangEntry.builder("Error.Command.Argument.InvalidIPAddress").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_INPUT) + " is not an IP address!"));




    @Deprecated
    public static final TextLocale EDITOR_INPUT_GENERIC_SECONDS = LangEntry.builder("Editor.Input.Generic.Seconds").text(
        GRAY.wrap("Enter " + GREEN.wrap("[Seconds Amount]")));

    @Deprecated
    public static final TextLocale    EDITOR_INPUT_GENERIC_NAME = LangEntry.builder("Editor.Input.Generic.Name").text(
        GRAY.wrap("Enter " + GREEN.wrap("[Name]")));

    public static final MessageLocale ERROR_REQUIRES_ITEM_IN_HAND = LangEntry.builder("Error.EmptyHand").chatMessage(
        Sound.BLOCK_ANVIL_PLACE,
        SOFT_RED.wrap("You must hold an item in your hand!"));

    public static final MessageLocale ERROR_NO_INTERNALS_HANDLER = LangEntry.builder("Error.NoInternalsHandler").chatMessage(
        SOFT_RED.wrap("Operation failed: Server internals handler is missing.")
    );

    public static final TextLocale OTHER_FREE = LangEntry.builder("Other.Free").text(GREEN.wrap("Free"));
}
