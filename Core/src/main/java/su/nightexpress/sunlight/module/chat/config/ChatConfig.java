package su.nightexpress.sunlight.module.chat.config;

import com.google.common.collect.Sets;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.EventPriority;
import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.api.lang.LangMessage;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.StringUtil;
import su.nexmedia.engine.utils.message.NexParser;
import su.nightexpress.sunlight.SunLightAPI;
import su.nightexpress.sunlight.module.chat.ChatPerms;
import su.nightexpress.sunlight.module.chat.util.ChatSpyType;
import su.nightexpress.sunlight.module.chat.util.Placeholders;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ChatConfig {

    public static final JOption<EventPriority> CHAT_EVENT_PRIORITY = JOption.create("Chat_Event_Priority",
        EventPriority.class, EventPriority.HIGH,
        "Sets the chat event priority. https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/EventPriority.html",
        "Change this only if you're experiencing compatibility issues with other plugins."
    );
    public static final JOption<Boolean> CHAT_JSON = JOption.create("Chat_JSON_Enabled", true,
        "When 'true', allows you to create Json elements for the chat formations.",
        Placeholders.ENGINE_URL_LANG_JSON,
        "When disabled, all Json content will be converted to regular text."
    );
    public static final JOption<Boolean> DISABLE_CHAT_REPORTS = JOption.create("Disable_Chat_Reports", true,
        "When 'true' completely disables Chat Reports system from the server.",
        "As well as disables join notifications about messages verification."
    );
    public static final JOption<Boolean> SILENT_CHANNEL_JOIN_ON_LOGIN = JOption.create("Silent_Channel_Join_On_Login", false,
        "When 'true', disables the chat channel join messages when players joined the server."
    );


    public static final JOption<Boolean> MODULE_JOIN_QUIT_MESSAGES = JOption.create("Modules.Join_Quit_Messages", true,
        "When 'true', enables custom Join and Quit messages."
    );
    public static final JOption<Boolean> MODULE_DEATH_MESSAGES = JOption.create("Modules.Death_Messages", true,
        "When 'true', enables custom Death messages."
    );
    public static final JOption<Boolean> MODULE_ANNOUNCER = JOption.create("Modules.Announcer", true,
        "When 'true', enables Announcer that broadcasts custom messages with certain intervals."
    );

    public static final JOption<Map<String, ChatPlayerFormat>> FORMAT_PLAYER = new JOption<Map<String, ChatPlayerFormat>>("Format",
        (cfg, path, def) -> cfg.getSection(path).stream().collect(Collectors.toMap(String::toLowerCase, v -> ChatPlayerFormat.read(cfg, path + "." + v))),
        () -> Map.of(
            Placeholders.DEFAULT, new ChatPlayerFormat(0,
                "<? show_text:\"&fConsider #c1fd9f/donate &fto get special ranks\" run_command:\"/donate\" ?>" + Placeholders.PLAYER_PREFIX + "</>" +
                    "<? " +
                    "show_text:\"#c59ffdPlayer: #fd9ff3" + Placeholders.PLAYER_NAME + " #c59ffdNickname: #fd9ff3" + Placeholders.PLAYER_DISPLAY_NAME + NexParser.TAG_NEWLINE + "#c59ffdBalance: #fd9ff3$%vault_eco_balance_formatted%" + NexParser.TAG_NEWLINE + "&7" + NexParser.TAG_NEWLINE + "&7(Click to send private message)\" " +
                    "suggest_command:\"/tell " + Placeholders.PLAYER_NAME + "\" "  +
                    "?>" +
                    Placeholders.PLAYER_DISPLAY_NAME +
                    "</>" +
                    Placeholders.PLAYER_SUFFIX,
                "<? show_text:\"&7&oMessage was sent at: #c1fd9f%localtime_time_HH:MM:ss%\" ?>" + Placeholders.FORMAT_PLAYER_COLOR + Placeholders.GENERIC_MESSAGE + "</>",
                ChatColor.GRAY.getName()),

            "owner", new ChatPlayerFormat(100,
                "<? show_text:\"&fThis player is the server #c1fd9fOwner\" ?>" + Placeholders.PLAYER_PREFIX + "</>" +
                    "<? " +
                    "show_text:\"#fe9e3ePlayer: #fbb671" + Placeholders.PLAYER_NAME + " #fe9e3eNickname: #fbb671" + Placeholders.PLAYER_DISPLAY_NAME + NexParser.TAG_NEWLINE + "#fe9e3eBalance: #fbb671$%vault_eco_balance_formatted%" + NexParser.TAG_NEWLINE + "&7" + NexParser.TAG_NEWLINE + "&7(Click to send private message)\" " +
                    "suggest_command:\"/tell " + Placeholders.PLAYER_NAME + "\" " +
                    "?>" + Placeholders.PLAYER_DISPLAY_NAME + "</>" + Placeholders.PLAYER_SUFFIX,
                "<? show_text:\"&7&oMessage was sent at: #c1fd9f%localtime_time_HH:MM:ss%\" ?>" + Placeholders.FORMAT_PLAYER_COLOR + Placeholders.GENERIC_MESSAGE + "</>",
                ChatColor.GRAY.getName())
        ),
        "In this section you can set custom format for each Permision Group",
        "If player has multiple permission groups, format with the highest priority will be used.",
        "(!) IMPORTANT: If you remove '" + Placeholders.DEFAULT + "' format and no other format will be available for player(s), no message will be sent!",
        "JSON Formatting is allowed here, please see: " + Placeholders.ENGINE_URL_LANG_JSON,
        "PlaceholderAPI is supported here.",
        "Internal Placeholders:",
        "- " + Placeholders.GENERIC_MESSAGE + " - Player message.",
        "- " + Placeholders.PLAYER_NAME + " - Player real name.",
        "- " + Placeholders.PLAYER_DISPLAY_NAME + " - Player display (custom) name.",
        "- " + Placeholders.PLAYER_PREFIX + " - Player prefix (from Permissions plugin)",
        "- " + Placeholders.PLAYER_SUFFIX + " - Player suffix (from Permissions plugin)",
        "- " + Placeholders.PLAYER_WORLD + " - Player world name.",
        "Placeholders to use in Chat Channel Format:",
        "- " + Placeholders.FORMAT_PLAYER_NAME + " - This 'Name' format.",
        "- " + Placeholders.FORMAT_PLAYER_MESSAGE + " - This 'Message' format.",
        "- " + Placeholders.FORMAT_PLAYER_COLOR + " - This 'Default_Color' color."
    ).setWriter((cfg, path, map) -> map.forEach((id, format) -> ChatPlayerFormat.write(format, cfg, path + "." + id)));

    public static final JOption<Boolean>   PM_ENABLED = JOption.create("Private_Messages.Enabled", true,
        "Enables Private Messages feature.",
        "This option will add two commands: /tell and /reply (you can change aliases in SunLight commands.yml)"
    );

    public static final JOption<LangMessage> PM_FORMAT_INCOMING = new JOption<>("Private_Messages.Format.Incoming",
        (cfg, path, def) -> new LangMessage(SunLightAPI.PLUGIN, cfg.getString(path, "")),
        new LangMessage(SunLightAPI.PLUGIN, "<! sound:\"" + Sound.BLOCK_NOTE_BLOCK_BELL.name() + "\" prefix:\"false\" !><? show_text:\"&bClick to reply!\" suggest_command:\"/tell " + Placeholders.PLAYER_NAME + "\" ?>" + "&d[Private] &f" + Placeholders.PLAYER_DISPLAY_NAME + " &7whispers:&7 " + Placeholders.GENERIC_MESSAGE + "</>"),
        "Sets the format for incoming private messages.",
        "Use " + Placeholders.GENERIC_MESSAGE + " placeholder for a message text.",
        "You can use 'Player' placeholders: " + Placeholders.ENGINE_URL_PLACEHOLDERS,
        "JSON and Message Options are allowed: " + Placeholders.ENGINE_URL_LANG_JSON
    ).setWriter((cfg, path, msg) -> cfg.set(path, msg.getRaw()));

    public static final JOption<LangMessage> PM_FORMAT_OUTGOING = new JOption<>("Private_Messages.Format.Outgoing",
        (cfg, path, def) -> new LangMessage(SunLightAPI.PLUGIN, cfg.getString(path, "")),
        new LangMessage(SunLightAPI.PLUGIN, "<! sound:\"" + Sound.UI_BUTTON_CLICK.name() + "\" prefix:\"false\" !>&d[Private] &7whisper to &f" + Placeholders.PLAYER_DISPLAY_NAME + ":&7 " + Placeholders.GENERIC_MESSAGE),
        "Sets the format for outgoing private messages.",
        "Use " + Placeholders.GENERIC_MESSAGE + " placeholder for a message text.",
        "You can use 'Player' placeholders: " + Placeholders.ENGINE_URL_PLACEHOLDERS,
        "JSON and Message Options are allowed: " + Placeholders.ENGINE_URL_LANG_JSON
    ).setWriter((cfg, path, msg) -> cfg.set(path, msg.getRaw()));


    public static final JOption<Boolean> MENTIONS_ENABLED = JOption.create("Mentions.Enabled", true,
        "When 'true', enables the Mentions feature.",
        "Mentions allows you to attract attention of certain players or players with certain ranks when typing their name/rank in chat.",
        "(!) IMPORTANT: You players must have " + ChatPerms.MENTION.getName() + " permission or " + ChatPerms.MENTION_PLAYER + "[playerName] or " + ChatPerms.MENTION_SPECIAL + "[mentionName] permissions."
    );

    public static final JOption<Integer> MENTIONS_MAXIMUM = JOption.create("Mentions.Max_Per_Message", 3,
        "Sets the maximum amount of mentions per player message.",
        "When there is more mentions than max allowed, all other mentions will have no effect.",
        "Set this to -1 for unlimit."
    );

    public static final JOption<Integer> MENTIONS_COOLDOWN = JOption.create("Mentions.Cooldown", 15,
        "Sets per player cooldown for the same mentions.",
        "When mention is on cooldown, it will have no effect.",
        "Set this to -1 to disable."
    );

    public static final JOption<String> MENTIONS_PREFIX = JOption.create("Mentions.Prefix", "@",
        "A prefix that mention have to be followed by to work.",
        "With default '@' mention will be: '@UserName'."
    );

    public static final JOption<String> MENTIONS_FORMAT = JOption.create("Mentions.Format",
        "&a@" + Placeholders.PLAYER_DISPLAY_NAME + Placeholders.FORMAT_PLAYER_COLOR,
        "A text that will replace mention if player is valid.",
        "You can use 'Player' placeholders: " + Placeholders.ENGINE_URL_PLACEHOLDERS,
        "JSON and Message Options are allowed: " + Placeholders.ENGINE_URL_LANG_JSON,
        "Use " + Placeholders.FORMAT_PLAYER_COLOR + " placeholder to keep the original message color after mention format.",
        "PlaceholderAPI is supported here."
    ).mapReader(Colorizer::apply);

    public static final JOption<LangMessage> MENTIONS_NOTIFY = new JOption<>("Mentions.Notify",
        (cfg, path, def) -> new LangMessage(SunLightAPI.PLUGIN, cfg.getString(path, "")),
        new LangMessage(SunLightAPI.PLUGIN, "<! type:\"titles:20:50:20\" sound:\"" + Sound.BLOCK_NOTE_BLOCK_BELL.name() + "\" !>&a&lMentioned!\\n&a" + Placeholders.PLAYER_DISPLAY_NAME + "&7 mentioned you in chat."),
        "This is the message, that will be sent to mentioned player(s).",
        "You can use 'Player' placeholders: " + Placeholders.ENGINE_URL_PLACEHOLDERS,
        "You can use all Lang Message features: " + Placeholders.ENGINE_URL_LANG,
        "PlaceholderAPI is supported here."
    ).setWriter((cfg, path, msg) -> cfg.set(path, msg.getRaw()));

    public static final JOption<Map<String, ChatMention>> MENTIONS_SPECIAL = new JOption<Map<String, ChatMention>>("Mentions.Special",
        (cfg, path, def) -> cfg.getSection(path).stream().collect(Collectors.toMap(String::toLowerCase, v -> ChatMention.read(cfg, path + "." + v))),
        () -> Map.of(
            "all", new ChatMention("&b@All" + Placeholders.FORMAT_PLAYER_COLOR, Sets.newHashSet(Placeholders.WILDCARD)),
            "admin", new ChatMention("&c@Admin" + Placeholders.FORMAT_PLAYER_COLOR, Sets.newHashSet("admin"))
        ),
        "A list of custom mentions, that can be applied to multiple players based on their Permission Group.",
            "Use asterisk (*) to include all permission groups.",
            "Keys are mentions. By default it's '@all' and '@admin'.",
            "You must have Vault installed for this feature to work.",
            "JSON Formatting is allowed here, please see: " + Placeholders.ENGINE_URL_LANG_JSON,
            "Placeholders:",
            "- " + Placeholders.FORMAT_PLAYER_COLOR + " - Player default message color from 'Format' settings."
    ).setWriter((cfg, path, map) -> map.forEach((sId, ment) -> ChatMention.write(ment, cfg, path + "." + sId)));


    public static final JOption<Boolean> SPY_ENABLED = JOption.create("SpyOps.Enabled", true,
        "Enables the SpyOps feature.",
        "It will add a command to enable spy mode for Private Messages, Commands and regular Chat messages."
    );
    public static final JOption<Map<ChatSpyType, String>> SPY_FORMAT = new JOption<Map<ChatSpyType, String>>("SpyOps.Format",
        (cfg, path, def) -> {
            return cfg.getSection(path).stream().map(typeRaw -> StringUtil.getEnum(typeRaw, ChatSpyType.class).orElse(null))
                .filter(Objects::nonNull).collect(Collectors.toMap(k -> k, v -> Colorizer.apply(cfg.getString(path + "." + v.name(), ""))));
        },
        () -> {
        Map<ChatSpyType, String> map = new HashMap<>();
        map.put(ChatSpyType.SOCIAL, Colorizer.apply("&c[SocialSpy] &4" + Placeholders.PLAYER_NAME + " &7-> &4%player_target%&7: &c" + Placeholders.GENERIC_MESSAGE));
        map.put(ChatSpyType.COMMAND, Colorizer.apply("&c[CommandSpy] &4" + Placeholders.PLAYER_NAME + " &7executed a command: &c" + Placeholders.GENERIC_MESSAGE));
        map.put(ChatSpyType.CHAT, Colorizer.apply("&c[ChatSpy] &7[&f" + Placeholders.CHANNEL_NAME + "&7] &4" + Placeholders.PLAYER_NAME + "&7: &c" + Placeholders.GENERIC_MESSAGE));
        return map;
        },
        "Sets the format for each Spy Mode.",
        "Use " + Placeholders.GENERIC_MESSAGE + " placeholder for a message text.",
        "For 'SOCIAL' Mode, you can use %player_target% placeholder.",
        "For 'CHAT' Mode, you can use 'Channel' placeholders.",
        "You can use 'Player' placeholders: " + Placeholders.ENGINE_URL_PLACEHOLDERS,
        "JSON format is allowed: " + Placeholders.ENGINE_URL_LANG_JSON
    ).setWriter((cfg, path, map) -> map.forEach((type, format) -> cfg.set(path + "." + type.name(), format)));


    public static final JOption<Boolean>     ANTI_CAPS_ENABLED                  = JOption.create("Anti_Caps.Enabled", true,
        "When 'true', enables the AntiCaps auto-moderation feature.",
        "This feature will prevent players from sending full caps messages."
    );
    public static final JOption<Integer>     ANTI_CAPS_MESSAGE_LENGTH_MIN       = JOption.create("Anti_Caps.Message_Length_Min",
        10,
        "Minimal message length for the AntiCaps feature to check it.",
        "This option might be useful to prevent trigger AntiCaps on messages like 'LOL', 'OMG', etc."
    );
    public static final JOption<Integer>     ANTI_CAPS_UPPER_LETTER_PERCENT_MIN = JOption.create("Anti_Caps.Upper_Letters_Percent_Min",
        75,
        "Minimal percentage of upper-case letters in a message for AntiCaps to handle it.",
        "This option might be useful to prevent trigger AntiCaps on messages with many upper-case letters without bad intentions."
    );
    public static final JOption<Set<String>> ANTI_CAPS_AFFECTED_COMMANDS        = JOption.create("Anti_Caps.Affected_Commands",
        Sets.newHashSet("tell", "me", "reply", "broadcase"),
        "A list of commands, that will be checked by AntiCaps for voilations.",
        "This option might be useful to prevent caps messages in private messages, broadcasts, etc.",
        "NOTE: You need to provide only ONE alias for each command. Example: If you have a /tell command with [/t, /msg, /w] aliases, you can provide only one of them."
    );
    public static final JOption<Set<String>> ANTI_CAPS_IGNORED_WORDS            = JOption.create("Anti_Caps.Ignored_Words",
        Sets.newHashSet("OMG", "LOL", "WTF", "IMHO", "WOW", "ROFL", "AHAHA", "DAMN"),
        "A list of words, that will be skipped when AntiCaps is checking a message.",
        "These words won't count into message length and upper-case letters amount."
    );

    public static final JOption<Boolean>     ANTI_SPAM_ENABLED = JOption.create("Anti_Spam.Enabled", true,
        "When 'true', enables the AntiSpam auto-moderation feature.",
        "This feature will prevent players from spamming (the same) messages/commands too fast."
    );
    public static final JOption<Double>      ANTI_SPAM_BLOCK_SIMILAR_PERCENT = JOption.create("Anti_Spam.Block_Similar_Messages.Percentage",
        90D,
        "How many (in percent) previous and current player message/command should match each other for the AntiSpam to trigger?",
        "Set this to 0 to disable."
    );
    public static final JOption<Double>      ANTI_SPAM_COMMAND_COOLDOWN = JOption.create("Anti_Spam.Command_Cooldown",
        2D,
    "Sets the cooldown between ALL player commands.",
        "For a chat message cooldown, check the channels config.",
        "Set this to 0 to disable."
    );
    public static final JOption<Set<String>> ANTI_SPAM_COMMAND_WHITELIST = JOption.create("Anti_Spam.Command_Whitelist",
        Sets.newHashSet("tell", "reply", "spawn", "home", "warp", "sethome", "delhome", "kit"),
        "A list of commands, that will be completely excluded from the AntiSpam checks.",
        "NOTE: You need to provide only ONE alias for each command. Example: If you have a /tell command with [/t, /msg, /w] aliases, you can provide only one of them."
    );


    public static final JOption<Boolean> ITEM_SHOW_ENABLED = JOption.create("Item_Show.Enabled", true,
        "Enables the Item Showcase feature.",
        "With this feature players will be able to show their items in chat and Private Messages."
    );
    public static final JOption<String> ITEM_SHOW_PLACEHOLDER = JOption.create("Item_Show.Placeholder",
        "@hand",
        "This placeholder will be replaced in player message with an item from their hand using the format below."
    );
    public static final JOption<String> ITEM_SHOW_FORMAT_CHAT = JOption.create("Item_Show.Format.Chat",
        "&7<<? show_item:\"" + Placeholders.ITEM_VALUE + "\" ?>&f" + Placeholders.ITEM_NAME + "</>&7>" + Placeholders.FORMAT_PLAYER_COLOR,
        "Format for the item showcase in main chat. Item placeholder will be replaced by this text.",
        "You can use JSON format here: " + Placeholders.ENGINE_URL_LANG_JSON,
        "You can use 'Player' placeholders: " + Placeholders.ENGINE_URL_PLACEHOLDERS,
        "Other Placeholders:",
        "- " + Placeholders.ITEM_NAME + " - Item display name.",
        "- " + Placeholders.ITEM_VALUE + " - Item Base64 value (for 'showItem' JSON argument)."
    ).mapReader(Colorizer::apply);

    public static final JOption<String> ITEM_SHOW_FORMAT_PM_IN = JOption.create("Item_Show.Format.Private.Incoming",
        "&d[Private] &f" + Placeholders.PLAYER_DISPLAY_NAME + " &7shows you &7<<? show_item:\"" + Placeholders.ITEM_VALUE + "\" ?>&f" + Placeholders.ITEM_NAME + "</>&7>",
        "Format for the incoming private message, when players uses 'Item_Show.Placeholder' in his message.",
        "This message will be sent next to the original message.",
        "You can use JSON format here: " + Placeholders.ENGINE_URL_LANG_JSON,
        "You can use 'Player' placeholders: " + Placeholders.ENGINE_URL_PLACEHOLDERS,
        "Other Placeholders:",
        "- " + Placeholders.ITEM_NAME + " - Item display name.",
        "- " + Placeholders.ITEM_VALUE + " - Item Base64 value (for 'showItem' JSON argument)."
    ).mapReader(Colorizer::apply);

    public static final JOption<String> ITEM_SHOW_FORMAT_PM_OUT = JOption.create("Item_Show.Format.Private.Outgoing",
        "&d[Private] &7You showed " + "&7<<? show_item:\"" + Placeholders.ITEM_VALUE + "\" ?>&f" + Placeholders.ITEM_NAME + "</>&7>" + " to &f" + Placeholders.PLAYER_DISPLAY_NAME + "&7.",
        "Format for the outgoing private message, when players uses 'Item_Show.Placeholder' in his message.",
        "This message will be sent next to the original message.",
        "You can use JSON format here: " + Placeholders.ENGINE_URL_LANG_JSON,
        "You can use 'Player' placeholders: " + Placeholders.ENGINE_URL_PLACEHOLDERS,
        "Other Placeholders:",
        "- " + Placeholders.ITEM_NAME + " - Item display name.",
        "- " + Placeholders.ITEM_VALUE + " - Item Base64 value (for 'showItem' JSON argument)."
    ).mapReader(Colorizer::apply);
}
