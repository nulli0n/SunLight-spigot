package su.nightexpress.sunlight.command.provider.definition;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record HubDefinition(boolean enabled, @NotNull String[] aliases, @NotNull String name,
                            @NotNull Map<String, String> childrenAliases) {

}
