package su.nightexpress.sunlight.module.scoreboard.board;

import org.jetbrains.annotations.NotNull;

public interface Board {

    @NotNull BoardConfig getBoardConfig();

    void create();

    void update();

    void updateIfReady();

    void remove();
}
