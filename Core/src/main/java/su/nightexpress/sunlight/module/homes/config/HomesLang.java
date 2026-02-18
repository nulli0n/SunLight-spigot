package su.nightexpress.sunlight.module.homes.config;

import org.bukkit.Sound;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.EnumLocale;
import su.nightexpress.nightcore.locale.entry.IconLocale;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.homes.HomeDefaults;
import su.nightexpress.sunlight.module.homes.impl.HomeType;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.module.homes.HomePlaceholders.*;
import static su.nightexpress.nightcore.util.placeholder.CommonPlaceholders.*;

public class HomesLang extends Lang {

    public static final EnumLocale<HomeType> ENUM_HOME_TYPE = LangEntry.builder("Homes.HomeType").enumeration(HomeType.class);

    public static final TextLocale COMMAND_ARGUMENT_NAME_HOME = LangEntry.builder("Homes.Command.Argument.Name.Home").text("home");

    public static final TextLocale COMMAND_HOMES_ROOT_DESC        = LangEntry.builder("Homes.Command.Homes.Root.Desc").text("Home commands.");
    public static final TextLocale COMMAND_DELETE_HOME_DESC       = LangEntry.builder("Homes.Command.Homes.Delete.Desc").text("Delete a home.");
    public static final TextLocale COMMAND_TELEPORT_HOME_DESC     = LangEntry.builder("Homes.Command.Homes.Teleport.Desc").text("Teleport to a home.");
    public static final TextLocale COMMAND_SET_HOME_DESC          = LangEntry.builder("Homes.Command.Homes.Set.Desc").text("Set home point.");
    public static final TextLocale COMMAND_VISIT_HOME_DESC        = LangEntry.builder("Homes.Command.Homes.Visit.Desc").text("Visit a home.");
    public static final TextLocale COMMAND_HOME_INVITE_DESC       = LangEntry.builder("Homes.Command.Homes.Invite.Desc").text("Invite player to a home.");
    public static final TextLocale COMMAND_HOME_LIST_DESC         = LangEntry.builder("Homes.Command.Homes.List.Desc").text("List of homes.");
    public static final TextLocale COMMAND_ADMIN_ROOT_DESC        = LangEntry.builder("Homes.Command.HomesAdmin.Root.Desc").text("Homes admin commands.");
    public static final TextLocale COMMAND_ADMIN_DELETE_HOME_DESC = LangEntry.builder("Homes.Command.HomesAdmin.Delete.Desc").text("Delete player's home.");
    public static final TextLocale COMMAND_ADMIN_CREATE_HOME_DESC = LangEntry.builder("Homes.Command.HomesAdmin.Create.Desc").text("Create a home for the player.");

