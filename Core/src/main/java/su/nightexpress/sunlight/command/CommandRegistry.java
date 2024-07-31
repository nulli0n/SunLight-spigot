package su.nightexpress.sunlight.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.command.experimental.RootCommand;
import su.nightexpress.nightcore.command.experimental.ServerCommand;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.CommandUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.list.*;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.command.template.DirectCommandTemplate;
import su.nightexpress.sunlight.command.template.GroupCommandTemplate;

import java.util.*;

public class CommandRegistry {

    private static final String FILE_NAME = "command-map.yml";

    private static final Map<String, NodeCreator<DirectNodeBuilder>> NODE_BUILDERS = new HashMap<>();

    private static final Map<String, CommandTemplate> TEMPLATES  = new LinkedHashMap<>();
    private static final Set<String>                  REGISTERED = new HashSet<>();

    public static void registerDirectExecutor(@NotNull String name, @NotNull NodeCreator<DirectNodeBuilder> creator) {
        NODE_BUILDERS.put(name.toLowerCase(), creator);
    }

    @Nullable
    public static DirectNodeBuilder getDirectBuilder(@NotNull DirectCommandTemplate template, @NotNull FileConfig config) {
        NodeCreator<DirectNodeBuilder> creator = NODE_BUILDERS.get(template.getNodeId().toLowerCase());
        return creator == null ? null : creator.create(template, config);
    }

    public static void addTemplate(@NotNull String name, @NotNull CommandTemplate template) {
        TEMPLATES.put(name.toLowerCase(), template);
    }

    public static void addSimpleTemplate(@NotNull String name) {
        TEMPLATES.put(name.toLowerCase(), DirectCommandTemplate.create(new String[]{name}, name));
    }

    public static boolean isExecutorRegistered(@NotNull String name) {
        return NODE_BUILDERS.containsKey(name.toLowerCase());
    }

    public static void setup(@NotNull SunLightPlugin plugin) {
        FileConfig config = FileConfig.loadOrExtract(plugin, FILE_NAME);

        plugin.registerPermissions(CommandPerms.class);
        config.initializeOptions(CommandConfig.class);

        loadCoreCommands(plugin, config);
        checkConfig(plugin, config);
        registerCommands(plugin, config);

        config.saveChanges();
    }

    public static void shutdown(@NotNull SunLightPlugin plugin) {
        new HashSet<>(REGISTERED).forEach(name -> unregister(plugin, name));
//        REGISTERED.forEach(name -> {
//            if (plugin.getCommandManager().unregisterServerCommand(name)) {
//                plugin.info("Unregistered command: " + name);
//            }
//        });

        NODE_BUILDERS.clear();
        TEMPLATES.clear();
        REGISTERED.clear();
    }

    private static void checkConfig(@NotNull SunLightPlugin plugin, @NotNull FileConfig config) {
        Set<String> disabledExecutors = ConfigValue.create("Settings.Disabled_Executors",
            Lists.newSet(),
            "Add here names of command executors to disable them completely.",
            "Disabled executor will result in disabling ALL commands using that executor."
        ).read(config);

        disabledExecutors.forEach(name -> {
            name = name.toLowerCase();
            if (NODE_BUILDERS.remove(name) != null) {
                plugin.info("Executor disabled: '" + name + "'.");
            }
        });
    }

