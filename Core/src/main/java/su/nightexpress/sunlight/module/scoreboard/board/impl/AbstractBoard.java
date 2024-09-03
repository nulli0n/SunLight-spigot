package su.nightexpress.sunlight.module.scoreboard.board.impl;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.Version;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.module.scoreboard.ScoreboardModule;
import su.nightexpress.sunlight.module.scoreboard.board.Board;
import su.nightexpress.sunlight.module.scoreboard.board.BoardConfig;
import su.nightexpress.sunlight.utils.DynamicText;
import su.nightexpress.sunlight.utils.SunUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractBoard<T> implements Board {

    protected final ScoreboardModule module;
    protected final BoardConfig      boardConfig;
    protected final Player           player;
    protected final String               identifier;
    protected final Map<Integer, String> scores;

    private boolean lock;
    private long    nextUpdateTicks;

    public AbstractBoard(@NotNull Player player, @NotNull ScoreboardModule module, @NotNull BoardConfig boardConfig) {
        this.module = module;
        this.boardConfig = boardConfig;
        this.player = player;
        this.identifier = SunUtils.createIdentifier(player).substring(0, 16);
        this.scores = new ConcurrentHashMap<>();
        this.nextUpdateTicks = 0L;
    }

    @Override
    @NotNull
    public final BoardConfig getBoardConfig() {
        return boardConfig;
    }

    @NotNull
    private String getPlayerIdentifier() {
        return this.identifier;
    }

    @NotNull
    private String getScoreIdentifier(int score) {
        if (Version.isBehind(Version.V1_20_R3)) {
            return ChatColor.COLOR_CHAR + String.join(String.valueOf(ChatColor.COLOR_CHAR), String.valueOf(score).split(""));
        }
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

    @NotNull
    protected abstract T createLegacyTeamRemovePacket(@NotNull String scoreId);

    @NotNull
    protected abstract T createLegacyTeamPacket(@NotNull String scoreId, int score, @NotNull String text, @NotNull AtomicBoolean result);

    @Override
    public void create() {
        this.sendPacket(this.player, this.createObjectivePacket(ObjectiveMode.CREATE, ""));
        this.sendPacket(this.player, this.createDisplayPacket());
    }

    @Override
    public void remove() {
        this.sendPacket(this.player, this.createObjectivePacket(ObjectiveMode.REMOVE, ""));

        this.scores.forEach((score, text) -> {
            if (Version.isBehind(Version.V1_20_R3)) {
                this.sendPacket(this.player, this.createLegacyTeamRemovePacket(this.getScoreIdentifier(score)));
            }
            else {
                this.sendPacket(this.player, this.createResetScorePacket(this.getScoreIdentifier(score)));
            }
        });

        this.scores.clear();
        this.lock = false;
    }

    @NotNull
    private String replacePlaceholders(@NotNull String string) {
        string = Placeholders.forPlayer(this.player).apply(string);
        for (DynamicText animation : this.module.getAnimations()) {
            string = animation.replacePlaceholders().apply(string);
        }
        if (Plugins.hasPlaceholderAPI()) {
            string = PlaceholderAPI.setPlaceholders(this.player, string);
        }
        return string;
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
        String title = this.boardConfig.getTitle();
        List<String> lines = this.boardConfig.getLines();

        Collection<DynamicText> animations = this.module.getAnimations();
        Map<Integer, String> scores = new HashMap<>();
        int index = lines.size();

        for (String line : lines) {
            scores.put(index--, this.replacePlaceholders(line));
        }
        title = this.replacePlaceholders(title);


        this.sendPacket(this.player, this.createObjectivePacket(ObjectiveMode.UPDATE, title));

        scores.forEach((score, text) -> {
            String scoreId = this.getScoreIdentifier(score);

            if (Version.isBehind(Version.V1_20_R3)) {
                AtomicBoolean result = new AtomicBoolean(true);
                this.sendPacket(this.player, this.createLegacyTeamPacket(scoreId, score, text, result));
                if (!result.get()) return;
            }

            this.sendPacket(this.player, this.createScorePacket(scoreId, score, text));
        });

        this.scores.entrySet().stream().filter(entry -> !scores.containsKey(entry.getKey())).forEach(entry -> {
            int score = entry.getKey();
            String scoreId = this.getScoreIdentifier(score);

            if (Version.isBehind(Version.V1_20_R3)) {
                this.sendPacket(this.player, this.createLegacyTeamRemovePacket(scoreId));
            }
            this.sendPacket(this.player, this.createResetScorePacket(scoreId));
        });

        this.scores.clear();
        this.scores.putAll(scores);
        this.lock = false;
        this.nextUpdateTicks = this.boardConfig.getUpdateInterval();
    }
}
