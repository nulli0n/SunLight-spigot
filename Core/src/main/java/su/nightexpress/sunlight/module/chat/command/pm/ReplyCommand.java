package su.nightexpress.sunlight.module.chat.command.pm;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.chat.config.ChatPerms;
import su.nightexpress.sunlight.module.chat.config.ChatLang;

public class ReplyCommand extends PrivateMessageCommand {

    public static final String NAME = "reply";

    public ReplyCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, ChatPerms.COMMAND_REPLY, Type.REPLY);
        this.setDescription(plugin.getMessage(ChatLang.COMMAND_REPLY_DESC));
        this.setUsage(plugin.getMessage(ChatLang.COMMAND_REPLY_USAGE));
    }
}
