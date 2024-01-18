package su.nightexpress.sunlight.module.chat.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.handler.ChatMessageHandler;

public class SunlightPreHandleChatEvent extends Event implements Cancellable {

    public static final HandlerList handlerList = new HandlerList();

    private final ChatModule module;
    private final ChatMessageHandler handler;

    private boolean cancelled;

    public SunlightPreHandleChatEvent(@NotNull ChatModule module, @NotNull ChatMessageHandler handler) {
        super(!Bukkit.isPrimaryThread());
        this.module = module;
        this.handler = handler;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @NotNull
    public ChatModule getModule() {
        return module;
    }

    @NotNull
    public ChatMessageHandler getHandler() {
        return handler;
    }
}
