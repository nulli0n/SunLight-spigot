package su.nightexpress.sunlight.module.chat.command.channel;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.chat.ChatChannel;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.ChatPerms;
import su.nightexpress.sunlight.module.chat.config.ChatLang;

class JoinSubCommand extends AbstractChannelSubCommand {

    public static final String NAME = "join";

    public JoinSubCommand(@NotNull ChatModule chatModule) {
        super(chatModule, new String[]{NAME}, ChatPerms.COMMAND_CHATCHANNEL);
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.plugin.getMessage(ChatLang.COMMAND_CHANNEL_JOIN_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.plugin.getMessage(ChatLang.COMMAND_CHANNEL_JOIN_DESC).getLocalized();
    }

    @Override
    protected void perform(@NotNull Player player, @NotNull ChatChannel channel) {
        this.chatModule.joinChannel(player, channel);
    }
}
