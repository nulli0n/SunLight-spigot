package su.nightexpress.sunlight.module.worlds.config;

import org.bukkit.Difficulty;
import su.nightexpress.nightcore.language.entry.LangItem;
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.EnumLocale;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.locale.message.MessageData;
import su.nightexpress.sunlight.module.worlds.util.Placeholders;

import static su.nightexpress.nightcore.language.entry.LangItem.builder;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.sunlight.module.worlds.util.Placeholders.*;

public class WorldsLang implements LangContainer {

    public static final EnumLocale<Difficulty> DIFFICULTY = LangEntry.builder("Worlds.Difficulty").enumeration(Difficulty.class);


    public static final TextLocale COMMAND_WORLDS_ROOT_DESC = LangEntry.builder("Worlds.Command.Worlds.Root.Desc").text("World Management.");

    public static final TextLocale COMMAND_CREATE_WORLD_DESC = LangEntry.builder("Worlds.Command.Worlds.Create.Desc").text(
        "Create a new world.");

    public static final MessageLocale COMMAND_CREATE_WORLD_ERROR = LangEntry.builder("Worlds.Command.Worlds.Create.Error").chatMessage(
        LIGHT_RED.wrap("World with such name already exists!")
    );

    public static final MessageLocale COMMAND_CREATE_WORLD_DONE = LangEntry.builder("Worlds.Command.Worlds.Create.Done").chatMessage(
        LIGHT_GRAY.wrap("Created world data: " + LIGHT_YELLOW.wrap(WORLD_ID) + "!")
    );


    public static final TextLocale COMMAND_DELETE_WORLD_DESC = LangEntry.builder("Worlds.Command.Worlds.Delete.Desc").text(
        "Delete a custom world.");

    public static final MessageLocale COMMAND_DELETE_WORLD_ERROR = LangEntry.builder("Worlds.Command.Worlds.Delete.Error").chatMessage(
        LIGHT_GRAY.wrap("Could not delete world: " + LIGHT_RED.wrap(WORLD_ID) + "!")
    );

    public static final MessageLocale COMMAND_DELETE_WORLD_DONE = LangEntry.builder("Worlds.Command.Worlds.Delete.Done").chatMessage(
        LIGHT_GRAY.wrap("World deleted: " + LIGHT_YELLOW.wrap(WORLD_ID) + "!")
    );


    public static final TextLocale COMMAND_EDITOR_DESC = LangEntry.builder("Worlds.Command.Worlds.Editor.Desc").text("Open world editor.");


    public static final TextLocale COMMAND_LOAD_WORLD_DESC = LangEntry.builder("Worlds.Command.Worlds.Load.Desc").text(
        "Load a custom world.");

    public static final MessageLocale COMMAND_LOAD_WORLD_ERROR = LangEntry.builder("Worlds.Command.Worlds.Load.Error").chatMessage(
        LIGHT_GRAY.wrap("Could not load world: " + LIGHT_RED.wrap(WORLD_ID) + "!")
    );

    public static final MessageLocale COMMAND_LOAD_WORLD_DONE = LangEntry.builder("Worlds.Command.Worlds.Load.Done").chatMessage(
        LIGHT_GRAY.wrap("World loaded: " + LIGHT_YELLOW.wrap(WORLD_ID) + "!")
    );


    public static final TextLocale COMMAND_UNLOAD_WORLD_DESC = LangEntry.builder("Worlds.Command.Worlds.Unload.Desc").text("Unload a custom world.");

    public static final MessageLocale COMMAND_UNLOAD_WORLD_ERROR = LangEntry.builder("Worlds.Command.Worlds.Unload.Error").chatMessage(
        LIGHT_GRAY.wrap("Could not unload world: " + LIGHT_RED.wrap(WORLD_ID) + "!")
    );

    public static final MessageLocale COMMAND_UNLOAD_WORLD_DONE = LangEntry.builder("Worlds.Command.Worlds.Unload.Done").chatMessage(
        LIGHT_GRAY.wrap("World unloaded: " + LIGHT_YELLOW.wrap(WORLD_ID) + "!")
    );


    public static final MessageLocale UNLOAD_MOVE_OUT_INFO = LangEntry.builder("Worlds.AutoWipe.MoveOut").chatMessage(
        LIGHT_GRAY.wrap("You have been teleported due to world unload.")
    );


    public static final MessageLocale AUTO_RESET_NOTIFY = LangEntry.builder("Worlds.AutoWipe.Notify").message(
        MessageData.CHAT_NO_PREFIX,
        "",
        LIGHT_RED.wrap(BOLD.wrap("Auto-Reset Warning:")),
        LIGHT_GRAY.wrap("The world will auto-reset in " + LIGHT_RED.wrap(GENERIC_TIME)),
        ""
    );

    public static final MessageLocale ERROR_COMMAND_BLOCKED = LangEntry.builder("Worlds.Error.CommandBlocked").chatMessage(
        LIGHT_RED.wrap("You can't use that command in this world!")
    );

