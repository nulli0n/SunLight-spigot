package su.nightexpress.sunlight.module.chat.module.announcer;

import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.random.Rnd;
import su.nightexpress.sunlight.module.chat.util.Placeholders;

import java.util.*;
import java.util.stream.Collectors;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.sunlight.module.chat.util.Placeholders.*;

public class Announcer {

    private final String       id;
    private final int          interval;
    private final boolean      randomOrder;
    private final Set<String>  ranks;
    private final List<String> messages;

    // TODO Per Worlds

    private final Map<UUID, Integer> lastIndexMap;

    public Announcer(@NotNull String id, int interval, boolean randomOrder, @NotNull Set<String> ranks, @NotNull List<String> messages) {
        this.id = id.toLowerCase();
        this.interval = interval;
        this.randomOrder = randomOrder;
        this.ranks = ranks.stream().map(String::toLowerCase).collect(Collectors.toSet());
        this.messages = messages;
        this.lastIndexMap = new HashMap<>();
    }

    @NotNull
    public static List<Announcer> getDefaults() {
        List<Announcer> list = new ArrayList<>();

        Announcer one = new Announcer("global", 120, true, Lists.newSet(Placeholders.WILDCARD), Lists.newList(
            asSingle(Lists.newList(
                " ",
                BLUE.enclose(BOLD.enclose("Join Discord Server!")),
                DARK_GRAY.enclose(BOLD.enclose("|")) + " " + GRAY.enclose("Stay up to date with all " + BLUE.enclose("announcements") + ", "),
                DARK_GRAY.enclose(BOLD.enclose("|")) + " " + GRAY.enclose(BLUE.enclose("giveaways") + ", " + BLUE.enclose("events") + " and much more!"),
                " ",
                BLUE.enclose("➥ " + CLICK.enclose("discord.gg/put_your_link", ClickEvent.Action.OPEN_URL, "https://discord.gg/put_your_link")),
                " "
            )),

            asSingle(Lists.newList(
                " ",
                GREEN.enclose(BOLD.enclose("Vote for <ServerName>")),
                DARK_GRAY.enclose(BOLD.enclose("|")) + " " + GRAY.enclose("Don't forget to " + GREEN.enclose("vote") + " for our"),
                DARK_GRAY.enclose(BOLD.enclose("|")) + " " + GRAY.enclose("server daily for " + GREEN.enclose("free rewards") + "."),
                " ",
                GREEN.enclose("➥ " + CLICK.enclose("https://put_your_link", ClickEvent.Action.OPEN_URL, "https://put_your_link")),
                " "
            )),

            asSingle(Lists.newList(
                " ",
                ORANGE.enclose(BOLD.enclose("<ServerName> Server Store")),
                DARK_GRAY.enclose(BOLD.enclose("|")) + " " + GRAY.enclose("Obtain " + ORANGE.enclose("crate keys") + " for an opportunity to"),
                DARK_GRAY.enclose(BOLD.enclose("|")) + " " + GRAY.enclose("acquire " + ORANGE.enclose("exciting rewards") + " within the game."),
                " ",
                ORANGE.enclose("➥ " + CLICK.enclose("https://store.put_your_link.net", ClickEvent.Action.OPEN_URL, "https://store.put_your_link.net")),
                " "
            ))
        ));

        list.add(one);

        return list;
    }

    @NotNull
    private static String asSingle(@NotNull List<String> list) {
        return String.join(TAG_LINE_BREAK, list);
    }

    @NotNull
    public static Announcer read(@NotNull FileConfig config, @NotNull String path, @NotNull String id) {
        int interval = config.getInt(path + ".Interval", 0);
        boolean randomOrder = config.getBoolean(path + ".RandomOrder");
        Set<String> ranks = config.getStringSet(path + ".Ranks");
        List<String> text = new ArrayList<>();

        config.getSection(path + ".Text").forEach(index -> {
            text.add(String.join(TAG_LINE_BREAK, config.getStringList(path + ".Text." + index)));
        });

        return new Announcer(id, interval, randomOrder, ranks, text);
    }

    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Interval", this.interval);
        config.set(path + ".RandomOrder", this.randomOrder);
        config.set(path + ".Ranks", this.ranks);
        config.set(path + ".Text", this.messages);
        config.remove(path + ".Text");
        this.wrapMessages().forEach((index, list) -> config.set(path + ".Text." + index, list));
    }

    public void clearIndex(@NotNull UUID uuid) {
        this.lastIndexMap.remove(uuid);
    }

    public boolean canSee(@NotNull Player player) {
        if (this.ranks.contains(Placeholders.WILDCARD)) return true;

        Set<String> groups = Players.getPermissionGroups(player);
        return this.ranks.stream().anyMatch(groups::contains);
    }

    @Nullable
    public String selectMessage(@NotNull Player player) {
        if (this.messages.isEmpty()) return null;

        int lastIndex = this.lastIndexMap.getOrDefault(player.getUniqueId(), -1);

        if (lastIndex != -1) {
            lastIndex = this.randomOrder ? Rnd.get(this.messages.size()) : lastIndex + 1;
        }

        if (lastIndex < 0 || lastIndex >= this.messages.size()) {
            lastIndex = this.randomOrder ? Rnd.get(this.messages.size()) : 0;
        }

        this.lastIndexMap.put(player.getUniqueId(), lastIndex);
        return this.messages.get(lastIndex);
    }

    @NotNull
    public String getId() {
        return id;
    }

    public int getInterval() {
        return interval;
    }

    public boolean isRandomOrder() {
        return randomOrder;
    }

    @NotNull
    public Set<String> getRanks() {
        return ranks;
    }

    @NotNull
    public List<String> getMessages() {
        return messages;
    }

    @NotNull
    public Map<Integer, List<String>> wrapMessages() {
        Map<Integer, List<String>> map = new HashMap<>();
        int count = 0;

        for (String line : this.getMessages()) {
            map.put(count++, Arrays.asList(LINE_BREAK.split(line)));
        }

        return map;
    }
}
