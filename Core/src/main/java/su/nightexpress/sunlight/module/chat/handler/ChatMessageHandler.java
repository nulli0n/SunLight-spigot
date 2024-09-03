package su.nightexpress.sunlight.module.chat.handler;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.nightcore.util.Colorizer;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.nightcore.util.text.tag.TagPool;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.module.chat.ChatChannel;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.PlayerChatData;
import su.nightexpress.sunlight.module.chat.config.ChatConfig;
import su.nightexpress.sunlight.module.chat.config.ChatLang;
import su.nightexpress.sunlight.module.chat.config.ChatPerms;
import su.nightexpress.sunlight.module.chat.discord.DiscordHandler;
import su.nightexpress.sunlight.module.chat.event.AsyncSunlightPlayerChatEvent;
import su.nightexpress.sunlight.module.chat.format.FormatContainer;
import su.nightexpress.sunlight.module.chat.mention.Mention;
import su.nightexpress.sunlight.module.chat.util.ChatUtils;
import su.nightexpress.sunlight.module.chat.util.Placeholders;
import su.nightexpress.sunlight.utils.SunUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UnknownFormatConversionException;

public class ChatMessageHandler {

    private final SunLightPlugin       plugin;
    private final ChatModule           module;
    private final AsyncPlayerChatEvent chatEvent;
    private final Player               player;
    private final PlayerChatData       chatData;
    private final Set<Player>          mentioned;

    private String          message;
    private ChatChannel     channel;
    private FormatContainer formatContainer;
    private boolean         colorsAllowed;
    private boolean         antiCapsEnabled;
    private boolean         antiSpamEnabled;
    private boolean         checkChannelCooldown;
    private boolean         checkChatRules;

    public ChatMessageHandler(@NotNull SunLightPlugin plugin, @NotNull ChatModule module, @NotNull AsyncPlayerChatEvent event) {
        this.plugin = plugin;
        this.module = module;
        this.chatEvent = event;
        this.player = event.getPlayer();
        this.chatData = module.getChatData(this.player);
        this.mentioned = new HashSet<>();

        this.setColorsAllowed(this.player.hasPermission(ChatPerms.COLOR));
        this.setAntiCapsEnabled(ChatUtils.isAntiCapsEnabled() && !this.player.hasPermission(ChatPerms.BYPASS_ANTICAPS));
        this.setAntiSpamEnabled(ChatUtils.isAntiSpamEnabled() && !this.player.hasPermission(ChatPerms.BYPASS_ANTISPAM));
        this.setCheckChannelCooldown(!this.player.hasPermission(ChatPerms.BYPASS_COOLDOWN_MESSAGE));
        this.setCheckChatRules(!this.player.hasPermission(ChatPerms.BYPASS_RULES));

        this.setMessage(event.getMessage());
    }

    public void setMessage(@NotNull String message) {
        TagPool tagPool;

        if (this.isColorsAllowed()) {
            tagPool = TagPool.BASE_COLORS_AND_STYLES;
        }
        else {
            message = Colorizer.restrip(message);
            tagPool = TagPool.NONE;
        }

        this.message = NightMessage.stripTags(message, tagPool);
    }

    public boolean handle() {
        if (this.isEmptyMessage()) return false;
        if (!this.findFormat()) return false;

        this.detectChannel();

        if (!this.checkChannelCooldown()) return false;

        this.doAntiCaps();

        if (!this.checkAntiSpam()) return false;
        if (!this.checkRules()) return false;

        if (!this.getChannel().contains(this.getPlayer())) {
            this.module.getChannelManager().joinChannel(player, this.getChannel(), true);
        }
        this.chatEvent.getRecipients().retainAll(this.getChannel().getRecipients(this.player));
        this.applyItemShowcase();
        this.applyMentions();

        return this.send();
    }

    private boolean isEmptyMessage() {
        return (Colorizer.strip(NightMessage.asLegacy(this.message))).isBlank();
    }

    private boolean findFormat() {
        if (this.formatContainer != null) return true;

        this.formatContainer = this.module.getFormatContainer(this.player);

        if (this.formatContainer == null) {
            this.module.error("Could not handle message from '" + player.getName() + "': No format is available!");
            return false;
        }
        return true;
    }

    private void detectChannel() {
        if (this.channel != null) return;

        ChatChannel channelByPrefix = this.module.getChannelManager().getChannelByPrefix(this.message);
        ChatChannel activeChannel = this.module.getChannelManager().getActiveChannel(this.player);
        if (channelByPrefix != null && channelByPrefix.canSpeak(this.player)) {
            activeChannel = channelByPrefix;
        }
        if (!activeChannel.canSpeak(this.player)) {
            activeChannel = this.module.getChannelManager().getDefaultChannel();
        }
        this.channel = activeChannel;

        // Remove channel prefix if present.
        if (this.message.startsWith(this.channel.getMessagePrefix())) {
            this.message = this.message.substring(this.channel.getMessagePrefix().length()).trim();
        }
    }

