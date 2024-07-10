package su.nightexpress.sunlight.module.chat.rule;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.FileUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.chat.util.Placeholders;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RuleCreator {

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

    private final SunLightPlugin plugin;
    private final RuleManager ruleManager;

    public RuleCreator(@NotNull SunLightPlugin plugin, @NotNull RuleManager ruleManager) {
        this.plugin = plugin;
        this.ruleManager = ruleManager;
    }

    public void create() {
        this.createAdvertisementData();
        this.createSwearData();
    }

    private void createAdvertisementData() {
        Map<String, ChatRule> ruleMap = new HashMap<>();

        ruleMap.put("inet_addresses", new ChatRule(
            Lists.newList(
                "\\b[0-9]{1,3}(\\.|dot|\\(dot\\)|-|;|:|,|(\\W|\\d|_)*\\s)+[0-9]{1,3}(\\.|dot|\\(dot\\)|-|;|:|,|(\\W|\\d|_)*\\s)+[0-9]{1,3}(\\.|dot|\\(dot\\)|-|;|:|,|(\\W|\\d|_)*\\s)+[0-9]{1,3}\\b"
            ), ChatRule.Type.DENY, "", Lists.newSet("127.0.0.1"))
        );

        ruleMap.put("domains", new ChatRule(
            Lists.newList(
                "[a-zA-Z0-9\\-\\.]+\\s?(\\.|dot|\\(dot\\)|-|;|:|,)\\s?(com|ro|org|net|cz|co|uk|sk|biz|mobi|xxx|eu|me|io|ru|su|tk|ua)\\b"
            ), ChatRule.Type.DENY, "", Lists.newSet("google.com", "spigotmc.org", "myserver.abc"))
        );

        createData("advertisement", data -> {
            data.getRuleMap().putAll(ruleMap);
        });
    }

    private void createSwearData() {
        List<String> punishCommands = Lists.newList("warn " + Placeholders.PLAYER_NAME, "smite " + Placeholders.PLAYER_NAME);
        Map<String, ChatRule> ruleMap = new HashMap<>();

        ruleMap.put("bad_words_en", this.createEnSwearRule());
        ruleMap.put("bad_words_ru", this.createRuSwearRule());

        createData("swear", data -> {
            data.getCommands().addAll(punishCommands);
            data.getRuleMap().putAll(ruleMap);
        });
    }

    @NotNull
    private ChatRule createRuSwearRule() {
        List<String> words = Lists.newList("сука", "суки", "сучк", "сучар", "сучен", "хуй", "хуя", "хуе", "жопа", "пизд", "пезд", "уеб",
            "ебан", "ебобо", "ебл", "залуп", "чмо", "дебил", "дибил", "долб", "лох", "лошар", "педик", "гомос", "шлюх", "проститут",
            "говн", "паскуд", "мудо", "муда", "чухан", "бля", "мраз");
        words = words.stream().map(this::createBadWordRegex).toList();

        return this.createBadWordRule(words, Lists.newSet("корбля", "стебля", "гребля", "сабля", "барсуки", "застрахуй", "подстрахуй"));
    }

    @NotNull
    private ChatRule createEnSwearRule() {
        List<String> words = Lists.newList("dick", "ass", "retard", "fuck", "dumbass", "nigga", "nigra", "bitch", "cock", "cunt", "bastard",
            "bullshit", "jackass");
        words = words.stream().map(this::createBadWordRegex).toList();

        return this.createBadWordRule(words, Lists.newSet());
    }


    private void createData(@NotNull String id, @NotNull Consumer<RuleData> consumer) {
        File file = new File(this.ruleManager.getRulesPath(), id + ".yml");
        if (file.exists()) return;

        FileUtil.create(file);

        RuleData data = new RuleData(plugin, file);
        consumer.accept(data);
        data.save();
    }

    @NotNull
    private String createBadWordRegex(@NotNull String word) {
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

    @NotNull
    private ChatRule createBadWordRule(@NotNull List<String> regex, @NotNull Set<String> ignoredWords) {
        return new ChatRule(regex, ChatRule.Type.CENSOR, "***", ignoredWords);
    }
}
