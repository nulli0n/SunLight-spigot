package su.nightexpress.sunlight.module.chat.processor.global;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.context.ChatContext;
import su.nightexpress.sunlight.module.chat.processor.ChatProcessor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class AntiCapsProcessor implements ChatProcessor<ChatContext> {

    @Override
    public void preProcess(@NotNull ChatModule module, @NotNull ChatContext context) {
        context.setMessage(this.moderateUpperCase(module, context.getMessage()));
    }

    @Override
    public void postProcess(@NotNull ChatModule module, @NotNull ChatContext context) {

    }

    @NotNull
    private String moderateUpperCase(@NotNull ChatModule module, @NotNull String message) {
        String[] words = message.split(" ");

        int totalUpperCase = 0;
        int lengthThreshold = module.getSettings().getAntiCapsLengthThreshold();

        Map<Integer, Function<String, String>> indexesToModerate = new LinkedHashMap<>();

        for (int index = 0; index < words.length; index++) {
            String word = words[index];

            int upperCaseCount = countUpperCaseLetters(word);

            Player player = Players.getPlayer(word);
            if (player != null) {
                String playerName = player.getName();
                int playerUpperCount = countUpperCaseLetters(playerName);
                if (upperCaseCount > playerUpperCount) {
                    totalUpperCase += upperCaseCount;
                    indexesToModerate.put(index, string -> playerName);
                    continue;
                }
            }

            if (word.length() >= lengthThreshold) {
                totalUpperCase += upperCaseCount;
            }
            indexesToModerate.put(index, LowerCase.USER_LOCALE::apply);
        }

        int messageLength = Stream.of(words).mapToInt(String::length).sum();
        double percent = (double) totalUpperCase / (double) messageLength;
        double threshold = module.getSettings().getAntiCapsUpperCaseThreshold() / 100D;

        if (percent < threshold) return message;

        StringBuilder builder = new StringBuilder();

        indexesToModerate.forEach((index, function) -> {
            String moderated = function.apply(words[index]);

            if (!builder.isEmpty()) builder.append(" ");
            builder.append(moderated);
        });

        return builder.toString();
    }

    private static int countUpperCaseLetters(@NotNull String string) {
        int count = 0;

        for (char c : string.toCharArray()) {
            if (!Character.isLetter(c) || Character.isWhitespace(c)) continue;
            if (Character.isUpperCase(c)) count++;
        }

        return count;
    }
}
