package su.nightexpress.sunlight.module.worlds.config;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.server.JPermission;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.module.worlds.commands.main.*;

public class WorldsPerms {

    private static final String PREFIX = Perms.PREFIX + "worlds.";
    private static final String PREFIX_COMMAND = PREFIX + "command.";
    private static final String PREFIX_BYPASS = PREFIX + "bypass.";

    @NotNull
    public static JPermission forCommand(@NotNull String name) {
        return new JPermission(PREFIX_COMMAND + name, "Access to the '/" + name + "' command.");
    }

    @NotNull
    public static JPermission forCommand(@NotNull String parent, @NotNull String child) {
        return new JPermission(PREFIX_COMMAND + parent + "." + child, "Access to the '/" + parent + " " + child + "' command.");
    }

    @NotNull
    public static JPermission forCommandOthers(@NotNull String name) {
        return new JPermission(PREFIX_COMMAND + name + ".others", "Access to the '/" + name + "' command on other players.");
    }

    @NotNull
    public static JPermission forCommandOthers(@NotNull String parent, @NotNull String child) {
        return new JPermission(PREFIX_COMMAND + parent + "." + child + ".others", "Access to the '/" + parent + " " + child + "' command on other players.");
    }

    public static final JPermission MODULE  = new JPermission(PREFIX + Placeholders.WILDCARD, "Access to all Worlds module functions.");
    public static final JPermission COMMAND = new JPermission(PREFIX_COMMAND + Placeholders.WILDCARD, "Access to all Worlds module commands.");
    public static final JPermission BYPASS  = new JPermission(PREFIX_BYPASS + Placeholders.WILDCARD, "Bypasses all Worlds module restrictions.");

    public static final JPermission COMMAND_WORLDS        = forCommand(WorldsCommand.NAME);
    public static final JPermission COMMAND_WORLDS_CREATE = forCommand(WorldsCommand.NAME, CreateSubCommand.NAME);
    public static final JPermission COMMAND_WORLDS_DELETE = forCommand(WorldsCommand.NAME, DeleteSubCommand.NAME);
    public static final JPermission COMMAND_WORLDS_LOAD   = forCommand(WorldsCommand.NAME, LoadSubCommand.NAME);
    public static final JPermission COMMAND_WORLDS_UNLOAD = forCommand(WorldsCommand.NAME, UnloadSubCommand.NAME);
    public static final JPermission COMMAND_WORLDS_EDITOR = forCommand(WorldsCommand.NAME, EditorSubCommand.NAME);

    public static final JPermission BYPASS_COMMANDS = new JPermission(PREFIX_BYPASS + "commands", "Bypasses blocked commands in worlds.");
    public static final JPermission BYPASS_FLY      = new JPermission(PREFIX_BYPASS + "fly", "Bypasses world fly restrictions.");

    static {
        Perms.PLUGIN.addChildren(MODULE);

        MODULE.addChildren(COMMAND, BYPASS);

        COMMAND.addChildren(COMMAND_WORLDS, COMMAND_WORLDS_CREATE, COMMAND_WORLDS_DELETE,
            COMMAND_WORLDS_EDITOR, COMMAND_WORLDS_LOAD, COMMAND_WORLDS_UNLOAD);

        BYPASS.addChildren(BYPASS_COMMANDS, BYPASS_FLY);
    }
}
