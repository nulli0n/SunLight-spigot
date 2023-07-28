package su.nightexpress.sunlight.module.kits.command.kits.child;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.kits.Kit;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.config.KitsLang;
import su.nightexpress.sunlight.module.kits.config.KitsPerms;

import java.util.List;

public class ResetCooldownSubCommand extends ModuleCommand<KitsModule> {

    public ResetCooldownSubCommand(@NotNull KitsModule module) {
        super(module, new String[]{"resetcooldown"}, KitsPerms.COMMAND_KITS_RESET_COOLDOWN);
        this.setDescription(plugin.getMessage(KitsLang.COMMAND_KITS_RESET_COOLDOWN_DESC));
        this.setUsage(plugin.getMessage(KitsLang.COMMAND_KITS_RESET_COOLDOWN_USAGE));

        this.addFlag(CommandFlags.SILENT);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return this.module.getKitIds();
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

        Kit kit = this.module.getKitById(result.getArg(1));
        if (kit == null) {
            this.plugin.getMessage(KitsLang.KIT_ERROR_INVALID).send(sender);
            return;
        }

        this.plugin.getUserManager().getUserDataAsync(result.getArg(2, sender.getName())).thenAccept(user -> {
            if (user == null) {
                this.errorPlayer(sender);
                return;
            }

            if (user.removeCooldown(kit)) {
                user.saveData(this.plugin);
            }

            if (!user.getName().equalsIgnoreCase(sender.getName())) {
                this.plugin.getMessage(KitsLang.COMMAND_KITS_RESET_COOLDOWN_DONE)
                    .replace(kit.replacePlaceholders())
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                    .send(sender);
            }

            Player target = user.getPlayer();
            if (!result.hasFlag(CommandFlags.SILENT) && target != null) {
                this.plugin.getMessage(KitsLang.COMMAND_KITS_RESET_COOLDOWN_NOTIFY)
                    .replace(kit.replacePlaceholders())
                    .send(target);
            }
        });
    }
}
