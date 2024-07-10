package su.nightexpress.sunlight.command.list;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.LocationUtil;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.CommandPerms;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.utils.SunUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public class MobCommand {

    public static final String NODE_CLEAR = "mob_clear";
    public static final String NODE_KILL  = "mob_kill";
    public static final String NODE_SPAWN = "mob_spawn";

    private static boolean clearIgnoreTamed;
    private static boolean clearIgnoreVillagers;
    private static int     spawnLimit;

    public static void load(@NotNull SunLightPlugin plugin) {
        CommandRegistry.registerDirectExecutor(NODE_CLEAR, (template, config) -> builderClear(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_KILL, (template, config) -> builderKill(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_SPAWN, (template, config) -> builderSpawn(plugin, template, config));

        CommandRegistry.addTemplate("mob", CommandTemplate.group(new String[]{"mob"},
            "Mob commands.",
            CommandPerms.PREFIX + "mob",
            CommandTemplate.direct(new String[]{"clear"}, NODE_CLEAR),
            CommandTemplate.direct(new String[]{"kill"}, NODE_KILL),
            CommandTemplate.direct(new String[]{"spawn"}, NODE_SPAWN)
        ));
        CommandRegistry.addTemplate("killmob", CommandTemplate.direct(new String[]{"killmob"}, NODE_KILL));
        CommandRegistry.addTemplate("spawnmob", CommandTemplate.direct(new String[]{"spawnmob"}, NODE_SPAWN));
    }

    public static DirectNodeBuilder builderClear(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        clearIgnoreTamed = ConfigValue.create("Settings.Mob.Clear.Ignore_Tamed",
            true,
            "Sets whether or not '" + NODE_CLEAR + "' command should ignore tamed mobs."
        ).read(config);

        clearIgnoreVillagers = ConfigValue.create("Settings.Mob.Clear.Ignore_Villagers",
            true,
            "Sets whether or not '" + NODE_CLEAR + "' command should ignore villagers."
        ).read(config);

        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_MOB_CLEAR_DESC)
            .permission(CommandPerms.MOB_CLEAR)
            .withArgument(ArgumentTypes.integer(CommandArguments.RADIUS)
                .localized(Lang.COMMAND_ARGUMENT_NAME_RADIUS)
                .withSamples(tabContext -> Lists.newList("-1", "10", "100")))
            .withArgument(ArgumentTypes.world(CommandArguments.WORLD))
            .executes((context, arguments) -> executeClear(plugin, context, arguments))
            ;
    }

    public static boolean executeClear(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getExecutor();

        World world = plugin.getServer().getWorlds().getFirst();
        Location location = world.getSpawnLocation();
        if (arguments.hasArgument(CommandArguments.WORLD)) {
            world = arguments.getWorldArgument(CommandArguments.WORLD);
            location = world.getSpawnLocation();
        }
        else if (player != null) {
            world = player.getWorld();
            location = player.getLocation();
        }

        HashSet<Entity> mobs = new HashSet<>();

        int radius = arguments.getIntArgument(CommandArguments.RADIUS, -1);
        if (radius > 0) {
            mobs.addAll(world.getNearbyEntities(location, radius, radius, radius, entity -> entity instanceof LivingEntity));
        }
        else {
            mobs.addAll(world.getEntitiesByClass(LivingEntity.class));
        }

        mobs.forEach(entity -> {
            if (entity.isDead() || !entity.isValid() || entity.isInvulnerable() || entity instanceof Player) return;
            if (clearIgnoreTamed && entity instanceof Tameable tameable && tameable.isTamed()) return;
            if (clearIgnoreVillagers && entity instanceof Villager) return;

            entity.remove();
        });

        context.send(Lang.COMMAND_MOB_CLEAR_DONE.getMessage()
            .replace(Placeholders.GENERIC_RADIUS, radius > 0 ? NumberUtil.format(radius) : Lang.OTHER_INFINITY.getString())
            .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(mobs.size()))
            .replace(Placeholders.GENERIC_WORLD, LangAssets.get(world))
        );

        return true;
    }

    public static DirectNodeBuilder builderKill(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_MOB_KILL_DESC)
            .permission(CommandPerms.MOB_KILL)
            .withArgument(CommandArguments.entityType(CommandArguments.TYPE).required())
            .withArgument(ArgumentTypes.integer(CommandArguments.RADIUS)
                .localized(Lang.COMMAND_ARGUMENT_NAME_RADIUS)
                .withSamples(tabContext -> Lists.newList("-1", "10", "100")))
            .withArgument(ArgumentTypes.world(CommandArguments.WORLD))
            .executes((context, arguments) -> executeKill(plugin, context, arguments))
            ;
    }

    public static boolean executeKill(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getExecutor();

        World world = plugin.getServer().getWorlds().getFirst();
        Location location = world.getSpawnLocation();
        if (arguments.hasArgument(CommandArguments.WORLD)) {
            world = arguments.getWorldArgument(CommandArguments.WORLD);
            location = world.getSpawnLocation();
        }
        else if (player != null) {
            world = player.getWorld();
            location = player.getLocation();
        }

        EntityType entityType = arguments.getArgument(CommandArguments.TYPE, EntityType.class);
        Class<? extends Entity> mobClass = entityType.getEntityClass();
        if (mobClass == null) return false;

        Set<Entity> mobs = new HashSet<>();
        int radius = arguments.getIntArgument(CommandArguments.RADIUS, -1);
        if (radius > 0) {
            mobs.addAll(world.getNearbyEntities(location, radius, radius, radius, entity -> mobClass.isAssignableFrom(entity.getClass())));
        }
        else {
            mobs.addAll(world.getEntitiesByClass(mobClass));
        }

        mobs.forEach(entity -> {
            if (entity.isDead() || !entity.isValid() || entity instanceof Player) return;
            if (entity instanceof Tameable tameable && tameable.isTamed()) return;

            entity.remove();
        });

        context.send(Lang.COMMAND_MOB_KILL_DONE.getMessage()
            .replace(Placeholders.GENERIC_RADIUS, radius > 0 ? NumberUtil.format(radius) : Lang.OTHER_INFINITY.getString())
            .replace(Placeholders.GENERIC_WORLD, LangAssets.get(world))
            .replace(Placeholders.GENERIC_TYPE, LangAssets.get(entityType))
            .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(mobs.size()))
        );

        return true;
    }

    public static DirectNodeBuilder builderSpawn(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        spawnLimit = Math.abs(ConfigValue.create("Settings.Mob.Spawn.Limit",
            10,
            "Sets max. possible amount for the '" + NODE_SPAWN + "' command."
        ).read(config));

        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(Lang.COMMAND_MOB_SPAWN_DESC)
            .permission(CommandPerms.MOB_SPAWN)
            .withArgument(CommandArguments.entityType(CommandArguments.TYPE, EntityType::isSpawnable)
                .required()
                .withSamples(tabContext -> SunUtils.getEntityTypes(EntityType::isSpawnable)))
            .withArgument(ArgumentTypes.integerAbs(CommandArguments.AMOUNT)
                .localized(Lang.COMMAND_ARGUMENT_NAME_AMOUNT)
                .withSamples(tabContext -> IntStream.range(1, spawnLimit).boxed().map(String::valueOf).toList()))
            .executes((context, arguments) -> executeSpawn(plugin, context, arguments))
            ;
    }

    public static boolean executeSpawn(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getExecutor();
        if (player == null) return false;

        EntityType entityType = arguments.getArgument(CommandArguments.TYPE, EntityType.class);

        int amount = Math.min(spawnLimit, arguments.getIntArgument(CommandArguments.AMOUNT, 1));
        //String name = result.getFlag(FLAG_NAME);

        Location location = LocationUtil.getCenter(player.getTargetBlock(null, 100).getRelative(BlockFace.UP).getLocation());
        for (int count = 0; count < amount; count++) {
            Entity entity = player.getWorld().spawnEntity(location, entityType);
            /*if (name != null) {
                entity.setCustomName(Colorizer.apply(name));
                entity.setCustomNameVisible(true);
            }*/
        }

        context.send(Lang.COMMAND_MOB_SPAWN_DONE.getMessage()
            .replace(Placeholders.GENERIC_AMOUNT, String.valueOf(amount))
            .replace(Placeholders.GENERIC_TYPE, LangAssets.get(entityType))
        );
        return true;
    }
}
