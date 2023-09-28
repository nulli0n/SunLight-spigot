package su.nightexpress.sunlight.module.chat.module;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.server.AbstractTask;
import su.nexmedia.engine.utils.*;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.chat.ChatModule;

import java.util.*;
import java.util.stream.Collectors;

public class ChatAnnounceManager {

    private final SunLight   plugin;
    private final ChatModule chatModule;

    private Map<String, Announcer> announcers;
    private boolean                isRandomOrder;

    private AnnouncerTask announcerTask;

    public ChatAnnounceManager(@NotNull ChatModule chatModule) {
        this.plugin = chatModule.plugin();
        this.chatModule = chatModule;
    }

    public void setup() {
        JYML cfg = JYML.loadOrExtract(plugin, chatModule.getLocalPath(), "announcer.yml");

        int interval = cfg.getInt("Interval", 180);
        this.isRandomOrder = cfg.getBoolean("Random_Order", true);
        this.announcers = new LinkedHashMap<>();
        for (String sId : cfg.getSection("Messages")) {
            String path2 = "Messages." + sId + ".";

            Set<String> groups = cfg.getStringSet(path2 + "Groups");
            List<String> text = cfg.getStringList(path2 + "Text");

            Announcer announcer = new Announcer(groups, text);
            this.announcers.put(sId.toLowerCase(), announcer);
        }

        if (!this.announcers.isEmpty()) {
            this.announcerTask = new AnnouncerTask(interval);
            this.announcerTask.start();
        }
    }

    public void shutdown() {
        if (this.announcerTask != null) {
            this.announcerTask.stop();
            this.announcerTask = null;
        }
        if (this.announcers != null) {
            this.announcers.clear();
            this.announcers = null;
        }
    }

    @Nullable
    public Announcer getAnnouncer(@NotNull Player player, int lastIndex) {
        Set<String> playerGroups = PlayerUtil.getPermissionGroups(player);
        List<String> keys = new ArrayList<>(this.announcers.entrySet().stream()
            .filter(entry -> entry.getValue().getGroups().stream()
                .anyMatch(anGroup -> playerGroups.contains(anGroup) || anGroup.equalsIgnoreCase(Placeholders.WILDCARD)))
            .map(Map.Entry::getKey).toList());

        if (keys.isEmpty()) return null;
        if (lastIndex >= keys.size()) lastIndex = 0;

        this.announcerTask.lastIndex.put(player, lastIndex);

        String id = this.isRandomOrder ? Rnd.get(keys) : keys.get(lastIndex);
        return id == null ? null : this.announcers.get(id.toLowerCase());
    }

    static class Announcer {

        private final Set<String>  groups;
        private final List<String> text;

        public Announcer(@NotNull Set<String> groups, @NotNull List<String> text) {
            this.groups = groups.stream().map(String::toLowerCase).collect(Collectors.toSet());
            this.text = Colorizer.apply(text);
        }

        @NotNull
        public Set<String> getGroups() {
            return groups;
        }

        @NotNull
        public List<String> getText() {
            return text;
        }
    }

    class AnnouncerTask extends AbstractTask<SunLight> {

        private final Map<Player, Integer> lastIndex;

        public AnnouncerTask(int interval) {
            super(ChatAnnounceManager.this.plugin, interval, true);
            this.lastIndex = new WeakHashMap<>();
        }

        @Override
        public void action() {
            boolean papi = EngineUtils.hasPlaceholderAPI();

            for (Player player : plugin.getServer().getOnlinePlayers()) {
                int lastIndex = this.lastIndex.getOrDefault(player, -1);

                Announcer announcer = getAnnouncer(player, lastIndex + 1);
                if (announcer == null) continue;

                for (String line : announcer.getText()) {
                    if (papi) line = PlaceholderAPI.setPlaceholders(player, line);
                    line = line.replace("%player%", player.getDisplayName());
                    PlayerUtil.sendRichMessage(player, line);
                }
            }
        }
    }
}
