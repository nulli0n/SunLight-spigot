package su.nightexpress.sunlight.command.item;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.utils.SunUtils;

import java.util.*;

public class ItemGetCommand extends AbstractCommand<SunLight> {

    public static final String NAME = "get";

    public ItemGetCommand(@NotNull SunLight plugin) {
        super(plugin, new String[] {NAME}, Perms.COMMAND_ITEM_GET);
        this.setDescription(plugin.getMessage(Lang.COMMAND_ITEM_GET_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_ITEM_GET_USAGE));
        this.setPlayerOnly(true);
        this.addFlag(ItemCommand.FLAG_ENCHANTS, ItemCommand.FLAG_LORE, ItemCommand.FLAG_NAME);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return SunUtils.ITEM_TYPES;
        }
        if (arg == 2) {
            return Arrays.asList("1", "8", "16", "32", "64", "128", "256", "512");
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        Material material = Material.getMaterial(result.getArg(1).toUpperCase());
        if (material == null) {
            plugin.getMessage(Lang.COMMAND_ITEM_ERROR_MATERIAL).replace(Placeholders.GENERIC_TYPE, result.getArg(1)).send(sender);
            return;
        }

        int amount = result.getInt(2, material.getMaxStackSize());

        ItemStack item = new ItemStack(material);

        String flagName = result.getFlag(ItemCommand.FLAG_NAME);
        String itemLore = result.getFlag(ItemCommand.FLAG_LORE);
        String flagEnchants = result.getFlag(ItemCommand.FLAG_ENCHANTS);

        List<String> checkLore = itemLore == null ? new ArrayList<>() : ItemCommand.parseFlagLore(itemLore);
        Map<Enchantment, Integer> itemEnchants = flagEnchants == null ? new HashMap<>() : ItemCommand.parseFlagEnchants(flagEnchants);

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (flagName != null) meta.setDisplayName(flagName);
            if (itemLore != null) meta.setLore(checkLore);
            if (flagEnchants != null) itemEnchants.forEach((enchant, level) -> meta.addEnchant(enchant, level, true));
        }
        item.setItemMeta(meta);

        Player player = (Player) sender;
        PlayerUtil.addItem(player, item, amount);

        plugin.getMessage(Lang.COMMAND_ITEM_GET_DONE)
            .replace(Placeholders.GENERIC_AMOUNT, amount)
            .replace(Placeholders.GENERIC_TYPE, ItemUtil.getItemName(item))
            .send(sender);
    }
}
