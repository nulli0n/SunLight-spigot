package su.nightexpress.sunlight.module.extras.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.EntityUtil;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.module.extras.ExtrasModule;
import su.nightexpress.sunlight.module.extras.config.ExtrasConfig;
import su.nightexpress.sunlight.module.extras.config.ExtrasPerms;

import java.util.List;
import java.util.Set;

public class ExtrasGenericListener extends AbstractListener<SunLight> {

    public ExtrasGenericListener(@NotNull ExtrasModule module) {
        super(module.plugin());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoinCommands(PlayerJoinEvent e) {
        if (!ExtrasConfig.JOIN_COMMANDS_ENABLED.get()) return;

        Player player = e.getPlayer();
        SunUser user = plugin.getUserManager().getUserData(player);
        List<String> commands;
        if (user.isRecentlyCreated()) {
            commands = ExtrasConfig.JOIN_COMMANDS_FIRST.get();
        }
        else {
            commands = ExtrasConfig.JOIN_COMMANDS_DEFAULT.get();
        }
        PlayerUtil.dispatchCommands(player, commands.toArray(new String[0]));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSignsColor(SignChangeEvent e) {
        if (!ExtrasConfig.SIGN_COLORS_ENABLED.get()) return;

        Player player = e.getPlayer();
        if (!player.hasPermission(ExtrasPerms.SIGNS_COLOR)) return;

        for (int index = 0; index < e.getLines().length; index++) {
            String line = e.getLine(index);
            if (line != null) {
                e.setLine(index, Colorizer.legacyHex(line));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAnvilsColor(PrepareAnvilEvent e) {
        if (!ExtrasConfig.ANVIL_COLORS_ENABLED.get()) return;
        if (e.getViewers().isEmpty()) return;

        ItemStack result = e.getResult();
        if (result == null || result.getType().isAir()) return;

        Player player = (Player) e.getViewers().get(0);
        if (!player.hasPermission(ExtrasPerms.ANVILS_COLOR)) return;

        ItemUtil.mapMeta(result, meta -> {
            meta.setDisplayName(Colorizer.legacyHex(meta.getDisplayName()));
        });
        e.setResult(result);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (!ExtrasConfig.KEEP_INVENTORY_ENABLED.get()) return;

        Player player = e.getEntity();
        if (EntityUtil.isNPC(player)) return;

        Set<String> ranks = PlayerUtil.getPermissionGroups(player);
        if (ranks.stream().anyMatch(rank -> ExtrasConfig.KEEP_INVENTORY_EXP_RANKS.get().contains(rank))) {
            e.setKeepLevel(true);
            e.setDroppedExp(0);
        }
        if (ranks.stream().anyMatch(rank -> ExtrasConfig.KEEP_INVENTORY_ITEMS_RANKS.get().contains(rank))) {
            e.setKeepInventory(true);
            e.getDrops().clear();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFarmKillerEnderEndermite(EntityTargetEvent e) {
        if (!ExtrasConfig.ANTI_FARM_ENDERMITE_MINECART.get()) return;
        if (!(e.getEntity() instanceof Enderman enderman)) return;
        if (!(e.getTarget() instanceof Endermite endermite)) return;

        e.setCancelled(endermite.isInsideVehicle());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFarmKillerFishingAuto(PlayerInteractEvent e) {
        if (!ExtrasConfig.ANTI_FARM_AUTO_FISHING.get()) return;
        if (e.useInteractedBlock() == Event.Result.DENY) return;
        if (e.useItemInHand() == Event.Result.DENY) return;

        Block block = e.getClickedBlock();
        if (block == null) return;

        Player player = e.getPlayer();
        ItemStack main = player.getInventory().getItemInMainHand();
        ItemStack off = player.getInventory().getItemInOffHand();
        if (!this.isFishingRod(main) && !this.isFishingRod(off)) return;

        Material blockType = block.getType();
        if (!blockType.isInteractable() && blockType.isSolid()) return;

        player.getNearbyEntities(16D, 16D, 16).stream()
            .filter(entity -> entity.getType() == EntityType.FISHING_HOOK).map(entity -> (FishHook) entity)
            .filter(fishHook -> fishHook.getShooter() instanceof Player && fishHook.getShooter().equals(player))
            .forEach(Entity::remove);
    }

    private boolean isFishingRod(@NotNull ItemStack item) {
        return !item.getType().isAir() && item.getType() == Material.FISHING_ROD;
    }
}
