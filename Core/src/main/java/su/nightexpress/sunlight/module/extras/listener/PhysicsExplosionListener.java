package su.nightexpress.sunlight.module.extras.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.GlassPane;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.utils.LocationUtil;
import su.nexmedia.engine.utils.PDCUtil;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.sunlight.SunLight;

import java.util.List;

public class PhysicsExplosionListener extends AbstractListener<SunLight> {

    private final NamespacedKey physx;

    public PhysicsExplosionListener(@NotNull SunLight plugin) {
        super(plugin);
        this.physx = new NamespacedKey(plugin, "physical_block");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent e) {
        this.create(e.blockList(), e.getLocation());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent e) {
        this.create(e.blockList(), e.getBlock().getLocation());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPhysLand(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof FallingBlock fallingBlock && PDCUtil.getBoolean(fallingBlock, this.physx).isPresent()) {
            plugin.getSunNMS().dropFallingContent(fallingBlock);
            event.setCancelled(true);
        }
    }

    private void create(@NotNull List<Block> list, @NotNull Location from) {
        list.removeIf(block -> {
            Material type = block.getType();
            if (type.isInteractable()) return false;

            BlockData blockData = block.getBlockData();

            // Do not launch non-solid, leaves & glasses.
            if (!type.isSolid() || blockData instanceof Leaves || blockData instanceof GlassPane) {
                return false;
            }
            if (type == Material.AIR || type == Material.TNT) return false;

            BlockData fallData = blockData;
            if (type == Material.GRASS || type == Material.MYCELIUM || type == Material.DIRT_PATH) {
                fallData = Material.DIRT.createBlockData();
            }

            FallingBlock fall = block.getWorld().spawnFallingBlock(block.getLocation(), fallData);
            fall.setDropItem(true);
            fall.setCancelDrop(false);
            fall.setFallDistance(0F);
            PDCUtil.set(fall, this.physx, true);

            Vector vector = LocationUtil.getDirection(from, fall.getLocation()).multiply(0.6D);
            vector.setY(vector.getY() + 0.3D + Rnd.getDouble(0D, 2D) / 4.5D);
            fall.setVelocity(vector);

            block.setType(Material.AIR);
            return true;
        });
    }
}
