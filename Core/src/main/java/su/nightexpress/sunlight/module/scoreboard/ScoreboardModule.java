package su.nightexpress.sunlight.module.scoreboard;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.nightcore.util.placeholder.PlaceholderContext;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.hook.HookId;
import su.nightexpress.sunlight.hook.placeholder.PlaceholderRegistry;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleContext;
import su.nightexpress.sunlight.module.scoreboard.board.Board;
import su.nightexpress.sunlight.module.scoreboard.board.BoardDefinition;
import su.nightexpress.sunlight.module.scoreboard.board.BoardProvider;
import su.nightexpress.sunlight.module.scoreboard.board.impl.PacketBoard;
import su.nightexpress.sunlight.module.scoreboard.board.impl.ProtocolBoard;
import su.nightexpress.sunlight.module.scoreboard.command.ScoreboardCommand;
import su.nightexpress.sunlight.module.scoreboard.config.SBConfig;
import su.nightexpress.sunlight.module.scoreboard.config.SBLang;
import su.nightexpress.sunlight.module.scoreboard.config.SBPerms;
import su.nightexpress.sunlight.module.scoreboard.listener.ScoreboardListener;
import su.nightexpress.sunlight.user.SunUser;
import su.nightexpress.sunlight.user.property.UserPropertyRegistry;
import su.nightexpress.sunlight.utils.DynamicText;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreboardModule extends Module {

    private final SBConfig settings;
    private final Map<String, DynamicText> animationMap;
    private final Map<Player, Board>       boardMap;

    private BoardProvider provider;

    public ScoreboardModule(@NotNull ModuleContext context) {
        super(context);
        this.settings = new SBConfig();
        this.animationMap = new HashMap<>();
        this.boardMap = new ConcurrentHashMap<>();
    }

    // TODO Auto update scoreboard if player rank/perm changed

    @Override
    protected void loadModule(@NotNull FileConfig config) {
        this.settings.load(config);
        this.plugin.injectLang(SBLang.class);
        UserPropertyRegistry.register(ScoreboardProperties.SCOREBOARD);

        this.loadAnimations();
        this.loadProvider();

        this.addListener(new ScoreboardListener(this.plugin, this));

        this.addAsyncTask(this::updateBoards, 1L);
        this.plugin.runTask(task -> this.loadPlayerBoards());
    }

    @Override
    protected void unloadModule() {
        this.boardMap.values().forEach(Board::remove);
        this.boardMap.clear();
        this.animationMap.clear();
    }

    @Override
    protected void registerPermissions(@NotNull PermissionTree root) {
        root.merge(SBPerms.MODULE);
    }

    @Override
    protected void registerCommands() {
        this.commandRegistry.addProvider("scoreboard", new ScoreboardCommand(this.plugin, this, this.userManager));
    }

    @Override
    public void registerPlaceholders(@NotNull PlaceholderRegistry registry) {
        registry.register("scoreboard_state", (player, payload) -> {
            return CoreLang.STATE_YES_NO.get(this.isScoreboardEnabled(player));
        });
    }

    private void loadProvider() {
        if (Plugins.isInstalled(HookId.PACKET_EVENTS)) {
            this.provider = PacketBoard::new;
        }
        else if (Plugins.isInstalled(HookId.PROTOCOL_LIB)) {
            this.provider = ProtocolBoard::new;
        }
    }

    private void loadAnimations() {
        FileConfig config = FileConfig.load(this.getSystemPath(), ScoreboardDefaults.FILE_ANIMATIONS);
        if (config.getSection("").isEmpty()) {
            ScoreboardDefaults.getDefaultAnimations().forEach(dynamicText -> dynamicText.write(config, dynamicText.getId()));
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



    @Nullable
    public BoardDefinition getBoardDefinition(@NotNull Player player) {
        return this.getBoardDefinition(player, this.createPlaceholderContext(player));
    }

    @Nullable
    public BoardDefinition getBoardDefinition(@NotNull Player player, @NotNull PlaceholderContext placeholderContext) {
        return this.settings.getBoardDefinitionMap().values().stream()
            .filter(board -> board.isAvailable(player, placeholderContext, this.plugin.getLogger()))
            .max(Comparator.comparingInt(BoardDefinition::getPriority))
            .orElse(null);
    }

    @NotNull
    public Set<DynamicText> getAnimations() {
        return Set.copyOf(this.animationMap.values());
    }

    @NotNull
    public Map<Player, Board> getBoardMap() {
        return Map.copyOf(this.boardMap);
    }

    @NotNull
    public Set<Board> getBoards() {
        return Set.copyOf(this.boardMap.values());
    }

    @Nullable
    public Board getBoard(@NotNull Player player) {
        return this.boardMap.get(player);
    }


    public void updateBoards() {
        if (this.boardMap.isEmpty()) return;

        this.getBoardMap().forEach((player, board) -> {
            boolean updated = board.updateIfReady();
            if (!updated) return;

            PlaceholderContext placeholderContext = this.createPlaceholderContext(player);
            BoardDefinition desiredBoard = this.getBoardDefinition(player, placeholderContext);
            BoardDefinition currentBoard = board.getBoardConfig();

            if (desiredBoard == null) {
                this.removeBoard(player);
                return;
            }

            if (desiredBoard != currentBoard) {
                this.removeBoard(player);
                this.addBoard(player, desiredBoard);
            }
        });
    }

    public boolean hasBoard(@NotNull Player player) {
        return this.getBoard(player) != null;
    }

    public void addBoard(@NotNull Player player) {
        BoardDefinition boardDefinition = this.getBoardDefinition(player);
        if (boardDefinition == null) return;

        this.addBoard(player, boardDefinition);
    }

    public synchronized void addBoard(@NotNull Player player, @NotNull BoardDefinition boardDefinition) {
        if (this.provider == null) return;
        if (this.hasBoard(player)) return;

        this.boardMap.computeIfAbsent(player, k -> this.provider.create(player, this.createPlaceholderContext(player), boardDefinition)).create();
    }

    public synchronized void removeBoard(@NotNull Player player) {
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
        return user.getPropertyOrDefault(ScoreboardProperties.SCOREBOARD);
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
