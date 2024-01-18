package su.nightexpress.sunlight.module.scoreboard;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.integration.VaultHook;
import su.nexmedia.engine.utils.EngineUtils;
import su.nexmedia.engine.utils.EntityUtil;
import su.nexmedia.engine.utils.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.data.impl.settings.UserSetting;
import su.nightexpress.sunlight.hook.HookId;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.scoreboard.command.ScoreboardCommand;
import su.nightexpress.sunlight.module.scoreboard.config.SBConfig;
import su.nightexpress.sunlight.module.scoreboard.config.SBLang;
import su.nightexpress.sunlight.module.scoreboard.config.SBPerms;
import su.nightexpress.sunlight.module.scoreboard.impl.Board;
import su.nightexpress.sunlight.module.scoreboard.impl.BoardConfig;
import su.nightexpress.sunlight.module.scoreboard.listener.ScoreboardListener;
import su.nightexpress.sunlight.module.scoreboard.task.BoardUpdateTask;
import su.nightexpress.sunlight.utils.SimpleTextAnimator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreboardModule extends Module {

    public static final UserSetting<Boolean> SETTING_SCOREBOARD = UserSetting.register("scoreboard", true, UserSetting.PARSER_BOOLEAN, true);

    public final  Map<String, SimpleTextAnimator> animationMap;
    private final Map<Player, Board>              playerBoardMap;

    private BoardUpdateTask boardTask;

    public ScoreboardModule(@NotNull SunLight plugin, @NotNull String id) {
        super(plugin, id);
        this.animationMap = new HashMap<>();
        this.playerBoardMap = new ConcurrentHashMap<>();
    }

    @Override
    public boolean canLoad() {
        if (!EngineUtils.hasPlugin(HookId.PROTOCOL_LIB)) {
            this.error("You must have " + HookId.PROTOCOL_LIB + " installed to use " + this.getName() + " module!");
            return false;
        }
        return true;
    }

    @Override
    public void onLoad() {
        this.plugin.registerPermissions(SBPerms.class);
        this.plugin.getLangManager().loadMissing(SBLang.class);
        this.plugin.getLang().saveChanges();
        this.getConfig().initializeOptions(SBConfig.class);

        JYML animConf = JYML.loadOrExtract(this.plugin, this.getLocalPath(), "animations.yml");
        for (String animId : animConf.getSection("")) {
            SimpleTextAnimator animation = SimpleTextAnimator.read(animConf, animId, animId);
            this.animationMap.put(animation.getId(), animation);
        }

        this.plugin.getCommandRegulator().register(ScoreboardCommand.NAME, (cfg1, aliases) -> new ScoreboardCommand(this, aliases), "board");

        this.addListener(new ScoreboardListener(this));

        this.boardTask = new BoardUpdateTask(this);
        this.boardTask.start();

        this.plugin.runTask(task -> {
            this.plugin.getServer().getOnlinePlayers().forEach(player -> {
                if (this.isScoreboardEnabled(player)) this.addBoard(player);
            });
        });
    }

    @Override
    public void onShutdown() {
        if (this.boardTask != null) {
            this.boardTask.stop();
            this.boardTask = null;
        }
        this.playerBoardMap.values().forEach(Board::remove);
        this.playerBoardMap.clear();
        this.animationMap.clear();
    }

    @Nullable
    public BoardConfig getBoardConfig(@NotNull Player player) {
        //Set<String> groups = PlayerUtil.getPermissionGroups(player);

        return SBConfig.BOARD_CONFIGS.get().values().stream()
            .filter(board -> board.getGroups().contains(VaultHook.getPermissionGroup(player)) || board.getGroups().contains(Placeholders.WILDCARD))
            .filter(board -> board.getWorlds().contains(player.getWorld().getName()) || board.getWorlds().contains(Placeholders.WILDCARD))
            .max(Comparator.comparingInt(BoardConfig::getPriority))
            .orElse(null);
    }

    @NotNull
    public Map<Player, Board> getPlayerBoardMap() {
        return playerBoardMap;
    }

    @Nullable
    public Board getBoard(@NotNull Player player) {
        return this.playerBoardMap.get(player);
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
        if (EntityUtil.isNPC(player)) return;
        if (this.hasBoard(player)) return;

        this.playerBoardMap.computeIfAbsent(player, k -> new Board(player, this, boardConfig)).create();
    }

    public void removeBoard(@NotNull Player player) {
        if (EntityUtil.isNPC(player)) return;

        Board board = this.playerBoardMap.remove(player);
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
        SunUser user = plugin.getUserManager().getUserData(player);
        return user.getSettings().get(SETTING_SCOREBOARD);
    }
}
