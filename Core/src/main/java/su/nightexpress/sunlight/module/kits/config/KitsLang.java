package su.nightexpress.sunlight.module.kits.config;

import org.bukkit.Sound;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.sunlight.config.Lang;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.module.kits.KitsPlaceholders.*;
import static su.nightexpress.sunlight.SLPlaceholders.*;

public class KitsLang extends Lang {

    public static final TextLocale COMMAND_KITS_ROOT_DESC           = LangEntry.builder("Kits.Command.Kits.Root.Desc").text("Kit commands.");
    public static final TextLocale COMMAND_KITS_EDITOR_DESC         = LangEntry.builder("Kits.Command.Kits.Editor.Desc").text("Open kit editor.");
    public static final TextLocale COMMAND_KITS_PREVIEW_DESC        = LangEntry.builder("Kits.Command.Kits.Preview.Desc").text("Preview a kit.");
    public static final TextLocale COMMAND_KITS_GET_DESC            = LangEntry.builder("Kits.Command.Kits.Get.Desc").text("Get a kit.");
    public static final TextLocale COMMAND_KITS_GIVE_DESC           = LangEntry.builder("Kits.Command.Kits.Give.Desc").text("Give a kit.");
    public static final TextLocale COMMAND_KITS_LIST_DESC           = LangEntry.builder("Kits.Command.Kits.List.Desc").text("Open Kits GUI.");
    public static final TextLocale COMMAND_KITS_RESET_COOLDOWN_DESC = LangEntry.builder("Kits.Command.Kits.ResetCooldown.Desc").text("Reset kit cooldown.");
    public static final TextLocale COMMAND_KITS_SET_COOLDOWN_DESC   = LangEntry.builder("Kits.Command.Kits.SetCooldown.Desc").text("Put kit on a cooldown.");

    public static final MessageLocale COMMAND_SYNTAX_INVALID_KIT = LangEntry.builder("Kits.Command.Syntax.InvalidKit").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_INPUT) + " is not a valid kit!")
    );

    public static final MessageLocale DATA_ERROR_NOT_LOADED = LangEntry.builder("Kits.Data.Error.NotLoaded").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("Please wait. The data is still loading...")
    );


    public static final MessageLocale KIT_PREVIEW_FEEDBACK = LangEntry.builder("Kits.Command.Kits.Preview.Others").chatMessage(
        GRAY.wrap("You have showed the " + WHITE.wrap(KIT_NAME) + " kit preview to " + ORANGE.wrap(PLAYER_DISPLAY_NAME) + ".")
    );



    public static final MessageLocale KIT_GET_ERROR_NO_PERMISSION = LangEntry.builder("Kits.Kit.Error.NoPermission").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You don't have permissions to use this kit!")
    );

    public static final MessageLocale KIT_GET_ERROR_NOT_ENOUGH_FUNDS = LangEntry.builder("Kits.Kit.Error.NotEnoughFunds").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You need " + SOFT_RED.wrap("$" + KIT_COST) + " to equip this kit.")
    );

    public static final MessageLocale KIT_GET_ERROR_COOLDOWN = LangEntry.builder("Kits.Kit.Error.Cooldown.Expirable").chatMessage(
        GRAY.wrap("The " + WHITE.wrap(KIT_NAME) + " kit is on cooldown: " + ORANGE.wrap(GENERIC_COOLDOWN))
    );

    public static final MessageLocale KIT_GET_ERROR_ONE_TIME = LangEntry.builder("Kits.Kit.Error.Cooldown.OneTimed").chatMessage(
        GRAY.wrap("You have already used this one-time kit.")
    );

    public static final MessageLocale KIT_GET_NOTIFY = LangEntry.builder("Kits.Kit.Get").titleMessage(
        GREEN.wrap(BOLD.wrap("Kit Equipped")),
        GRAY.wrap("You have equipped the " + GREEN.wrap(KIT_NAME) + " kit."),
        Sound.ITEM_ARMOR_EQUIP_LEATHER
    );



    public static final MessageLocale KIT_GIVE_FEEDBACK = LangEntry.builder("Kits.Command.Kits.Give.Done").chatMessage(
        GRAY.wrap("You have given the " + WHITE.wrap(KIT_NAME) + " kit to " + ORANGE.wrap(PLAYER_DISPLAY_NAME) + ".")
    );



    public static final MessageLocale KIT_BROWSER_OPEN_FEEDBACK = LangEntry.builder("Kits.Command.Kits.List.Others").chatMessage(
        GRAY.wrap("You have showed Kits GUI for " + ORANGE.wrap(PLAYER_DISPLAY_NAME) + ".")
    );

    public static final MessageLocale KIT_RESET_COOLDOWN_FEEDBACK = LangEntry.builder("Kits.Command.Kits.ResetCooldown.Done").chatMessage(
        GRAY.wrap("You have reset " + WHITE.wrap(KIT_NAME) + " kit cooldown for " + ORANGE.wrap(PLAYER_NAME) + ".")
    );

    public static final MessageLocale KIT_RESET_COOLDOWN_NOTIFY = LangEntry.builder("Kits.Command.Kits.ResetCooldown.Notify").chatMessage(
        GRAY.wrap("Your " + WHITE.wrap(KIT_NAME) + " kit cooldown has been reset!")
    );

    public static final MessageLocale KIT_SET_COOLDOWN_FEEDBACK = LangEntry.builder("Kits.Command.Kits.SetCooldown.Done").chatMessage(
        GRAY.wrap("You have put " + WHITE.wrap(KIT_NAME) + " kit on " + WHITE.wrap(GENERIC_AMOUNT) + " cooldown for " + ORANGE.wrap(PLAYER_NAME) + ".")
    );

    public static final MessageLocale KIT_SET_COOLDOWN_NOTIFY = LangEntry.builder("Kits.Command.Kits.SetCooldown.Notify").chatMessage(
        GRAY.wrap("Your " + WHITE.wrap(KIT_NAME) + " kit has been put on cooldown for " + ORANGE.wrap(GENERIC_AMOUNT) + ".")
    );


    public static final MessageLocale KIT_CREATION_ERROR = LangEntry.builder("Kits.KitCreation.Error").chatMessage(
        GRAY.wrap("Kit not created: " + SOFT_RED.wrap(GENERIC_MESSAGE) + ".")
    );



    public static final TextLocale EDITOR_TITLE_LIST     = LangEntry.builder("Kits.Editor.Title.List").text(BLACK.wrap("Kits Editor"));
    public static final TextLocale EDITOR_TITLE_SETTINGS = LangEntry.builder("Kits.Editor.Title.Settings").text(BLACK.wrap("Kit Settings"));
    public static final TextLocale EDITOR_TITLE_CONTENT  = LangEntry.builder("Kits.Editor.Title.Content").text(BLACK.wrap("Kit Content"));
}
