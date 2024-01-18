package su.nightexpress.sunlight.module.rtp;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.rtp.command.RTPCommand;
import su.nightexpress.sunlight.module.rtp.config.RTPConfig;
import su.nightexpress.sunlight.module.rtp.config.RTPLang;
import su.nightexpress.sunlight.module.rtp.config.RTPPerms;
import su.nightexpress.sunlight.module.rtp.impl.LocationFinder;
import su.nightexpress.sunlight.module.rtp.listener.RTPListener;
import su.nightexpress.sunlight.module.rtp.task.FinderTickTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RTPModule extends Module {

    private final Map<Player, LocationFinder> finderMap;

    private FinderTickTask finderTickTask;

    public RTPModule(@NotNull SunLight plugin, @NotNull String id) {
        super(plugin, id);
        this.finderMap = new ConcurrentHashMap<>();
    }

    @Override
    protected void onLoad() {
        this.plugin.registerPermissions(RTPPerms.class);
        this.plugin.getLangManager().loadMissing(RTPLang.class);
        this.plugin.getLang().saveChanges();
        this.getConfig().initializeOptions(RTPConfig.class);

        this.addListener(new RTPListener(this));
        this.plugin.getCommandRegulator().register(RTPCommand.NAME, (cfg1, aliases) -> new RTPCommand(this, aliases), "wild");

        this.finderTickTask = new FinderTickTask(this);
        this.finderTickTask.start();
    }

    @Override
    protected void onShutdown() {
        if (this.finderTickTask != null) {
            this.finderTickTask.stop();
            this.finderTickTask = null;
        }
        this.finderMap.clear();
    }

    @NotNull
    public Map<Player, LocationFinder> getFinderMap() {
        this.finderMap.values().removeIf(LocationFinder::isFailed);
        return finderMap;
    }

    @Nullable
    public LocationFinder getFinder(@NotNull Player player) {
        return this.getFinderMap().get(player);
    }

    public void stopSearch(@NotNull Player player) {
        this.finderMap.remove(player);
    }

    public void startSearch(@NotNull Player player) {
        if (this.getFinder(player) != null) {
            this.plugin.getMessage(RTPLang.TELEPORT_ERROR_ALREADY_IN).send(player);
            return;
        }

        LocationFinder finder = new LocationFinder(plugin, player, RTPConfig.LOCATION_SEARCH_ATTEMPTS.get());
        this.getFinderMap().put(player, finder);

        this.plugin.getMessage(RTPLang.TELEPORT_NOTIFY_SEARCH)
            .replace(Placeholders.GENERIC_CURRENT, 0)
            .replace(Placeholders.GENERIC_MAX, RTPConfig.LOCATION_SEARCH_ATTEMPTS.get())
            .send(player);
    }
}
