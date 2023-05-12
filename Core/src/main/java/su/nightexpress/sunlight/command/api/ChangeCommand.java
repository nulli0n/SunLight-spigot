package su.nightexpress.sunlight.command.api;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.SunLight;

import java.util.List;
import java.util.function.BiFunction;

public abstract class ChangeCommand extends TargetCommand {

    public ChangeCommand(@NotNull SunLight plugin, @NotNull String[] aliases,
                         @NotNull Permission permission, @NotNull Permission permissionOthers) {
        super(plugin, aliases, permission, permissionOthers, 2);
    }

    public enum Mode {
        ADD(Integer::sum),
        SET((in, amount) -> amount),
        REMOVE((in, amount) -> in - amount);

        private final BiFunction<Integer, Integer, Integer> function;

        Mode(@NotNull BiFunction<Integer, Integer, Integer> function) {
            this.function = function;
        }

        public int modify(int input, int amount) {
            return this.function.apply(input, amount);
        }
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return CollectionsUtil.getEnumsList(Mode.class).stream().map(String::toLowerCase).toList();
        }
        if (arg == 2) {
            return this.getTab(player);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        Mode mode = StringUtil.getEnum(result.getArg(0), Mode.class).orElse(Mode.SET);
        double amount = result.getDouble(1, 0);
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        this.run(sender, result, mode, target, amount);
    }

    protected abstract List<String> getTab(@NotNull Player player);

    protected abstract void run(
        @NotNull CommandSender sender,
        @NotNull CommandResult result,
        @NotNull Mode mode, @NotNull Player target, double amount);
}
