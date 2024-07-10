package su.nightexpress.sunlight.module.warps.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.RootCommand;
import su.nightexpress.nightcore.command.experimental.ServerCommand;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.module.warps.config.WarpsLang;
import su.nightexpress.sunlight.module.warps.impl.Warp;

public class WarpShortcutCommand {

    public static void register(@NotNull SunLightPlugin plugin, @NotNull Warp warp) {
        //module.getWarps().forEach(warp -> {
            String shortcut = warp.getCommandShortcut();
            if (shortcut == null) return;

            ServerCommand command = RootCommand.direct(plugin, warp.getCommandShortcut(), builder -> builder
                .playerOnly()
                .description(warp.replacePlaceholders().apply(WarpsLang.COMMAND_DIRECT_WARP_DESC.getString()))
                .permission(warp.getPermission())
                .executes((context, arguments) -> execute(plugin, warp.getModule(), context, arguments, warp.getId()))
            );
            plugin.getCommandManager().registerCommand(command);
        //});
    }

    public static void unregister(@NotNull SunLightPlugin plugin, @NotNull Warp warp) {
        String shortcut = warp.getCommandShortcut();
        if (shortcut == null) return;

        plugin.getCommandManager().unregisterServerCommand(shortcut);
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull WarpsModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments,
                                  @NotNull String id) {
        Player player = context.getExecutor();
        if (player == null) return false;

        Warp warp = module.getWarp(id);
        if (warp == null) return false;

        warp.teleport(player, false);
        return true;
    }
}
