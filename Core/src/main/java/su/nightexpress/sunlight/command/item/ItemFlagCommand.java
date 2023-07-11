package su.nightexpress.sunlight.command.item;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;

import java.util.List;
import java.util.stream.Stream;

public class ItemFlagCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "flag";

    public ItemFlagCommand(@NotNull SunLight plugin) {
        super(plugin, new String[] {NAME}, Perms.COMMAND_ITEM_LORE);
        this.setDescription(plugin.getMessage(Lang.COMMAND_ITEM_FLAG_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_ITEM_FLAG_USAGE));
        this.setPlayerOnly(true);

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
            this.setDescription(plugin.getMessage(Lang.COMMAND_ITEM_FLAG_ADD_DESC));
            this.setUsage(plugin.getMessage(Lang.COMMAND_ITEM_FLAG_ADD_USAGE));
            this.setPlayerOnly(true);
        }

        @Override
        @NotNull
        public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
            if (arg == 1) {
                return Stream.of(ItemFlag.values()).map(Enum::name).toList();
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

            ItemFlag flag = StringUtil.getEnum(result.getArg(2), ItemFlag.class).orElse(null);
            if (flag == null) {
                this.printUsage(sender);
                return;
            }

            meta.addItemFlags(flag);
            item.setItemMeta(meta);

            plugin.getMessage(Lang.COMMAND_ITEM_FLAG_ADD_DONE)
                .replace(Placeholders.GENERIC_NAME, flag.name())
                .send(sender);
        }
    }

    private static class RemoveCommand extends AbstractCommand<SunLight> {

        public RemoveCommand(@NotNull SunLight plugin) {
            super(plugin, new String[]{"remove"});
            this.setDescription(plugin.getMessage(Lang.COMMAND_ITEM_FLAG_REMOVE_DESC));
            this.setUsage(plugin.getMessage(Lang.COMMAND_ITEM_FLAG_REMOVE_USAGE));
            this.setPlayerOnly(true);
        }

        @Override
        @NotNull
        public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
            if (arg == 1) {
                return Stream.of(ItemFlag.values()).map(Enum::name).toList();
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

            ItemFlag flag = StringUtil.getEnum(result.getArg(2), ItemFlag.class).orElse(null);
            if (flag == null) {
                this.printUsage(sender);
                return;
            }

            meta.removeItemFlags(flag);
            item.setItemMeta(meta);

            plugin.getMessage(Lang.COMMAND_ITEM_FLAG_REMOVE_DONE)
                .replace(Placeholders.GENERIC_NAME, flag.name())
                .send(sender);
        }
    }

    private static class ClearCommand extends AbstractCommand<SunLight> {

        public ClearCommand(@NotNull SunLight plugin) {
            super(plugin, new String[]{"clear"});
            this.setDescription(plugin.getMessage(Lang.COMMAND_ITEM_FLAG_CLEAR_DESC));
            this.setPlayerOnly(true);
        }

        @Override
        protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
            Player player = (Player) sender;
            ItemStack item = player.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            if (item.getType().isAir() || meta == null) {
                plugin.getMessage(Lang.COMMAND_ITEM_ERROR_EMPTY_HAND).send(sender);
                return;
            }

            meta.removeItemFlags(ItemFlag.values());
            item.setItemMeta(meta);

            plugin.getMessage(Lang.COMMAND_ITEM_FLAG_CLEAR_DONE).send(sender);
        }
    }
}
