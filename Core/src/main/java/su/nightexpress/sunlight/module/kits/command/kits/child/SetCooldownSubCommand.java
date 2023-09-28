package su.nightexpress.sunlight.module.kits.command.kits.child;

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
import su.nightexpress.sunlight.module.kits.Kit;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.config.KitsLang;
import su.nightexpress.sunlight.module.kits.config.KitsPerms;

import java.util.Arrays;
import java.util.List;

public class SetCooldownSubCommand extends ModuleCommand<KitsModule> {

    public SetCooldownSubCommand(@NotNull KitsModule module) {
        super(module, new String[]{"setcooldown"}, KitsPerms.COMMAND_KITS_SET_COOLDOWN);
        this.setDescription(plugin.getMessage(KitsLang.COMMAND_KITS_SET_COOLDOWN_DESC));
        this.setUsage(plugin.getMessage(KitsLang.COMMAND_KITS_SET_COOLDOWN_USAGE));

        this.addFlag(CommandFlags.SILENT);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return this.module.getKitIds();
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

        Kit kit = this.module.getKitById(result.getArg(1));
        if (kit == null) {
            this.plugin.getMessage(KitsLang.KIT_ERROR_INVALID).send(sender);
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

            user.addCooldown(new CooldownInfo(CooldownType.KIT, kit.getId(), expireDate));
            this.plugin.getUserManager().saveUser(user);

            String time = amount < 0 ? LangManager.getPlain(Lang.OTHER_INFINITY) : TimeUtil.formatTimeLeft(expireDate + 100L);

            if (!user.getName().equalsIgnoreCase(sender.getName())) {
                this.plugin.getMessage(KitsLang.COMMAND_KITS_SET_COOLDOWN_DONE)
                    .replace(kit.replacePlaceholders())
                    .replace(Placeholders.GENERIC_AMOUNT, time)
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                    .send(sender);
            }

            Player target = user.getPlayer();
            if (!result.hasFlag(CommandFlags.SILENT) && target != null) {
                this.plugin.getMessage(KitsLang.COMMAND_KITS_SET_COOLDOWN_NOTIFY)
                    .replace(Placeholders.GENERIC_AMOUNT, time)
                    .replace(kit.replacePlaceholders())
                    .send(target);
            }
        });
    }
}
