package su.nightexpress.sunlight.api.provider;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public interface VanishProvider {

    boolean isVanished(@NonNull Player player);
}
