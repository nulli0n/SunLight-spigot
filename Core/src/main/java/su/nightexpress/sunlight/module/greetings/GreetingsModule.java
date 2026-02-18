package su.nightexpress.sunlight.module.greetings;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.EventUtils;
import su.nightexpress.nightcore.util.bridge.wrapper.NightComponent;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.nightcore.util.placeholder.PlaceholderContext;
import su.nightexpress.nightcore.util.text.night.NightMessage;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.exception.ModuleLoadException;
import su.nightexpress.sunlight.hook.placeholder.PlaceholderRegistry;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleContext;
import su.nightexpress.sunlight.module.greetings.listener.GreetingsListener;
import su.nightexpress.sunlight.module.greetings.message.GreetingMessage;
import su.nightexpress.sunlight.module.greetings.message.MessageType;

import java.util.Comparator;
import java.util.Set;
import java.util.function.Consumer;

public class GreetingsModule extends Module {

    private final GreetingsSettings settings;

    public GreetingsModule(@NotNull ModuleContext context) {
        super(context);
        this.settings = new GreetingsSettings();
    }

    @Override
    protected void loadModule(@NotNull FileConfig config) throws ModuleLoadException {
        this.settings.load(config);

        this.addListener(new GreetingsListener(this.plugin, this));
    }

    @Override
    protected void unloadModule() {

    }

    @Override
    protected void registerPermissions(@NotNull PermissionTree root) {

    }

    @Override
    public void registerPlaceholders(@NotNull PlaceholderRegistry registry) {

    }

    @Override
    protected void registerCommands() {

    }

    public void handleJoinEvent(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.setEventMessage(player, MessageType.JOIN, component -> EventUtils.getAdapter().setJoinMessage(event, component));
    }

    public void handleQuitEvent(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.setEventMessage(player, MessageType.QUIT, component -> EventUtils.getAdapter().setQuitMessage(event, component));
    }

    private void setEventMessage(@NotNull Player player, @NotNull MessageType type, @NotNull Consumer<NightComponent> consumer) {
        GreetingMessage message = this.getAvailableMessage(player, type);
        if (message == null) {
            consumer.accept(null);
            return;
        }

        PlaceholderContext context = PlaceholderContext.builder()
            .with(CommonPlaceholders.PLAYER.resolver(player))
            .andThen(CommonPlaceholders.forPlaceholderAPI(player))
            .build();

        NightComponent component = NightMessage.parse(context.apply(message.getMessage()));
        consumer.accept(component);
    }

    @NotNull
    public Set<GreetingMessage> getMessages(@NotNull MessageType type) {
        return Set.copyOf(this.settings.getMessages(type).values());
    }

    @Nullable
    public GreetingMessage getJoinMessage(@NotNull Player player) {
        return this.getAvailableMessage(player, MessageType.JOIN);
    }

    @Nullable
    public GreetingMessage getQuitMessage(@NotNull Player player) {
        return this.getAvailableMessage(player, MessageType.QUIT);
    }

    @Nullable
    public GreetingMessage getAvailableMessage(@NotNull Player player, @NotNull MessageType type) {
        return this.getMessages(type)
            .stream()
            .filter(message -> message.isApplicable(player))
            .max(Comparator.comparingInt(GreetingMessage::getPriority))
            .orElse(null);
    }
}
