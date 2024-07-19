package su.nightexpress.sunlight.module.extras.listener;

import com.google.common.collect.Sets;
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
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.nightcore.util.LocationUtil;
import su.nightexpress.nightcore.util.PDCUtil;
import su.nightexpress.nightcore.util.random.Rnd;
import su.nightexpress.sunlight.SunLightPlugin;

import java.util.List;
import java.util.Set;

public class PhysicsExplosionListener extends AbstractListener<SunLightPlugin> {

    private static final Set<Material> ILLEGAL_ITEMS = Sets.newHashSet(
        Material.AIR, Material.TNT, Material.SPAWNER,
        Material.BEDROCK, Material.BARRIER, Material.FARMLAND, Material.BUDDING_AMETHYST
    );

    private final NamespacedKey physx;

    public PhysicsExplosionListener(@NotNull SunLightPlugin plugin) {
        super(plugin);
        this.physx = new NamespacedKey(plugin, "physical_block");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!this.plugin.getSunNMS().canDestroyBlocks(event)) return;

        this.create(event.blockList(), event.getLocation());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        this.create(event.blockList(), event.getBlock().getLocation());
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
            if (ILLEGAL_ITEMS.contains(type)) return false;

            BlockData fallData = blockData;
            if (type == Material.GRASS_BLOCK || type == Material.MYCELIUM || type == Material.DIRT_PATH) {
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
