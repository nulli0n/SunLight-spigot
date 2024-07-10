package su.nightexpress.sunlight.command.list;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.*;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.config.Lang;

import java.util.function.Function;

public class GameModeCommand {

    private static final Function<GameMode, String> NODE_MODE = mode -> "gamemode_" + mode.name().toLowerCase();

    public static void load(@NotNull SunLightPlugin plugin) {
        for (GameMode mode : GameMode.values()) {
            CommandRegistry.registerDirectExecutor(NODE_MODE.apply(mode), (template, config) -> builderType(plugin, template, config, mode));
        }

        CommandRegistry.addTemplate("gamemode", CommandTemplate.group(new String[]{"gamemode", "gm"},
            "Gamemode commands.",
            CommandPerms.PREFIX + "gamemode",
            CommandTemplate.direct(new String[]{"adventure"}, NODE_MODE.apply(GameMode.ADVENTURE)),
            CommandTemplate.direct(new String[]{"survival"}, NODE_MODE.apply(GameMode.SURVIVAL)),
            CommandTemplate.direct(new String[]{"spectator"}, NODE_MODE.apply(GameMode.SPECTATOR)),
            CommandTemplate.direct(new String[]{"creative"}, NODE_MODE.apply(GameMode.CREATIVE))
        ));
        for (GameMode mode : GameMode.values()) {
            String shortcut = switch (mode) {
                case CREATIVE -> "gmc";
                case SURVIVAL -> "gms";
                case ADVENTURE -> "gma";
                case SPECTATOR -> "gmsp";
            };

            CommandRegistry.addTemplate("gamemode_" + mode.name().toLowerCase(), CommandTemplate.direct(new String[]{shortcut}, NODE_MODE.apply(mode)));
        }
    }

    public static DirectNodeBuilder builderType(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config, @NotNull GameMode mode) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_GAME_MODE_TYPE_DESC.getString().replace(Placeholders.GENERIC_TYPE, Lang.GAME_MODE.getLocalized(mode)))
            .permission(CommandPerms.GAMEMODE_TYPE.apply(mode))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(CommandPerms.GAMEMODE_TYPE_OTHERS.apply(mode)))
            .withFlag(CommandFlags.silent().permission(CommandPerms.GAMEMODE_TYPE_OTHERS.apply(mode)))
            .executes((context, arguments) -> executeType(plugin, context, arguments, mode))
            ;
    }

    public static boolean executeType(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull GameMode mode) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        setGameMode(plugin, target, mode);

        if (context.getSender() != target) {
            Lang.COMMAND_GAME_MODE_TARGET.getMessage()
                .replace(Placeholders.forPlayer(target))
                .replace(Placeholders.GENERIC_TYPE, Lang.GAME_MODE.getLocalized(mode))
                .send(context.getSender());
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            Lang.COMMAND_GAME_MODE_NOTIFY.getMessage()
                .replace(Placeholders.GENERIC_TYPE, Lang.GAME_MODE.getLocalized(mode))
                .send(target);
        }

        return true;
    }

    public static void setGameMode(@NotNull SunLightPlugin plugin, @NotNull Player player, @NotNull GameMode mode) {
        if (player.isOnline()) {
            player.setGameMode(mode);
            return;
        }
        plugin.getSunNMS().setGameMode(player, mode);
    }
}
