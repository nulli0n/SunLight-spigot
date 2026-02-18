package su.nightexpress.sunlight.module.ptp;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.nightcore.util.time.TimeFormatType;
import su.nightexpress.nightcore.util.time.TimeFormats;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.api.event.PlayerTeleportRequestEvent;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.hook.placeholder.PlaceholderRegistry;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleContext;
import su.nightexpress.sunlight.module.ptp.command.PTPCommands;
import su.nightexpress.sunlight.module.ptp.config.PTPSettings;
import su.nightexpress.sunlight.module.ptp.config.PTPLang;
import su.nightexpress.sunlight.module.ptp.config.PTPPerms;
import su.nightexpress.sunlight.module.ptp.listener.PTPListener;
import su.nightexpress.sunlight.module.ptp.request.TeleportMode;
import su.nightexpress.sunlight.module.ptp.request.TeleportRequest;
import su.nightexpress.sunlight.teleport.*;
import su.nightexpress.sunlight.user.property.UserPropertyRegistry;

import java.util.*;

public class PTPModule extends Module {

    private final TeleportManager teleportManager;
    private final PTPSettings                      settings;
    private final Map<UUID, List<TeleportRequest>> requestsMap;

    public PTPModule(@NotNull ModuleContext context, @NotNull TeleportManager teleportManager) {
        super(context);
        this.teleportManager = teleportManager;
        this.settings = new PTPSettings();
        this.requestsMap = new HashMap<>();
    }

    @Override
    protected void loadModule(@NotNull FileConfig config) {
        this.settings.load(config);
        this.plugin.injectLang(PTPLang.class);
        UserPropertyRegistry.register(PTPProperties.TELEPORT_REQUESTS);

        this.addListener(new PTPListener(this.plugin, this));
    }

    @Override
    protected void unloadModule() {
        this.requestsMap.clear();
    }

    @Override
    protected void registerPermissions(@NotNull PermissionTree root) {
        root.merge(PTPPerms.MODULE);
    }

    protected void registerCommands() {
        this.commandRegistry.addProvider("ptp", new PTPCommands(this.plugin, this, this.userManager));
    }

    @Override
    public void registerPlaceholders(@NotNull PlaceholderRegistry registry) {
        registry.register("ptp_requests_enabled", (player, payload) -> {
            return CoreLang.STATE_YES_NO.get(this.isRequestsEnabled(player));
        });
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
        return requests.isEmpty() ? null : requests.getLast();
    }

    public void clearRequests(@NotNull Player player) {
        this.clearRequests(player.getUniqueId());
    }

    public void clearRequests(@NotNull UUID playerId) {
        this.requestsMap.remove(playerId);
    }


    public boolean isRequestsEnabled(@NotNull Player player) {
        return this.userManager.getOrFetch(player).getPropertyOrDefault(PTPProperties.TELEPORT_REQUESTS);
    }


    public boolean sendRequest(@NotNull Player sender, @NotNull Player target, @NotNull TeleportMode mode) {
        // Check if 'accepter' disaled requests so request should be declined.
        if (!this.isRequestsEnabled(target) && !sender.hasPermission(PTPPerms.BYPASS_REQUESTS_DISABLED)) {
            this.sendPrefixed(PTPLang.REQUEST_SEND_ERROR_DISABLED, sender, builder -> builder.with(CommonPlaceholders.PLAYER.resolver(target)));
            return false;
        }

        // Check if user already sent request to this player and it's not expired yet.
        // So players can't spam requests while there is active one.
        TeleportRequest requestHas = this.getPlayerRequest(target, sender.getName());
        if (requestHas != null && !requestHas.isExpired()) {
            this.sendPrefixed(PTPLang.REQUEST_SEND_ERROR_COOLDOWN, sender, builder -> builder
                .with(SLPlaceholders.GENERIC_TIME, () -> TimeFormats.formatDuration(requestHas.getExpireDate(), TimeFormatType.LITERAL))
            );
            return false;
        }

        TeleportRequest request = new TeleportRequest(sender, target, mode, this.settings.getRequestTimeout());

        PlayerTeleportRequestEvent eventTeleport = new PlayerTeleportRequestEvent(request);
        plugin.getPluginManager().callEvent(eventTeleport);
        if (eventTeleport.isCancelled()) return false;

        this.getRequests(target).add(request);

        this.sendPrefixed(mode == TeleportMode.REQUEST ? PTPLang.REQUEST_NOTIFY : PTPLang.INVITE_NOTIFY, target, builder -> builder
            .with(CommonPlaceholders.PLAYER.resolver(sender)));

        this.sendPrefixed(mode == TeleportMode.REQUEST ? PTPLang.REQUEST_SENT : PTPLang.INVITE_SENT, sender, builder -> builder
            .with(CommonPlaceholders.PLAYER.resolver(target)));

        if (this.plugin.afkProvider().map(afkProvider -> afkProvider.isAfk(target)).orElse(false)) {
            this.sendPrefixed(PTPLang.REQUEST_SEND_TARGET_AFK, sender, builder -> builder
                .with(CommonPlaceholders.PLAYER.resolver(target))
            );
        }

        return true;
    }

    public boolean accept(@NotNull Player player, @Nullable String name) {
        TeleportRequest request = name == null ? this.getLatest(player) : this.getPlayerRequest(player, name);
        if (request == null) {
            this.sendPrefixed(PTPLang.REQUEST_ACCEPT_NOTHING, player);
            return false;
        }

        Player sender = request.getSender();
        if (sender == null) {
            this.sendPrefixed(CoreLang.ERROR_INVALID_PLAYER, player);
            return false;
        }

        Player teleporter = request.getMode() == TeleportMode.INVITE ? player : sender;
        Location destination = request.getMode() == TeleportMode.INVITE ? sender.getLocation() : player.getLocation();

        request.setExpired();

        TeleportContext teleportContext = TeleportContext.builder(this, teleporter, destination)
            .withFlag(TeleportFlag.LOOK_FOR_SURFACE)
            .withFlag(TeleportFlag.AVOID_LAVA)
            .callback(() -> {
                this.sendPrefixed(PTPLang.REQUEST_ACCEPT_DONE, player, builder -> builder.with(CommonPlaceholders.PLAYER.resolver(sender)));
                this.sendPrefixed(PTPLang.REQUEST_ACCEPT_NOTIFY, sender, builder -> builder.with(CommonPlaceholders.PLAYER.resolver(player)));
            })
            .build();

        return this.teleportManager.teleport(teleportContext, TeleportType.PTP);
    }

    public boolean decline(@NotNull Player player, @Nullable String name) {
        TeleportRequest request = name == null ? this.getLatest(player) : this.getPlayerRequest(player, name);
        if (request == null) {
            this.sendPrefixed(PTPLang.REQUEST_ACCEPT_NOTHING, player);
            return false;
        }

        Player sender = request.getSender();
        if (sender == null) {
            this.sendPrefixed(CoreLang.ERROR_INVALID_PLAYER, player);
            return false;
        }

        request.setExpired();

        this.sendPrefixed(PTPLang.REQUEST_DECLINE_DONE, player, builder -> builder.with(CommonPlaceholders.PLAYER.resolver(sender)));
        this.sendPrefixed(PTPLang.REQUEST_DECLINE_NOTIFY, sender, builder -> builder.with(CommonPlaceholders.PLAYER.resolver(player)));

        return true;
    }
}
