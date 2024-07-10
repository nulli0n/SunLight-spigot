package su.nightexpress.sunlight.module.chat.config;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Sound;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.wrapper.UniSound;
import su.nightexpress.sunlight.hook.HookId;
import su.nightexpress.sunlight.module.chat.command.PrivateMessageCommands;
import su.nightexpress.sunlight.module.chat.command.RoleplayCommands;
import su.nightexpress.sunlight.module.chat.format.FormatComponent;
import su.nightexpress.sunlight.module.chat.format.FormatContainer;
import su.nightexpress.sunlight.module.chat.mention.GroupMention;
import su.nightexpress.sunlight.module.chat.module.spy.SpyType;
import su.nightexpress.sunlight.module.chat.util.Placeholders;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.sunlight.module.chat.util.Placeholders.*;

public class ChatConfig {

    public static final ConfigValue<EventPriority> EVENT_PRIORITY = ConfigValue.create("Settings.Event_Priority",
        EventPriority.class,
        EventPriority.HIGH,
        "Sets priority for the AsyncPlayerChatEvent to handle it by the Chat module.",
        "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/EventPriority.html",
        "Change this only if you're experiencing compatibility issues with other plugins."
    );

    public static final ConfigValue<Boolean> USE_COMPONENTS = ConfigValue.create("Settings.Use_Components",
        true,
        "When enabled, sends chat messages as Text (JSON) Components instead of legacy strings.",
        "This allows you to use hover and click events in chat format."
    );

    public static final ConfigValue<Boolean> DISABLE_REPORTS = ConfigValue.create("Settings.Disable_Reports",
        true,
        "[" + HookId.PROTOCOL_LIB + " Required]",
        "Completely disables Chat Reports system and annoying verification messages on join."
    );

    public static final ConfigValue<Boolean> SILENT_CHANNEL_JOIN_ON_LOGIN = ConfigValue.create("Settings.Silent_Channel_Join_On_Login",
        true,
        "When 'true', disables the chat channel join messages when players joined the server."
    );

    public static final ConfigValue<Boolean> MODULE_JOIN_QUIT_MESSAGES = ConfigValue.create("Modules.Join_Quit_Messages",
        true,
        "When 'true', enables custom Join and Quit messages."
    );

    public static final ConfigValue<Boolean> MODULE_DEATH_MESSAGES = ConfigValue.create("Modules.Death_Messages",
        true,
        "When 'true', enables custom Death messages."
    );

    public static final ConfigValue<Boolean> MODULE_ANNOUNCER = ConfigValue.create("Modules.Announcer",
        true,
        "When 'true', enables Announcer that broadcasts custom messages with certain intervals."
    );

    public static final ConfigValue<Boolean> ROLEPLAY_COMMANDS_ENABLED = ConfigValue.create("Roleplay_Commands.Enabled",
        true,
        "Sets whether or not roleplay commands are enabled."
    );

    public static final ConfigValue<String> ROLEPLAY_COMMANDS_ME_FORMAT = ConfigValue.create("Roleplay_Commands.Format",
        ITALIC.enclose(LIGHT_CYAN.enclose(PLAYER_DISPLAY_NAME) + " " + LIGHT_GRAY.enclose(GENERIC_MESSAGE)),
        "Sets format for the '" + RoleplayCommands.NODE_ME + "' command."
    );

