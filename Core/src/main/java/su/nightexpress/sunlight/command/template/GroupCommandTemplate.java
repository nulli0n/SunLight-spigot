package su.nightexpress.sunlight.command.template;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.wrapper.UniPermission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupCommandTemplate extends CommandTemplate {

    private final String       description;
    private final String       permission;
    private final List<DirectCommandTemplate> commands;

    public GroupCommandTemplate(@NotNull String[] aliases, String description, String permission, List<DirectCommandTemplate> commands) {
        super(aliases);
        this.description = description;
        this.permission = permission;
        this.commands = commands;
    }

    @NotNull
    public static GroupCommandTemplate create(@NotNull String[] aliases, @NotNull String description, @NotNull UniPermission permission, @NotNull DirectCommandTemplate... commands) {
        return create(aliases, description, permission, Arrays.asList(commands));
    }

    @NotNull
    public static GroupCommandTemplate create(@NotNull String[] aliases, @NotNull String description, @NotNull UniPermission permission, @NotNull List<DirectCommandTemplate> commands) {
        return create(aliases, description, permission.getName(), commands);
    }

    @NotNull
    public static GroupCommandTemplate create(@NotNull String[] aliases, @NotNull String description, @NotNull String permission, @NotNull List<DirectCommandTemplate> commands) {
        return new GroupCommandTemplate(aliases, description, permission, commands);
    }

    @NotNull
    public static GroupCommandTemplate read(@NotNull FileConfig config, @NotNull String path) {
        String[] aliases = ConfigValue.create(path + ".Aliases", new String[0]).read(config);
        String description = ConfigValue.create(path + ".Description", "").read(config);
        String permission = ConfigValue.create(path + ".Permission", "").read(config);
        List<DirectCommandTemplate> commands = new ArrayList<>();
        for (String name : config.getSection(path + ".Commands")) {
            DirectCommandTemplate template = DirectCommandTemplate.read(config, path + ".Commands." + name);
            commands.add(template);
        }

        return new GroupCommandTemplate(aliases, description, permission, commands);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Aliases", String.join(",", this.aliases));
        config.set(path + ".Description", this.description);
        config.set(path + ".Permission", this.permission);
        config.remove(path + ".Commands");
        this.commands.forEach(template -> {
            template.write(config, path + ".Commands." + template.getNodeId());
        });
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    @NotNull
    public String getPermission() {
        return permission;
    }

    @NotNull
    public List<DirectCommandTemplate> getCommands() {
        return commands;
    }
}
