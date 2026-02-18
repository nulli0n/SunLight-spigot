package su.nightexpress.sunlight.api.provider;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public interface AfkProvider {

    boolean isAfk(@NonNull Player player);
}
