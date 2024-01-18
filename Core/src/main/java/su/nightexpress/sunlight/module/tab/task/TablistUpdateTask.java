package su.nightexpress.sunlight.module.tab.task;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.server.AbstractTask;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.tab.TabModule;

public class TablistUpdateTask extends AbstractTask<SunLight> {

    private final TabModule tabModule;

    public TablistUpdateTask(@NotNull TabModule tabModule, long interval) {
        super(tabModule.plugin(), interval, true);
        this.tabModule = tabModule;
    }

    @Override
    public void action() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            this.tabModule.updateTabListFormat(player);
            this.tabModule.updateTabListName(player);
        }
    }
}
