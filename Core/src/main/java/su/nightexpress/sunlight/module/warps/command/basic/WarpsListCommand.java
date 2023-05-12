package su.nightexpress.sunlight.module.warps.command.basic;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.module.warps.config.WarpsLang;
import su.nightexpress.sunlight.module.warps.util.Placeholders;
import su.nightexpress.sunlight.module.warps.util.WarpsPerms;

import java.util.List;
import java.util.Map;

public class WarpsListCommand extends ModuleCommand<WarpsModule> {

    public static final String NAME = "list";

    public WarpsListCommand(@NotNull WarpsModule module) {
        super(module, new String[]{NAME}, WarpsPerms.COMMAND_WARPS_LIST);
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.plugin.getMessage(WarpsLang.COMMAND_WARPS_LIST_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.plugin.getMessage(WarpsLang.COMMAND_WARPS_LIST_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
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
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
        if (args.length >= 2 && !sender.hasPermission(WarpsPerms.COMMAND_WARPS_LIST_OTHERS)) {
            this.errorPermission(sender);
            return;
        }

        String pName = args.length >= 2 ? args[1] : sender.getName();
        Player player = plugin.getServer().getPlayer(pName);
        if (player == null) {
            this.errorPlayer(sender);
            return;
        }

        this.module.getWarpsMenu().open(player, 1);

        if (!player.equals(sender)) {
            this.plugin.getMessage(WarpsLang.COMMAND_WARPS_LIST_OTHERS)
                .replace(Placeholders.Player.replacer(player))
                .send(sender);
        }
    }
}
