package su.nightexpress.sunlight.module.homes.config;

import org.bukkit.Sound;
import su.nightexpress.nightcore.language.entry.LangEnum;
import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.entry.LangText;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.homes.command.HomeCommands;
import su.nightexpress.sunlight.module.homes.impl.HomeType;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.sunlight.module.homes.util.Placeholders.*;
import static su.nightexpress.nightcore.language.tag.MessageTags.*;

public class HomesLang extends Lang {

    public static final LangEnum<HomeType> HOME_TYPE = LangEnum.of("Homes.HomeType", HomeType.class);

    public static final LangString COMMAND_ARGUMENT_NAME_HOME = LangString.of("Homes.Command.Argument.Name.Home", "home");

    public static final LangString COMMAND_DELETE_HOME_DESC       = LangString.of("Homes.Command.Homes.Delete.Desc", "Delete a home.");
    public static final LangString COMMAND_TELEPORT_HOME_DESC     = LangString.of("Homes.Command.Homes.Teleport.Desc", "Teleport to a home.");
    public static final LangString COMMAND_SET_HOME_DESC          = LangString.of("Homes.Command.Homes.Set.Desc", "Set home point.");
    public static final LangString COMMAND_VISIT_HOME_DESC        = LangString.of("Homes.Command.Homes.Visit.Desc", "Visit a home.");
    public static final LangString COMMAND_HOME_INVITE_DESC       = LangString.of("Homes.Command.Homes.Invite.Desc", "Invite player to a home.");
    public static final LangString COMMAND_HOME_LIST_DESC         = LangString.of("Homes.Command.Homes.List.Desc", "List of homes.");
    public static final LangString COMMAND_ADMIN_DELETE_HOME_DESC = LangString.of("Homes.Command.HomesAdmin.Delete.Desc", "Delete player's home.");
    public static final LangString COMMAND_ADMIN_CREATE_HOME_DESC = LangString.of("Homes.Command.HomesAdmin.Create.Desc", "Create a home for the player.");

    public static final LangText COMMAND_HOME_INVITE_DONE = LangText.of("Homes.Command.Homes.Invute.Usage",
        LIGHT_GRAY.enclose("You invited " + LIGHT_YELLOW.enclose(PLAYER_NAME) + " to your " + LIGHT_YELLOW.enclose(HOME_NAME) + " home!")
    );

    public static final LangText COMMAND_ADMIN_DELETE_HOME_DONE_SINGLE = LangText.of("Homes.Command.HomesAdmin.Delete.Done.Single",
        LIGHT_GRAY.enclose("You deleted " + LIGHT_YELLOW.enclose(PLAYER_NAME) + "'s " + LIGHT_YELLOW.enclose(HOME_ID) + " home.")
    );

    public static final LangText COMMAND_ADMIN_DELETE_HOME_DONE_ALL = LangText.of("Homes.Command.HomesAdmin.Delete.Done.All",
        LIGHT_GRAY.enclose("You deleted all " + LIGHT_YELLOW.enclose(PLAYER_NAME) + "'s homes.")
    );

    public static final LangText COMMAND_ADMIN_CREATE_HOME_DONE_FRESH = LangText.of("Homes.Command.HomesAdmin.Create.Done.Fresh",
        LIGHT_GRAY.enclose("Set " + LIGHT_YELLOW.enclose(HOME_ID) + " home for " + LIGHT_YELLOW.enclose(PLAYER_NAME) + ".")
    );

    public static final LangText COMMAND_ADMIN_CREATE_HOME_DONE_EDITED = LangText.of("Homes.Command.HomesAdmin.Create.Done.Edited",
        LIGHT_GRAY.enclose("Updated " + LIGHT_YELLOW.enclose(HOME_ID) + "home of " + LIGHT_YELLOW.enclose(PLAYER_NAME) + ".")
    );

