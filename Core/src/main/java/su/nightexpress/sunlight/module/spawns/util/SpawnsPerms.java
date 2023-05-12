package su.nightexpress.sunlight.module.spawns.util;

import su.nexmedia.engine.api.server.JPermission;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.module.ModuleId;
import su.nightexpress.sunlight.module.spawns.command.*;

public class SpawnsPerms {

    private static final String PREFIX         = Perms.PREFIX + ModuleId.SPAWNS + ".";
    public static final  String PREFIX_SPAWN   = PREFIX + "spawn.";
    private static final String PREFIX_COMMAND = PREFIX + "command.";
    private static final String PREFIX_BYPASS  = PREFIX + "bypass.";

    public static final JPermission MODULE  = new JPermission(PREFIX + Placeholders.WILDCARD, "Access to all Spawns features.");
    public static final JPermission COMMAND = new JPermission(PREFIX_COMMAND + Placeholders.WILDCARD, "Access to all Spawns commands.");
    public static final JPermission BYPASS  = new JPermission(PREFIX_BYPASS + Placeholders.WILDCARD, "Bypass all Spawns restrictions.");
    public static final JPermission SPAWN   = new JPermission(PREFIX_SPAWN + Placeholders.WILDCARD, "Access to all spawns in Spawns module.");

    public static final JPermission COMMAND_SPAWNS                 = new JPermission(PREFIX_COMMAND + SpawnsCommand.NAME, "Access to the '/" + SpawnsCommand.NAME + "' command (without sub-commands).");
    public static final JPermission COMMAND_SPAWNS_CREATE          = new JPermission(PREFIX_COMMAND + SpawnsCreateCommand.NAME, "Access to the '/" + SpawnsCommand.NAME + " " + SpawnsCreateCommand.NAME + "' command.");
    public static final JPermission COMMAND_SPAWNS_DELETE          = new JPermission(PREFIX_COMMAND + SpawnsDeleteCommand.NAME, "Access to the '/" + SpawnsCommand.NAME + " " + SpawnsDeleteCommand.NAME + "' command.");
    public static final JPermission COMMAND_SPAWNS_TELEPORT        = new JPermission(PREFIX_COMMAND + SpawnsTeleportCommand.NAME, "Access to the '/" + SpawnsCommand.NAME + " " + SpawnsTeleportCommand.NAME + "' command.");
    public static final JPermission COMMAND_SPAWNS_TELEPORT_OTHERS = new JPermission(PREFIX_COMMAND + SpawnsTeleportCommand.NAME + ".others", "Access to the '/" + SpawnsCommand.NAME + " " + SpawnsTeleportCommand.NAME + "' command on other players.");
    public static final JPermission COMMAND_SPAWNS_EDITOR          = new JPermission(PREFIX_COMMAND + SpawnsEditorCommand.NAME, "Access to the '/" + SpawnsCommand.NAME + " " + SpawnsEditorCommand.NAME + "' command.");

    static {
        Perms.PLUGIN.addChildren(MODULE);

        MODULE.addChildren(COMMAND, BYPASS);

        COMMAND.addChildren(COMMAND_SPAWNS, COMMAND_SPAWNS_CREATE, COMMAND_SPAWNS_DELETE,
            COMMAND_SPAWNS_EDITOR, COMMAND_SPAWNS_TELEPORT, COMMAND_SPAWNS_TELEPORT_OTHERS);
    }
}
