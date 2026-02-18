package su.nightexpress.sunlight.command;

import org.jetbrains.annotations.NotNull;

public record CommandKey(@NotNull String providerId, @NotNull String nodeId) {

    private static final String DELIMITER = ":";

    @NotNull
    public String toKeyString() {
        return this.providerId + DELIMITER + this.nodeId;
    }

    @NotNull
    public static CommandKey fromKeyString(@NotNull String string) {
        String[] parts = string.split(DELIMITER, 2);

        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid key format: '%s'".formatted(string));
        }

        return new CommandKey(parts[0], parts[1]);
    }

}
