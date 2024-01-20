package su.nightexpress.sunlight.module.chat.rule;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChatRuleLazyGen {

    private static final Map<Character, String[]> CHAR_MAPPER = new HashMap<>();
    private static final String ANY_CHAR_REGEX = "[^a-zA-ZА-Яа-я]|\\s";

    static {
        CHAR_MAPPER.put('а', new String[]{"a","@","А"});
        CHAR_MAPPER.put('б', new String[]{"b","Б"});
        CHAR_MAPPER.put('в', new String[]{"v","w","В"});
        CHAR_MAPPER.put('г', new String[]{"g","r","Г"});
        CHAR_MAPPER.put('д', new String[]{"d","Д"});
        CHAR_MAPPER.put('е', new String[]{"e","ё","Е","Ё"});
        CHAR_MAPPER.put('ё', new String[]{"e","е","Ё","Е"});
        CHAR_MAPPER.put('ж', new String[]{"zh","j","Ж"});
        CHAR_MAPPER.put('з', new String[]{"z","3","З"});
        CHAR_MAPPER.put('и', new String[]{"i","u","И","Й","й"});
        CHAR_MAPPER.put('й', new String[]{"i","u","и","Й","И"});
        CHAR_MAPPER.put('к', new String[]{"k","К"});
        CHAR_MAPPER.put('л', new String[]{"l","Л"});
        CHAR_MAPPER.put('м', new String[]{"m","М"});
        CHAR_MAPPER.put('н', new String[]{"n","Н"});
        CHAR_MAPPER.put('о', new String[]{"o","0","О"});
        CHAR_MAPPER.put('п', new String[]{"p","П"});
        CHAR_MAPPER.put('р', new String[]{"r","p","Р"});
        CHAR_MAPPER.put('с', new String[]{"s","c","$","С"});
        CHAR_MAPPER.put('т', new String[]{"t","Т"});
        CHAR_MAPPER.put('у', new String[]{"u","y","У"});
        CHAR_MAPPER.put('ф', new String[]{"f","Ф"});
        CHAR_MAPPER.put('х', new String[]{"x","h","Х"});
        CHAR_MAPPER.put('ц', new String[]{"ts","c","с","Ц"});
        CHAR_MAPPER.put('ч', new String[]{"ch","4","Ч"});
        CHAR_MAPPER.put('ш', new String[]{"sh","Ш","Щ","ш","щ"});
        CHAR_MAPPER.put('щ', new String[]{"sh","ш","Щ","Ш"});
        CHAR_MAPPER.put('ы', new String[]{"i","и","Ы"});
        CHAR_MAPPER.put('э', new String[]{"e","Э"});
        CHAR_MAPPER.put('ю', new String[]{"u","yu","Ю"});
        CHAR_MAPPER.put('я', new String[]{"ya","9","ia","йя","йа","Я"});


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
