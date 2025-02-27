package su.nightexpress.sunlight.command;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.mode.ToggleMode;
import su.nightexpress.sunlight.data.user.SunUser;

public class CommandTools {

    @NotNull
    public static ToggleMode getToggleMode(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull String argName) {
        return arguments.getArgument(argName, ToggleMode.class, ToggleMode.TOGGLE);
    }

    @Nullable
    public static World getWorld(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull String argName) {
        if (arguments.hasArgument(argName)) {
            return arguments.getWorldArgument(argName);
        }

        Player player = context.getExecutor();
        if (player == null) {
            context.errorPlayerOnly();
            return null;
        }

        return player.getWorld();
    }

    @Nullable
    public static Player getTarget(@NotNull SunLightPlugin plugin,
                                   @NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull String argName, boolean loadData) {
        String playerName;
        if (arguments.hasArgument(argName)) {
            playerName = arguments.getStringArgument(argName);
        }
        else {
            if (context.getExecutor() == null) {
                context.errorPlayerOnly();
                return null;
            }
            return context.getExecutor();
        }

        Player target = Players.getPlayer(playerName);

        if (target == null && loadData) {
            SunUser user = plugin.getUserManager().getUserData(playerName); // TODO Find only ID and Name
            if (user != null) {
                target = plugin.getSunNMS().loadPlayerData(user.getId(), user.getName());
            }
        }

        if (target == null || (context.getExecutor() != null && !context.getExecutor().canSee(target))) {
            context.errorBadPlayer();
            return null;
        }
        return target;
    }
}
