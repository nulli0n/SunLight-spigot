package su.nightexpress.sunlight.module.scoreboard.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.scoreboard.impl.BoardConfig;
import su.nightexpress.sunlight.module.scoreboard.impl.Board;
import su.nightexpress.sunlight.module.scoreboard.ScoreboardModule;

public class ScoreboardListener extends AbstractListener<SunLightPlugin> {

    private final ScoreboardModule module;

    public ScoreboardListener(@NotNull SunLightPlugin plugin, @NotNull ScoreboardModule module) {
        super(plugin);
        this.module = module;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBoardWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (!this.module.isScoreboardEnabled(player)) return;

        Board board = this.module.getBoard(player);
        BoardConfig currentBoard = board != null ? board.getBoardConfig() : null;
        BoardConfig worldBoard = this.module.getBoardConfig(player);

        if (currentBoard != null) {
            this.module.removeBoard(player);
        }
        if (worldBoard != null) {
            this.module.addBoard(player, worldBoard);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBoardJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!this.module.isScoreboardEnabled(player)) return;

        this.module.addBoard(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBoardQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.module.removeBoard(player);
    }
}
