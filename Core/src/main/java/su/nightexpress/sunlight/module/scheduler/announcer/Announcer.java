package su.nightexpress.sunlight.module.scheduler.announcer;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.random.Rnd;
import su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers;
import su.nightexpress.sunlight.SLPlaceholders;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Announcer {

    private final int                       interval;
    private final boolean                   randomOrder;
    private final Set<String>               ranks;
    private final Map<String, List<String>> texts;

    private final Set<String> usedTexts;

    public Announcer(int interval, boolean randomOrder, @NotNull Set<String> ranks, @NotNull Map<String, List<String>> texts) {
        this.interval = interval;
        this.randomOrder = randomOrder;
        this.ranks = ranks.stream().map(String::toLowerCase).collect(Collectors.toSet());
        this.texts = texts;
        this.usedTexts = new HashSet<>();
    }

    @NotNull
    public static Announcer fromFile(@NotNull Path path) {
        FileConfig config = FileConfig.load(path);

        int interval = AnnouncerSchema.INTERVAL.resolveWithDefaults(config);
        boolean randomOrder = AnnouncerSchema.RANDOM_ORDER.resolveWithDefaults(config);
        Set<String> ranks = AnnouncerSchema.RANKS.resolveWithDefaults(config);
        Map<String, List<String>> texts = AnnouncerSchema.TEXTS.resolveWithDefaults(config);

        config.saveChanges();

        return new Announcer(interval, randomOrder, ranks, texts);
    }

    public void writeToFile(@NotNull Path path) {
        FileConfig.load(path).edit(config -> {
            AnnouncerSchema.INTERVAL.writeValue(config, this.interval);
            AnnouncerSchema.RANDOM_ORDER.writeValue(config, this.randomOrder);
            AnnouncerSchema.RANKS.writeValue(config, this.ranks);
            AnnouncerSchema.TEXTS.writeValue(config, this.texts);
        });
    }

    public boolean canSee(@NotNull Player player) {
        if (this.ranks.contains(SLPlaceholders.WILDCARD)) return true;

        Set<String> groups = Players.getInheritanceGroups(player);
        return this.ranks.stream().anyMatch(groups::contains);
    }

    @Nullable
    public String selectMessage() {
        if (this.texts.isEmpty()) return null;
        if (this.usedTexts.size() >= this.texts.size()) this.usedTexts.clear();

        List<String> freeTexts = new ArrayList<>();
        for (String key : this.texts.keySet()) {
            if (!this.usedTexts.contains(key)) {
                freeTexts.add(key);
                if (!this.randomOrder) break;
            }
        }
        if (freeTexts.isEmpty()) return null;

        String index = this.randomOrder ? Rnd.get(freeTexts) : freeTexts.getFirst();

        this.usedTexts.add(index);

        return String.join(TagWrappers.BR, this.texts.get(index));
    }

    public int getInterval() {
        return this.interval;
    }

    public boolean isRandomOrder() {
        return this.randomOrder;
    }

    @NotNull
    public Set<String> getRanks() {
        return Set.copyOf(this.ranks);
    }

    @NotNull
    public Map<String, List<String>> getTexts() {
        return Map.copyOf(this.texts);
    }
}