    public static final MessageLocale COMMAND_SYNTAX_INVALID_HOME = LangEntry.builder("Homes.Command.Syntax.InvalidHome").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_INPUT) + " is not a valid home.")
    );

    public static final MessageLocale DATA_NOT_LOADED = LangEntry.builder("Homes.Data.NotLoaded").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("Please wait, the data is still loading...")
    );



    public static final MessageLocale HOME_INVITE_ERROR_YOURSELF = LangEntry.builder("Homes.Command.Homes.Invite.Usage").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You can't invite yourself!")
    );

    public static final MessageLocale HOME_INVITE_SUCCESS_FEEDBACK = LangEntry.builder("Homes.Invite.Success.Feedback").chatMessage(
        Sound.BLOCK_WOODEN_DOOR_OPEN,
        GRAY.wrap("You have invited " + ORANGE.wrap(PLAYER_NAME) + " to your " + WHITE.wrap(HOME_NAME) + " home.")
    );

    public static final MessageLocale HOME_INVITE_SUCCESS_NOTIFY = LangEntry.builder("Homes.Invite.Success.Notify").chatMessage(
        Sound.BLOCK_WOODEN_DOOR_OPEN,
        GRAY.wrap("You have been invited to " + ORANGE.wrap(PLAYER_NAME) + "'s " + WHITE.wrap(HOME_NAME) + " home!")
    );



    public static final MessageLocale ADMIN_HOME_DELETE_ERROR_NO_HOME = LangEntry.builder("Homes.Admin.Delete.Home.NoHome").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("Player " + ORANGE.wrap(PLAYER_NAME) + " does not have a " + WHITE.wrap(HOME_ID) + " home.")
    );

    public static final MessageLocale ADMIN_HOME_DELETE_FEEDBACK = LangEntry.builder("Homes.Command.HomesAdmin.Delete.Done.Single").chatMessage(
        Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR,
        GRAY.wrap("You have deleted " + ORANGE.wrap(PLAYER_NAME) + "'s " + WHITE.wrap(HOME_ID) + " home.")
    );

    public static final MessageLocale ADMIN_HOME_DELETE_ALL_FEEDBACK = LangEntry.builder("Homes.Command.HomesAdmin.Delete.Done.All").chatMessage(
        Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR,
        GRAY.wrap("You have deleted all " + ORANGE.wrap(PLAYER_NAME) + "'s homes.")
    );

    public static final MessageLocale ADMIN_HOME_CREATE_FEEDBACK = LangEntry.builder("Homes.Command.HomesAdmin.Create.Done.Fresh").chatMessage(
        Sound.BLOCK_WOODEN_DOOR_OPEN,
        GRAY.wrap("You have set " + WHITE.wrap(HOME_ID) + " home for " + ORANGE.wrap(PLAYER_NAME) + ".")
    );

    public static final MessageLocale ADMIN_HOME_MOVE_FEEDBACK = LangEntry.builder("Homes.Command.HomesAdmin.Create.Done.Edited").chatMessage(
        Sound.BLOCK_WOODEN_DOOR_OPEN,
        GRAY.wrap("You have moved " + WHITE.wrap(HOME_ID) + "home for " + ORANGE.wrap(PLAYER_NAME) + ".")
    );



    public static final MessageLocale HOME_TELEPORT_ERROR_INACTIVE = LangEntry.builder("Homes.Home.Teleports.Error.Inactive").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("Home " + ORANGE.wrap(HOME_NAME) + " located in a world that is currently unavailable.")
    );

    public static final MessageLocale HOME_TELEPORT_SUCCESS = LangEntry.builder("Homes.Home.Teleports.Success").titleMessage(
        ORANGE.wrap(BOLD.wrap("Home")),
        GRAY.wrap("You have teleported to " + ORANGE.wrap(HOME_NAME) + " home."),
        Sound.ENTITY_ENDERMAN_TELEPORT
    );



    public static final MessageLocale HOME_VISIT_SUCCESS = LangEntry.builder("Homes.Home.Visit.Success").chatMessage(
        Sound.ENTITY_ENDERMAN_TELEPORT,
        GRAY.wrap("You have teleported to " + ORANGE.wrap(HOME_OWNER) + "'s " + WHITE.wrap(HOME_NAME) + " home.")
    );

    public static final MessageLocale HOME_VISIT_ERROR_NOT_PERMITTED = LangEntry.builder("Homes.Home.Visit.Error.NotPermitted").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You don't have an invite to this home!")
    );



    public static final MessageLocale HOME_SET_ERROR_LIMIT = LangEntry.builder("Homes.Home.Set.Error.Limit").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You have a maximum " + ORANGE.wrap(GENERIC_AMOUNT) + " of " + ORANGE.wrap(SLPlaceholders.GENERIC_MAX) + " homes created.")
    );

    public static final MessageLocale HOME_SET_ERROR_WORLD = LangEntry.builder("Homes.Home.Set.Error.World").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You can't create homes in this world.")
    );

    public static final MessageLocale HOME_SET_ERROR_PROTECTION = LangEntry.builder("Homes.Home.Set.Error.Protection").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You can't create homes in protected areas.")
    );

    public static final MessageLocale HOME_SET_CREATED = LangEntry.builder("Homes.Home.Set.Success").titleMessage(
        GREEN.wrap(BOLD.wrap("Home Set!")),
        GRAY.wrap("Usage: " + GREEN.wrap("/" + HomeDefaults.DEFAULT_TELEPORT_ALIAS + " " + HOME_ID) + " | View homes: " + GREEN.wrap("/" + HomeDefaults.DEFAULT_LIST_ALIAS)),
        Sound.BLOCK_WOODEN_DOOR_OPEN
    );

    public static final MessageLocale HOME_SET_MOVED = LangEntry.builder("Homes.Home.Set.Edited").titleMessage(
        YELLOW.wrap(BOLD.wrap("Home Moved!")),
        GRAY.wrap("You have moved your home."),
        Sound.BLOCK_WOODEN_DOOR_CLOSE
    );



    public static final MessageLocale HOME_DELETE_DONE = LangEntry.builder("Homes.Home.Delete.Done").titleMessage(
        RED.wrap(BOLD.wrap("Home Removed!")),
        GRAY.wrap("You have removed " + RED.wrap(HOME_NAME) + " home."),
        Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR
    );


    public static final IconLocale UI_HOMES_NORMAL = LangEntry.iconBuilder("Homes.UI.Homes.Normal")
        .rawName(YELLOW.and(BOLD).wrap("Home:") + " " + WHITE.wrap(HOME_NAME))
        .appendCurrent("ID", HOME_ID)
        .appendCurrent("Type", HOME_TYPE)
        .appendCurrent("Location", GRAY.wrap(RED.wrap(HOME_X) + ", " + GREEN.wrap(HOME_Y) + ", " + BLUE.wrap(HOME_Z) + " @ " + WHITE.wrap(HOME_WORLD)))
        .br()
        .appendClick("Click to configure", YELLOW)
        .build();

    public static final IconLocale UI_HOMES_FAVORITE = LangEntry.iconBuilder("Homes.UI.Homes.Favorite")
        .rawName(GREEN.and(BOLD).wrap("Home:") + " " + WHITE.wrap(HOME_NAME))
        .rawLore(GRAY.wrap(GREEN.wrap("✔") + " This is your " + GREEN.wrap("favorite") + " home."))
        .br()
        .appendCurrent("ID", HOME_ID)
        .appendCurrent("Type", HOME_TYPE)
        .appendCurrent("Location", GRAY.wrap(RED.wrap(HOME_X) + ", " + GREEN.wrap(HOME_Y) + ", " + BLUE.wrap(HOME_Z) + " @ " + WHITE.wrap(HOME_WORLD)))
        .br()
        .appendClick("Click to configure", GREEN)
        .build();

    public static final IconLocale UI_HOMES_LOCKED = LangEntry.iconBuilder("Homes.UI.Homes.Locked")
        .rawName(RED.and(BOLD).wrap("Locked Slot"))
        .rawLore(GRAY.wrap("Upgrade your " + RED.wrap("/rank") + " to unlock more slots!"))
        .build();

    public static final IconLocale UI_HOMES_EMPTY = LangEntry.iconBuilder("Homes.UI.Homes.Empty")
        .rawName(WHITE.and(BOLD).wrap("Empty Slot"))
        .appendClick("Click to set home", WHITE)
        .build();

    public static final IconLocale UI_HOMES_TELEPORT = LangEntry.iconBuilder("Homes.UI.Homes.Teleport")
        .rawName(GREEN.and(BOLD).wrap("Teleport"))
        .appendClick("Click to teleport", GREEN)
        .build();

    public static final IconLocale UI_ICON_SELECTION_ICON = LangEntry.iconBuilder("Homes.UI.IconSelection.Icon")
        .rawName(YELLOW.and(BOLD).wrap("Icon: ") + WHITE.wrap(GENERIC_NAME))
        .appendClick("Click to select", YELLOW)
        .build();

    public static final IconLocale UI_INVITED_PLAYERS_PLAYER = LangEntry.iconBuilder("Homes.UI.InvitedPlayers.Player")
        .rawName(YELLOW.and(BOLD).wrap(PLAYER_NAME))
        .appendClick("Press " + KEY.apply("key.drop") + " to remove", YELLOW)
        .build();


    public static final MessageLocale ERROR_NO_HOMES = LangEntry.builder("Homes.Error.NoHomes").chatMessage(
        GRAY.wrap("You have no homes set.")
    );
}
