package su.nightexpress.sunlight.module.spawns.config;

import su.nexmedia.engine.api.lang.LangKey;
import su.nightexpress.sunlight.config.LangColors;
import su.nightexpress.sunlight.module.spawns.command.SpawnsCommand;
import su.nightexpress.sunlight.module.spawns.command.SpawnsEditorCommand;
import su.nightexpress.sunlight.module.spawns.util.Placeholders;

public class SpawnsLang implements LangColors {

    public static final LangKey COMMAND_SPAWNS_DESC = LangKey.of("Spawns.Command.Spawns.Desc", "Spawn management.");
    public static final LangKey COMMAND_SPAWNS_USAGE = LangKey.of("Spawns.Command.Spawns.Usage", "[help]");
    
    public static final LangKey COMMAND_SPAWNS_DELETE_DESC  = LangKey.of("Spawns.Command.Spawns.Delete.Desc", "Delete specified spawn.");
    public static final LangKey COMMAND_SPAWNS_DELETE_USAGE = LangKey.of("Spawns.Command.Spawns.Delete.Usage", "<name>");
    public static final LangKey COMMAND_SPAWNS_DELETE_DONE  = LangKey.of("Spawns.Command.Spawns.Delete.Done",
        "<! type:\"titles:20:50:20\" !>" +
        "\n" + RED + "&lSpawn Deleted" +
        "\n" + GRAY + "You deleted " + RED + Placeholders.SPAWN_NAME + GRAY + " spawn!");
    
    public static final LangKey COMMAND_SPAWNS_CREATE_DESC  = LangKey.of("Spawns.Command.Spawns.Create.Desc", "Create spawn point.");
    public static final LangKey COMMAND_SPAWNS_CREATE_USAGE = LangKey.of("Spawns.Command.Spawns.Create.Usage", "[name]");
    public static final LangKey COMMAND_SPAWNS_CREATE_DONE  = LangKey.of("Spawns.Command.Spawns.Create.Done",
        "<! type:\"titles:20:50:20\" !>" +
            "\n" + GREEN + "&lSpawn Set!" +
            "\n" + GRAY + "Spawn Id: " + GREEN + Placeholders.SPAWN_ID + GRAY + " | Editor: " + GREEN + "/" + SpawnsCommand.NAME + " " + SpawnsEditorCommand.NAME);
    
    public static final LangKey COMMAND_SPAWNS_TELEPORT_DESC  = LangKey.of("Spawns.Command.Spawns.Teleport.Desc", "Teleport on specified spawn.");
    public static final LangKey COMMAND_SPAWNS_TELEPORT_USAGE  = LangKey.of("Spawns.Command.Spawns.Teleport.Usage", "[spawn] [player]");
    public static final LangKey COMMAND_SPAWNS_TELEPORT_NOTIFY = LangKey.of("Spawns.Command.Spawns.Teleport.Notify", GRAY + "Teleporting " + YELLOW + Placeholders.Player.DISPLAY_NAME + GRAY + " on " + YELLOW + Placeholders.SPAWN_ID + GRAY + " spawn.");

    public static final LangKey COMMAND_SPAWNS_EDITOR_DESC = LangKey.of("Spawns.Command.Spawns.Editor.Desc", "Open Spawns editor.");
    
    public static final LangKey SPAWN_ERROR_INVALID = LangKey.of("Spawns.Spawn.Error.Invalid", RED + "Spawn does not exist.");
    public static final LangKey SPAWN_TELEPORT_DONE            = LangKey.of("Spawns.Command.Spawns.Teleport.Done",
        "<! type:\"titles:20:60:20\" !>" +
            "\n" + YELLOW + "&lSpawn" +
            "\n" + GRAY + "You teleported on the " + YELLOW + Placeholders.SPAWN_NAME + GRAY + " spawn.");

    public static final LangKey SPAWNS_EDITOR_ENTER_NAME     = LangKey.of("Spawns.Editor.Enter.Name", GRAY + "Enter " + GREEN + "[Spawn Name]");
    public static final LangKey SPAWNS_EDITOR_ENTER_PRIORITY = LangKey.of("Spawns.Editor.Enter.Priority", GRAY + "Enter " + GREEN + "[Priority]");
    public static final LangKey SPAWNS_EDITOR_ENTER_GROUP    = LangKey.of("Spawns.Editor.Enter.Group", GRAY + "Enter " + GREEN + "[Group Name]");
}
