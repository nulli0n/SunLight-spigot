package su.nightexpress.sunlight.module.warps.command.warps.child;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.module.warps.config.WarpsLang;
import su.nightexpress.sunlight.module.warps.config.WarpsPerms;
import su.nightexpress.sunlight.module.warps.impl.Warp;

import java.util.ArrayList;
import java.util.List;

public class ResetCooldownSubCommand extends ModuleCommand<WarpsModule> {

    public ResetCooldownSubCommand(@NotNull WarpsModule module) {
        super(module, new String[]{"resetcooldown"}, WarpsPerms.COMMAND_WARPS_RESET_COOLDOWN);
        this.setDescription(plugin.getMessage(WarpsLang.COMMAND_WARPS_RESET_COOLDOWN_DESC));
        this.setUsage(plugin.getMessage(WarpsLang.COMMAND_WARPS_RESET_COOLDOWN_USAGE));

        this.addFlag(CommandFlags.SILENT);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return new ArrayList<>(this.module.getWarpsMap().keySet());
        }
        if (arg == 2) {
            return CollectionsUtil.playerNames(player);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        Warp warp = this.module.getWarpById(result.getArg(1));
        if (warp == null) {
            this.plugin.getMessage(WarpsLang.WARP_ERROR_INVALID).send(sender);
            return;
        }

        this.plugin.getUserManager().getUserDataAsync(result.getArg(2, sender.getName())).thenAccept(user -> {
            if (user == null) {
                this.errorPlayer(sender);
                return;
            }

            if (user.removeCooldown(warp)) {
                user.saveData(this.plugin);
            }

            if (!user.getName().equalsIgnoreCase(sender.getName())) {
                this.plugin.getMessage(WarpsLang.COMMAND_WARPS_SET_COOLDOWN_DONE)
                    .replace(warp.replacePlaceholders())
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                    .send(sender);
            }

            Player target = user.getPlayer();
            if (!result.hasFlag(CommandFlags.SILENT) && target != null) {
                this.plugin.getMessage(WarpsLang.COMMAND_WARPS_SET_COOLDOWN_NOTIFY)
                    .replace(warp.replacePlaceholders())
                    .send(target);
            }
        });
    }
}
