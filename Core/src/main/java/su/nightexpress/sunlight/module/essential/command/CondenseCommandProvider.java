package su.nightexpress.sunlight.module.essential.command;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.module.essential.EssentialModule;
import su.nightexpress.sunlight.module.essential.EssentialPerms;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.*;

public class CondenseCommandProvider extends AbstractCommandProvider {

    private static final Permission PERMISSION  = EssentialPerms.COMMAND.permission("condense");
    private static final TextLocale DESCRIPTION = LangEntry.builder("Command.Condense.Desc").text("Condense items into blocks.");

    private static final MessageLocale MESSAGE_NOTHING = LangEntry.builder("Command.Condense.Error.Nothing").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("Nothing to condense.")
    );

    private static final MessageLocale MESSAGE_NOT_ENOUGH = LangEntry.builder("Command.Condense.Error.NotEnough").chatMessage(
        GRAY.wrap("Not enough items to convert " + RED.wrap(GENERIC_SOURCE) + " to " + RED.wrap(GENERIC_RESULT) + ". Need at least " + RED.wrap(GENERIC_AMOUNT) + ".")
    );

    private static final MessageLocale MESSAGE_DONE = LangEntry.builder("Command.Condense.Done").chatMessage(
        GRAY.wrap("Converted " + SOFT_YELLOW.wrap("x" + GENERIC_TOTAL + " " + GENERIC_SOURCE) + " to " + SOFT_YELLOW.wrap("x" + GENERIC_AMOUNT + " " + GENERIC_RESULT) + ".")
    );

    private final EssentialModule module;

    public CondenseCommandProvider(@NotNull SunLightPlugin plugin, @NotNull EssentialModule module) {
        super(plugin);
        this.module = module;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral("condense", true, new String[]{"condense"}, builder -> builder
            .description(DESCRIPTION)
            .permission(PERMISSION)
            .playerOnly()
            .executes(this::execute)
        );
    }

    private boolean execute(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();

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
                int finalAmountPerCraft = amountPerCraft;
                ItemStack finalRecipeResult = recipeResult;
                this.module.sendPrefixed(MESSAGE_NOT_ENOUGH, context.getSender(), builder -> builder
                    .with(SLPlaceholders.GENERIC_AMOUNT, () -> String.valueOf(finalAmountPerCraft))
                    .with(SLPlaceholders.GENERIC_SOURCE, () -> ItemUtil.getItemName(userItem))
                    .with(SLPlaceholders.GENERIC_RESULT, () -> ItemUtil.getItemName(finalRecipeResult))
                );
                continue;
            }

            for (int craft = 0; craft < amountCraftCan; craft++) {
                Players.takeItem(player, userItem, amountPerCraft);
                Players.addItem(player, recipeResult);
            }

            ItemStack finalRecipeResult1 = recipeResult;
            int finalAmountPerCraft1 = amountPerCraft;
            this.module.sendPrefixed(MESSAGE_DONE, context.getSender(), builder -> builder
                .with(SLPlaceholders.GENERIC_SOURCE, () -> ItemUtil.getItemName(userItem))
                .with(SLPlaceholders.GENERIC_RESULT, () -> ItemUtil.getItemName(finalRecipeResult1))
                .with(SLPlaceholders.GENERIC_TOTAL, () -> String.valueOf(amountCraftCan * finalAmountPerCraft1))
                .with(SLPlaceholders.GENERIC_AMOUNT, () -> String.valueOf(amountCraftMin * amountCraftCan))
            );
            done = true;
        }

        if (!done) {
            this.module.sendPrefixed(MESSAGE_NOTHING, context.getSender());
        }

        return true;
    }
}
