package su.nightexpress.sunlight.module.rtp.task;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.server.AbstractTask;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.rtp.impl.LocationFinder;
import su.nightexpress.sunlight.module.rtp.RTPModule;

import java.util.function.Predicate;

public class FinderTickTask extends AbstractTask<SunLight> {

    private final RTPModule module;

    public FinderTickTask(@NotNull RTPModule module) {
        super(module.plugin(), 1, true);
        this.module = module;
    }

    @Override
    public void action() {
        this.module.getFinderMap().values().stream().filter(Predicate.not(LocationFinder::isFailed)).forEach(LocationFinder::tick);
    }
}
