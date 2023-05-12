package su.nightexpress.sunlight.module.tab.task;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.server.AbstractTask;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.tab.TabModule;

public class NametagUpdateTask extends AbstractTask<SunLight> {

    private final TabModule tabModule;

    public NametagUpdateTask(@NotNull TabModule tabModule, long interval) {
        super(tabModule.plugin(), interval, true);
        this.tabModule = tabModule;
    }

    @Override
    public void action() {
        this.tabModule.updateNameTagsAndSortTab();
    }
}
