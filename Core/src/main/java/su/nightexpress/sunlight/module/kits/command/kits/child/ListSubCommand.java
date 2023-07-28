package su.nightexpress.sunlight.module.kits.command.kits.child;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.config.KitsLang;
import su.nightexpress.sunlight.module.kits.config.KitsPerms;
import su.nightexpress.sunlight.module.kits.util.Placeholders;

import java.util.List;

public class ListSubCommand extends ModuleCommand<KitsModule> {

    public ListSubCommand(@NotNull KitsModule module) {
        super(module, new String[]{"list"}, KitsPerms.COMMAND_KITS_LIST);
        this.setDescription(plugin.getMessage(KitsLang.COMMAND_KITS_LIST_DESC));
        this.setUsage(plugin.getMessage(KitsLang.COMMAND_KITS_LIST_USAGE));
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1 && player.hasPermission(KitsPerms.COMMAND_KITS_LIST_OTHERS)) {
            return CollectionsUtil.playerNames(player);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() >= 2 && !sender.hasPermission(KitsPerms.COMMAND_KITS_LIST_OTHERS)) {
            this.errorPermission(sender);
            return;
        }

        Player target = plugin.getServer().getPlayer(result.getArg(1, sender.getName()));
        if (target == null) {
            this.errorPlayer(sender);
            return;
        }

        this.module.getKitsMenu().open(target, 1);

        if (sender != target) {
            this.plugin.getMessage(KitsLang.COMMAND_KITS_LIST_OTHERS)
                .replace(Placeholders.forPlayer(target))
                .send(sender);
        }
    }
}
