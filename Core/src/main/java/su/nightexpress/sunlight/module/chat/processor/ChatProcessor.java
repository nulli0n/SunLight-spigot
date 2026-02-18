package su.nightexpress.sunlight.module.chat.processor;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.context.ChatContext;

public interface ChatProcessor<T extends ChatContext> {

    void preProcess(@NotNull ChatModule module, @NotNull T context);

    void postProcess(@NotNull ChatModule module, @NotNull T context);
}
