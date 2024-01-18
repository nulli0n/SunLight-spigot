package su.nightexpress.sunlight.command.item;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;

import java.util.Arrays;
import java.util.List;

public class ItemAmountCommand extends AbstractCommand<SunLight> {

    public static final String NAME = "amount";

    private final int amountDefault;

    public ItemAmountCommand(@NotNull SunLight plugin, @NotNull JYML cfg) {
        super(plugin, new String[] {NAME}, Perms.COMMAND_ITEM_AMOUNT);
        this.setDescription(plugin.getMessage(Lang.COMMAND_ITEM_AMOUNT_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_ITEM_AMOUNT_USAGE));
        this.setPlayerOnly(true);

        this.amountDefault = JOption.create("Item.Amount.Default_Value", 64,
            "Sets default amount value if not specified in command.").read(cfg);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return Arrays.asList("1", "8", "16", "32", "64");
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            plugin.getMessage(Lang.COMMAND_ITEM_ERROR_EMPTY_HAND).send(sender);
            return;
        }

        int amount = result.getInt(1, this.amountDefault);
        item.setAmount(amount);

        plugin.getMessage(Lang.COMMAND_ITEM_AMOUNT_DONE)
            .replace(Placeholders.GENERIC_AMOUNT, amount)
            .replace(Placeholders.GENERIC_ITEM, ItemUtil.getItemName(item))
            .send(sender);
    }
}
