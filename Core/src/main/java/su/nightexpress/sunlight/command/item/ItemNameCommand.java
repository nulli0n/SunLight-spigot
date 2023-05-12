package su.nightexpress.sunlight.command.item;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.Colorizer;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemNameCommand extends AbstractCommand<SunLight> {

    public static final String NAME = "name";

    public ItemNameCommand(@NotNull SunLight plugin) {
        super(plugin, new String[] {NAME}, Perms.COMMAND_ITEM_NAME);
        this.setDescription(plugin.getMessage(Lang.COMMAND_ITEM_NAME_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_ITEM_NAME_USAGE));
        this.setPlayerOnly(true);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        if (item.getType().isAir() || meta == null) {
            plugin.getMessage(Lang.ERROR_ITEM_INVALID).send(sender);
            return;
        }

        if (result.length() < 2) {
            meta.setDisplayName(null);
            plugin.getMessage(Lang.COMMAND_ITEM_NAME_DONE_RESET).send(sender);
        }
        else {
            String name = Colorizer.apply(Stream.of(result.getArgs()).skip(1).collect(Collectors.joining(" ")));
            meta.setDisplayName(name);
            plugin.getMessage(Lang.COMMAND_ITEM_NAME_DONE_CHANGED).send(sender);
        }
        item.setItemMeta(meta);
    }
}
