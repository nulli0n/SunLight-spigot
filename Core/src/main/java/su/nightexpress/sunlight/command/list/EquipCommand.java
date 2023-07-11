package su.nightexpress.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;

import java.util.List;

public class EquipCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "equip";

    public EquipCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_EQUIP);
        this.setDescription(plugin.getMessage(Lang.COMMAND_EQUIP_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_EQUIP_USAGE));
        this.setPlayerOnly(true);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return CollectionsUtil.getEnumsList(EquipmentSlot.class).stream().map(String::toLowerCase).toList();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 1) {
            this.printUsage(sender);
            return;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            this.plugin.getMessage(Lang.COMMAND_ITEM_ERROR_EMPTY_HAND).send(sender);
            return;
        }

        EquipmentSlot slot = StringUtil.getEnum(result.getArg(0), EquipmentSlot.class).orElse(EquipmentSlot.HAND);
        if (!player.hasPermission(Perms.COMMAND_EQUIP.getName() + "." + slot.name().toLowerCase())) {
            this.errorPermission(sender);
            return;
        }

        if (slot != EquipmentSlot.HAND) {
            ItemStack oldItem = player.getInventory().getItem(slot);
            player.getInventory().setItemInMainHand(null);
            player.getInventory().setItem(slot, item);

            if (oldItem != null && !oldItem.getType().isAir()) {
                PlayerUtil.addItem(player, oldItem);
            }

            plugin.getMessage(Lang.COMMAND_EQUIP_DONE).replace(Placeholders.GENERIC_ITEM, ItemUtil.getItemName(item)).send(sender);
        }
    }
}
