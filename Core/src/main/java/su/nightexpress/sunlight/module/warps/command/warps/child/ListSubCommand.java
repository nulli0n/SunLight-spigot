package su.nightexpress.sunlight.module.warps.command.warps.child;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.module.warps.config.WarpsLang;
import su.nightexpress.sunlight.module.warps.util.Placeholders;
import su.nightexpress.sunlight.module.warps.config.WarpsPerms;

import java.util.List;

public class ListSubCommand extends ModuleCommand<WarpsModule> {

    public static final String NAME = "list";

    public ListSubCommand(@NotNull WarpsModule module) {
        super(module, new String[]{NAME}, WarpsPerms.COMMAND_WARPS_LIST);
        this.setDescription(plugin.getMessage(WarpsLang.COMMAND_WARPS_LIST_DESC));
        this.setUsage(plugin.getMessage(WarpsLang.COMMAND_WARPS_LIST_USAGE));
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1 && player.hasPermission(WarpsPerms.COMMAND_WARPS_LIST_OTHERS)) {
            return CollectionsUtil.playerNames(player);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() >= 2 && !sender.hasPermission(WarpsPerms.COMMAND_WARPS_LIST_OTHERS)) {
            this.errorPermission(sender);
            return;
        }

        String name = result.getArg(1, sender.getName());
        Player target = plugin.getServer().getPlayer(name);
        if (target == null) {
            this.errorPlayer(sender);
            return;
        }

        this.module.getWarpsMenu().open(target, 1);

        if (sender != target) {
            this.plugin.getMessage(WarpsLang.COMMAND_WARPS_LIST_OTHERS)
                .replace(Placeholders.forPlayer(target))
                .send(sender);
        }
    }
}
