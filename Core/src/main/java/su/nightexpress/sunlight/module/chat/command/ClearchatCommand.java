package su.nightexpress.sunlight.module.chat.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.chat.ChatPerms;
import su.nightexpress.sunlight.module.chat.config.ChatLang;

import java.util.Map;

public class ClearchatCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "clearchat";

    public ClearchatCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, ChatPerms.COMMAND_CLEARCHAT);
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(ChatLang.COMMAND_CLEAR_CHAT_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
        for (int i = 0; i < 100; i++) {
            plugin.getServer().broadcastMessage(" ");
        }
        plugin.getMessage(ChatLang.COMMAND_CLEAR_CHAT_DONE).replace(Placeholders.Player.replacer(sender)).broadcast();
    }
}
