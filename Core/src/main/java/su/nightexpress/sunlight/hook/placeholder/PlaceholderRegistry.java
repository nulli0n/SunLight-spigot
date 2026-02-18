package su.nightexpress.sunlight.hook.placeholder;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.LowerCase;

import java.util.HashMap;
import java.util.Map;

public class PlaceholderRegistry {

    private final Map<String, PlaceholderHandler> handlerMap = new HashMap<>();

    public record ParsedPlaceholder(@NotNull String key, @NotNull PlaceholderHandler handler, @NotNull String payload) {}

    public void register(@NotNull String key, @NotNull PlaceholderHandler handler) {
        this.handlerMap.put(LowerCase.INTERNAL.apply(key), handler);
    }

    @Nullable
    public String onPlaceholderRequest(@NotNull Player player, @NotNull String params) {
        ParsedPlaceholder parsed = this.findHandler(params);
        if (parsed == null) return null;

        return parsed.handler().handle(player, parsed.payload());
    }

    @Nullable
    private ParsedPlaceholder findHandler(@NotNull String params) {
        String currentKey = params;
        StringBuilder currentPayload = new StringBuilder();

        while (true) {
            if (this.handlerMap.containsKey(currentKey)) {
                return new ParsedPlaceholder(currentKey, this.handlerMap.get(currentKey), currentPayload.toString());
            }

            int lastUnderscoreIndex = currentKey.lastIndexOf('_');
            if (lastUnderscoreIndex == -1) return null;

            String suffix = currentKey.substring(lastUnderscoreIndex + 1);

            if (currentPayload.isEmpty()) {
                currentPayload = new StringBuilder(suffix);
            }
            else {
                currentPayload.insert(0, suffix + "_");
            }

            currentKey = currentKey.substring(0, lastUnderscoreIndex);
        }
    }
}
