package su.nightexpress.sunlight.hook.placeholder;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface PlaceholderHandler {

    @NotNull String handle(@NotNull Player player, @NotNull String payload);
}
