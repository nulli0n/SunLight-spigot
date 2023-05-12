package su.nightexpress.sunlight.command.mob;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandFlag;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.EntityUtil;
import su.nexmedia.engine.utils.NumberUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.config.Lang;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MobKillCommand extends AbstractCommand<SunLight> {

    public static final String NAME = "kill";

    private static final CommandFlag<Boolean> FLAG_FORCE = CommandFlag.booleanFlag("force");
    private static final CommandFlag<Integer> FLAG_RADIUS = CommandFlag.intFlag("r");
    private static final CommandFlag<Integer> FLAG_LIMIT = CommandFlag.intFlag("lim");

    private static final Set<EntityType> ALLOWED_VALUES = Stream.of(EntityType.values()).collect(Collectors.toSet());

    public MobKillCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_MOB_KILL);
        this.setUsage(plugin.getMessage(Lang.COMMAND_MOB_KILL_USAGE));
        this.setDescription(plugin.getMessage(Lang.COMMAND_MOB_KILL_DESC));
        this.addFlag(CommandFlags.WORLD, CommandFlags.ALL, FLAG_FORCE, FLAG_RADIUS, FLAG_LIMIT);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg >= 1) {
            return new ArrayList<>(ALLOWED_VALUES.stream().map(Enum::name).map(String::toLowerCase).toList());
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        World world;
        if (result.hasFlag(CommandFlags.WORLD)) {
            world = result.getFlag(CommandFlags.WORLD);
        }
        else if (sender instanceof Player player) {
            world = player.getWorld();
        }
        else {
            this.printUsage(sender);
            return;
        }

        if (world == null) {
            this.plugin.getMessage(Lang.ERROR_WORLD_INVALID).send(sender);
            return;
        }

        Set<Class<? extends Entity>> mobClasses = new HashSet<>();
        if (result.hasFlag(CommandFlags.ALL)) {
            mobClasses.add(LivingEntity.class);
        }
        else {
            Stream.of(result.getArgs()).skip(1).forEach(mobName -> {
                EntityType entityType = StringUtil.getEnum(mobName, EntityType.class).orElse(null);
                if (entityType == null || entityType.getEntityClass() == null) return;

                if (!ALLOWED_VALUES.contains(entityType)) {
                    plugin.getMessage(Lang.COMMAND_MOB_KILL_ERROR_TYPE).replace(Placeholders.GENERIC_TYPE, entityType.name()).send(sender);
                    return;
                }
                mobClasses.add(entityType.getEntityClass());
            });
        }

        if (mobClasses.isEmpty()) {
            this.printUsage(sender);
            return;
        }

        double radius = result.getFlag(FLAG_RADIUS, -1);
        boolean isForce = result.hasFlag(FLAG_FORCE);

        Set<Entity> mobs = new HashSet<>();
        if (radius > 0D && sender instanceof Player player && player.getWorld() == world) {
            mobClasses.forEach(clazz -> {
                mobs.addAll(world.getNearbyEntities(player.getLocation(), radius, radius, radius, e -> clazz.isAssignableFrom(e.getClass())));
            });
        }
        else {
            mobClasses.forEach(clazz -> {
                mobs.addAll(world.getEntitiesByClass(clazz));
            });
        }

        Map<EntityType, Integer> killed = new HashMap<>();
        mobs.stream().filter(mob -> {
            if (mob instanceof Player) return false;
            if (EntityUtil.isNPC(mob)) return false;
            if (!isForce) {
                if (mob instanceof Tameable tameable && tameable.isTamed()) return false;
                if (mob instanceof Villager && !mobClasses.contains(Villager.class)) return false;
                if (mob.isInvulnerable()) return false;
            }
            return !mob.isDead() && mob.isValid();
        }).forEach(e -> {
            EntityType type = e.getType();
            killed.put(type, killed.computeIfAbsent(type, i -> 0) + 1);
            e.remove();
        });

        plugin.getMessage(Lang.COMMAND_MOB_KILL_DONE)
            .replace(Placeholders.GENERIC_RADIUS, radius > 0 ? NumberUtil.format(radius) : LangManager.getPlain(Lang.OTHER_INFINITY))
            .replace(Placeholders.GENERIC_TOTAL, String.valueOf(killed.values().stream().mapToInt(i -> i).sum()))
            .replace(Placeholders.GENERIC_WORLD, LangManager.getWorld(world))
            .replace(line -> line.contains(Placeholders.GENERIC_TYPE), (line, list) -> {
                killed.forEach((type, amount) -> {
                    list.add(line
                        .replace(Placeholders.GENERIC_TYPE, plugin.getLangManager().getEnum(type))
                        .replace(Placeholders.GENERIC_AMOUNT, String.valueOf(amount)));
                });
            })
            .send(sender);
    }
}
