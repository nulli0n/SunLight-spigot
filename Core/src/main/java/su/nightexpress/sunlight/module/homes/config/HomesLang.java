package su.nightexpress.sunlight.module.homes.config;

import org.bukkit.Sound;
import su.nexmedia.engine.api.lang.LangKey;
import su.nightexpress.sunlight.module.homes.command.basic.HomesCommand;
import su.nightexpress.sunlight.module.homes.command.basic.HomesListCommand;
import su.nightexpress.sunlight.module.homes.command.basic.HomesTeleportCommand;

import static su.nexmedia.engine.utils.Colors.*;
import static su.nightexpress.sunlight.module.homes.util.Placeholders.*;

public class HomesLang {

    public static final LangKey COMMAND_HOMES_DESC  = LangKey.of("Homes.Command.Homes.Desc", "Home management.");
    public static final LangKey COMMAND_HOMES_USAGE = LangKey.of("Homes.Command.Homes.Usage", "[help]");

    public static final LangKey COMMAND_HOMES_DELETE_DESC  = LangKey.of("Homes.Command.Homes.Delete.Desc", "Delete home.");
    public static final LangKey COMMAND_HOMES_DELETE_USAGE = LangKey.of("Homes.Command.Homes.Delete.Usage", "<home>");

    public static final LangKey COMMAND_HOMES_TELEPORT_DESC  = LangKey.of("Homes.Command.Homes.Teleport.Desc", "Teleport to home.");
    public static final LangKey COMMAND_HOMES_TELEPORT_USAGE = LangKey.of("Homes.Command.Homes.Teleport.Usage", "<home>");

    public static final LangKey COMMAND_HOMES_VISIT_DESC  = LangKey.of("Homes.Command.Homes.Visit.Desc", "Visit public home a home you have invite to.");
    public static final LangKey COMMAND_HOMES_VISIT_USAGE = LangKey.of("Homes.Command.Homes.Visit.Usage", "<player> <home>");

    public static final LangKey COMMAND_HOMES_INVITE_DESC  = LangKey.of("Homes.Command.Homes.Invite.Desc", "Invite player to a home.");
    public static final LangKey COMMAND_HOMES_INVITE_USAGE = LangKey.of("Homes.Command.Homes.Invite.Usage", "<player> [home]");
    public static final LangKey COMMAND_HOMES_INVITE_DONE  = LangKey.of("Homes.Command.Homes.Invute.Usage", LIGHT_YELLOW + "You invited " + ORANGE + PLAYER_NAME + LIGHT_YELLOW + " to your " + HOME_NAME + LIGHT_YELLOW + " home!");

    public static final LangKey COMMAND_HOMES_LIST_DESC  = LangKey.of("Homes.Command.Homes.List.Desc", "A list of homes.");
    public static final LangKey COMMAND_HOMES_LIST_USAGE = LangKey.of("Homes.Command.Homes.List.Usage", "[player]");

    public static final LangKey COMMAND_HOMES_SET_DESC  = LangKey.of("Homes.Command.Homes.Set.Desc", "Create a new home point.");
    public static final LangKey COMMAND_HOMES_SET_USAGE = LangKey.of("Homes.Command.Homes.Set.Usage", "[name]");

    public static final LangKey COMMAND_HOMES_ADMIN_DESC  = LangKey.of("Homes.Command.HomesAdmin.Desc", "Admin home management utils.");
    public static final LangKey COMMAND_HOMES_ADMIN_USAGE = LangKey.of("Homes.Command.HomesAdmin.Usage", "[help]");

    public static final LangKey COMMAND_HOMES_ADMIN_DELETE_DESC        = LangKey.of("Homes.Command.HomesAdmin.Delete.Desc", "Delete player's home.");
    public static final LangKey COMMAND_HOMES_ADMIN_DELETE_USAGE       = LangKey.of("Homes.Command.HomesAdmin.Delete.Usage", "<player> [home]");
    public static final LangKey COMMAND_HOMES_ADMIN_DELETE_DONE_SINGLE = LangKey.of("Homes.Command.HomesAdmin.Delete.Done.Single", "You deleted " + GREEN + PLAYER_NAME + GRAY + "'s " + GREEN + HOME_ID + GRAY + " home.");
    public static final LangKey COMMAND_HOMES_ADMIN_DELETE_DONE_ALL    = LangKey.of("Homes.Command.HomesAdmin.Delete.Done.All", "You deleted all " + GREEN + PLAYER_NAME + GRAY + "'s " + GRAY + " homes.");

