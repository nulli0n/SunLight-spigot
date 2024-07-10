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
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.nightcore.util.text.tag.TagPool;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.module.extras.ExtrasModule;
import su.nightexpress.sunlight.module.extras.config.ExtrasConfig;
import su.nightexpress.sunlight.module.extras.config.ExtrasPerms;

import java.util.List;
import java.util.Set;

public class ExtrasGenericListener extends AbstractListener<SunLightPlugin> {

    public ExtrasGenericListener(@NotNull SunLightPlugin plugin, @NotNull ExtrasModule module) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoinCommands(PlayerJoinEvent event) {
        if (!ExtrasConfig.JOIN_COMMANDS_ENABLED.get()) return;

        Player player = event.getPlayer();
        SunUser user = plugin.getUserManager().getUserData(player);
        List<String> commands;
        if (user.isNewlyCreated()) {
            commands = ExtrasConfig.JOIN_COMMANDS_FIRST.get();
        }
        else {
            commands = ExtrasConfig.JOIN_COMMANDS_DEFAULT.get();
        }
        Players.dispatchCommands(player, commands);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSignsColor(SignChangeEvent event) {
        if (!ExtrasConfig.SIGN_COLORS_ENABLED.get()) return;

        Player player = event.getPlayer();
        if (!player.hasPermission(ExtrasPerms.SIGNS_COLOR)) return;

        for (int index = 0; index < event.getLines().length; index++) {
            String line = event.getLine(index);
            if (line != null) {
                event.setLine(index, NightMessage.from(line, TagPool.BASE_COLORS_AND_STYLES).toLegacy());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAnvilColor(PrepareAnvilEvent event) {
        if (!ExtrasConfig.ANVIL_COLORS_ENABLED.get()) return;
        if (event.getViewers().isEmpty()) return;

        ItemStack result = event.getResult();
        if (result == null || result.getType().isAir()) return;

        Player player = (Player) event.getViewers().getFirst();
        if (!player.hasPermission(ExtrasPerms.ANVILS_COLOR)) return;

        ItemUtil.editMeta(result, meta -> {
            meta.setDisplayName(NightMessage.from(meta.getDisplayName(), TagPool.BASE_COLORS_AND_STYLES).toLegacy());
        });
        event.setResult(result);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onKeepInventoryDeath(PlayerDeathEvent event) {
        if (!ExtrasConfig.KEEP_INVENTORY_ENABLED.get()) return;

        Player player = event.getEntity();

        Set<String> ranks = Players.getPermissionGroups(player);
        if (ranks.stream().anyMatch(rank -> ExtrasConfig.KEEP_INVENTORY_XP_RANKS.get().contains(rank))) {
            event.setKeepLevel(true);
            event.setDroppedExp(0);
        }
        if (ranks.stream().anyMatch(rank -> ExtrasConfig.KEEP_INVENTORY_ITEMS_RANKS.get().contains(rank))) {
            event.setKeepInventory(true);
            event.getDrops().clear();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFarmKillerEnderEndermite(EntityTargetEvent event) {
        if (!ExtrasConfig.ANTI_FARM_ENDERMITE_MINECART.get()) return;
        if (!(event.getEntity() instanceof Enderman enderman)) return;
        if (!(event.getTarget() instanceof Endermite endermite)) return;

        event.setCancelled(endermite.isInsideVehicle());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFarmKillerFishingAuto(PlayerInteractEvent event) {
        if (!ExtrasConfig.ANTI_FARM_AUTO_FISHING.get()) return;
        if (event.useInteractedBlock() == Event.Result.DENY) return;
        if (event.useItemInHand() == Event.Result.DENY) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        Player player = event.getPlayer();
        ItemStack main = player.getInventory().getItemInMainHand();
        ItemStack off = player.getInventory().getItemInOffHand();
        if (!this.isFishingRod(main) && !this.isFishingRod(off)) return;

        Material blockType = block.getType();
        if (!blockType.isInteractable() && blockType.isSolid()) return;

        player.getNearbyEntities(16D, 16D, 16).stream()
            .filter(entity -> entity instanceof FishHook).map(entity -> (FishHook) entity)
            .filter(fishHook -> fishHook.getShooter() instanceof Player && fishHook.getShooter().equals(player))
            .forEach(Entity::remove);
    }

    private boolean isFishingRod(@NotNull ItemStack item) {
        return !item.getType().isAir() && item.getType() == Material.FISHING_ROD;
    }
}
