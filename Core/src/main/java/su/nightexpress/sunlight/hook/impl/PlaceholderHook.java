package su.nightexpress.sunlight.hook.impl;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.hook.placeholder.PlaceholderRegistry;

public class PlaceholderHook {

    private static Expansion expansion;

    public static void setup(@NotNull SunLightPlugin plugin) {
        if (expansion == null) {
            expansion = new Expansion(plugin);
            expansion.register();
        }
    }

    public static void shutdown() {
        if (expansion != null) {
            expansion.unregister();
            expansion = null;
        }
    }

    private static class Expansion extends PlaceholderExpansion {

        private final SunLightPlugin plugin;
        private final PlaceholderRegistry registry;

        public Expansion(@NotNull SunLightPlugin plugin) {
            this.plugin = plugin;
            this.registry = new PlaceholderRegistry();

            this.plugin.getModuleRegistry().getModules().forEach(module -> {
                module.registerPlaceholders(this.registry);
            });
        }

        @Override
        @NotNull
        public String getIdentifier() {
            return plugin.getName().toLowerCase();
        }

        @Override
        @NotNull
        public String getAuthor() {
            return plugin.getDescription().getAuthors().getFirst();
        }

        @Override
        @NotNull
        public String getVersion() {
            return plugin.getDescription().getVersion();
        }

        @Override
        public boolean persist() {
            return true;
        }

        @Override
        public String onPlaceholderRequest(Player player, @NotNull String params) {
            if (player == null) return null;

            if (this.registry != null) {
                return this.registry.onPlaceholderRequest(player, params);
            }

            return super.onPlaceholderRequest(player, params);
        }
    }
}
