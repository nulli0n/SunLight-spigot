package su.nightexpress.sunlight.module.essential.command;

import org.bukkit.World;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.EnumLocale;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.module.essential.EssentialModule;
import su.nightexpress.sunlight.module.essential.EssentialPerms;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.GENERIC_TYPE;
import static su.nightexpress.sunlight.SLPlaceholders.GENERIC_WORLD;

public class WeatherCommandProvider extends AbstractCommandProvider {

    private static final Permission PERMISSION_ROOT         = EssentialPerms.COMMAND.permission("weather.root");
    private static final Permission PERMISSION_CLEAR        = EssentialPerms.COMMAND.permission("weather.clear");
    private static final Permission PERMISSION_STORM        = EssentialPerms.COMMAND.permission("weather.storm");
    private static final Permission PERMISSION_THUNDERSTORM = EssentialPerms.COMMAND.permission("weather.thunder");

    private static final TextLocale DESCRIPTION_ROOT = LangEntry.builder("Command.Weather.Root.Desc").text("Weather commands.");
    private static final TextLocale DESCRIPTION_TYPE = LangEntry.builder("Command.Weather.Type.Desc").text("Set world's weather to " + GENERIC_TYPE + ".");

    private static final MessageLocale MESSAGE_SET_FEEDBACK = LangEntry.builder("Command.Weather.Set").chatMessage(
        GRAY.wrap("You have set " + WHITE.wrap(GENERIC_WORLD) + "'s weather to " + SOFT_YELLOW.wrap(GENERIC_TYPE) + ".")
    );

    private static final EnumLocale<Type> WEATHER_TYPE = LangEntry.builder("WeatherType").enumeration(Type.class);

    private final EssentialModule module;

    public WeatherCommandProvider(@NotNull SunLightPlugin plugin, @NotNull EssentialModule module) {
        super(plugin);
        this.module = module;
    }

    @Override
    public void registerDefaults() {
        Map<String, String> rootChildrens = new LinkedHashMap<>();

        for (Type type : Type.values()) {
            String name = LowerCase.INTERNAL.apply(type.name());

            Permission permission = switch (type) {
                case CLEAR -> PERMISSION_CLEAR;
                case STORM -> PERMISSION_STORM;
                case THUNDERSTORM -> PERMISSION_THUNDERSTORM;
            };

            this.registerLiteral(name, true, new String[]{name}, builder -> builder
                .description(DESCRIPTION_TYPE.text().replace(SLPlaceholders.GENERIC_TYPE, WEATHER_TYPE.getLocalized(type)))
                .permission(permission)
                .withArguments(Arguments.world(CommandArguments.WORLD).optional())
                .executes((context, arguments) -> this.setWeather(context, arguments, type))
            );

            rootChildrens.put(name, name);
        }

        this.registerRoot("Weather", true, new String[]{"weather"}, rootChildrens, builder -> builder
            .description(DESCRIPTION_ROOT)
            .permission(PERMISSION_ROOT)
        );
    }

    private boolean setWeather(@NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull Type type) {
        World world = this.getWorld(context, arguments, CommandArguments.WORLD);
        if (world == null) return false;

        type.apply(world);

        this.module.sendPrefixed(MESSAGE_SET_FEEDBACK, context.getSender(), replacer -> replacer
            .with(GENERIC_TYPE, () -> WEATHER_TYPE.getLocalized(type))
            .with(GENERIC_WORLD, () -> LangAssets.get(world))
        );

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
        THUNDERSTORM(world -> {
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
