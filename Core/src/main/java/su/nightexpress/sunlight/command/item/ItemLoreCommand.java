package su.nightexpress.sunlight.command.item;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandFlag;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nexmedia.engine.utils.Colorizer;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemLoreCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "lore";

    private static final CommandFlag<Integer> FLAG_POSITION = CommandFlag.intFlag("pos");

    public ItemLoreCommand(@NotNull SunLight plugin) {
        super(plugin, new String[] {NAME}, Perms.COMMAND_ITEM_LORE);
        this.setDescription(plugin.getMessage(Lang.COMMAND_ITEM_LORE_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_ITEM_LORE_USAGE));
        this.setPlayerOnly(true);
        this.addFlag(FLAG_POSITION);

        this.addDefaultCommand(new HelpSubCommand<>(plugin));
        this.addChildren(new AddCommand(plugin));
        this.addChildren(new RemoveCommand(plugin));
        this.addChildren(new ClearCommand(plugin));
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {

    }

    private static class AddCommand extends AbstractCommand<SunLight> {

        public AddCommand(@NotNull SunLight plugin) {
            super(plugin, new String[]{"add"});
            this.setDescription(plugin.getMessage(Lang.COMMAND_ITEM_LORE_ADD_DESC));
            this.setUsage(plugin.getMessage(Lang.COMMAND_ITEM_LORE_ADD_USAGE));
            this.setPlayerOnly(true);
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
                plugin.getMessage(Lang.ERROR_ITEM_INVALID).send(sender);
                return;
            }

            List<String> lore = meta.getLore();
            if (lore == null) lore = new ArrayList<>();

            int pos = result.getFlag(FLAG_POSITION, Integer.MIN_VALUE);
            String text = Colorizer.apply(Stream.of(result.getArgs()).skip(2).collect(Collectors.joining(" ")));
            if (pos >= 0 && pos < lore.size()) {
                lore.add(pos, text);
            }
            else {
                lore.add(text);
            }
            meta.setLore(lore);
            item.setItemMeta(meta);

            plugin.getMessage(Lang.COMMAND_ITEM_LORE_ADD_DONE).send(sender);
        }
    }

    private static class RemoveCommand extends AbstractCommand<SunLight> {

        public RemoveCommand(@NotNull SunLight plugin) {
            super(plugin, new String[]{"remove"});
            this.setDescription(plugin.getMessage(Lang.COMMAND_ITEM_LORE_REMOVE_DESC));
            this.setUsage(plugin.getMessage(Lang.COMMAND_ITEM_LORE_REMOVE_USAGE));
            this.setPlayerOnly(true);
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
                plugin.getMessage(Lang.ERROR_ITEM_INVALID).send(sender);
                return;
            }

            int pos = result.getInt(2, 0) - 1;
            List<String> lore = meta.getLore();
            if (lore != null) {
                if (pos < 0 || pos >= lore.size()) {
                    lore.remove(lore.size() - 1);
                }
                else {
                    lore.remove(pos - 1);
                }
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            plugin.getMessage(Lang.COMMAND_ITEM_LORE_REMOVE_DONE).send(sender);
        }
    }

    private static class ClearCommand extends AbstractCommand<SunLight> {

        public ClearCommand(@NotNull SunLight plugin) {
            super(plugin, new String[]{"clear"});
            this.setDescription(plugin.getMessage(Lang.COMMAND_ITEM_LORE_CLEAR_DESC));
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

            meta.setLore(new ArrayList<>());
            item.setItemMeta(meta);

            plugin.getMessage(Lang.COMMAND_ITEM_LORE_CLEAR_DONE).send(sender);
        }
    }
}
