package su.nightexpress.sunlight.module.chat.processor.global;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.text.night.NightMessage;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.context.ChatContext;
import su.nightexpress.sunlight.module.chat.processor.ChatProcessor;

public class ColorProcessor implements ChatProcessor<ChatContext> {

    @Override
    public void preProcess(@NotNull ChatModule module, @NotNull ChatContext context) {
        context.setMessage(NightMessage.stripTags(context.getMessage())); // Strip all legacy colors (+ all possible tags)
    }

    @Override
    public void postProcess(@NotNull ChatModule module, @NotNull ChatContext context) {

    }
}
