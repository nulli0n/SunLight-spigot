package su.nightexpress.sunlight.user;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import su.nightexpress.nightcore.user.AbstractUserManager;
import su.nightexpress.nightcore.user.UserInfo;
import su.nightexpress.nightcore.user.data.DefaultUserDataAccessor;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.sunlight.SLUtils;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandKey;
import su.nightexpress.sunlight.data.DataHandler;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserManager extends AbstractUserManager<SunLightPlugin, SunUser> {

    private final DataHandler dataHandler;

    public UserManager(@NonNull SunLightPlugin plugin, @NonNull DataHandler dataHandler) {
        super(plugin, new DefaultUserDataAccessor<>(dataHandler, dataHandler));
        this.dataHandler = dataHandler;
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        this.addListener(new UserListener(this.plugin, this));

        // TODO Placeholders
        /*if (params.startsWith("command_is_on_cooldown_")) {
            String name = params.substring("command_is_on_cooldown_".length());
            return CoreLang.getYesOrNo(user.getCooldown(CooldownType.COMMAND, name).isPresent());
        }
        if (params.startsWith("command_cooldown_")) {
            String name = params.substring("command_cooldown_".length());
            return user.getCooldown(CooldownType.COMMAND, name).map(c -> TimeUtil.formatDuration(c.getExpireDate())).orElse("-");
        }*/
    }

    @Override
    protected void synchronize(@NonNull SunUser fetched, @NonNull SunUser cached) {
        cached.updateFrom(fetched);
    }

    @Override
    @NonNull
    protected SunUser create(@NonNull UUID uuid, @NonNull String name, @NonNull InetAddress address) {
        long timestamp = System.currentTimeMillis();
        Map<CommandKey, Long> commandCooldowns = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();

        SunUser user = new SunUser(uuid, name, timestamp, timestamp, address, commandCooldowns, properties);
        user.setFirstTimeJoined(true);

        return user;
    }

    @NonNull
    public CompletableFuture<Player> loadTargetPlayer(@NonNull String playerName) {
        return this.loadTargetProfile(playerName).thenCompose(this::loadTargetPlayer);
    }

    @NonNull
    public CompletableFuture<Player> loadTargetPlayer(@NonNull UserInfo profile) {
        return this.loadTargetPlayer(profile.id(), profile.name());
    }

    @NonNull
    public CompletableFuture<Player> loadTargetPlayer(@NonNull SunUser user) {
        return this.loadTargetPlayer(user.getId(), user.getName());
    }

    @NonNull
    public CompletableFuture<Player> loadTargetPlayer(@NonNull UUID id, @NonNull String name) {
        Player target = Players.getPlayer(id);
        if (target != null) return CompletableFuture.completedFuture(target);

        return CompletableFuture.supplyAsync(() -> this.plugin.internals().map(nms -> nms.loadPlayerData(id, name)).orElse(null));
    }

    @NonNull
    public CompletableFuture<UserInfo> loadTargetProfile(@NonNull String playerName) {
        Player target = Players.getPlayer(playerName);
        if (target != null) return CompletableFuture.completedFuture(UserInfo.of(target));

        return CompletableFuture.supplyAsync(() -> this.dataHandler.loadProfile(playerName).orElse(null));
    }

    @NonNull
    public CompletableFuture<InetAddress> loadInetAddress(@NonNull UUID playerId) {
        Player target = Players.getPlayer(playerId);
        if (target != null) return CompletableFuture.completedFuture(SLUtils.getInetAddress(target).orElse(null));

        return CompletableFuture.supplyAsync(() -> this.dataHandler.loadInetAddress(playerId).orElse(null));
    }

}