    public static final MessageLocale ERROR_FLY_DISABLED = LangEntry.builder("Worlds.Error.FlyDisabled").chatMessage(
        LIGHT_RED.wrap("Flying is not allowed in this world!")
    );

    public static final MessageLocale ERROR_COMMAND_INVALID_WORLD_DATA_ARGUMENT = LangEntry.builder("Worlds.Error.Command.Argument.InvalidWorldData").chatMessage(
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(GENERIC_VALUE) + " is not a valid world!")
    );

    public static final TextLocale EDITOR_TITLE_LIST = LangEntry.builder("Worlds.Editor.Title.List").text(BLACK.wrap("World List"));

    public static final TextLocale EDITOR_TITLE_SETTINGS = LangEntry.builder("Worlds.Editor.Title.Settings").text(BLACK.wrap("World Settings"));

    public static final TextLocale EDITOR_TITLE_GENERATION = LangEntry.builder("Worlds.Editor.Title.Generation").text(BLACK.wrap("World Loading/Generation"));

    public static final TextLocale EDITOR_TITLE_GAME_RULES = LangEntry.builder("Worlds.Editor.Title.GameRules").text(BLACK.wrap("World Game Rules"));


    private static final String PREFIX = "Worlds.Editor.";

    public static final LangItem EDITOR_WORLD_RULE_OBJECT = builder(PREFIX + "World.Rule.Object")
        .name("Rule: " + RESET.getBracketsName() + WHITE.wrap(GENERIC_NAME))
        .current("Value", GENERIC_VALUE)
        .emptyLine()
        .click("change")
        .build();

    public static final LangItem EDITOR_WORLD_DIFFICULTY = builder(PREFIX + "World.Difficulty")
        .name("Difficulty")
        .current("Current", Placeholders.WORLD_DIFFICULTY)
        .emptyLine()
        .text("Sets world's difficulty.")
        .emptyLine()
        .click("toggle")
        .build();

    public static final LangItem EDITOR_WORLD_AUTO_WIPE = builder(PREFIX + "World.AutoWipe")
        .name("Auto Reset")
        .text("Sets whether or not this world", "should auto reset with", "certain interval.")
        .emptyLine()
        .current("Enabled", WORLD_AUTO_RESET_ENABLED)
        .current("Interval", WORLD_AUTO_RESET_INTERVAL)
        .current("Last Reset", WORLD_LAST_RESET_DATE)
        .current("Next Reset", WORLD_NEXT_RESET_DATE)
        .emptyLine()
        .leftClick("toggle")
        .rightClick("change interval")
        .dropKey("set last time")
        .build();

    public static final LangItem EDITOR_WORLD_GAME_RULES = builder(PREFIX + "World.GameRules")
        .name("Game Rules")
        .emptyLine()
        .click("navigate")
        .build();

    public static final LangItem EDITOR_WORLD_SET_STRUCTURES = builder(PREFIX + "World.Structures")
        .name("Structure Generation")
        .current("Enabled", WORLD_STRUCTURES)
        .emptyLine()
        .click("toggle")
        .build();

    public static final LangItem EDITOR_WORLD_SET_ENVIRONMENT = builder(PREFIX + "World.Environment")
        .name("Environment")
        .current("Current", WORLD_ENVIRONMENT)
        .emptyLine()
        .click("toggle")
        .build();

    public static final LangItem EDITOR_WORLD_SET_GENERATOR = builder(PREFIX + "World.Generator")
        .name("Chunk Generator")
        .current("Current", WORLD_GENERATOR)
        .emptyLine()
        .click("change")
        .build();

    public static final LangItem EDITOR_WORLD_DELETE = builder(PREFIX + "World.Delete")
        .name("Delete Data")
        .text(LIGHT_RED.wrap("(No Undo)"))
        .emptyLine()
        .dropKey("delete world files")
        .rightClick("delete config")
        .shiftRight("delete both")
        .build();

    public static final LangItem EDITOR_WORLD_UNLOAD = builder(PREFIX + "World.Unload")
        .name("Unload World")
        .build();

    public static final LangItem EDITOR_WORLD_LOAD = builder(PREFIX + "World.Load")
        .name("Load World")
        .build();

    public static final LangItem EDITOR_WORLD_AUTO_LOAD = builder(PREFIX + "World.AutoLoad")
        .name("Auto-Load")
        .current("Enabled", WORLD_IS_AUTO_LOAD)
        .emptyLine()
        .text("Loads the world on server startup.")
        .emptyLine()
        .click("toggle")
        .build();

    public static final LangItem EDITOR_WORLD_OBJECT = builder(PREFIX + "World.Object")
        .name("World: " + RESET.getBracketsName() + WHITE.wrap(WORLD_ID))
        .current("Created", WORLD_IS_CREATED)
        .current("Loaded", WORLD_IS_LOADED)
        .current("Auto-Load", WORLD_IS_AUTO_LOAD)
        .emptyLine()
        .click("edit")
        .build();
}
