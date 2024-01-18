package su.nightexpress.sunlight.module.chat;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.chat.command.ShortChannelCommand;
import su.nightexpress.sunlight.module.chat.config.ChatConfig;
import su.nightexpress.sunlight.module.chat.config.ChatLang;

import java.util.*;
import java.util.stream.Collectors;

public class ChannelManager extends AbstractManager<SunLight> {

    public static final String CONFIG_NAME = "channels.yml";

    private final ChatModule module;
    private final Map<String, ChatChannel> channelMap;
    private final Map<UUID, String> activeChannelMap;
    private ChatChannel defaultChannel;

    public ChannelManager(@NotNull SunLight plugin, @NotNull ChatModule module) {
        super(plugin);
        this.module = module;
        this.channelMap = new HashMap<>();
        this.activeChannelMap = new HashMap<>();
    }

    protected void onLoad() {
        JYML cfg = JYML.loadOrExtract(this.plugin, this.module.getLocalPath(), CONFIG_NAME);
        for (String channelId : cfg.getSection("")) {
            ChatChannel channel2 = ChatChannel.read(cfg, channelId, channelId);
            this.getChannelMap().put(channel2.getId(), channel2);
        }

        cfg.saveChanges();

        this.defaultChannel = this.getChannels().stream().filter(ChatChannel::isDefault).findFirst().orElse(this.getChannels().stream().findAny().orElse(null));
        if (this.defaultChannel == null) {
            throw new IllegalStateException("Chat module has no channels! You must have at least one channel for the module to work.");
        }

        this.getChannels().forEach(channel -> this.plugin.getCommandManager().registerCommand(new ShortChannelCommand(this.module, channel)));
        this.plugin.getServer().getOnlinePlayers().forEach(this::autoJoinChannels);
    }

    protected void onShutdown() {
        this.getChannelMap().clear();
        this.getActiveChannelMap().clear();
    }

    public void autoJoinChannels(@NotNull Player player) {
        this.getAvailableChannels(player).stream().filter(ChatChannel::isAutoJoin).forEach(channel -> {
            this.joinChannel(player, channel, ChatConfig.SILENT_CHANNEL_JOIN_ON_LOGIN.get());
        });
    }

    @NotNull
    public Map<String, ChatChannel> getChannelMap() {
        return this.channelMap;
    }

    @NotNull
    public Map<UUID, String> getActiveChannelMap() {
        return this.activeChannelMap;
    }

    @NotNull
    public Collection<ChatChannel> getChannels() {
        return this.getChannelMap().values();
    }

    @NotNull
    public Set<ChatChannel> getAvailableChannels(@NotNull Player player) {
        return this.getChannels().stream().filter(channel -> channel.canHear(player)).collect(Collectors.toSet());
    }

    @NotNull
    public ChatChannel getDefaultChannel() {
        return this.defaultChannel;
    }

    @Nullable
    public ChatChannel getChannel(@NotNull String id) {
        return this.getChannelMap().get(id.toLowerCase());
    }

    @Nullable
    public ChatChannel getChannelByPrefix(@NotNull String message) {
        return this.getChannels().stream().filter(channel -> channel.isPrefix(message)).findFirst().orElse(null);
    }

    @Nullable
    public ChatChannel getChannelByCommand(@NotNull String command) {
        return this.getChannels().stream().filter(channel -> channel.isCommand(command)).findFirst().orElse(null);
    }

    @NotNull
    public ChatChannel getActiveChannel(@NotNull Player player) {
        String id = this.getActiveChannelMap().getOrDefault(player.getUniqueId(), Placeholders.DEFAULT);
        ChatChannel chatChannel = this.getChannel(id);
        if (chatChannel == null) {
            chatChannel = this.getDefaultChannel();
        }
        return chatChannel;
    }

    public boolean setActiveChannel(@NotNull Player player, @NotNull ChatChannel channel) {
        if (!channel.contains(player) && !this.joinChannel(player, channel)) {
            return false;
        }
        this.getActiveChannelMap().put(player.getUniqueId(), channel.getId());
        this.plugin.getMessage(ChatLang.CHANNEL_SET_SUCCESS).replace(channel.replacePlaceholders()).send(player);
        return true;
    }

    public boolean joinChannel(@NotNull Player player, @NotNull ChatChannel channel) {
        return this.joinChannel(player, channel, false);
    }

    public boolean joinChannel(@NotNull Player player, @NotNull ChatChannel channel, boolean isSilent) {
        if (!channel.canJoin(player)) {
            if (!isSilent) {
                this.plugin.getMessage(ChatLang.CHANNEL_JOIN_ERROR_NO_PERMISSION).replace(channel.replacePlaceholders()).send(player);
            }
            return false;
        }
        if (channel.join(player)) {
            if (!isSilent) {
                this.plugin.getMessage(ChatLang.CHANNEL_JOIN_SUCCESS).replace(channel.replacePlaceholders()).send(player);
            }
            return true;
        }
        if (!isSilent) {
            this.plugin.getMessage(ChatLang.CHANNEL_JOIN_ERROR_ALREADY_IN).replace(channel.replacePlaceholders()).send(player);
        }
        return false;
    }

    public boolean leaveChannel(@NotNull Player player, @NotNull ChatChannel channel) {
        if (channel.removePlayer(player)) {
            this.plugin.getMessage(ChatLang.CHANNEL_LEAVE_SUCCESS).replace(channel.replacePlaceholders()).send(player);
            return true;
        }
        this.plugin.getMessage(ChatLang.CHANNEL_LEAVE_ERROR_NOT_IN).replace(channel.replacePlaceholders()).send(player);
        return false;
    }

    public void leaveAllChannels(@NotNull Player player) {
        this.getChannels().forEach(channel -> channel.removePlayer(player));
        this.getActiveChannelMap().remove(player.getUniqueId());
    }
}

