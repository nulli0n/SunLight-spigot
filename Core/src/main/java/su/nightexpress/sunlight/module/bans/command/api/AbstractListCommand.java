package su.nightexpress.sunlight.module.bans.command.api;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.server.JPermission;
import su.nightexpress.sunlight.module.GeneralModuleCommand;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;

public abstract class AbstractListCommand extends GeneralModuleCommand<BansModule> {

    protected final PunishmentType punishmentType;

    public AbstractListCommand(@NotNull BansModule bansModule, @NotNull String[] aliases, @Nullable JPermission permission,
                               @NotNull PunishmentType punishmentType) {
        super(bansModule, aliases, permission);
        this.punishmentType = punishmentType;
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player player = (Player) sender;
        this.module.getListMenu().open(player, this.punishmentType);
    }
}
