package su.nightexpress.sunlight.command.list;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.api.TargetCommand;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.SunUser;

public class SkullCommand extends TargetCommand {

    public static final String NAME = "skull";

    public SkullCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_SKULL, Perms.COMMAND_SKULL_OTHERS, 0);
        this.setAllowDataLoad();
        this.setDescription(plugin.getMessage(Lang.COMMAND_SKULL_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_SKULL_USAGE));
        this.setPlayerOnly(true);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        SunUser user = plugin.getUserManager().getUserData(target);

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        ItemUtil.mapMeta(skull, meta -> {
            if (meta instanceof SkullMeta skullMeta) {
                skullMeta.setOwningPlayer(user.getOfflinePlayer());
            }
        });
        PlayerUtil.addItem(target, skull);
        plugin.getMessage(Lang.COMMAND_SKULL_DONE).replace(Placeholders.Player.NAME, user.getName()).send(sender);
    }
}
