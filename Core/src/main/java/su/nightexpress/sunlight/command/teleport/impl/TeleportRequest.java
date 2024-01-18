package su.nightexpress.sunlight.command.teleport.impl;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.utils.Placeholders;
import su.nightexpress.sunlight.SunLightAPI;
import su.nightexpress.sunlight.config.Lang;

import java.util.*;

public class TeleportRequest {

    private static final Map<UUID, List<TeleportRequest>> REQUEST_MAP = new HashMap<>();

    private final String target;
    private final String sender;
    private final boolean isSummon;

    private long expireDate;

    public TeleportRequest(@NotNull String sender, @NotNull String target, boolean isSummon, int expire) {
        this.sender = sender;
        this.target = target;
        this.isSummon = isSummon;
        this.expireDate = System.currentTimeMillis() + (long)expire * 1000L;
    }

    @NotNull
    public static Map<UUID, List<TeleportRequest>> getRequestMap() {
        REQUEST_MAP.values().forEach(list -> list.removeIf(TeleportRequest::isExpired));
        REQUEST_MAP.values().removeIf(List::isEmpty);
        return REQUEST_MAP;
    }

    public static boolean addRequest(@NotNull UUID playerId, @NotNull TeleportRequest request, boolean override) {
        TeleportRequest has = TeleportRequest.getFrom(playerId, request.getSender());
        if (has != null) {
            if (!override) {
                return false;
            }
            TeleportRequest.getRequests(playerId).remove(has);
        }
        return TeleportRequest.getRequests(playerId).add(request);
    }

    @Nullable
    public static TeleportRequest getFrom(@NotNull UUID playerId, @NotNull String sender) {
        return TeleportRequest.getRequests(playerId).stream().filter(request -> request.getSender().equalsIgnoreCase(sender)).findFirst().orElse(null);
    }

    @Nullable
    public static TeleportRequest getLatest(@NotNull UUID playerId) {
        List<TeleportRequest> requests = TeleportRequest.getRequests(playerId);
        if (requests.isEmpty()) {
            return null;
        }
        return requests.get(requests.size() - 1);
    }

    @NotNull
    public static List<TeleportRequest> getRequests(@NotNull UUID playerId) {
        return TeleportRequest.getRequestMap().computeIfAbsent(playerId, k -> new ArrayList<>());
    }

    public boolean accept(boolean doNotify) {
        Player pAsker = Bukkit.getPlayer(this.getSender());
        Player pAccept = Bukkit.getPlayer(this.getTarget());
        if (pAsker == null || pAccept == null) {
            return false;
        }
        Player teleporter = this.isSummon() ? pAccept : pAsker;
        Location destination = this.isSummon() ? pAsker.getLocation() : pAccept.getLocation();
        teleporter.teleport(destination);
        this.expire();
        if (doNotify) {
            SunLightAPI.PLUGIN.getMessage(Lang.COMMAND_TELEPORT_ACCEPT_NOTIFY_SENDER).replace(Placeholders.forPlayer(pAsker)).send(pAccept);
            SunLightAPI.PLUGIN.getMessage(Lang.COMMAND_TELEPORT_ACCEPT_NOTIFY_TARGET).replace(Placeholders.forPlayer(pAccept)).send(pAsker);
        }
        return true;
    }

    public boolean decline(boolean doNotify) {
        Player pSender = Bukkit.getPlayer(this.getSender());
        Player pTarget = Bukkit.getPlayer(this.getTarget());
        if (pSender == null || pTarget == null) {
            return false;
        }
        this.expire();
        if (doNotify) {
            SunLightAPI.PLUGIN.getMessage(Lang.COMMAND_TELEPORT_DECLINE_NOTIFY_SENDER).replace(Placeholders.forPlayer(pSender)).send(pTarget);
            SunLightAPI.PLUGIN.getMessage(Lang.COMMAND_TELEPORT_DECLINE_NOTIFY_TARGET).replace(Placeholders.forPlayer(pTarget)).send(pSender);
        }
        return true;
    }

    @NotNull
    public String getSender() {
        return this.sender;
    }

    @NotNull
    public String getTarget() {
        return this.target;
    }

    public boolean isSummon() {
        return this.isSummon;
    }

    public long getExpireDate() {
        return this.expireDate;
    }

    public boolean isExpired() {
        return this.expireDate < System.currentTimeMillis();
    }

    public void expire() {
        this.expireDate = System.currentTimeMillis();
    }
}

