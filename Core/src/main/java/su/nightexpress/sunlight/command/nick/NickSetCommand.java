package su.nightexpress.sunlight.command.nick;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.Colorizer;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.SunUser;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NickSetCommand extends AbstractCommand<SunLight> {

    public static final String NAME = "set";

    public NickSetCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_NICK_SET);
        this.setDescription(plugin.getMessage(Lang.COMMAND_NICK_SET_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_NICK_SET_USAGE));
        this.setPlayerOnly(true);
        this.addFlag(CommandFlags.SILENT);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return CollectionsUtil.playerNames(player);
        }
        if (arg == 2) {
            return Collections.singletonList("<nick>");
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 3) {
            this.printUsage(sender);
            return;
        }

        SunUser user = plugin.getUserManager().getUserData(result.getArg(1));
        if (user == null) {
            this.errorPlayer(sender);
            return;
        }

        Player target = user.getPlayer();

        String nick = Stream.of(result.getArgs()).skip(2).collect(Collectors.joining(" "));
        if (sender.hasPermission(Perms.COMMAND_NICK_COLORS)) {
            nick = Colorizer.apply(nick);
        }
        else nick = Colorizer.restrip(nick);

        user.setCustomName(nick);
        user.updatePlayerName();
        this.plugin.getUserManager().saveUser(user);

        if (sender != user.getPlayer()) {
            plugin.getMessage(Lang.COMMAND_NICK_SET_TARGET)
                .replace(Placeholders.GENERIC_NAME, nick)
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .send(sender);
        }
        if (!result.hasFlag(CommandFlags.SILENT) && target != null) {
            plugin.getMessage(Lang.COMMAND_NICK_SET_NOTIFY)
                .replace(Placeholders.GENERIC_NAME, nick)
                .send(target);
        }
    }
}
