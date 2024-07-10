package su.nightexpress.sunlight.command.template;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;

public class DirectCommandTemplate extends CommandTemplate {

    private final String nodeId;

    public DirectCommandTemplate(@NotNull String[] aliases, @NotNull String nodeId) {
        super(aliases);
        this.nodeId = nodeId;
    }

    @NotNull
    public static DirectCommandTemplate create(@NotNull String[] aliases, @NotNull String nodeId) {
        return new DirectCommandTemplate(aliases, nodeId);
    }

    @NotNull
    public static DirectCommandTemplate read(@NotNull FileConfig config, @NotNull String path) {
        String[] aliases = ConfigValue.create(path + ".Aliases", new String[0]).read(config);
        String nodeId = ConfigValue.create(path + ".Executor", "null").read(config);

        return new DirectCommandTemplate(aliases, nodeId);
    }

    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Aliases", String.join(",", this.aliases));
        config.set(path + ".Executor", this.nodeId);
    }

    @NotNull
    public String getNodeId() {
        return nodeId;
    }
}
