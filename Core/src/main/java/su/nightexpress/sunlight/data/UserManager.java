package su.nightexpress.sunlight.data;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.data.AbstractUserManager;
import su.nexmedia.engine.utils.StringUtil;
import su.nexmedia.engine.utils.TimeUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandConfig;
import su.nightexpress.sunlight.command.CommandCooldown;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.data.impl.cooldown.CooldownInfo;
import su.nightexpress.sunlight.data.listener.UserListener;
import su.nightexpress.sunlight.data.menu.IgnoreListMenu;
import su.nightexpress.sunlight.data.menu.IgnoreSettingsMenu;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

public class UserManager extends AbstractUserManager<SunLight, SunUser> {

    private IgnoreListMenu ignoreListMenu;
    private IgnoreSettingsMenu ignoreSettingsMenu;

    public UserManager(@NotNull SunLight plugin) {
        super(plugin, plugin);
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        this.ignoreListMenu = new IgnoreListMenu(this.plugin);
        this.ignoreSettingsMenu = new IgnoreSettingsMenu(this.plugin);

        this.addListener(new UserListener(this.plugin));
    }

    @Override
    protected void onShutdown() {
        super.onShutdown();
        this.ignoreListMenu.clear();
        this.ignoreSettingsMenu.clear();
    }

    @Override
    @NotNull
    protected SunUser createData(@NotNull UUID uuid, @NotNull String name) {
        return new SunUser(this.plugin, uuid, name);
    }

    public boolean checkCommandCooldown(@NotNull Player player, @NotNull String commandLine) {
        if (player.hasPermission(Perms.BYPASS_COMMAND_COOLDOWN)) return true;

        String commandName = StringUtil.extractCommandName(commandLine);
        String[] arguments = Stream.of(commandLine.split(" ")).skip(1).toArray(String[]::new);
        SunUser user = plugin.getUserManager().getUserData(player);

        Map<String, CommandCooldown> cooldownMap = CommandConfig.COOLDOWNS.get();
        for (CommandCooldown cooldown : cooldownMap.values()) {
            if (!cooldown.isApplicable(commandName, arguments)) continue;

            CooldownInfo cooldownInfo = user.getCooldown(cooldown).orElse(null);
            if (cooldownInfo != null) {
                long expireDate = cooldownInfo.getExpireDate();
                String time = TimeUtil.formatTimeLeft(expireDate);

                plugin.getMessage(expireDate < 0 ? Lang.GENERIC_COMMAND_COOLDOWN_ONE_TIME : Lang.GENERIC_COMMAND_COOLDOWN_DEFAULT)
                    .replace(Placeholders.GENERIC_TIME, time).replace(Placeholders.GENERIC_COMMAND, commandLine)
                    .send(player);
                return false;
            }

            int seconds = cooldown.getCooldown().getLowestValue(player, 0);
            if (seconds == 0) continue;

            user.addCooldown(CooldownInfo.of(cooldown, seconds));
            this.plugin.getUserManager().saveUser(user);
        }
        return true;
    }

    @NotNull
    public IgnoreListMenu getIgnoreListMenu() {
        return ignoreListMenu;
    }

    @NotNull
    public IgnoreSettingsMenu getIgnoreSettingsMenu() {
        return ignoreSettingsMenu;
    }
}
