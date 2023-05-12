package su.nightexpress.sunlight.command.api;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.data.impl.SunUser;

import java.util.List;

public abstract class TargetCommand extends GeneralCommand<SunLight> {

    protected final Permission permissionOthers;
    protected final int targetIndex;

    protected boolean allowDataLoad;

    public TargetCommand(@NotNull SunLight plugin, @NotNull String[] aliases,
                         @NotNull Permission permission, @NotNull Permission permissionOthers,
                         int targetIndex) {
        super(plugin, aliases, permission);
        this.permissionOthers = permissionOthers;
        this.targetIndex = targetIndex;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        int index = this.getParent() == null ? (targetIndex + 1) : targetIndex;

        if (arg == index && player.hasPermission(this.permissionOthers)) {
            return CollectionsUtil.playerNames(player);
        }
        return super.getTab(player, arg, args);
    }

    protected void setAllowDataLoad() {
        this.allowDataLoad = true;
    }

    protected boolean isAllowDataLoad() {
        return allowDataLoad;
    }

    @Nullable
    public Player getCommandTarget(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() <= this.targetIndex) {
            if (!(sender instanceof Player player)) {
                this.printUsage(sender);
                return null;
            }
            return player;
        }
        else {
            if (!sender.hasPermission(this.permissionOthers)) {
                this.errorPermission(sender);
                return null;
            }

            String name = result.getArg(this.targetIndex);
            Player target = this.plugin.getServer().getPlayer(name);

            if (target == null && this.isAllowDataLoad()) {
                SunUser user = this.plugin.getUserManager().getUserData(name);
                if (user != null) {
                    target = plugin.getSunNMS().loadPlayerData(user.getId(), user.getName());
                }
            }

            if (target == null || (sender instanceof Player player && !player.canSee(target))) {
                this.errorPlayer(sender);
            }
            return target;
        }
    }
}
