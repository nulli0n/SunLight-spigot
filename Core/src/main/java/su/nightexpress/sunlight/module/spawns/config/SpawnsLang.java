package su.nightexpress.sunlight.module.spawns.config;

import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.nightcore.language.entry.LangItem;
import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.entry.LangText;
import su.nightexpress.sunlight.module.spawns.command.SpawnCommands;

import static su.nightexpress.nightcore.language.entry.LangItem.builder;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.nightcore.language.tag.MessageTags.*;
import static su.nightexpress.sunlight.module.spawns.util.Placeholders.*;

public class SpawnsLang extends CoreLang {

    public static final LangString COMMAND_DELETE_SPAWN_DESC = LangString.of("Spawns.Command.Spawns.Delete.Desc",
        "Delete a spawn."
    );

    public static final LangText COMMAND_DELETE_SPAWN_DONE = LangText.of("Spawns.Command.Spawns.Delete.Done",
        OUTPUT.wrap(20, 60),
        LIGHT_YELLOW.wrap(BOLD.wrap("Spawn Deleted!")),
        LIGHT_GRAY.wrap("You deleted " + LIGHT_YELLOW.wrap(SPAWN_DISPLAY_NAME) + " spawn!")
    );

    public static final LangString COMMAND_SET_SPAWN_DESC = LangString.of("Spawns.Command.Spawns.Create.Desc",
        "Set spawn point."
    );

    public static final LangText COMMAND_SET_SPAWN_DONE = LangText.of("Spawns.Command.Spawns.Create.Done",
        OUTPUT.wrap(20, 60),
        LIGHT_GREEN.wrap(BOLD.wrap("Spawn Set: ") + WHITE.wrap(SPAWN_DISPLAY_NAME)),
        LIGHT_GRAY.wrap("Use " + LIGHT_GREEN.wrap("/" + SpawnCommands.DEF_EDITOR_ALIAS) + " to configure it.")
    );

    public static final LangString COMMAND_SPAWN_DESC = LangString.of("Spawns.Command.Spawns.Teleport.Desc",
        "Teleport to a spawn."
    );

    public static final LangText COMMAND_SPAWN_DONE_OTHERS = LangText.of("Spawns.Command.Spawns.Teleport.Notify",
        LIGHT_GRAY.wrap("Teleporting " + LIGHT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + " on " + LIGHT_YELLOW.wrap(SPAWN_DISPLAY_NAME) + " spawn.")
    );

    public static final LangText COMMAND_SPAWN_ERROR_NO_DEFAULT = LangText.of("Spawns.Command.Spawn.Error.NoDefault",
        LIGHT_RED.wrap("You must specify spawn name.")
    );

    public static final LangString COMMAND_SPAWNS_EDITOR_DESC = LangString.of("Spawns.Command.Spawns.Editor.Desc",
        "Open spawn editor."
    );

    public static final LangText SPAWN_TELEPORT_DONE = LangText.of("Spawns.Command.Spawns.Teleport.Done",
        OUTPUT.wrap(20, 60),
        LIGHT_YELLOW.wrap(BOLD.wrap("Spawn")),
        LIGHT_GRAY.wrap("You teleported to the " + LIGHT_YELLOW.wrap(SPAWN_DISPLAY_NAME) + " spawn.")
    );

    public static final LangText SPAWN_TELEPORT_ERROR_WORLD = LangText.of("Spawns.Spawn.Teleport.Error.World",
        LIGHT_RED.wrap("This spawn located in a world that is not available currently.")
    );

