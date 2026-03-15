package su.nightexpress.sunlight.module.scoreboard.board;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.placeholder.PlaceholderContext;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.utils.ConditionExpression;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class BoardDefinition implements Writeable {

    private final int                 updateInterval;
    private final int                 priority;
    private final Set<String>         worlds;
    private final Set<String>         ranks;
    private final String              title;
    private final List<String>        lines;
    private final String              condition;
    private final ConditionExpression conditionExpression;

    public BoardDefinition(int updateInterval,
                           int priority,
                           @NotNull Set<String> worlds,
                           @NotNull Set<String> ranks,
                           @NotNull String title,
                           @NotNull List<String> lines,
                           @NotNull String condition,
                           @NotNull ConditionExpression conditionExpression) {
        this.updateInterval = Math.max(1, updateInterval);
        this.priority = priority;
        this.worlds = worlds;
        this.ranks = ranks;
        this.title = title;
        this.lines = lines;
        this.condition = condition;
        this.conditionExpression = conditionExpression;
    }

    @NotNull
    public static BoardDefinition read(@NotNull FileConfig config, @NotNull String path) {
        int updateInterval = config.getInt(path + ".Update_Interval", 20);
        int priority = config.getInt(path + ".Priority");
        Set<String> worlds = Lists.modify(config.getStringSet(path + ".Worlds"), LowerCase.INTERNAL::apply);
        Set<String> ranks = Lists.modify(config.getStringSet(path + ".Groups"), LowerCase.INTERNAL::apply);
        String title = config.getString(path + ".Title", "");
        List<String> lines = config.getStringList(path + ".Lines");
        String condition = config.getString(path + ".Condition", "");

        ConditionExpression conditionExpression = ConditionExpression.of(condition, path);

        return new BoardDefinition(updateInterval, priority, worlds, ranks, title, lines, condition, conditionExpression);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Update_Interval", this.updateInterval);
        config.set(path + ".Priority", this.priority);
        config.set(path + ".Worlds", this.worlds);
        config.set(path + ".Groups", this.ranks);
        config.set(path + ".Title", this.title);
        config.set(path + ".Lines", this.lines);
        config.set(path + ".Condition", this.condition);
    }

    public boolean isAvailable(@NotNull Player player, @NotNull PlaceholderContext placeholderContext, @NotNull Logger logger) {
        return this.isAvailableForWorld(player)
            && this.isAvailableForRank(player)
            && this.conditionExpression.evaluate(placeholderContext, logger);
    }

    public boolean isAvailableForRank(@NotNull Player player) {
        if (this.ranks.contains(SLPlaceholders.WILDCARD)) return true;

        Set<String> playerRanks = Players.getInheritanceGroups(player);
        return playerRanks.stream().anyMatch(this.ranks::contains);
    }

    public boolean isAvailableForWorld(@NotNull Player player) {
        if (this.worlds.contains(SLPlaceholders.WILDCARD)) return true;

        return this.worlds.contains(LowerCase.INTERNAL.apply(player.getWorld().getName()));
    }

    public int getUpdateInterval() {
        return this.updateInterval;
    }

    public int getPriority() {
        return this.priority;
    }

    @NotNull
    public Set<String> getWorlds() {
        return this.worlds;
    }

    @NotNull
    public Set<String> getRanks() {
        return this.ranks;
    }

    @NotNull
    public String getTitle() {
        return this.title;
    }

    @NotNull
    public List<String> getLines() {
        return this.lines;
    }

    @NotNull
    public String getCondition() {
        return this.condition;
    }
}
