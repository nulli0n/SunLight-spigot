package su.nightexpress.sunlight.module.scoreboard;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.Version;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.core.user.settings.Setting;
import su.nightexpress.sunlight.core.user.settings.SettingRegistry;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.hook.HookId;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleInfo;
import su.nightexpress.sunlight.module.scoreboard.board.Board;
import su.nightexpress.sunlight.module.scoreboard.board.BoardProvider;
import su.nightexpress.sunlight.module.scoreboard.board.impl.ProtocolBoard;
import su.nightexpress.sunlight.module.scoreboard.command.ScoreboardCommand;
import su.nightexpress.sunlight.module.scoreboard.config.SBConfig;
import su.nightexpress.sunlight.module.scoreboard.config.SBLang;
import su.nightexpress.sunlight.module.scoreboard.config.SBPerms;
import su.nightexpress.sunlight.module.scoreboard.board.BoardConfig;
import su.nightexpress.sunlight.module.scoreboard.board.impl.PacketBoard;
import su.nightexpress.sunlight.module.scoreboard.listener.ScoreboardListener;
import su.nightexpress.sunlight.utils.DynamicText;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreboardModule extends Module {

    public static final Setting<Boolean> SETTING_SCOREBOARD = SettingRegistry.register(Setting.create("scoreboard", true, true));

    private final Map<String, DynamicText> animationMap;
    private final Map<Player, Board>       boardMap;

    private BoardProvider provider;

    public ScoreboardModule(@NotNull SunLightPlugin plugin, @NotNull String id) {
        super(plugin, id);
        this.animationMap = new HashMap<>();
        this.boardMap = new ConcurrentHashMap<>();
    }

    // TODO Auto update scoreboard if player rank/perm changed

    @Override
    protected void gatherInfo(@NotNull ModuleInfo moduleInfo) {
        moduleInfo.setConfigClass(SBConfig.class);
        moduleInfo.setLangClass(SBLang.class);
        moduleInfo.setPermissionsClass(SBPerms.class);
    }

    @Override
    protected void onModuleLoad() {
        this.loadAnimations();
        this.loadProvider();

        this.registerCommands();

        this.addListener(new ScoreboardListener(this.plugin, this));

        this.addTask(this.plugin.createAsyncTask(this::updateBoards).setTicksInterval(1L));
        this.plugin.runTask(task -> this.loadPlayerBoards());
    }

    @Override
    protected void onModuleUnload() {
        this.boardMap.values().forEach(Board::remove);
        this.boardMap.clear();
        this.animationMap.clear();
    }

    private void loadProvider() {
        if (Plugins.isInstalled(HookId.PACKET_EVENTS) && Version.isAtLeast(Version.V1_20_R3)) {
            this.provider = PacketBoard::new;
        }
        else if (Plugins.isInstalled(HookId.PROTOCOL_LIB)) {
            this.provider = ProtocolBoard::new;
        }

        if (this.provider == null) {
            this.warn("No compatible packet API plugins are installed.");
            this.warn("Install one of the following plugins to enable the Scoreboard feature: " + HookId.PACKET_EVENTS + ", " + HookId.PROTOCOL_LIB);
        }
    }

    private void loadAnimations() {
        FileConfig config = FileConfig.loadOrExtract(this.plugin, this.getLocalPath(), SBConfig.FILE_ANIMATIONS);
        if (config.getSection("").isEmpty()) {
            SBConfig.getDefaultAnimations().forEach(dynamicText -> dynamicText.write(config, dynamicText.getId()));
        }

        for (String sId : config.getSection("")) {
            DynamicText dynamicText = DynamicText.read(config, sId, sId);
            this.animationMap.put(dynamicText.getId(), dynamicText);
        }
        config.saveChanges();
    }

    private void loadPlayerBoards() {
        this.plugin.getServer().getOnlinePlayers().forEach(player -> {
            if (this.isScoreboardEnabled(player)) this.addBoard(player);
        });
    }

    private void registerCommands() {
        ScoreboardCommand.load(this.plugin, this);
    }

    @Nullable
    public BoardConfig getBoardConfig(@NotNull Player player) {
        Set<String> groups = Players.getPermissionGroups(player);

        return SBConfig.BOARD_CONFIGS.get().values().stream()
            .filter(board -> board.isGoodWorld(player.getWorld()) && board.isGoodRank(groups))
            .max(Comparator.comparingInt(BoardConfig::getPriority))
            .orElse(null);
    }

    @NotNull
    public Collection<DynamicText> getAnimations() {
        return this.animationMap.values();
    }

    @NotNull
    public Map<Player, Board> getBoardMap() {
        return boardMap;
    }

    @NotNull
    public Collection<Board> getBoards() {
        return new HashSet<>(this.boardMap.values());
    }

    @Nullable
    public Board getBoard(@NotNull Player player) {
        return this.boardMap.get(player);
    }


    public void updateBoards() {
        if (this.boardMap.isEmpty()) return;

        this.getBoards().forEach(Board::updateIfReady);
    }

    public boolean hasBoard(@NotNull Player player) {
        return this.getBoard(player) != null;
    }

    public void addBoard(@NotNull Player player) {
        BoardConfig boardConfig = this.getBoardConfig(player);
        if (boardConfig == null) return;

        this.addBoard(player, boardConfig);
    }

    public void addBoard(@NotNull Player player, @NotNull BoardConfig boardConfig) {
        if (this.provider == null) return;
        if (this.hasBoard(player)) return;

        this.boardMap.computeIfAbsent(player, k -> this.provider.create(player, this, boardConfig)).create();
    }

    public void removeBoard(@NotNull Player player) {
        Board board = this.boardMap.remove(player);
        if (board == null) return;

        board.remove();
    }

    public void toggleBoard(@NotNull Player player) {
        if (!this.hasBoard(player)) {
            this.addBoard(player);
        }
        else {
            this.removeBoard(player);
        }
    }

    public boolean isScoreboardEnabled(@NotNull Player player) {
        SunUser user = plugin.getUserManager().getOrFetch(player);
        return user.getSettings().get(SETTING_SCOREBOARD);
    }
}