    public static final LangText ERROR_COMMAND_INVALID_SPAWN_ARGUMENT = LangText.of("Spawns.Spawn.Error.Invalid",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(GENERIC_VALUE) + " is not a valid spawn!")
    );

    public static final LangString EDITOR_TITLE_LIST = LangString.of("Spawns.Editor.Title.List", BLACK.wrap("Spawns Editor"));
    public static final LangString EDITOR_TITLE_SETTINGS = LangString.of("Spawns.Editor.Title.Settings", BLACK.wrap("Spawn Settings"));

    public static final LangString EDITOR_ENTER_NAME = LangString.of("Spawns.Editor.Enter.Name",
        LIGHT_GRAY.wrap("Enter " + LIGHT_GREEN.wrap("[Spawn Name]"))
    );

    public static final LangString EDITOR_ENTER_PRIORITY = LangString.of("Spawns.Editor.Enter.Priority",
        LIGHT_GRAY.wrap("Enter " + LIGHT_GREEN.wrap("[Priority]"))
    );

    public static final LangString EDITOR_ENTER_GROUP = LangString.of("Spawns.Editor.Enter.Group",
        LIGHT_GRAY.wrap("Enter " + LIGHT_GREEN.wrap("[Group Name]"))
    );

    private static final String PREFIX_OLD = "Editor.SpawnsEditorType.";

    public static final LangItem EDITOR_SPAWN_DEATH_TELEPORT_RANKS = builder(PREFIX_OLD + "DeathTeleport.Ranks")
        .name("Death Groups")
        .textRaw(SPAWN_RESPAWN_TELEPORT_GROUPS)
        .emptyLine()
        .text("List of permission groups affected", "by Death Teleport setting.")
        .emptyLine()
        .shiftLeft("add group")
        .shiftRight("clear groups")
        .build();

    public static final LangItem EDITOR_SPAWN_DEATH_TELEPORT_TOGGLE = builder(PREFIX_OLD + "DeathTeleport.Toggle")
        .name("Death Teleport")
        .current("Enabled", SPAWN_RESPAWN_TELEPORT_ENABLED)
        .emptyLine()
        .text("Teleports players from listed groups", "to this spawn after death.")
        .emptyLine()
        .leftClick("toggle")
        .build();

    public static final LangItem EDITOR_SPAWN_LOGIN_TELEPORT_RANKS = builder(PREFIX_OLD + "LoginTeleport.Ranks")
        .name("Login Groups")
        .textRaw(SPAWN_LOGIN_TELEPORT_GROUPS)
        .emptyLine()
        .text("List of permission groups affected", "by Login Teleport setting.")
        .emptyLine()
        .shiftLeft("add group")
        .shiftRight("clear groups")
        .build();

    public static final LangItem EDITOR_SPAWN_LOGIN_TELEPORT_TOGGLE = builder(PREFIX_OLD + "LoginTeleport.Toggle")
        .name("Login Teleport")
        .current("Enabled", SPAWN_LOGIN_TELEPORT_ENABLED)
        .emptyLine()
        .text("Teleports players from listed groups", "to this spawn on login.")
        .emptyLine()
        .leftClick("toggle")
        .build();

    public static final LangItem EDITOR_SPAWN_EDIT_PRIORITY = builder(PREFIX_OLD + "SPAWN_CHANGE_PRIORITY")
        .name("Priority")
        .current("Current", SPAWN_PRIORITY)
        .emptyLine()
        .text("If multiple spawns are available", "for a player on death/login,")
        .text("the one with the greatest", "priority will be used.")
        .emptyLine()
        .leftClick("change")
        .build();

    public static final LangItem EDITOR_SPAWN_PERMISSION = builder(PREFIX_OLD + "SPAWN_CHANGE_PERMISSION")
        .name("Permission Requirement")
        .current("Enabled", SPAWN_PERMISSION_REQUIRED)
        .current("Permission Node", SPAWN_PERMISSION_NODE)
        .emptyLine()
        .text("Sets whether or not players", "must have permission to", "use this spawn.")
        .emptyLine()
        .leftClick("toggle")
        .build();

    public static final LangItem EDITOR_SPAWN_LOCATION = builder(PREFIX_OLD + "SPAWN_CHANGE_LOCATION")
        .name("Location")
        .current("Current", SPAWN_LOCATION_X + ", " + SPAWN_LOCATION_Y + ", " + SPAWN_LOCATION_Z + " in " + SPAWN_LOCATION_WORLD)
        .emptyLine()
        .leftClick("set to your position")
        .rightClick("set as world spawn")
        .build();

    public static final LangItem EDITOR_SPAWN_NAME = builder(PREFIX_OLD + "SPAWN_CHANGE_NAME")
        .name("Display Name")
        .current("Current", SPAWN_DISPLAY_NAME)
        .emptyLine()
        .text("Sets spawn display name.")
        .emptyLine()
        .leftClick("change")
        .build();

    public static final LangItem EDITOR_SPAWN_OBJECT = builder(PREFIX_OLD + "SPAWN_OBJECT")
        .name(SPAWN_DISPLAY_NAME + RESET.getBracketsName() + LIGHT_GRAY.wrap(" (ID: " + WHITE.wrap(SPAWN_ID) + ")"))
        .current("Priority", SPAWN_PRIORITY)
        .current("Permission", SPAWN_PERMISSION_REQUIRED)
        .emptyLine()
        .leftClick("edit")
        .shiftRight("delete " + LIGHT_RED.wrap("(no undo)"))
        .build();
}
