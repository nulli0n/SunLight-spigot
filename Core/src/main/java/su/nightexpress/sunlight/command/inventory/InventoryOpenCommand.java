package su.nightexpress.sunlight.command.inventory;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.Placeholders;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.api.TargetCommand;
import su.nightexpress.sunlight.config.Lang;

public class InventoryOpenCommand extends TargetCommand {

    public static final String NAME = "open";

    public InventoryOpenCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_INVENTORY_OPEN, Perms.COMMAND_INVENTORY_OPEN, 1);
        this.setAllowDataLoad();
        this.setDescription(plugin.getMessage(Lang.COMMAND_INVENTORY_OPEN_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_INVENTORY_OPEN_USAGE));
        this.setPlayerOnly(true);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;
        if (target == sender) {
            this.plugin.getMessage(Lang.ERROR_COMMAND_SELF).send(sender);
            return;
        }

        Player player = (Player) sender;
        Inventory inventory = plugin.getSunNMS().getPlayerInventory(target);
        player.openInventory(inventory);

        plugin.getMessage(Lang.COMMAND_INVENTORY_OPEN_DONE)
            .replace(Placeholders.forPlayer(target))
            .send(sender);
    }
}
