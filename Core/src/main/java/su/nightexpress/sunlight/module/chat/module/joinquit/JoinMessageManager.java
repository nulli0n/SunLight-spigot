package su.nightexpress.sunlight.module.chat.module.joinquit;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.chat.ChatModule;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class JoinMessageManager extends AbstractManager<SunLightPlugin> {

    public static final String FILE_NAME = "join_quit_messages.yml";

    private final ChatModule module;

    private final Map<String, JoinMessage> joinMessageMap;
    private final Map<String, JoinMessage> quitMessageMap;

    public JoinMessageManager(@NotNull SunLightPlugin plugin, @NotNull ChatModule module) {
        super(plugin);
        this.module = module;
        this.joinMessageMap = new HashMap<>();
        this.quitMessageMap = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        this.loadConfig();

        this.addListener(new JoinMessageListener(this.plugin, this));
    }

    @Override
    protected void onShutdown() {
        this.joinMessageMap.clear();
        this.quitMessageMap.clear();
    }

    private void loadConfig() {
        FileConfig config = FileConfig.loadOrExtract(this.plugin, this.module.getLocalPath(), FILE_NAME);

        if (config.getSection("Join_Messages").isEmpty()) {
            JoinMessage.getDefaultJoins().write(config, "Join_Messages.default");
        }
        if (config.getSection("Quit_Messages").isEmpty()) {
            JoinMessage.getDefaultQuits().write(config, "Quit_Messages.default");
        }

        for (String sId : config.getSection("Join_Messages")) {
            JoinMessage message = JoinMessage.read(config, "Join_Messages." + sId);
            this.joinMessageMap.put(sId.toLowerCase(), message);
        }

        for (String sId : config.getSection("Quit_Messages")) {
            JoinMessage message = JoinMessage.read(config, "Quit_Messages." + sId);
            this.quitMessageMap.put(sId.toLowerCase(), message);
        }

        config.saveChanges();
    }

    @NotNull
    public Map<String, JoinMessage> getJoinMessageMap() {
        return joinMessageMap;
    }

    @NotNull
    public Map<String, JoinMessage> getQuitMessageMap() {
        return quitMessageMap;
    }

    @NotNull
    public Collection<JoinMessage> getJoinMessages() {
        return this.joinMessageMap.values();
    }

    @NotNull
    public Collection<JoinMessage> getQuitMessages() {
        return this.quitMessageMap.values();
    }

    @Nullable
    public JoinMessage getJoinMessage(@NotNull Player player) {
        return this.getMessage(player, this.getJoinMessages());
    }

    @Nullable
    public JoinMessage getQuitMessage(@NotNull Player player) {
        return this.getMessage(player, this.getQuitMessages());
    }

    @Nullable
    private JoinMessage getMessage(@NotNull Player player, @NotNull Collection<JoinMessage> messages) {
        return messages.stream().filter(message -> message.isAvailable(player)).max(Comparator.comparingInt(JoinMessage::getPriority)).orElse(null);
    }
}
