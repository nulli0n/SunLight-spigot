package su.nightexpress.sunlight.module.chat.core;

import static su.nightexpress.nightcore.util.placeholder.CommonPlaceholders.PLAYER_DISPLAY_NAME;
import static su.nightexpress.nightcore.util.placeholder.CommonPlaceholders.PLAYER_NAME;
import static su.nightexpress.nightcore.util.placeholder.CommonPlaceholders.PLAYER_PREFIX;
import static su.nightexpress.nightcore.util.placeholder.CommonPlaceholders.PLAYER_SUFFIX;
import static su.nightexpress.nightcore.util.placeholder.CommonPlaceholders.PLAYER_WORLD;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.GRAY;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.ITALIC;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.LIGHT_PURPLE;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.SHOW_ITEM;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.SHOW_TEXT;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.SOFT_AQUA;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.SOFT_GREEN;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.SOFT_PINK;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.SUGGEST_COMMAND;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.WHITE;
import static su.nightexpress.sunlight.SLPlaceholders.GENERIC_MESSAGE;
import static su.nightexpress.sunlight.SLPlaceholders.GENERIC_TARGET;
import static su.nightexpress.sunlight.module.chat.ChatPlaceholders.CHANNEL_NAME;
import static su.nightexpress.sunlight.module.chat.ChatPlaceholders.ITEM_NAME;
import static su.nightexpress.sunlight.module.chat.ChatPlaceholders.ITEM_VALUE;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.bukkit.Sound;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import su.nightexpress.nightcore.bridge.wrap.NightSound;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.configuration.AbstractConfig;
import su.nightexpress.nightcore.configuration.ConfigProperty;
import su.nightexpress.nightcore.configuration.ConfigType;
import su.nightexpress.nightcore.configuration.ConfigTypes;
import su.nightexpress.nightcore.util.CommandUtil;
import su.nightexpress.nightcore.util.Enums;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.sound.VanillaSound;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.hook.HookId;
import su.nightexpress.sunlight.module.chat.ChatDefaults;
import su.nightexpress.sunlight.module.chat.ChatFiles;
import su.nightexpress.sunlight.module.chat.format.FormatComponent;
import su.nightexpress.sunlight.module.chat.format.FormatDefinition;
import su.nightexpress.sunlight.module.chat.mention.GroupMention;
import su.nightexpress.sunlight.module.chat.spy.SpyType;

public class ChatSettings extends AbstractConfig {

    private static final ConfigType<FormatComponent> FORMAT_COMPONENT_CONFIG_TYPE = ConfigType.of(
        FormatComponent::read,
        FileConfig::set
    );

    private static final ConfigType<FormatDefinition> FORMAT_DEFINITION_CONFIG_TYPE = ConfigType.of(
        FormatDefinition::read,
        FileConfig::set
    );

    private static final ConfigType<GroupMention> GROUP_MENTION_CONFIG_TYPE = ConfigType.of(
        GroupMention::read,
        FileConfig::set
    );

    private final ConfigProperty<EventPriority> chatEventPriority = this.addProperty(ConfigTypes.forEnum(
        EventPriority.class), "Global.Chat-Event-Priority",
        EventPriority.HIGH,
        "Sets priority for the AsyncChatEvent (Paper) / AsyncPlayerChatEvent (Spigot) handler.",
        "[>] Available values: [" + Enums.inline(EventPriority.class) + "]",
        "[*] Do not touch unless you're experiencing compatibility issues or you know what you're doing."
    );

    private final ConfigProperty<Boolean> discordHookEnabled = this.addProperty(ConfigTypes.BOOLEAN,
        "Global.DiscordSRV.Enabled",
        true,
        "Enables %s integration.".formatted(HookId.DISCORD_SRV),
        "[*] Experimental. Currently supports only Game -> Discord messages."
    );

    private final ConfigProperty<Boolean> disableReports = this.addProperty(ConfigTypes.BOOLEAN,
        "Global.Disable-Chat-Reports",
        true,
        "Disables the Chat Reports system and verification messages on join."
    );

    private final ConfigProperty<Boolean> roleplayEnabled = this.addProperty(ConfigTypes.BOOLEAN,
        "Roleplay-Commands.Enabled",
        true,
        "Adds a few roleplay-like commands if enabled."
    );

