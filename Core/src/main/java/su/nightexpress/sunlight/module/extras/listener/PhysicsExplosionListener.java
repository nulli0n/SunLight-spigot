package su.nightexpress.sunlight.module.extras.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.GlassPane;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.sunlight.SunLight;

import java.util.List;

public class PhysicsExplosionListener extends AbstractListener<SunLight> {

    public PhysicsExplosionListener(@NotNull SunLight plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent e) {
        this.create(e.blockList());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent e) {
        this.create(e.blockList());
    }

    private void create(@NotNull List<Block> list) {
        for (Block block : list) {
            Material type = block.getType();
            if ((type == Material.GRASS) || (type == Material.MYCELIUM)) {
                block.setType(Material.DIRT);
            }

            if (!type.isSolid() || block.getBlockData() instanceof Leaves || block.getBlockData() instanceof GlassPane) {
                block.setType(Material.AIR);
            }
            if (type == Material.TNT) {
                block.setType(Material.AIR);
                TNTPrimed primed = block.getWorld().spawn(block.getLocation(), TNTPrimed.class);
                primed.setFuseTicks(40);
                continue;
            }

            if (block.getType() == Material.AIR) continue;

            FallingBlock fall = block.getWorld().spawnFallingBlock(block.getLocation(), block.getBlockData());
            fall.setDropItem(false);
            fall.setFallDistance(0.0F);

            int rX = Rnd.nextBoolean() ? -1 : 1;
            int rZ = Rnd.nextBoolean() ? -1 : 1;

            double vX = Rnd.nextBoolean() ? rX * (0.25D + Rnd.getDouble(0D, 3D) / 5D) : 0.0D;
            double vY = 0.6D + Rnd.getDouble(0D, 2D) / 4.5D;
            double vZ = Rnd.nextBoolean() ? rZ * (0.25D + Rnd.getDouble(0D, 3D) / 5D) : 0.0D;

            fall.setVelocity(new Vector(vX, vY, vZ));
        }
    }
}
