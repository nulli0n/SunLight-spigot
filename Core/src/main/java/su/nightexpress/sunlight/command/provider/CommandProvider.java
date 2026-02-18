package su.nightexpress.sunlight.command.provider;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.builder.HubNodeBuilder;
import su.nightexpress.nightcore.commands.builder.LiteralNodeBuilder;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.sunlight.command.provider.definition.HubDefinition;
import su.nightexpress.sunlight.command.provider.definition.LiteralDefinition;

import java.util.Map;
import java.util.function.Consumer;

public interface CommandProvider extends LangContainer {

    void load(@NotNull FileConfig config);

    void registerDefaults();

    @NotNull Map<String, HubDefinition> getRootDefinitions();

    @NotNull Map<String, Consumer<HubNodeBuilder>> getRootBuilders();

    @NotNull Map<String, LiteralDefinition> getLiteralDefinitions();

    @NotNull Map<String, Consumer<LiteralNodeBuilder>> getLiteralBuilders();
}