    private boolean checkChannelCooldown() {
        if (!this.isCheckChannelCooldown()) return true;
        if (!this.chatData.hasMessageCooldown(this.channel)) return true;

        String time = TimeUtil.formatDuration(this.chatData.getNextMessageTimestamp(this.channel));
        ChatLang.ANTI_SPAM_DELAY_MESSAGE.getMessage().replace(Placeholders.GENERIC_TIME, time).send(this.player);
        return false;
    }

    private void doAntiCaps() {
        if (!this.isAntiCapsEnabled()) return;

        this.message = ChatUtils.doAntiCaps(this.message);
    }

    private boolean checkAntiSpam() {
        if (!this.isAntiSpamEnabled()) return true;
        if (!this.chatData.isSpamMessage(this.message)) return true;

        ChatLang.ANTI_SPAM_SIMILAR_MESSAGE.getMessage().send(this.player);
        return false;
    }

    private boolean checkRules() {
        if (!this.isCheckChatRules()) return true;

        this.message = this.module.getRuleManager().checkRules(this.player, this.message, Colorizer.strip(this.message));
        return this.message != null;
    }

    private void applyMentions() {
        if (!ChatConfig.MENTIONS_ENABLED.get()) return;

        int max = player.hasPermission(ChatPerms.BYPASS_MENTION_AMOUNT) ? -1 : ChatConfig.MENTIONS_MAXIMUM.get();
        if (max == 0) return;

        int count = 0;

        String prefix = ChatConfig.MENTIONS_PREFIX.get();
        Set<Player> channelPlayers = this.chatEvent.getRecipients();

        StringBuilder builder = new StringBuilder();
        int length = this.message.length();

        int lastIndex = 0;
        while (true) {
            if (max >= 0 && count >= max) break;

            int findIndex = this.message.indexOf(prefix, lastIndex);
            if (findIndex == -1) break;

            builder.append(this.message, lastIndex, findIndex);

            StringBuilder mentionBuilder = new StringBuilder();
            for (int index = findIndex; index < length; index++) {
                char letter = this.message.charAt(index);
                if (Character.isWhitespace(letter)) {
                    lastIndex = index;
                    break;
                }

                mentionBuilder.append(letter);
                lastIndex = index + 1;
            }

            // Do not search mentions for a single '@'
            if (mentionBuilder.length() == 1) {
                builder.append(mentionBuilder);
                continue;
            }

            String mentionFull = mentionBuilder.toString();
            String mentionName = mentionFull.substring(prefix.length());

            if (this.chatData.hasMentionCooldown(mentionName)) {
                ChatLang.MENTION_ERROR_COOLDOWN.getMessage()
                    .replace(Placeholders.GENERIC_TIME, TimeUtil.formatDuration(this.chatData.getNextMentionTimestamp(mentionName)))
                    .replace(Placeholders.GENERIC_NAME, mentionName)
                    .send(player);
                continue;
            }

            Mention mention = this.module.getMention(mentionName);
            if (mention != null && mention.hasPermission(this.player)) {
                mention.getAffectedPlayers(this.channel).forEach(target -> {
                    SunUser targetUser = this.plugin.getUserManager().getUserData(target);
                    if (targetUser.getSettings().get(ChatModule.MENTIONS_SETTING)) {
                        this.mentioned.add(target);
                    }
                });

                builder.append(mention.getFormat());
                count++;
            }
            else builder.append(mentionFull);

            if (!this.player.hasPermission(ChatPerms.BYPASS_MENTION_COOLDOWN)) {
                this.chatData.setMentionCooldown(mentionName);
            }
        }

        builder.append(this.message.substring(lastIndex));

        this.message = builder.toString();
    }

    private void applyItemShowcase() {
        if (ChatUtils.containsItemLink(this.message)) {
            ItemStack item = this.player.getInventory().getItemInMainHand();
            this.message = ChatUtils.appendItemComponent(this.message, item);
        }
//        if (!ChatConfig.ITEM_SHOW_ENABLED.get()) return;
//        if (!this.message.contains(ChatConfig.ITEM_SHOW_PLACEHOLDER.get())) return;
//
//        ItemStack item = ChatUtils.getLiteCopy(this.player.getInventory().getItemInMainHand());
//        String itemFormat = ChatConfig.ITEM_SHOW_FORMAT_CHAT.get()
//            .replace(Placeholders.ITEM_NAME, ItemUtil.getItemName(item))
//            .replace(Placeholders.ITEM_VALUE, String.valueOf(ItemNbt.compress(item)));
//
//        this.message = this.message.replace(ChatConfig.ITEM_SHOW_PLACEHOLDER.get(), itemFormat);
    }

    @NotNull
    private String replaceComponents(@NotNull String string) {
        return Placeholders.forComponents(this.module.getFormatComponents()).replacer().apply(string);
    }

