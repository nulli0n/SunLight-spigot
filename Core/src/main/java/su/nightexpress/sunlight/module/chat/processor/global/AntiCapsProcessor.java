package su.nightexpress.sunlight.module.chat.processor.global;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.context.ChatContext;
import su.nightexpress.sunlight.module.chat.processor.ChatProcessor;

public class AntiCapsProcessor implements ChatProcessor<ChatContext> {

    @Override
    public void preProcess(@NonNull ChatModule module, @NonNull ChatContext context) {
        context.setMessage(this.moderateUpperCase(module, context.getMessage()));
    }

    @Override
    public void postProcess(@NonNull ChatModule module, @NonNull ChatContext context) {

    }

    @NonNull
    private String moderateUpperCase(@NonNull ChatModule module, @NonNull String message) {
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

    private static int countUpperCaseLetters(@NonNull String string) {
        int count = 0;

        for (char c : string.toCharArray()) {
            if (!Character.isLetter(c) || Character.isWhitespace(c)) continue;
            if (Character.isUpperCase(c)) count++;
        }

        return count;
    }
}
