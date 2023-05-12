package su.nightexpress.sunlight.module.scoreboard.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.scoreboard.impl.BoardConfig;
import su.nightexpress.sunlight.module.scoreboard.impl.Board;
import su.nightexpress.sunlight.module.scoreboard.ScoreboardModule;

public class ScoreboardListener extends AbstractListener<SunLight> {

    private final ScoreboardModule module;

    public ScoreboardListener(@NotNull ScoreboardModule module) {
        super(module.plugin());
        this.module = module;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBoardWorldChange(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        if (!this.module.isScoreboardEnabled(player)) return;

        Board board = this.module.getBoard(player);
        BoardConfig boardHas = board != null ? board.getBoardConfig() : null;
        BoardConfig boardNew = this.module.getBoardConfig(player);

        if (boardHas == null && boardNew == null) return;
        if (boardHas != null && boardHas.equals(boardNew)) return;

        if (boardHas != null) {
            this.module.removeBoard(player);
        }
        if (boardNew != null) {
            this.module.addBoard(player, boardNew);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBoardJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (!this.module.isScoreboardEnabled(player)) return;

        this.module.addBoard(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBoardQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        this.module.removeBoard(player);
    }
}
