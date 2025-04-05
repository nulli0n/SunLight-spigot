package su.nightexpress.sunlight.module.worlds.config;

import org.bukkit.Difficulty;
import su.nightexpress.nightcore.language.entry.LangEnum;
import su.nightexpress.nightcore.language.entry.LangItem;
import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.entry.LangText;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.worlds.util.Placeholders;

import static su.nightexpress.nightcore.language.entry.LangItem.builder;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.sunlight.module.worlds.util.Placeholders.*;

public class WorldsLang extends Lang {

    public static final LangEnum<Difficulty> DIFFICULTY = LangEnum.of("Worlds.Difficulty", Difficulty.class);


    public static final LangString COMMAND_CREATE_WORLD_DESC = LangString.of("Worlds.Command.Worlds.Create.Desc",
        "Create a new world.");

    public static final LangText COMMAND_CREATE_WORLD_ERROR = LangText.of("Worlds.Command.Worlds.Create.Error",
        LIGHT_RED.wrap("World with such name already exists!")
    );

    public static final LangText COMMAND_CREATE_WORLD_DONE = LangText.of("Worlds.Command.Worlds.Create.Done",
        LIGHT_GRAY.wrap("Created world data: " + LIGHT_YELLOW.wrap(WORLD_ID) + "!")
    );


    public static final LangString COMMAND_DELETE_WORLD_DESC = LangString.of("Worlds.Command.Worlds.Delete.Desc",
        "Delete a custom world.");

    public static final LangText COMMAND_DELETE_WORLD_ERROR = LangText.of("Worlds.Command.Worlds.Delete.Error",
        LIGHT_GRAY.wrap("Could not delete world: " + LIGHT_RED.wrap(WORLD_ID) + "!")
    );

    public static final LangText COMMAND_DELETE_WORLD_DONE = LangText.of("Worlds.Command.Worlds.Delete.Done",
        LIGHT_GRAY.wrap("World deleted: " + LIGHT_YELLOW.wrap(WORLD_ID) + "!")
    );


    public static final LangString COMMAND_EDITOR_DESC = LangString.of("Worlds.Command.Worlds.Editor.Desc", "Open world editor.");


    public static final LangString COMMAND_LOAD_WORLD_DESC = LangString.of("Worlds.Command.Worlds.Load.Desc",
        "Load a custom world.");

    public static final LangText COMMAND_LOAD_WORLD_ERROR = LangText.of("Worlds.Command.Worlds.Load.Error",
        LIGHT_GRAY.wrap("Could not load world: " + LIGHT_RED.wrap(WORLD_ID) + "!")
    );

    public static final LangText COMMAND_LOAD_WORLD_DONE = LangText.of("Worlds.Command.Worlds.Load.Done",
        LIGHT_GRAY.wrap("World loaded: " + LIGHT_YELLOW.wrap(WORLD_ID) + "!")
    );


    public static final LangString COMMAND_UNLOAD_WORLD_DESC = LangString.of("Worlds.Command.Worlds.Unload.Desc",
        "Unload a custom world.");

    public static final LangText COMMAND_UNLOAD_WORLD_ERROR = LangText.of("Worlds.Command.Worlds.Unload.Error",
        LIGHT_GRAY.wrap("Could not unload world: " + LIGHT_RED.wrap(WORLD_ID) + "!")
    );

    public static final LangText COMMAND_UNLOAD_WORLD_DONE = LangText.of("Worlds.Command.Worlds.Unload.Done",
        LIGHT_GRAY.wrap("World unloaded: " + LIGHT_YELLOW.wrap(WORLD_ID) + "!")
    );


    public static final LangText UNLOAD_MOVE_OUT_INFO = LangText.of("Worlds.AutoWipe.MoveOut",
        LIGHT_GRAY.wrap("You have been teleported due to world unload.")
    );


    public static final LangText AUTO_RESET_NOTIFY = LangText.of("Worlds.AutoWipe.Notify",
        TAG_NO_PREFIX,
        "",
        LIGHT_RED.wrap(BOLD.wrap("Auto-Reset Warning:")),
        LIGHT_GRAY.wrap("The world will auto-reset in " + LIGHT_RED.wrap(GENERIC_TIME)),
        ""
    );

    public static final LangText ERROR_COMMAND_BLOCKED = LangText.of("Worlds.Error.CommandBlocked",
        LIGHT_RED.wrap("You can't use that command in this world!")
    );

    public static final LangText ERROR_FLY_DISABLED = LangText.of("Worlds.Error.FlyDisabled",
        LIGHT_RED.wrap("Flying is not allowed in this world!")
    );

    public static final LangText ERROR_COMMAND_INVALID_WORLD_DATA_ARGUMENT = LangText.of("Worlds.Error.Command.Argument.InvalidWorldData",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(GENERIC_VALUE) + " is not a valid world!")
    );

    public static final LangString EDITOR_TITLE_LIST = LangString.of("Worlds.Editor.Title.List", BLACK.wrap("World List"));

    public static final LangString EDITOR_TITLE_SETTINGS = LangString.of("Worlds.Editor.Title.Settings", BLACK.wrap("World Settings"));

    public static final LangString EDITOR_TITLE_GENERATION = LangString.of("Worlds.Editor.Title.Generation", BLACK.wrap("World Loading/Generation"));

    public static final LangString EDITOR_TITLE_GAME_RULES = LangString.of("Worlds.Editor.Title.GameRules", BLACK.wrap("World Game Rules"));


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
