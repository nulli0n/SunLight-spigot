package su.nightexpress.sunlight.command.list;

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.TargetCommand;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.utils.SunUtils;

import java.util.List;
import java.util.stream.Stream;

public class GamemodeCommand extends TargetCommand {

    public static final String NAME = "gamemode";

    public GamemodeCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_GAMEMODE, Perms.COMMAND_GAMEMODE_OTHERS, 1);
        this.setAllowDataLoad();
        this.setDescription(plugin.getMessage(Lang.COMMAND_GAME_MODE_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_GAME_MODE_USAGE));
        this.addFlag(CommandFlags.SILENT);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return Stream.of(GameMode.values()).filter(mode -> this.hasPermission(player, mode)).map(Enum::name).map(String::toLowerCase).toList();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        GameMode mode = StringUtil.getEnum(result.getArg(0), GameMode.class).orElse(GameMode.SURVIVAL);
        if (!this.hasPermission(sender, mode)) {
            this.errorPermission(sender);
            return;
        }

        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        SunUtils.setGameMode(target, mode);

        if (sender != target) {
            plugin.getMessage(Lang.COMMAND_GAME_MODE_TARGET)
                .replace(Placeholders.Player.replacer(target))
                .replace(Placeholders.GENERIC_TYPE, plugin.getLangManager().getEnum(mode))
                .send(sender);
        }
        if (!result.hasFlag(CommandFlags.SILENT)) {
            plugin.getMessage(Lang.COMMAND_GAME_MODE_NOTIFY)
                .replace(Placeholders.GENERIC_TYPE, plugin.getLangManager().getEnum(mode))
                .send(target);
        }
    }

    private boolean hasPermission(@NotNull CommandSender sender, @NotNull GameMode mode) {
        return sender.hasPermission(Perms.COMMAND_GAMEMODE.getName() + "." + mode.name().toLowerCase());
    }
}