    private static void loadCoreCommands(@NotNull SunLightPlugin plugin, @NotNull FileConfig config) {
        AirCommand.load(plugin);
        AnvilCommand.load(plugin);
        BroadcastCommand.load(plugin);
        CondenseCommand.load(plugin);
        DimensionCommand.load(plugin);
        DisposalCommand.load(plugin);
        EnchantCommand.load(plugin);
        EnchantingCommand.load(plugin);
        EnderchestCommand.load(plugin);
        ExperienceCommand.load(plugin);
        //ExtinguishCommand.load(plugin);
        FireCommands.load(plugin);
        FlyCommand.load(plugin);
        FlySpeedCommand.load(plugin);
        FoodLevelCommand.load(plugin);
        GameModeCommand.load(plugin);
        GrindstoneCommand.load(plugin);
        HatCommand.load(plugin);
        HealthCommand.load(plugin);
        IgnoreCommands.load(plugin);
        InventoryCommand.load(plugin);
        ItemCommands.load(plugin);
        LoomCommand.load(plugin);
        MobCommand.load(plugin);
        NearCommand.load(plugin);
        NickCommand.load(plugin);
        PlayerInfoCommand.load(plugin);
        StaffCommand.load(plugin);
        SkullCommand.load(plugin);
        SmiteCommand.load(plugin);
        SpawnerCommand.load(plugin);
        SpeedCommand.load(plugin);
        SudoCommand.load(plugin);
        SuicideCommand.load(plugin);
        TeleportCommands.load(plugin);
        TimeCommands.load(plugin, config);
        WeatherCommands.load(plugin);
        WorkbenchCommand.load(plugin);
    }

    private static void registerCommands(@NotNull SunLightPlugin plugin, @NotNull FileConfig config) {
        TEMPLATES.forEach((name, template) -> {
            String path;
            if (template instanceof GroupCommandTemplate) {
                path = "CommandMap.Groups." + name;
            }
            else path = "CommandMap.Commands." + name;

            boolean present = config.contains(path);

            config.addMissing(path + ".Enabled", true);
            if (!present) {
                template.write(config, path);
            }
        });
        TEMPLATES.clear();

        config.getSection("CommandMap.Commands").forEach(name -> {
            if (!config.getBoolean("CommandMap.Commands." + name + ".Enabled")) return;

            DirectCommandTemplate template = DirectCommandTemplate.read(config, "CommandMap.Commands." + name);
            register(plugin, template, config);
        });

        config.getSection("CommandMap.Groups").forEach(name -> {
            if (!config.getBoolean("CommandMap.Groups." + name + ".Enabled")) return;

            GroupCommandTemplate template = GroupCommandTemplate.read(config, "CommandMap.Groups." + name);
            register(plugin, template, config);
        });
    }

    public static void register(@NotNull SunLightPlugin plugin, @NotNull DirectCommandTemplate template, @NotNull FileConfig config) {
        DirectNodeBuilder directBuilder = getDirectBuilder(template, config);
        if (directBuilder == null) {
            plugin.error("Could not find executor '" + template.getNodeId() + "'!");
            return;
        }

        ServerCommand command = RootCommand.build(plugin, directBuilder);

        register(plugin, command);
    }


    public static void register(@NotNull SunLightPlugin plugin, @NotNull GroupCommandTemplate template, @NotNull FileConfig config) {
        ServerCommand command = RootCommand.chained(plugin, template.getAliases(), rootBuilder -> {
            rootBuilder.description(template.getDescription());
            rootBuilder.permission(template.getPermission());

            template.getCommands().forEach(child -> {
                DirectNodeBuilder directBuilder = getDirectBuilder(child, config);
                if (directBuilder == null) {
                    plugin.error("Could not find child executor '" + child.getNodeId() + "'!");
                    return;
                }

                rootBuilder.child(directBuilder);
            });
        });

        register(plugin, command);
    }

    public static void register(@NotNull SunLightPlugin plugin, @NotNull ServerCommand command) {
        String name = command.getNode().getName();

        if (CommandConfig.UNREGISTER_CONFLICTS.get()) {
            CommandUtil.unregister(name);
        }

        plugin.getCommandManager().registerCommand(command);
        REGISTERED.add(name);
        plugin.info("Registered command: " + name);
    }

    public static boolean unregister(@NotNull SunLightPlugin plugin, @NotNull String name) {
        if (plugin.getCommandManager().unregisterServerCommand(name)) {
            REGISTERED.remove(name);
            plugin.info("Unregistered command: " + name);
            return true;
        }

        return false;
    }
}
