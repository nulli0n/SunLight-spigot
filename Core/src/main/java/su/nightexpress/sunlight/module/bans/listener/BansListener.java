package su.nightexpress.sunlight.module.bans.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.command.CommandRegister;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.data.impl.settings.DefaultSettings;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.config.BansConfig;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.punishment.Punishment;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;
import su.nightexpress.sunlight.utils.SunUtils;

import java.util.Set;

public class BansListener extends AbstractListener<SunLight> {

    private final BansModule bansModule;

    public BansListener(@NotNull BansModule bansModule) {
        super(bansModule.plugin());
        this.bansModule = bansModule;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBanLogin(AsyncPlayerPreLoginEvent e) {
        String name = e.getName();

        Punishment ban = bansModule.getActivePunishment(name, PunishmentType.BAN);
        if (ban == null) ban = bansModule.getActivePunishment(SunUtils.getIP(e.getAddress()), PunishmentType.BAN);
        if (ban == null) return;

        String message;
        if (ban.isPermanent()) {
            message = String.join("\n", BansConfig.GENERAL_DISCONNECT_INFO_BAN_PERMANENT.get());
        }
        else {
            message = String.join("\n", BansConfig.GENERAL_DISCONNECT_INFO_BAN_TEMP.get());
        }
        e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ban.replacePlaceholders().apply(message));
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onMuteChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String name = player.getName();

        Punishment punishment = this.bansModule.getActivePunishment(player, PunishmentType.MUTE);
        if (punishment == null) return;

        e.setCancelled(true);
        e.getRecipients().clear();

        this.plugin.getMessage(BansLang.getPunishNotify(punishment)).replace(punishment.replacePlaceholders()).send(player);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onMuteCommand(PlayerCommandPreprocessEvent e) {
        if (BansConfig.PUNISHMENTS_MUTE_BLOCKED_COMMANDS.get().isEmpty()) return;

        Player player = e.getPlayer();
        String name = player.getName();

        Punishment punishment = this.bansModule.getActivePunishment(player, PunishmentType.MUTE);
        if (punishment == null) return;

        String command = StringUtil.extractCommandName(e.getMessage());
        Set<String> aliases = CommandRegister.getAliases(command, true);

        if (aliases.stream().anyMatch(alias -> BansConfig.PUNISHMENTS_MUTE_BLOCKED_COMMANDS.get().contains(alias))) {
            e.setCancelled(true);
            this.plugin.getMessage(BansLang.getPunishNotify(punishment)).replace(punishment.replacePlaceholders()).send(player);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        SunUser user = this.plugin.getUserManager().getUserData(event.getPlayer());
        user.getSettings().set(DefaultSettings.LAST_RANK, PlayerUtil.getPermissionGroup(player));
    }
}
