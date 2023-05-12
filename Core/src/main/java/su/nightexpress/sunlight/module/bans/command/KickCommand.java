package su.nightexpress.sunlight.module.bans.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.Placeholders;
import su.nightexpress.sunlight.module.GeneralModuleCommand;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.config.BansConfig;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentReason;
import su.nightexpress.sunlight.module.bans.util.BansPerms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KickCommand extends GeneralModuleCommand<BansModule> {

    public static final String NAME = "kick";

    public KickCommand(@NotNull BansModule bansModule, @NotNull String[] aliases) {
        super(bansModule, aliases, BansPerms.COMMAND_KICK);
        this.setDescription(plugin.getMessage(BansLang.COMMAND_KICK_DESC));
        this.setUsage(plugin.getMessage(BansLang.COMMAND_KICK_USAGE));
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return CollectionsUtil.playerNames(player);
        }
        if (arg == 2) {
            return new ArrayList<>(BansConfig.GENERAL_REASONS.get().keySet());
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
        if (args.length < 1) {
            this.printUsage(sender);
            return;
        }

        Player player = plugin.getServer().getPlayer(args[0]);
        if (player == null) {
            this.errorPlayer(sender);
            return;
        }

        int reasonIndex = 1;
        String reasonMsg;
        PunishmentReason reason = args.length > reasonIndex ? this.module.getReason(args[reasonIndex]) : this.module.getReason(Placeholders.DEFAULT);

        if (reason != null) {
            reasonMsg = reason.getMessage();
        }
        else {
            reasonMsg = Colorizer.apply(Stream.of(args).skip(reasonIndex).collect(Collectors.joining(" ")));
        }

        this.module.kick(player.getName(), sender, reasonMsg);
    }
}
