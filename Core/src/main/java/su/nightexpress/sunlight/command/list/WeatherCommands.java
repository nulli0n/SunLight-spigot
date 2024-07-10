package su.nightexpress.sunlight.command.list;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.CommandPerms;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.CommandTools;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.config.Lang;

import java.util.function.Consumer;
import java.util.function.Function;

public class WeatherCommands {

    public static final Function<Type, String> NODE_TYPE = type -> "weather_" + type.name().toLowerCase();

    public static void load(@NotNull SunLightPlugin plugin) {

        CommandRegistry.addTemplate("weather", CommandTemplate.group(new String[]{"weather"},
            "Weather commands.",
            CommandPerms.PREFIX + "weather",
            CommandTemplate.direct(new String[]{"clear"}, NODE_TYPE.apply(Type.CLEAR)),
            CommandTemplate.direct(new String[]{"storm"}, NODE_TYPE.apply(Type.STORM)),
            CommandTemplate.direct(new String[]{"thunder"}, NODE_TYPE.apply(Type.THUNDER))
        ));

        for (Type type : Type.values()) {
            String shortcut = switch (type) {
                case CLEAR -> "sun";
                case STORM -> "rain";
                case THUNDER -> "thunderstorm";
            };

            CommandRegistry.registerDirectExecutor(NODE_TYPE.apply(type), (template, config) -> builderType(plugin, template, config, type));
            CommandRegistry.addTemplate("weather_" + type.name().toLowerCase(), CommandTemplate.direct(new String[]{shortcut}, NODE_TYPE.apply(type)));
        }
    }

    @NotNull
    public static DirectNodeBuilder builderType(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config, @NotNull Type type) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_WEATHER_TYPE_DESC.getString().replace(Placeholders.GENERIC_TYPE, Lang.WEATHER_TYPE.getLocalized(type)))
            .permission(CommandPerms.WEATHER_TYPE.apply(type))
            .withArgument(ArgumentTypes.world(CommandArguments.WORLD))
            .executes((context, arguments) -> executeType(plugin, context, arguments, type))
            ;
    }

    public static boolean executeType(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull Type type) {
        World world = CommandTools.getWorld(plugin, context, arguments, CommandArguments.WORLD);
        if (world == null) return false;

        type.apply(world);

        Lang.COMMAND_WEATHER_SET.getMessage()
            .replace(Placeholders.GENERIC_TYPE, Lang.WEATHER_TYPE.getLocalized(type))
            .replace(Placeholders.GENERIC_WORLD, LangAssets.get(world))
            .send(context.getSender());

        return true;
    }

    public enum Type {
        CLEAR(world -> {
            world.setStorm(false);
            world.setThundering(false);
        }),
        STORM(world -> {
            world.setThundering(false);
            world.setStorm(true);
        }),
        THUNDER(world -> {
            world.setStorm(true);
            world.setThundering(true);
        });

        private final Consumer<World> consumer;

        Type(@NotNull Consumer<World> consumer) {
            this.consumer = consumer;
        }

        public void apply(@NotNull World world) {
            this.consumer.accept(world);
        }
    }
}
