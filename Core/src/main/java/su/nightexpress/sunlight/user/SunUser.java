package su.nightexpress.sunlight.user;

import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import su.nightexpress.nightcore.user.UserTemplate;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.sunlight.command.CommandKey;
import su.nightexpress.sunlight.user.cache.UserCacheContainer;
import su.nightexpress.sunlight.user.property.UserProperty;
import su.nightexpress.sunlight.user.property.UserPropertyRegistry;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class SunUser extends UserTemplate {

    private final Map<CommandKey, Long> commandCooldowns;
    private final Map<String, Object>   properties;
    private final Map<Class<? extends UserCacheContainer>, UserCacheContainer> caches;

    private final long dateCreated;

    private InetAddress latestAddress;
    private boolean     firstTimeJoined;
    private long        lastOnline;

    public SunUser(@NonNull UUID uuid,
                   @NonNull String name,
                   long dateCreated,
                   long lastOnline,
                   @Nullable InetAddress latestAddress,
                   @NonNull Map<CommandKey, Long> commandCooldowns,
                   @NonNull Map<String, Object> properties) {
        super(uuid, name);

        this.commandCooldowns = commandCooldowns;
        this.properties = properties;
        this.latestAddress = latestAddress;

        this.dateCreated = dateCreated;
        this.lastOnline = lastOnline;

        this.caches = new ConcurrentHashMap<>();
    }

    public void updateFrom(@NonNull SunUser other) {
        this.commandCooldowns.clear();
        this.properties.clear();

        this.commandCooldowns.putAll(other.commandCooldowns);
        this.properties.putAll(other.properties);
    }

    @NonNull
    public <T extends UserCacheContainer> Optional<T> getCache(@NonNull Class<T> type) {
        UserCacheContainer container = this.caches.get(type);
        if (container == null) return Optional.empty();

        T cache = type.cast(container);
        cache.clearExpired();

        return Optional.of(cache);
    }

    @NonNull
    public <T extends UserCacheContainer> T getCacheOrCreate(@NonNull Class<T> type, @NonNull Supplier<T> supplier) {
        if (this.caches.containsKey(type)) {
            return this.getCache(type).orElseThrow();
        }

        T cache = supplier.get();
        this.caches.put(type, cache);

        return cache;
    }

    @NonNull
    public Map<String, Object> getProperties() {
        return this.properties;
    }

    @NonNull
    public Map<String, Object> getPropertiesToSave() {
        Map<String, Object> map = new HashMap<>();
        for (UserProperty<?> property : UserPropertyRegistry.values()) {
            if (!property.isPersistent()) continue;

            Object value = this.properties.get(property.getName());
            if (value == null) continue;

            map.put(property.getName(), value);
        }
        return map;
    }

    public <T> boolean hasProperty(@NonNull UserProperty<T> property) {
        return this.properties.containsKey(property.getName());
    }

    public <T> void setProperty(@NonNull UserProperty<T> property, @NonNull T value) {
        this.properties.put(property.getName(), value);
    }

    @NonNull
    public <T> T getPropertyOrDefault(@NonNull UserProperty<T> property) {
        return this.getPropertyOr(property, property.getDefaultValue());
    }

    @NonNull
    public <T> T getPropertyOr(@NonNull UserProperty<T> property, @NonNull T defaultvalue) {
        return this.getProperty(property.getName(), property.getType(), defaultvalue);
    }

    @NonNull
    public <T> T getProperty(@NonNull String name, @NonNull Class<T> type, @NonNull T defaultValue) {
        String key = LowerCase.INTERNAL.apply(name);

        Object value = this.properties.get(key);
        if (value == null) return defaultValue;

        if (!type.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("User property '%s' is defined as %s, not %s".formatted(name, value.getClass().getSimpleName(), type.getSimpleName()));
        }

        return type.cast(value);
    }

    public <T> void removeProperty(@NonNull UserProperty<T> property) {
        this.removeProperty(property.getName());
    }

    public void removeProperty(@NonNull String property) {
        this.properties.remove(LowerCase.INTERNAL.apply(property));
    }

    @Nullable
    public Long getCommandCooldown(@NonNull CommandKey key) {
        return this.commandCooldowns.get(key);
    }

    public void setCommandCooldown(@NonNull CommandKey key, long expireDate) {
        this.commandCooldowns.put(key, expireDate);
    }
    
    @NonNull
    public Map<CommandKey, Long> getCommandCooldowns() {
        return this.commandCooldowns;
    }
    


    public void setFirstTimeJoined(boolean firstTimeJoined) {
        this.firstTimeJoined = firstTimeJoined;
    }

    public boolean isFirstTimeJoined() {
        return this.firstTimeJoined;
    }

    public boolean hasPlayedBefore() {
        return !this.firstTimeJoined;
    }



    public long getDateCreated() {
        return this.dateCreated;
    }

    public long getLastOnline() {
        return this.lastOnline;
    }

    public void setLastOnline(long lastOnline) {
        this.lastOnline = lastOnline;
    }
    
    @NonNull
    public Optional<InetAddress> getLatestAddress() {
        return Optional.ofNullable(this.latestAddress);
    }

    public void setLatestAddress(@Nullable InetAddress address) {
        this.latestAddress = address;
    }
}
