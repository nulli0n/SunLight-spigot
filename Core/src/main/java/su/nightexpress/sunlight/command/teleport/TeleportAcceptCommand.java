package su.nightexpress.sunlight.command.teleport;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.teleport.impl.TeleportRequest;
import su.nightexpress.sunlight.config.Lang;

import java.util.List;
import java.util.UUID;

public class TeleportAcceptCommand extends AbstractCommand<SunLight> {

    public static final String NAME = "accept";

    public TeleportAcceptCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_TELEPORT_ACCEPT);
        this.setDescription(plugin.getMessage(Lang.COMMAND_TELEPORT_ACCEPT_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_TELEPORT_ACCEPT_USAGE));
        this.setPlayerOnly(true);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return TeleportRequest.getRequests(player.getUniqueId()).stream().map(TeleportRequest::getSender).toList();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player player = (Player) sender;

        // Get TeleportRequest by a sender name (if provided) or just the latest one.
        // Expired requests are not included and will be NULL.
        UUID playerId = player.getUniqueId();
        TeleportRequest request = result.length() < 2 ? TeleportRequest.getRequest(playerId) : TeleportRequest.getRequest(playerId, result.getArg(1));
        if (request == null) {
            plugin.getMessage(Lang.COMMAND_TELEPORT_ACCEPT_ERROR_NOTHING).send(sender);
            return;
        }

        // Check if request sender is online.
        Player target = plugin.getServer().getPlayer(request.getSender());
        if (target == null) {
            this.errorPlayer(sender);
            return;
        }

        // Accept request
        request.accept(true);
    }
}
