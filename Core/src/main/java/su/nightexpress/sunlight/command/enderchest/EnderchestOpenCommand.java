package su.nightexpress.sunlight.command.enderchest;

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

public class EnderchestOpenCommand extends TargetCommand {

    public static final String NAME = "open";

    public EnderchestOpenCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_ENDERCHEST_OPEN, Perms.COMMAND_ENDERCHEST_OPEN_OTHERS, 1);
        this.setAllowDataLoad();
        this.setDescription(plugin.getMessage(Lang.COMMAND_ENDERCHEST_OPEN_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_ENDERCHEST_OPEN_USAGE));
        this.setPlayerOnly(true);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player player = (Player) sender;
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        Inventory inventory = plugin.getSunNMS().getPlayerEnderChest(target);
        player.openInventory(inventory);

        plugin.getMessage(Lang.COMMAND_ENDERCHEST_OPEN_DONE_EXECUTOR)
            .replace(Placeholders.forPlayer(target))
            .send(sender);
    }
}