    private final ConfigProperty<String> roleplayMeFormat = this.addProperty(ConfigTypes.STRING,
        "Roleplay-Commands.Me.Format",
        ITALIC.wrap(SOFT_AQUA.wrap(PLAYER_DISPLAY_NAME) + " " + GRAY.wrap(SLPlaceholders.GENERIC_MESSAGE)),
        "Format for the /me command."
    );

    private final ConfigProperty<Map<String, FormatComponent>> formatComponents = this.addProperty(ConfigTypes
        .forMapWithLowerKeys(FORMAT_COMPONENT_CONFIG_TYPE),
        "Chat.Format.Components",
        ChatDefaults.getDefaultFormatComponents(),
        "Here you can create custom 'components' to use in the user formats below.",
        "To insert a component into user format, wrap it into '%' brackets: %my_component%",
        "[>] Text Formations: " + SLPlaceholders.URL_WIKI_TEXT,
        "[>] %s Support: Yes".formatted(Plugins.PLACEHOLDER_API),
        "[>] Built-in Placeholders:",
        "- " + SLPlaceholders.GENERIC_MESSAGE + " - Original message sent by a player.",
        "- " + PLAYER_NAME + " - Player name.",
        "- " + PLAYER_DISPLAY_NAME + " - Player display (custom) name.",
        "- " + PLAYER_PREFIX + " - Player prefix (from Permissions plugin)",
        "- " + PLAYER_SUFFIX + " - Player suffix (from Permissions plugin)",
        "- " + PLAYER_WORLD + " - Player's world name."
    );

    private final ConfigProperty<Map<String, FormatDefinition>> formatDefinitions = this.addProperty(ConfigTypes
        .forMapWithLowerKeys(FORMAT_DEFINITION_CONFIG_TYPE),
        "Chat.Format.User",
        ChatDefaults.getDefaultFormats(),
        "Here you can create custom chat formats for players based on their permission group(s).",
        "[>] Options:",
        "    - Priority: If multiple formats available, the one with the greatest priority is used.",
        "    - Format: Chat message format. For a list of placeholders, please see the 'Format.Components' section above.",
        "    - Ranks: List of ranks (permission group names) to which this format is applicable. Add '%s' to allow any rank."
            .formatted(SLPlaceholders.WILDCARD),
        "[>] If no format is available for a player, fallbacks to the 'Format.Fallback' setting."
    );

    private final ConfigProperty<String> formatFallback = this.addProperty(ConfigTypes.STRING, "Chat.Format.Fallback",
        PLAYER_NAME + ": " + SLPlaceholders.GENERIC_MESSAGE,
        "Fallback chat message format used when no other format is available for a player.",
        "[*] Please don't rely much on this setting, instead ensure to setup your user formats properly."
    );

    private final ConfigProperty<Boolean> channelsEnabled = this.addProperty(ConfigTypes.BOOLEAN, "Channels.Enabled",
        true,
        "Enables the Channels feature.",
        "This allows you to create multiple chat channels with different formats, permissions, message cooldowns, etc.",
        "[*] When disabled, the chat fallbacks to the %s channel inside the %s directory and no other channel features are available to use."
            .formatted(ChatDefaults.DEFAULT_CHANNEL_ID, ChatFiles.DIR_CHANNELS)
    );

    private final ConfigProperty<String> channelsDefaultId = this.addProperty(ConfigTypes.STRING,
        "Channels.Default-Channel-Id",
        ChatDefaults.DEFAULT_LOCAL_CHANNEL_ID,
        "Sets which channel used by default for all player messages.",
        "[*] If invalid channel provided, fallbacks to the %s channel.".formatted(ChatDefaults.DEFAULT_CHANNEL_ID)
    );

    private final ConfigProperty<Boolean> channelNoHeardMessage = this.addProperty(ConfigTypes.BOOLEAN,
        "Channels.Nobody-Heard-Message", true,
        "Controls whether players will get the 'Nobody heard you' message when there is no one in the channel who can hear them."
    );

    private final ConfigProperty<Boolean> conversationEnabled = this.addProperty(ConfigTypes.BOOLEAN,
        "Conversations.Enabled",
        true,
        "Enables the Conversations feature allowing players to send private messages to each other."
    );

    private final ConfigProperty<Boolean> conversationSoundEnabled = this.addProperty(ConfigTypes.BOOLEAN,
        "Conversations.Sound.Enabled",
        true,
        "If enabled, a sound will play whenever a player receives or sends a private message."
    );

    private final ConfigProperty<NightSound> conversationSoundIncoming = this.addProperty(ConfigTypes.NIGHT_SOUND,
        "Conversations.Sound.Incoming",
        VanillaSound.of(Sound.BLOCK_NOTE_BLOCK_BELL),
        "Sound for incoming private messages."
    );

