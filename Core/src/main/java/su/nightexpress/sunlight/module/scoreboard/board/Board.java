package su.nightexpress.sunlight.module.scoreboard.board;

import org.jetbrains.annotations.NotNull;

public interface Board {

    @NotNull BoardDefinition getBoardConfig();

    void create();

    void update();

    boolean updateIfReady();

    void remove();
}
