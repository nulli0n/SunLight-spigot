package su.nightexpress.sunlight.module.chat.mention;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerMention implements ChatMention {

    private final String playerName;
    private final String format;

    public PlayerMention(@NotNull String playerName, @NotNull String format) {
        this.playerName = playerName;
        this.format = format;
    }

    @Override
    @NotNull
    public String getFormat() {
        return this.format;
    }

    @Override
    public boolean isApplicable(@NotNull Player player) {
        return player.getName().equalsIgnoreCase(this.playerName);
    }
}
