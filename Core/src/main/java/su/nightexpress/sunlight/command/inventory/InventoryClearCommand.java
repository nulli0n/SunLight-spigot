package su.nightexpress.sunlight.command.inventory;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.TargetCommand;
import su.nightexpress.sunlight.config.Lang;

public class InventoryClearCommand extends TargetCommand {

    public static final String NAME = "clear";

    public InventoryClearCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_INVENTORY_CLEAR, Perms.COMMAND_INVENTORY_CLEAR_OTHERS, 1);
        this.setAllowDataLoad();
        this.setDescription(plugin.getMessage(Lang.COMMAND_INVENTORY_CLEAR_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_INVENTORY_CLEAR_USAGE));
        this.addFlag(CommandFlags.SILENT);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        plugin.getSunNMS().getPlayerInventory(target).clear();
        target.saveData();

        if (!result.hasFlag(CommandFlags.SILENT)) {
            plugin.getMessage(Lang.COMMAND_INVENTORY_CLEAR_DONE_NOTIFY)
                .replace(Placeholders.forSender(sender))
                .send(target);
        }
        if (sender != target) {
            plugin.getMessage(Lang.COMMAND_INVENTORY_CLEAR_DONE_TARGET)
                .replace(Placeholders.forPlayer(target))
                .send(sender);
        }
    }
}
