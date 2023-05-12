package su.nightexpress.sunlight.command.item;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;

import java.util.Arrays;
import java.util.List;

public class ItemPotionCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "potion";

    public ItemPotionCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_ITEM_POTION);
        this.setUsage(plugin.getMessage(Lang.COMMAND_ITEM_POTION_ADD_USAGE));
        this.setDescription(plugin.getMessage(Lang.COMMAND_ITEM_POTION_ADD_DESC));
        this.setPlayerOnly(true);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return Arrays.stream(PotionEffectType.values()).map(PotionEffectType::getName).toList();
        }
        if (arg == 2) {
            return Arrays.asList("<amplifier>", "0", "1", "5", "10", "127");
        }
        if (arg == 3) {
            return Arrays.asList("<duration>", "60", "300", "600", "3600");
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 4) {
            this.printUsage(sender);
            return;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            this.errorItem(sender);
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof PotionMeta potionMeta)) {
            plugin.getMessage(Lang.COMMAND_ITEM_POTION_ERROR_NOT_A_POTION).send(player);
            return;
        }

        PotionEffectType pType = PotionEffectType.getByName(result.getArg(1).toUpperCase());
        if (pType == null) {
            plugin.getMessage(Lang.COMMAND_ITEM_POTION_ERROR_INVALID_EFFECT).send(player);
            return;
        }

        int amplifier = result.getInt(2, -1);
        if (amplifier < 0) {
            potionMeta.removeCustomEffect(pType);
        }
        else {
            int duration = result.getInt(3, 0);
            if (duration <= 0) return;

            PotionEffect pEffect = new PotionEffect(pType, duration, amplifier);
            potionMeta.addCustomEffect(pEffect, true);
        }

        plugin.getMessage(Lang.COMMAND_ITEM_POTION_ADD_DONE).send(player);
        item.setItemMeta(meta);
    }
}
