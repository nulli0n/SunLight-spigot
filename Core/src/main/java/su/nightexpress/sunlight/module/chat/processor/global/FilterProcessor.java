package su.nightexpress.sunlight.module.chat.processor.global;

import org.jspecify.annotations.NonNull;

import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.context.ChatContext;
import su.nightexpress.sunlight.module.chat.processor.ChatProcessor;
import su.nightexpress.sunlight.module.chat.rule.WordFilter;

public class FilterProcessor implements ChatProcessor<ChatContext> {

    private final WordFilter filter;

    public FilterProcessor(@NonNull WordFilter filter) {
        this.filter = filter;
    }

    @Override
    public void preProcess(@NonNull ChatModule module, @NonNull ChatContext context) {
        String message = context.getMessage();

        context.setMessage(this.filter.censor(message, '*'));
    }

    @Override
    public void postProcess(@NonNull ChatModule module, @NonNull ChatContext context) {

    }
}
