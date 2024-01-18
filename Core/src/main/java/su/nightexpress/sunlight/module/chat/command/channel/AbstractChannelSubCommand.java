package su.nightexpress.sunlight.module.chat.command.channel;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.chat.ChatChannel;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.config.ChatLang;

import java.util.List;

abstract class AbstractChannelSubCommand extends AbstractCommand<SunLight> {

    protected final ChatModule module;

    public AbstractChannelSubCommand(@NotNull ChatModule module, @NotNull String[] aliases, @Nullable Permission permission) {
        super(module.plugin(), aliases, permission);
        this.module = module;
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    protected abstract void perform(@NotNull Player player, @NotNull ChatChannel channel);

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return this.module.getChannelManager().getAvailableChannels(player).stream().map(ChatChannel::getId).toList();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        ChatChannel channel = this.module.getChannelManager().getChannel(result.getArg(1));
        if (channel == null) {
            this.plugin.getMessage(ChatLang.CHANNEL_ERROR_INVALID).send(sender);
            return;
        }

        Player player = (Player) sender;
        this.perform(player, channel);
    }
}
