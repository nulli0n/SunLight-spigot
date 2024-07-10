package su.nightexpress.sunlight.command.list;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandPerms;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.config.Lang;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class CondenseCommand {

    public static final String NAME = "condense";

    public static void load(@NotNull SunLightPlugin plugin) {
        CommandRegistry.registerDirectExecutor(NAME, (template, config) -> builderRoot(plugin, template, config));
        CommandRegistry.addSimpleTemplate(NAME);
    }

    public static DirectNodeBuilder builderRoot(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_CONDENSE_DESC)
            .permission(CommandPerms.CONDENSE)
            .playerOnly()
            .executes((context, arguments) -> execute(plugin, context, arguments))
            ;
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getExecutor();
        if (player == null) return false;

        boolean done = false;
        Set<Material> userItems = new HashSet<>();

        // Put materials to set to avoid duplicates and 'double' converts
        for (ItemStack userItem : player.getInventory().getContents()) {
            if (userItem == null || userItem.getType().isAir()) continue;
            userItems.add(userItem.getType());
        }

        for (Material userMaterial : userItems) {
            ItemStack userItem = new ItemStack(userMaterial);
            int amountPerCraft = 0;

            ItemStack recipeResult = null;

            Iterator<Recipe> iter = plugin.getServer().recipeIterator();

            Label_Recipe:
            while (iter.hasNext()) {
                Recipe recipe = iter.next();
                if (!(recipe instanceof ShapedRecipe shapedRecipe)) continue;

                Collection<ItemStack> recipeItems = shapedRecipe.getIngredientMap().values();

                // Only 'cuboid' crafts.
                String[] shape = shapedRecipe.getShape();
                if (shape.length < 2) continue;
                for (String line : shape) {
                    if (line.length() != shape.length) continue Label_Recipe;
                }

                // Check for same ingredients
                int amountPerRecipe = 0;
                for (ItemStack srcItem : recipeItems) {
                    if (srcItem == null || srcItem.getType().isAir()) continue;
                    if (!srcItem.isSimilar(userItem)) continue Label_Recipe;

                    amountPerRecipe += srcItem.getAmount();
                }

                // Get the greater recipe
                if (amountPerRecipe > amountPerCraft) {
                    amountPerCraft = amountPerRecipe;
                    recipeResult = recipe.getResult();
                }
            }

            // Check for valid recipe
            if (amountPerCraft <= 1/* || recipeResult == null*/) {
                continue;
            }

            int amountUserHas = Players.countItem(player, userItem);
            int amountCraftCan = (int) ((double) amountUserHas / (double) amountPerCraft);
            int amountCraftMin = recipeResult.getAmount();

            if (amountCraftCan < amountCraftMin) {
                Lang.COMMAND_CONDENSE_ERROR_NOT_ENOUGH.getMessage()
                    .replace(Placeholders.GENERIC_AMOUNT, String.valueOf(amountPerCraft))
                    .replace(Placeholders.GENERIC_SOURCE, ItemUtil.getItemName(userItem))
                    .replace(Placeholders.GENERIC_RESULT, ItemUtil.getItemName(recipeResult))
                    .send(context.getSender());
                continue;
            }

            for (int craft = 0; craft < amountCraftCan; craft++) {
                Players.takeItem(player, userItem, amountPerCraft);
                Players.addItem(player, recipeResult);
            }

            Lang.COMMAND_CONDENSE_DONE.getMessage()
                .replace(Placeholders.GENERIC_SOURCE, ItemUtil.getItemName(userItem))
                .replace(Placeholders.GENERIC_RESULT, ItemUtil.getItemName(recipeResult))
                .replace(Placeholders.GENERIC_TOTAL, String.valueOf(amountCraftCan * amountPerCraft))
                .replace(Placeholders.GENERIC_AMOUNT, String.valueOf(amountCraftMin * amountCraftCan))
                .send(context.getSender());
            done = true;
        }

        if (!done) {
            Lang.COMMAND_CONDENSE_ERROR_NOTHING.getMessage().send(context.getSender());
        }

        return true;
    }
}
