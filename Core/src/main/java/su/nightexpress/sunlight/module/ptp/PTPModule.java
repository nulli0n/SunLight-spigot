package su.nightexpress.sunlight.module.ptp;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.api.event.PlayerTeleportRequestEvent;
import su.nightexpress.sunlight.config.Perms;
import su.nightexpress.sunlight.core.user.IgnoredUser;
import su.nightexpress.sunlight.core.user.settings.Setting;
import su.nightexpress.sunlight.core.user.settings.SettingRegistry;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleInfo;
import su.nightexpress.sunlight.module.ptp.command.AcceptCommands;
import su.nightexpress.sunlight.module.ptp.command.PTPCommand;
import su.nightexpress.sunlight.module.ptp.command.RequestCommands;
import su.nightexpress.sunlight.module.ptp.command.ToggleCommand;
import su.nightexpress.sunlight.module.ptp.config.PTPConfig;
import su.nightexpress.sunlight.module.ptp.config.PTPLang;
import su.nightexpress.sunlight.module.ptp.config.PTPPerms;
import su.nightexpress.sunlight.module.ptp.listener.PTPListener;

import java.util.*;

public class PTPModule extends Module {

    public static final Setting<Boolean> TELEPORT_REQUESTS = SettingRegistry.register(Setting.create("teleport_requests", true, true));

    private final Map<UUID, List<TeleportRequest>> requestsMap;

    public PTPModule(@NotNull SunLightPlugin plugin, @NotNull String id) {
        super(plugin, id);
        this.requestsMap = new HashMap<>();
    }

    @Override
    protected void gatherInfo(@NotNull ModuleInfo moduleInfo) {
        moduleInfo.setConfigClass(PTPConfig.class);
        moduleInfo.setLangClass(PTPLang.class);
        moduleInfo.setPermissionsClass(PTPPerms.class);
    }

    @Override
    protected void onModuleLoad() {
        this.registerCommands();

        this.addListener(new PTPListener(this.plugin, this));
    }

    @Override
    protected void onModuleUnload() {
        this.requestsMap.clear();
    }

    private void registerCommands() {
        PTPCommand.load(this.plugin, this);
        RequestCommands.load(this.plugin, this);
        AcceptCommands.load(this.plugin, this);
        ToggleCommand.load(this.plugin, this);
    }

    @NotNull
    public Map<UUID, List<TeleportRequest>> getRequestsMap() {
        return this.requestsMap;
    }

    @NotNull
    public List<TeleportRequest> getRequests(@NotNull Player player) {
        return this.getRequests(player.getUniqueId());
    }

    @NotNull
    public List<TeleportRequest> getRequests(@NotNull UUID playerId) {
        List<TeleportRequest> requests = this.requestsMap.computeIfAbsent(playerId, k -> new ArrayList<>());
        requests.removeIf(TeleportRequest::isExpired);

        return requests;
    }

    @Nullable
    public TeleportRequest getPlayerRequest(@NotNull Player player, @NotNull String name) {
        return this.getPlayerRequest(player.getUniqueId(), name);
    }

    @Nullable
    public TeleportRequest getPlayerRequest(@NotNull UUID playerId, @NotNull String name) {
        return this.getRequests(playerId).stream().filter(request -> request.isSender(name)).findFirst().orElse(null);
    }

    @Nullable
    public TeleportRequest getLatest(@NotNull Player player) {
        return this.getLatest(player.getUniqueId());
    }

    @Nullable
    public TeleportRequest getLatest(@NotNull UUID playerId) {
        List<TeleportRequest> requests = this.getRequests(playerId);
        return requests.isEmpty() ? null : requests.get(requests.size() - 1);
    }

    public void clearRequests(@NotNull Player player) {
        this.clearRequests(player.getUniqueId());
    }

    public void clearRequests(@NotNull UUID playerId) {
        this.requestsMap.remove(playerId);
    }


    public boolean isRequestsEnabled(@NotNull Player player) {
        return this.plugin.getUserManager().getUserData(player).getSettings().get(TELEPORT_REQUESTS);
    }


