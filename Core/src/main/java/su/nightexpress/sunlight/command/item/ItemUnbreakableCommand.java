package su.nightexpress.sunlight.command.item;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;

public class ItemUnbreakableCommand extends AbstractCommand<SunLight> {

    public static final String NAME = "unbreakable";

    public ItemUnbreakableCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_ITEM_UNBREAKABLE);
        this.setDescription(plugin.getMessage(Lang.COMMAND_ITEM_UNBREAKABLE_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_ITEM_UNBREAKABLE_USAGE));
        this.setPlayerOnly(true);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir() || !(item.getItemMeta() instanceof Damageable damageable)) {
            this.plugin.getMessage(Lang.COMMAND_ITEM_UNBREAKABLE_ERROR_BAD_ITEM)
                .replace(Placeholders.GENERIC_ITEM, ItemUtil.getItemName(item))
                .send(sender);
            return;
        }

        damageable.setUnbreakable(!damageable.isUnbreakable());
        item.setItemMeta(damageable);
        this.plugin.getMessage(Lang.COMMAND_ITEM_UNBREAKABLE_DONE)
            .replace(Placeholders.GENERIC_ITEM, ItemUtil.getItemName(item))
            .replace(Placeholders.GENERIC_STATE, LangManager.getBoolean(damageable.isUnbreakable()))
            .send(player);
    }
}
