package su.nightexpress.sunlight.module.playerwarps.core;

import org.bukkit.Sound;
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.EnumLocale;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.module.playerwarps.menu.PlayerWarpSortType;

import static su.nightexpress.nightcore.util.placeholder.CommonPlaceholders.*;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.module.playerwarps.PlayerWarpsPlaceholders.*;

public class PlayerWarpsLang implements LangContainer {

    public static final EnumLocale<PlayerWarpSortType> SORT_TYPE = LangEntry.builder("PlayerWarps.UI.WarpsMenu.SortType").enumeration(PlayerWarpSortType.class);

    public static final TextLocale CATEGORY_OWN_NAME = LangEntry.builder("PlayerWarps.Category.Own.Name").text("Your Own");
    public static final TextLocale CATEGORY_ALL_NAME = LangEntry.builder("PlayerWarps.Category.All.Name").text("All");

    public static final TextLocale COMMAND_WARPS_ROOT_DESC   = LangEntry.builder("PlayerWarps.Command.Warps.Root.Desc").text("PlayerWarps commands.");
    public static final TextLocale COMMAND_WARPS_DELETE_DESC = LangEntry.builder("PlayerWarps.Command.Warps.Delete.Desc").text("Delete a warp.");
    public static final TextLocale COMMAND_WARPS_CREATE_DESC = LangEntry.builder("PlayerWarps.Command.Warps.Create.Desc").text("Create a new warp.");
    public static final TextLocale COMMAND_WARPS_UPDATE_DESC = LangEntry.builder("PlayerWarps.Command.Warps.Update.Desc").text("Update warp location.");
    public static final TextLocale COMMAND_WARPS_JUMP_DESC   = LangEntry.builder("PlayerWarps.Command.Warps.Jump.Desc").text("Jump to a warp.");
    public static final TextLocale COMMAND_WARPS_LIST_DESC   = LangEntry.builder("PlayerWarps.Command.Warps.List.Desc").text("Open Warps GUI.");

    public static final MessageLocale COMMAND_SYNTAX_INVALID_WARP = LangEntry.builder("PlayerWarps.Command.Syntax.InvalidWarp").chatMessage(
        GRAY.wrap(RED.wrap(GENERIC_INPUT) + " is not a valid warp!"));



    public static final MessageLocale WARP_LIST_OPEN_FEEDBACK = LangEntry.builder("PlayerWarps.WarpsList.Open.Feedback").chatMessage(
        GRAY.wrap("You have opened Warps GUI for " + WHITE.wrap(PLAYER_DISPLAY_NAME) + ".")
    );


    public static final MessageLocale WARP_DELETE_NOTIFY = LangEntry.builder("PlayerWarps.Warp.Delete.Done").chatMessage(
        Sound.BLOCK_GLASS_BREAK,
        GRAY.wrap("You have successfully removed the " + WHITE.wrap(WARP_NAME) + " warp.")
    );



    public static final MessageLocale WARP_UPDATE_NOTIFY = LangEntry.builder("PlayerWarps.Warp.Update.Notify").chatMessage(
        Sound.BLOCK_ANVIL_PLACE,
        GRAY.wrap("You have successfully updated " + WHITE.wrap(WARP_NAME) + "'s warp location.")
    );



    public static final MessageLocale WARP_CREATION_NOTIFY = LangEntry.builder("PlayerWarps.Warp.Creation.Notify").chatMessage(
        Sound.BLOCK_ANVIL_PLACE,
        GRAY.wrap("You have successfully created the " + WHITE.wrap(WARP_NAME) + " warp.")
    );

    public static final MessageLocale WARP_CREATION_INVALID_ID = LangEntry.builder("PlayerWarps.Creation.InvalidId").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("Warp ID must be alphabetical.")
    );

    public static final MessageLocale WARP_CREATION_LONG_ID = LangEntry.builder("PlayerWarps.Creation.IdTooLong").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("Warp ID must not exceed a limit of " + RED.wrap(GENERIC_AMOUNT) + " characters.")
    );

    public static final MessageLocale WARP_CREATION_LIMIT_REACHED = LangEntry.builder("PlayerWarps.Warp.Creation.LimitReached").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You already have created " + RED.wrap(GENERIC_AMOUNT) + "/" + RED.wrap(SLPlaceholders.GENERIC_MAX) + " warps allowed.")
    );

    public static final MessageLocale WARP_CREATION_BANNED_WORLD = LangEntry.builder("PlayerWarps.Warp.Creation.BannedWorld").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You can't create warps in this world.")
    );

    public static final MessageLocale WARP_CREATION_ALREADY_EXISTS = LangEntry.builder("PlayerWarps.Warp.Creation.AlreadyExists").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("Warp with such name already exists.")
    );



    public static final MessageLocale WARP_JUMP_NOTIFY = LangEntry.builder("PlayerWarps.Warp.Jump.Notify").titleMessage(
        SOFT_YELLOW.wrap(BOLD.wrap(WARP_NAME)),
        GRAY.wrap(WARP_DESCRIPTION),
        Sound.ENTITY_ENDERMAN_TELEPORT
    );

    public static final MessageLocale WARP_JUMP_FEEDBACK = LangEntry.builder("PlayerWarps.Warp.Jump.Feedback").chatMessage(
        GRAY.wrap("You have teleported " + ORANGE.wrap(PLAYER_DISPLAY_NAME) + " to " + WHITE.wrap(WARP_NAME) + " warp.")
    );

    public static final MessageLocale WARP_JUMP_INSUFFICIENT_FUNDS = LangEntry.builder("PlayerWarps.Warp.Jump.InsufficientFunds").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You need " + RED.wrap(WARP_PRICE) + " to use " + WHITE.wrap(WARP_NAME) + " warp!")
    );

    public static final MessageLocale WARP_JUMP_INACTIVE = LangEntry.builder("PlayerWarps.Warp.Jump.Inactive").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("Warp " + RED.wrap(WARP_NAME) + " located in a world that is currently unavailable.")
    );



    public static final MessageLocale WARP_FEATURE_NOTHING = LangEntry.builder("PlayerWarps.Feature.Nothing").chatMessage(
        GRAY.wrap("You have no warps to feature.")
    );

    public static final MessageLocale WARP_FEATURE_CANT_AFFORD = LangEntry.builder("PlayerWarps.Feature.CantAfford").chatMessage(
        GRAY.wrap("You need " + RED.wrap(SLOT_PRICE) + " to feature a warp.")
    );

    public static final MessageLocale WARP_FEATURE_UNEXPECTED_ERROR = LangEntry.builder("PlayerWarps.Feature.UnexpectedError").chatMessage(
        GRAY.wrap("Whoops! A selected slot or warp is no more available. Please try again.")
    );

    public static final MessageLocale WARP_FEATURE_SUCCESS = LangEntry.builder("PlayerWarps.Feature.Nothing").chatMessage(
        GRAY.wrap("You have successfully featured the " + ORANGE.wrap(WARP_NAME) + " warp for " + WHITE.wrap(SLOT_DURATION) + ".")
    );



    public static final MessageLocale ERROR_NO_PRIMARY_CATEGORY = LangEntry.builder("PlayerWarps.Error.NoPrimaryCategory").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("No primary category is available!")
    );

    public static final MessageLocale ERROR_NOT_OWN_WARP = LangEntry.builder("PlayerWarps.Error.NotOwnWarp").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You don't own the " + RED.wrap(WARP_NAME) + " warp!")
    );
}
