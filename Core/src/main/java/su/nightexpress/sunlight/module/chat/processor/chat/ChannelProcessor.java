package su.nightexpress.sunlight.module.chat.processor.chat;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.time.TimeFormatType;
import su.nightexpress.nightcore.util.time.TimeFormats;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.chat.channel.ChatChannel;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.cache.UserChatCache;
import su.nightexpress.sunlight.module.chat.core.ChatLang;
import su.nightexpress.sunlight.module.chat.core.ChatPerms;
import su.nightexpress.sunlight.module.chat.context.MessageContext;
import su.nightexpress.sunlight.module.chat.processor.MessageProcessor;

public class ChannelProcessor implements MessageProcessor {

    private final SunLightPlugin plugin;

    public ChannelProcessor(@NotNull SunLightPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void preProcess(@NotNull ChatModule module, @NotNull MessageContext context) {
        Player player = context.getPlayer();
        ChatChannel channel = context.getChannel();
        UserChatCache cache = context.getCache();

        if (!channel.canSpeakHere(player)) {
            module.sendPrefixed(ChatLang.CHANNEL_SPEAK_NO_PERMISSION, player, builder -> builder.with(channel.placeholders()));
            context.cancel();
            return;
        }

        // Check channel cooldown.
        if (cache.hasChannelCooldown(channel.getId())) {
            context.cancel();
            module.sendPrefixed(ChatLang.CHANNEL_MESSAGE_COOLDOWN, player, builder -> builder
                .with(SLPlaceholders.GENERIC_TIME, () -> TimeFormats.formatDuration(cache.getChannelCooldownTimestamp(channel.getId()), TimeFormatType.LITERAL))
            );
            return;
        }

        // Remove channel prefix from the message.
        if (channel.hasPrefix() && context.getMessage().charAt(0) == channel.getPrefixChar()) {
            context.setMessage(context.getMessage().substring(1).trim());
        }

        // Do not send empty messages, mimic default chat behavior.
        if (context.getMessage().isBlank()) {
            context.cancel();
            return;
        }

        // Add player to the channel, so they can listen for new messages.
        if (!channel.contains(player)) {
            module.joinChannel(player, channel, true);
        }

        context.getViewers().removeIf(sender -> !channel.isInChannelRadius(sender, player));
    }

    @Override
    public void postProcess(@NotNull ChatModule module, @NotNull MessageContext context) {
        Player player = context.getPlayer();

        if (this.isAlone(player, context)) {
            // One tick delay to send after player's message.
            this.plugin.runTask(() -> module.sendPrefixed(ChatLang.CHANNEL_NOBODY_HERE, player));
        }

        if (!player.hasPermission(ChatPerms.BYPASS_CHANNEL_COOLDOWN)) {
            UserChatCache cache = context.getCache();
            ChatChannel channel = context.getChannel();
            int cooldown = channel.getAccessibility().messageCooldown();
            if (cooldown <= 0) return;

            cache.setChannelCooldown(channel.getId(), cooldown);
        }
    }

    private boolean isAlone(@NotNull Player player, @NotNull MessageContext context) {
        return context.getViewers().stream().noneMatch(sender -> sender != player && !(sender instanceof ConsoleCommandSender));
    }
}