    public static final ConfigValue<Map<String, FormatComponent>> FORMAT_COMPONENTS = ConfigValue.forMap("Format.Components",
        (cfg, path, id) -> FormatComponent.read(cfg, path + "." + id, id),
        (cfg, path, map) -> map.forEach((id, component) -> component.write(cfg, path + "." + id)),
        () -> {
            Map<String, FormatComponent> map = new HashMap<>();
            String br = TAG_LINE_BREAK;

            map.put("player_info", new FormatComponent("player_info",
                HOVER.encloseHint(
                    CLICK.enclose(
                        PLAYER_DISPLAY_NAME,
                        ClickEvent.Action.SUGGEST_COMMAND,
                        "/tell " + PLAYER_NAME + " "
                    ),
                    LIGHT_GRAY.enclose("Player: " + LIGHT_PURPLE.enclose(PLAYER_NAME) + " Nickname: " + LIGHT_PURPLE.enclose(PLAYER_DISPLAY_NAME) + RESET.getBracketsName() + br + "Balance: " + LIGHT_PURPLE.enclose("$%vault_eco_balance_formatted%") + br + br + GRAY.enclose("(Click to send private message)"))
                )
            ));

            map.put("rank_owner", new FormatComponent("rank_owner",
                HOVER.encloseHint(PLAYER_PREFIX, LIGHT_GRAY.enclose("This player is the server " + LIGHT_YELLOW.enclose("Owner")))
            ));

            map.put("rank_member", new FormatComponent("rank_member",
                HOVER.encloseHint(
                    CLICK.enclose(PLAYER_PREFIX, ClickEvent.Action.RUN_COMMAND, "/donate"),
                    LIGHT_GRAY.enclose("Consider " + LIGHT_GREEN.enclose("/donate") + " to get special ranks")
                )
            ));

            map.put("msg", new FormatComponent("msg",
                HOVER.encloseHint(
                    GRAY.enclose(GENERIC_MESSAGE),
                    ITALIC.enclose(LIGHT_GRAY.enclose("Message was sent at: " + WHITE.enclose("%localtime_time_HH:MM:ss%")))
                )
            ));

            return map;
        },
        "Create here custom format components to insert them in chat format below!",
        "Every component has its own placeholder like '%component_name%'."
    );

    public static final ConfigValue<Map<String, FormatContainer>> FORMAT_LIST = ConfigValue.forMap("Format.List",
        (cfg, path, id) -> FormatContainer.read(cfg, path + "." + id),
        (cfg, path, map) -> map.forEach((id, component) -> component.write(cfg, path + "." + id)),
        () -> {
            Map<String, FormatContainer> map = new HashMap<>();

            map.put(DEFAULT, new FormatContainer(
                0,
                "%rank_member%%player_info%: ",
                "%msg%",
                Lists.newSet(WILDCARD)
            ));

            map.put("owner", new FormatContainer(
                0,
                "%rank_owner%%player_info%: ",
                "%msg%",
                Lists.newSet("admin", "owner")
            ));

            return map;
        },
        "In this section you can set custom format for each Permision Group",
        "If player has multiple permission groups, format with the highest priority will be used.",
        "Text Formations: " + WIKI_TEXT_URL,
        Plugins.PLACEHOLDER_API + " is supported here.",
        "Internal Placeholders:",
        "- " + PLAYER_NAME + " - Player real name.",
        "- " + PLAYER_DISPLAY_NAME + " - Player display (custom) name.",
        "- " + PLAYER_PREFIX + " - Player prefix (from Permissions plugin)",
        "- " + PLAYER_SUFFIX + " - Player suffix (from Permissions plugin)",
        "- " + PLAYER_WORLD + " - Player world name.",
        "Placeholders to use in Channel Format:",
        "- " + GENERIC_FORMAT + " - This 'Name' format.",
        "- " + GENERIC_MESSAGE + " - This 'Message' format."
    );

    public static final ConfigValue<Boolean> PM_ENABLED = ConfigValue.create("Private_Messages.Enabled",
        true,
        "Enables the Private Messages feature."
    );

    public static final ConfigValue<UniSound> PM_SOUND_INCOMING = ConfigValue.create("Private_Messages.Sound.Incoming",
        UniSound.of(Sound.BLOCK_NOTE_BLOCK_BELL),
        "Sets sound for incoming private messages."
    );

