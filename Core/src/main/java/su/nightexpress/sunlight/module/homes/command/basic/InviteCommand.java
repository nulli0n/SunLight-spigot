package su.nightexpress.sunlight.module.homes.command.basic;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.config.HomesLang;
import su.nightexpress.sunlight.module.homes.config.HomesPerms;
import su.nightexpress.sunlight.module.homes.impl.Home;

import java.util.List;

public class InviteCommand extends ModuleCommand<HomesModule> {

    public static final String NAME = "invite";

    public InviteCommand(@NotNull HomesModule module) {
        super(module, new String[]{NAME}, HomesPerms.COMMAND_HOMES_INVITE);
        this.setDescription(this.plugin.getMessage(HomesLang.COMMAND_HOMES_INVITE_DESC));
        this.setUsage(this.plugin.getMessage(HomesLang.COMMAND_HOMES_INVITE_USAGE));
        this.setPlayerOnly(true);
    }

    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return CollectionsUtil.playerNames(player);
        }
        if (arg == 2) {
            return this.module.getHomes(player).values().stream().map(Home::getId).toList();
        }
        return super.getTab(player, arg, args);
    }

    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        Player player = (Player)sender;
        String userName = result.getArg(1);
        if (player.getName().equalsIgnoreCase(userName)) {
            this.plugin.getMessage(Lang.ERROR_COMMAND_SELF).send(player);
            return;
        }

        Home home = this.module.getHome(player, result.getArg(2, Placeholders.DEFAULT)).orElse(null);
        if (home == null) {
            this.plugin.getMessage(HomesLang.HOME_ERROR_INVALID).send(sender);
            return;
        }

        this.plugin.getUserManager().getUserDataAsync(userName).thenAccept(user -> {
            if (user == null) {
                this.errorPlayer(sender);
                return;
            }
            this.plugin.runTask(task -> {
                if (this.module.addHomeInvite(player, home, user)) {
                    this.plugin.getMessage(HomesLang.COMMAND_HOMES_INVITE_DONE)
                        .replace(Placeholders.PLAYER_NAME, user.getName())
                        .replace(home.replacePlaceholders())
                        .send(sender);
                }
            });
        });
    }
}

