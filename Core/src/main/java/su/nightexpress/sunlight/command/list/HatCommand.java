package su.nightexpress.sunlight.command.list;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.sunlight.command.CommandPerms;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.config.Lang;

public class HatCommand {

    public static final String NODE = "hat";

    public static void load(@NotNull SunLightPlugin plugin) {
        CommandRegistry.registerDirectExecutor(NODE, (template, config) -> builder(plugin, template, config));
        CommandRegistry.addSimpleTemplate(NODE);
    }

    @NotNull
    public static DirectNodeBuilder builder(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(Lang.COMMAND_HAT_DESC)
            .permission(CommandPerms.HAT)
            .executes((context, arguments) -> execute(plugin, context, arguments))
            ;
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            context.send(Lang.ERROR_EMPTY_HAND.getMessage());
            return false;
        }

        EquipmentSlot slot = EquipmentSlot.HEAD;
        ItemStack oldItem = player.getInventory().getItem(slot);
        player.getInventory().setItemInMainHand(null);
        player.getInventory().setItem(slot, item);

        if (oldItem != null && !oldItem.getType().isAir()) {
            Players.addItem(player, oldItem);
        }

        Lang.COMMAND_HAT_DONE.getMessage().replace(Placeholders.GENERIC_ITEM, ItemUtil.getItemName(item)).send(context.getSender());
        return true;
    }
}
