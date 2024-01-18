package su.nightexpress.sunlight.command.nick;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.TargetCommand;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.SunUser;

public class NickClearCommand extends TargetCommand {

    public static final String NAME = "clear";

    public NickClearCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_NICK_CLEAR, Perms.COMMAND_NICK_CLEAR_OTHERS, 1);
        this.setAllowDataLoad();
        this.setDescription(plugin.getMessage(Lang.COMMAND_NICK_CLEAR_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_NICK_CLEAR_USAGE));
        this.addFlag(CommandFlags.SILENT);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        SunUser user = plugin.getUserManager().getUserData(target);
        user.setCustomName(null);
        target.setDisplayName(null);
        this.plugin.getUserManager().saveUser(user);

        if (sender != target) {
            plugin.getMessage(Lang.COMMAND_NICK_CLEAR_TARGET)
                .replace(Placeholders.forPlayer(target))
                .send(sender);
        }
        if (!result.hasFlag(CommandFlags.SILENT)) {
            plugin.getMessage(Lang.COMMAND_NICK_CLEAR_NOTIFY)
                .send(target);
        }
    }
}
