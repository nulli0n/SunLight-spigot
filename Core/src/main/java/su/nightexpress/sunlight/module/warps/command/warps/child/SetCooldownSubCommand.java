package su.nightexpress.sunlight.module.warps.command.warps.child;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.TimeUtil;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.cooldown.CooldownInfo;
import su.nightexpress.sunlight.data.impl.cooldown.CooldownType;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.module.warps.config.WarpsLang;
import su.nightexpress.sunlight.module.warps.config.WarpsPerms;
import su.nightexpress.sunlight.module.warps.impl.Warp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetCooldownSubCommand extends ModuleCommand<WarpsModule> {

    public SetCooldownSubCommand(@NotNull WarpsModule module) {
        super(module, new String[]{"setcooldown"}, WarpsPerms.COMMAND_WARPS_SET_COOLDOWN);
        this.setDescription(plugin.getMessage(WarpsLang.COMMAND_WARPS_SET_COOLDOWN_DESC));
        this.setUsage(plugin.getMessage(WarpsLang.COMMAND_WARPS_SET_COOLDOWN_USAGE));

        this.addFlag(CommandFlags.SILENT);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return new ArrayList<>(this.module.getWarpsMap().keySet());
        }
        if (arg == 2) {
            return Arrays.asList("60", "3600", "86400");
        }
        if (arg == 3) {
            return CollectionsUtil.playerNames(player);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 3) {
            this.printUsage(sender);
            return;
        }

        Warp warp = this.module.getWarpById(result.getArg(1));
        if (warp == null) {
            this.plugin.getMessage(WarpsLang.WARP_ERROR_INVALID).send(sender);
            return;
        }

        int amount = result.getInt(2, 0);
        if (amount == 0) return;

        long expireDate = amount < 0 ? -1L : System.currentTimeMillis() + amount * 1000L;

        this.plugin.getUserManager().getUserDataAsync(result.getArg(3, sender.getName())).thenAccept(user -> {
            if (user == null) {
                this.errorPlayer(sender);
                return;
            }

            user.addCooldown(new CooldownInfo(CooldownType.WARP, warp.getId(), expireDate));
            user.saveData(this.plugin);

            String time = amount < 0 ? LangManager.getPlain(Lang.OTHER_INFINITY) : TimeUtil.formatTimeLeft(expireDate + 100L);

            if (!user.getName().equalsIgnoreCase(sender.getName())) {
                this.plugin.getMessage(WarpsLang.COMMAND_WARPS_SET_COOLDOWN_DONE)
                    .replace(warp.replacePlaceholders())
                    .replace(Placeholders.GENERIC_AMOUNT, time)
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                    .send(sender);
            }

            Player target = user.getPlayer();
            if (!result.hasFlag(CommandFlags.SILENT) && target != null) {
                this.plugin.getMessage(WarpsLang.COMMAND_WARPS_SET_COOLDOWN_NOTIFY)
                    .replace(Placeholders.GENERIC_AMOUNT, time)
                    .replace(warp.replacePlaceholders())
                    .send(target);
            }
        });
    }
}
