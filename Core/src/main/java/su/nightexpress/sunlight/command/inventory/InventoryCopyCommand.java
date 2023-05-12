package su.nightexpress.sunlight.command.inventory;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.SunUser;

import java.util.List;

public class InventoryCopyCommand extends AbstractCommand<SunLight> {

    public static final String NAME = "copy";

    public InventoryCopyCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_INVENTORY_COPY);
        this.setDescription(plugin.getMessage(Lang.COMMAND_INVENTORY_COPY_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_INVENTORY_COPY_USAGE));
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1 || (arg == 2 && player.hasPermission(Perms.COMMAND_INVENTORY_COPY_OTHERS))) {
            return CollectionsUtil.playerNames(player);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }
        if (result.length() < 3 && !(sender instanceof Player)) {
            this.errorSender(sender);
            return;
        }
        if (result.length() >= 3 && !sender.hasPermission(Perms.COMMAND_INVENTORY_COPY_OTHERS)) {
            this.errorPermission(sender);
            return;
        }

        Player source = plugin.getServer().getPlayer(result.getArg(1));
        Player target = plugin.getServer().getPlayer(result.length() >= 3 ? result.getArg(2) : sender.getName());

        SunUser userFrom = source == null ? plugin.getUserManager().getUserData(result.getArg(1)) : null;
        SunUser userTo = target == null ? plugin.getUserManager().getUserData(result.length() >= 3 ? result.getArg(2) : sender.getName()) : null;

        if ((source == null && userFrom == null) || (target == null && userTo == null)) {
            this.errorPlayer(sender);
            return;
        }
        if (sender.equals(source) && sender.equals(target)) {
            plugin.getMessage(Lang.ERROR_COMMAND_SELF).send(sender);
            return;
        }
        if (source == null) {
            source = plugin.getSunNMS().loadPlayerData(userFrom.getId(), userFrom.getName());
        }

        if (target == null) {
            target = plugin.getSunNMS().loadPlayerData(userTo.getId(), userTo.getName());
        }

        Inventory invFrom = plugin.getSunNMS().getPlayerInventory(source);
        Inventory invTo = plugin.getSunNMS().getPlayerInventory(target);
        for (int slot = 0; slot < invTo.getSize(); slot++) {
            invTo.setItem(slot, invFrom.getItem(slot));
        }
        target.saveData();

        if (sender == target) {
            plugin.getMessage(Lang.COMMAND_INVENTORY_COPY_DONE_NOTIFY)
                .replace(Placeholders.Player.replacer(source))
                .send(sender);
        }
        else {
            plugin.getMessage(Lang.COMMAND_INVENTORY_COPY_DONE_TARGET)
                .replace(Placeholders.GENERIC_SOURCE, source.getName())
                .replace(Placeholders.GENERIC_TARGET, target.getName())
                .send(sender);
        }
    }
}
