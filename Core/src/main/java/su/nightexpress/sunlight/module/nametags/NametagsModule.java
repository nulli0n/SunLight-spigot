package su.nightexpress.sunlight.module.nametags;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.nightcore.util.placeholder.PlaceholderContext;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.exception.ModuleLoadException;
import su.nightexpress.sunlight.hook.HookId;
import su.nightexpress.sunlight.hook.placeholder.PlaceholderRegistry;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleContext;
import su.nightexpress.sunlight.module.nametags.handler.NametagHandler;
import su.nightexpress.sunlight.module.nametags.handler.PacketsTagHandler;
import su.nightexpress.sunlight.module.nametags.handler.ProtocolTagHandler;
import su.nightexpress.sunlight.module.nametags.listener.NametagsListener;

import java.util.Comparator;

public class NametagsModule extends Module {

    private final NametagsSettings settings;

    private NametagHandler tagHandler;

    public NametagsModule(@NotNull ModuleContext context) {
        super(context);
        this.settings = new NametagsSettings();
    }

    @Override
    protected void loadModule(@NotNull FileConfig config) throws ModuleLoadException {
        this.settings.load(config);

        this.loadTagHandler();

        this.addListener(new NametagsListener(this.plugin, this));
    }

    private void loadTagHandler() {
        if (Plugins.isInstalled(HookId.PACKET_EVENTS)) {
            this.tagHandler = new PacketsTagHandler(this.plugin);
        }
        else if (Plugins.isInstalled(HookId.PROTOCOL_LIB)) {
            this.tagHandler = new ProtocolTagHandler(this.plugin);
        }

        if (this.tagHandler != null) {
            this.tagHandler.setup();
            this.addAsyncTask(this::updatePlayerNameTags, this.settings.getNameTagUpdateInterval());
        }
    }

    @Override
    protected void unloadModule() {

    }

    @Override
    protected void registerPermissions(@NotNull PermissionTree root) {

    }

    @Override
    protected void registerCommands() {

    }

    @Override
    public void registerPlaceholders(@NotNull PlaceholderRegistry registry) {

    }

    @Nullable
    public NameTagFormat getPlayerNameTagFormat(@NotNull Player player, @NotNull PlaceholderContext placeholderContext) {
        return this.settings.getNameTagFormatsMap().values().stream()
            .filter(entry -> entry.isAvailable(player, placeholderContext, this.plugin.getLogger()))
            .max(Comparator.comparingInt(NameTagFormat::getPriority))
            .orElse(null);
    }

    public void handleJoin(@NotNull PlayerJoinEvent event) {
        this.updatePlayerNameTag(event.getPlayer());
    }

    public void updatePlayerNameTag(@NotNull Player player) {
        if (this.tagHandler == null) return;

        PlaceholderContext placeholderContext = this.createPlaceholderContext(player);
        NameTagFormat tag = this.getPlayerNameTagFormat(player, placeholderContext);
        if (tag == null) return;

        this.tagHandler.sendTeamPacket(player, tag, placeholderContext);
    }

    public void updatePlayerNameTags() {
        Players.getOnline().forEach(this::updatePlayerNameTag);
    }

    @NotNull
    private PlaceholderContext createPlaceholderContext(@NotNull Player player) {
        return PlaceholderContext.builder()
            .with(CommonPlaceholders.PLAYER.resolver(player))
            .andThen(CommonPlaceholders.forPlaceholderAPI(player))
            .build();
    }
}
