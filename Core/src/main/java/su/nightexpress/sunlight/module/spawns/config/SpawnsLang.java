package su.nightexpress.sunlight.module.spawns.config;

import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;

import static su.nightexpress.nightcore.util.placeholder.CommonPlaceholders.*;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.module.spawns.SpawnsPlaceholders.*;

public class SpawnsLang implements LangContainer {

    public static final TextLocale COMMAND_SPAWN_DELETE_DESC   = LangEntry.builder("Spawns.Command.Spawns.Delete.Desc").text("Delete a spawn.");
    public static final TextLocale COMMAND_SPAWN_SET_DESC      = LangEntry.builder("Spawns.Command.Spawns.Create.Desc").text("Create a spawn.");
    public static final TextLocale COMMAND_SPAWN_TELEPORT_DESC = LangEntry.builder("Spawns.Command.Spawns.Teleport.Desc").text("Teleport to a spawn.");
    public static final TextLocale COMMAND_SPAWN_EDITOR_DESC   = LangEntry.builder("Spawns.Command.Spawns.Editor.Desc").text("Open spawn editor.");

    public static final MessageLocale COMMAND_SYNTAX_INVALID_SPAWN = LangEntry.builder("Spawns.Command.Syntax.InvalidSpawn").chatMessage(
        GRAY.wrap(RED.wrap(GENERIC_INPUT) + " is not a valid spawn!")
    );


    public static final MessageLocale SPAWN_DELETE_FEEDBACK = LangEntry.builder("Spawns.Command.Spawns.Delete.Done").chatMessage(
        GRAY.wrap("You have deleted " + ORANGE.wrap(SPAWN_NAME) + " spawn.")
    );

    public static final MessageLocale SPAWN_SET_FEEDBACK = LangEntry.builder("Spawns.Command.Spawns.Create.Done").chatMessage(
        GRAY.wrap("You have set " + ORANGE.wrap(SPAWN_NAME) + " spawn.")
    );


    public static final MessageLocale SPAWN_TELEPORT_FEEDBACK = LangEntry.builder("Spawns.Command.Spawns.Teleport.Notify").chatMessage(
        GRAY.wrap("You have teleported " + WHITE.wrap(PLAYER_DISPLAY_NAME) + " to the " + ORANGE.wrap(SPAWN_NAME) + " spawn.")
    );

    public static final MessageLocale SPAWN_TELEPORT_NOTIFY = LangEntry.builder("Spawns.Command.Spawns.Teleport.Done").chatMessage(
        GRAY.wrap("You teleported to the " + ORANGE.wrap(SPAWN_NAME) + " spawn.")
    );


    public static final MessageLocale ERROR_SPAWN_INACTIVE = LangEntry.builder("Spawns.Spawn.Teleport.Error.World").chatMessage(
        GRAY.wrap("This spawn located in a world that is not available currently.")
    );

    public static final MessageLocale ERROR_NO_DEFAULT_SPAWN = LangEntry.builder("Spawns.Command.Spawn.Error.NoDefault").chatMessage(
        GRAY.wrap("You must specify spawn name.")
    );

    public static final TextLocale EDITOR_TITLE_LIST     = LangEntry.builder("Spawns.Editor.Title.List").text(BLACK.wrap("Spawns Editor"));
    public static final TextLocale EDITOR_TITLE_SETTINGS = LangEntry.builder("Spawns.Editor.Title.Settings").text(BLACK.wrap("Spawn Settings"));
}