    public static final ConfigValue<UniSound> PM_SOUND_OUTGOING = ConfigValue.create("Private_Messages.Sound.Outgoing",
        UniSound.of(Sound.UI_BUTTON_CLICK),
        "Sets sound for outgoing private messages."
    );

    public static final ConfigValue<String> PM_FORMAT_INCOMING = ConfigValue.create("Private_Messages.Format.Incoming",
        LIGHT_PURPLE.enclose("[Whisper]") + " " + LIGHT_PINK.enclose(PLAYER_DISPLAY_NAME + ": ") + LIGHT_GRAY.enclose(GENERIC_MESSAGE) + " " +
            CLICK.enclose(
                HOVER.encloseHint(LIGHT_PURPLE.enclose("[⬅]"), LIGHT_PURPLE.enclose("Click to reply!")),
                ClickEvent.Action.SUGGEST_COMMAND, "/" + PrivateMessageCommands.DEF_PM_ALIAS + " " + PLAYER_NAME
            ),
        "Format for incoming private messages.",
        "Use " + GENERIC_MESSAGE + " placeholder for a message text.",
        "Player name placeholders: " + PLAYER_NAME + ", " + PLAYER_DISPLAY_NAME,
        "Text Formations: " + WIKI_TEXT_URL
    );

    public static final ConfigValue<String> PM_FORMAT_OUTGOING = ConfigValue.create("Private_Messages.Format.Outgoing",
        LIGHT_PURPLE.enclose("[Whisper]") + " " + LIGHT_PINK.enclose("You ➡ " + PLAYER_DISPLAY_NAME + ": ") + LIGHT_GRAY.enclose(GENERIC_MESSAGE),
        "Format for outgoing private messages.",
        "Use " + GENERIC_MESSAGE + " placeholder for a message text.",
        "Player name placeholders: " + PLAYER_NAME + ", " + PLAYER_DISPLAY_NAME,
        "Text Formations: " + WIKI_TEXT_URL
    );


    public static final ConfigValue<Boolean> MENTIONS_ENABLED = ConfigValue.create("Mentions.Enabled",
        true,
        "When 'true', enables the Mentions feature.",
        "Mentions allows you to attract attention of certain players or players with certain ranks when typing their name/rank in chat.",
        "(!) IMPORTANT: You players must have " + ChatPerms.MENTION.getName() + " permission or " + ChatPerms.MENTION_PLAYER + "[playerName] or " + ChatPerms.MENTION_SPECIAL + "[mentionName] permissions."
    );

    public static final ConfigValue<Integer> MENTIONS_MAXIMUM = ConfigValue.create("Mentions.Max_Per_Message",
        3,
        "Sets the maximum amount of mentions per player message.",
        "When there is more mentions than max allowed, all other mentions will have no effect.",
        "Set this to -1 for unlimit."
    );

    public static final ConfigValue<Integer> MENTIONS_COOLDOWN = ConfigValue.create("Mentions.Cooldown",
        15,
        "Sets per player cooldown for the same mentions.",
        "When mention is on cooldown, it will have no effect.",
        "Set this to -1 to disable."
    );

    public static final ConfigValue<String> MENTIONS_PREFIX = ConfigValue.create("Mentions.Prefix",
        "@",
        "A prefix that mention have to be followed by to work.",
        "With default '@' mention will be: '@UserName'."
    );

//    public static final ConfigValue<String> MENTIONS_PATTERN = ConfigValue.create("Mentions.Pattern",
//        "@+[A-Za-z0-9_]+",
//        "Sets mention regex pattern used to find and replace mentions in message.");

    public static final ConfigValue<String> MENTIONS_FORMAT = ConfigValue.create("Mentions.Format",
        LIGHT_GREEN.enclose("@" + PLAYER_DISPLAY_NAME),
        "A text that will replace mention if player is valid.",
        "Player name placeholders: " + PLAYER_NAME + ", " + PLAYER_DISPLAY_NAME,
        "Text Formations: " + WIKI_TEXT_URL,
        "PlaceholderAPI is supported here."
    );

