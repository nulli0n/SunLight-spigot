package su.nightexpress.sunlight.module.chat.util;

import org.bukkit.block.Container;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Colorizer;
import su.nightexpress.nightcore.util.ItemNbt;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.sunlight.module.chat.config.ChatConfig;

public class ChatUtils {

    @NotNull
    public static String cleanMessage(@NotNull String message) {
        return Colorizer.restrip(NightMessage.stripTags(message));
    }

    @NotNull
    public static ItemStack getLiteCopy(@NotNull ItemStack origin) {
        ItemStack copy = new ItemStack(origin);

        ItemMeta meta = copy.getItemMeta();
        if (!(meta instanceof BlockStateMeta blockStateMeta)) return copy;

        if (blockStateMeta.getBlockState() instanceof Container container) {
            container.getInventory().clear();
            blockStateMeta.setBlockState(container);
        }
        copy.setItemMeta(blockStateMeta);
        return copy;
    }

    @NotNull
    public static String doAntiCaps(@NotNull String message) {
        String rawMessage = cleanMessage(message);

        // Ignore player names in case if they are fully caps
        for (String name : Players.playerNames()) {
            rawMessage = rawMessage.replace(name, "");
        }
        for (String ignored : ChatConfig.ANTI_CAPS_IGNORED_WORDS.get()) {
            rawMessage = rawMessage.replace(ignored, "");
        }

        // Then check if message has enought length to check
        if (rawMessage.length() < ChatConfig.ANTI_CAPS_MESSAGE_LENGTH_MIN.get()) return message;

        double uppers = 0;
        double length = 0;
        for (char letter : rawMessage.toCharArray()) {
            if (!Character.isLetter(letter) || Character.isWhitespace(letter)) continue;
            if (Character.isUpperCase(letter)) uppers++;
            length++;
        }
        double percent = uppers / length * 100D;
        return percent >= ChatConfig.ANTI_CAPS_UPPER_LETTER_PERCENT_MIN.get() ? message.toLowerCase() : message;
    }


    public static boolean isAntiSpamEnabled() {
        return ChatConfig.ANTI_SPAM_ENABLED.get() && ChatConfig.ANTI_SPAM_BLOCK_SIMILAR_PERCENT.get() > 0D;
    }

    public static boolean isAntiCapsEnabled() {
        return ChatConfig.ANTI_CAPS_ENABLED.get() & ChatConfig.ANTI_CAPS_UPPER_LETTER_PERCENT_MIN.get() > 0D;
    }

    public static double getSimilarityPercent(@NotNull String first, @NotNull String second) {
        String longer = first, shorter = second;
        if (first.length() < second.length()) {
            longer = second;
            shorter = first;
        }

        int longerLength = longer.length();
        if (longerLength == 0) return 1D;

        return (longerLength - compareSimilarity(longer, shorter)) / (double) longerLength;
    }

    private static int compareSimilarity(@NotNull String first, @NotNull String second) {
        first = first.toLowerCase();
        second = second.toLowerCase();

        int[] costs = new int[second.length() + 1];
        for (int i = 0; i <= first.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= second.length(); j++) {
                if (i == 0) costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (first.charAt(i - 1) != second.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0) costs[second.length()] = lastValue;
        }
        return costs[second.length()];
    }


    public static boolean containsItemLink(@NotNull String string) {
        if (!ChatConfig.ITEM_SHOW_ENABLED.get()) return false;

        return string.contains(ChatConfig.ITEM_SHOW_PLACEHOLDER.get());
    }

    @NotNull
    public static String appendItemComponent(@NotNull String string, @NotNull ItemStack origin) {
        if (origin.getType().isAir()) return string;

        ItemStack item = ChatUtils.getLiteCopy(origin);
        String itemFormat = ChatConfig.ITEM_SHOW_FORMAT.get()
            .replace(Placeholders.ITEM_NAME, ItemUtil.getItemName(item))
            .replace(Placeholders.ITEM_VALUE, String.valueOf(ItemNbt.compress(item)));

        return string.replace(ChatConfig.ITEM_SHOW_PLACEHOLDER.get(), itemFormat);
    }
}
