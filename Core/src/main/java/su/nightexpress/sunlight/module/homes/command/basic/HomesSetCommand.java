package su.nightexpress.sunlight.module.homes.command.basic;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.Placeholders;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.config.HomesLang;
import su.nightexpress.sunlight.module.homes.config.HomesPerms;

import java.util.Collections;
import java.util.List;

public class HomesSetCommand extends ModuleCommand<HomesModule> {

    public static final String NAME = "set";

    HomesSetCommand(@NotNull HomesModule homesModule) {
        super(homesModule, new String[]{NAME}, HomesPerms.COMMAND_HOMES_SET);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(HomesLang.COMMAND_HOMES_SET_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(HomesLang.COMMAND_HOMES_SET_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return Collections.singletonList(this.getUsage());
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player player = (Player) sender;
        String homeId = result.length() >= 2 ? result.getArg(1).toLowerCase() : Placeholders.DEFAULT;
        this.module.setHome(player, homeId, false);
    }
}
