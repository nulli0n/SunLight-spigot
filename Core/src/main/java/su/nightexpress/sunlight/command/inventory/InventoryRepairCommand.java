package su.nightexpress.sunlight.command.inventory;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.TargetCommand;
import su.nightexpress.sunlight.config.Lang;

public class InventoryRepairCommand extends TargetCommand {

    public static final String NAME = "repair";

    public InventoryRepairCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_INVENTORY_REPAIR, Perms.COMMAND_INVENTORY_REPAIR_OTHERS, 1);
        this.setAllowDataLoad();
        this.setDescription(plugin.getMessage(Lang.COMMAND_INVENTORY_REPAIR_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_INVENTORY_REPAIR_USAGE));
        this.addFlag(CommandFlags.SILENT);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        plugin.getSunNMS().getPlayerInventory(target).forEach(this::fix);
        target.saveData();

        if (sender != target) {
            plugin.getMessage(Lang.COMMAND_INVENTORY_REPAIR_TARGET)
                .replace(Placeholders.forPlayer(target))
                .send(sender);
        }
        if (!result.hasFlag(CommandFlags.SILENT)) {
            plugin.getMessage(Lang.COMMAND_INVENTORY_REPAIR_NOTIFY).send(target);
        }
    }

    private boolean fix(@Nullable ItemStack item) {
        if (item == null) return false;

        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable damageable)) return false;

        damageable.setDamage(0);
        item.setItemMeta(meta);
        return true;
    }
}
