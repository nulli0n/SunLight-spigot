package su.nightexpress.sunlight.command.list;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.CommandPerms;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.command.CommandTools;
import su.nightexpress.sunlight.config.Lang;

public class EnchantCommand {

    public static final String NAME = "enchant";

    private static final String ARG_SLOT    = "slot";
    private static final String ARG_ENCHANT = "enchant";
    private static final String ARG_LEVEL   = "level";

    public static void load(@NotNull SunLightPlugin plugin) {
        CommandRegistry.registerDirectExecutor(NAME, (template, config) -> builder(plugin, template, config));
        CommandRegistry.addSimpleTemplate(NAME);
    }

    @NotNull
    public static DirectNodeBuilder builder(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_ENCHANT_DESC)
            .permission(CommandPerms.ENCHANT)
            .withArgument(CommandArguments.slot(ARG_SLOT).required())
            .withArgument(ArgumentTypes.enchantment(ARG_ENCHANT).required().withSamples(tabContext -> BukkitThing.getEnchantments().stream().map(BukkitThing::toString).toList()))
            .withArgument(ArgumentTypes.integerAbs(ARG_LEVEL).required().withSamples(tabContext -> Lists.newList("0", "1", "5", "10", "127")))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(CommandPerms.ENCHANT_OTHERS))
            .withFlag(CommandFlags.silent().permission(CommandPerms.ENCHANT_OTHERS))
            .executes((context, arguments) -> execute(plugin, context, arguments))
            ;
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        EquipmentSlot slot = arguments.getArgument(ARG_SLOT, EquipmentSlot.class);

        ItemStack item = target.getInventory().getItem(slot);
        if (item == null || item.getType().isAir() || item.getItemMeta() == null) {
            return context.sendFailure(Lang.COMMAND_ENCHANT_ERROR_NO_ITEM.getMessage());
        }

        Enchantment enchant = arguments.getEnchantmentArgument(ARG_ENCHANT);
        int level = arguments.getIntArgument(ARG_LEVEL);

        ItemMeta meta = item.getItemMeta();
        if (meta instanceof EnchantmentStorageMeta storageMeta) {
            if (level > 0) {
                storageMeta.addStoredEnchant(enchant, level, true);
            }
            else {
                storageMeta.removeStoredEnchant(enchant);
            }
            item.setItemMeta(storageMeta);
        }
        else {
            if (level > 0) {
                meta.addEnchant(enchant, level, true);
            }
            else {
                meta.removeEnchant(enchant);
            }
            item.setItemMeta(meta);
        }

        if (!target.isOnline()) target.saveData();

        if (target != context.getSender()) {
            (level > 0 ? Lang.COMMAND_ENCHANT_ENCHANTED_TARGET : Lang.COMMAND_ENCHANT_DISENCHANTED_TARGET).getMessage()
                .replace(Placeholders.forPlayer(target))
                .replace(Placeholders.GENERIC_TYPE, Lang.EQUIPMENT_SLOT.getLocalized(slot))
                .replace(Placeholders.GENERIC_ITEM, ItemUtil.getItemName(item))
                .replace(Placeholders.GENERIC_NAME, LangAssets.get(enchant))
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.toRoman(level))
                .send(context.getSender());
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            (level > 0 ? Lang.COMMAND_ENCHANT_ENCHANTED_NOTIFY : Lang.COMMAND_ENCHANT_DISENCHANTED_NOTIFY).getMessage()
                .replace(Placeholders.GENERIC_TYPE, Lang.EQUIPMENT_SLOT.getLocalized(slot))
                .replace(Placeholders.GENERIC_ITEM, ItemUtil.getItemName(item))
                .replace(Placeholders.GENERIC_NAME, LangAssets.get(enchant))
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.toRoman(level))
                .send(target);
        }

        return true;
    }
}
