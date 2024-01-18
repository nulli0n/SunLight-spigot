package su.nightexpress.sunlight.command.api;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandFlag;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;

import java.util.function.Function;

public abstract class ToggleCommand extends TargetCommand {

    protected static final CommandFlag<Boolean> FLAG_ON = CommandFlag.booleanFlag("on");
    protected static final CommandFlag<Boolean> FLAG_OFF = CommandFlag.booleanFlag("off");

    public ToggleCommand(@NotNull SunLight plugin, @NotNull String[] aliases,
                         @NotNull Permission permission, @NotNull Permission permissionOthers) {
        this(plugin, aliases, permission, permissionOthers, 0);
    }

    public ToggleCommand(@NotNull SunLight plugin, @NotNull String[] aliases,
                         @NotNull Permission permission, @NotNull Permission permissionOthers, int targetIndex) {
        super(plugin, aliases, permission, permissionOthers, targetIndex);
        this.addFlag(FLAG_OFF, FLAG_ON, CommandFlags.SILENT);
    }

    public enum Mode {
        ON(in -> true),
        OFF(in -> false),
        REVERSE(in -> !in);

        private final Function<Boolean, Boolean> function;

        Mode(@NotNull Function<Boolean, Boolean> function) {
            this.function = function;
        }

        public boolean apply(boolean input) {
            return this.function.apply(input);
        }
    }

    @NotNull
    protected Mode getMode(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.hasFlag(FLAG_ON)) return Mode.ON;
        if (result.hasFlag(FLAG_OFF)) return Mode.OFF;
        return Mode.REVERSE;
    }
}
