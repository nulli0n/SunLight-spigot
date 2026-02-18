package su.nightexpress.sunlight.module.scoreboard.board.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.placeholder.PlaceholderContext;
import su.nightexpress.sunlight.SLUtils;
import su.nightexpress.sunlight.module.scoreboard.board.Board;
import su.nightexpress.sunlight.module.scoreboard.board.BoardDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBoard<T> implements Board {

    protected final PlaceholderContext   placeholderContext;
    protected final BoardDefinition      boardDefinition;
    protected final Player               player;
    protected final String               identifier;
    protected final Map<Integer, String> scores;

    private boolean lock;
    private long    nextUpdateTicks;

    public AbstractBoard(@NotNull Player player, @NotNull PlaceholderContext placeholderContext, @NotNull BoardDefinition boardDefinition) {
        this.placeholderContext = placeholderContext;
        this.boardDefinition = boardDefinition;
        this.player = player;
        this.identifier = SLUtils.createIdentifier(player).substring(0, 16);
        this.scores = new ConcurrentHashMap<>();
        this.nextUpdateTicks = 0L;
    }

    @Override
    @NotNull
    public final BoardDefinition getBoardConfig() {
        return boardDefinition;
    }

    @NotNull
    private String getScoreIdentifier(int score) {
        return "line_" + score;
    }

    protected enum ObjectiveMode {
        CREATE,
        REMOVE,
        UPDATE
    }

    protected abstract void sendPacket(@NotNull Player player, @NotNull T packet);

    @NotNull
    protected abstract T createObjectivePacket(ObjectiveMode mode, @NotNull String displayName);

    @NotNull
    protected abstract T createResetScorePacket(@NotNull String scoreId);

    @NotNull
    protected abstract T createScorePacket(@NotNull String scoreId, int score, @NotNull String text);

    @NotNull
    protected abstract T createDisplayPacket();

    @Override
    public void create() {
        this.sendPacket(this.player, this.createObjectivePacket(ObjectiveMode.CREATE, ""));
        this.sendPacket(this.player, this.createDisplayPacket());
    }

    @Override
    public void remove() {
        this.sendPacket(this.player, this.createObjectivePacket(ObjectiveMode.REMOVE, ""));

        this.scores.forEach((score, text) -> {
            this.sendPacket(this.player, this.createResetScorePacket(this.getScoreIdentifier(score)));
        });

        this.scores.clear();
        this.lock = false;
    }

    @Override
    public void updateIfReady() {
        if (--this.nextUpdateTicks <= 0L) {
            this.update();
        }
    }

    @Override
    public void update() {
        // Fixes waterfall kick issue where scoreboard tries to be registered twice on login for whatever reason.
        if (this.lock) return;

        this.lock = true;
        String title = this.placeholderContext.apply(this.boardDefinition.getTitle());
        List<String> lines = this.placeholderContext.apply(this.boardDefinition.getLines());

        Map<Integer, String> scores = new HashMap<>();
        int index = lines.size();

        for (String line : lines) {
            scores.put(index--, line);
        }

        this.sendPacket(this.player, this.createObjectivePacket(ObjectiveMode.UPDATE, title));

        scores.forEach((score, text) -> {
            String scoreId = this.getScoreIdentifier(score);

            this.sendPacket(this.player, this.createScorePacket(scoreId, score, text));
        });

        this.scores.entrySet().stream().filter(entry -> !scores.containsKey(entry.getKey())).forEach(entry -> {
            int score = entry.getKey();
            String scoreId = this.getScoreIdentifier(score);

            this.sendPacket(this.player, this.createResetScorePacket(scoreId));
        });

        this.scores.clear();
        this.scores.putAll(scores);
        this.lock = false;
        this.nextUpdateTicks = this.boardDefinition.getUpdateInterval();
    }
}
