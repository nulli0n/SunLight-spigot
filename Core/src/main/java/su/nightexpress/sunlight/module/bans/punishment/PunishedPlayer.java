package su.nightexpress.sunlight.module.bans.punishment;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.bans.util.Placeholders;

import java.util.UUID;

public class PunishedPlayer extends PunishData {

    private final UUID playerId;
    private String playerName;

    public PunishedPlayer(@NotNull UUID id, @NotNull UUID playerId, @NotNull String playerName, @NotNull String reason, @NotNull String admin, long createDate, long expireDate) {
        super(id, reason, admin, createDate, expireDate);
        this.playerId = playerId;
        this.playerName = playerName;

        this.placeholderMap.add(Placeholders.forPunishedPlayer(this));
    }

    public boolean updateName(@NotNull String name) {
        if (!this.playerName.equalsIgnoreCase(name)) {
            this.playerName = name;
            return true;
        }
        return false;
    }

    @Override
    public boolean isApplicable(@NotNull Player player) {
        return this.getPlayerName().equalsIgnoreCase(player.getName());
    }

    @NotNull
    public UUID getPlayerId() {
        return playerId;
    }

    @NotNull
    public String getPlayerName() {
        return playerName;
    }
}
