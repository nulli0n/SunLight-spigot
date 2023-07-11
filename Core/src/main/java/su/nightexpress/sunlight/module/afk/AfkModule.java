package su.nightexpress.sunlight.module.afk;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.EngineUtils;
import su.nexmedia.engine.utils.EntityUtil;
import su.nexmedia.engine.utils.TimeUtil;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.data.impl.settings.UserSetting;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.afk.command.AfkCommand;
import su.nightexpress.sunlight.module.afk.config.AfkConfig;
import su.nightexpress.sunlight.module.afk.config.AfkLang;
import su.nightexpress.sunlight.module.afk.config.AfkPerms;
import su.nightexpress.sunlight.module.afk.event.PlayerAfkEvent;
import su.nightexpress.sunlight.module.afk.listener.AfkListener;
import su.nightexpress.sunlight.module.afk.task.AfkTickTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AfkModule extends Module {

    public static final UserSetting<Boolean> SETTING_AFK_STATE = UserSetting.asBoolean("afk.state", false, false);
    public static final UserSetting<Integer> SETTING_IDLE_TIME = UserSetting.asInt("afk.idle_time", 0, false);
    public static final UserSetting<Long>    SETTING_AFK_SINCE = UserSetting.asLong("afk.since", 0L, false);

    private final Map<Player, AfkTracker> trackerMap;

    private AfkTickTask afkTickTask;

    public AfkModule(@NotNull SunLight plugin, @NotNull String id) {
        super(plugin, id);
        this.trackerMap = new ConcurrentHashMap<>();
    }

    @Override
    public void onLoad() {
        this.plugin.registerPermissions(AfkPerms.class);
        this.plugin.getLangManager().loadMissing(AfkLang.class);
        this.plugin.getLang().saveChanges();
        this.getConfig().initializeOptions(AfkConfig.class);

        this.plugin.getCommandRegulator().register(AfkCommand.NAME, (cfg1, aliases) -> new AfkCommand(this, aliases));
        this.addListener(new AfkListener(this));

        this.afkTickTask = new AfkTickTask(this);
        this.afkTickTask.start();
    }

    @Override
    public void onShutdown() {
        if (this.afkTickTask != null) {
            this.afkTickTask.stop();
            this.afkTickTask = null;
        }

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            this.exitAfk(player);
        }
        this.trackerMap.clear();
    }

    public boolean isAfk(@NotNull Player player) {
        return isAfk(this.plugin.getUserManager().getUserData(player));
    }

    public static boolean isAfk(@NotNull SunUser user) {
        return user.getSettings().get(SETTING_AFK_STATE);
    }

    public static void setAfkState(@NotNull SunUser user, boolean isAfk) {
        user.getSettings().set(SETTING_AFK_STATE, isAfk);
    }

    public static int getIdleTime(@NotNull SunUser user) {
        return user.getSettings().get(SETTING_IDLE_TIME);
    }

    public static void setIdleTime(@NotNull SunUser user, int afkTime) {
        user.getSettings().set(SETTING_IDLE_TIME, afkTime);
    }

    public static long getAfkSince(@NotNull SunUser user) {
        return user.getSettings().get(SETTING_AFK_SINCE);
    }

    public static void setAfkSince(@NotNull SunUser user, long since) {
        user.getSettings().set(SETTING_AFK_SINCE, since);
    }

    @NotNull
    public AfkTracker getTrack(@NotNull Player player) {
        return this.trackerMap.computeIfAbsent(player, k -> new AfkTracker(this, player));
    }

    public void untrack(@NotNull Player player) {
        this.exitAfk(player);
        this.trackerMap.remove(player);
    }

    public void exitAfk(@NotNull Player player) {
        if (EntityUtil.isNPC(player)) return;

        SunUser user = this.plugin.getUserManager().getUserData(player);
        if (!isAfk(user)) return;

        PlayerAfkEvent event = new PlayerAfkEvent(player, user, false);
        this.plugin.getPluginManager().callEvent(event);

        this.plugin.getMessage(AfkLang.AFK_EXIT)
            .replace(Placeholders.forPlayer(player))
            .replace(Placeholders.GENERIC_TIME, TimeUtil.formatTime(System.currentTimeMillis() - getAfkSince(user)))
            .broadcast();

        setAfkState(user, false);
        setIdleTime(user, 0);
        setAfkSince(user, 0L);
        this.getTrack(player).clear();
        this.getTrack(player).setSleepCooldown();

        AfkConfig.WAKE_UP_COMMANDS.get().forEach(command -> {
            if (EngineUtils.hasPlaceholderAPI()) command = PlaceholderAPI.setPlaceholders(player, command);
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), Placeholders.forPlayer(player).apply(command));
        });
    }

    public void enterAfk(@NotNull Player player) {
        if (EntityUtil.isNPC(player)) return;

        SunUser user = plugin.getUserManager().getUserData(player);
        if (isAfk(user)) return;

        setAfkState(user, true);
        setAfkSince(user, System.currentTimeMillis());
        this.getTrack(player).setLastPosition();

        PlayerAfkEvent event = new PlayerAfkEvent(player, user, true);
        this.plugin.getPluginManager().callEvent(event);

        AfkConfig.AFK_COMMANDS.get().forEach(command -> {
            if (EngineUtils.hasPlaceholderAPI()) command = PlaceholderAPI.setPlaceholders(player, command);
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), Placeholders.forPlayer(player).apply(command));
        });

        this.plugin.getMessage(AfkLang.AFK_ENTER).replace(Placeholders.forPlayer(player)).broadcast();
    }

    public static int getTimeToAfk(@NotNull Player player) {
        return AfkConfig.AFK_IDLE_TIMES.get().getBestValue(player, -1);
    }

    public static int getTimeToKick(@NotNull Player player) {
        return AfkConfig.AFK_KICK_TIMES.get().getBestValue(player, -1);
    }
}