    public static final ConfigValue<Map<String, GroupMention>> MENTIONS_SPECIAL = ConfigValue.forMap("Mentions.Special",
        (cfg, path, id) -> GroupMention.read(cfg, path + "." + id, id),
        (cfg, path, map) -> map.forEach((id, mention) -> mention.write(cfg, path + "." + id)),
        () -> Map.of(
            "all", new GroupMention("all", CYAN.enclose("@All"), Lists.newSet(Placeholders.WILDCARD)),
            "admin", new GroupMention("admin", LIGHT_RED.enclose("@Admin"), Lists.newSet("admin"))
        ),
        "A list of custom mentions, that can be applied to multiple players based on their permission group.",
        "Use asterisk (*) to include all permission groups.",
        "Keys are mentions. Defaults are '@all' and '@admin'.",
        "Text Formations: " + WIKI_TEXT_URL
    );


    public static final ConfigValue<Boolean> SPY_ENABLED = ConfigValue.create("SpyOps.Enabled",
        true,
        "Enables the SpyOps feature.",
        "It will add commands to enable spy mode for Private Messages, Commands and regular Chat messages."
    );

    public static final ConfigValue<Map<SpyType, String>> SPY_FORMAT = ConfigValue.forMap("SpyOps.Format",
        (raw) -> StringUtil.getEnum(raw, SpyType.class).orElse(null),
        (cfg, path, type) -> cfg.getString(path + "." + type, ""),
        (cfg, path, map) -> map.forEach((type, format) -> cfg.set(path + "." + type.name(), format)),
        () -> {
            Map<SpyType, String> map = new HashMap<>();
            map.put(SpyType.SOCIAL, LIGHT_RED.enclose("[SocialSpy]") + " " + GRAY.enclose(PLAYER_NAME + " ➡ " + GENERIC_TARGET + ":") + " " + LIGHT_GRAY.enclose(GENERIC_MESSAGE));
            map.put(SpyType.COMMAND, LIGHT_RED.enclose("[CommandSpy]") + " " + GRAY.enclose(PLAYER_NAME + " executed a command:") + " " + LIGHT_GRAY.enclose(GENERIC_MESSAGE));
            map.put(SpyType.CHAT, LIGHT_RED.enclose("[ChatSpy]") + " " + GRAY.enclose("[" + CHANNEL_NAME + "]" + " " + PLAYER_NAME + ":") + " " + LIGHT_GRAY.enclose(GENERIC_MESSAGE));
            return map;
        },
        "Sets the format for each Spy Mode.",
        "Use " + GENERIC_MESSAGE + " placeholder for a message text.",
        "For '" + SpyType.SOCIAL.name() + "' Mode, you can use " + GENERIC_TARGET + " placeholder.",
        "For '" + SpyType.CHAT.name() + "' Mode, you can use 'Channel' placeholders.",
        "Player name placeholders: " + PLAYER_NAME + ", " + PLAYER_DISPLAY_NAME,
        "Text Formations: " + WIKI_TEXT_URL
    );


    public static final ConfigValue<Boolean> ANTI_CAPS_ENABLED = ConfigValue.create("Anti_Caps.Enabled",
        true,
        "When 'true', enables the AntiCaps auto-moderation feature.",
        "This feature will prevent players from sending full caps messages."
    );

    public static final ConfigValue<Integer> ANTI_CAPS_MESSAGE_LENGTH_MIN = ConfigValue.create("Anti_Caps.Message_Length_Min",
        10,
        "Minimal message length for the AntiCaps feature to check it.",
        "This option might be useful to prevent AntiCaps trigger on short messages like 'LOL', 'OMG', etc."
    );

