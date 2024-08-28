package su.nightexpress.sunlight.data;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.database.AbstractUserManager;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.core.menu.IgnoreListMenu;
import su.nightexpress.sunlight.core.menu.IgnoreSettingsMenu;
import su.nightexpress.sunlight.data.user.SunUser;

import java.util.UUID;

public class UserManager extends AbstractUserManager<SunLightPlugin, SunUser> {

    private IgnoreListMenu ignoreListMenu;
    private IgnoreSettingsMenu ignoreSettingsMenu;

    public UserManager(@NotNull SunLightPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        this.ignoreListMenu = new IgnoreListMenu(this.plugin);
        this.ignoreSettingsMenu = new IgnoreSettingsMenu(this.plugin);
    }

    @Override
    protected void onShutdown() {
        super.onShutdown();
        this.ignoreListMenu.clear();
        this.ignoreSettingsMenu.clear();
    }

    @Override
    @NotNull
    public SunUser createUserData(@NotNull UUID uuid, @NotNull String name) {
        return SunUser.create(this.plugin, uuid, name);
    }

    @NotNull
    @Deprecated
    public IgnoreListMenu getIgnoreListMenu() {
        return ignoreListMenu;
    }

    @NotNull
    @Deprecated
    public IgnoreSettingsMenu getIgnoreSettingsMenu() {
        return ignoreSettingsMenu;
    }
}
