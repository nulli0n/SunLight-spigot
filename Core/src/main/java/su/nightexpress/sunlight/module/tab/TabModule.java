package su.nightexpress.sunlight.module.tab;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.nightcore.util.placeholder.PlaceholderContext;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.hook.placeholder.PlaceholderRegistry;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleContext;
import su.nightexpress.sunlight.module.tab.format.TabLayoutFormat;
import su.nightexpress.sunlight.module.tab.format.TabNameFormat;
import su.nightexpress.sunlight.module.tab.listener.TabListener;
import su.nightexpress.sunlight.utils.DynamicText;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TabModule extends Module {

    private final TabSettings settings;

    private final Map<String, DynamicText> animationMap;
    private final Map<UUID, TabPlayer>     playerMap;

    public TabModule(@NotNull ModuleContext context) {
        super(context);
        this.settings = new TabSettings();
        this.animationMap = new HashMap<>();
        this.playerMap = new ConcurrentHashMap<>();
    }

    @Override
    protected void loadModule(@NotNull FileConfig config) {
        this.settings.load(config);

        this.loadAnimations();

        this.addListener(new TabListener(this.plugin, this));

        this.addAsyncTask(this::updatePlayerList, this.settings.getPlayerListUpdateInterval());
        this.addAsyncTask(this::sortPlayerList, this.settings.getPlayerListSortInterval());

        Players.getOnline().forEach(this::loadPlayer);
    }

    @Override
    protected void unloadModule() {
        this.animationMap.clear();
        this.playerMap.clear();
    }

    private void loadAnimations() {
        FileConfig config = FileConfig.load(this.getSystemPath(), TabFiles.FILE_ANIMATIONS);
        if (config.getSection("").isEmpty()) {
            TabDefaults.getDefaultAnimations().forEach(animator -> animator.write(config, animator.getId()));
        }

        for (String sId : config.getSection("")) {
            DynamicText animator = DynamicText.read(config, sId, sId);
            this.animationMap.put(animator.getId(), animator);
        }

        config.saveChanges();
    }

    @Override
    protected void registerPermissions(@NotNull PermissionTree root) {

    }

    @Override
    protected void registerCommands() {

    }

    @Override
    public void registerPlaceholders(@NotNull PlaceholderRegistry registry) {

    }

    @NotNull
    public Set<DynamicText> getAnimations() {
        return Set.copyOf(this.animationMap.values());
    }

    @NotNull
    public Set<TabPlayer> getPlayers() {
        return Set.copyOf(this.playerMap.values());
    }

    @Nullable
    public TabPlayer getPlayer(@NotNull UUID playerId) {
        return this.playerMap.get(playerId);
    }

    public synchronized void loadPlayer(@NotNull Player player) {
        PlaceholderContext context = this.createPlaceholderContext(player);
        TabPlayer tabPlayer = new TabPlayer(player, context);

        this.playerMap.put(player.getUniqueId(), tabPlayer);
    }

    public synchronized void unloadPlayer(@NotNull UUID playerId) {
        this.playerMap.remove(playerId);
    }

    @Nullable
    public TabLayoutFormat getPlayerListLayoutFormat(@NotNull Player player) {
        return this.settings.getPlayerListLayoutFormatsMap().values().stream()
            .filter(format -> format.isAvailable(player))
            .max(Comparator.comparingInt(TabLayoutFormat::getPriority))
            .orElse(null);
    }

    @Nullable
    public TabNameFormat getPlayerListNameFormat(@NotNull Player player) {
        return this.settings.getPlayerListNameFormatsMap().values().stream()
            .filter(format -> format.isAvailable(player))
            .max(Comparator.comparingInt(TabNameFormat::getPriority))
            .orElse(null);
    }



    public void handleJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        this.loadPlayer(player);
        this.updatePlayerList(player);
        this.sortPlayerList();
    }

    public void handleQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();

        this.unloadPlayer(player.getUniqueId());
    }

    public void updatePlayerList(@NotNull TabPlayer tabPlayer) {
        Player player = tabPlayer.getPlayer();

        TabNameFormat nameFormat = this.getPlayerListNameFormat(player);
        TabLayoutFormat layoutFormat = this.getPlayerListLayoutFormat(player);

        tabPlayer.setNameFormat(nameFormat);
        tabPlayer.setLayoutFormat(layoutFormat);
        tabPlayer.updatePlayerList();
    }

    public void updatePlayerList(@NotNull Player player) {
        TabPlayer tabPlayer = this.getPlayer(player.getUniqueId());
        if (tabPlayer == null) return;

        this.updatePlayerList(tabPlayer);
    }

    public void updatePlayerList() {
        this.getPlayers().forEach(this::updatePlayerList);
    }

    public void sortPlayerList() {
        List<? extends Player> sorted = this.plugin.getServer().getOnlinePlayers().stream()
            .sorted(Comparator.comparingInt(this::getRankOrder).reversed().thenComparing(Player::getName))
            .toList();
        if (sorted.isEmpty()) return;

        int size = sorted.size();

        for (int index = 0; index < sorted.size(); index++) {
            int order = size - index;
            Player player = sorted.get(index);

            player.setPlayerListOrder(order);
        }
    }

    private int getRankOrder(@NotNull Player player) {
        Map<String, Integer> rankOrderMap = this.settings.getPlayerListRankOrderMap();
        int defValue = rankOrderMap.getOrDefault(SLPlaceholders.DEFAULT, 0);

        Set<String> ranks = Players.getInheritanceGroups(player);

        return ranks.stream().mapToInt(rank -> rankOrderMap.getOrDefault(rank, defValue)).max().orElse(defValue);
    }

    @NotNull
    private PlaceholderContext createPlaceholderContext(@NotNull Player player) {
        PlaceholderContext.Builder builder = PlaceholderContext.builder()
            .with(CommonPlaceholders.PLAYER.resolver(player))
            .andThen(CommonPlaceholders.forPlaceholderAPI(player));

        for (DynamicText animator : this.getAnimations()) {
            builder.with(SLPlaceholders.ANIMATION.apply(animator.getId()), animator::getMessage);
        }

        return builder.build();
    }
}
