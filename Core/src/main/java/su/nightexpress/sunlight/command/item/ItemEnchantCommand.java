package su.nightexpress.sunlight.command.item;

import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.NumberUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ItemEnchantCommand extends AbstractCommand<SunLight> {

    public static final String NAME = "enchant";

    public ItemEnchantCommand(@NotNull SunLight plugin) {
        super(plugin, new String[] {NAME}, Perms.COMMAND_ITEM_ENCHANT);
        this.setDescription(plugin.getMessage(Lang.COMMAND_ITEM_ENCHANT_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_ITEM_ENCHANT_USAGE));
        this.setPlayerOnly(true);

    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return Stream.of(Enchantment.values()).map(e -> e.getKey().getKey()).toList();
        }
        if (arg == 2) {
            return Arrays.asList("0", "1", "2", "3", "4", "5", "10", "100", "200", "300", "1000");
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 3) {
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

        Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(result.getArg(1).toLowerCase()));
        if (enchantment == null) {
            plugin.getMessage(Lang.ERROR_ENCHANTMENT_INVALID).send(sender);
            return;
        }

        int level = Math.max(0, result.getInt(2, 1));
        if (level == 0) {
            meta.removeEnchant(enchantment);
            plugin.getMessage(Lang.COMMAND_ITEM_ENCHANT_DONE_DISENCHANTED)
                .replace(Placeholders.GENERIC_ITEM, ItemUtil.getItemName(item))
                .replace(Placeholders.GENERIC_NAME, LangManager.getEnchantment(enchantment))
                .send(sender);
        }
        else {
            meta.addEnchant(enchantment, level, true);
            plugin.getMessage(Lang.COMMAND_ITEM_ENCHANT_DONE_ENCHANTED)
                .replace(Placeholders.GENERIC_ITEM, ItemUtil.getItemName(item))
                .replace(Placeholders.GENERIC_NAME, LangManager.getEnchantment(enchantment))
                .replace(Placeholders.GENERIC_LEVEL, NumberUtil.toRoman(level))
                .send(sender);
        }
        item.setItemMeta(meta);
    }
}
