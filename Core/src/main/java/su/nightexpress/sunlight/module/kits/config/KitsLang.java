package su.nightexpress.sunlight.module.kits.config;

import org.bukkit.Sound;
import su.nightexpress.nightcore.language.entry.LangItem;
import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.entry.LangText;
import su.nightexpress.sunlight.config.Lang;

import static su.nightexpress.nightcore.language.entry.LangItem.builder;
import static su.nightexpress.nightcore.language.tag.MessageTags.OUTPUT;
import static su.nightexpress.nightcore.language.tag.MessageTags.SOUND;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.sunlight.module.kits.util.Placeholders.*;

public class KitsLang extends Lang {

    public static final LangString COMMAND_KITS_EDITOR_DESC         = LangString.of("Kits.Command.Kits.Editor.Desc", "Open kit editor.");
    public static final LangString COMMAND_KITS_PREVIEW_DESC        = LangString.of("Kits.Command.Kits.Preview.Desc", "Preview a kit.");
    public static final LangString COMMAND_KITS_GIVE_DESC           = LangString.of("Kits.Command.Kits.Give.Desc", "Give a kit.");
    public static final LangString COMMAND_KITS_LIST_DESC           = LangString.of("Kits.Command.Kits.List.Desc", "Open Kits GUI.");
    public static final LangString COMMAND_KITS_RESET_COOLDOWN_DESC = LangString.of("Kits.Command.Kits.ResetCooldown.Desc", "Reset player's kit cooldown.");
    public static final LangString COMMAND_KITS_SET_COOLDOWN_DESC   = LangString.of("Kits.Command.Kits.SetCooldown.Desc", "Set player's kit cooldown.");

    public static final LangText COMMAND_PREVIEW_KIT_OTHERS = LangText.of("Kits.Command.Kits.Preview.Others",
        LIGHT_GRAY.enclose("Opened " + LIGHT_YELLOW.enclose(KIT_NAME) + " kit preview for " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + ".")
    );

    public static final LangText COMMAND_KIT_OTHERS = LangText.of("Kits.Command.Kits.Give.Done",
        LIGHT_GRAY.enclose("Given " + LIGHT_YELLOW.enclose(KIT_NAME) + " kit to " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + "!")
    );

//    public static final LangText COMMAND_KIT_DONE = LangText.of("Kits.Command.Kits.Give.Notify",
//        LIGHT_GRAY.enclose("You got " + LIGHT_YELLOW.enclose(KIT_NAME) + " kit.")
//    );

    public static final LangText COMMAND_KIT_LIST_OTHERS = LangText.of("Kits.Command.Kits.List.Others",
        LIGHT_GRAY.enclose("Opened Kits GUI for " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + ".")
    );

    public static final LangText COMMAND_KITS_RESET_COOLDOWN_DONE = LangText.of("Kits.Command.Kits.ResetCooldown.Done",
        LIGHT_GRAY.enclose("Reset " + LIGHT_YELLOW.enclose(KIT_NAME) + " kit cooldown for " + LIGHT_YELLOW.enclose(PLAYER_NAME) + ".")
    );

    public static final LangText COMMAND_KITS_RESET_COOLDOWN_NOTIFY = LangText.of("Kits.Command.Kits.ResetCooldown.Notify",
        LIGHT_GRAY.enclose("Your " + LIGHT_YELLOW.enclose(KIT_NAME) + " kit cooldown have been reset!")
    );

    public static final LangText COMMAND_KITS_SET_COOLDOWN_DONE = LangText.of("Kits.Command.Kits.SetCooldown.Done",
        LIGHT_GRAY.enclose("Set " + LIGHT_YELLOW.enclose(KIT_NAME) + " kit cooldown on " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " for " + LIGHT_YELLOW.enclose(PLAYER_NAME) + ".")
    );

    public static final LangText COMMAND_KITS_SET_COOLDOWN_NOTIFY = LangText.of("Kits.Command.Kits.SetCooldown.Notify",
        LIGHT_GRAY.enclose("Your " + LIGHT_YELLOW.enclose(KIT_NAME) + " kit cooldown have been set to " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + "!")
    );


