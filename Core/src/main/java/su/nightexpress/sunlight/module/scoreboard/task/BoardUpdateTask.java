package su.nightexpress.sunlight.module.scoreboard.task;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.server.AbstractTask;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.scoreboard.ScoreboardModule;

public class BoardUpdateTask extends AbstractTask<SunLight> {

    private final ScoreboardModule module;

    private int counter;

    public BoardUpdateTask(@NotNull ScoreboardModule module) {
        super(module.plugin(), 1L, true);
        this.module = module;
        this.counter = 0;
    }

    @Override
    public void action() {
        this.module.getPlayerBoardMap().forEach((player, board) -> {
            if (counter % board.getBoardConfig().getUpdateInterval() == 0) {
                board.update();
            }
        });
        if (counter++ >= Short.MAX_VALUE) {
            counter = 0;
        }
    }
}
