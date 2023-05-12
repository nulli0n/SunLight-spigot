package su.nightexpress.sunlight.module.rtp.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.module.GeneralModuleCommand;
import su.nightexpress.sunlight.module.rtp.RTPModule;
import su.nightexpress.sunlight.module.rtp.config.RTPLang;
import su.nightexpress.sunlight.module.rtp.config.RTPPerms;

public class RTPCommand extends GeneralModuleCommand<RTPModule> {

    public static final String NAME = "rtp";

    public RTPCommand(@NotNull RTPModule module, @NotNull String[] aliases) {
        super(module, aliases, RTPPerms.COMMAND_RTP);
        this.setDescription(this.plugin.getMessage(RTPLang.COMMAND_RTP_DESC));
        this.setPlayerOnly(true);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player player = (Player) sender;
        this.module.startSearch(player);
    }
}
