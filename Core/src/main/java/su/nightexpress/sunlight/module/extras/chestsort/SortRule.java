package su.nightexpress.sunlight.module.extras.chestsort;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Colorizer;
import su.nightexpress.nightcore.util.ItemUtil;

import java.util.function.Function;

public enum SortRule {

    IS_BLOCK(item -> {
        Material material = item.getType();
        return material.isBlock() && material.isSolid() ? "A" : "B";
    }),
    IS_ITEM(item -> {
        Material material = item.getType();
        return !material.isBlock() || !material.isSolid() ? "A" : "B";
    }),
    MATERIAL(item -> item.getType().name()),
    AMOUNT(item -> String.valueOf(item.getMaxStackSize() - item.getAmount())),
    NAME(item -> Colorizer.strip(ItemUtil.getItemName(item)))
    ;

    private final Function<ItemStack, String> function;

    SortRule(@NotNull Function<ItemStack, String> function) {
        this.function = function;
    }

    @NotNull
    public String getRule(@NotNull ItemStack item) {
        return this.function.apply(item);
    }
}
