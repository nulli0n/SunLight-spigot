package su.nightexpress.sunlight.core;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.CommandUtil;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.cooldown.CommandCooldown;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.config.Perms;
import su.nightexpress.sunlight.core.cooldown.CooldownInfo;
import su.nightexpress.sunlight.core.listener.CoreListener;
import su.nightexpress.sunlight.data.user.SunUser;

public class CoreManager extends AbstractManager<SunLightPlugin> {

    public CoreManager(@NotNull SunLightPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void onLoad() {
        this.addListener(new CoreListener(this.plugin));
    }

    @Override
    protected void onShutdown() {

    }

    public boolean checkCommandCooldown(@NotNull Player player, @NotNull String commandMessage) {
        if (player.hasPermission(Perms.BYPASS_COMMAND_COOLDOWN)) return true;

        String commandName = CommandUtil.getCommandName(commandMessage);

        CommandCooldown cooldown = CommandRegistry.getCooldown(commandName);
        if (cooldown == null || !cooldown.isApplicable(commandName, commandMessage)) return true;

        SunUser user = plugin.getUserManager().getOrFetch(player);

        CooldownInfo cooldownInfo = user.getCooldown(cooldown).orElse(null);
        if (cooldownInfo != null) {
            long expireDate = cooldownInfo.getExpireDate();
            String time = TimeUtil.formatDuration(expireDate);

            (expireDate < 0 ? Lang.CORE_COMMAND_COOLDOWN_ONE_TIME : Lang.CORE_COMMAND_COOLDOWN_DEFAULT).getMessage()
                .replace(Placeholders.GENERIC_TIME, time).replace(Placeholders.GENERIC_COMMAND, commandMessage)
                .send(player);
            return false;
        }

        int seconds = cooldown.getCooldown(player);
        if (seconds == 0) return true;

        user.addCooldown(CooldownInfo.of(cooldown, seconds));
        this.plugin.getUserManager().save(user);
        return true;
    }
}
