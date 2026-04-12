package su.nightexpress.sunlight.module.chat.processor.global;

import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import su.nightexpress.nightcore.util.ItemTag;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Strings;
import su.nightexpress.nightcore.util.placeholder.PlaceholderContext;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.ChatPlaceholders;
import su.nightexpress.sunlight.module.chat.context.FormattedContext;
import su.nightexpress.sunlight.module.chat.core.ChatLang;
import su.nightexpress.sunlight.module.chat.processor.ChatProcessor;

public class ItemDisplayProcessor implements ChatProcessor<FormattedContext> {

    @Override
    public void preProcess(@NonNull ChatModule module, @NonNull FormattedContext context) {
        String format = context.getFormat();

        String placeholder = module.getSettings().getItemShowPlaceholder();
        int index = format.indexOf(placeholder);
        if (index == -1) return;

        ItemStack itemStack = context.getPlayer().getInventory().getItemInMainHand();
        if (itemStack.isEmpty()) {
            module.sendPrefixed(ChatLang.ITEM_SHOW_NOTHING, context.getPlayer());
            return;
        }

        //ItemStack lite = getLiteCopy(itemStack);

        PlaceholderContext itemContext = PlaceholderContext.builder()
            .with(ChatPlaceholders.ITEM_NAME, () -> ItemUtil.getNameSerialized(itemStack))
            .with(ChatPlaceholders.ITEM_VALUE, () -> Strings.toBase64(ItemTag.getTagString(itemStack)))
            .build();

        String itemFormat = itemContext.apply(module.getSettings().getItemShowFormat());

        context.setFormat(format.replace(placeholder, itemFormat));
    }

    @Override
    public void postProcess(@NonNull ChatModule module, @NonNull FormattedContext context) {

    }

    /*@NonNull
    private static ItemStack getLiteCopy(@NonNull ItemStack origin) {
        ItemStack copy = new ItemStack(origin);
    
        ItemMeta meta = copy.getItemMeta();
        if (!(meta instanceof BlockStateMeta blockStateMeta)) return copy;
    
        if (blockStateMeta.getBlockState() instanceof Container container) {
            container.getInventory().clear();
            blockStateMeta.setBlockState(container);
        }
    
        copy.setItemMeta(blockStateMeta);
        return copy;
    }*/
}
