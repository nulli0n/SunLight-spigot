package su.nightexpress.sunlight.module.scoreboard.board;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.scoreboard.ScoreboardModule;

public interface BoardProvider {

    @NotNull
    Board create(@NotNull Player player, @NotNull ScoreboardModule module, @NotNull BoardConfig config);
}
