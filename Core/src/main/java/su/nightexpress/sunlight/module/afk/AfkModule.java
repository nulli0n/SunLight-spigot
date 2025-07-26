package su.nightexpress.sunlight.module.afk;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleInfo;
import su.nightexpress.sunlight.module.afk.command.AfkCommand;
import su.nightexpress.sunlight.module.afk.config.AfkConfig;
import su.nightexpress.sunlight.module.afk.config.AfkLang;
import su.nightexpress.sunlight.module.afk.config.AfkPerms;
import su.nightexpress.sunlight.module.afk.listener.AfkListener;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AfkModule extends Module {

    private final Map<UUID, AfkState> stateMap;

    public AfkModule(@NotNull SunLightPlugin plugin, @NotNull String id) {
        super(plugin, id);
        this.stateMap = new ConcurrentHashMap<>();
    }

    @Override
    protected void gatherInfo(@NotNull ModuleInfo moduleInfo) {
        moduleInfo.setConfigClass(AfkConfig.class);
        moduleInfo.setLangClass(AfkLang.class);
        moduleInfo.setPermissionsClass(AfkPerms.class);
    }

    @Override
    protected void onModuleLoad() {
        this.registerCommands();

        this.addListener(new AfkListener(this.plugin, this));

        this.addTask(this.plugin.createAsyncTask(this::tickAfkStates).setSecondsInterval(1));

        this.plugin.getServer().getOnlinePlayers().forEach(this::track);
    }

    @Override
    protected void onModuleUnload() {
        this.stateMap.values().forEach(AfkState::exitAfk);
        this.stateMap.clear();
    }

    private void registerCommands() {
        AfkCommand.load(this.plugin, this);
    }

    private void tickAfkStates() {
        new HashSet<>(this.stateMap.values()).forEach(AfkState::tick);
    }

    public void kick(@NotNull Player player) {
        int timeToKick = AfkModule.getTimeToKick(player);
        String reason = String.join("\n", AfkConfig.AFK_KICK_MESSAGE.get()).replace(Placeholders.GENERIC_TIME, TimeUtil.formatTime(timeToKick * 1000L));

        player.kickPlayer(NightMessage.asLegacy(reason));
    }

    public boolean isAfk(@NotNull Player player) {
        AfkState state = this.getState(player);
        return state != null && state.isAfk();
    }

    public void track(@NotNull Player player) {
        AfkState state = new AfkState(this.plugin, this, player);
        this.stateMap.put(player.getUniqueId(), state);
    }

    public void untrack(@NotNull Player player, boolean silent) {
        AfkState tracker = this.stateMap.remove(player.getUniqueId());
        if (tracker == null) return;

        tracker.exitAfk(silent);
        tracker.reset();
    }

    public void exitAfk(@NotNull Player player) {
        AfkState state = this.getState(player);
        if (state != null) {
            state.exitAfk();
        }
    }

    public void enterAfk(@NotNull Player player) {
        AfkState state = this.getState(player);
        if (state != null) {
            state.enterAfk();
        }
    }

    public void trackActivity(@NotNull Player player, int amount) {
        AfkState state = this.getState(player);
        if (state == null) return;

        state.onActivity(amount);
    }

    @Nullable
    public AfkState getState(@NotNull Player player) {
        return this.stateMap.get(player.getUniqueId());
    }

    public static int getTimeToAfk(@NotNull Player player) {
        return AfkConfig.AFK_IDLE_TIMES.get().getGreatest(player);
    }

    public static int getTimeToKick(@NotNull Player player) {
        return AfkConfig.AFK_KICK_TIMES.get().getGreatest(player);
    }
}
