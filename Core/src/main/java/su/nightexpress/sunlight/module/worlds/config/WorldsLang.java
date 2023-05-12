package su.nightexpress.sunlight.module.worlds.config;

import su.nexmedia.engine.api.lang.LangKey;
import su.nightexpress.sunlight.config.LangColors;
import su.nightexpress.sunlight.module.worlds.util.Placeholders;

public class WorldsLang implements LangColors {

    public static final LangKey COMMAND_WORLDS_DESC = LangKey.of("Worlds.Command.WorldManager.Desc", "World management tools.");

    public static final LangKey COMMAND_WORLDS_CREATE_USAGE = LangKey.of("Worlds.Command.Worlds.Create.Usage", "<name>");
    public static final LangKey COMMAND_WORLDS_CREATE_DESC  = LangKey.of("Worlds.Command.Worlds.Create.Desc", "Create a new world config.");
    public static final LangKey COMMAND_WORLDS_CREATE_ERROR = LangKey.of("Worlds.Command.Worlds.Create.Error", RED + "World with this name already exists!");
    public static final LangKey COMMAND_WORLDS_CREATE_DONE  = LangKey.of("Worlds.Command.Worlds.Create.Done", LIGHT_YELLOW + "Created world config: " + ORANGE + Placeholders.WORLD_ID + LIGHT_YELLOW + "!");

    public static final LangKey COMMAND_WORLDS_DELETE_USAGE = LangKey.of("Worlds.Command.Worlds.Delete.Usage", "<world> [-f]");
    public static final LangKey COMMAND_WORLDS_DELETE_DESC  = LangKey.of("Worlds.Command.Worlds.Delete.Desc", "Delete specified world cofig [and world folder].");
    public static final LangKey COMMAND_WORLDS_DELETE_ERROR = LangKey.of("Worlds.Command.Worlds.Delete.Error", RED + "Could not delete world " + ORANGE + Placeholders.WORLD_ID + RED + "! World is loaded or file access error.");
    public static final LangKey COMMAND_WORLDS_DELETE_DONE  = LangKey.of("Worlds.Command.Worlds.Delete.Done", LIGHT_YELLOW + "Deleted " + ORANGE + Placeholders.WORLD_ID + LIGHT_YELLOW + " world!");

    public static final LangKey COMMAND_WORLDS_EDITOR_DESC = LangKey.of("Worlds.Command.Worlds.Editor.Desc", "Open world editor.");

    public static final LangKey COMMAND_WORLDS_LOAD_USAGE = LangKey.of("Worlds.Command.Worlds.Load.Usage", "<world>");
    public static final LangKey COMMAND_WORLDS_LOAD_DESC  = LangKey.of("Worlds.Command.Worlds.Load.Desc", "Load specified world into the server.");
    public static final LangKey COMMAND_WORLDS_LOAD_ERROR = LangKey.of("Worlds.Command.Worlds.Load.Error", RED + "World is already loaded or world settings are invalid!");
    public static final LangKey COMMAND_WORLDS_LOAD_DONE  = LangKey.of("Worlds.Command.Worlds.Load.Done", LIGHT_YELLOW + "Loaded world: " + ORANGE + Placeholders.WORLD_ID + LIGHT_YELLOW + "!");

    public static final LangKey COMMAND_WORLDS_UNLOAD_USAGE = LangKey.of("Worlds.Command.Worlds.Unload.Usage", "<world>");
    public static final LangKey COMMAND_WORLDS_UNLOAD_DESC  = LangKey.of("Worlds.Command.Worlds.Unload.Desc", "Unload specified world from the server.");
    public static final LangKey COMMAND_WORLDS_UNLOAD_ERROR = LangKey.of("Worlds.Command.Worlds.Unload.Error", RED + "World is already unloaded!");
    public static final LangKey COMMAND_WORLDS_UNLOAD_DONE  = LangKey.of("Worlds.Command.Worlds.Unload.Done", LIGHT_YELLOW + "Unloaded world: " + ORANGE + Placeholders.WORLD_ID + LIGHT_YELLOW + "!");

    public static final LangKey WORLDS_ERROR_COMMAND_BLOCKED = LangKey.of("Worlds.Error.CommandBlocked", RED + "You can't use that command in this world!");
    public static final LangKey WORLDS_ERROR_FLY_DISABLED = LangKey.of("Worlds.Error.FlyDisabled", RED + "Flying is not allowed in this world!");

    public static final LangKey EDITOR_ENTER_VALUE     = LangKey.of("Worlds.Editor.Enter.Value", GRAY + "Enter " + GREEN + "[Value]");
    public static final LangKey EDITOR_ENTER_SPAWNS    = LangKey.of("Worlds.Editor.Enter.Spawns", GRAY + "Enter " + GREEN + "[Category] [Value]");
    public static final LangKey EDITOR_ENTER_SECONDS   = LangKey.of("Worlds.Editor.Enter.Seconds", GRAY + "Enter " + GREEN + "[Seconds Amount]");
    public static final LangKey EDITOR_ENTER_GENERATOR = LangKey.of("Worlds.Editor.Enter.Generator", GRAY + "Enter " + GREEN + "[Generator Name]");

}
