package su.nightexpress.sunlight.module.bans.punishment;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.placeholder.PlaceholderResolver;
import su.nightexpress.sunlight.module.bans.BansPlaceholders;

import java.util.UUID;

public class PlayerPunishment extends AbstractPunishment {

    private final UUID playerId;

    private String playerName;

    public PlayerPunishment(@NotNull UUID playerId, @NotNull String playerName, @NotNull PunishmentData data, boolean active) {
        super(data, active);
        this.playerId = playerId;
        this.playerName = playerName;
    }

    @Override
    @NotNull
    public PlaceholderResolver placeholders() {
        return BansPlaceholders.PLAYER_PUNISHMENT.resolver(this);
    }

    public void updateName(@NotNull String name) {
        if (!this.playerName.equalsIgnoreCase(name)) {
            this.playerName = name;
            this.markDirty();
        }
    }

    @Override
    public boolean isApplicable(@NotNull Player player) {
        return this.playerName.equalsIgnoreCase(player.getName());
    }

    @Override
    @NotNull
    public String getName() {
        return this.playerName;
    }

    @NotNull
    public UUID getPlayerId() {
        return this.playerId;
    }

    @NotNull
    public String getPlayerName() {
        return this.playerName;
    }
}
