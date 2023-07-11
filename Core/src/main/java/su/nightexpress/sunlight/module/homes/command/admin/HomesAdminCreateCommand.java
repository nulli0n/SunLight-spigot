package su.nightexpress.sunlight.module.homes.command.admin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.config.HomesLang;
import su.nightexpress.sunlight.module.homes.impl.Home;
import su.nightexpress.sunlight.module.homes.util.HomesPerms;
import su.nightexpress.sunlight.module.homes.util.Placeholders;
import su.nightexpress.sunlight.utils.UserInfo;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class HomesAdminCreateCommand extends ModuleCommand<HomesModule> {

    public static final String NAME = "create";

    HomesAdminCreateCommand(@NotNull HomesModule module) {
        super(module, new String[]{NAME}, HomesPerms.COMMAND_HOMES_ADMIN);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(HomesLang.COMMAND_HOMES_ADMIN_CREATE_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(HomesLang.COMMAND_HOMES_ADMIN_CREATE_DESC).getLocalized();
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
        if (arg == 2) {
            return Arrays.asList("[name]", Placeholders.DEFAULT);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 3) {
            this.printUsage(sender);
            return;
        }

        Player player = (Player) sender;
        String userName = result.getArg(1);
        String homeId = result.getArg(2).toLowerCase();

        this.plugin.getUserManager().getUserDataAsync(userName).thenAcceptAsync(user -> {
            if (user == null) {
                this.errorPlayer(sender);
                return;
            }

            UUID userId = user.getId();
            this.module.loadHomesIfAbsent(userId);

            //Home home = this.plugin.getData().getHome(userId, homeId);
            Home home = this.module.getHome(userId, homeId).orElse(null);
            if (home == null) {
                this.plugin.runTask(task -> {
                    this.module.createHome(homeId, new UserInfo(user), player.getLocation());
                    this.plugin.getMessage(HomesLang.COMMAND_HOMES_ADMIN_CREATE_DONE_FRESH)
                        .replace(Placeholders.HOME_ID, homeId)
                        .replace(Placeholders.PLAYER_NAME, user.getName())
                        .send(player);
                });
                return;
            }

            home.setLocation(player.getLocation());
            home.save();
            this.plugin.getMessage(HomesLang.COMMAND_HOMES_ADMIN_CREATE_DONE_EDITED)
                .replace(Placeholders.HOME_ID, homeId)
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .send(player);
        });
    }
}
