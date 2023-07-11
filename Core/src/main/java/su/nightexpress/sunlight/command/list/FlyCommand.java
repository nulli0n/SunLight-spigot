package su.nightexpress.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.ToggleCommand;
import su.nightexpress.sunlight.config.Lang;

public class FlyCommand extends ToggleCommand {

    public static final String NAME = "fly";

    public FlyCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_FLY, Perms.COMMAND_FLY_OTHERS);
        this.setDescription(plugin.getMessage(Lang.COMMAND_FLY_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_FLY_USAGE));
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        Mode mode = this.getMode(sender, result);
        target.setAllowFlight(mode.apply(target.getAllowFlight()));

        if (!result.hasFlag(CommandFlags.SILENT)) {
            plugin.getMessage(Lang.COMMAND_FLY_NOTIFY)
                .replace(Placeholders.GENERIC_STATE, Lang.getEnable(target.getAllowFlight())).send(target);
        }
        if (sender != target) {
            plugin.getMessage(Lang.COMMAND_FLY_TARGET)
                .replace(Placeholders.forPlayer(target))
                .replace(Placeholders.GENERIC_STATE, Lang.getEnable(target.getAllowFlight())).send(sender);
        }
    }

    /*class Listener extends AbstractListener<SunLight> {

        Listener(@NotNull SunLight plugin) {
            super(plugin);
        }

        @EventHandler(priority = EventPriority.NORMAL)
        public void onCommandFlyFlight(PlayerToggleFlightEvent e) {
            FlyCommand.this.checkWorld(e.getPlayer());
        }

        @EventHandler(priority = EventPriority.NORMAL)
        public void onCommandFlyGlide(EntityToggleGlideEvent e) {
            if (e.getEntity() instanceof Player player) {
                FlyCommand.this.checkWorld(player);
            }
        }

        @EventHandler(priority = EventPriority.NORMAL)
        public void onCommandFlyWorldChange(PlayerChangedWorldEvent e) {
            Player player = e.getPlayer();
            if (player.isFlying() || player.getAllowFlight() || player.isGliding()) {
                FlyCommand.this.checkWorld(player);
            }
        }
    }*/
}
