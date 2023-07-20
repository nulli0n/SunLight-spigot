package su.nightexpress.sunlight.module.chat.command.channel;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.chat.ChatChannel;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.config.ChatPerms;
import su.nightexpress.sunlight.module.chat.config.ChatLang;

class SetSubCommand extends AbstractChannelSubCommand {

    public static final String NAME = "set";

    public SetSubCommand(@NotNull ChatModule chatModule) {
        super(chatModule, new String[]{NAME}, ChatPerms.COMMAND_CHATCHANNEL);
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.plugin.getMessage(ChatLang.COMMAND_CHANNEL_SET_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.plugin.getMessage(ChatLang.COMMAND_CHANNEL_SET_DESC).getLocalized();
    }

    @Override
    protected void perform(@NotNull Player player, @NotNull ChatChannel channel) {
        this.chatModule.setChannel(player, channel);
    }
}