    public static final LangText HOME_TELEPORT_SUCCESS = LangText.of("Homes.Home.Teleport",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_ENDERMAN_TELEPORT),
        LIGHT_YELLOW.enclose(BOLD.enclose("Home")),
        LIGHT_GRAY.enclose("You teleported to " + LIGHT_YELLOW.enclose(HOME_NAME) + " home.")
    );

    public static final LangText HOME_VISIT_SUCCESS = LangText.of("Homes.Home.Visit.Success",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_ENDERMAN_TELEPORT),
        LIGHT_YELLOW.enclose(BOLD.enclose("Home Visit")),
        LIGHT_GRAY.enclose("You visited the " + LIGHT_YELLOW.enclose(HOME_OWNER) + "'s " + LIGHT_YELLOW.enclose(HOME_NAME) + " home.")
    );

    public static final LangText HOME_VISIT_ERROR_NOT_PERMITTED = LangText.of("Homes.Home.Visit.Error.NotPermitted",
        SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose("You don't have an invite to this home!")
    );

    public static final LangText HOME_VISIT_ERROR_UNSAFE = LangText.of("Homes.Home.Visit.Error.UnsafeLocation",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose(BOLD.enclose("Unsafe Location!")),
        LIGHT_RED.enclose("Visit has been cancelled.")
    );

    public static final LangText HOME_SET_ERROR_LIMIT = LangText.of("Homes.Home.Set.Error.Limit",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose(BOLD.enclose("Limit Reached!")),
        LIGHT_RED.enclose("You can't create more homes.")
    );

    public static final LangText HOME_SET_ERROR_WORLD = LangText.of("Homes.Home.Set.Error.World",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose(BOLD.enclose("World Error!")),
        LIGHT_RED.enclose("You can't create homes in this world.")
    );

    public static final LangText HOME_SET_ERROR_UNSAFE = LangText.of("Homes.Home.Set.Error.UnsafeLocation",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose(BOLD.enclose("Unsafe Location!")),
        LIGHT_RED.enclose("You can't create homes here.")
    );

    public static final LangText HOME_SET_ERROR_PROTECTION = LangText.of("Homes.Home.Set.Error.Protection",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose(BOLD.enclose("Protected Area!")),
        LIGHT_RED.enclose("You can't create homes here.")
    );

    public static final LangText HOME_SET_SUCCESS = LangText.of("Homes.Home.Set.Success",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.BLOCK_WOODEN_DOOR_OPEN),
        LIGHT_YELLOW.enclose(BOLD.enclose("Home Set!")),
        LIGHT_GRAY.enclose("Usage: " + LIGHT_YELLOW.enclose("/" + HomeCommands.DEF_TELEPORT_ALIAS + " " + HOME_ID) + " | View homes: " + LIGHT_YELLOW.enclose("/" + HomeCommands.DEF_LIST_ALIAS))
    );

    public static final LangText HOME_SET_EDITED = LangText.of("Homes.Home.Set.Edited",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.BLOCK_WOODEN_DOOR_CLOSE),
        LIGHT_YELLOW.enclose(BOLD.enclose("Home Updated!")),
        LIGHT_GRAY.enclose("Home location has been updated.")
    );

    public static final LangText HOME_DELETE_DONE = LangText.of("Homes.Home.Delete.Done",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR),
        LIGHT_YELLOW.enclose(BOLD.enclose("Home Removed!")),
        LIGHT_GRAY.enclose("You removed " + LIGHT_YELLOW.enclose(HOME_NAME) + " home.")
    );

    public static final LangText ERROR_NO_HOMES = LangText.of("Homes.Error.NoHomes",
        LIGHT_RED.enclose("You have no homes set!")
    );

    public static final LangText COMMAND_ERROR_INVALID_HOME_ARGUMENT = LangText.of("Homes.Command.Error.Argument.InvalidHome",
        LIGHT_GRAY.enclose(LIGHT_RED.enclose(GENERIC_VALUE) + " is not a valid home.")
    );

    public static final LangString EDITOR_ENTER_PLAYER_NAME = LangString.of("Homes.Editor.Enter.PlayerName",
        LIGHT_GRAY.enclose("Enter " + LIGHT_GREEN.enclose("[Player Name]"))
    );

    public static final LangString EDITOR_ENTER_HOME_NAME = LangString.of("Homes.Editor.Enter.HomeName",
        LIGHT_GRAY.enclose("Enter " + LIGHT_GREEN.enclose("[Home Name]"))
    );
}
