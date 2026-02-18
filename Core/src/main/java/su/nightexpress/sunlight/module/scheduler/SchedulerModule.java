package su.nightexpress.sunlight.module.scheduler;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.FileUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.Strings;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.nightcore.util.placeholder.PlaceholderContext;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.exception.ModuleLoadException;
import su.nightexpress.sunlight.hook.placeholder.PlaceholderRegistry;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleContext;
import su.nightexpress.sunlight.module.scheduler.announcer.Announcer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SchedulerModule extends Module {

    private final SchedulerSettings settings;

    private final Map<String, Announcer> announcerByIdMap;

    public SchedulerModule(@NotNull ModuleContext context) {
        super(context);
        this.settings = new SchedulerSettings();
        this.announcerByIdMap = new HashMap<>();
    }

    @Override
    protected void loadModule(@NotNull FileConfig config) throws ModuleLoadException {
        this.settings.load(config);

        this.loadAnnouncers();

        this.getAnnouncers().forEach(announcer -> {
            this.addAsyncTask(() -> this.broadcastAnnouncer(announcer), announcer.getInterval());
        });
    }

    @Override
    protected void unloadModule() {
        this.announcerByIdMap.clear();
    }

    @Override
    protected void registerCommands() {

    }

    @Override
    protected void registerPermissions(@NotNull PermissionTree root) {

    }

    @Override
    public void registerPlaceholders(@NotNull PlaceholderRegistry registry) {

    }

    @NotNull
    public SchedulerSettings getSettings() {
        return this.settings;
    }

    @NotNull
    public Set<Announcer> getAnnouncers() {
        return Set.copyOf(this.announcerByIdMap.values());
    }

    private void loadAnnouncers() {
        Path dirPath = Path.of(this.getSystemPath(), SchedulerFiles.DIR_ANNOUNCERS);
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);

                SchedulerDefaults.getDefaultAnnouncers().forEach((id, announcer) -> {
                    Path filePath = Path.of(dirPath.toString(), FileConfig.withExtension(id));
                    announcer.writeToFile(filePath);
                });
            }
            catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        FileUtil.findYamlFiles(dirPath.toString()).forEach(filePath -> {
            String name = FileUtil.getNameWithoutExtension(filePath);
            String id = Strings.varStyle(name).orElse(null);
            if (id == null) {
                this.error("Could not load '%s' announcer: Invalid file name.".formatted(filePath.toString()));
                return;
            }

            Announcer announcer = Announcer.fromFile(filePath);
            this.announcerByIdMap.put(id, announcer);
        });

        this.info("Loaded " + this.announcerByIdMap.size() + " announcers.");
    }

    public void broadcastAnnouncer(@NotNull Announcer announcer) {
        String message = announcer.selectMessage();
        if (message == null) return;

        Players.getOnline().forEach(player -> {
            PlaceholderContext context = PlaceholderContext.builder()
                .with(CommonPlaceholders.PLAYER.resolver(player))
                .andThen(CommonPlaceholders.forPlaceholderAPI(player))
                .build();

            Players.sendMessage(player, context.apply(message));
        });
    }
}
