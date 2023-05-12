package su.nightexpress.sunlight.command.teleport;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.utils.SunUtils;

import java.util.List;

public class TeleportSendCommand extends AbstractCommand<SunLight> {

    public static final String NAME = "send";

    public TeleportSendCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_TELEPORT_TO);
        this.setDescription(plugin.getMessage(Lang.COMMAND_TELEPORT_TO_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_TELEPORT_TO_USAGE));
        this.addFlag(CommandFlags.SILENT);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1 || arg == 2) {
            return CollectionsUtil.playerNames(player);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 3) {
            this.printUsage(sender);
            return;
        }

        String sourceName = result.getArg(1);
        String targetName = result.getArg(2);

        Player source = plugin.getServer().getPlayer(sourceName);
        if (source == null) {
            SunUser user = plugin.getUserManager().getUserData(sourceName);
            if (user == null) {
                this.errorPlayer(sender);
                return;
            }
            source = plugin.getSunNMS().loadPlayerData(user.getId(), user.getName());
        }

        Player target = plugin.getServer().getPlayer(targetName);
        if (target == null) {
            SunUser user = plugin.getUserManager().getUserData(targetName);
            if (user == null) {
                this.errorPlayer(sender);
                return;
            }
            target = plugin.getSunNMS().loadPlayerData(user.getId(), user.getName());
        }

        if (source == target && sender == source) {
            this.plugin.getMessage(Lang.ERROR_COMMAND_SELF).send(sender);
            return;
        }

        SunUtils.teleport(source, target);

        plugin.getMessage(Lang.COMMAND_TELEPORT_SEND_TARGET)
            .replace(Placeholders.GENERIC_SOURCE, source.getName())
            .replace(Placeholders.GENERIC_TARGET, target.getName())
            .send(sender);

        if (!result.hasFlag(CommandFlags.SILENT)) {
            plugin.getMessage(Lang.COMMAND_TELEPORT_SEND_NOTIFY)
                .replace(Placeholders.Player.replacer(target))
                .send(source);
        }
    }
}
