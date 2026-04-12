package su.nightexpress.sunlight.module.chat.processor.global;

import org.jspecify.annotations.NonNull;

import su.nightexpress.nightcore.util.text.night.NightMessage;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.context.ChatContext;
import su.nightexpress.sunlight.module.chat.processor.ChatProcessor;

public class ColorProcessor implements ChatProcessor<ChatContext> {

    @Override
    public void preProcess(@NonNull ChatModule module, @NonNull ChatContext context) {
        context.setMessage(NightMessage.stripTags(context.getMessage())); // Strip all legacy colors (+ all possible tags)
    }

    @Override
    public void postProcess(@NonNull ChatModule module, @NonNull ChatContext context) {

    }
}
