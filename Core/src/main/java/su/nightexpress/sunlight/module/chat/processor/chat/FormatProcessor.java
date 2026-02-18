package su.nightexpress.sunlight.module.chat.processor.chat;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.nightcore.util.placeholder.PlaceholderContext;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.context.MessageContext;
import su.nightexpress.sunlight.module.chat.format.FormatComponent;
import su.nightexpress.sunlight.module.chat.processor.MessageProcessor;

public class FormatProcessor implements MessageProcessor {

    @Override
    public void preProcess(@NotNull ChatModule module, @NotNull MessageContext context) {
        Player player = context.getPlayer();

        PlaceholderContext componentContext = PlaceholderContext.builder()
            .with(key -> {
                String raw = CommonPlaceholders.withoutBrackets(key);
                FormatComponent component = module.getSettings().getFormatComponents().get(raw);
                return component == null ? null : component.getText();
            })
            .build();

        PlaceholderContext globalContext = PlaceholderContext.builder()
            .with(CommonPlaceholders.PLAYER.resolver(player))
            .andThen(CommonPlaceholders.forPlaceholderAPI(player))
            .build();

        // Replace %message% latest to not apply placeholders to it.
        PlaceholderContext messageContext = PlaceholderContext.builder()
            .with(SLPlaceholders.GENERIC_MESSAGE, context::getMessage)
            .build();

        String withComponents = componentContext.apply(context.getFormat());
        String withPlayerText = globalContext.apply(withComponents);
        String oneSpaced = messageContext.apply(oneSpace(withPlayerText));

        context.setFormat(oneSpaced);
    }

    @Override
    public void postProcess(@NotNull ChatModule module, @NotNull MessageContext context) {

    }

    @NotNull
    private static String oneSpace(@NotNull String str) {
        return str.trim().replaceAll("\\s+", " ");
    }
}
