package su.nightexpress.sunlight.module.chat.processor.global;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.context.ChatContext;
import su.nightexpress.sunlight.module.chat.processor.ChatProcessor;
import su.nightexpress.sunlight.module.chat.rule.WordFilter;

public class FilterProcessor implements ChatProcessor<ChatContext> {

    private final WordFilter filter;

    public FilterProcessor(@NotNull WordFilter filter) {
        this.filter = filter;
    }

    @Override
    public void preProcess(@NotNull ChatModule module, @NotNull ChatContext context) {
        String message = context.getMessage();

        context.setMessage(this.filter.censor(message, '*'));
    }

    @Override
    public void postProcess(@NotNull ChatModule module, @NotNull ChatContext context) {

    }
}
