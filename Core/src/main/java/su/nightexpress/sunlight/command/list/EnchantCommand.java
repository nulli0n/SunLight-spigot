package su.nightexpress.sunlight.command.list;

import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.NumberUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.TargetCommand;
import su.nightexpress.sunlight.config.Lang;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class EnchantCommand extends TargetCommand {

    public static final String NAME = "enchant";

    public EnchantCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_ENCHANT, Perms.COMMAND_ENCHANT_OTHERS, 3);
        this.setDescription(plugin.getMessage(Lang.COMMAND_ENCHANT_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_ENCHANT_USAGE));
        this.addFlag(CommandFlags.SILENT);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return CollectionsUtil.getEnumsList(EquipmentSlot.class);
        }
        if (arg == 2) {
            return Stream.of(Enchantment.values()).map(e -> e.getKey().getKey()).toList();
        }
        if (arg == 3) {
            return Arrays.asList("0", "1", "5", "10", "127");
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 3) {
            this.printUsage(sender);
            return;
        }

        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        EquipmentSlot slot = StringUtil.getEnum(result.getArg(0), EquipmentSlot.class).orElse(EquipmentSlot.HAND);
        ItemStack item = target.getInventory().getItem(slot);
        if (item == null || item.getType().isAir() || item.getItemMeta() == null) {
            plugin.getMessage(Lang.ERROR_PLAYER_NO_ITEM).send(sender);
            return;
        }

        Enchantment enchant = Enchantment.getByKey(NamespacedKey.minecraft(result.getArg(1).toLowerCase()));
        if (enchant == null) {
            plugin.getMessage(Lang.ERROR_ENCHANTMENT_INVALID).send(sender);
            return;
        }

        int level = result.getInt(2, 1);

        ItemMeta meta1 = item.getItemMeta();
        if (meta1 instanceof EnchantmentStorageMeta meta) {
            if (level > 0) {
                meta.addStoredEnchant(enchant, level, true);
            }
            else {
                meta.removeStoredEnchant(enchant);
            }
            item.setItemMeta(meta);
        }
        else {
            if (level > 0) {
                meta1.addEnchant(enchant, level, true);
            }
            else {
                meta1.removeEnchant(enchant);
            }
            item.setItemMeta(meta1);
        }

        if (target != sender) {
            plugin.getMessage(level > 0 ? Lang.COMMAND_ENCHANT_ENCHANTED_TARGET : Lang.COMMAND_ENCHANT_DISENCHANTED_TARGET)
                .replace(Placeholders.Player.replacer(target))
                .replace(Placeholders.GENERIC_TYPE, plugin.getLangManager().getEnum(slot))
                .replace(Placeholders.GENERIC_ITEM, ItemUtil.getItemName(item))
                .replace(Placeholders.GENERIC_NAME, LangManager.getEnchantment(enchant))
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.toRoman(level))
                .send(sender);
        }

        if (!result.hasFlag(CommandFlags.SILENT)) {
            plugin.getMessage(level > 0 ? Lang.COMMAND_ENCHANT_ENCHANTED_NOTIFY : Lang.COMMAND_ENCHANT_DISENCHANTED_NOTIFY)
                .replace(Placeholders.GENERIC_TYPE, plugin.getLangManager().getEnum(slot))
                .replace(Placeholders.GENERIC_ITEM, ItemUtil.getItemName(item))
                .replace(Placeholders.GENERIC_NAME, LangManager.getEnchantment(enchant))
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.toRoman(level))
                .send(target);
        }
    }
}
