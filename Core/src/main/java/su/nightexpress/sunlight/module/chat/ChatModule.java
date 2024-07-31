package su.nightexpress.sunlight.module.chat;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.core.user.settings.Setting;
import su.nightexpress.sunlight.core.user.settings.SettingRegistry;
import su.nightexpress.sunlight.hook.HookId;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleInfo;
import su.nightexpress.sunlight.module.chat.command.*;
import su.nightexpress.sunlight.module.chat.command.ChannelCommands;
import su.nightexpress.sunlight.module.chat.config.ChatConfig;
import su.nightexpress.sunlight.module.chat.config.ChatLang;
import su.nightexpress.sunlight.module.chat.config.ChatPerms;
import su.nightexpress.sunlight.module.chat.event.SunlightPreHandleChatEvent;
import su.nightexpress.sunlight.module.chat.format.FormatComponent;
import su.nightexpress.sunlight.module.chat.format.FormatContainer;
import su.nightexpress.sunlight.module.chat.handler.ChatMessageHandler;
import su.nightexpress.sunlight.module.chat.handler.CommandHandler;
import su.nightexpress.sunlight.module.chat.handler.PrivateMessageHandler;
import su.nightexpress.sunlight.module.chat.listener.ChatListener;
import su.nightexpress.sunlight.module.chat.mention.Mention;
import su.nightexpress.sunlight.module.chat.mention.PlayerMention;
import su.nightexpress.sunlight.module.chat.module.announcer.AnnounceManager;
import su.nightexpress.sunlight.module.chat.module.deathmessage.DeathMessageManager;
import su.nightexpress.sunlight.module.chat.module.joinquit.JoinMessageManager;
import su.nightexpress.sunlight.module.chat.module.spy.SpyManager;
import su.nightexpress.sunlight.module.chat.rule.RuleManager;
import su.nightexpress.sunlight.module.chat.util.ReportDisabler;

import java.util.*;

public class ChatModule extends Module {

    public static final Setting<Boolean> MENTIONS_SETTING = SettingRegistry.register(Setting.create("mentions", true, true));

    private final Map<UUID, PlayerChatData> chatDataMap;

    private ChannelManager      channelManager;
    private DeathMessageManager deathManager;
    private JoinMessageManager  joinManager;
    private RuleManager        ruleManager;
    private AnnounceManager announceManager;
    private SpyManager      spyManager;
    private ReportDisabler reportDisabler;

    public ChatModule(@NotNull SunLightPlugin plugin, @NotNull String id) {
        super(plugin, id);
        this.chatDataMap = new HashMap<>();
    }

    @Override
    protected void gatherInfo(@NotNull ModuleInfo moduleInfo) {
        moduleInfo.setConfigClass(ChatConfig.class);
        moduleInfo.setLangClass(ChatLang.class);
        moduleInfo.setPermissionsClass(ChatPerms.class);
    }

    @Override
    protected void onModuleLoad() {
        this.loadManagers();
        this.loadCommands();
        this.loadModules();

        this.addListener(new ChatListener(this.plugin, this));
    }

    @Override
    protected void onModuleUnload() {
        ChannelCommands.unload(this.plugin, this);

        if (this.reportDisabler != null) this.reportDisabler.shutdown();
        if (this.spyManager != null) this.spyManager.shutdown();
        if (this.announceManager != null) this.announceManager.shutdown();
        if (this.deathManager != null) this.deathManager.shutdown();
        if (this.joinManager != null) this.joinManager.shutdown();
        if (this.ruleManager != null) this.ruleManager.shutdown();
        if (this.channelManager != null) this.channelManager.shutdown();
    }

    private void loadCommands() {
        ChannelCommands.load(this.plugin, this);
        ClearChatCommand.load(this.plugin);

        if (ChatConfig.PM_ENABLED.get()) {
            PrivateMessageCommands.load(this.plugin, this);
        }
        if (ChatConfig.MENTIONS_ENABLED.get()) {
            MentionsCommand.load(this.plugin);
        }
        if (ChatConfig.ROLEPLAY_COMMANDS_ENABLED.get()) {
            RoleplayCommands.load(this.plugin);
        }
    }

