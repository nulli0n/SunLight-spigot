package su.nightexpress.sunlight.module.chat.mention;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.chat.ChatChannel;

import java.util.Set;

public interface Mention {

    boolean hasPermission(@NotNull Player player);

    //boolean isApplicable(@NotNull Player player);

    @NotNull Set<Player> getAffectedPlayers(@NotNull ChatChannel channel);

    @NotNull String getFormat();
}
