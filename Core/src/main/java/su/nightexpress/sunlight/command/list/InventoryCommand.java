package su.nightexpress.sunlight.command.list;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.*;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.utils.SunUtils;

public class InventoryCommand {

    public static final String NODE_CLEAR  = "inventory_clear";
    public static final String NODE_COPY   = "inventory_copy";
    public static final String NODE_FILL   = "inventory_fill";
    public static final String NODE_OPEN   = "inventory_open";
    public static final String NODE_REPAIR = "inventory_repair";

    public static void load(@NotNull SunLightPlugin plugin) {
        CommandRegistry.registerDirectExecutor(NODE_CLEAR, (template, config) -> builderClear(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_COPY, (template, config) -> builderCopy(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_FILL, (template, config) -> builderFill(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_OPEN, (template, config) -> builderOpen(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_REPAIR, (template, config) -> builderRepair(plugin, template, config));

        CommandRegistry.addTemplate("inventory", CommandTemplate.group(new String[]{"inventory", "inv"},
            "Inventory commands.",
            CommandPerms.PREFIX + "inventory",
            CommandTemplate.direct(new String[]{"clear"}, NODE_CLEAR),
            CommandTemplate.direct(new String[]{"copy"}, NODE_COPY),
            CommandTemplate.direct(new String[]{"fill"}, NODE_FILL),
            CommandTemplate.direct(new String[]{"open"}, NODE_OPEN),
            CommandTemplate.direct(new String[]{"repair"}, NODE_REPAIR)
        ));

        CommandRegistry.addTemplate("clearinv", CommandTemplate.direct(new String[]{"clearinv"}, NODE_CLEAR));
        CommandRegistry.addTemplate("invsee", CommandTemplate.direct(new String[]{"invsee"}, NODE_OPEN));
        CommandRegistry.addTemplate("repairall", CommandTemplate.direct(new String[]{"repairall", "fixall"}, NODE_REPAIR));
    }

    @NotNull
    public static DirectNodeBuilder builderClear(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_INVENTORY_CLEAR_DESC)
            .permission(CommandPerms.INVENTORY_CLEAR)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(CommandPerms.INVENTORY_CLEAR_OTHERS))
            .withFlag(CommandFlags.silent().permission(CommandPerms.INVENTORY_CLEAR_OTHERS))
            .executes((context, arguments) -> executeClear(plugin, context, arguments))
            ;
    }

    public static boolean executeClear(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        plugin.getSunNMS().getPlayerInventory(target).clear();
        if (!target.isOnline()) target.saveData();

        if (context.getSender() != target) {
            context.send(Lang.COMMAND_INVENTORY_CLEAR_DONE_TARGET.getMessage()
                .replace(Placeholders.forPlayer(target))
            );
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            Lang.COMMAND_INVENTORY_CLEAR_DONE_NOTIFY.getMessage().send(target);
        }

        return true;
    }

    @NotNull
    public static DirectNodeBuilder builderCopy(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_INVENTORY_COPY_DESC)
            .permission(CommandPerms.INVENTORY_COPY)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .withArgument(ArgumentTypes.playerName(CommandArguments.TARGET).permission(CommandPerms.INVENTORY_COPY_OTHERS))
            .executes((context, arguments) -> executeCopy(plugin, context, arguments))
            ;
    }

    public static boolean executeCopy(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player source = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (source == null) return false;

        Player target = context.getExecutor();
        if (arguments.hasArgument(CommandArguments.TARGET)) {
            target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.TARGET, true);
            if (target == null) return false;
        }
        else if (target == null) {
            context.errorPlayerOnly();
            return false;
        }

        if (source == context.getSender() && target == context.getSender()) {
            return context.sendFailure(Lang.ERROR_COMMAND_NOT_YOURSELF.getMessage());
        }

        Inventory invFrom = plugin.getSunNMS().getPlayerInventory(source);
        Inventory invTo = plugin.getSunNMS().getPlayerInventory(target);
        for (int slot = 0; slot < invTo.getSize(); slot++) {
            invTo.setItem(slot, invFrom.getItem(slot));
        }
        if (!target.isOnline()) target.saveData();

        if (context.getSender() == target) {
            context.send(Lang.COMMAND_INVENTORY_COPY_DONE_INFO.getMessage()
                .replace(Placeholders.forPlayer(source))
            );
        }
        else {
            context.send(Lang.COMMAND_INVENTORY_COPY_DONE_OTHERS.getMessage()
                .replace(Placeholders.GENERIC_SOURCE, source.getName())
                .replace(Placeholders.GENERIC_TARGET, target.getName())
            );
        }

        return true;
    }

    @NotNull
    public static DirectNodeBuilder builderFill(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_INVENTORY_FILL_DESC)
            .permission(CommandPerms.INVENTORY_FILL)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .withArgument(CommandArguments.material(CommandArguments.ITEM, Material::isItem).required())
            .executes((context, arguments) -> executeFill(plugin, context, arguments))
            ;
    }

    public static boolean executeFill(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        Material material = arguments.getMaterialArgument(CommandArguments.ITEM);
        Inventory inventory = plugin.getSunNMS().getPlayerInventory(target);

        for (int slot = 0; slot < 36; slot++) {
            ItemStack has = inventory.getItem(slot);
            if (has == null || has.getType().isAir()) {
                inventory.setItem(slot, new ItemStack(material));
            }
        }

        //ItemStack[] armor = target.getInventory().getArmorContents();
        //target.getInventory().setArmorContents(armor);
        if (!target.isOnline()) target.saveData();

        context.send(Lang.COMMAND_INVENTORY_FILL_DONE.getMessage()
            .replace(Placeholders.forPlayer(target))
            .replace(Placeholders.GENERIC_ITEM, LangAssets.get(material))
        );

        return true;
    }

    @NotNull
    public static DirectNodeBuilder builderOpen(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(Lang.COMMAND_INVENTORY_OPEN_DESC)
            .permission(CommandPerms.INVENTORY_OPEN)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .executes((context, arguments) -> executeOpen(plugin, context, arguments))
            ;
    }

    public static boolean executeOpen(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getExecutor();
        if (player == null) return false;

        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        if (target == context.getSender()) {
            return context.sendFailure(Lang.ERROR_COMMAND_NOT_YOURSELF.getMessage());
        }

        plugin.getSunNMS().openPlayerInventory(player, target);

        return context.sendSuccess(Lang.COMMAND_INVENTORY_OPEN_DONE.getMessage().replace(Placeholders.forPlayer(target)));
    }

    @NotNull
    public static DirectNodeBuilder builderRepair(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_INVENTORY_REPAIR_DESC)
            .permission(CommandPerms.INVENTORY_REPAIR)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(CommandPerms.INVENTORY_REPAIR_OTHERS))
            .withFlag(CommandFlags.silent().permission(CommandPerms.INVENTORY_REPAIR_OTHERS))
            .executes((context, arguments) -> executeRepair(plugin, context, arguments))
            ;
    }

    public static boolean executeRepair(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        plugin.getSunNMS().getPlayerInventory(target).forEach(SunUtils::repairItem);
        if (!target.isOnline()) target.saveData();

        if (context.getSender() != target) {
            context.send(Lang.COMMAND_INVENTORY_REPAIR_TARGET.getMessage().replace(Placeholders.forPlayer(target)));
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            Lang.COMMAND_INVENTORY_REPAIR_NOTIFY.getMessage().send(target);
        }

        return true;
    }
}
