package su.nightexpress.sunlight.command;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.builder.ContentFlagBuilder;
import su.nightexpress.nightcore.command.experimental.builder.SimpleFlagBuilder;
import su.nightexpress.nightcore.command.experimental.flag.FlagTypes;

public class CommandFlags {

    public static final String SILENT = "s";
    public static final String WORLD = "w";

    @NotNull
    public static SimpleFlagBuilder silent() {
        return FlagTypes.simple(SILENT);
    }

    @NotNull
    public static ContentFlagBuilder<World> world() {
        return FlagTypes.world(WORLD);
    }
}
