package su.nightexpress.sunlight.module.chat.command.channel;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nightexpress.sunlight.module.GeneralModuleCommand;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.config.ChatPerms;
import su.nightexpress.sunlight.module.chat.config.ChatLang;

public class ChatChannelCommand extends GeneralModuleCommand<ChatModule> {

    public static final String NAME = "chatchannel";

    public ChatChannelCommand(@NotNull ChatModule chatModule, @NotNull String[] aliases) {
        super(chatModule, aliases, ChatPerms.COMMAND_CHATCHANNEL);
        this.setDescription(this.plugin.getMessage(ChatLang.COMMAND_CHANNEL_DESC));
        this.setPlayerOnly(true);

        this.addDefaultCommand(new HelpSubCommand<>(chatModule.plugin()));
        this.addChildren(new JoinSubCommand(chatModule));
        this.addChildren(new LeaveSubCommand(chatModule));
        this.addChildren(new SetSubCommand(chatModule));
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {

    }
}
