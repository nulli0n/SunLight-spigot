package su.nightexpress.sunlight.command.item;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;

import java.util.Arrays;
import java.util.List;

public class ItemModelCommand extends AbstractCommand<SunLight> {

    public static final String NAME = "model";

    public ItemModelCommand(@NotNull SunLight plugin) {
        super(plugin, new String[] {NAME}, Perms.COMMAND_ITEM_MODEL);
        this.setDescription(plugin.getMessage(Lang.COMMAND_ITEM_MODEL_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_ITEM_MODEL_USAGE));
        this.setPlayerOnly(true);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return Arrays.asList("0", "10001");
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        if (item.getType().isAir() || meta == null) {
            plugin.getMessage(Lang.COMMAND_ITEM_ERROR_EMPTY_HAND).send(sender);
            return;
        }

        int modelData = result.getInt(1, 0);
        meta.setCustomModelData(modelData);
        item.setItemMeta(meta);

        plugin.getMessage(Lang.COMMAND_ITEM_MODEL_DONE)
            .replace(Placeholders.GENERIC_AMOUNT, modelData)
            .replace(Placeholders.GENERIC_NAME, ItemUtil.getItemName(item))
            .send(sender);
    }
}
