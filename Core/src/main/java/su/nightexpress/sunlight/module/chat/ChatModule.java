package su.nightexpress.sunlight.module.chat;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.Version;
import su.nexmedia.engine.command.CommandRegister;
import su.nexmedia.engine.utils.*;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.api.event.PlayerPrivateMessageEvent;
import su.nightexpress.sunlight.config.Config;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.hook.HookId;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.chat.command.BroadcastCommand;
import su.nightexpress.sunlight.module.chat.command.ClearchatCommand;
import su.nightexpress.sunlight.module.chat.command.MeCommand;
import su.nightexpress.sunlight.module.chat.command.MentionsCommand;
import su.nightexpress.sunlight.module.chat.command.channel.ChatChannelCommand;
import su.nightexpress.sunlight.module.chat.command.pm.ReplyCommand;
import su.nightexpress.sunlight.module.chat.command.pm.TellCommand;
import su.nightexpress.sunlight.module.chat.command.pm.TogglePMCommand;
import su.nightexpress.sunlight.module.chat.command.spy.ChatSpyCommand;
import su.nightexpress.sunlight.module.chat.config.*;
import su.nightexpress.sunlight.module.chat.event.AsyncSunlightPlayerChatEvent;
import su.nightexpress.sunlight.module.chat.event.SunlightPreHandleChatEvent;
import su.nightexpress.sunlight.module.chat.handler.ChatMessageHandler;
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
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ChatModule extends Module {

    public static Pattern mentionsPattern;

    private ChannelManager channelManager;
    private ChatDeathManager deathManager;
    private ChatJoinManager joinManager;
    private ChatRuleManager ruleManager;
    private ChatAnnounceManager announceManager;

    public ChatModule(@NotNull SunLight plugin, @NotNull String id) {
        super(plugin, id);
    }

    protected void onLoad() {
        this.plugin.registerPermissions(ChatPerms.class);
        this.plugin.getLangManager().loadMissing(ChatLang.class);
        this.plugin.getLangManager().loadEnum(ChatSpyType.class);
        this.plugin.getLang().saveChanges();
        this.getConfig().initializeOptions(ChatConfig.class);

        this.channelManager = new ChannelManager(this.plugin, this);
        this.channelManager.setup();

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

        this.plugin.getCommandRegulator().register("clearchat", (cfg, aliases) -> new ClearchatCommand(this.plugin, aliases));
        this.plugin.getCommandRegulator().register("chatchannel", (cfg, aliases) -> new ChatChannelCommand(this, aliases));
        this.plugin.getCommandRegulator().register("me", (cfg, aliases) -> new MeCommand(this, cfg, aliases));
        this.plugin.getCommandRegulator().register("broadcast", (cfg, aliases) -> new BroadcastCommand(this.plugin, aliases), "bc");
        if (ChatConfig.PM_ENABLED.get()) {
            this.plugin.getCommandRegulator().register("tell", (cfg, aliases) -> new TellCommand(this.plugin, aliases), "t", "pm", "whisper", "w", "dm", "msg");
            this.plugin.getCommandRegulator().register("reply", (cfg, aliases) -> new ReplyCommand(this.plugin, aliases), "r");
            this.plugin.getCommandRegulator().register("togglepm", (cfg, aliases) -> new TogglePMCommand(this.plugin, aliases));
        }
        if (ChatConfig.SPY_ENABLED.get()) {
            this.plugin.getCommandRegulator().register("chatspy", (cfg, aliases) -> new ChatSpyCommand(this.plugin, aliases), "spy");
        }
        if (ChatConfig.MENTIONS_ENABLED.get()) {
            this.plugin.getCommandRegulator().register("mentions", (cfg, aliases) -> new MentionsCommand(this.plugin, aliases));
        }
        if (ChatConfig.DISABLE_CHAT_REPORTS.get() && EngineUtils.hasPlugin("ProtocolLib") && Version.isAbove(Version.V1_18_R2)) {
            ChatReportDisabler.setup(this.plugin);
        }

        this.addListener(new ChatListener(this));
    }

    protected void onShutdown() {
        if (ChatConfig.DISABLE_CHAT_REPORTS.get() && EngineUtils.hasPlugin(HookId.PROTOCOL_LIB)) {
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
        if (this.channelManager != null) {
            this.channelManager.shutdown();
        }
    }

    @NotNull
    public ChannelManager getChannelManager() {
        return this.channelManager;
    }

    @NotNull
    public ChatRuleManager getRuleManager() {
        return this.ruleManager;
    }

    @Nullable
    public static ChatPlayerFormat getPlayerFormat(@NotNull Player player) {
        Set<String> ranks = PlayerUtil.getPermissionGroups(player);
        var map = ChatConfig.FORMAT_PLAYER.get();
        return map.entrySet().stream().filter(entry -> ranks.contains(entry.getKey()) || entry.getKey().equalsIgnoreCase("default")).map(Map.Entry::getValue).max(Comparator.comparingInt(ChatPlayerFormat::getPriority)).orElse(null);
    }

    @Nullable
    public static ChatMention getMentionSpecial(@NotNull String id) {
        return ChatConfig.MENTIONS_SPECIAL.get().get(id.toLowerCase());
    }

    public void handleChatEvent(@NotNull AsyncPlayerChatEvent event) {
        ChatMessageHandler handler = new ChatMessageHandler(this, event);
        SunlightPreHandleChatEvent handleChatEvent = new SunlightPreHandleChatEvent(this, handler);
        this.plugin.getPluginManager().callEvent(handleChatEvent);

        if (handleChatEvent.isCancelled() || !handler.handle()) {
            event.setCancelled(true);
        }
    }

    public void handleCommandEvent(@NotNull PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission(ChatPerms.BYPASS_COOLDOWN_COMMAND) && !ChatUtils.isNextCommandAvailable(player)) {
            String time = TimeUtil.formatTimeLeft(ChatUtils.getNextCommandTime(player));
            this.plugin.getMessage(ChatLang.ANTI_SPAM_DELAY_CMD).replace(Placeholders.GENERIC_TIME, time).send(player);
            event.setCancelled(true);
            return;
        }

        String msgRaw = Colorizer.strip(event.getMessage());
        String msgCmd = StringUtil.extractCommandName(msgRaw);
        String msgReal = msgRaw;
        Set<String> aliases = CommandRegister.getAliases(msgCmd, true);

        Set<String> colorCommands = ChatConfig.CHAT_COLOR_COMMANDS.get();
        if (!colorCommands.isEmpty()) {
            if (aliases.stream().anyMatch(colorCommands::contains)) {
                msgReal = player.hasPermission(ChatPerms.COLOR) ? Colorizer.apply(event.getMessage()) : msgRaw;
            }
        }

        boolean doCheckSimilar = ChatConfig.ANTI_SPAM_ENABLED.get() && aliases.stream().noneMatch(cmd -> ChatConfig.ANTI_SPAM_COMMAND_WHITELIST.get().contains(cmd));
        if (!player.hasPermission(ChatPerms.BYPASS_ANTISPAM) && doCheckSimilar && !ChatUtils.checkSpamSimilarCommand(player, msgRaw)) {
            this.plugin.getMessage(ChatLang.ANTI_SPAM_SIMILAR_CMD).send(player);
            event.setCancelled(true);
            return;
        }

        boolean doCheckCaps = ChatConfig.ANTI_CAPS_ENABLED.get() && CommandRegister.getAliases(msgCmd, true).stream().anyMatch(cmd -> ChatConfig.ANTI_CAPS_AFFECTED_COMMANDS.get().contains(cmd));
        if (!player.hasPermission(ChatPerms.BYPASS_ANTICAPS) && doCheckCaps) {
            msgReal = ChatUtils.doAntiCaps(msgReal);
        }

        if (!player.hasPermission(ChatPerms.BYPASS_RULES)) {
            String msgRawNoCmd = msgRaw.replace(msgCmd, "");
            String msgRealNoCmd = msgReal.replace(msgCmd, "");
            String msgRealNoCmdRuled = this.ruleManager.checkRules(player, msgRealNoCmd, msgRawNoCmd);
            if (msgRealNoCmdRuled == null) {
                event.setCancelled(true);
                return;
            }
            msgReal = msgReal.replace(msgRealNoCmd, msgRealNoCmdRuled);
        }

        event.setMessage(msgReal);

        if (!player.hasPermission(ChatPerms.BYPASS_ANTISPAM)) {
            ChatUtils.setLastCommand(player, msgRaw);
        }
        if (!player.hasPermission(ChatPerms.BYPASS_COOLDOWN_COMMAND)) {
            ChatUtils.setNextCommandTime(player);
        }
    }

    @NotNull
    public Set<Player> getSpies(@NotNull ChatSpyType spyType) {
        return this.plugin.getServer().getOnlinePlayers().stream().filter(player -> this.plugin.getUserManager().getUserData(player).getSettings().get(spyType.getSettingChat())).collect(Collectors.toSet());
    }

    @NotNull
    private String getSpyFormat(@NotNull ChatSpyType spyType, @NotNull CommandSender player, @NotNull String message) {
        String format = ChatConfig.SPY_FORMAT.get().getOrDefault(spyType, "")
            .replace(Placeholders.GENERIC_MESSAGE, message);
        format = Placeholders.forSender(player).apply(format);
        return format;
    }

    public void handleSpyMode(@NotNull AsyncSunlightPlayerChatEvent event) {
        String format = this.getSpyFormat(ChatSpyType.CHAT, event.getPlayer(), event.getMessage());
        format = event.getChannel().replacePlaceholders().apply(format);
        this.handleSpyMode(event.getPlayer(), format, ChatSpyType.CHAT, spy -> !event.getChannel().isInRadius(event.getPlayer(), spy));
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
        this.getSpies(spyType).stream().filter(spy -> spy != sender && (spyFilter == null || spyFilter.test(spy))).forEach(spy -> PlayerUtil.sendRichMessage(spy, format));
    }

    public void handleSpyLog(@NotNull CommandSender sender, @NotNull String format, @NotNull ChatSpyType spyType) {
        if (!(sender instanceof Player player)) {
            return;
        }
        SunUser user = this.plugin.getUserManager().getUserData(player);
        if (!user.getSettings().get(spyType.getSettingLog())) {
            return;
        }
        this.plugin.info(format);
        String date = Config.GENERAL_DATE_FORMAT.get().format(TimeUtil.toEpochMillis(LocalDateTime.now()));
        String outFile = "[" + date + "] " + Colorizer.strip(format);
        String filePath = this.getAbsolutePath() + "/spy_logs/" + player.getName() + "_" + spyType.name().toLowerCase() + ".log";
        FileUtil.create(new File(filePath));
        this.plugin.runTaskAsync(c -> {
            try {
                BufferedWriter output = new BufferedWriter(new FileWriter(filePath, true));
                output.append(outFile);
                output.newLine();
                output.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}

