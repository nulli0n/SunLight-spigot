package su.nightexpress.sunlight.module.bans.time;

@FunctionalInterface
public interface BanTimeAccumulator {

    long accumulate(long quantity);
}
