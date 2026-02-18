package su.nightexpress.sunlight.user.cache;

public interface UserCacheContainer {

    void clear();

    void clearExpired();
}