    private final ConfigProperty<NightSound> conversationSoundOutgoing = this.addProperty(ConfigTypes.NIGHT_SOUND,
        "Conversations.Sound.Outgoing",
        VanillaSound.of(Sound.UI_BUTTON_CLICK),
        "Sound for outgoing private messages."
    );

    private final ConfigProperty<String> conversationProxyFormat = this.addProperty(ConfigTypes.STRING,
        "Conversations.Format.Basic",
        SLPlaceholders.GENERIC_MESSAGE,
        "Conversation message format used in 'Incoming' and 'Outgoing' formats below.",
        "[>] Text Formations: " + SLPlaceholders.URL_WIKI_TEXT,
        "[>] %s Support: Yes".formatted(Plugins.PLACEHOLDER_API),
        "[>] Built-in Placeholders:",
        "- " + SLPlaceholders.GENERIC_MESSAGE + " - Original message sent by a player.",
        "- " + PLAYER_NAME + " - Player name.",
        "- " + PLAYER_DISPLAY_NAME + " - Player display (custom) name.",
        "- " + PLAYER_PREFIX + " - Player prefix (from Permissions plugin)",
        "- " + PLAYER_SUFFIX + " - Player suffix (from Permissions plugin)",
        "- " + PLAYER_WORLD + " - Player's world name."
    );

    private final ConfigProperty<String> conversationIncomingFormat = this.addProperty(ConfigTypes.STRING,
        "Conversations.Format.Incoming",
        LIGHT_PURPLE.wrap("[Whisper]") + " " + SOFT_PINK.wrap(PLAYER_DISPLAY_NAME + ": ") + GRAY.wrap(
            SLPlaceholders.GENERIC_FORMAT) + " " +
            SUGGEST_COMMAND.with("/" + ChatDefaults.DEFAULT_PM_ALIAS + " " + PLAYER_NAME).wrap(
                SHOW_TEXT.with(LIGHT_PURPLE.wrap("Click to reply!")).wrap(LIGHT_PURPLE.wrap("[⬅]"))
            ),
        "Format for incoming private messages.",
        "[>] Use the %s placeholder to insert 'basic' conversation format from the 'Format.Basic' section above"
            .formatted(SLPlaceholders.GENERIC_FORMAT),
        "[*] For a list of placeholders, please see the 'Format.Basic' section above."
    );

    private final ConfigProperty<String> conversationOutgoingFormat = this.addProperty(ConfigTypes.STRING,
        "Conversations.Format.Outgoing",
        LIGHT_PURPLE.wrap("[Whisper]") + " " + SOFT_PINK.wrap("You ➡ " + PLAYER_DISPLAY_NAME + ": ") + GRAY.wrap(
            SLPlaceholders.GENERIC_FORMAT),
        "Format for outgoing private messages.",
        "[>] Use the %s placeholder to insert 'basic' conversation format from the 'Format.Basic' section above"
            .formatted(SLPlaceholders.GENERIC_FORMAT),
        "[*] For a list of placeholders, please see the 'Format.Basic' section above."
    );


    private final ConfigProperty<Boolean> mentionsEnabled = this.addProperty(ConfigTypes.BOOLEAN, "Mentions.Enabled",
        true,
        "If enabled, this allows players to mention (tag) other players or entire groups in the chat."
    );

    private final ConfigValue<Integer> mentionsLimit = ConfigValue.create("Mentions.Limit-Per-Message",
        3,
        "This sets the maximum number of mentions (tags) allowed per message. Any additional mentions beyond this limit will not have any effect.",
        "[>] Set -1 for no limits."
    );

    private final ConfigValue<Integer> mentionsCooldown = ConfigValue.create("Mentions.Cooldown-Per-Mention",
        15,
        "Sets the cooldown time for each mention (tag).",
        "[>] Set -1 for to disable."
    );

    private final ConfigProperty<String> mentionsPattern = this.addProperty(ConfigTypes.STRING, "Mentions.Prefix",
        "@(\\w+)",
        "The regex pattern used to identify mentions (tags) in a player's message. By default, this is @Something."
    );

