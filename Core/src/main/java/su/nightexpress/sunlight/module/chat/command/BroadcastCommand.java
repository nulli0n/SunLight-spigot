package su.nightexpress.sunlight.module.chat.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.Colorizer;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;

import java.util.List;
import java.util.stream.Stream;

@Deprecated
// TODO Move in the chat module?
// TODO Add broadcast types? Like Info/Ad/etc?
public class BroadcastCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "broadcast";

    public BroadcastCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.CMD_BROADCAST);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(Lang.Command_Broadcast_Usage).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(Lang.Command_Broadcast_Desc).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        return super.getTab(player, i, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() == 0) {
            this.printUsage(sender);
            return;
        }

        String broadcast = Colorizer.apply(String.join(" ", Stream.of(result.getArgs()).toList())).trim();
        plugin.getMessage(Lang.Command_Broadcast_Format).replace("%msg%", broadcast).broadcast();
    }
}