    private void loadManagers() {
        this.channelManager = new ChannelManager(this.plugin, this);
        this.channelManager.setup();

        this.ruleManager = new RuleManager(this.plugin, this);
        this.ruleManager.setup();
    }

    private void loadModules() {
        if (ChatConfig.SPY_ENABLED.get()) {
            this.spyManager = new SpyManager(this.plugin, this);
            this.spyManager.setup();
        }

        if (ChatConfig.MODULE_JOIN_QUIT_MESSAGES.get()) {
            this.joinManager = new JoinMessageManager(this.plugin, this);
            this.joinManager.setup();
        }

        if (ChatConfig.MODULE_DEATH_MESSAGES.get()) {
            if (Version.isAtLeast(Version.MC_1_20_6)) {
                this.deathManager = new DeathMessageManager(this.plugin, this);
                this.deathManager.setup();
            }
            else {
                this.error("Death Messages feature is available only for " + Version.MC_1_20_6.getLocalized() + " and above!");
            }
        }

        if (ChatConfig.MODULE_ANNOUNCER.get()) {
            this.announceManager = new AnnounceManager(this.plugin, this);
            this.announceManager.setup();
        }

        if (ChatConfig.DISABLE_REPORTS.get() && Plugins.isLoaded(HookId.PROTOCOL_LIB) && Version.isAbove(Version.V1_18_R2)) {
            this.reportDisabler = new ReportDisabler(this.plugin);
            this.reportDisabler.setup();
        }
    }

    @NotNull
    public PlayerChatData getChatData(@NotNull Player player) {
        return this.chatDataMap.computeIfAbsent(player.getUniqueId(), k -> new PlayerChatData(player));
    }

    public void clearChatData(@NotNull UUID playerId) {
        this.chatDataMap.remove(playerId);
    }

    @NotNull
    public ChannelManager getChannelManager() {
        return this.channelManager;
    }

    @NotNull
    public RuleManager getRuleManager() {
        return this.ruleManager;
    }

    @Nullable
    public AnnounceManager getAnnounceManager() {
        return this.announceManager;
    }

    @Nullable
    public SpyManager getSpyManager() {
        return this.spyManager;
    }

    @NotNull
    public Set<FormatComponent> getFormatComponents() {
        return new HashSet<>(ChatConfig.FORMAT_COMPONENTS.get().values());
    }

    @Nullable
    public FormatContainer getFormatContainer(@NotNull Player player) {
        var map = ChatConfig.FORMAT_LIST.get();
        return map.values().stream().filter(container -> container.isApplicable(player)).max(Comparator.comparingInt(FormatContainer::getPriority)).orElse(null);
    }

    @Nullable
    public Mention getMention(@NotNull String name) {
        Mention mention = ChatConfig.getSpecialMention(name);
        if (mention != null) return mention;

        Player player = this.plugin.getServer().getPlayerExact(name);
        if (player == null) return null;

        return new PlayerMention(player);
    }

    public void handleChatEvent(@NotNull AsyncPlayerChatEvent event) {
        ChatMessageHandler handler = new ChatMessageHandler(this.plugin, this, event);
        SunlightPreHandleChatEvent handleChatEvent = new SunlightPreHandleChatEvent(this, handler);
        this.plugin.getPluginManager().callEvent(handleChatEvent);

        if (handleChatEvent.isCancelled() || !handler.handle()) {
            event.setCancelled(true);
        }
    }

    public void handleCommandEvent(@NotNull PlayerCommandPreprocessEvent event) {
        CommandHandler handler = new CommandHandler(this.plugin, this, event);
        if (!handler.handle()) {
            event.setCancelled(true);
        }
    }

    public boolean handlePrivateMessage(@NotNull CommandSender sender, @NotNull CommandSender receiver, @NotNull String message) {
        PrivateMessageHandler handler = new PrivateMessageHandler(this.plugin, this, sender, receiver, message);
        return handler.handle();
    }
}

