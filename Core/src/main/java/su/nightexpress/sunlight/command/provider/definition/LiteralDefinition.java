package su.nightexpress.sunlight.command.provider.definition;

import org.jetbrains.annotations.NotNull;

public record LiteralDefinition(boolean enabled, @NotNull String[] aliases, int cooldown) {

}
