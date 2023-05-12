package su.nightexpress.sunlight.module.chat.module;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.Placeholders;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.chat.ChatModule;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatJoinManager extends AbstractManager<SunLight> {

    private final ChatModule chatModule;

    private Map<String, List<String>> joinInGroups;
    private Map<String, List<String>>      joinOutGroups;

    public ChatJoinManager(@NotNull ChatModule chatModule) {
        super(chatModule.plugin());
        this.chatModule = chatModule;
    }

    @Override
    public void onLoad() {
        JYML cfg = JYML.loadOrExtract(plugin, chatModule.getLocalPath(), "join.quit.messages.yml");

        this.joinInGroups = new HashMap<>();
        this.joinOutGroups = new HashMap<>();

        for (String group : cfg.getSection("Join_Groups")) {
            List<String> msg = Colorizer.apply(cfg.getStringList("Join_Groups." + group));
            this.joinInGroups.put(group.toLowerCase(), msg);
        }

        for (String group : cfg.getSection("Quit_Groups")) {
            List<String> msg = Colorizer.apply(cfg.getStringList("Quit_Groups." + group));
            this.joinOutGroups.put(group.toLowerCase(), msg);
        }

        this.addListener(new Listener(this.plugin));
    }

    @Override
    public void onShutdown() {
        if (this.joinInGroups != null) {
            this.joinInGroups.clear();
            this.joinInGroups = null;
        }
        if (this.joinOutGroups != null) {
            this.joinOutGroups.clear();
            this.joinOutGroups = null;
        }
    }

    @Nullable
    public String getMessage(@NotNull Player player, @NotNull Map<String, List<String>> map) {
        String group = Hooks.getPermissionGroup(player);
        List<String> messages = map.getOrDefault(group, Collections.emptyList());
        if (messages.isEmpty()) return null;

        String message = Rnd.get(messages);
        message = Placeholders.Player.replacer(player).apply(message);
        if (Hooks.hasPlaceholderAPI()) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        return message;
    }

    class Listener extends AbstractListener<SunLight> {

        public Listener(@NotNull SunLight plugin) {
            super(plugin);
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onChatJoinMessage(PlayerJoinEvent e) {
            Player player = e.getPlayer();
            e.setJoinMessage(getMessage(player, joinInGroups));
        }

        @EventHandler(priority = EventPriority.NORMAL)
        public void onChatQuitMessage(PlayerQuitEvent e) {
            Player player = e.getPlayer();
            e.setQuitMessage(getMessage(player, joinOutGroups));
        }
    }
}
