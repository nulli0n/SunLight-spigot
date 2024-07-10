package su.nightexpress.sunlight.data;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.database.AbstractUserManager;
import su.nightexpress.nightcore.util.CommandUtil;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.sunlight.config.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandConfig;
import su.nightexpress.sunlight.command.CommandCooldown;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.core.cooldown.CooldownInfo;
import su.nightexpress.sunlight.core.listener.UserListener;
import su.nightexpress.sunlight.core.menu.IgnoreListMenu;
import su.nightexpress.sunlight.core.menu.IgnoreSettingsMenu;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

public class UserManager extends AbstractUserManager<SunLightPlugin, SunUser> {

    private IgnoreListMenu ignoreListMenu;
    private IgnoreSettingsMenu ignoreSettingsMenu;

    public UserManager(@NotNull SunLightPlugin plugin) {
        super(plugin);
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
    public SunUser createUserData(@NotNull UUID uuid, @NotNull String name) {
        return SunUser.create(this.plugin, uuid, name);
    }

    public boolean checkCommandCooldown(@NotNull Player player, @NotNull String commandLine) {
        if (player.hasPermission(Perms.BYPASS_COMMAND_COOLDOWN)) return true;

//        String commandName = CommandUtil.getCommandName(commandLine);
//        String[] arguments = Stream.of(commandLine.split(" ")).skip(1).toArray(String[]::new);
//        SunUser user = plugin.getUserManager().getUserData(player);
//
//        Map<String, CommandCooldown> cooldownMap = CommandConfig.COOLDOWNS.get();
//        for (CommandCooldown cooldown : cooldownMap.values()) {
//            if (!cooldown.isApplicable(commandName, arguments)) continue;
//
//            CooldownInfo cooldownInfo = user.getCooldown(cooldown).orElse(null);
//            if (cooldownInfo != null) {
//                long expireDate = cooldownInfo.getExpireDate();
//                String time = TimeUtil.formatDuration(expireDate);
//
//                (expireDate < 0 ? Lang.GENERIC_COMMAND_COOLDOWN_ONE_TIME : Lang.GENERIC_COMMAND_COOLDOWN_DEFAULT).getMessage()
//                    .replace(Placeholders.GENERIC_TIME, time).replace(Placeholders.GENERIC_COMMAND, commandLine)
//                    .send(player);
//                return false;
//            }
//
//            // TODO Check rank map values
//            int seconds = cooldown.getCooldown().getSmallest(player);
//            if (seconds == 0) continue;
//
//            user.addCooldown(CooldownInfo.of(cooldown, seconds));
//            this.plugin.getUserManager().saveAsync(user);
//        }
        return true;
    }

    @NotNull
    @Deprecated
    public IgnoreListMenu getIgnoreListMenu() {
        return ignoreListMenu;
    }

    @NotNull
    @Deprecated
    public IgnoreSettingsMenu getIgnoreSettingsMenu() {
        return ignoreSettingsMenu;
    }
}
