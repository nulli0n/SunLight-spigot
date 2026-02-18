package su.nightexpress.sunlight.module.chat.mention;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ChatMention {

    boolean isApplicable(@NotNull Player player);

    @NotNull String getFormat();
}
