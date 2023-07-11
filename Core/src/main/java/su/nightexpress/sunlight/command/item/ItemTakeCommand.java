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
import su.nexmedia.engine.utils.PlayerUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.api.TargetCommand;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.utils.SunUtils;

import java.util.*;
import java.util.function.Predicate;

public class ItemTakeCommand extends TargetCommand {

    public static final String NAME = "take";

    public ItemTakeCommand(@NotNull SunLight plugin) {
        super(plugin, new String[] {NAME}, Perms.COMMAND_ITEM_TAKE, Perms.COMMAND_ITEM_TAKE, 1);
        this.setAllowDataLoad();
        this.setDescription(plugin.getMessage(Lang.COMMAND_ITEM_TAKE_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_ITEM_TAKE_USAGE));

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

        String flagName = result.getFlag(ItemCommand.FLAG_NAME);
        String flagLore = result.getFlag(ItemCommand.FLAG_LORE);
        String flagEnchants = result.getFlag(ItemCommand.FLAG_ENCHANTS);

        List<String> checkLore = flagLore == null ? new ArrayList<>() : ItemCommand.parseFlagLore(flagLore);
        Map<Enchantment, Integer> checkEnchants = flagEnchants == null ? new HashMap<>() : ItemCommand.parseFlagEnchants(flagEnchants);

        Predicate<ItemStack> predicate = (itemHas -> {
            if (itemHas == null || itemHas.getType() != material) return false;

            ItemMeta meta = itemHas.getItemMeta();
            if (flagName != null || flagLore != null || flagEnchants != null) {
                if (meta == null) return false;
                if (flagName != null && !meta.getDisplayName().contains(flagName)) return false;
                if (flagLore != null && !checkLore.isEmpty()) {
                    if (meta.getLore() == null) return false;
                    if (checkLore.size() == 1) {
                        if (meta.getLore().stream().noneMatch(lineLore -> lineLore.contains(flagLore))) return false;
                    }
                    else {
                        if (!meta.getLore().containsAll(checkLore)) return false;
                    }
                }
                if (flagEnchants != null && !meta.getEnchants().entrySet().containsAll(checkEnchants.entrySet())) return false;
            }

            return true;
        });

        boolean isTaken = PlayerUtil.takeItem(target, predicate, amount);
        if (isTaken) {
            plugin.getMessage(Lang.COMMAND_ITEM_TAKE_DONE)
                .replace(Placeholders.forPlayer(target))
                .replace(Placeholders.GENERIC_AMOUNT, amount)
                .replace(Placeholders.GENERIC_TYPE, plugin.getLangManager().getEnum(material))
                .send(sender);

            plugin.getMessage(Lang.COMMAND_ITEM_TAKE_NOTIFY)
                .replace(Placeholders.GENERIC_AMOUNT, amount)
                .replace(Placeholders.GENERIC_TYPE, plugin.getLangManager().getEnum(material))
                .send(target);
        }
        else {
            plugin.getMessage(Lang.COMMAND_ITEM_TAKE_ERROR_NOT_ENOUGH)
                .replace(Placeholders.GENERIC_TOTAL, amount)
                .replace(Placeholders.GENERIC_TYPE, plugin.getLangManager().getEnum(material))
                .replace(Placeholders.GENERIC_AMOUNT, PlayerUtil.countItem(target, predicate))
                .send(sender);
        }
    }
}