    public boolean sendRequest(@NotNull Player player, @NotNull Player target, @NotNull Mode mode) {
        SunUser targetUser = plugin.getUserManager().getUserData(target);

        // Check if 'accepter' disaled requests so request should be declined.
        if (!this.isRequestsEnabled(target) && !player.hasPermission(Perms.BYPASS_TELEPORT_REQUESTS_DISABLED)) {
            PTPLang.REQUEST_SEND_ERROR_DISABLED.getMessage().replace(Placeholders.forPlayer(target)).send(player);
            return false;
        }

        // Check if 'accepter' is ignoring 'sended' so request should be declined.
        IgnoredUser ignoredUser = targetUser.getIgnoredUser(player);
        if (ignoredUser != null && ignoredUser.isDenyTeleports() && !player.hasPermission(Perms.BYPASS_IGNORE_TELEPORTS)) {
            PTPLang.REQUEST_SEND_ERROR_DISABLED.getMessage().replace(Placeholders.forPlayer(target)).send(player);
            return false;
        }

        // Check if user already sent request to this player and it's not expired yet.
        // So players can't spam requests while there is active one.
        TeleportRequest requestHas = this.getPlayerRequest(target, player.getName());
        if (requestHas != null && !requestHas.isExpired()) {
            PTPLang.REQUEST_SEND_ERROR_COOLDOWN.getMessage()
                .replace(Placeholders.GENERIC_TIME, TimeUtil.formatDuration(requestHas.getExpireDate()))
                .send(player);
            return false;
        }

        TeleportRequest request = new TeleportRequest(player, target, mode, PTPConfig.REQUEST_TIMEOUT.get());

        PlayerTeleportRequestEvent eventTeleport = new PlayerTeleportRequestEvent(request);
        plugin.getPluginManager().callEvent(eventTeleport);
        if (eventTeleport.isCancelled()) return false;

        this.getRequests(target).add(request);

        (mode == Mode.REQUEST ? PTPLang.REQUEST_NOTIFY : PTPLang.INVITE_NOTIFY).getMessage().replace(Placeholders.forPlayer(player)).send(target);
        (mode == Mode.REQUEST ? PTPLang.REQUEST_SENT : PTPLang.INVITE_SENT).getMessage().replace(Placeholders.forPlayer(target)).send(player);

        return true;
    }

    /*public boolean addRequest(@NotNull UUID playerId, @NotNull TeleportRequest request, boolean override) {
        TeleportRequest has = this.getFrom(playerId, request.getSender());
        if (has != null) {
            if (!override) {
                return false;
            }
            this.getRequests(playerId).remove(has);
        }
        return this.getRequests(playerId).add(request);
    }*/


    public boolean accept(@NotNull Player player, @Nullable String name) {
        TeleportRequest request = name == null ? this.getLatest(player) : this.getPlayerRequest(player, name);
        if (request == null) {
            PTPLang.REQUEST_ACCEPT_NOTHING.getMessage().send(player);
            return false;
        }

        Player sender = request.getSender();
        if (sender == null) {
            PTPLang.ERROR_INVALID_PLAYER.getMessage().send(player);
            return false;
        }

        Player teleporter = request.getMode() == Mode.INVITE ? player : sender;
        Location destination = request.getMode() == Mode.INVITE ? sender.getLocation() : player.getLocation();
        teleporter.teleport(destination);
        request.setExpired();

        PTPLang.REQUEST_ACCEPT_DONE.getMessage().replace(Placeholders.forPlayer(sender)).send(player);
        PTPLang.REQUEST_ACCEPT_NOTIFY.getMessage().replace(Placeholders.forPlayer(player)).send(sender);

        return true;
    }

    public boolean decline(@NotNull Player player, @Nullable String name) {
        TeleportRequest request = name == null ? this.getLatest(player) : this.getPlayerRequest(player, name);
        if (request == null) {
            PTPLang.REQUEST_ACCEPT_NOTHING.getMessage().send(player);
            return false;
        }

        Player sender = request.getSender();
        if (sender == null) {
            PTPLang.ERROR_INVALID_PLAYER.getMessage().send(player);
            return false;
        }

        request.setExpired();

        PTPLang.REQUEST_DECLINE_DONE.getMessage().replace(Placeholders.forPlayer(sender)).send(player);
        PTPLang.REQUEST_DECLINE_NOTIFY.getMessage().replace(Placeholders.forPlayer(player)).send(sender);

        return true;
    }
}
