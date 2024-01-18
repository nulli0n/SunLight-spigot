package su.nightexpress.sunlight.module.chat.util;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.Pair;
import su.nightexpress.sunlight.SunLightAPI;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.data.impl.settings.DefaultSettings;
import su.nightexpress.sunlight.module.chat.ChatChannel;
import su.nightexpress.sunlight.module.chat.config.ChatConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChatUtils {

    private static final Map<String, Pair<String, Long>> LAST_MESSAGE = new HashMap<>();
    private static final Map<String, Pair<String, Long>> LAST_COMMAND = new HashMap<>();

    private static final Map<String, Map<String, Long>> NEXT_MESSAGE_IN = new HashMap<>();
    private static final Map<String, Long>              NEXT_COMMAND_IN = new HashMap<>();
    private static final Map<String, Map<String, Long>> NEXT_MENTION_IN = new HashMap<>();
    private static final String[] BANNED_CHARACTERS = {"<?", "?>", "</>"};

    public static void clear(@NotNull Player player) {
        String key = player.getName();
        LAST_MESSAGE.remove(key);
        LAST_COMMAND.remove(key);
        NEXT_MESSAGE_IN.getOrDefault(key, Collections.emptyMap()).values().removeIf(date -> date < System.currentTimeMillis());
        NEXT_COMMAND_IN.values().removeIf(date -> date < System.currentTimeMillis());
        NEXT_MENTION_IN.getOrDefault(key, Collections.emptyMap()).values().removeIf(date -> date < System.currentTimeMillis());
    }

    @NotNull
    public static String legalizeMessage(@NotNull String message) {
        for (String banned : BANNED_CHARACTERS) {
            message = message.replace(banned, "");
        }
        return message;
    }


    public static void setLastMessage(@NotNull Player player, @NotNull String message) {
        int cooldown = ChatConfig.ANTI_SPAM_BLOCK_SIMILAR_COOLDOWN.get();
        long expireDate = cooldown <= 0 ? -1 : System.currentTimeMillis() + cooldown * 1000L;

        LAST_MESSAGE.put(player.getName(), Pair.of(Colorizer.strip(message), expireDate));
    }

    @Nullable
    public static String getLastMessage(@NotNull Player player) {
        var last = LAST_MESSAGE.get(player.getName());
        if (last == null || last.getSecond() > 0 && System.currentTimeMillis() > last.getSecond()) return null;

        return last.getFirst();
    }

    public static void setLastCommand(@NotNull Player player, @NotNull String command) {
        int cooldown = ChatConfig.ANTI_SPAM_BLOCK_SIMILAR_COOLDOWN.get();
        long expireDate = cooldown <= 0 ? -1 : System.currentTimeMillis() + cooldown * 1000L;

        LAST_COMMAND.put(player.getName(), Pair.of(Colorizer.strip(command), expireDate));
    }

    @Nullable
    public static String getLastCommand(@NotNull Player player) {
        var last = LAST_COMMAND.get(player.getName());
        if (last == null || last.getSecond() > 0 && System.currentTimeMillis() > last.getSecond()) return null;

        return last.getFirst();
    }


    public static long getNextCommandTime(@NotNull Player player) {
        return NEXT_COMMAND_IN.getOrDefault(player.getName(), 0L);
    }

    public static void setNextCommandTime(@NotNull Player player) {
        if (ChatConfig.ANTI_SPAM_COMMAND_COOLDOWN.get() <= 0) return;

        NEXT_COMMAND_IN.put(player.getName(), (long) (System.currentTimeMillis() + (ChatConfig.ANTI_SPAM_COMMAND_COOLDOWN.get() * 1000D)));
    }

    public static boolean isNextCommandAvailable(@NotNull Player player) {
        return System.currentTimeMillis() > getNextCommandTime(player);
    }


    public static void setNextMessageTime(@NotNull Player player, @NotNull ChatChannel channel) {
        if (channel.getMessageCooldown() <= 0) return;

        NEXT_MESSAGE_IN.computeIfAbsent(player.getName(), k -> new HashMap<>())
            .put(channel.getId(), System.currentTimeMillis() + channel.getMessageCooldown() * 1000L);
    }

    public static long getNextMessageTime(@NotNull Player player, @NotNull ChatChannel channel) {
        return NEXT_MESSAGE_IN.getOrDefault(player.getName(), Collections.emptyMap())
            .getOrDefault(channel.getId(), 0L);
    }

    public static boolean isNextMessageAvailable(@NotNull Player player, @NotNull ChatChannel channel) {
        return System.currentTimeMillis() > getNextMessageTime(player, channel);
    }


    public static void setMentionCooldown(@NotNull Player player, @NotNull String mention) {
        if (ChatConfig.MENTIONS_COOLDOWN.get() <= 0) return;

        NEXT_MENTION_IN.computeIfAbsent(player.getName(), k -> new HashMap<>())
            .put(mention.toLowerCase(), System.currentTimeMillis() + ChatConfig.MENTIONS_COOLDOWN.get() * 1000L);
    }

    public static long getNextMentionTimestamp(@NotNull Player player, @NotNull String mention) {
        return NEXT_MENTION_IN.getOrDefault(player.getName(), Collections.emptyMap())
            .getOrDefault(mention.toLowerCase(), 0L);
    }

    public static boolean isMentionRefreshed(@NotNull Player player, @NotNull String mention) {
        return System.currentTimeMillis() > getNextMentionTimestamp(player, mention);
    }

    public static boolean isMentionsEnabled(@NotNull Player player) {
        SunUser user = SunLightAPI.PLUGIN.getUserManager().getUserData(player);
        return user.getSettings().get(DefaultSettings.MENTIONS);
    }


    @NotNull
    public static String doAntiCaps(@NotNull String msgReal) {
        if (!ChatConfig.ANTI_CAPS_ENABLED.get()) return msgReal;

        String msgRaw = Colorizer.strip(msgReal);

        // Ignore player names in case if they are fully caps
        for (String name : CollectionsUtil.playerNames()) {
            msgRaw = msgRaw.replace(name, "");
        }
        for (String ignored : ChatConfig.ANTI_CAPS_IGNORED_WORDS.get()) {
            msgRaw = msgRaw.replace(ignored, "");
        }

        // Then check if message has enought length to check
        if (msgRaw.length() < ChatConfig.ANTI_CAPS_MESSAGE_LENGTH_MIN.get()) return msgReal;

        double uppers = 0;
        double length = 0;
        for (char char2 : msgRaw.toCharArray()) {
            if (!Character.isLetter(char2) || Character.isWhitespace(char2)) continue;
            if (Character.isUpperCase(char2)) uppers++;
            length++;
        }
        double percent = uppers / length * 100D;
        return percent >= ChatConfig.ANTI_CAPS_UPPER_LETTER_PERCENT_MIN.get() ? msgReal.toLowerCase() : msgReal;
    }


    public static boolean checkSpamSimilarMessage(@NotNull Player player, @NotNull String msgRaw) {
        return checkSpamSimilar(player, msgRaw, getLastMessage(player));
    }

    public static boolean checkSpamSimilarCommand(@NotNull Player player, @NotNull String msgRaw) {
        return checkSpamSimilar(player, msgRaw, getLastCommand(player));
    }

    private static boolean checkSpamSimilar(@NotNull Player player, @NotNull String msgRaw, @Nullable String msgLast) {
        if (!ChatConfig.ANTI_SPAM_ENABLED.get() || ChatConfig.ANTI_SPAM_BLOCK_SIMILAR_PERCENT.get() <= 0D) return true;
        if (msgLast == null || msgLast.isEmpty()) return true;

        double similarity = similarity(Colorizer.strip(msgRaw), msgLast);
        return !(similarity >= ChatConfig.ANTI_SPAM_BLOCK_SIMILAR_PERCENT.get() / 100D);
    }


    private static double similarity(@NotNull String s1, @NotNull String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) {
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) { return 1.0; /* both strings are zero length */ }

        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }

    private static int editDistance(@NotNull String s1, @NotNull String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }
}
