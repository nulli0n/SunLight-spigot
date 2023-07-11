package su.nightexpress.sunlight.command.teleport;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.lang.LangMessage;
import su.nexmedia.engine.api.server.JPermission;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.TimeUtil;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.api.event.PlayerTeleportRequestEvent;
import su.nightexpress.sunlight.command.teleport.impl.TeleportRequest;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.IgnoredUser;
import su.nightexpress.sunlight.data.impl.SunUser;

import java.util.List;

abstract class AbstractRequestCommand extends AbstractCommand<SunLight> {

    protected final int requestTimeout;

    public AbstractRequestCommand(@NotNull SunLight plugin, @NotNull JYML cfg, @NotNull String[] aliases, @NotNull JPermission permission) {
        super(plugin, aliases, permission);
        this.setPlayerOnly(true);

        this.requestTimeout = JOption.create("Teleport.Request.Timeout", 60,
            "Sets how long (in seconds) teleport request will be valid to accept/decline it.").read(cfg);
    }

    public abstract boolean isSummon();

    @NotNull
    public abstract LangMessage getMessageForSender();

    @NotNull
    public abstract LangMessage getMessageForTarget();

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return CollectionsUtil.playerNames(player);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        Player target = plugin.getServer().getPlayer(result.getArg(1));
        if (target == null) {
            this.errorPlayer(sender);
            return;
        }
        if (target == sender) {
            plugin.getMessage(Lang.ERROR_COMMAND_SELF).send(sender);
            return;
        }

        Player player = (Player) sender;
        SunUser userTarget = plugin.getUserManager().getUserData(target);

        // Check if 'accepter' disaled requests so request should be declined.
        if (!userTarget.getSettings().get(TeleportRequest.SETTING_REQUESTS)) {
            plugin.getMessage(Lang.COMMAND_TELEPORT_ERROR_REQUESTS_DISABLED)
                .replace(Placeholders.forPlayer(target))
                .send(sender);
            return;
        }

        // Check if 'accepter' is ignoring 'sended' so request should be declined.
        IgnoredUser ignoredUser = userTarget.getIgnoredUser(player);
        if (ignoredUser != null && ignoredUser.isDenyTeleports()) {
            this.errorPermission(sender);
            return;
        }

        // Check if user already sent request to this player and it's not expired yet.
        // So players can't spam requests while there is active one.
        TeleportRequest requestHas = TeleportRequest.getRequest(player.getUniqueId(), sender.getName());
        if (requestHas != null && !requestHas.isExpired()) {
            plugin.getMessage(Lang.COMMAND_TELEPORT_ERROR_REQUESTS_COOLDOWN)
                .replace(Placeholders.GENERIC_TIME, TimeUtil.formatTimeLeft(requestHas.getExpireDate(), System.currentTimeMillis()))
                .send(sender);
            return;
        }

        // Create a TeleportRequest object.
        TeleportRequest request = new TeleportRequest(sender.getName(), target.getName(), this.isSummon(), requestTimeout);

        // Call a custom TeleportRequest event, so other plugins or modules can handle it.
        PlayerTeleportRequestEvent eventTeleport = new PlayerTeleportRequestEvent(request);
        plugin.getPluginManager().callEvent(eventTeleport);
        if (eventTeleport.isCancelled()) return;

        /*if (accepterSettings.isRequestsAutoAccept(player)) {
            request.accept(true);
            return;
        }
        if (accepterSettings.isRequestsAutoDecline(player)) {
            request.decline(true);
            return;
        }*/

        // Add TeleportRequest object to user teleport requests list.
        TeleportRequest.addRequest(target.getUniqueId(), request, false);

        // Send teleport notifications.
        this.getMessageForTarget().replace(Placeholders.forPlayer(player)).send(target);
        this.getMessageForSender().replace(Placeholders.forPlayer(target)).send(sender);
    }
}
