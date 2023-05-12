package su.nightexpress.sunlight.data.listener;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.command.CommandRegister;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.utils.StringUtil;
import su.nexmedia.engine.utils.TimeUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandConfig;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.IgnoredUser;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.data.impl.cooldown.CooldownInfo;
import su.nightexpress.sunlight.utils.SunUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserListener extends AbstractListener<SunLight> {

    public UserListener(@NotNull SunLight plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onUserJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        SunUser user = plugin.getUserManager().getUserData(player);

        user.updatePlayerName();
        user.setIp(SunUtils.getIP(player));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onUserIgnoreChat(AsyncPlayerChatEvent e) {
        Player pSender = e.getPlayer();
        SunUser userSender = plugin.getUserManager().getUserData(pSender);

        e.getRecipients().removeIf(pReceiver -> {
            SunUser userReceiver = plugin.getUserManager().getUserData(pReceiver);

            IgnoredUser ignoredSender = userReceiver.getIgnoredUser(pSender);
            if (ignoredSender == null) return false;

            return ignoredSender.isHideChatMessages();
        });
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onUserCommandCooldown(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        if (player.hasPermission(Perms.CMD_BYPASS_COOLDOWN)) return;

        String name = StringUtil.extractCommandName(e.getMessage());
        Command command = CommandRegister.getCommand(name).orElse(null);
        if (command == null) return;

        SunUser user = plugin.getUserManager().getUserData(player);
        CooldownInfo cooldownInfo = user.getCooldown(command).orElse(null);
        if (cooldownInfo != null) {
            e.setCancelled(true);
            long date = cooldownInfo.getExpireDate();
            String time = TimeUtil.formatTimeLeft(date, System.currentTimeMillis());

            (date < 0 ? plugin.getMessage(Lang.Generic_Command_Cooldown_Onetime) : plugin.getMessage(Lang.Generic_Command_Cooldown_Default))
                .replace("%time%", time).replace("%cmd%", "/" + name)
                .send(player);
        }
        else {
            Map<String, Map<String, Integer>> cooldownMap = CommandConfig.COOLDOWNS.get();
            Set<String> ranks = Hooks.getPermissionGroups(player);
            Set<String> aliases = new HashSet<>(command.getAliases());
            aliases.add(command.getLabel());

            //int cooldown = 0;
            Integer cooldown = null;

            for (String rank : ranks) {
                Map<String, Integer> rankMap = cooldownMap.getOrDefault(rank, Collections.emptyMap());
                if (rankMap.isEmpty()) continue;

                int seconds = 0;
                for (String alias : aliases) {
                    if (rankMap.containsKey(alias.toLowerCase())) {
                        seconds = rankMap.get(alias.toLowerCase());
                        break;
                    }
                }

                if (cooldown == null || (seconds <= cooldown && seconds >= 0)) {
                    cooldown = seconds;
                }
            }
            if (cooldown == null || cooldown == 0) return;

            user.addCooldown(CooldownInfo.of(command, cooldown));
            user.saveData(this.plugin);
        }
    }
}
