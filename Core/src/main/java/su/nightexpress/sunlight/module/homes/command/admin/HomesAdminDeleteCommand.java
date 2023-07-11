package su.nightexpress.sunlight.module.homes.command.admin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.config.HomesLang;
import su.nightexpress.sunlight.module.homes.util.HomesPerms;
import su.nightexpress.sunlight.module.homes.util.Placeholders;
import su.nightexpress.sunlight.utils.UserInfo;

import java.util.*;

public class HomesAdminDeleteCommand extends ModuleCommand<HomesModule> {

    public static final String NAME = "delete";

    HomesAdminDeleteCommand(@NotNull HomesModule module) {
        super(module, new String[]{NAME}, HomesPerms.COMMAND_HOMES_ADMIN);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(HomesLang.COMMAND_HOMES_ADMIN_DELETE_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(HomesLang.COMMAND_HOMES_ADMIN_DELETE_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return CollectionsUtil.playerNames(player);
        }
        if (arg == 2) {
            List<String> homeIds = new ArrayList<>();
            homeIds.add(Placeholders.WILDCARD);
            homeIds.addAll(this.module.getCache().getUserHomes(args[1]).stream().sorted().toList());
            return homeIds;
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        String userName = result.getArg(1);
        String homeId = result.length() >= 3 ? result.getArg(2).toLowerCase() : Placeholders.DEFAULT;
        this.plugin.getUserManager().getUserDataAsync(userName).thenAcceptAsync(user -> {
            if (user == null) {
                this.errorPlayer(sender);
                return;
            }

            UUID userId = user.getId();
            if (homeId.equalsIgnoreCase(Placeholders.WILDCARD)) {
                this.module.getCache().getUserHomes(user.getName()).stream().toList().forEach(homeId2 -> {
                    this.module.getCache().uncache(user.getName(), homeId2);
                });
                this.module.unloadHomes(userId);
                this.plugin.getData().deleteHomes(userId);
                this.plugin.getMessage(HomesLang.COMMAND_HOMES_ADMIN_DELETE_DONE_ALL)
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                    .send(sender);
            }
            else {
                if (!this.module.getCache().getUserHomes(user.getName()).contains(homeId)) {
                    plugin.getMessage(HomesLang.HOME_ERROR_INVALID).send(sender);
                    return;
                }
                this.module.deleteHome(new UserInfo(user), homeId);
                this.plugin.getMessage(HomesLang.COMMAND_HOMES_ADMIN_DELETE_DONE_SINGLE)
                    .replace(Placeholders.HOME_ID, homeId)
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                    .send(sender);
            }
        });
    }
}
