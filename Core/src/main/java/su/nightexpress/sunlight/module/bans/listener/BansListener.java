package su.nightexpress.sunlight.module.bans.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.language.entry.LangText;
import su.nightexpress.nightcore.manager.AbstractListener;
import su.nightexpress.nightcore.util.CommandUtil;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.config.BansConfig;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.punishment.PunishData;
import su.nightexpress.sunlight.module.bans.punishment.PunishedPlayer;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;
import su.nightexpress.sunlight.utils.SunUtils;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BansListener extends AbstractListener<SunLightPlugin> {

    private final BansModule module;

    public BansListener(@NotNull SunLightPlugin plugin, @NotNull BansModule module) {
        super(plugin);
        this.module = module;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBanLogin(AsyncPlayerPreLoginEvent event) {
        String address = SunUtils.getRawAddress(event.getAddress());
        UUID playerId = event.getUniqueId();

        PunishData punishData = module.getActivePlayerPunishment(playerId, PunishmentType.BAN);
        if (punishData == null) punishData = module.getActiveIPPunishment(address);
        if (punishData == null) return;

        // Update player name in case it was changed.
        if (punishData instanceof PunishedPlayer punishedPlayer && punishedPlayer.updateName(event.getName())) {
            this.plugin.runTaskAsync(task -> this.module.getDataHandler().saveData(punishedPlayer, PunishmentType.BAN));
        }

        List<String> text = punishData.isPermanent() ? BansConfig.GENERAL_DISCONNECT_INFO_BAN_PERMANENT.get() : BansConfig.GENERAL_DISCONNECT_INFO_BAN_TEMP.get();
        String message = NightMessage.asLegacy(String.join("\n", text));

        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, punishData.replacePlaceholders().apply(message));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onMuteChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PunishedPlayer punishedPlayer = this.module.getActivePlayerPunishment(player.getUniqueId(), PunishmentType.MUTE);
        if (punishedPlayer == null) return;

        event.setCancelled(true);
        event.getRecipients().clear();

        LangText lang = BansLang.getPunishNotify(punishedPlayer, PunishmentType.MUTE);
        if (lang != null) {
            lang.getMessage().replace(punishedPlayer.replacePlaceholders()).send(player);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onMuteCommand(PlayerCommandPreprocessEvent event) {
        Set<String> blockedCommands = BansConfig.PUNISHMENTS_MUTE_BLOCKED_COMMANDS.get();
        if (blockedCommands.isEmpty()) return;

        Player player = event.getPlayer();
        PunishedPlayer punishedPlayer = this.module.getActivePlayerPunishment(player.getUniqueId(), PunishmentType.MUTE);
        if (punishedPlayer == null) return;

        String command = CommandUtil.getCommandName(event.getMessage());
        Set<String> aliases = CommandUtil.getAliases(command, true);

        if (aliases.stream().anyMatch(blockedCommands::contains)) {
            event.setCancelled(true);

            LangText lang = BansLang.getPunishNotify(punishedPlayer, PunishmentType.MUTE);
            if (lang != null) {
                lang.getMessage().replace(punishedPlayer.replacePlaceholders()).send(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!BansConfig.isAltCheckerEnabled()) return;

        Player player = event.getPlayer();
        SunUser user = this.plugin.getUserManager().getOrFetch(event.getPlayer());
        this.module.linkAltAccount(user);
    }
}
