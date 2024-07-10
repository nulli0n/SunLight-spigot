package su.nightexpress.sunlight.module.worlds.impl;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.placeholder.Placeholder;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;
import su.nightexpress.sunlight.module.worlds.util.Placeholders;

public class WrappedWorld implements Placeholder {

    private final World          world;
    private final WorldData      worldData;
    private final PlaceholderMap placeholders;

    public WrappedWorld(@Nullable World world, @Nullable WorldData worldData) {
        this.world = world;
        this.worldData = worldData;
        this.placeholders = Placeholders.forWrapped(this);
    }

    @NotNull
    @Override
    public PlaceholderMap getPlaceholders() {
        return placeholders;
    }

    public boolean isCustom() {
        return this.worldData != null;
    }

    public boolean isPresent() {
        return this.world != null;
    }

    public World getWorld() {
        return world;
    }

    public WorldData getData() {
        return worldData;
    }
}