    public static final ConfigValue<Integer> ANTI_CAPS_UPPER_LETTER_PERCENT_MIN = ConfigValue.create("Anti_Caps.Upper_Letters_Percent_Min",
        75,
        "Minimal percentage of upper-case letters in a message for AntiCaps to handle it.",
        "This option might be useful to prevent AntiCaps trigger on messages with many upper-case letters without bad intentions."
    );

    public static final ConfigValue<Set<String>> ANTI_CAPS_AFFECTED_COMMANDS = ConfigValue.create("Anti_Caps.Affected_Commands",
        Lists.newSet("tell", "me", "reply", "broadcast"),
        "List of commands, that will be checked by AntiCaps for voilations.",
        "This option might be useful to prevent caps messages in private messages, broadcasts, etc."
    );

    public static final ConfigValue<Set<String>> ANTI_CAPS_IGNORED_WORDS = ConfigValue.create("Anti_Caps.Ignored_Words",
        Lists.newSet("OMG", "LOL", "WTF", "IMHO", "WOW", "ROFL", "AHAHA", "DAMN"),
        "List of words, that will be skipped when AntiCaps is checking a message.",
        "These words won't count into message length and upper-case letters amount."
    );

    public static final ConfigValue<Boolean> ANTI_SPAM_ENABLED = ConfigValue.create("Anti_Spam.Enabled",
        true,
        "When 'true', enables the AntiSpam auto-moderation feature.",
        "This feature will prevent players from spamming messages/commands."
    );

    public static final ConfigValue<Double> ANTI_SPAM_BLOCK_SIMILAR_PERCENT = ConfigValue.create("Anti_Spam.Block_Similar_Messages.Percentage",
        90D,
        "How many (in percent) previous and current player message/command should match each other for the AntiSpam to trigger?",
        "Set this to 0 to disable."
    );

    public static final ConfigValue<Integer> ANTI_SPAM_BLOCK_SIMILAR_COOLDOWN = ConfigValue.create("Anti_Spam.Block_Similar_Messages.Cooldown",
        3,
        "For how long (in seconds) previous player message/command will be stored to compare it with next ones.",
        "Set this to 0 to disable."
    );

    public static final ConfigValue<Double> ANTI_SPAM_COMMAND_COOLDOWN = ConfigValue.create("Anti_Spam.Command_Cooldown",
        1.5D,
        "Sets the cooldown between ALL player commands.",
        "For a chat message cooldown, check the channels config.",
        "Set this to 0 to disable."
    );

    public static final ConfigValue<Set<String>> ANTI_SPAM_COMMAND_WHITELIST = ConfigValue.create("Anti_Spam.Command_Whitelist",
        Lists.newSet("tell", "reply", "spawn", "home", "warp", "sethome", "delhome", "kit"),
        "A list of commands, that will be completely excluded from the AntiSpam checks."
    );


    public static final ConfigValue<Boolean> ITEM_SHOW_ENABLED = ConfigValue.create("Item_Show.Enabled",
        true,
        "With Item Showcase feature enabled, players can show their items in chat and Private Messages."
    );

    public static final ConfigValue<String> ITEM_SHOW_PLACEHOLDER = ConfigValue.create("Item_Show.Placeholder",
        "@hand",
        "Keyword used to display item in hand in chat or Private Messages."
    );

    public static final ConfigValue<String> ITEM_SHOW_FORMAT = ConfigValue.create("Item_Show.Format.Chat",
        GRAY.enclose("<" + WHITE.enclose(HOVER.enclose(ITEM_NAME, HoverEvent.Action.SHOW_ITEM, ITEM_VALUE)) + ">"),
        "Item Showcase format.",
        "Text Formations: " + WIKI_TEXT_URL,
        "Placeholders:",
        "- " + ITEM_NAME + " - Item display name.",
        "- " + ITEM_VALUE + " - Item NBT value."
    );

    @Nullable
    public static GroupMention getSpecialMention(@NotNull String id) {
        return ChatConfig.MENTIONS_SPECIAL.get().get(id.toLowerCase());
    }
}
