package su.nightexpress.sunlight.module.chat.module.announcer;

import me.clip.placeholderapi.PlaceholderAPI;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.util.Placeholders;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AnnounceManager extends AbstractManager<SunLightPlugin> {

    public static final String FILE_NAME = "announcer.yml";

    private final ChatModule             module;
    private final Map<String, Announcer> announcerMap;

    public AnnounceManager(@NotNull SunLightPlugin plugin, @NotNull ChatModule module) {
        super(plugin);
        this.module = module;
        this.announcerMap = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        this.loadConfig();

        this.addListener(new AnnounceListener(this.plugin, this));

        this.getAnnouncers().forEach(announcer -> {
            this.addTask(this.plugin.createAsyncTask(() -> this.broadcast(announcer)).setSecondsInterval(announcer.getInterval()));
        });
    }

    @Override
    protected void onShutdown() {
        this.announcerMap.clear();
    }

    private void loadConfig() {
        FileConfig config = FileConfig.loadOrExtract(this.plugin, this.module.getLocalPath(), FILE_NAME);

        if (config.getSection("Announcers").isEmpty()) {
            Announcer.getDefaults().forEach(announcer -> announcer.write(config, "Announcers." + announcer.getId()));
        }

        for (String sId : config.getSection("Announcers")) {
            Announcer announcer = Announcer.read(config, "Announcers." + sId, sId);
            this.announcerMap.put(announcer.getId(), announcer);
        }

        this.module.info("Loaded " + this.announcerMap.size() + " announcer(s)!");

        config.saveChanges();
    }

    private void loadAnnouncers(@NotNull FileConfig config) {

    }

    @NotNull
    public Map<String, Announcer> getAnnouncerMap() {
        return announcerMap;
    }

    @NotNull
    public Collection<Announcer> getAnnouncers() {
        return this.announcerMap.values();
    }

    public void broadcast(@NotNull Announcer announcer) {
        this.plugin.getServer().getOnlinePlayers().forEach(player -> {
            if (!announcer.canSee(player)) return;

            String message = announcer.selectMessage(player);
            if (message == null) return;

            message = Placeholders.forPlayer(player).apply(message);
            if (Plugins.hasPlaceholderAPI()) {
                message = PlaceholderAPI.setPlaceholders(player, message);
            }

            Players.sendModernMessage(player, message);
        });
    }
}
