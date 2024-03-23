package su.nightexpress.sunlight.module.chat.handler;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.*;
import su.nexmedia.engine.utils.message.NexParser;
import su.nexmedia.engine.utils.regex.RegexUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.chat.ChatChannel;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.config.*;
import su.nightexpress.sunlight.module.chat.event.AsyncSunlightPlayerChatEvent;
import su.nightexpress.sunlight.module.chat.util.ChatUtils;
import su.nightexpress.sunlight.module.chat.util.Placeholders;

import java.util.HashSet;
import java.util.Set;
import java.util.UnknownFormatConversionException;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class ChatMessageHandler {

    private final SunLight             plugin;
    private final ChatModule           module;
    private final AsyncPlayerChatEvent chatEvent;
    private final Player               player;
    //private final SunUser              user;
    private final Set<Player>          mentioned;

    private String           message;
    private ChatChannel      channel;
    private ChatPlayerFormat playerFormat;
    private boolean          colorsAllowed;
    private boolean          antiCapsEnabled;
    private boolean          antiSpamEnabled;
    private boolean          checkChannelCooldown;
    private boolean          checkChatRules;

    public ChatMessageHandler(@NotNull ChatModule module, @NotNull AsyncPlayerChatEvent event) {
        this.plugin = module.plugin();
        this.module = module;
        this.chatEvent = event;
        this.player = event.getPlayer();
        //this.user = plugin.getUserManager().getUserData(player);
        this.mentioned = new HashSet<>();
        this.setMessage(event.getMessage());

        this.setColorsAllowed(this.player.hasPermission(ChatPerms.COLOR));
        this.setAntiCapsEnabled(!this.player.hasPermission(ChatPerms.BYPASS_ANTICAPS));
        this.setAntiSpamEnabled(!this.player.hasPermission(ChatPerms.BYPASS_ANTISPAM));
        this.setCheckChannelCooldown(!this.player.hasPermission(ChatPerms.BYPASS_COOLDOWN_MESSAGE));
        this.setCheckChatRules(!this.player.hasPermission(ChatPerms.BYPASS_RULES));
    }

    public boolean handle() {
        if (!this.detectFormat()) return false;
        if (!this.detectChannel()) return false;

        this.applyColors();

        if (!this.checkEmptyMessage()) return false;
        if (!this.checkChannelCooldown()) return false;
        if (!this.doAntiCaps()) return false;
        if (!this.doAntiSpam()) return false;
        if (!this.checkRules()) return false;

        if (!this.getChannel().contains(this.getPlayer())) {
            this.module.getChannelManager().joinChannel(player, this.getChannel(), true);
        }
        this.chatEvent.getRecipients().retainAll(this.getChannel().getRecipients(this.player));

        this.applyMentions();
        this.applyItemShowcase();

        return this.send();
    }

    private boolean detectChannel() {
        if (this.channel != null) return true;

        ChatChannel channelPrefix = this.module.getChannelManager().getChannelByPrefix(this.getMessage());
        ChatChannel channelActive = this.module.getChannelManager().getActiveChannel(this.getPlayer());
        if (channelPrefix != null && channelPrefix.canSpeak(this.player)) {
            channelActive = channelPrefix;
        }
        if (!channelActive.canSpeak(this.player)) {
            channelActive = this.module.getChannelManager().getDefaultChannel();
        }
        this.channel = channelActive;

        // Remove channel prefix if present.
        if (this.message.startsWith(this.channel.getMessagePrefix())) {
            this.message = this.message.substring(this.channel.getMessagePrefix().length()).trim();
        }

        return true;
    }

    private boolean detectFormat() {
        if (this.playerFormat != null) return true;

        this.playerFormat = ChatModule.getPlayerFormat(this.player);
        if (this.playerFormat == null) {
            this.module.error("Could not handle message from '" + player.getName() + "': No player format is available!");
            return false;
        }
        return true;
    }

    private void applyColors() {
        if (!this.isColorsAllowed()) {
            this.message = Colorizer.restrip(this.message);
            return;
        }

        this.message = Colorizer.legacyHex(this.message);
    }

    private boolean doAntiCaps() {
        if (!this.isAntiCapsEnabled()) return true;

        this.message = ChatUtils.doAntiCaps(this.message);
        return true;
    }

    private boolean doAntiSpam() {
        if (!this.isAntiSpamEnabled()) return true;
        if (ChatUtils.checkSpamSimilarMessage(this.player, this.message)) return true;

        this.plugin.getMessage(ChatLang.ANTI_SPAM_SIMILAR_MSG).send(player);
        return false;
    }

    private boolean checkEmptyMessage() {
        return !StringUtil.noSpace(Colorizer.strip(this.message)).isEmpty();
    }

    private boolean checkChannelCooldown() {
        if (!this.isCheckChannelCooldown()) return true;
        if (ChatUtils.isNextMessageAvailable(this.player, this.getChannel())) return true;

        String time = TimeUtil.formatTimeLeft(ChatUtils.getNextMessageTime(this.player, this.getChannel()));
        this.plugin.getMessage(ChatLang.ANTI_SPAM_DELAY_MSG).replace(Placeholders.GENERIC_TIME, time).send(player);
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

        Matcher matcher = RegexUtil.getMatcher(ChatModule.mentionsPattern, this.message);
        int count = 0;

        Set<Player> channelPlayers = this.chatEvent.getRecipients();
        while (RegexUtil.matcherFind(matcher) && (max < 0 || count++ < max)) {
            String mentionFull = matcher.group(0);
            String mentionName = mentionFull.substring(ChatConfig.MENTIONS_PREFIX.get().length());
            String mentionFormat;

            if (!ChatUtils.isMentionRefreshed(player, mentionName)) {
                plugin.getMessage(ChatLang.MENTION_ERROR_COOLDOWN)
                    .replace(Placeholders.GENERIC_TIME, TimeUtil.formatTimeLeft(ChatUtils.getNextMentionTimestamp(player, mentionName)))
                    .replace(Placeholders.GENERIC_NAME, mentionName)
                    .send(player);
                continue;
            }

            ChatMention mention = ChatModule.getMentionSpecial(mentionName);
            if (mention != null) {
                if (!player.hasPermission(ChatPerms.MENTION) && !player.hasPermission(ChatPerms.MENTION_SPECIAL + mentionName)) {
                    continue;
                }
                this.getMentioned().addAll(channelPlayers.stream().filter(mention::isApplicable).collect(Collectors.toSet()));
                mentionFormat = mention.getFormat();
            }
            else {
                if (!player.hasPermission(ChatPerms.MENTION) && !player.hasPermission(ChatPerms.MENTION_PLAYER + mentionName)) {
                    continue;
                }
                Player mentioned = channelPlayers.stream().filter(p -> p.getName().equalsIgnoreCase(mentionName)).findFirst().orElse(null);
                if (mentioned == null || !ChatUtils.isMentionsEnabled(mentioned)) continue;

                this.getMentioned().add(mentioned);
                mentionFormat = Placeholders.forPlayer(mentioned).apply(ChatConfig.MENTIONS_FORMAT.get());
            }
            this.message = this.message.replace(mentionFull, mentionFormat);

            if (!player.hasPermission(ChatPerms.BYPASS_MENTION_COOLDOWN)) {
                ChatUtils.setMentionCooldown(player, mentionName);
            }
        }
    }

    private void applyItemShowcase() {
        if (!ChatConfig.ITEM_SHOW_ENABLED.get()) return;
        if (!this.message.contains(ChatConfig.ITEM_SHOW_PLACEHOLDER.get())) return;

        ItemStack item = this.player.getInventory().getItemInMainHand();
        String itemFormat = ChatConfig.ITEM_SHOW_FORMAT_CHAT.get()
            .replace(Placeholders.ITEM_NAME, ItemUtil.getItemName(item))
            .replace(Placeholders.ITEM_VALUE, String.valueOf(ItemUtil.compress(item)));

        this.message = this.message.replace(ChatConfig.ITEM_SHOW_PLACEHOLDER.get(), itemFormat);
    }

    private boolean send() {
        String message = this.getFormat().prepareMessage(this.player, this.message);
        String format = this.getFormat().prepareFormat(this.player, this.getChannel().getFormat());

        try {
            if (ChatConfig.CHAT_JSON.get()) {
                this.chatEvent.setMessage(NexParser.toPlainText(message));
                this.chatEvent.setFormat(NexParser.toPlainText(format.replace(Placeholders.GENERIC_MESSAGE, "%2$s")));
            } else {
                this.chatEvent.setMessage(Colorizer.apply(NexParser.toPlainText(message)));
                this.chatEvent.setFormat(Colorizer.apply(NexParser.toPlainText(format.replace(Placeholders.GENERIC_MESSAGE, "%2$s"))));
            }
        } catch (UnknownFormatConversionException exception) {
            this.plugin.error("Could not set chat format due to bad formation string. Here are what we get:");
            this.plugin.error("--- Message: " + NexParser.toPlainText(message));
            this.plugin.error("--- Format: " + NexParser.toPlainText(format));
            this.plugin.error("Stacktrace for developers:");
            exception.printStackTrace();
        }

        AsyncSunlightPlayerChatEvent event = new AsyncSunlightPlayerChatEvent(this.player, this.getChannel(), chatEvent.getRecipients(), message, format);
        plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        if (ChatConfig.CHAT_JSON.get()) {
            String finalFormat = event.getFinalFormat();
            event.getRecipients().forEach(receiver -> PlayerUtil.sendRichMessage(receiver, finalFormat));
            event.getRecipients().clear();
            if (ChatConfig.CHAT_JSON_ECHO.get()) {
                PlayerUtil.sendRichMessage(this.plugin.getServer().getConsoleSender(), NexParser.toPlainText(finalFormat));
            }
        }

        this.getMentioned().forEach(mentioned -> {
            ChatConfig.MENTIONS_NOTIFY.get().replace(Placeholders.forPlayer(this.player)).send(mentioned);
        });

        if (this.isAntiSpamEnabled()) {
            ChatUtils.setLastMessage(this.player, this.chatEvent.getMessage());
        }
        if (this.isCheckChannelCooldown()) {
            ChatUtils.setNextMessageTime(this.player, this.getChannel());
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
    public ChatPlayerFormat getFormat() {
        return playerFormat;
    }

    public void setFormat(@NotNull ChatPlayerFormat format) {
        this.playerFormat = format;
    }

    @NotNull
    public String getMessage() {
        return message;
    }

    public void setMessage(@NotNull String message) {
        // Strip all non-expected Json elements, aka avoid hacking.
        this.message = NexParser.toPlainText(message);
        // Remove all unallowed characters to prevent JSON chat breaking.
        if (ChatConfig.CHAT_JSON.get()) {
            this.message = ChatUtils.legalizeMessage(this.message);
        }
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
