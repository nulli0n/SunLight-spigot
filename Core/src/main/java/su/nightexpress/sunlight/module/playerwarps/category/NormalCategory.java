package su.nightexpress.sunlight.module.playerwarps.category;

import org.jspecify.annotations.NonNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.placeholder.PlaceholderResolvable;
import su.nightexpress.nightcore.util.placeholder.PlaceholderResolver;
import su.nightexpress.sunlight.module.playerwarps.PlayerWarp;
import su.nightexpress.sunlight.module.playerwarps.PlayerWarpsPlaceholders;

import java.util.List;

public class NormalCategory implements WarpCategory, PlaceholderResolvable, Writeable {

    private final String       id;
    private final String       name;
    private final boolean      primary;
    private final List<String> description;
    private final NightItem    icon;

    public NormalCategory(@NonNull String id,
                          @NonNull String name,
                          boolean primary,
                          @NonNull List<String> description,
                          @NonNull NightItem icon) {
        this.id = id;
        this.name = name;
        this.primary = primary;
        this.description = description;
        this.icon = icon;
    }

    @NonNull
    public static NormalCategory read(@NonNull FileConfig config, @NonNull String path) {
        String id = LowerCase.INTERNAL.apply(config.getString(path + ".Id", "null"));
        String name = config.getString(path + ".Name", id);
        boolean primary = config.getBoolean(path + ".Primary");
        List<String> description = config.getStringList(path + ".Description");
        NightItem icon = config.getCosmeticItem(path + ".Icon");

        return new NormalCategory(id, name, primary, description, icon);
    }

    @Override
    public void write(@NonNull FileConfig config, @NonNull String path) {
        config.set(path + ".Id", this.id);
        config.set(path + ".Name", this.name);
        config.set(path + ".Primary", this.primary);
        config.set(path + ".Description", this.description);
        config.set(path + ".Icon", this.icon);
    }

    @Override
    @NonNull
    public PlaceholderResolver placeholders() {
        return PlayerWarpsPlaceholders.CATEGORY.resolver(this);
    }

    public boolean isWarpOfThis(@NonNull PlayerWarp warp) {
        return warp.getCategoryId().equalsIgnoreCase(this.id);
    }

    @NonNull
    public String id() {
        return this.id;
    }

    @NonNull
    public String name() {
        return this.name;
    }

    public boolean primary() {
        return this.primary;
    }

    @NonNull
    public List<String> description() {
        return List.copyOf(this.description);
    }

    @NonNull
    public NightItem icon() {
        return this.icon.copy();
    }
}
