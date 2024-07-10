package su.nightexpress.sunlight.module.chat;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.sunlight.module.chat.config.ChatConfig;
import su.nightexpress.sunlight.module.chat.util.ChatUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerChatData {

    private final UUID holder;
    private final Map<String, Long> messageCooldownMap;
    private final Map<String, Long> mentionCooldownMap;

    private UUID   lastConversationWith;
    private String lastMessage;
    private String lastCommand;
    private long lastMessageExpireTimestamp;
    private long lastCommandExpireTimestamp;
    private long nextCommandTimestamp;

    public PlayerChatData(@NotNull Player player) {
        this.holder = player.getUniqueId();
        this.messageCooldownMap = new HashMap<>();
        this.mentionCooldownMap = new HashMap<>();

        this.lastMessageExpireTimestamp = 0L;
        this.lastCommandExpireTimestamp = 0L;
    }

    @NotNull
    public UUID getHolder() {
        return holder;
    }

    @Nullable
    public UUID getLastConversationWith() {
        return lastConversationWith;
    }

    public void setLastConversationWith(@Nullable UUID lastConversationWith) {
        this.lastConversationWith = lastConversationWith;
    }

    public long getNextMessageTimestamp(@NotNull ChatChannel channel) {
        return this.messageCooldownMap.getOrDefault(channel.getId(), 0L);
    }

    public long getNextCommandTimestamp() {
        return nextCommandTimestamp;
    }



    public boolean isSpamMessage(@NotNull String message) {
        return isSimilarEnough(message, this.getLastMessage());
    }

    public boolean isSpamCommand(@NotNull String message) {
        return isSimilarEnough(message, this.getLastCommand());
    }

    private boolean isSimilarEnough(@NotNull String message, @Nullable String lastMessage) {
        if (lastMessage == null || lastMessage.isBlank()) return false;

        String rawMessage = ChatUtils.cleanMessage(message);

        double value = ChatUtils.getSimilarityPercent(rawMessage, lastMessage);
        double threshold = ChatConfig.ANTI_SPAM_BLOCK_SIMILAR_PERCENT.get() / 100D;

        return value >= threshold;
    }



    public void setLastMessage(@NotNull String message) {
        int cooldown = ChatConfig.ANTI_SPAM_BLOCK_SIMILAR_COOLDOWN.get();
        long expireDate = cooldown <= 0 ? -1 : System.currentTimeMillis() + cooldown * 1000L;

        this.lastMessage = ChatUtils.cleanMessage(message);
        this.lastMessageExpireTimestamp = expireDate;
    }

    @Nullable
    public String getLastMessage() {
        if (this.lastMessageExpireTimestamp > 0 && System.currentTimeMillis() >= this.lastMessageExpireTimestamp) {
            this.lastMessage = null;
        }

        return this.lastMessage;
    }



    public void setLastCommand(@NotNull String command) {
        int cooldown = ChatConfig.ANTI_SPAM_BLOCK_SIMILAR_COOLDOWN.get();
        long expireDate = cooldown <= 0 ? -1 : System.currentTimeMillis() + cooldown * 1000L;

        this.lastCommand = ChatUtils.cleanMessage(command);
        this.lastCommandExpireTimestamp = expireDate;
    }

    @Nullable
    public String getLastCommand() {
        if (this.lastCommandExpireTimestamp > 0 && System.currentTimeMillis() >= this.lastCommandExpireTimestamp) {
            this.lastCommand = null;
        }

        return this.lastCommand;
    }



    public void setCommandCooldown() {
        long cooldown = (long) (ChatConfig.ANTI_SPAM_COMMAND_COOLDOWN.get() * 1000L);
        if (cooldown <= 0) return;

        this.nextCommandTimestamp = System.currentTimeMillis() + cooldown;
    }

    public boolean hasCommandCooldown() {
        return System.currentTimeMillis() < this.getNextCommandTimestamp();
    }



    public void setMessageCooldown(@NotNull ChatChannel channel) {
        long cooldown = channel.getMessageCooldown() * 1000L;
        if (cooldown <= 0) return;

        long timestamp = System.currentTimeMillis() + cooldown;
        this.messageCooldownMap.put(channel.getId(), timestamp);
    }

    public boolean hasMessageCooldown(@NotNull ChatChannel channel) {
        return System.currentTimeMillis() < this.getNextMessageTimestamp(channel);
    }



    public void setMentionCooldown(@NotNull String mention) {
        long cooldown = ChatConfig.MENTIONS_COOLDOWN.get() * 1000L;
        if (cooldown <= 0) return;

        this.mentionCooldownMap.put(mention.toLowerCase(), System.currentTimeMillis() + cooldown);
    }

    public long getNextMentionTimestamp(@NotNull String mention) {
        return this.mentionCooldownMap.getOrDefault(mention.toLowerCase(), 0L);
    }

    public boolean hasMentionCooldown(@NotNull String mention) {
        return System.currentTimeMillis() < this.getNextMentionTimestamp(mention);
    }
}
