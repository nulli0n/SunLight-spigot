package su.nightexpress.sunlight.module.chat.command.tell;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.chat.ChatPerms;
import su.nightexpress.sunlight.module.chat.config.ChatLang;

public class ReplyCommand extends PrivateMessageCommand {

    public static final String NAME = "reply";

    public ReplyCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, ChatPerms.COMMAND_REPLY, Type.REPLY);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(ChatLang.COMMAND_REPLY_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(ChatLang.COMMAND_REPLY_DESC).getLocalized();
    }
}
