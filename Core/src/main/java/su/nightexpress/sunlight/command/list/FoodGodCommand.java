package su.nightexpress.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.FoodLevelChangeEvent;
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
import su.nightexpress.sunlight.data.impl.settings.DefaultSettings;
import su.nightexpress.sunlight.data.impl.settings.UserSetting;
import su.nightexpress.sunlight.utils.Cleanable;

public class FoodGodCommand extends ToggleCommand implements Cleanable {

    public static final String NAME = "foodgod";

    private final Listener listener;

    public FoodGodCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_FOOD_GOD, Perms.COMMAND_FOOD_GOD_OTHERS);
        this.setAllowDataLoad();
        this.setDescription(plugin.getMessage(Lang.COMMAND_FOOD_GOD_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_FOOD_GOD_USAGE));

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

        SunUser user = plugin.getUserManager().getUserData(target);
        Mode mode = this.getMode(sender, result);
        UserSetting<Boolean> setting = DefaultSettings.FOOD_GOD;
        boolean state = mode.apply(user.getSettings().get(setting));
        user.getSettings().set(setting, state);
        this.plugin.getUserManager().saveUser(user);

        if (sender != target) {
            plugin.getMessage(Lang.COMMAND_FOOD_GOD_TARGET)
                .replace(Placeholders.forPlayer(target))
                .replace(Placeholders.GENERIC_STATE, Lang.getEnable(state))
                .send(sender);
        }
        if (!result.hasFlag(CommandFlags.SILENT)) {
            plugin.getMessage(Lang.COMMAND_FOOD_GOD_NOTIFY)
                .replace(Placeholders.GENERIC_STATE, Lang.getEnable(state))
                .send(target);
        }
    }

    private static class Listener extends AbstractListener<SunLight> {

        public Listener(@NotNull SunLight plugin) {
            super(plugin);
        }

        @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
        public void onFoodChange(FoodLevelChangeEvent e) {
            if (!(e.getEntity() instanceof Player player)) return;

            SunUser user = plugin.getUserManager().getUserData(player);
            e.setCancelled(user.getSettings().get(DefaultSettings.FOOD_GOD));
        }
    }
}