    public static final LangKey COMMAND_HOMES_ADMIN_CREATE_DESC        = LangKey.of("Homes.Command.HomesAdmin.Create.Desc", "Create a home for the player.");
    public static final LangKey COMMAND_HOMES_ADMIN_CREATE_USAGE       = LangKey.of("Homes.Command.HomesAdmin.Create.Usage", "<player> [name]");
    public static final LangKey COMMAND_HOMES_ADMIN_CREATE_DONE_FRESH  = LangKey.of("Homes.Command.HomesAdmin.Create.Done.Fresh", "You created " + GREEN + HOME_ID + GRAY + " home for " + GREEN + PLAYER_NAME + GRAY + ".");
    public static final LangKey COMMAND_HOMES_ADMIN_CREATE_DONE_EDITED = LangKey.of("Homes.Command.HomesAdmin.Create.Done.Edited", "You edited " + GREEN + PLAYER_NAME + GRAY + "'s " + GREEN + HOME_ID + GRAY + " home location.");

    public static final LangKey HOME_TELEPORT_SUCCESS = LangKey.of("Homes.Home.Teleport",
        "<! type:\"titles:20:50:20\" sound:\"" + Sound.ENTITY_ENDERMAN_TELEPORT.name() + "\" !>" +
            "\n" + GREEN + BOLD + "Home" +
            "\n" + GRAY + "You teleported to your " + GREEN + HOME_NAME + GRAY + " home.");

    public static final LangKey HOME_VISIT_SUCCESS = LangKey.of("Homes.Home.Visit.Success",
        "<! type:\"titles:20:50:20\" sound:\"" + Sound.ENTITY_ENDERMAN_TELEPORT.name() + "\" !>" +
            "\n" + YELLOW + BOLD + "Guest Home" +
            "\n" + GRAY + "You visited the " + YELLOW + HOME_OWNER + GRAY + "'s " + YELLOW + HOME_NAME + GRAY + " home.");

    public static final LangKey HOME_VISIT_ERROR_NOT_PERMITTED = LangKey.of("Homes.Home.Visit.Error.NotPermitted",
        "<! sound:\"" + Sound.ENTITY_VILLAGER_NO.name() + "\" !>" +
            RED + "You don't have invite to this home!");

    public static final LangKey HOME_SET_ERROR_LIMIT      = LangKey.of("Homes.Home.Set.Error.Limit", RED + "You have reached your homes limit. You can not set more.");
    public static final LangKey HOME_SET_ERROR_WORLD      = LangKey.of("Homes.Home.Set.Error.World", RED + "You can not set home in this world!");
    public static final LangKey HOME_SET_ERROR_REGION     = LangKey.of("Homes.Home.Set.Error.Region", RED + "You can not set home in this region!");
    public static final LangKey HOME_SET_ERROR_PROTECTION = LangKey.of("Homes.Home.Set.Error.Protection", RED + "You can not set home in protected areas!");

    public static final LangKey HOME_SET_SUCCESS = LangKey.of("Homes.Home.Set.Success",
        "<! type:\"titles:20:50:20\" sound:\"" + Sound.BLOCK_WOODEN_DOOR_OPEN.name() + "\" !>" +
            "\n" + GREEN + BOLD + "Home Set!" +
            "\n" + GRAY + "Teleport: " + GREEN + "/" + HomesCommand.NAME + " " + HomesTeleportCommand.NAME + " " + HOME_ID + GRAY + " | Menu: " + GREEN + "/" + HomesCommand.NAME + " " + HomesListCommand.NAME);

    public static final LangKey HOME_SET_EDITED = LangKey.of("Homes.Home.Set.Edited",
        "<! type:\"titles:20:50:20\" sound:\"" + Sound.BLOCK_WOODEN_DOOR_CLOSE.name() + "\" !>" +
            "\n" + GREEN + BOLD + "Home Set!" +
            "\n" + GRAY + "Home location has been updated.");

    public static final LangKey HOME_DELETE_DONE = LangKey.of("Homes.Home.Delete.Done",
        "<! type:\"titles:20:50:20\" sound:\"" + Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR.name() + "\" !>" +
            "\n" + GREEN + BOLD + "Home Removed!" +
            "\n" + GRAY + "You deleted " + GREEN + HOME_NAME + GRAY + " home.");

    public static final LangKey HOME_ERROR_INVALID     = LangKey.of("Homes.Home.Error.Invalid", RED + "Home with such id does not exist.");
    public static final LangKey HOME_ERROR_NOT_CREATED = LangKey.of("Homes.Home.Error.NotCreated", RED + "You don't have any homes created yet.");

    public static final LangKey EDITOR_ENTER_PLAYER_NAME = LangKey.of("Homes.Editor.Enter.PlayerName", GRAY + "Enter " + GREEN + "[Player Name]");
    public static final LangKey EDITOR_ENTER_HOME_NAME   = LangKey.of("Homes.Editor.Enter.HomeName", GRAY + "Enter " + GREEN + "[Home Name]");
}