    @NotNull
    private String prepareFormat() {
        String format = this.replaceComponents(this.channel.getFormat().replace(Placeholders.GENERIC_FORMAT, this.formatContainer.getNameFormat()))
            .replace(Placeholders.PLAYER_PREFIX, Players.getPrefix(player))
            .replace(Placeholders.PLAYER_SUFFIX, Players.getSuffix(player))
            .replace(Placeholders.PLAYER_DISPLAY_NAME, player.getDisplayName())
            .replace(Placeholders.PLAYER_NAME, player.getName())
            .replace(Placeholders.PLAYER_WORLD, LangAssets.get(player.getWorld()));

        if (Plugins.hasPlaceholderAPI()) {
            format = PlaceholderAPI.setPlaceholders(player, format);
        }

        return SunUtils.oneSpace(format);
    }

    @NotNull
    private String prepareMessage() {
        String format = this.replaceComponents(this.formatContainer.getMessageFormat());

        if (Plugins.hasPlaceholderAPI()) {
            format = PlaceholderAPI.setPlaceholders(player, format);
        }

        return SunUtils.oneSpace(format.replace(Placeholders.GENERIC_MESSAGE, this.message));
    }

    private boolean send() {
        String message = this.prepareMessage();
        String format = this.prepareFormat();

        try {
            this.chatEvent.setMessage(NightMessage.asLegacy(message));
            this.chatEvent.setFormat(NightMessage.asLegacy(format.replace(Placeholders.GENERIC_MESSAGE, "%2$s")));
        }
        catch (UnknownFormatConversionException exception) {
            this.plugin.error("Could not set chat format due to bad formation string. Here are what we get:");
            this.plugin.error("--- Message: " + message);
            this.plugin.error("--- Format: " + format);
            this.plugin.error("Stacktrace for developers:");
            exception.printStackTrace();
        }

        AsyncSunlightPlayerChatEvent event = new AsyncSunlightPlayerChatEvent(this.player, this.channel, this.chatEvent.getRecipients(), message, format);
        plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        if (ChatConfig.USE_COMPONENTS.get()) {
            String finalFormat = event.getFinalFormat();
            event.getRecipients().forEach(receiver -> Players.sendModernMessage(receiver, finalFormat));
            event.getRecipients().clear();
        }
        else if (!Players.isReal(this.player)) {
            String finalFormat = event.getFinalFormat();
            event.getRecipients().forEach(receiver -> receiver.sendMessage(NightMessage.asLegacy(finalFormat)));
            event.getRecipients().clear();
        }

        DiscordHandler discordHandler = this.module.getDiscordHandler();
        if (discordHandler != null) {
            discordHandler.sendToChannel(this.channel, NightMessage.stripAll(event.getFinalFormat()));
        }

        this.getMentioned().forEach(mentioned -> {
            ChatLang.MENTION_NOTIFY.getMessage().replace(Placeholders.forPlayer(this.player)).send(mentioned);
        });

        if (this.isAntiSpamEnabled()) {
            this.chatData.setLastMessage(this.chatEvent.getMessage());
        }
        if (this.isCheckChannelCooldown()) {
            this.chatData.setMessageCooldown(this.channel);
        }
        return true;
    }

    @NotNull
    public ChatModule getModule() {
        return module;
    }

    @NotNull
    public AsyncPlayerChatEvent getChatEvent() {
        return chatEvent;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public ChatChannel getChannel() {
        return channel;
    }

    public void setChannel(@NotNull ChatChannel channel) {
        this.channel = channel;
    }

    @NotNull
    public FormatContainer getFormat() {
        return formatContainer;
    }

    public void setFormat(@NotNull FormatContainer format) {
        this.formatContainer = format;
    }

    @NotNull
    public String getMessage() {
        return message;
    }

    public boolean isColorsAllowed() {
        return colorsAllowed;
    }

    public void setColorsAllowed(boolean colorsAllowed) {
        this.colorsAllowed = colorsAllowed;
    }

    public boolean isAntiCapsEnabled() {
        return antiCapsEnabled;
    }

    public void setAntiCapsEnabled(boolean antiCapsEnabled) {
        this.antiCapsEnabled = antiCapsEnabled;
    }

    public boolean isAntiSpamEnabled() {
        return antiSpamEnabled;
    }

    public void setAntiSpamEnabled(boolean antiSpamEnabled) {
        this.antiSpamEnabled = antiSpamEnabled;
    }

    public boolean isCheckChannelCooldown() {
        return checkChannelCooldown;
    }

    public void setCheckChannelCooldown(boolean checkChannelCooldown) {
        this.checkChannelCooldown = checkChannelCooldown;
    }

    public boolean isCheckChatRules() {
        return checkChatRules;
    }

    public void setCheckChatRules(boolean checkChatRules) {
        this.checkChatRules = checkChatRules;
    }

    @NotNull
    public Set<Player> getMentioned() {
        return mentioned;
    }
}
