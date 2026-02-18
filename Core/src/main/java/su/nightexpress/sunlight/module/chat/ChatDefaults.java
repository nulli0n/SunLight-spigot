package su.nightexpress.sunlight.module.chat;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.module.chat.format.FormatComponent;
import su.nightexpress.sunlight.module.chat.format.FormatDefinition;
import su.nightexpress.sunlight.module.chat.mention.GroupMention;
import su.nightexpress.sunlight.module.chat.spy.SpyType;

import java.util.*;

import static su.nightexpress.nightcore.util.placeholder.CommonPlaceholders.*;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.module.chat.ChatPlaceholders.*;

public class ChatDefaults {

    public static final String DEFAULT_PM_ALIAS    = "msg";
    public static final String DEFAULT_REPLY_ALIAS = "reply";

    public static final String DEFAULT_CHANNEL_ID     = "_default_";
    public static final String DEFAULT_CHANNEL_FORMAT = GRAY.wrap("[" + CHANNEL_NAME + "]") + " " + SLPlaceholders.GENERIC_FORMAT;
    public static final String DEFAULT_LOCAL_CHANNEL_ID = "local";

    public static final String DEFAULT_USER_FORMAT    = CommonPlaceholders.PLAYER_NAME + ": " + SLPlaceholders.GENERIC_FORMAT;
    public static final String DEFAULT_RULE_FILE_NAME = "default.txt";

    @NotNull
    public static Map<String, FormatDefinition> getDefaultFormats() {
        Map<String, FormatDefinition> map = new HashMap<>();

        map.put(DEFAULT, new FormatDefinition(
            0,
            "%rank_member%%player_info%: " + GRAY.wrap(SLPlaceholders.GENERIC_MESSAGE),
            Set.of(SLPlaceholders.WILDCARD)
        ));

        map.put("owner", new FormatDefinition(
            100,
            "%rank_owner%%player_info%: " + GRAY.wrap(SLPlaceholders.GENERIC_MESSAGE),
            Set.of("admin", "owner")
        ));

        return map;
    }

    @NotNull
    public static Map<String, FormatComponent> getDefaultFormatComponents() {
        Map<String, FormatComponent> map = new HashMap<>();

        map.put("player_info", new FormatComponent(
            SHOW_TEXT.with(GRAY.wrap("Player: " + LIGHT_PURPLE.wrap(PLAYER_NAME) + BR + "Nickname: " + LIGHT_PURPLE.wrap(PLAYER_DISPLAY_NAME) + BR + BR + GRAY.wrap("(Click to message)")))
                .wrap(SUGGEST_COMMAND.with("/" + DEFAULT_PM_ALIAS + " " + PLAYER_NAME + " ").wrap(PLAYER_DISPLAY_NAME))
        ));

        map.put("rank_owner", new FormatComponent(
            SHOW_TEXT.with(GRAY.wrap("This player is the server " + SOFT_YELLOW.wrap("Owner"))).wrap(PLAYER_PREFIX)
        ));

        map.put("rank_member", new FormatComponent(
            SHOW_TEXT.with(GRAY.wrap("Type " + SOFT_GREEN.wrap("/ranks") + " to view all special ranks.")).wrap(RUN_COMMAND.with("/ranks").wrap(PLAYER_PREFIX))
        ));

        return map;
    }

    @NotNull
    public static Map<String, GroupMention> getDefaultMentions() {
        Map<String, GroupMention> map = new HashMap<>();

        map.put("all", new GroupMention(SOFT_AQUA.wrap("@All"), Set.of(SLPlaceholders.WILDCARD)));
        map.put("admin", new GroupMention(SOFT_RED.wrap("@Admin"), Set.of("admin")));

        return map;
    }

    @NotNull
    public static Collection<String> getDefaultWordFilterRules(@NotNull Locale locale) {
        String code = locale.getLanguage();

        if (code.equalsIgnoreCase("ru")) {
            return getRussianWordFilterRules();
        }

        return getEnglishWordFilterRules();
    }

    @NotNull
    private static Collection<String> getEnglishWordFilterRules() {
        String[] rules = {
            "ass", "asshole", "bastard", "bitch", "cock+", "cunt", "dickhead", "dick", "dumbass",
            "fag+", "+fuck+", "nigga", "nigra", "pussy"
        };

        return Arrays.asList(rules);
    }

    @NotNull
    private static Collection<String> getRussianWordFilterRules() {
        String[] rulesA = {
            "-страхуй-", "-бляха-", "-бляшка-",
            "+хуй+", "+хуе+", "+пизд+", "+пёзд+", "уеб+", "уёб+",
            "ебан+", "ёбан+", "ебла+", "еблы+", "бля+",
            "залуп+", "херн+", "херов+", "хер", "сука", "суки", "сучар+",
            "дебил+", "дибил+", "долба+", "далба+", "гнида", "гнидо+", "гандон+", "даун", "чмо",
            "мраз+", "мудак", "мудило", "мудила", "ублюд+",
            "чушпан", "шлюх+", "шлюш+"
        };

        return Arrays.asList(rulesA);
    }

    @NotNull
    public static Map<SpyType, String> getDefaultSpyFormat() {
        Map<SpyType, String> map = new HashMap<>();
        map.put(SpyType.SOCIAL, SOFT_RED.wrap("[SocialSpy]") + " " + GRAY.wrap(PLAYER_NAME + " ➡ " + SLPlaceholders.GENERIC_TARGET + ":") + " " + GRAY.wrap(SLPlaceholders.GENERIC_MESSAGE));
        map.put(SpyType.COMMAND, SOFT_RED.wrap("[CommandSpy]") + " " + GRAY.wrap(PLAYER_NAME + " executed a command:") + " " + GRAY.wrap(SLPlaceholders.GENERIC_MESSAGE));
        map.put(SpyType.CHAT, SOFT_RED.wrap("[ChatSpy]") + " " + GRAY.wrap("[" + CHANNEL_NAME + "]" + " " + PLAYER_NAME + ":") + " " + GRAY.wrap(SLPlaceholders.GENERIC_MESSAGE));
        return map;
    }
}
