package su.nightexpress.sunlight.module.scheduler;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.module.scheduler.announcer.Announcer;

import java.util.*;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;

public class SchedulerDefaults {

    @NotNull
    public static Map<String, Announcer> getDefaultAnnouncers() {
        Map<String, Announcer> map = new HashMap<>();

        Map<String, List<String>> texts = new LinkedHashMap<>();
        
        texts.put("discord", List.of(
            " ",
            BLUE.wrap(BOLD.wrap("Join Discord Server!")),
            DARK_GRAY.wrap(BOLD.wrap("|")) + " " + GRAY.wrap("Stay up to date with all " + BLUE.wrap("announcements") + ", "),
            DARK_GRAY.wrap(BOLD.wrap("|")) + " " + GRAY.wrap(BLUE.wrap("giveaways") + ", " + BLUE.wrap("events") + " and much more!"),
            " ",
            BLUE.wrap("➥ " + OPEN_URL.with("https://YOUR_DISCORD_LINK/").wrap("Click to join!")),
            " "
        ));
        
        texts.put("vote", List.of(
            " ",
            GREEN.wrap(BOLD.wrap("Vote for <ServerName>")),
            DARK_GRAY.wrap(BOLD.wrap("|")) + " " + GRAY.wrap("Don't forget to " + GREEN.wrap("vote") + " for our"),
            DARK_GRAY.wrap(BOLD.wrap("|")) + " " + GRAY.wrap("server daily for " + GREEN.wrap("free rewards") + "."),
            " ",
            GREEN.wrap("➥ " + OPEN_URL.with("https://YOUR_VOTE_LINK/").wrap("Click to vote!")),
            " "
        ));
        
        texts.put("store", List.of(
            " ",
            ORANGE.wrap(BOLD.wrap("<ServerName> Server Store")),
            DARK_GRAY.wrap(BOLD.wrap("|")) + " " + GRAY.wrap("Obtain " + ORANGE.wrap("crate keys") + " for an opportunity to"),
            DARK_GRAY.wrap(BOLD.wrap("|")) + " " + GRAY.wrap("acquire " + ORANGE.wrap("exciting rewards") + " within the game."),
            " ",
            ORANGE.wrap("➥ " + OPEN_URL.with("https://YOUR_STORE_LINK/").wrap("Click for store!")),
            " "
        ));

        Announcer one = new Announcer(120, true, Set.of(SLPlaceholders.WILDCARD), texts);

        map.put("global", one);

        return map;
    }
}
