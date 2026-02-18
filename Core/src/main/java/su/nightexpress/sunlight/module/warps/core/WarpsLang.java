package su.nightexpress.sunlight.module.warps.core;

import org.bukkit.Sound;
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;

import static su.nightexpress.nightcore.util.placeholder.CommonPlaceholders.GENERIC_INPUT;
import static su.nightexpress.nightcore.util.placeholder.CommonPlaceholders.PLAYER_DISPLAY_NAME;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.module.warps.WarpsPlaceholders.WARP_DESCRIPTION;
import static su.nightexpress.sunlight.module.warps.WarpsPlaceholders.WARP_NAME;

public class WarpsLang implements LangContainer {

    public static final TextLocale COMMAND_WARPS_ROOT_DESC   = LangEntry.builder("Warps.Command.Warps.Root.Desc").text("Warps commands.");
    public static final TextLocale COMMAND_WARPS_DELETE_DESC = LangEntry.builder("Warps.Command.Warps.Delete.Desc").text("Delete a warp.");
    public static final TextLocale COMMAND_WARPS_CREATE_DESC = LangEntry.builder("Warps.Command.Warps.Create.Desc").text("Create a new warp.");
    public static final TextLocale COMMAND_WARPS_UPDATE_DESC = LangEntry.builder("Warps.Command.Warps.Update.Desc").text("Update warp location.");
    public static final TextLocale COMMAND_WARPS_EDIT_DESC   = LangEntry.builder("Warps.Command.Warps.Edit.Desc").text("Edit a warp.");
    public static final TextLocale COMMAND_WARPS_JUMP_DESC   = LangEntry.builder("Warps.Command.Warps.Jump.Desc").text("Jump to a warp.");
    public static final TextLocale COMMAND_WARPS_LIST_DESC   = LangEntry.builder("Warps.Command.Warps.List.Desc").text("Open Warps GUI.");

    public static final TextLocale COMMAND_WARP_DESC = LangEntry.builder("Warps.Command.Warp.Desc").text("Go to the " + WARP_NAME + " warp.");

    public static final MessageLocale COMMAND_SYNTAX_INVALID_WARP = LangEntry.builder("Warps.Command.Syntax.InvalidWarp").chatMessage(
        GRAY.wrap(RED.wrap(GENERIC_INPUT) + " is not a valid warp!"));



    public static final MessageLocale WARP_CREATION_INVALID_ID = LangEntry.builder("Warps.Creation.InvalidId").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("Warp ID must be alphabetical!")
    );

    public static final MessageLocale WARP_CREATION_ALREADY_EXISTS = LangEntry.builder("Warps.Creation.AlreadyExists").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("Warp already exists!")
    );

    public static final MessageLocale WARP_CREATION_NOTIFY = LangEntry.builder("Warps.Creation.Notfy").chatMessage(
        Sound.BLOCK_ANVIL_PLACE,
        GRAY.wrap("You have created " + ORANGE.wrap(WARP_NAME) + " warp.")
    );


    public static final MessageLocale WARP_UPDATE_NOTIFY = LangEntry.builder("Warps.Update.Notify").chatMessage(
        Sound.BLOCK_ANVIL_PLACE,
        GRAY.wrap("You have updated " + ORANGE.wrap(WARP_NAME) + " warp's location.")
    );


    public static final MessageLocale WARP_DELETE_NOTIFY = LangEntry.builder("Warps.Delete.Notify").chatMessage(
        Sound.BLOCK_GLASS_BREAK,
        GRAY.wrap("You have removed " + ORANGE.wrap(WARP_NAME) + " warp.")
    );

    public static final MessageLocale WARP_TELEPORT_NOTIFY = LangEntry.builder("Warps.Warp.Jump.Notify").titleMessage(
        ORANGE.wrap(BOLD.wrap(WARP_NAME)),
        GRAY.wrap(WARP_DESCRIPTION),
        Sound.ENTITY_ENDERMAN_TELEPORT
    );

    public static final MessageLocale WARP_TELEPORT_FEEDBACK = LangEntry.builder("Warps.Warp.Jump.Feedback").chatMessage(
        GRAY.wrap("You have teleported " + ORANGE.wrap(PLAYER_DISPLAY_NAME) + " to " + WHITE.wrap(WARP_NAME) + " warp.")
    );


    public static final MessageLocale BROWSER_EMPTY = LangEntry.builder("Warps.Browser.Empty").chatMessage(
        GRAY.wrap("There are no warps available.")
    );

    public static final MessageLocale BROWSER_FEEDBACK = LangEntry.builder("Warps.Browser.Feedback").chatMessage(
        GRAY.wrap("You have opened Warps GUI for " + ORANGE.wrap(PLAYER_DISPLAY_NAME) + ".")
    );


    public static final MessageLocale ERROR_NO_WARP_PERMISSION = LangEntry.builder("Warps.Error.NoWarpPermission").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You don't have access to the " + RED.wrap(WARP_NAME) + " warp.")
    );

    public static final MessageLocale ERROR_INACTIVE_WARP = LangEntry.builder("Warps.Error.InactiveWarp").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap(RED.wrap(WARP_NAME) + " is currently unavailable.")
    );
}
