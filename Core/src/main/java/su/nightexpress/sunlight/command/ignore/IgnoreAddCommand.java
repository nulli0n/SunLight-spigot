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

public class IgnoreAddCommand extends AbstractCommand<SunLight> {

    public static final String NAME = "add";

    public IgnoreAddCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_IGNORE);
        this.setDescription(plugin.getMessage(Lang.COMMAND_IGNORE_ADD_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_IGNORE_ADD_USAGE));
        this.setPlayerOnly(true);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        Player player = (Player) sender;
        SunUser user = plugin.getUserManager().getUserData(player);

        // Check if target player is online.
        SunUser userTarget = plugin.getUserManager().getUserData(result.getArg(1));
        if (userTarget == null) {
            this.errorPlayer(sender);
            return;
        }

        // Check if player trying to block himself.
        if (user == userTarget) {
            plugin.getMessage(Lang.ERROR_COMMAND_SELF).send(sender);
            return;
        }

        // Check if target player is already blocked.
        if (!user.addIgnoredUser(userTarget)) {
            plugin.getMessage(Lang.COMMAND_IGNORE_ADD_ERROR_ALREADY_IN).send(sender);
            return;
        }
        user.saveData(this.plugin);

        plugin.getMessage(Lang.COMMAND_IGNORE_ADD_DONE)
            .replace(Placeholders.PLAYER_NAME, userTarget.getName())
            .send(sender);
    }
}
