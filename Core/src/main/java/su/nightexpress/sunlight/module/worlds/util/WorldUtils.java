package su.nightexpress.sunlight.module.worlds.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorldUtils {

    @NotNull
    public static Collection<Plugin> getGeneratorPlugins(@NotNull String worldName) {
        return Stream.of(Bukkit.getServer().getPluginManager().getPlugins())
            .filter(plugin -> plugin.getDefaultWorldGenerator(worldName, null) != null)
            .collect(Collectors.toSet());
    }
}
