package su.nightexpress.sunlight.command.item;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
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

public class ItemDamageCommand extends AbstractCommand<SunLight> {

    public static final String NAME = "damage";

    public ItemDamageCommand(@NotNull SunLight plugin) {
        super(plugin, new String[] {NAME}, Perms.COMMAND_ITEM_DAMAGE);
        this.setDescription(plugin.getMessage(Lang.COMMAND_ITEM_DAMAGE_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_ITEM_DAMAGE_USAGE));
        this.setPlayerOnly(true);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return Arrays.asList("0", "50", "100", "500");
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir() || !(item.getItemMeta() instanceof Damageable damageable)) {
            plugin.getMessage(Lang.COMMAND_ITEM_DAMAGE_ERROR_BAD_ITEM)
                .replace(Placeholders.GENERIC_ITEM, ItemUtil.getItemName(item))
                .send(sender);
            return;
        }

        int amount = result.getInt(1, 0);
        damageable.setDamage(amount);
        item.setItemMeta(damageable);

        plugin.getMessage(Lang.COMMAND_ITEM_DAMAGE_DONE)
            .replace(Placeholders.GENERIC_AMOUNT, amount)
            .replace(Placeholders.GENERIC_ITEM, ItemUtil.getItemName(item))
            .send(sender);
    }
}
