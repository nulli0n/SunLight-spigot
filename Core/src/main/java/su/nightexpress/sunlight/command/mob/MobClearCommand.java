package su.nightexpress.sunlight.command.mob;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.EntityUtil;
import su.nexmedia.engine.utils.NumberUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.config.Lang;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class MobClearCommand extends AbstractCommand<SunLight> {

    public static final String NAME = "clear";

    public MobClearCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_MOB_CLEAR);
        this.setDescription(plugin.getMessage(Lang.COMMAND_MOB_CLEAR_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_MOB_CLEAR_USAGE));
        this.addFlag(CommandFlags.WORLD);
    }

    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return Arrays.asList("-1", "5", "10", "15", "100");
        }
        return super.getTab(player, arg, args);
    }

    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        World world;
        int radius = result.getInt(1, -1);
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

        HashSet<Entity> mobs = new HashSet<>();
        if (radius > 0) {
            Player player;
            Location location = sender instanceof Player && (player = (Player)sender).getWorld() == world ? player.getLocation() : world.getSpawnLocation();
            mobs.addAll(world.getNearbyEntities(location, radius, radius, radius, entity -> entity instanceof LivingEntity));
        }
        else {
            mobs.addAll(world.getEntitiesByClass(LivingEntity.class));
        }

        mobs.removeIf(mob -> {
            if (mob instanceof Player) return true;
            if (EntityUtil.isNPC(mob)) return true;
            if (mob instanceof Tameable tameable && tameable.isTamed()) return true;
            if (mob instanceof Villager) return true;
            if (mob.isInvulnerable()) return true;

            return mob.isDead() || !mob.isValid();
        });

        mobs.forEach(Entity::remove);
        this.plugin.getMessage(Lang.COMMAND_MOB_CLEAR_DONE)
        .replace(Placeholders.GENERIC_RADIUS, radius > 0 ? NumberUtil.format(radius) : LangManager.getPlain(Lang.OTHER_INFINITY))
        .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(mobs.size()))
        .replace(Placeholders.GENERIC_WORLD, LangManager.getWorld(world))
        .send(sender);
    }
}

