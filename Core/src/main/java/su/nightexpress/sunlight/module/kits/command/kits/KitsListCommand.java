package su.nightexpress.sunlight.module.kits.command.kits;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.config.KitsLang;
import su.nightexpress.sunlight.module.kits.util.KitsPerms;

import java.util.List;
import java.util.Map;

public class KitsListCommand extends ModuleCommand<KitsModule> {

    public KitsListCommand(@NotNull KitsModule module) {
        super(module, new String[]{"list"}, KitsPerms.COMMAND_KITS_LIST);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(KitsLang.COMMAND_KITS_LIST_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(KitsLang.COMMAND_KITS_LIST_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
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
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
        Player pTarget = plugin.getServer().getPlayer(args.length >= 2 && sender.hasPermission(KitsPerms.COMMAND_KITS_LIST_OTHERS) ? args[1] : sender.getName());
        if (pTarget == null) {
            this.errorPlayer(sender);
            return;
        }

        this.module.getKitsMenu().open(pTarget, 1);
    }
}
