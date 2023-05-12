package su.nightexpress.sunlight.module.bans.command.api;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.server.JPermission;
import su.nexmedia.engine.utils.regex.RegexUtil;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.module.GeneralModuleCommand;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.punishment.Punishment;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;

import java.util.List;
import java.util.Map;

public abstract class AbstractHistoryCommand extends GeneralModuleCommand<BansModule> {

    protected final PunishmentType type;

    public AbstractHistoryCommand(@NotNull BansModule module,
                                  @NotNull String[] aliases, @Nullable JPermission permission,
                                  @NotNull PunishmentType type) {
        super(module, aliases, permission);
        this.type = type;
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return this.module.getPunishments(this.type).stream().map(Punishment::getUser).toList();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
        Player player = (Player) sender;
        if (args.length < 1) {
            this.printUsage(sender);
            return;
        }

        // Get the user name to view history of.
        String userName = args[0];

        // If 'userName' is not an IP address, we have to check if user is exists.
        // not needed to open menu for non-existent users.
        // If 'userName' is an IP address, we can open menu even if there are no players with such IP,
        // because we can't check if this IP is valid.
        if (!RegexUtil.isIpAddress(userName)) {
            SunUser user = plugin.getUserManager().getUserData(userName);
            if (user == null && this.module.getPunishments(userName).isEmpty()) {
                this.errorPlayer(sender);
                return;
            }
            //userName = user.getName();
        }
        this.module.getListMenu().open(player, this.type, userName);
    }
}
