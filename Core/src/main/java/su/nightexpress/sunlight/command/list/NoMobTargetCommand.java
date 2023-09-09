package su.nightexpress.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.utils.EntityUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.ToggleCommand;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.data.impl.settings.DefaultSettings;
import su.nightexpress.sunlight.data.impl.settings.UserSetting;
import su.nightexpress.sunlight.utils.Cleanable;

public class NoMobTargetCommand extends ToggleCommand implements Cleanable {

    public static final String NAME = "nomobtarget";

    private final Listener listener;

    public NoMobTargetCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_NO_MOB_TARGET, Perms.COMMAND_NO_MOB_TARGET_OTHERS);
        this.setDescription(plugin.getMessage(Lang.COMMAND_NO_MOB_TARGET_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_NO_MOB_TARGET_USAGE));
        this.setAllowDataLoad();

        this.listener = new Listener(plugin);
        this.listener.registerListeners();
    }

    @Override
    public void clear() {
        this.listener.unregisterListeners();
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        Mode mode = this.getMode(sender, result);
        UserSetting<Boolean> setting = DefaultSettings.NO_MOB_TARGET;
        SunUser user = this.plugin.getUserManager().getUserData(target);
        boolean state = mode.apply(user.getSettings().get(setting));

        user.getSettings().set(setting, state);
        user.saveData(this.plugin);

        if (sender != target) {
            plugin.getMessage(Lang.COMMAND_NO_MOB_TARGET_TOGGLE_TARGET)
                .replace(Placeholders.GENERIC_STATE, Lang.getEnable(state))
                .replace(Placeholders.forPlayer(target))
                .send(sender);
        }
        if (!result.hasFlag(CommandFlags.SILENT)) {
            plugin.getMessage(Lang.COMMAND_NO_MOB_TARGET_TOGGLE_NOTIFY)
                .replace(Placeholders.GENERIC_STATE, Lang.getEnable(state))
                .send(target);
        }
    }

    private static class Listener extends AbstractListener<SunLight> {

        public Listener(@NotNull SunLight plugin) {
            super(plugin);
        }

        @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
        public void onMobTarget(EntityTargetLivingEntityEvent event) {
            if (!(event.getTarget() instanceof Player player)) return;
            if (!EntityUtil.isNPC(player)) return;

            SunUser user = this.plugin.getUserManager().getUserData(player);
            if (user.getSettings().get(DefaultSettings.NO_MOB_TARGET)) {
                event.setCancelled(true);
            }
        }
    }
}
