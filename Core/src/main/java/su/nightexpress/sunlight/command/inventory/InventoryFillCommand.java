package su.nightexpress.sunlight.command.inventory;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.NumberUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.utils.SunUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InventoryFillCommand extends AbstractCommand<SunLight> {

    public static final String NAME = "fill";

    public InventoryFillCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_INVENTORY_FILL);
        this.setDescription(plugin.getMessage(Lang.COMMAND_INVENTORY_FILL_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_INVENTORY_FILL_USAGE));
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return CollectionsUtil.playerNames(player);
        }
        if (arg >= 2) {
            return SunUtils.ITEM_TYPES;
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 3) {
            this.printUsage(sender);
            return;
        }

        Player target = plugin.getServer().getPlayer(result.getArg(1));
        SunUser user = target == null ? plugin.getUserManager().getUserData(result.getArg(1)) : null;
        if (target == null && user == null) {
            this.errorPlayer(sender);
            return;
        }

        List<Material> materials = Stream.of(result.getArgs()).skip(2).limit(36)
            .map(String::toUpperCase)
            .map(Material::getMaterial).filter(Objects::nonNull).toList();
        if (materials.isEmpty()) {
            plugin.getMessage(Lang.ERROR_MATERIAL_INVALID).send(sender);
            return;
        }

        int invSize = 36;
        int matSize = materials.size();
        int[] slotsPerItem = NumberUtil.splitIntoParts(invSize, matSize);
        int indexSlot = 0;
        List<ItemStack> content = new ArrayList<>();

        for (int indexItem = 0; indexItem < matSize; indexItem++) {
            int slotsAmount = slotsPerItem[indexItem];
            ItemStack item = new ItemStack(materials.get(indexItem));
            for (int slot = 0; slot < slotsAmount; slot++) {
                content.add(item);
            }
        }

        Collections.shuffle(content);

        if (target == null) {
            target = plugin.getSunNMS().loadPlayerData(user.getId(), user.getName());
        }

        Inventory inventory = plugin.getSunNMS().getPlayerInventory(target);
        ItemStack[] armor = target.getInventory().getArmorContents();
        inventory.setContents(content.toArray(new ItemStack[0]));
        target.getInventory().setArmorContents(armor);
        target.saveData();

        String itemName = materials.stream().map(LangManager::getMaterial).collect(Collectors.joining(", "));

        plugin.getMessage(Lang.COMMAND_INVENTORY_FILL_DONE)
            .replace(Placeholders.forPlayer(target))
            .replace(Placeholders.GENERIC_ITEM, itemName)
            .send(sender);
    }
}
