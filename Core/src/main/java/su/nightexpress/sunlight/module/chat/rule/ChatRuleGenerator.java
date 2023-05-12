package su.nightexpress.sunlight.module.chat.rule;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChatRuleGenerator {

    private static final Map<Character, String[]> CHAR_MAPPER = new HashMap<>();
    private static final String ANY_CHAR_REGEX = "[^a-zA-ZА-Яа-я]|\\s";

    static {
        CHAR_MAPPER.put('а', new String[]{"a","@"});
        CHAR_MAPPER.put('б', new String[]{"b"});
        CHAR_MAPPER.put('в', new String[]{"v","w"});
        CHAR_MAPPER.put('г', new String[]{"g","r"});
        CHAR_MAPPER.put('д', new String[]{"d"});
        CHAR_MAPPER.put('е', new String[]{"e","ё"});
        CHAR_MAPPER.put('ё', new String[]{"e","е"});
        CHAR_MAPPER.put('ж', new String[]{"zh","j"});
        CHAR_MAPPER.put('з', new String[]{"z","3"});
        CHAR_MAPPER.put('и', new String[]{"i","u"});
        CHAR_MAPPER.put('й', new String[]{"i","u","и"});
        CHAR_MAPPER.put('к', new String[]{"k"});
        CHAR_MAPPER.put('л', new String[]{"l"});
        CHAR_MAPPER.put('м', new String[]{"m"});
        CHAR_MAPPER.put('н', new String[]{"n"});
        CHAR_MAPPER.put('о', new String[]{"o","0"});
        CHAR_MAPPER.put('п', new String[]{"p"});
        CHAR_MAPPER.put('р', new String[]{"r","p"});
        CHAR_MAPPER.put('с', new String[]{"s","c","$"});
        CHAR_MAPPER.put('т', new String[]{"t"});
        CHAR_MAPPER.put('у', new String[]{"u","y"});
        CHAR_MAPPER.put('ф', new String[]{"f"});
        CHAR_MAPPER.put('х', new String[]{"x","h"});
        CHAR_MAPPER.put('ц', new String[]{"ts","c","с"});
        CHAR_MAPPER.put('ч', new String[]{"ch","4"});
        CHAR_MAPPER.put('ш', new String[]{"sh"});
        CHAR_MAPPER.put('щ', new String[]{"sh","ш"});
        CHAR_MAPPER.put('ы', new String[]{"i","и"});
        CHAR_MAPPER.put('э', new String[]{"e"});
        CHAR_MAPPER.put('ю', new String[]{"u","yu"});
        CHAR_MAPPER.put('я', new String[]{"ya","9","ia","йя","йа"});


        CHAR_MAPPER.put('i', new String[]{"1"});
        CHAR_MAPPER.put('e', new String[]{"3"});
        CHAR_MAPPER.put('a', new String[]{"4"});
        CHAR_MAPPER.put('s', new String[]{"5"});
        CHAR_MAPPER.put('t', new String[]{"7"});
        CHAR_MAPPER.put('g', new String[]{"9"});
        CHAR_MAPPER.put('o', new String[]{"0"});
    }

    /*public static void createRus() {
        String[] words = {"сука","суки","сучк","сучар","сучен","хуй","хуя","хуе","жопа","пизд","пезд","уеб",
            "ебан","ебобо","ебл","залуп","чмо","дебил","дибил","долб","лох","лошар","педик","гомос","шлюх","проститут",
            "говн","паскуд","мудо","муда","чухан","бля","мраз"};

        for (String word : words) {
            System.out.println(generate(word));
        }
    }*/

    @NotNull
    public static String generate(@NotNull String word) {
        StringBuilder builder = new StringBuilder();
        builder.append("(?i)(");

        List<String> byk = new ArrayList<>();

        char[] letters = word.toCharArray();
        for (char letter : letters) {
            Set<String> alts = Stream.of(CHAR_MAPPER.getOrDefault(letter, new String[0])).collect(Collectors.toSet());
            alts.add(String.valueOf(letter));

            String allAlts = String.join("|", alts);
            byk.add("(?:" + allAlts + ")+");
        }

        String split = String.join("(?:" + ANY_CHAR_REGEX + ")*", byk);
        builder.append(split);
        builder.append(")");
        return builder.toString();
    }
}