    public static final LangText KIT_GET = LangText.of("Kits.Kit.Get",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ITEM_ARMOR_EQUIP_LEATHER),
        LIGHT_YELLOW.enclose(BOLD.enclose("Kit Received!")),
        LIGHT_GRAY.enclose("You got " + LIGHT_YELLOW.enclose(KIT_NAME) + " kit!")
    );


    public static final LangText KIT_ERROR_NO_PERMISSION = LangText.of("Kits.Kit.Error.NoPermission",
        LIGHT_RED.enclose("You don't have permissions to use this kit!")
    );

    public static final LangText KIT_ERROR_NOT_ENOUGH_FUNDS = LangText.of("Kits.Kit.Error.NotEnoughFunds",
        LIGHT_RED.enclose("You can't afford this kit! You need " + LIGHT_RED.enclose("$" + KIT_COST) + ".")
    );

    public static final LangText KIT_ERROR_COOLDOWN_EXPIRABLE = LangText.of("Kits.Kit.Error.Cooldown.Expirable",
        LIGHT_RED.enclose("You can use this kit again in " + LIGHT_RED.enclose(GENERIC_COOLDOWN))
    );

    public static final LangText KIT_ERROR_COOLDOWN_ONE_TIMED = LangText.of("Kits.Kit.Error.Cooldown.OneTimed",
        LIGHT_RED.enclose("You already have used this kit. You can not use it more.")
    );

    public static final LangText COMMAND_ERROR_INVALID_KIT_ARGUMENT = LangText.of("Kits.Command.Error.Argument.InvalidKit",
        LIGHT_GRAY.enclose(LIGHT_RED.enclose(GENERIC_VALUE) + " is not a valid kit!")
    );

    public static final LangString EDITOR_TITLE_LIST     = LangString.of("Kits.Editor.Title.List", BLACK.enclose("Kits Editor"));
    public static final LangString EDITOR_TITLE_SETTINGS = LangString.of("Kits.Editor.Title.Settings", BLACK.enclose("Kit Settings"));
    public static final LangString EDITOR_TITLE_CONTENT  = LangString.of("Kits.Editor.Title.Content", BLACK.enclose("Kit Content"));

    public static final LangString EDITOR_ENTER_KIT_ID = LangString.of("Kits.Editor.Enter.Id",
        LIGHT_GRAY.enclose("Enter " + LIGHT_GREEN.enclose("[Kit Identifier]"))
    );

    public static final LangString EDITOR_ENTER_COMMAND = LangString.of("Kits.Editor.Enter.Command",
        LIGHT_GRAY.enclose("Enter " + LIGHT_GREEN.enclose("[Command]"))
    );

    public static final LangString EDITOR_ENTER_COST = LangString.of("Kits.Editor.Enter.Cost",
        LIGHT_GRAY.enclose("Enter " + LIGHT_GREEN.enclose("[Money Cost]"))
    );

    public static final LangString EDITOR_ENTER_DESCRIPTION = LangString.of("Kits.Editor.Enter.Description",
        LIGHT_GRAY.enclose("Enter " + LIGHT_GREEN.enclose("[Description]"))
    );

    public static final LangString EDITOR_ENTER_PRIORITY = LangString.of("Kits.Editor.Enter.Priority",
        LIGHT_GRAY.enclose("Enter " + LIGHT_GREEN.enclose("[Priority]"))
    );

    public static final LangString EDITOR_ERROR_ALREADY_EXISTS = LangString.of("Kits.Editor.Error.AlreadyExists",
        LIGHT_GRAY.enclose("Kit already exists!")
    );

    private static final String PREFIX_OLD = "Editor.KitsEditorType.";

    public static final LangItem EDITOR_KIT_ARMOR = builder(PREFIX_OLD + "KIT_CHANGE_ARMOR")
        .name("Armor Content")
        .emptyLine()
        .text(LIGHT_YELLOW.enclose("Slot #1: " + LIGHT_GRAY.enclose("Boots")))
        .text(LIGHT_YELLOW.enclose("Slot #2: " + LIGHT_GRAY.enclose("Leggings")))
        .text(LIGHT_YELLOW.enclose("Slot #3: " + LIGHT_GRAY.enclose("Chestplate")))
        .text(LIGHT_YELLOW.enclose("Slot #4: " + LIGHT_GRAY.enclose("Helmet")))
        .text(LIGHT_YELLOW.enclose("Slot #5: " + LIGHT_GRAY.enclose("OffHand")))
        .emptyLine()
        .click("navigate")
        .build();

    public static final LangItem EDITOR_KIT_INVENTORY = builder(PREFIX_OLD + "KIT_CHANGE_INVENTORY")
        .name("Inventory Content")
        .emptyLine()
        .text(LIGHT_YELLOW.enclose("Row #1: " + LIGHT_GRAY.enclose("Hotbar")))
        .text(LIGHT_YELLOW.enclose("Row #2+: " + LIGHT_GRAY.enclose("Inventory")))
        .emptyLine()
        .click("navigate")
        .build();

    public static final LangItem EDITOR_KIT_SET_COMMANDS = builder(PREFIX_OLD + "KIT_CHANGE_COMMANDS")
        .name("Commands")
        .textRaw(KIT_COMMANDS)
        .emptyLine()
        .text("Listed commands will be execued when", "player recievies the kit.")
        .emptyLine()
        .leftClick("add command")
        .rightClick("clear")
        .build();

    public static final LangItem EDITOR_KIT_SET_ICON = builder(PREFIX_OLD + "KIT_CHANGE_ICON")
        .name("Icon")
        .dragAndDrop("change")
        .build();

    public static final LangItem EDITOR_KIT_SET_PRIORITY = builder(PREFIX_OLD + "KIT_CHANGE_PRIORITY")
        .name("Priority")
        .current("Current", KIT_PRIORITY)
        .emptyLine()
        .text("Kits with higher priority will", "appear first in the GUI.")
        .emptyLine()
        .click("change")
        .build();

    public static final LangItem EDITOR_KIT_SET_COST = builder(PREFIX_OLD + "KIT_CHANGE_COST")
        .name("Cost")
        .current("Current", KIT_COST)
        .emptyLine()
        .text("Sets how much player must pay", "in order to use this kit.")
        .emptyLine()
        .leftClick("change")
        .rightClick("disable")
        .build();

    public static final LangItem EDITOR_KIT_SET_PERMISSION = builder(PREFIX_OLD + "KIT_CHANGE_PERMISSION")
        .name("Permission Requirement")
        .current("Required", KIT_PERMISSION_REQUIRED)
        .current("Node", KIT_PERMISSION_NODE)
        .emptyLine()
        .text("Sets whether or not player must", "have permission in order to", "use this kit.")
        .emptyLine()
        .click("toggle")
        .build();

    public static final LangItem EDITOR_KIT_SET_COOLDOWN    = builder(PREFIX_OLD + "KIT_CHANGE_COOLDOWN")
        .name("Cooldown")
        .current("Current", KIT_COOLDOWN)
        .emptyLine()
        .text("Sets cooldown to use the kit again.")
        .emptyLine()
        .click("change")
        .swapKey("make one-time")
        .dropKey("disable")
        .build();
    public static final LangItem EDITOR_KIT_SET_DESCRIPTION = builder(PREFIX_OLD + "KIT_CHANGE_DESCRIPTION")
        .name("Description")
        .textRaw(KIT_DESCRIPTION)
        .emptyLine()
        .leftClick("add line")
        .rightClick("clear")
        .build();

    public static final LangItem KIT_SET_NAME = builder(PREFIX_OLD + "KIT_CHANGE_NAME")
        .name("Display Name")
        .current("Current", KIT_NAME)
        .emptyLine()
        .click("change")
        .build();

    public static final LangItem EDITOR_KIT_CREATE = builder(PREFIX_OLD).name("New Kit").build();

    public static final LangItem EDITOR_KIT_OBJECT = builder(PREFIX_OLD + "KIT_OBJECT")
        .name(KIT_NAME + RESET.getBracketsName() + " " + GRAY.enclose("(" + WHITE.enclose(KIT_ID) + ")"))
        .current("Priority", KIT_PRIORITY)
        .current("Permission", KIT_PERMISSION_REQUIRED)
        .emptyLine()
        .leftClick("edit")
        .shiftRight("Delete " + LIGHT_RED.enclose("(no undo)"))
        .build();
}
