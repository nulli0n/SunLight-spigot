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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomesVisitCommand extends ModuleCommand<HomesModule> {

    public static final String NAME = "visit";

    HomesVisitCommand(@NotNull HomesModule homesModule) {
        super(homesModule, new String[]{NAME}, HomesPerms.COMMAND_HOMES_VISIT);
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.plugin.getMessage(HomesLang.COMMAND_HOMES_VISIT_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.plugin.getMessage(HomesLang.COMMAND_HOMES_VISIT_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            Set<String> invitesAndPublics = new HashSet<>();
            if (player.hasPermission(HomesPerms.COMMAND_HOMES_VISIT_ALL)) {
                invitesAndPublics.addAll(this.module.getCache().getUserHomes().keySet());
            }
            else {
                invitesAndPublics.addAll(this.module.getCache().getPublicHomes().keySet());
                invitesAndPublics.addAll(this.module.getCache().getInviteOwnersHomes(player.getName()));
            }
            invitesAndPublics.remove(player.getName());
            return invitesAndPublics.stream().sorted().toList();
        }
        if (arg == 2) {
            String ownerName = args[arg - 1];
            Set<String> invitesAndPublics = new HashSet<>();
            if (player.hasPermission(HomesPerms.COMMAND_HOMES_VISIT_ALL)) {
                invitesAndPublics.addAll(this.module.getCache().getUserHomes(ownerName));
            }
            else {
                invitesAndPublics.addAll(this.module.getCache().getPublicHomes(ownerName));
                invitesAndPublics.addAll(this.module.getCache().getInvitesHomes(ownerName, player.getName()));
            }
            return invitesAndPublics.stream().sorted().toList();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        Player player = (Player) sender;
        String ownerName = result.getArg(1);
        String id = result.length() >= 3 ? result.getArg(2) : Placeholders.DEFAULT;

        this.plugin.getUserManager().getUserDataAsync(ownerName).thenAccept(user -> {
            if (user == null) {
                this.errorPlayer(sender);
                return;
            }

            this.module.loadHomesIfAbsent(user.getId());

            Home home = this.module.getHome(user.getId(), id).orElse(null);
            if (home == null) {
                this.plugin.getMessage(HomesLang.HOME_ERROR_INVALID).send(sender);
                return;
            }

            if (!player.hasPermission(HomesPerms.COMMAND_HOMES_VISIT_ALL)) {
                if (!home.isPublic() && !home.isInvitedPlayer(player) && !home.isOwner(player)) {
                    plugin.getMessage(HomesLang.HOME_VISIT_ERROR_NOT_PERMITTED)
                        .replace(home.replacePlaceholders())
                        .send(sender);
                    return;
                }
            }

            // Async teleport is illegal.
            plugin.runTask(task -> home.teleport(player));
        });

        /*CompletableFuture.supplyAsync(() -> plugin.getData().getHome(ownerName, id)).thenAccept(home -> {
            if (home == null) {
                this.plugin.getMessage(HomesLang.HOME_ERROR_INVALID).send(sender);
                return;
            }

            if (!player.hasPermission(HomesPerms.COMMAND_HOMES_VISIT_ALL)) {
                if (!home.isPublic() && !home.isInvitedPlayer(player) && !home.isOwner(player)) {
                    plugin.getMessage(HomesLang.HOME_VISIT_ERROR_NOT_PERMITTED)
                        .replace(home.replacePlaceholders())
                        .send(sender);
                    return;
                }
            }

            // Load all homes of that user for a fast future access.
            this.module.loadHomesIfAbsent(home.getOwner().getId());

            // Async teleport is illegal.
            plugin.runTask(task -> home.teleport(player));
        });*/
    }
}
