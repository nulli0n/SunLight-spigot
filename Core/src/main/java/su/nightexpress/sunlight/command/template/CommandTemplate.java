package su.nightexpress.sunlight.command.template;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public abstract class CommandTemplate {

    protected final String[] aliases;

    public CommandTemplate(@NotNull String[] aliases) {
        this.aliases = aliases;
    }

    public static DirectCommandTemplate direct(@NotNull String[] aliases, @NotNull String nodeId) {
        return DirectCommandTemplate.create(aliases, nodeId);
    }

    public static GroupCommandTemplate group(@NotNull String[] aliases,
                                             @NotNull String description,
                                             @NotNull String permission,
                                             DirectCommandTemplate... templates) {
        return GroupCommandTemplate.create(aliases, description, permission, Arrays.asList(templates));
    }

    public static GroupCommandTemplate group(@NotNull String[] aliases,
                                             @NotNull String description,
                                             @NotNull String permission,
                                             @NotNull Consumer<List<DirectCommandTemplate>> consumer) {
        List<DirectCommandTemplate> templates = new ArrayList<>();
        consumer.accept(templates);

        return GroupCommandTemplate.create(aliases, description, permission, templates);
    }

    public abstract void write(@NotNull FileConfig config, @NotNull String path);

    @NotNull
    public String[] getAliases() {
        return aliases;
    }
}
