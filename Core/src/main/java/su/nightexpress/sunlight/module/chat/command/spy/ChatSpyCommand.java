package su.nightexpress.sunlight.module.chat.command.spy;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.chat.config.ChatPerms;
import su.nightexpress.sunlight.module.chat.config.ChatLang;

public class ChatSpyCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "chatspy";

    public ChatSpyCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, ChatPerms.COMMAND_SPY);
        this.addDefaultCommand(new HelpSubCommand<>(plugin));
        this.addChildren(new ModeSubCommand(plugin));
        this.addChildren(new LoggerSubCommand(plugin));
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(ChatLang.COMMAND_SPY_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(ChatLang.COMMAND_SPY_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    protected void onExecute(@NotNull CommandSender commandSender, @NotNull CommandResult commandResult) {

    }
}
