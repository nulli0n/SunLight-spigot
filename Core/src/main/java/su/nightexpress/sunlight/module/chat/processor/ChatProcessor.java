package su.nightexpress.sunlight.module.chat.processor;

import org.jspecify.annotations.NonNull;

import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.context.ChatContext;

public interface ChatProcessor<T extends ChatContext> {

    void preProcess(@NonNull ChatModule module, @NonNull T context);

    void postProcess(@NonNull ChatModule module, @NonNull T context);
}
