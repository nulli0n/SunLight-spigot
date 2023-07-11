package su.nightexpress.sunlight.command.item;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.generator.WorldInfo;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.NumberUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.utils.SunUtils;

import java.util.*;

public class ItemSpawnCommand extends AbstractCommand<SunLight> {

    public static final String NAME = "spawn";

    public ItemSpawnCommand(@NotNull SunLight plugin) {
        super(plugin, new String[] {NAME}, Perms.COMMAND_ITEM_SPAWN);
        this.setDescription(plugin.getMessage(Lang.COMMAND_ITEM_SPAWN_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_ITEM_SPAWN_USAGE));

        this.addFlag(ItemCommand.FLAG_NAME, ItemCommand.FLAG_LORE, ItemCommand.FLAG_ENCHANTS);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return SunUtils.ITEM_TYPES;
        }
        if (arg == 2) {
            return Arrays.asList("1", "8", "16", "32", "64");
        }
        if (arg == 3) {
            return plugin.getServer().getWorlds().stream().map(WorldInfo::getName).toList();
        }
        if (arg == 4) {
            return Arrays.asList("<x>", NumberUtil.format(player.getLocation().getX()));
        }
        if (arg == 5) {
            return Arrays.asList("<y>", NumberUtil.format(player.getLocation().getY()));
        }
        if (arg == 6) {
            return Arrays.asList("<z>", NumberUtil.format(player.getLocation().getZ()));
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 3 || (result.length() < 7 && !(sender instanceof Player))) {
            this.printUsage(sender);
            return;
        }

        Material material = Material.getMaterial(result.getArg(1).toUpperCase());
        if (material == null) {
            plugin.getMessage(Lang.COMMAND_ITEM_ERROR_MATERIAL).replace(Placeholders.GENERIC_TYPE, result.getArg(1)).send(sender);
            return;
        }

        int amount = result.getInt(2, 1);

        World world;
        Location location;
        if (result.length() >= 7) {
            world = plugin.getServer().getWorld(result.getArg(3));
            if (world == null) {
                plugin.getMessage(Lang.ERROR_WORLD_INVALID).send(sender);
                return;
            }

            double x = result.getDouble(4, 0);
            double y = result.getDouble(5, 0);
            double z = result.getDouble(6, 0);
            location = new Location(world, x, y, z);
        }
        else {
            Player player = (Player) sender;
            world = player.getWorld();
            location = player.getTargetBlock(null, 100).getRelative(BlockFace.UP).getLocation();
        }

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
        world.dropItemNaturally(location, item);

        plugin.getMessage(Lang.COMMAND_ITEM_SPAWN_DONE)
            .replace(Placeholders.GENERIC_AMOUNT, amount)
            .replace(Placeholders.GENERIC_TYPE, ItemUtil.getItemName(item))
            .replace(Placeholders.forLocation(location))
            .send(sender);
    }
}
