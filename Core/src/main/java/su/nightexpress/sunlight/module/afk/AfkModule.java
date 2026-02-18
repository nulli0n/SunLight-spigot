package su.nightexpress.sunlight.module.afk;

import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.bridge.chat.UniversalChatEvent;
import su.nightexpress.nightcore.bridge.chat.UniversalChatEventHandler;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.geodata.pos.BlockPos;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.nightcore.util.placeholder.PlaceholderContext;
import su.nightexpress.nightcore.util.time.TimeFormatType;
import su.nightexpress.nightcore.util.time.TimeFormats;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.api.provider.AfkProvider;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.hook.placeholder.PlaceholderRegistry;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleContext;
import su.nightexpress.sunlight.module.afk.command.AfkCommandProvider;
import su.nightexpress.sunlight.module.afk.core.AfkLang;
import su.nightexpress.sunlight.module.afk.core.AfkPerms;
import su.nightexpress.sunlight.module.afk.core.AfkSettings;
import su.nightexpress.sunlight.module.afk.event.PlayerAfkEvent;
import su.nightexpress.sunlight.module.afk.listener.AfkListener;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class AfkModule extends Module implements AfkProvider {

    private final Map<UUID, ActivityTracker> activityTrackerMap;
    private final AfkSettings                settings;

    private UniversalChatEventHandler chatEventHandler;

    public AfkModule(@NotNull ModuleContext context) {
        super(context);
        this.activityTrackerMap = new ConcurrentHashMap<>();
        this.settings = new AfkSettings();
    }

    @Override
    protected void loadModule(@NotNull FileConfig config) {
        this.settings.load(config);
        this.plugin.injectLang(AfkLang.class);
        this.registerCommands();

        this.addListener(new AfkListener(this.plugin, this));

        if (this.settings.getActivityPoints(ActivityType.CHAT) > 0) {
            this.chatEventHandler = this::handleChatEvent;
            this.plugin.addChatHandler(EventPriority.MONITOR, this.chatEventHandler);
        }

        this.addTask(this::tickTrackers, 1);

        Players.getOnline().forEach(this::track);
    }

    @Override
    protected void unloadModule() {
        if (this.chatEventHandler != null) {
            this.plugin.removeChatHandler(this.chatEventHandler);
            this.chatEventHandler = null;
        }

        Players.getOnline().forEach(player -> this.exitAfk(player, true));
        this.activityTrackerMap.clear();
    }

    @Override
    protected void registerPermissions(@NotNull PermissionTree root) {
        root.merge(AfkPerms.ROOT);
    }

    @Override
    protected void registerCommands() {
        this.commandRegistry.addProvider("afk", new AfkCommandProvider(this.plugin, this));
    }

    @Override
    public void registerPlaceholders(@NotNull PlaceholderRegistry registry) {
        registry.register("afk_state", (player, payload) -> {
            return CoreLang.STATE_YES_NO.get(this.isAfk(player));
        });

        registry.register("afk_mode", (player, payload) -> {
            return PlaceholderContext.builder()
                .with(SLPlaceholders.GENERIC_TIME, () -> TimeFormats.formatSince(this.getAfkEnterTimestamp(player), TimeFormatType.LITERAL))
                .build()
                .apply(AfkLang.PLACEHOLDER_MODE.get(this.isAfk(player)));
        });

        registry.register("afk_idle_time", (player, payload) -> {
            return String.valueOf(this.getIdleTime(player));
        });

        registry.register("afk_idle_time_formatted", (player, payload) -> {
            return TimeFormats.formatAmount(TimeUnit.MILLISECONDS.convert(this.getIdleTime(player), TimeUnit.SECONDS), TimeFormatType.LITERAL);
        });
    }

    public void kickForIdling(@NotNull Player player) {
        String reason = PlaceholderContext.builder()
            .with(SLPlaceholders.GENERIC_TIME, () -> TimeFormats.formatAmount(TimeUnit.MILLISECONDS.convert(this.getIdleTime(player), TimeUnit.SECONDS), TimeFormatType.LITERAL))
            .andThen(SLPlaceholders.forPlayerWithPAPI(player))
            .build()
            .apply(String.join("\n", this.settings.kickText.get()));

        Players.kick(player, reason);
    }

    @Override
    public boolean isAfk(@NotNull Player player) {
        return this.activityTracker(player).map(ActivityTracker::isAfk).orElse(false);
    }

    public void track(@NotNull Player player) {
        ActivityTracker tracker = new ActivityTracker(this.settings);
        this.activityTrackerMap.put(player.getUniqueId(), tracker);
    }

    public void untrack(@NotNull Player player, boolean silent) {
        this.exitAfk(player, silent);
        this.activityTrackerMap.remove(player.getUniqueId());
    }

    public void exitAfk(@NotNull Player player, boolean silent) {
        this.activityTracker(player).filter(ActivityTracker::isAfk).ifPresent(tracker -> {
            this.handleAfkExit(player, tracker, silent);
            tracker.resetCounters();
        });
    }

    public void enterAfk(@NotNull Player player, boolean silent) {
        this.activityTracker(player).filter(Predicate.not(ActivityTracker::isAfk)).ifPresent(tracker -> {
            this.handleAfkEnter(player, tracker, silent);
        });
    }

    public void trackActivity(@NotNull Player player, @NotNull ActivityType type) {
        this.activityTracker(player).ifPresent(tracker -> tracker.countActivity(type));
    }

    public void trackActivity(@NotNull Player player, int amount) {
        this.activityTracker(player).ifPresent(tracker -> tracker.countActivity(amount));
    }

    private void tickTrackers() {
        Map.copyOf(this.activityTrackerMap).forEach((playerId, tracker) -> {
            Player player = Players.getPlayer(playerId);
            if (player == null) return;

            tracker.updatePosition(BlockPos.from(player.getLocation()));
            tracker.tick();

            if (tracker.isAfk()) {
                if (tracker.isEnoughActivity()) {
                    this.handleAfkExit(player, tracker, false);
                    return;
                }

                if (this.settings.afkStatusBarEnabled.get()) {
                    Players.sendActionBar(player, PlaceholderContext.builder()
                        .with(SLPlaceholders.GENERIC_TIME, () -> TimeFormats.formatSince(tracker.getAfkEnterTimestamp(), TimeFormatType.LITERAL))
                        .andThen(SLPlaceholders.forPlayerWithPAPI(player))
                        .build()
                        .apply(this.settings.afkStatusBarText.get())
                    );
                }
            }
            else if (!tracker.isAfk()) {
                int idleTime = tracker.getIdleTime();
                int timeToAfk = this.getTimeToAfk(player);
                if (timeToAfk > 0 && idleTime >= timeToAfk) {

                    int timeToKick = this.getTimeToKick(player);
                    if (timeToKick > 0 && idleTime >= timeToKick) {
                        this.kickForIdling(player);
                        return;
                    }

                    this.handleAfkEnter(player, tracker, false);
                }
            }
        });
    }

    private void handleChatEvent(@NotNull UniversalChatEvent event) {
        if (event.isCancelled()) return;

        this.trackActivity(event.getPlayer(), this.settings.getActivityPoints(ActivityType.CHAT));
    }

    private void handleAfkEnter(@NotNull Player player, @NotNull ActivityTracker tracker, boolean silent) {
        tracker.resetCounters();
        tracker.setAfkTimestamp();

        PlaceholderContext context = PlaceholderContext.builder()
            .with(CommonPlaceholders.PLAYER.resolver(player))
            .andThen(CommonPlaceholders.forPlaceholderAPI(player))
            .build();

        List<String> commands = context.apply(this.settings.afkCommands.get());
        Players.dispatchCommands(player, commands);

        PlayerAfkEvent event = new PlayerAfkEvent(player, true);
        this.plugin.getPluginManager().callEvent(event);

        if (!silent) this.broadcastPrefixed(AfkLang.AFK_ENTER_BROADCAST, context);
    }

    private void handleAfkExit(@NotNull Player player, @NotNull ActivityTracker tracker, boolean silent) {
        PlaceholderContext context = PlaceholderContext.builder()
            .with(SLPlaceholders.GENERIC_TIME, () -> TimeFormats.formatSince(tracker.getAfkEnterTimestamp(), TimeFormatType.LITERAL))
            .with(CommonPlaceholders.PLAYER.resolver(player))
            .andThen(CommonPlaceholders.forPlaceholderAPI(player))
            .build();

        List<String> commands = context.apply(this.settings.wakeUpCommands.get());
        Players.dispatchCommands(player, commands);

        PlayerAfkEvent event = new PlayerAfkEvent(player, false);
        this.plugin.getPluginManager().callEvent(event);

        if (!silent) this.broadcastPrefixed(AfkLang.AFK_EXIT_BROADCAST, context);

        tracker.resetCounters();
    }

    @NotNull
    public Set<ActivityTracker> getActivityTrackers() {
        return new HashSet<>(this.activityTrackerMap.values());
    }

    @Nullable
    public ActivityTracker getActivityTracker(@NotNull Player player) {
        return this.getActivityTracker(player.getUniqueId());
    }

    @Nullable
    public ActivityTracker getActivityTracker(@NotNull UUID playerId) {
        return this.activityTrackerMap.get(playerId);
    }

    @NotNull
    public Optional<ActivityTracker> activityTracker(@NotNull Player player) {
        return Optional.ofNullable(this.getActivityTracker(player));
    }

    public int getTimeToAfk(@NotNull Player player) {
        return this.settings.idleAfkTimes.get().getGreatest(player).intValue();
    }

    public int getTimeToKick(@NotNull Player player) {
        return this.settings.idleKickTimes.get().getGreatest(player).intValue();
    }

    public int getIdleTime(@NotNull Player player) {
        return this.activityTracker(player).map(ActivityTracker::getIdleTime).orElse(0);
    }

    public long getAfkEnterTimestamp(@NotNull Player player) {
        return this.activityTracker(player).map(ActivityTracker::getAfkEnterTimestamp).orElse(0L);
    }
}
