package su.nightexpress.sunlight.module.chat.mention;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.chat.ChatChannel;
import su.nightexpress.sunlight.module.chat.config.ChatConfig;
import su.nightexpress.sunlight.module.chat.config.ChatPerms;
import su.nightexpress.sunlight.module.chat.util.Placeholders;

import java.util.Collections;
import java.util.Set;

public class PlayerMention implements Mention {

    private final Player target;

    public PlayerMention(@NotNull Player target) {
        this.target = target;
    }

    @Override
    public boolean hasPermission(@NotNull Player player) {
        return player.hasPermission(ChatPerms.MENTION) || player.hasPermission(ChatPerms.MENTION_PLAYER + this.target.getName().toLowerCase());
    }

    @Override
    @NotNull
    public String getFormat() {
        return Placeholders.forPlayer(this.target).apply(ChatConfig.MENTIONS_FORMAT.get());
    }

    @Override
    @NotNull
    public Set<Player> getAffectedPlayers(@NotNull ChatChannel channel) {
        return channel.contains(this.target) ? Set.of(this.target) : Collections.emptySet();
    }
}
