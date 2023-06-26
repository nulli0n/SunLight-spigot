package su.nightexpress.sunlight.module.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.Version;
import su.nexmedia.engine.command.CommandRegister;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.utils.*;
import su.nexmedia.engine.utils.regex.RegexUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.api.event.PlayerPrivateMessageEvent;
import su.nightexpress.sunlight.config.Config;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.hook.HookId;
import su.nightexpress.sunlight.hook.impl.DiscordSrvHook;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.chat.command.BroadcastCommand;
import su.nightexpress.sunlight.module.chat.command.ClearchatCommand;
import su.nightexpress.sunlight.module.chat.command.MeCommand;
import su.nightexpress.sunlight.module.chat.command.ShortChannelCommand;
import su.nightexpress.sunlight.module.chat.command.channel.ChatChannelCommand;
import su.nightexpress.sunlight.module.chat.command.spy.ChatSpyCommand;
import su.nightexpress.sunlight.module.chat.command.tell.ReplyCommand;
import su.nightexpress.sunlight.module.chat.command.tell.TellCommand;
import su.nightexpress.sunlight.module.chat.config.ChatConfig;
import su.nightexpress.sunlight.module.chat.config.ChatLang;
import su.nightexpress.sunlight.module.chat.config.ChatMention;
import su.nightexpress.sunlight.module.chat.config.ChatPlayerFormat;
import su.nightexpress.sunlight.module.chat.event.AsyncSunlightPlayerChatEvent;
import su.nightexpress.sunlight.module.chat.listener.ChatListener;
import su.nightexpress.sunlight.module.chat.module.ChatAnnounceManager;
import su.nightexpress.sunlight.module.chat.module.ChatDeathManager;
import su.nightexpress.sunlight.module.chat.module.ChatJoinManager;
import su.nightexpress.sunlight.module.chat.rule.ChatRuleManager;
import su.nightexpress.sunlight.module.chat.util.ChatReportDisabler;
import su.nightexpress.sunlight.module.chat.util.ChatSpyType;
import su.nightexpress.sunlight.module.chat.util.ChatUtils;
import su.nightexpress.sunlight.module.chat.util.Placeholders;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ChatModule extends Module {

    private static Pattern mentionsPattern;

    private ChatDeathManager    deathManager;
    private ChatJoinManager     joinManager;
    private ChatRuleManager     ruleManager;
    private ChatAnnounceManager announceManager;

    public ChatModule(@NotNull SunLight plugin, @NotNull String id) {
        super(plugin, id);
    }

    @Override
    protected void onLoad() {
        this.plugin.registerPermissions(ChatPerms.class);
        this.plugin.getLangManager().loadMissing(ChatLang.class);
        this.plugin.getLangManager().setupEnum(ChatSpyType.class);
        this.plugin.getLang().saveChanges();
        this.getConfig().initializeOptions(ChatConfig.class);
        ChatChannel.loadChannels(this);

        if (ChatConfig.MENTIONS_ENABLED.get()) {
            mentionsPattern = Pattern.compile(ChatConfig.MENTIONS_PREFIX.get() + "+.*?\\S+");
        }

        if (ChatConfig.MODULE_JOIN_QUIT_MESSAGES.get()) {
            this.joinManager = new ChatJoinManager(this);
            this.joinManager.setup();
        }
        if (ChatConfig.MODULE_DEATH_MESSAGES.get()) {
            this.deathManager = new ChatDeathManager(this);
            this.deathManager.setup();
        }
        if (ChatConfig.MODULE_ANNOUNCER.get()) {
            this.announceManager = new ChatAnnounceManager(this);
            this.announceManager.setup();
        }

        this.ruleManager = new ChatRuleManager(this);
        this.ruleManager.setup();

        this.plugin.getCommandRegulator().register(ClearchatCommand.NAME, (cfg1, aliases) -> new ClearchatCommand(this.plugin, aliases));
        this.plugin.getCommandRegulator().register(ChatChannelCommand.NAME, (cfg1, aliases) -> new ChatChannelCommand(this, aliases));
        this.getChannels().forEach(channel -> {
            this.plugin.getCommandManager().registerCommand(new ShortChannelCommand(this, channel));
        });
        this.plugin.getCommandRegulator().register(MeCommand.NAME, (cfg1, aliases) -> new MeCommand(this, cfg1, aliases));
        this.plugin.getCommandRegulator().register(BroadcastCommand.NAME, (cfg1, aliases) -> new BroadcastCommand(this.plugin, aliases), "bc");
        if (ChatConfig.PM_ENABLED.get()) {
            this.plugin.getCommandRegulator().register(TellCommand.NAME, (cfg1, aliases) -> new TellCommand(this.plugin, aliases), "t", "pm", "whisper", "w", "dm", "msg");
            this.plugin.getCommandRegulator().register(ReplyCommand.NAME, (cfg1, aliases) -> new ReplyCommand(this.plugin, aliases), "r");
        }
        if (ChatConfig.SPY_ENABLED.get()) {
            this.plugin.getCommandRegulator().register(ChatSpyCommand.NAME, (cfg1, aliases) -> new ChatSpyCommand(this.plugin, aliases), "spy");
        }

        if (ChatConfig.DISABLE_CHAT_REPORTS.get() && Hooks.hasPlugin(HookId.PROTOCOL_LIB) && Version.isAbove(Version.V1_18_R2)) {
            ChatReportDisabler.setup(this.plugin);
        }

        this.addListener(new ChatListener(this));
    }

    @Override
    protected void onShutdown() {
        if (ChatConfig.DISABLE_CHAT_REPORTS.get() && Hooks.hasPlugin(HookId.PROTOCOL_LIB)) {
            ChatReportDisabler.shutdown(this.plugin);
        }

        if (this.announceManager != null) {
            this.announceManager.shutdown();
            this.announceManager = null;
        }
        if (this.deathManager != null) {
            this.deathManager.shutdown();
            this.deathManager = null;
        }
        if (this.joinManager != null) {
            this.joinManager.shutdown();
            this.joinManager = null;
        }
        if (this.ruleManager != null) {
            this.ruleManager.shutdown();
            this.ruleManager = null;
        }
        ChatChannel.clear();
    }

    @Nullable
    public static ChatPlayerFormat getPlayerFormat(@NotNull Player player) {
        Set<String> ranks = Hooks.getPermissionGroups(player);
        Map<String, ChatPlayerFormat> map = ChatConfig.FORMAT_PLAYER.get();

        return map.entrySet().stream()
            .filter(entry -> ranks.contains(entry.getKey()) || entry.getKey().equalsIgnoreCase(Placeholders.DEFAULT))
            .map(Map.Entry::getValue).max(Comparator.comparingInt(ChatPlayerFormat::getPriority)).orElse(null);
    }

    @Nullable
    public static ChatMention getMentionSpecial(@NotNull String id) {
        return ChatConfig.MENTIONS_SPECIAL.get().get(id.toLowerCase());
    }

    @NotNull
    public Collection<ChatChannel> getChannels() {
        return ChatChannel.CHANNELS.values();
    }

    @Nullable
    public ChatChannel getChannel(@NotNull String id) {
        return ChatChannel.CHANNELS.get(id.toLowerCase());
    }

    @NotNull
    public ChatChannel getChannel(@NotNull Player player) {
        ChatChannel chatChannel = this.getChannel(ChatChannel.getChannelActiveId(player));
        if (chatChannel == null) chatChannel = this.getChannelDefault();

        return chatChannel;
    }

    @NotNull
    public ChatChannel getChannelDefault() {
        return ChatChannel.DEFAULT_CHANNEL;
    }

    @NotNull
    public Set<ChatChannel> getChannelsAvailable(@NotNull Player player) {
        return this.getChannels().stream().filter(channel -> channel.canHear(player)).collect(Collectors.toSet());
    }

    @Nullable
    public ChatChannel getChannelByPrefix(@NotNull String message) {
        return this.getChannels().stream()
            .filter(channel -> !channel.getMessagePrefix().isEmpty() && message.startsWith(channel.getMessagePrefix()))
            .findFirst().orElse(null);
    }

    @Nullable
    public ChatChannel getChannelByCommand(@NotNull String command) {
        return this.getChannels().stream().filter(channel -> !channel.getCommandAlias().equalsIgnoreCase(command))
            .findFirst().orElse(null);
    }

    @NotNull
    public Set<Player> getChannelPlayers(@NotNull ChatChannel channel) {
        return plugin.getServer().getOnlinePlayers().stream()
            .filter(player -> channel.canSpeak(player) || channel.canHear(player))
            .filter(player -> ChatChannel.getChannels(player).contains(channel.getId()))
            .collect(Collectors.toSet());
    }

    @NotNull
    public Collection<Player> getChannelRecipients(@NotNull Player speaker, @NotNull ChatChannel channel) {
        return this.getChannelPlayers(channel).stream()
            .filter(recipient -> channel.isInRadius(speaker, recipient)).collect(Collectors.toSet());
    }

    public boolean setChannel(@NotNull Player player, @NotNull ChatChannel channel) {
        if (!channel.canSpeak(player)) {
            this.plugin.getMessage(ChatLang.Channel_Join_Error_NoPermission).replace(channel.replacePlaceholders()).send(player);
            return false;
        }
        if (!this.isInChannel(player, channel)) {
            this.joinChannel(player, channel);
        }
        ChatChannel.setChannelActiveId(player, channel);
        this.plugin.getMessage(ChatLang.Channel_Set_Success).replace(channel.replacePlaceholders()).send(player);
        return true;
    }

    public boolean isInChannel(@NotNull Player player, @NotNull ChatChannel channel) {
        return ChatChannel.getChannels(player).contains(channel.getId());
    }

    public boolean joinChannel(@NotNull Player player, @NotNull ChatChannel channel) {
        return this.joinChannel(player, channel, false);
    }

    public boolean joinChannel(@NotNull Player player, @NotNull ChatChannel channel, boolean isSilent) {
        if (!channel.canHear(player) && !channel.canSpeak(player)) {
            if (!isSilent) this.plugin.getMessage(ChatLang.Channel_Join_Error_NoPermission).replace(channel.replacePlaceholders()).send(player);
            return false;
        }

        if (ChatChannel.getChannels(player).add(channel.getId())) {
            if (!isSilent) this.plugin.getMessage(ChatLang.Channel_Join_Success).replace(channel.replacePlaceholders()).send(player);
            return true;
        }

        if (!isSilent) this.plugin.getMessage(ChatLang.Channel_Join_Error_AlreadyIn).replace(channel.replacePlaceholders()).send(player);
        return false;
    }

    public boolean leaveChannel(@NotNull Player player, @NotNull ChatChannel channel) {
        if (ChatChannel.getChannels(player).remove(channel.getId())) {
            this.plugin.getMessage(ChatLang.Channel_Leave_Success).replace(channel.replacePlaceholders()).send(player);
            return true;
        }

        this.plugin.getMessage(ChatLang.Channel_Leave_Error_NotIn).replace(channel.replacePlaceholders()).send(player);
        return false;
    }

    public void handleChatEvent(@NotNull AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        SunUser user = plugin.getUserManager().getUserData(player);

        // Get permission group based player format.
        ChatPlayerFormat playerFormat = getPlayerFormat(player);
        if (playerFormat == null) {
            this.error("Could not handle message from '" + player.getName() + "': No player format is available!");
            e.setCancelled(true);
            return;
        }

        // Apply message colors for permission.
        String msgReal = player.hasPermission(ChatPerms.COLOR) ? Colorizer.legacyHex(e.getMessage()) : Colorizer.restrip(e.getMessage());

        // Strip all non-expected Json elements, aka avoid hacking.
        msgReal = MessageUtil.stripJson(msgReal);
        // Remove all unallowed characters to prevent JSON chat breaking.
        if (ChatConfig.CHAT_JSON.get()) {
            msgReal = ChatUtils.legalizeMessage(msgReal);
        }

        // Check message caps percentage.
        if (!player.hasPermission(ChatPerms.BYPASS_ANTICAPS)) {
            msgReal = ChatUtils.doAntiCaps(msgReal);
        }

        // Check for the spam of the similar messages.
        if (!player.hasPermission(ChatPerms.BYPASS_ANTISPAM) && !ChatUtils.checkSpamSimilarMessage(player, msgReal)) {
            this.plugin.getMessage(ChatLang.Chat_AntiSpam_Similar_Msg).send(player);
            e.setCancelled(true);
            return;
        }

        ChatChannel channelPrefix = this.getChannelByPrefix(Colorizer.strip(msgReal));
        ChatChannel channelActive = this.getChannel(ChatChannel.getChannelActiveId(player));
        if (channelPrefix != null && channelPrefix.canSpeak(player)) {
            channelActive = channelPrefix;
        }
        if (channelActive == null || !channelActive.canSpeak(player)) {
            channelActive = this.getChannelDefault();
        }

        // Check for the channel message cooldown.
        if (!player.hasPermission(ChatPerms.BYPASS_COOLDOWN_MESSAGE) && !ChatUtils.isNextMessageAvailable(player, channelActive)) {
            String time = TimeUtil.formatTimeLeft(ChatUtils.getNextMessageTime(player, channelActive));
            this.plugin.getMessage(ChatLang.Chat_AntiSpam_Delay_Msg).replace("%time%", time).send(player);
            e.setCancelled(true);
            return;
        }

        // Join the channel, where player is sending message, if it wasn't there.
        if (!this.isInChannel(player, channelActive)) {
            this.joinChannel(player, channelActive);
        }

        // Extract message without the channel prefix.
        if (msgReal.startsWith(channelActive.getMessagePrefix())) {
            msgReal = msgReal.substring(channelActive.getMessagePrefix().length()).trim();
        }

        // Do no send empty messages.
        if (StringUtil.noSpace(Colorizer.strip(msgReal)).isEmpty()) {
            e.setCancelled(true);
            return;
        }
        // Check for custom regex rules.
        if (!player.hasPermission(ChatPerms.BYPASS_RULES)) {
            msgReal = this.ruleManager.checkRules(player, msgReal, Colorizer.strip(msgReal));
            if (msgReal == null) {
                e.setCancelled(true);
                return;
            }
        }

        // Retain recipients for the channel.
        e.getRecipients().retainAll(this.getChannelRecipients(player, channelActive));

        // Apply mentions if present.
        Set<Player> toMention = new HashSet<>();
        if (ChatConfig.MENTIONS_ENABLED.get()) {
            Matcher matcher = RegexUtil.getMatcher(mentionsPattern, msgReal);
            int count = 0;
            int max = player.hasPermission(ChatPerms.BYPASS_MENTION_AMOUNT) ? -1 : ChatConfig.MENTIONS_MAXIMUM.get();
            Set<Player> channelPlayers = e.getRecipients();
            while (RegexUtil.matcherFind(matcher) && (max < 0 || count++ < max)) {
                String mentionFull = matcher.group(0);
                String mentionName = mentionFull.substring(ChatConfig.MENTIONS_PREFIX.get().length());
                String mentionFormat;

                if (!ChatUtils.isMentionRefreshed(player, mentionName)) {
                    plugin.getMessage(ChatLang.CHAT_MENTION_ERROR_COOLDOWN)
                        .replace(Placeholders.GENERIC_TIME, TimeUtil.formatTimeLeft(ChatUtils.getNextMentionTimestamp(player, mentionName)))
                        .replace(Placeholders.GENERIC_NAME, mentionName)
                        .send(player);
                    continue;
                }

                ChatMention mention = getMentionSpecial(mentionName);
                if (mention != null) {
                    if (!player.hasPermission(ChatPerms.MENTION) && !player.hasPermission(ChatPerms.MENTION_SPECIAL + mentionName)) {
                        continue;
                    }
                    toMention.addAll(channelPlayers.stream().filter(mention::isApplicable).collect(Collectors.toSet()));
                    mentionFormat = mention.getFormat();
                }
                else {
                    if (!player.hasPermission(ChatPerms.MENTION) && !player.hasPermission(ChatPerms.MENTION_PLAYER + mentionName)) {
                        continue;
                    }
                    Player mentioned = channelPlayers.stream().filter(p -> p.getName().equalsIgnoreCase(mentionName)).findFirst().orElse(null);
                    if (mentioned == null) continue;
                    toMention.add(mentioned);
                    mentionFormat = Placeholders.Player.replacer(mentioned).apply(ChatConfig.MENTIONS_FORMAT.get());
                }
                msgReal = msgReal.replace(mentionFull, mentionFormat);

                if (!player.hasPermission(ChatPerms.BYPASS_MENTION_COOLDOWN)) {
                    ChatUtils.setMentionCooldown(player, mentionName);
                }
            }
        }

        // Apply Item Showcase if present.
        if (ChatConfig.ITEM_SHOW_ENABLED.get() && msgReal.contains(ChatConfig.ITEM_SHOW_PLACEHOLDER.get())) {
            ItemStack item = player.getInventory().getItemInMainHand();
            String itemFormat = ChatConfig.ITEM_SHOW_FORMAT_CHAT.get()
                .replace(Placeholders.ITEM_NAME, ItemUtil.getItemName(item))
                .replace(Placeholders.ITEM_VALUE, String.valueOf(ItemUtil.toBase64(item)));
            msgReal = msgReal.replace(ChatConfig.ITEM_SHOW_PLACEHOLDER.get(), itemFormat);
        }

        String message = playerFormat.prepareMessage(player, msgReal);
        String format = playerFormat.prepareFormat(player, channelActive.getFormat());

        e.setMessage(MessageUtil.toSimpleText(message));
        e.setFormat(MessageUtil.toSimpleText(format.replace(Placeholders.GENERIC_MESSAGE, "%2$s")));

        AsyncSunlightPlayerChatEvent event = new AsyncSunlightPlayerChatEvent(player, channelActive, e.getRecipients(), message, format);
        plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            e.setCancelled(true);
            return;
        }

        if (ChatConfig.CHAT_JSON.get()) {
            String finalFormat = event.getFinalFormat();
            e.setCancelled(true);
            event.getRecipients().forEach(receiver -> MessageUtil.sendWithJson(receiver, finalFormat));
            MessageUtil.sendWithJson(this.plugin.getServer().getConsoleSender(), MessageUtil.toSimpleText(finalFormat));
        }

        DiscordSrvHook.sendMessageToMain(event.getPlayer().getDisplayName() + ": " + ChatColor.stripColor(msgReal));

        toMention.forEach(mentioned -> ChatConfig.MENTIONS_NOTIFY.get().replace(Placeholders.Player.replacer(player)).send(mentioned));

        if (!player.hasPermission(ChatPerms.BYPASS_ANTISPAM)) {
            ChatUtils.setLastMessage(player, e.getMessage());
        }
        if (!player.hasPermission(ChatPerms.BYPASS_COOLDOWN_MESSAGE)) {
            ChatUtils.setNextMessageTime(player, channelActive);
        }
    }

    public void handleCommandEvent(@NotNull PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();

        // Check for command cooldown.
        if (!player.hasPermission(ChatPerms.BYPASS_COOLDOWN_COMMAND) && !ChatUtils.isNextCommandAvailable(player)) {
            String time = TimeUtil.formatTimeLeft(ChatUtils.getNextCommandTime(player));
            this.plugin.getMessage(ChatLang.Chat_AntiSpam_Delay_Cmd).replace("%time%", time).send(player);
            e.setCancelled(true);
            return;
        }

        String msgRaw = Colorizer.strip(e.getMessage());
        String msgCmd = StringUtil.extractCommandName(msgRaw);
        String msgReal = player.hasPermission(ChatPerms.COLOR) ? Colorizer.apply(e.getMessage()) : msgRaw;

        // Check command similarity.
        boolean doCheckSimilar = ChatConfig.ANTI_SPAM_ENABLED.get() && CommandRegister.getAliases(msgCmd, true).stream().noneMatch(cmd -> {
            return ChatConfig.ANTI_SPAM_COMMAND_WHITELIST.get().contains(cmd);
        });
        if (!player.hasPermission(ChatPerms.BYPASS_ANTISPAM) && doCheckSimilar &&
            !ChatUtils.checkSpamSimilarCommand(player, msgRaw)) {
            this.plugin.getMessage(ChatLang.Chat_AntiSpam_Similar_Cmd).send(player);
            e.setCancelled(true);
            return;
        }

        // Check for caps in command.
        boolean doCheckCaps = ChatConfig.ANTI_CAPS_ENABLED.get() && CommandRegister.getAliases(msgCmd, true).stream().anyMatch(cmd -> {
            return ChatConfig.ANTI_CAPS_AFFECTED_COMMANDS.get().contains(cmd);
        });
        if (!player.hasPermission(ChatPerms.BYPASS_ANTICAPS) && doCheckCaps) {
            msgReal = ChatUtils.doAntiCaps(msgReal);
        }

        // Check for chat regex rules.
        if (!player.hasPermission(ChatPerms.BYPASS_RULES)) {
            String msgRawNoCmd = msgRaw.replace(msgCmd, "");
            String msgRealNoCmd = msgReal.replace(msgCmd, "");

            String msgRealNoCmdRuled = this.ruleManager.checkRules(player, msgRealNoCmd, msgRawNoCmd);
            if (msgRealNoCmdRuled == null) {
                e.setCancelled(true);
                return;
            }
            msgReal = msgReal.replace(msgRealNoCmd, msgRealNoCmdRuled);
        }

        e.setMessage(msgReal);

        if (!player.hasPermission(ChatPerms.BYPASS_ANTISPAM)) {
            ChatUtils.setLastCommand(player, msgRaw);
        }
        if (!player.hasPermission(ChatPerms.BYPASS_COOLDOWN_COMMAND)) {
            ChatUtils.setNextCommandTime(player);
        }
    }

    @NotNull
    public Set<Player> getSpies(@NotNull ChatSpyType spyType) {
        return this.plugin.getServer().getOnlinePlayers().stream()
            .filter(player -> plugin.getUserManager().getUserData(player).getSettings().get(spyType.getSettingChat()))
            .collect(Collectors.toSet());
    }

    @NotNull
    private String getSpyFormat(@NotNull ChatSpyType spyType, @NotNull CommandSender player, @NotNull String message) {
        String format = ChatConfig.SPY_FORMAT.get().getOrDefault(spyType, "")
            .replace(Placeholders.GENERIC_MESSAGE, message);
        format = Placeholders.Player.replacer(player).apply(format);
        return format;
    }

    public void handleSpyMode(@NotNull AsyncSunlightPlayerChatEvent event) {
        String format = this.getSpyFormat(ChatSpyType.CHAT, event.getPlayer(), event.getMessage());
        format = event.getChannel().replacePlaceholders().apply(format);
        this.handleSpyMode(event.getPlayer(), format, ChatSpyType.CHAT, (spy) -> !event.getChannel().isInRadius(event.getPlayer(), spy));
        this.handleSpyLog(event.getPlayer(), format, ChatSpyType.CHAT);
    }

    public void handleSpyMode(@NotNull PlayerCommandPreprocessEvent event) {
        String format = this.getSpyFormat(ChatSpyType.COMMAND, event.getPlayer(), event.getMessage());
        this.handleSpyMode(event.getPlayer(), format, ChatSpyType.COMMAND, null);
        this.handleSpyLog(event.getPlayer(), format, ChatSpyType.COMMAND);
    }

    public void handleSpyMode(@NotNull PlayerPrivateMessageEvent event) {
        String format = this.getSpyFormat(ChatSpyType.SOCIAL, event.getSender(), event.getMessage())
            .replace("%player_target%", event.getTarget().getName());
        this.handleSpyMode(event.getSender(), format, ChatSpyType.SOCIAL, null);
        this.handleSpyLog(event.getSender(), format, ChatSpyType.SOCIAL);
    }

    private void handleSpyMode(@NotNull CommandSender sender, @NotNull String format, @NotNull ChatSpyType spyType, @Nullable Predicate<Player> spyFilter) {
        this.getSpies(spyType).stream()
            .filter(spy -> !spy.equals(sender) && (spyFilter == null || spyFilter.test(spy)))
            .forEach(spy -> MessageUtil.sendWithJson(spy, format));
    }

    public void handleSpyLog(@NotNull CommandSender sender, @NotNull String format, @NotNull ChatSpyType spyType) {
        if (!(sender instanceof Player player)) return;

        SunUser user = plugin.getUserManager().getUserData(player);
        if (!user.getSettings().get(spyType.getSettingLog())) return;

        this.plugin.info(format);
        String date = Config.GENERAL_DATE_FORMAT.get().format(TimeUtil.toEpochMillis(LocalDateTime.now()));
        String outFile = "[" + date + "] " + Colorizer.strip(format);
        String filePath = this.getAbsolutePath() + "/spy_logs/" + player.getName() + "_" + spyType.name().toLowerCase() + ".log";
        FileUtil.create(new File(filePath));

        this.plugin.runTask(c -> {
            BufferedWriter output;
            try {
                output = new BufferedWriter(new FileWriter(filePath, true));
                output.append(outFile);
                output.newLine();
                output.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }, true);
    }
}
