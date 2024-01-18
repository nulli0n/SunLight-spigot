package su.nightexpress.sunlight.command;

import org.bukkit.World;
import su.nexmedia.engine.api.command.CommandFlag;

public class CommandFlags {

    public static final CommandFlag<World> WORLD = CommandFlag.worldFlag("w");
    public static final CommandFlag<Boolean> SILENT = CommandFlag.booleanFlag("s");
    public static final CommandFlag<Boolean> ALL = CommandFlag.booleanFlag("all");

}
