package su.nightexpress.sunlight.module.chat.cache;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.TimeUtil;

public class CachedContent {

    private final String content;
    private final long   creationTimestamp;
    private final long   expirationTimestamp;

    private int count;

    private CachedContent(String content, long creationTimestamp, long expirationTimestamp) {
        this.content = content;
        this.creationTimestamp = creationTimestamp;
        this.expirationTimestamp = expirationTimestamp;
        this.count = 1;
    }

    @NotNull
    public static CachedContent create(@NotNull String content, long lifeTime) {
        return new CachedContent(content, System.currentTimeMillis(), TimeUtil.createFutureTimestamp(lifeTime));
    }

    public void addCount() {
        this.count++;
    }

    public boolean isExpired() {
        return TimeUtil.isPassed(this.expirationTimestamp);
    }

    @NotNull
    public String content() {
        return this.content;
    }

    public long getCreationTimestamp() {
        return this.creationTimestamp;
    }

    public long getExpirationTimestamp() {
        return this.expirationTimestamp;
    }

    public int getCount() {
        return this.count;
    }
}
