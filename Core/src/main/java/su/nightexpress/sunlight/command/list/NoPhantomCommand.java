package su.nightexpress.sunlight.command.list;

import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.api.server.AbstractTask;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.ToggleCommand;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.data.impl.settings.UserSetting;

public class NoPhantomCommand extends ToggleCommand implements ICleanable {

    public static final String NAME = "nophantom";
    public static final UserSetting<Boolean> ANTI_PHANTOM = UserSetting.asBoolean("anti_phantom", false, true);

    private PhantomTask phantomTask;

    public NoPhantomCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_NOPHANTOM, Perms.COMMAND_NOPHANTOM_OTHERS);
        this.setAllowDataLoad();
        this.setDescription(plugin.getMessage(Lang.COMMAND_NO_PHANTOM_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_NO_PHANTOM_USAGE));

        this.phantomTask = new PhantomTask(plugin);
        this.phantomTask.start();
    }

    @Override
    public void clear() {
        if (this.phantomTask != null) {
            this.phantomTask.stop();
            this.phantomTask = null;
        }
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        SunUser user = plugin.getUserManager().getUserData(target);
        Mode mode = this.getMode(sender, result);
        boolean state = mode.apply(user.getSettings().get(ANTI_PHANTOM));

        user.getSettings().set(ANTI_PHANTOM, state);
        user.saveData(this.plugin);

        if (sender != target) {
            plugin.getMessage(Lang.COMMAND_NO_PHANTOM_TOGGLE_TARGET)
                .replace(Placeholders.GENERIC_STATE, Lang.getEnable(state))
                .replace(Placeholders.Player.replacer(target))
                .send(sender);
        }
        if (!result.hasFlag(CommandFlags.SILENT)) {
            plugin.getMessage(Lang.COMMAND_NO_PHANTOM_TOGGLE_NOTIFY)
                .replace(Placeholders.GENERIC_STATE, Lang.getEnable(state))
                .send(target);
        }
    }

    private static class PhantomTask extends AbstractTask<SunLight> {

        public PhantomTask(@NotNull SunLight plugin) {
            super(plugin, 600, false);
        }

        @Override
        public void action() {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                SunUser user = plugin.getUserManager().getUserData(player);
                if (!user.getSettings().get(ANTI_PHANTOM)) continue;

                player.setStatistic(Statistic.TIME_SINCE_REST, 0);
            }
        }
    }
}
