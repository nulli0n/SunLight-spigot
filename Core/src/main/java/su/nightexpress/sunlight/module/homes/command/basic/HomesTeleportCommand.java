package su.nightexpress.sunlight.module.homes.command.basic;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.config.HomesLang;
import su.nightexpress.sunlight.module.homes.impl.Home;
import su.nightexpress.sunlight.module.homes.util.HomesPerms;

import java.util.List;
import java.util.Map;

public class HomesTeleportCommand extends ModuleCommand<HomesModule> {

    public static final String NAME = "teleport";

    HomesTeleportCommand(@NotNull HomesModule homesModule) {
        super(homesModule, new String[]{NAME, "tp"}, HomesPerms.COMMAND_HOMES_TELEPORT);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(HomesLang.COMMAND_HOMES_TELEPORT_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(HomesLang.COMMAND_HOMES_TELEPORT_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return this.module.getHomes(player).values().stream().map(Home::getId).toList();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
        Player player = (Player) sender;
        Home home;
        if (args.length < 2) {
            home = this.module.getHomeDefault(player).orElse(null);
            if (home == null) {
                this.printUsage(sender);
                return;
            }
        }
        else {
            home = this.module.getHome(player, args[1]).orElse(null);
            if (home == null) {
                plugin.getMessage(HomesLang.HOME_ERROR_INVALID).send(sender);
                return;
            }
        }
        home.teleport(player);
    }
}
