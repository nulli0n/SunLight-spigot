package su.nightexpress.sunlight.command.ignore;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.data.impl.IgnoredUser;

import java.util.List;

public class IgnoreRemoveCommand extends AbstractCommand<SunLight> {

    public static final String NAME = "remove";

    public IgnoreRemoveCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_IGNORE);
        this.setDescription(plugin.getMessage(Lang.COMMAND_IGNORE_REMOVE_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_IGNORE_REMOVE_USAGE));
        this.setPlayerOnly(true);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            SunUser user = plugin.getUserManager().getUserData(player);
            return user.getIgnoredUsers().values().stream().map(is -> is.getUserInfo().getName()).toList();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        String userName = result.getArg(1);

        Player player = (Player) sender;
        SunUser user = plugin.getUserManager().getUserData(player);

        IgnoredUser ignoredUser = user.getIgnoredUser(userName);
        if (ignoredUser == null) {
            plugin.getMessage(Lang.COMMAND_IGNORE_REMOVE_ERROR_NOT_IN).send(sender);
            return;
        }

        if (user.removeIgnoredUser(ignoredUser.getUserInfo().getId())) {
            user.saveData(this.plugin);
        }

        plugin.getMessage(Lang.COMMAND_IGNORE_REMOVE_DONE)
            .replace(Placeholders.Player.NAME, ignoredUser.getUserInfo().getName())
            .send(sender);
    }
}
