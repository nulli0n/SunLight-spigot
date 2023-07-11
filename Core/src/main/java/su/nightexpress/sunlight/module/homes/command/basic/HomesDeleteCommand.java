package su.nightexpress.sunlight.module.homes.command.basic;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.Placeholders;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.config.HomesLang;
import su.nightexpress.sunlight.module.homes.impl.Home;
import su.nightexpress.sunlight.module.homes.util.HomesPerms;

import java.util.List;

public class HomesDeleteCommand extends ModuleCommand<HomesModule> {

    public static final String NAME = "delete";

    HomesDeleteCommand(@NotNull HomesModule homesModule) {
        super(homesModule, new String[]{NAME}, HomesPerms.COMMAND_HOMES_DELETE);
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.plugin.getMessage(HomesLang.COMMAND_HOMES_DELETE_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.plugin.getMessage(HomesLang.COMMAND_HOMES_DELETE_DESC).getLocalized();
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
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player player = (Player) sender;
        String homeId = result.length() >= 2 ? result.getArg(1) : Placeholders.DEFAULT;
        this.module.removeHome(player, homeId);
    }
}
