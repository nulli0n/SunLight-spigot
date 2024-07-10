package su.nightexpress.sunlight.command;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.CommandFlag;
import su.nightexpress.nightcore.command.experimental.builder.ContentFlagBuilder;
import su.nightexpress.nightcore.command.experimental.builder.FlagBuilder;
import su.nightexpress.nightcore.command.experimental.builder.SimpleFlagBuilder;
import su.nightexpress.nightcore.command.experimental.flag.ContentFlag;
import su.nightexpress.nightcore.command.experimental.flag.FlagTypes;

public class CommandFlags {

    public static final String SILENT = "s";
    public static final String WORLD = "w";

    @Deprecated public static final CommandFlag<World>   WORLD_LEGACY  = CommandFlag.worldFlag("w");
    @Deprecated public static final CommandFlag<Boolean> SILENT_LEGACY = CommandFlag.booleanFlag("s");
    @Deprecated public static final CommandFlag<Boolean> ALL_LEGACY    = CommandFlag.booleanFlag("all");

    @NotNull
    public static SimpleFlagBuilder silent() {
        return FlagTypes.simple(SILENT);
    }

    @NotNull
    public static ContentFlagBuilder<World> world() {
        return FlagTypes.world(WORLD);
    }
}