    private final ConfigProperty<String> mentionsFormat = this.addProperty(ConfigTypes.STRING, "Mentions.Player.Format",
        SOFT_GREEN.wrap("@" + PLAYER_DISPLAY_NAME),
        "Format for player mentions.",
        "[>] Text Formations: " + SLPlaceholders.URL_WIKI_TEXT,
        "[>] Built-in Placeholders:",
        "- " + PLAYER_NAME + " - Player name.",
        "- " + PLAYER_DISPLAY_NAME + " - Player display (custom) name.",
        "- " + PLAYER_PREFIX + " - Player prefix (from Permissions plugin)",
        "- " + PLAYER_SUFFIX + " - Player suffix (from Permissions plugin)",
        "- " + PLAYER_WORLD + " - Player's world name."
    );

    private final ConfigProperty<Map<String, GroupMention>> mentionsCustoms = this.addProperty(ConfigTypes.forMap(
        GROUP_MENTION_CONFIG_TYPE),
        "Mentions.Special",
        ChatDefaults.getDefaultMentions(),
        "Here you can create custom mentions that affects all players with certain ranks (permission groups).",
        "[>] Text Formations: " + SLPlaceholders.URL_WIKI_TEXT,
        "[>] Options:",
        "    - Format: Mention format.",
        "    - Ranks: List of ranks (permission group names) to which this mention is applicable. Add '%s' to allow any rank."
            .formatted(SLPlaceholders.WILDCARD)
    );

    private final ConfigProperty<Integer> autoModerationUserCacheLifetime = this.addProperty(ConfigTypes.INT,
        "AutoModeration.UserCache.Life-Time",
        60,
        "Sets cache lifetime (in seconds) for user content tracked by the Auto-Moderation feature.",
        "[>] This includes:",
        "- Latest message sent with timestamp.",
        "- Latest command used with timestamp.",
        "[*] Disabling the cache will break some of Auto-Moderation features (e.g. Anti-Flood)."
    );

    private final ConfigProperty<Boolean> antiCapsEnabled = this.addProperty(ConfigTypes.BOOLEAN,
        "AutoModeration.Anti-Caps.Enabled",
        true,
        "This feature forces a player's message to lowercase if the number of uppercase characters exceeds the specified threshold."
    );

    private final ConfigProperty<Integer> antiCapsLengthThreshold = this.addProperty(ConfigTypes.INT,
        "AutoModeration.Anti-Caps.Min-Message-Length",
        5,
        "The minimum message length required for the Anti-Caps feature to trigger."
    );

    private final ConfigProperty<Integer> antiCapsUpperCaseThreshold = this.addProperty(ConfigTypes.INT,
        "AutoModeration.Anti-Caps.Uppercase-Threshold",
        70,
        "The minimum threshold (in %) of uppercase characters in a message required for the Anti-Caps feature to trigger."
    );

    private final ConfigProperty<Set<String>> antiCapsAffectedCommands = this.addProperty(
        ConfigTypes.STRING_SET_LOWER_CASE, "AutoModeration.Anti-Caps.Affected-Commands",
        Set.of("tell", "me", "reply", "broadcast"),
        "List of commands for which the Anti-Caps feature will be active."
    );

    private final ConfigProperty<Boolean> antiFloodEnabled = this.addProperty(ConfigTypes.BOOLEAN,
        "AutoModeration.Anti-Flood.Enabled",
        true,
        "This feature prevents players from sending the same (or very similar) messages and commands within a (very) short period of time."
    );

    private final ConfigProperty<Double> antiFloodSimilarityScoreThreshold = this.addProperty(ConfigTypes.DOUBLE,
        "AutoModeration.Anti-Flood.Similarity.Score-Threshold",
        80D,
        "The similarity trigger threshold (in %) for the Anti-Flood feature regarding messages and commands.",
        "If the similarity percentage between a player's last message/command and the one they are trying to send meets or exceeds this value, the message will be blocked."
    );

    private final ConfigProperty<Integer> antiFloodSimilarityCountThreshold = this.addProperty(ConfigTypes.INT,
        "AutoModeration.Anti-Flood.Similarity.Count-Threshold",
        3,
        "The quantitative trigger threshold for the Anti-Flood feature for messages and commands",
        "If a player sends this specific number of similar messages or commands in a row within the time specified in 'UserCache.Life-Time', further similar messages will be blocked until the cache expires."
    );

    private final ConfigProperty<Long> antiFloodCommandCooldown = this.addProperty(ConfigTypes.LONG,
        "AutoModeration.Anti-Flood.Commands.Cooldown",
        500L,
        "Sets global cooldown (in milliseconds) on command usage..",
        "[Default is 500ms (0.5s)]"
    );

