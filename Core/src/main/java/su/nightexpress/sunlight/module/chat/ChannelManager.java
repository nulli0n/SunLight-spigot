package su.nightexpress.sunlight.module.chat;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.chat.config.ChatConfig;
import su.nightexpress.sunlight.module.chat.config.ChatLang;

import java.util.*;
import java.util.stream.Collectors;

public class ChannelManager extends AbstractManager<SunLightPlugin> {

    public static final String FILE_NAME = "channels.yml";

    private final ChatModule module;

    private final Map<String, ChatChannel> channelMap;
    private final Map<UUID, String>        activeChannelMap;

    private ChatChannel defaultChannel;

    public ChannelManager(@NotNull SunLightPlugin plugin, @NotNull ChatModule module) {
        super(plugin);
        this.module = module;
        this.channelMap = new HashMap<>();
        this.activeChannelMap = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        this.loadConfig();

        this.defaultChannel = this.getChannels().stream().filter(ChatChannel::isDefault).findFirst().orElse(this.getChannels().stream().findAny().orElse(null));
        if (this.defaultChannel == null) {
            throw new IllegalStateException("Chat module has no channels! You must have at least one channel for the module to work.");
        }

        this.plugin.getServer().getOnlinePlayers().forEach(this::autoJoinChannels);
    }

    @Override
    protected void onShutdown() {
        this.channelMap.clear();
        this.activeChannelMap.clear();
    }

    private void loadConfig() {
        FileConfig config = FileConfig.loadOrExtract(this.plugin, this.module.getLocalPath(), FILE_NAME);

        if (config.getSection("").isEmpty()) {
            ChatChannel.getDefaults().forEach(channel -> channel.write(config, channel.getId()));
        }

        for (String sId : config.getSection("")) {
            ChatChannel channel = ChatChannel.read(config, sId, sId);
            this.channelMap.put(channel.getId(), channel);
        }

        config.saveChanges();
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
        return this.channelMap.values();
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
        return this.channelMap.get(id.toLowerCase());
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
        String id = this.activeChannelMap.getOrDefault(player.getUniqueId(), Placeholders.DEFAULT);
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
        this.activeChannelMap.put(player.getUniqueId(), channel.getId());
        ChatLang.CHANNEL_SET_SUCCESS.getMessage().replace(channel.replacePlaceholders()).send(player);
        return true;
    }

    public boolean joinChannel(@NotNull Player player, @NotNull ChatChannel channel) {
        return this.joinChannel(player, channel, false);
    }

    public boolean joinChannel(@NotNull Player player, @NotNull ChatChannel channel, boolean isSilent) {
        if (!channel.canJoin(player)) {
            if (!isSilent) {
                ChatLang.CHANNEL_JOIN_ERROR_NO_PERMISSION.getMessage().replace(channel.replacePlaceholders()).send(player);
            }
            return false;
        }

        if (channel.join(player)) {
            if (!isSilent) {
                ChatLang.CHANNEL_JOIN_SUCCESS.getMessage().replace(channel.replacePlaceholders()).send(player);
            }
            return true;
        }

        if (!isSilent) {
            ChatLang.CHANNEL_JOIN_ERROR_ALREADY_IN.getMessage().replace(channel.replacePlaceholders()).send(player);
        }

        return false;
    }

    public boolean leaveChannel(@NotNull Player player, @NotNull ChatChannel channel) {
        if (channel.removePlayer(player)) {
            ChatLang.CHANNEL_LEAVE_SUCCESS.getMessage().replace(channel.replacePlaceholders()).send(player);
            return true;
        }

        ChatLang.CHANNEL_LEAVE_ERROR_NOT_IN.getMessage().replace(channel.replacePlaceholders()).send(player);
        return false;
    }

    public void leaveAllChannels(@NotNull Player player) {
        this.getChannels().forEach(channel -> channel.removePlayer(player));
        this.activeChannelMap.remove(player.getUniqueId());
    }
}

