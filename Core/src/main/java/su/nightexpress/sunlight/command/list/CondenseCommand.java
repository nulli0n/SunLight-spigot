package su.nightexpress.sunlight.command.list;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;

import java.util.*;

public class CondenseCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "condense";

    public static final String PLACEHOLDER_SOURCE = "%source%";
    public static final String PLACEHOLDER_RESULT = "%result%";
    public static final String PLACEHOLDER_TOTAL = "%total%";

    public CondenseCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_CONDENSE);
        this.setDescription(plugin.getMessage(Lang.COMMAND_CONDENSE_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_CONDENSE_USAGE));
        this.setPlayerOnly(true);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player player = (Player) sender;
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

            int amountUserHas = PlayerUtil.countItem(player, userItem);
            int amountCraftCan = (int) ((double) amountUserHas / (double) amountPerCraft);
            int amountCraftMin = recipeResult.getAmount();

            if (amountCraftCan < amountCraftMin) {
                plugin.getMessage(Lang.COMMAND_CONDENSE_ERROR_NOT_ENOUGH)
                    .replace(Placeholders.GENERIC_AMOUNT, String.valueOf(amountPerCraft))
                    .replace(PLACEHOLDER_SOURCE, ItemUtil.getItemName(userItem))
                    .replace(PLACEHOLDER_RESULT, ItemUtil.getItemName(recipeResult))
                    .send(sender);
                continue;
            }

            for (int craft = 0; craft < amountCraftCan; craft++) {
                PlayerUtil.takeItem(player, userItem, amountPerCraft);
                PlayerUtil.addItem(player, recipeResult);
            }

            plugin.getMessage(Lang.COMMAND_CONDENSE_DONE)
                .replace(PLACEHOLDER_SOURCE, ItemUtil.getItemName(userItem))
                .replace(PLACEHOLDER_RESULT, ItemUtil.getItemName(recipeResult))
                .replace(PLACEHOLDER_TOTAL, String.valueOf(amountCraftCan * amountPerCraft))
                .replace(Placeholders.GENERIC_AMOUNT, String.valueOf(amountCraftMin * amountCraftCan))
                .send(sender);
            done = true;
        }

        if (!done) {
            plugin.getMessage(Lang.COMMAND_CONDENSE_ERROR_NOTHING).send(sender);
        }
    }
}
