package su.nightexpress.sunlight.command.inventory;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;

public class InventoryCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "inventory";

    public InventoryCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_INVENTORY);
        this.setDescription(plugin.getMessage(Lang.COMMAND_INVENTORY_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_INVENTORY_USAGE));

        this.addDefaultCommand(new HelpSubCommand<>(plugin));
        this.addChildren(new InventoryClearCommand(plugin));
        this.addChildren(new InventoryCopyCommand(plugin));
        this.addChildren(new InventoryFillCommand(plugin));
        this.addChildren(new InventoryOpenCommand(plugin));
        this.addChildren(new InventoryRepairCommand(plugin));
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {

    }
}
