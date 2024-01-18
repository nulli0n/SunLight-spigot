package su.nightexpress.sunlight.command.mob;

import org.bukkit.Location;
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

    private static final CommandFlag<Integer> FLAG_RADIUS = CommandFlag.intFlag("r");
    private static final CommandFlag<Integer> FLAG_LIMIT = CommandFlag.intFlag("lim");

    private static final Set<EntityType> ALLOWED_VALUES = Stream.of(EntityType.values()).collect(Collectors.toSet());

    public MobKillCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_MOB_KILL);
        this.setUsage(plugin.getMessage(Lang.COMMAND_MOB_KILL_USAGE));
        this.setDescription(plugin.getMessage(Lang.COMMAND_MOB_KILL_DESC));
        this.addFlag(CommandFlags.WORLD, FLAG_RADIUS, FLAG_LIMIT);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return new ArrayList<>(ALLOWED_VALUES.stream().map(Enum::name).map(String::toLowerCase).toList());
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

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

        EntityType entityType = StringUtil.getEnum(result.getArg(1), EntityType.class).orElse(null);
        if (entityType == null || entityType.getEntityClass() == null) return;

        if (!ALLOWED_VALUES.contains(entityType)) {
            plugin.getMessage(Lang.COMMAND_MOB_KILL_ERROR_TYPE).replace(Placeholders.GENERIC_TYPE, entityType.name()).send(sender);
            return;
        }

        Class<? extends Entity> mobClass = entityType.getEntityClass();
        double radius = result.getFlag(FLAG_RADIUS, -1);

        Set<Entity> mobs = new HashSet<>();
        if (radius > 0) {
            Location location = sender instanceof Player player && player.getWorld() == world ? player.getLocation() : world.getSpawnLocation();

            mobs.addAll(world.getNearbyEntities(location, radius, radius, radius, entity -> mobClass.isAssignableFrom(entity.getClass())));
        }
        else {
            mobs.addAll(world.getEntitiesByClass(mobClass));
        }

        mobs.removeIf(mob -> {
            if (mob instanceof Player) return true;
            if (EntityUtil.isNPC(mob)) return true;
            if (mob instanceof Tameable tameable && tameable.isTamed()) return true;
            return mob.isDead() || !mob.isValid();
        });
        mobs.forEach(Entity::remove);

        plugin.getMessage(Lang.COMMAND_MOB_KILL_DONE)
            .replace(Placeholders.GENERIC_RADIUS, radius > 0 ? NumberUtil.format(radius) : LangManager.getPlain(Lang.OTHER_INFINITY))
            .replace(Placeholders.GENERIC_WORLD, LangManager.getWorld(world))
            .replace(Placeholders.GENERIC_TYPE, LangManager.getEntityType(entityType))
            .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(mobs.size()))
            .send(sender);
    }
}
