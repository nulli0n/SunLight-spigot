package su.nightexpress.sunlight.command;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.builder.NodeBuilder;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.sunlight.command.template.CommandTemplate;

public interface NodeCreator<B extends NodeBuilder<?, B>> {

    @NotNull B create(@NotNull CommandTemplate template, @NotNull FileConfig config);
}
