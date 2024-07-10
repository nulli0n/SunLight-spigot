package su.nightexpress.sunlight.module.extras.chairs;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.nightcore.util.LocationUtil;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.data.user.SunUser;

public class ChairsListener extends AbstractListener<SunLightPlugin> {

    private final ChairsManager chairsManager;

    public ChairsListener(@NotNull SunLightPlugin plugin, @NotNull ChairsManager chairsManager) {
        super(plugin);
        this.chairsManager = chairsManager;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChairsEnterRightClick(PlayerInteractEvent e) {
        if (e.useInteractedBlock() == Event.Result.DENY) return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (e.getBlockFace() == BlockFace.DOWN) return;

        Player player = e.getPlayer();
        if (player.isSneaking()) return;

        Block block = e.getClickedBlock();
        if (block == null || !ChairsManager.isChair(block)) return;

        SunUser user = plugin.getUserManager().getUserData(player);
        if (!ChairsManager.isChairsEnabled(user)) return;

        if (player.getLocation().distance(LocationUtil.getCenter(block.getLocation())) >= 2D) {
            return;
        }

        // Stop sit if player is building something over "chair" blocks.
        ItemStack item = e.getItem();
        if (item != null && item.getType().isBlock()) return;

        this.chairsManager.sitPlayer(player, block);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChairsLeaveDismount(EntityDismountEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (!(e.getDismounted() instanceof ArmorStand stand)) return;

        this.chairsManager.standUp(player, stand, false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChairsLeaveChangeGameMode(PlayerGameModeChangeEvent e) {
        if (e.getNewGameMode() == GameMode.SPECTATOR) {
            this.chairsManager.standUp(e.getPlayer(), false);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChairsLeaveDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        this.chairsManager.standUp(player, true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChairsLeaveQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.chairsManager.standUp(player, false);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onChairsProtectBlockBreak(BlockBreakEvent e) {
        if (this.chairsManager.isOccupied(e.getBlock())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChairsProtectBlockExplode(BlockExplodeEvent e) {
        e.blockList().removeIf(this.chairsManager::isOccupied);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChairsProtectEntityExplode(EntityExplodeEvent e) {
        e.blockList().removeIf(this.chairsManager::isOccupied);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onChairsProtectPistonExtend(BlockPistonExtendEvent e) {
        if (e.getBlocks().stream().anyMatch(this.chairsManager::isOccupied)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onChairsProtectPistonRetract(BlockPistonRetractEvent e) {
        if (e.getBlocks().stream().anyMatch(this.chairsManager::isOccupied)) {
            e.setCancelled(true);
        }
    }
}