    private final ConfigProperty<Set<String>> antiFloodCommandWhitelist = this.addProperty(
        ConfigTypes.STRING_SET_LOWER_CASE, "AutoModeration.Anti-Flood.Command-Whitelist",
        Set.of("tell", "reply", "spawn", "home", "warp", "sethome", "delhome", "kit"),
        "List of commands excluded from the Anti-Flood feature, so players can use them without all the Anti-Flood checks and restrictions."
    );

    private final ConfigProperty<Boolean> profanityFilterEnabled = this.addProperty(ConfigTypes.BOOLEAN,
        "AutoModeration.Profanity-Filter.Enabled",
        true,
        "This feature enables a filter for profanity (or any specified words) in messages and commands."
    );

    private final ConfigProperty<Set<String>> profanityFilterRules = this.addProperty(ConfigTypes.STRING_SET_LOWER_CASE,
        "AutoModeration.Profanity-Filter.Rules",
        Set.of(ChatDefaults.DEFAULT_RULE_FILE_NAME),
        "The list of rule files for the Profanity Filter.",
        "[>] Rule files are stored in the %s folder.".formatted(ChatFiles.DIR_RULES),
        "[>] You can create your own files or modify existing ones."
    );

    private final ConfigProperty<Set<String>> profanityFilterAffectedCommands = this.addProperty(
        ConfigTypes.STRING_SET_LOWER_CASE, "AutoModeration.Profanity-Filter.Affected-Commands",
        Set.of("tell", "me", "reply", "broadcast"),
        "List of commands for which the Profanity-Filter feature will be active."
    );


    private final ConfigProperty<Boolean> itemShowEnabled = this.addProperty(ConfigTypes.BOOLEAN, "ItemShow.Enabled",
        true,
        "This feature allows players to use a placeholder to display the item they are currently holding in the chat."
    );

    private final ConfigProperty<String> itemShowPlaceholder = this.addProperty(ConfigTypes.STRING,
        "ItemShow.Placeholder",
        "@hand",
        "The placeholder used to display item in chat."
    );

    private final ConfigProperty<String> itemShowFormat = this.addProperty(ConfigTypes.STRING, "ItemShow.Format",
        GRAY.wrap("<" + WHITE.wrap(SHOW_ITEM.with(ITEM_VALUE).wrap(ITEM_NAME)) + ">"),
        "Item display format.",
        "[>] Text Formations: %s".formatted(SLPlaceholders.URL_WIKI_TEXT),
        "[>] Placeholders:",
        "- %s - Item display name.".formatted(ITEM_NAME),
        "- %s - Item NBT Tag (in Base64).".formatted(ITEM_VALUE)
    );

    private final ConfigProperty<Boolean> spyEnabled = this.addProperty(ConfigTypes.BOOLEAN, "Spy.Enabled",
        true,
        "This feature allows you to monitor player's chat messages, used commands and conversations."
    );

    private final ConfigProperty<Map<SpyType, String>> spyFormat = this.addProperty(ConfigTypes.forMap(string -> Enums
        .get(string, SpyType.class), Enum::name, ConfigTypes.STRING),
        "Spy.Info-Format",
        ChatDefaults.getDefaultSpyFormat(),
        "Format for Spy Modes.",
        "[>] Text Formations: %s".formatted(SLPlaceholders.URL_WIKI_TEXT),
        "[>] Placeholders:",
        "- %s - Player message/command.".formatted(GENERIC_MESSAGE),
        "- %s - Target player name (for %s mode only).".formatted(GENERIC_TARGET, SpyType.SOCIAL.name()),
        "- %s - Channel name (for %s mode only).".formatted(CHANNEL_NAME, SpyType.CHAT.name()),
        "- " + PLAYER_NAME + " - Player name.",
        "- " + PLAYER_DISPLAY_NAME + " - Player display (custom) name.",
        "- " + PLAYER_PREFIX + " - Player prefix (from Permissions plugin)",
        "- " + PLAYER_SUFFIX + " - Player suffix (from Permissions plugin)",
        "- " + PLAYER_WORLD + " - Player's world name."
    );

    @NonNull
    public EventPriority getChatEventPriority() {
        return this.chatEventPriority.get();
    }

    public boolean getReportsDisable() {
        return this.disableReports.get();
    }

    public boolean isDiscordHookEnabled() {
        return this.discordHookEnabled.get();
    }

    public int getUserContentCacheLifetime() {
        return this.autoModerationUserCacheLifetime.get();
    }


