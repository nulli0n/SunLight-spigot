package su.nightexpress.sunlight.module.homes.command.basic;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.config.HomesLang;
import su.nightexpress.sunlight.module.homes.util.HomesPerms;

import java.util.List;
import java.util.Map;

public class HomesListCommand extends ModuleCommand<HomesModule> {

    public static final String NAME = "list";

    HomesListCommand(@NotNull HomesModule homesModule) {
        super(homesModule, new String[]{NAME}, HomesPerms.COMMAND_HOMES_LIST);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(HomesLang.COMMAND_HOMES_LIST_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(HomesLang.COMMAND_HOMES_LIST_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return CollectionsUtil.playerNames(player);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
        Player player = (Player) sender;
        if (args.length <= 1) {
            this.module.getHomesMenu().open(player, 1);
            return;
        }
        if (!sender.hasPermission(HomesPerms.COMMAND_HOMES_LIST_OTHERS)) {
            this.errorPermission(sender);
            return;
        }

        String userName = args[1];
        this.plugin.getUserManager().getUserDataAsync(userName).thenAcceptAsync(user -> {
            if (user == null) {
                this.errorPlayer(sender);
                return;
            }
            this.module.loadHomesIfAbsent(user.getId());
            this.plugin.runTask(task -> this.module.getHomesMenu().open(player, user.getId()));
        });
    }
}
