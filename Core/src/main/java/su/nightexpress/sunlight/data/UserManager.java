package su.nightexpress.sunlight.data;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.data.AbstractUserManager;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.data.listener.UserListener;
import su.nightexpress.sunlight.data.menu.IgnoreListMenu;
import su.nightexpress.sunlight.data.menu.IgnoreSettingsMenu;

import java.util.UUID;

public class UserManager extends AbstractUserManager<SunLight, SunUser> {

    private IgnoreListMenu ignoreListMenu;
    private IgnoreSettingsMenu ignoreSettingsMenu;

    public UserManager(@NotNull SunLight plugin) {
        super(plugin, plugin);
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        this.ignoreListMenu = new IgnoreListMenu(this.plugin);
        this.ignoreSettingsMenu = new IgnoreSettingsMenu(this.plugin);

        this.addListener(new UserListener(this.plugin));
    }

    @Override
    protected void onShutdown() {
        super.onShutdown();
        this.ignoreListMenu.clear();
        this.ignoreSettingsMenu.clear();
    }

    @Override
    @NotNull
    protected SunUser createData(@NotNull UUID uuid, @NotNull String name) {
        return new SunUser(this.plugin, uuid, name);
    }

    @NotNull
    public IgnoreListMenu getIgnoreListMenu() {
        return ignoreListMenu;
    }

    @NotNull
    public IgnoreSettingsMenu getIgnoreSettingsMenu() {
        return ignoreSettingsMenu;
    }
}
