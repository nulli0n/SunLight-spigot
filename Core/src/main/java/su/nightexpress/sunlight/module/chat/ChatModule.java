package su.nightexpress.sunlight.module.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.bridge.chat.UniversalChatEvent;
import su.nightexpress.nightcore.bridge.chat.UniversalChatEventHandler;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.util.FileUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.nightcore.util.placeholder.PlaceholderContext;
import su.nightexpress.nightcore.util.text.night.NightMessage;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.hook.HookId;
import su.nightexpress.sunlight.hook.placeholder.PlaceholderRegistry;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleContext;
import su.nightexpress.sunlight.module.chat.cache.UserChatCache;
import su.nightexpress.sunlight.module.chat.channel.ChannelRepository;
import su.nightexpress.sunlight.module.chat.channel.ChannelSchema;
import su.nightexpress.sunlight.module.chat.channel.ChatChannel;
import su.nightexpress.sunlight.module.chat.command.*;
import su.nightexpress.sunlight.module.chat.context.ChatContext;
import su.nightexpress.sunlight.module.chat.context.CommandContext;
import su.nightexpress.sunlight.module.chat.context.ConversationContext;
import su.nightexpress.sunlight.module.chat.context.MessageContext;
import su.nightexpress.sunlight.module.chat.core.ChatLang;
import su.nightexpress.sunlight.module.chat.core.ChatPerms;
import su.nightexpress.sunlight.module.chat.core.ChatSettings;
import su.nightexpress.sunlight.module.chat.discord.DiscordHandler;
import su.nightexpress.sunlight.module.chat.event.PlayerPrivateMessageEvent;
import su.nightexpress.sunlight.module.chat.format.FormatDefinition;
import su.nightexpress.sunlight.module.chat.listener.ChatListener;
import su.nightexpress.sunlight.module.chat.processor.ChatProcessor;
import su.nightexpress.sunlight.module.chat.processor.chat.ChannelProcessor;
import su.nightexpress.sunlight.module.chat.processor.chat.DiscordProcessor;
import su.nightexpress.sunlight.module.chat.processor.chat.FormatProcessor;
import su.nightexpress.sunlight.module.chat.processor.chat.MentionProcessor;
import su.nightexpress.sunlight.module.chat.processor.command.CommandCooldownProcessor;
import su.nightexpress.sunlight.module.chat.processor.conversation.ConversationProcessor;
import su.nightexpress.sunlight.module.chat.processor.global.*;
import su.nightexpress.sunlight.module.chat.report.ReportHandler;
import su.nightexpress.sunlight.module.chat.report.ReportPacketsHandler;
import su.nightexpress.sunlight.module.chat.report.ReportProtocolHandler;
import su.nightexpress.sunlight.module.chat.rule.WordFilter;
import su.nightexpress.sunlight.module.chat.spy.SpyLogger;
import su.nightexpress.sunlight.module.chat.spy.SpyType;
import su.nightexpress.sunlight.user.SunUser;
import su.nightexpress.sunlight.user.property.UserProperty;
import su.nightexpress.sunlight.user.property.UserPropertyRegistry;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChatModule extends Module {

    private final ChatSettings              settings;
    private final ChannelRepository         channelRepository;
    private final UniversalChatEventHandler chatEventHandler;

    private Pattern        mentionsPattern;
    private WordFilter     wordFilter;
    private SpyLogger      spyLogger;
    private ReportHandler  reportHandler;
    private DiscordHandler discordHandler;

    public ChatModule(@NotNull ModuleContext context) {
        super(context);
        this.settings = new ChatSettings();
        this.channelRepository = new ChannelRepository();
        this.chatEventHandler = this::handleChatMessage;
    }

    @Override
    protected void loadModule(@NotNull FileConfig config) {
        this.plugin.injectLang(ChatLang.class);
        this.settings.load(config);

        this.loadMentions();
        this.loadConversations();
        this.loadChannels();
        this.loadWordFilter();
        this.loadSpy();
        this.loadReportHandler();
        this.loadDiscordHook();

        this.plugin.addChatHandler(this.settings.getChatEventPriority(), this.chatEventHandler);

        this.plugin.getServer().getOnlinePlayers().forEach(this::autoJoinChannels);

        this.addListener(new ChatListener(this.plugin, this));
    }

    @Override
    protected void unloadModule() {
        this.plugin.removeChatHandler(this.chatEventHandler);

        this.channelRepository.clear();
        this.wordFilter = null;

        if (this.spyLogger != null) {
            this.spyLogger.write();
            this.spyLogger.shutdown();
            this.spyLogger = null;
        }

        if (this.discordHandler != null) {
            this.discordHandler.shutdown();
            this.discordHandler = null;
        }

        if (this.reportHandler != null) {
            this.reportHandler.unload();
            this.reportHandler = null;
        }
    }

    @Override
    protected void registerPermissions(@NotNull PermissionTree root) {
        // Attach channel-specific permissions.
        this.channelRepository.getChannels().forEach(channel -> {
            ChatPerms.CHANNEL_LISTEN.permission(channel.getId());
            ChatPerms.CHANNEL_SPEAK.permission(channel.getId());
        });

        root.merge(ChatPerms.ROOT);
    }

    @Override
    protected void registerCommands() {
        this.commandRegistry.addProvider("chat-clearchat", new ClearChatCommandProvider(this.plugin, this));

        if (this.settings.isChannelsEnabled()) {
            this.commandRegistry.addProvider("chat-channel", new ChannelCommandsProvider(this.plugin, this));
        }

        if (this.settings.isConversationsEnabled()) {
            this.commandRegistry.addProvider("chat-conversations",
                new ConversationCommandProvider(this.plugin, this, this.userManager));
        }

        if (this.settings.isMentionsEnabled()) {
            this.commandRegistry.addProvider("chat-mentions",
                new MentionsCommandProvider(this.plugin, this, this.userManager));
        }

        if (this.settings.isRoleplayCommandEnabled()) {
            this.commandRegistry.addProvider("chat-roleplay", new RoleplayCommands(this.plugin, this));
        }

        if (this.settings.isSpyEnabled()) {
            this.commandRegistry.addProvider("chat-spy", new SpyCommandProvider(this.plugin, this, this.userManager));
        }
    }

    @Override
    public void registerPlaceholders(@NotNull PlaceholderRegistry registry) {
        registry.register("chat_conversations_state", (player, payload) -> {
            return CoreLang.STATE_ENABLED_DISALBED.get(this.userManager.getOrFetch(player).getPropertyOrDefault(
                ChatProperties.CONVERSATIONS));
        });

        registry.register("chat_conversations_bool", (player, payload) -> {
            return String.valueOf(this.userManager.getOrFetch(player).getPropertyOrDefault(
                ChatProperties.CONVERSATIONS));
        });
    }

    private void loadMentions() {
        if (!this.settings.isMentionsEnabled()) return;

        UserPropertyRegistry.register(ChatProperties.MENTIONS);

        this.mentionsPattern = Pattern.compile(this.settings.getMentionsPattern());
        this.settings.getCustomMentions().forEach((id, groupMention) -> {
            ChatPerms.MENTION.permission(id);
        });
    }

    private void loadConversations() {
        if (!this.settings.isConversationsEnabled()) return;

        UserPropertyRegistry.register(ChatProperties.CONVERSATIONS);
    }

    private void loadChannels() {
        Path channelsDir = Path.of(this.getSystemPath() + ChatFiles.DIR_CHANNELS);

        if (!this.settings.isChannelsEnabled()) {
            this.loadDefaultChannel(channelsDir);
            return;
        }

        if (!Files.exists(channelsDir)) {
            ChannelSchema.getDefaultChannels().forEach(channel -> this.writeChannel(channelsDir, channel));
        }

        FileUtil.findYamlFiles(channelsDir.toString()).forEach(this::loadChannel);

        String defaultId = this.settings.getChannelDefaultId();
        ChatChannel defChannel = this.channelRepository.getById(defaultId);

        if (defChannel == null) {
            this.error(
                "Channel '%s', that is set as default one, does not exist. The '%s' one will be used to keep the chat working."
                    .formatted(defaultId, ChatDefaults.DEFAULT_CHANNEL_ID));
            this.loadDefaultChannel(channelsDir);
            return;
        }

        this.channelRepository.setDefaultChannel(defChannel);
    }

    private void writeChannel(@NotNull Path channelsDir, @NotNull ChatChannel channel) {
        Path file = Path.of(channelsDir.toString(), FileConfig.withExtension(channel.getId()));
        FileConfig config = FileConfig.load(file);
        config.edit(channel::write);
    }

    @NotNull
    private ChatChannel loadChannel(@NotNull Path channelFile) {
        ChatChannel channel = ChatChannel.fromFile(channelFile);
        this.channelRepository.add(channel);
        return channel;
    }

    private void loadDefaultChannel(@NotNull Path channelsDir) {
        Path defFile = Path.of(channelsDir.toString(), FileConfig.withExtension(ChatDefaults.DEFAULT_CHANNEL_ID));
        if (!Files.exists(defFile)) {
            this.writeChannel(channelsDir, ChannelSchema.createDefaultChannel());
        }

        ChatChannel channel = this.loadChannel(defFile);

        this.channelRepository.setDefaultChannel(channel);
    }

    private void loadWordFilter() {
        if (!this.settings.getProfanityFilterEnabled()) return;

        Path rulesPath = Path.of(this.getSystemPath() + ChatFiles.DIR_RULES);
        if (!Files.exists(rulesPath)) {
            try {
                Files.createDirectories(rulesPath);

                Collection<String> defaultRules = ChatDefaults.getDefaultWordFilterRules(Locale.getDefault());
                if (!defaultRules.isEmpty()) {
                    this.writeRules(defaultRules, Path.of(rulesPath.toString(), ChatDefaults.DEFAULT_RULE_FILE_NAME));
                }
            }
            catch (IOException exception) {
                exception.printStackTrace();
                return;
            }
        }

        Set<String> ruleNames = this.settings.getProfanityFilterRules();
        Set<String> allRules = new HashSet<>();

        FileUtil.findFiles(rulesPath.toString(), file -> ruleNames.contains(file.getFileName().toString())).forEach(
            file -> {
                allRules.addAll(this.readRules(file));
            });

        this.wordFilter = new WordFilter(allRules);
    }

    private void writeRules(@NotNull Collection<String> rules, @NotNull Path file) {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            for (String rule : rules) {
                writer.append(rule);
                writer.newLine();
            }
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @NotNull
    private Collection<String> readRules(@NotNull Path file) {
        Set<String> rules = new HashSet<>();

        try (Stream<String> stream = Files.lines(file)) {
            stream.filter(Predicate.not(String::isBlank)).forEach(rules::add);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }

        return rules;
    }

    private void loadSpy() {
        if (!this.settings.isSpyEnabled()) return;

        for (SpyType spyType : SpyType.values()) {
            UserPropertyRegistry.register(ChatProperties.getSpyInfoProperty(spyType));
            UserPropertyRegistry.register(ChatProperties.getSpyLogProperty(spyType));
        }

        try {
            this.spyLogger = new SpyLogger(this.plugin, Path.of(this.getSystemPath(), ChatFiles.FILE_SPY_LOG));
            this.addAsyncTask(this.spyLogger::write, 60);
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void loadReportHandler() {
        if (this.settings.getReportsDisable()) {
            if (Plugins.isInstalled(HookId.PACKET_EVENTS)) {
                this.reportHandler = new ReportPacketsHandler();
            }
            else if (Plugins.isInstalled(HookId.PROTOCOL_LIB)) {
                this.reportHandler = new ReportProtocolHandler(this.plugin);
            }

            if (this.reportHandler != null) {
                this.reportHandler.load();
            }
        }
    }

    private void loadDiscordHook() {
        if (this.settings.isDiscordHookEnabled() && HookId.hasDiscordSRV()) {
            this.discordHandler = new DiscordHandler(this.plugin, this);
            this.discordHandler.setup();
        }
    }

    @NotNull
    public ChatSettings getSettings() {
        return this.settings;
    }

    @NotNull
    public UserChatCache getChatCache(@NotNull Player player) {
        return this.userManager.getOrFetch(player).getCacheOrCreate(UserChatCache.class, UserChatCache::new);
    }

    @NotNull
    public ChannelRepository getChannelRepository() {
        return this.channelRepository;
    }

    @Nullable
    public DiscordHandler getDiscordHandler() {
        return this.discordHandler;
    }

    @NotNull
    public Set<ChatChannel> getChannelsAllowedToListen(@NotNull Player player) {
        return this.channelRepository.getChannels().stream().filter(channel -> channel.canListenHere(player)).collect(
            Collectors.toSet());
    }

    @NotNull
    public String getEffectiveChatFormat(@NotNull Player player) {
        return this.settings.getFormatDefinitions()
            .values()
            .stream()
            .filter(container -> container.isApplicable(player))
            .max(Comparator.comparingInt(FormatDefinition::getPriority))
            .map(FormatDefinition::getFormat)
            .orElse(this.settings.getFormatFallback());
    }

    @NotNull
    public ChatChannel getEffectiveChannel(@NotNull Player player, @Nullable Character prefix) {
        if (prefix != null) {
            ChatChannel byPrefix = this.channelRepository.getByPrefix(prefix);
            if (byPrefix != null && byPrefix.canSpeakHere(player)) {
                return byPrefix;
            }
        }

        return this.channelRepository.getDefaultChannel();
    }

    public boolean joinChannel(@NotNull Player player, @NotNull ChatChannel channel) {
        return this.joinChannel(player, channel, false);
    }

    public boolean joinChannel(@NotNull Player player, @NotNull ChatChannel channel, boolean isSilent) {
        if (!channel.canListenOrSpeakHere(player)) {
            if (!isSilent) {
                this.sendPrefixed(ChatLang.CHANNEL_JOIN_ERROR_NO_PERMISSION, player, builder -> builder.with(channel
                    .placeholders()));
            }
            return false;
        }

        if (channel.addPlayer(player)) {
            if (!isSilent) {
                this.sendPrefixed(ChatLang.CHANNEL_JOIN_SUCCESS, player, builder -> builder.with(channel
                    .placeholders()));
            }
            return true;
        }

        if (!isSilent) {
            this.sendPrefixed(ChatLang.CHANNEL_JOIN_ERROR_ALREADY_IN, player, builder -> builder.with(channel
                .placeholders()));
        }

        return false;
    }

    public boolean leaveChannel(@NotNull Player player, @NotNull ChatChannel channel) {
        if (channel.removePlayer(player)) {
            this.sendPrefixed(ChatLang.CHANNEL_LEAVE_SUCCESS, player, builder -> builder.with(channel.placeholders()));
            return true;
        }

        this.sendPrefixed(ChatLang.CHANNEL_LEAVE_ERROR_NOT_IN, player, builder -> builder.with(channel.placeholders()));
        return false;
    }

    public void autoJoinChannels(@NotNull Player player) {
        this.getChannelsAllowedToListen(player).stream().filter(channel -> channel.getAccessibility().autoJoin())
            .forEach(channel -> {
                this.joinChannel(player, channel, true);
            });
    }

    public void removeFromAllChannels(@NotNull Player player) {
        this.channelRepository.getChannels().forEach(channel -> channel.removePlayer(player));
    }

    @NotNull
    public Set<Player> getSpies(@NotNull SpyType type) {
        UserProperty<Boolean> property = ChatProperties.getSpyInfoProperty(type);

        return this.plugin.getServer().getOnlinePlayers().stream()
            .filter(player -> this.userManager.getOrFetch(player).getPropertyOrDefault(property))
            .collect(Collectors.toSet());
    }

    public void sendSpyInfo(@NotNull Player player, @NotNull String message, @NotNull String format,
                            @NotNull SpyType spyType) {
        PlaceholderContext context = PlaceholderContext.builder()
            .with(CommonPlaceholders.PLAYER.resolver(player))
            .with(SLPlaceholders.GENERIC_MESSAGE, () -> message)
            .build();

        String formatted = context.apply(format);

        this.getSpies(spyType).forEach(spy -> Players.sendMessage(spy, formatted));

        SunUser user = this.userManager.getOrFetch(player);
        if (this.spyLogger != null && user.getPropertyOrDefault(ChatProperties.getSpyLogProperty(spyType))) {
            this.spyLogger.addEntry(formatted);
        }
    }

    public void handleChatMessage(@NotNull UniversalChatEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        String originalMessage = event.message();

        UserChatCache data = this.getChatCache(player);
        String format = this.getEffectiveChatFormat(player);
        ChatChannel channel = this.getEffectiveChannel(player, originalMessage.charAt(0));


        MessageContext context = new MessageContext(player, data, originalMessage, format, channel, event.viewers());
        List<ChatProcessor<? super MessageContext>> processors = new ArrayList<>();

        processors.add(new ColorProcessor());

        processors.add(new ChannelProcessor(this.plugin)); // Check channel cooldown, remove channel prefix from message.

        if (this.settings.isAntiFloodEnabled() && !player.hasPermission(ChatPerms.BYPASS_ANTI_FLOOD)) {
            processors.add(new AntiFloodProcessor()); // Check message similarity only after all modifications are done.
        }

        if (this.settings.isAntiCapsEnabled() && !player.hasPermission(ChatPerms.BYPASS_ANTI_CAPS)) {
            processors.add(new AntiCapsProcessor()); // Check CAPS usage and adjust to lower case if needed.
        }

        if (this.settings.getProfanityFilterEnabled() && !player.hasPermission(ChatPerms.BYPASS_PROFANITY_FILTER)) {
            if (this.wordFilter != null) {
                processors.add(new FilterProcessor(this.wordFilter)); // Check custom regex rules and adjust/cancel if needed.
            }
        }

        processors.add(new FormatProcessor()); // Replace format placeholders in postProcess

        if (this.settings.isItemShowEnabled()) {
            processors.add(new ItemDisplayProcessor()); // Inject item display in postProcess in prepared format.
        }

        if (this.settings.isMentionsEnabled()) {
            processors.add(new MentionProcessor(this.mentionsPattern, this.userManager)); // Inject mentions in postProcess in prepared format.
        }

        if (this.settings.isSpyEnabled() && !player.hasPermission(ChatPerms.BYPASS_SPY_MONITOR)) {
            processors.add(new SpyProcessor());
        }

        if (this.discordHandler != null) {
            processors.add(new DiscordProcessor(this.discordHandler));
        }

        if (!this.process(processors, context)) {
            event.setCancelled(true);
            return;
        }

        event.editViewers(viewers -> {
            viewers.clear();
            viewers.addAll(context.getViewers());
        });
        event.message(NightMessage.parse(context.getMessage()));
        event.renderer((source, sourceDisplayName, message, viewer) -> {
            return NightMessage.parse(context.getFormat());
        });
    }

    public void handleCommandEvent(@NotNull PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        UserChatCache cache = this.getChatCache(player);
        String originalMessage = event.getMessage();

        CommandContext context = new CommandContext(player, cache, originalMessage);
        List<ChatProcessor<? super CommandContext>> processors = new ArrayList<>();

        String commandName = context.getCommandName();

        if (this.settings.isAntiFloodEnabled() && !this.settings.isAntiFloodWhitelistedCommand(commandName) && !player
            .hasPermission(ChatPerms.BYPASS_ANTI_FLOOD)) {
            processors.add(new CommandCooldownProcessor()); // Check general commands cooldown.
            processors.add(new AntiFloodProcessor()); // Check message similarity only after all modifications are done.
        }

        if (this.settings.isAntiCapsEnabled() && this.settings.isAntiCapsBlacklistedCommand(commandName) && !player
            .hasPermission(ChatPerms.BYPASS_ANTI_CAPS)) {
            processors.add(new AntiCapsProcessor()); // Check CAPS usage and adjust to lower case if needed.
        }

        if (this.settings.getProfanityFilterEnabled() && this.settings.isProfanityFilterAffectedCommand(
            commandName) && !player.hasPermission(ChatPerms.BYPASS_PROFANITY_FILTER)) {
            if (this.wordFilter != null) {
                processors.add(new FilterProcessor(this.wordFilter)); // Check custom regex rules and adjust/cancel if needed.
            }
        }

        if (this.settings.isSpyEnabled() && !player.hasPermission(ChatPerms.BYPASS_SPY_MONITOR)) {
            processors.add(new SpyProcessor());
        }

        if (!this.process(processors, context)) {
            event.setCancelled(true);
            return;
        }

        event.setMessage(context.getMessage());
    }

    public boolean sendPrivateMessage(@NotNull Player player, @NotNull Player target, @NotNull String message) {
        if (player == target) {
            this.sendPrefixed(ChatLang.CONVERSATIONS_SEND_YOURSELF, player);
            return false;
        }

        SunUser targetUser = this.userManager.getOrFetch(target);
        if (!targetUser.getPropertyOrDefault(ChatProperties.CONVERSATIONS) && !player.hasPermission(
            ChatPerms.BYPASS_CONVERSATIONS_DISABLED)) {
            this.sendPrefixed(ChatLang.CONVERSATIONS_SEND_DENIED, player, replacer -> replacer.with(
                CommonPlaceholders.PLAYER.resolver(target)));
            return false;
        }

        PlayerPrivateMessageEvent event = new PlayerPrivateMessageEvent(player, target, message);
        this.plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        if (!this.handlePrivateMessage(event)) {
            return false;
        }

        if (this.plugin.afkProvider().map(afkProvider -> afkProvider.isAfk(target)).orElse(false)) {
            this.sendPrefixed(ChatLang.CONVERSATIONS_TARGET_AFK, player, builder -> builder
                .with(CommonPlaceholders.PLAYER.resolver(target))
            );
        }

        return true;
    }

    public boolean handlePrivateMessage(@NotNull PlayerPrivateMessageEvent event) {
        Player player = event.getSender();
        Player target = event.getTarget();
        String originalMessage = event.getMessage();
        String proxyFormat = this.settings.getConversationProxyFormat();

        UserChatCache cache = this.getChatCache(player);
        ConversationContext context = new ConversationContext(player, cache, originalMessage, proxyFormat, target);

        List<ChatProcessor<? super ConversationContext>> processors = new ArrayList<>();

        processors.add(new ColorProcessor());

        if (this.settings.isAntiFloodEnabled() && !player.hasPermission(ChatPerms.BYPASS_ANTI_FLOOD)) {
            processors.add(new AntiFloodProcessor()); // Check message similarity only after all modifications are done.
        }

        if (this.settings.isAntiCapsEnabled() && !player.hasPermission(ChatPerms.BYPASS_ANTI_CAPS)) {
            processors.add(new AntiCapsProcessor()); // Check CAPS usage and adjust to lower case if needed.
        }

        if (this.settings.getProfanityFilterEnabled() && !player.hasPermission(ChatPerms.BYPASS_PROFANITY_FILTER)) {
            if (this.wordFilter != null) {
                processors.add(new FilterProcessor(this.wordFilter)); // Check custom regex rules and adjust/cancel if needed.
            }
        }

        if (this.settings.isItemShowEnabled()) {
            processors.add(new ItemDisplayProcessor()); // Inject item display in postProcess in prepared format.
        }

        if (this.settings.isSpyEnabled() && !player.hasPermission(ChatPerms.BYPASS_SPY_MONITOR)) {
            processors.add(new SpyProcessor());
        }

        processors.add(new ConversationProcessor()); // Format and send messages.

        return this.process(processors, context);
    }

    private <T extends ChatContext> boolean process(@NotNull List<ChatProcessor<? super T>> processors,
                                                    @NotNull T context) {
        for (var processor : processors) {
            processor.preProcess(this, context);

            if (context.isCancelled()) {
                return false;
            }
        }

        processors.forEach(messageProcessor -> messageProcessor.postProcess(this, context));
        return true;
    }
}