    public boolean isRoleplayCommandEnabled() {
        return this.roleplayEnabled.get();
    }

    @NonNull
    public String getRoleplayMeFormat() {
        return this.roleplayMeFormat.get();
    }


    @NonNull
    public Map<String, FormatComponent> getFormatComponents() {
        return this.formatComponents.get();
    }

    @NonNull
    public Map<String, FormatDefinition> getFormatDefinitions() {
        return this.formatDefinitions.get();
    }

    @NonNull
    public String getFormatFallback() {
        return this.formatFallback.get();
    }

    public boolean isChannelsEnabled() {
        return this.channelsEnabled.get();
    }

    @NonNull
    public String getChannelDefaultId() {
        return this.channelsDefaultId.get();
    }

    public boolean isChannelNoHeardMessageEnabled() {
        return this.channelNoHeardMessage.get();
    }

    public boolean isAntiCapsEnabled() {
        return this.antiCapsEnabled.get();
    }

    public int getAntiCapsLengthThreshold() {
        return this.antiCapsLengthThreshold.get();
    }

    public int getAntiCapsUpperCaseThreshold() {
        return this.antiCapsUpperCaseThreshold.get();
    }

    public boolean isAntiCapsBlacklistedCommand(@NonNull String command) {
        return this.isCommandInList(command, this.antiCapsAffectedCommands.get());
    }

    public boolean isAntiFloodEnabled() {
        return this.antiFloodEnabled.get();
    }

    public double getAntiFloodSimilarityScoreThreshold() {
        return this.antiFloodSimilarityScoreThreshold.get();
    }

    public int getAntiFloodSimilarityCountThreshold() {
        return this.antiFloodSimilarityCountThreshold.get();
    }

    public long getAntiFloodCommandCooldown() {
        return this.antiFloodCommandCooldown.get();
    }

    public boolean isAntiFloodWhitelistedCommand(@NonNull String command) {
        return this.isCommandInList(command, this.antiFloodCommandWhitelist.get());
    }


    public boolean getProfanityFilterEnabled() {
        return this.profanityFilterEnabled.get();
    }

    @NonNull
    public Set<String> getProfanityFilterRules() {
        return this.profanityFilterRules.get();
    }


    public boolean isConversationsEnabled() {
        return this.conversationEnabled.get();
    }

    @NonNull
    public String getConversationProxyFormat() {
        return this.conversationProxyFormat.get();
    }

    @NonNull
    public String getConversationIncomingFormat() {
        return this.conversationIncomingFormat.get();
    }

    @NonNull
    public String getConversationOutgoingFormat() {
        return this.conversationOutgoingFormat.get();
    }

    public boolean isConversationSoundsEnabled() {
        return this.conversationSoundEnabled.get();
    }

    @NonNull
    public NightSound getConversationIncomingSound() {
        return this.conversationSoundIncoming.get();
    }

    @NonNull
    public NightSound getConversationOutgoingSound() {
        return this.conversationSoundOutgoing.get();
    }


    public boolean isMentionsEnabled() {
        return this.mentionsEnabled.get();
    }

    @NonNull
    public String getMentionsPattern() {
        return this.mentionsPattern.get();
    }

    public int getMentionsLimit() {
        return this.mentionsLimit.get();
    }

    public int getMentionsCooldown() {
        return this.mentionsCooldown.get();
    }

    @NonNull
    public String getMentionsFormat() {
        return this.mentionsFormat.get();
    }

    @NonNull
    public Map<String, GroupMention> getCustomMentions() {
        return this.mentionsCustoms.get();
    }

    public boolean isItemShowEnabled() {
        return this.itemShowEnabled.get();
    }

    @NonNull
    public String getItemShowPlaceholder() {
        return this.itemShowPlaceholder.get();
    }

    @NonNull
    public String getItemShowFormat() {
        return this.itemShowFormat.get();
    }

    public boolean isSpyEnabled() {
        return this.spyEnabled.get();
    }

    @Nullable
    public String getSpyModeFormat(@NonNull SpyType type) {
        return this.spyFormat.get().get(type);
    }

    public boolean isProfanityFilterAffectedCommand(@NonNull String command) {
        return this.isCommandInList(command, this.profanityFilterAffectedCommands.get());
    }

    private boolean isCommandInList(@NonNull String command, @NonNull Collection<String> collection) {
        return CommandUtil.getAliases(command, true).stream().anyMatch(collection::contains);
    }
}
