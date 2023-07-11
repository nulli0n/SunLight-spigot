package su.nightexpress.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.ToggleCommand;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.data.impl.settings.UserSetting;
import su.nightexpress.sunlight.utils.Cleanable;

public class VanishCommand extends ToggleCommand implements Cleanable {

    public static final String NAME = "vanish";
    public static final UserSetting<Boolean> VANISH = UserSetting.asBoolean("vanish", false, false);

    private final Listener listener;

    public VanishCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_VANISH, Perms.COMMAND_VANISH_OTHERS);
        this.setDescription(plugin.getMessage(Lang.COMMAND_VANISH_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_VANISH_USAGE));

        (this.listener = new Listener(plugin)).registerListeners();
    }

    @Override
    public void clear() {
        this.listener.unregisterListeners();
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        SunUser user = plugin.getUserManager().getUserData(target);

        Mode mode = this.getMode(sender, result);
        boolean state = mode.apply(user.getSettings().get(VANISH));

        user.getSettings().set(VANISH, state);
        this.vanish(target, state);

        if (!result.hasFlag(CommandFlags.SILENT)) {
            plugin.getMessage(Lang.COMMAND_VANISH_NOTIFY)
                .replace(Placeholders.GENERIC_STATE, Lang.getEnable(state))
                .send(target);
        }
        if (sender != target) {
            plugin.getMessage(Lang.COMMAND_VANISH_TARGET)
                .replace(Placeholders.forPlayer(target))
                .replace(Placeholders.GENERIC_STATE, Lang.getEnable(state))
                .send(sender);
        }
    }

    private void vanish(@NotNull Player target, boolean isVanished) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (isVanished) {
                if (!player.hasPermission(Perms.COMMAND_VANISH_BYPASS_SEE)) {
                    player.hidePlayer(plugin, target);
                }
            }
            else {
                player.showPlayer(plugin, target);
            }
        }
    }

    private static class Listener extends AbstractListener<SunLight> {

        public Listener(@NotNull SunLight plugin) {
            super(plugin);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onJoin(PlayerJoinEvent e) {
            Player player = e.getPlayer();
            for (Player target : plugin.getServer().getOnlinePlayers()) {
                if (target == player) continue;

                SunUser user = plugin.getUserManager().getUserData(target);
                if (user.getSettings().get(VANISH)) {
                    player.hidePlayer(plugin, target);
                }
            }
        }
    }
}
