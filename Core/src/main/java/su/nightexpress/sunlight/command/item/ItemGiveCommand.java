package su.nightexpress.sunlight.command.item;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.api.TargetCommand;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.utils.SunUtils;

import java.util.*;

public class ItemGiveCommand extends TargetCommand {

    public static final String NAME = "give";

    public ItemGiveCommand(@NotNull SunLight plugin) {
        super(plugin, new String[] {NAME}, Perms.COMMAND_ITEM_GIVE, Perms.COMMAND_ITEM_GIVE, 1);
        this.setAllowDataLoad();
        this.setDescription(plugin.getMessage(Lang.COMMAND_ITEM_GIVE_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_ITEM_GIVE_USAGE));
        this.addFlag(ItemCommand.FLAG_NAME, ItemCommand.FLAG_LORE, ItemCommand.FLAG_ENCHANTS);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return CollectionsUtil.playerNames(player);
        }
        if (arg == 2) {
            return SunUtils.ITEM_TYPES;
        }
        if (arg == 3) {
            return Arrays.asList("1", "8", "16", "32", "64", "128", "256", "512");
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 3) {
            this.printUsage(sender);
            return;
        }

        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        Material material = Material.getMaterial(result.getArg(2).toUpperCase());
        if (material == null) {
            plugin.getMessage(Lang.COMMAND_ITEM_ERROR_MATERIAL).replace(Placeholders.GENERIC_TYPE, result.getArg(2)).send(sender);
            return;
        }

        int amount = result.getInt(3, 1);

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

        PlayerUtil.addItem(target, item, amount);

        plugin.getMessage(Lang.COMMAND_ITEM_GIVE_DONE)
            .replace(Placeholders.forPlayer(target))
            .replace(Placeholders.GENERIC_AMOUNT, amount)
            .replace(Placeholders.GENERIC_TYPE, ItemUtil.getItemName(item))
            .send(sender);

        plugin.getMessage(Lang.COMMAND_ITEM_GIVE_NOTIFY)
            .replace(Placeholders.PLAYER_NAME, sender.getName())
            .replace(Placeholders.GENERIC_AMOUNT, amount)
            .replace(Placeholders.GENERIC_TYPE, ItemUtil.getItemName(item))
            .send(target);
    }
}
