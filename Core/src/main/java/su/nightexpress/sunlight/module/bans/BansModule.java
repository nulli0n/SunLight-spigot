package su.nightexpress.sunlight.module.bans;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.data.config.DataConfig;
import su.nexmedia.engine.api.lang.LangMessage;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.utils.TimeUtil;
import su.nexmedia.engine.utils.regex.RegexUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.bans.command.KickCommand;
import su.nightexpress.sunlight.module.bans.command.ban.BanCommand;
import su.nightexpress.sunlight.module.bans.command.ban.BanipCommand;
import su.nightexpress.sunlight.module.bans.command.ban.UnbanCommand;
import su.nightexpress.sunlight.module.bans.command.history.BanHistoryCommand;
import su.nightexpress.sunlight.module.bans.command.history.MuteHistoryCommand;
import su.nightexpress.sunlight.module.bans.command.history.WarnHistoryCommand;
import su.nightexpress.sunlight.module.bans.command.list.BanListCommand;
import su.nightexpress.sunlight.module.bans.command.list.MuteListCommand;
import su.nightexpress.sunlight.module.bans.command.list.WarnListCommand;
import su.nightexpress.sunlight.module.bans.command.mute.MuteCommand;
import su.nightexpress.sunlight.module.bans.command.mute.UnmuteCommand;
import su.nightexpress.sunlight.module.bans.command.warn.UnwarnCommand;
import su.nightexpress.sunlight.module.bans.command.warn.WarnCommand;
import su.nightexpress.sunlight.module.bans.config.BansConfig;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.data.BansDataHandler;
import su.nightexpress.sunlight.module.bans.listener.BansListener;
import su.nightexpress.sunlight.module.bans.menu.PunishmentListMenu;
import su.nightexpress.sunlight.module.bans.punishment.Punishment;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentReason;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;
import su.nightexpress.sunlight.module.bans.punishment.RankDuration;
import su.nightexpress.sunlight.module.bans.util.BansPerms;
import su.nightexpress.sunlight.module.bans.util.Placeholders;
import su.nightexpress.sunlight.utils.SunUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class BansModule extends Module {

    private final Map<String, Map<PunishmentType, Set<Punishment>>> punishmentMap;

    private PunishmentListMenu listMenu;

    private BansDataHandler dataHandler;

    public BansModule(@NotNull SunLight plugin, @NotNull String id) {
        super(plugin, id);
        this.punishmentMap = new ConcurrentHashMap<>();
    }

    @Override
    public void onLoad() {
        this.getConfig().initializeOptions(BansConfig.class);
        this.plugin.getLangManager().loadMissing(BansLang.class);
        this.plugin.getLangManager().setupEnum(PunishmentType.class);
        this.plugin.getLang().saveChanges();
        this.plugin.registerPermissions(BansPerms.class);

        this.dataHandler = new BansDataHandler(this, new DataConfig(this.getConfig()));
        this.dataHandler.setup();
        this.getDataHandler().onSynchronize();

        this.listMenu = new PunishmentListMenu(this);

        this.plugin.getCommandRegulator().register(KickCommand.NAME, (cfg1, aliases) -> new KickCommand(this, aliases));
        this.plugin.getCommandRegulator().register(BanCommand.NAME, (cfg1, aliases) -> new BanCommand(this, aliases));
        this.plugin.getCommandRegulator().register(BanipCommand.NAME, (cfg1, aliases) -> new BanipCommand(this, aliases));
        this.plugin.getCommandRegulator().register(BanHistoryCommand.NAME, (cfg1, aliases) -> new BanHistoryCommand(this, aliases));
        this.plugin.getCommandRegulator().register(BanListCommand.NAME, (cfg1, aliases) -> new BanListCommand(this, aliases));
        this.plugin.getCommandRegulator().register(UnbanCommand.NAME, (cfg1, aliases) -> new UnbanCommand(this, aliases));
        this.plugin.getCommandRegulator().register(MuteCommand.NAME, (cfg1, aliases) -> new MuteCommand(this, aliases));
        this.plugin.getCommandRegulator().register(MuteHistoryCommand.NAME, (cfg1, aliases) -> new MuteHistoryCommand(this, aliases));
        this.plugin.getCommandRegulator().register(MuteListCommand.NAME, (cfg1, aliases) -> new MuteListCommand(this, aliases));
        this.plugin.getCommandRegulator().register(UnmuteCommand.NAME, (cfg1, aliases) -> new UnmuteCommand(this, aliases));
        this.plugin.getCommandRegulator().register(WarnCommand.NAME, (cfg1, aliases) -> new WarnCommand(this, aliases));
        this.plugin.getCommandRegulator().register(WarnHistoryCommand.NAME, (cfg1, aliases) -> new WarnHistoryCommand(this, aliases));
        this.plugin.getCommandRegulator().register(WarnListCommand.NAME, (cfg1, aliases) -> new WarnListCommand(this, aliases));
        this.plugin.getCommandRegulator().register(UnwarnCommand.NAME, (cfg1, aliases) -> new UnwarnCommand(this, aliases));

        this.addListener(new BansListener(this));
    }

    @Override
    public void onShutdown() {
        this.dataHandler.shutdown();
        this.punishmentMap.clear();
        this.listMenu.clear();
    }

    @NotNull
    public BansDataHandler getDataHandler() {
        return dataHandler;
    }

    @Nullable
    public PunishmentReason getReason(@NotNull String id) {
        return BansConfig.GENERAL_REASONS.get().get(id.toLowerCase());
    }

    @NotNull
    public PunishmentListMenu getListMenu() {
        return this.listMenu;
    }

    @NotNull
    public Map<String, Map<PunishmentType, Set<Punishment>>> getPunishmentMap() {
        return punishmentMap;
    }

    @NotNull
    public List<Punishment> getPunishments(@NotNull PunishmentType type) {
        List<Punishment> list = new ArrayList<>();
        this.punishmentMap.forEach((userName, mapType) -> {
            list.addAll(mapType.getOrDefault(type, Collections.emptySet()));
        });
        return list;
    }

    @NotNull
    public Map<PunishmentType, Set<Punishment>> getPunishments(@NotNull String user) {
        return this.punishmentMap.computeIfAbsent(user.toLowerCase(), k -> new HashMap<>());
    }

    @NotNull
    public Set<Punishment> getPunishments(@NotNull String user, @NotNull PunishmentType type) {
        return this.getPunishments(user.toLowerCase()).computeIfAbsent(type, k -> new HashSet<>());
    }

    @NotNull
    public List<Punishment> getActivePunishments(@NotNull String user, @NotNull PunishmentType type) {
        return this.getPunishments(user.toLowerCase(), type).stream()
            .filter(Predicate.not(Punishment::isExpired))
            .sorted((p1, p2) -> (int) (p2.getCreatedDate() - p1.getCreatedDate()))
            .toList();
    }

    @Nullable
    public Punishment getActivePunishment(@NotNull Player player, @NotNull PunishmentType type) {
        Punishment punishment = this.getActivePunishment(player.getName(), type);
        return punishment == null ? this.getActivePunishment(SunUtils.getIP(player), type) : punishment;
    }

    @Nullable
    public Punishment getActivePunishment(@NotNull String user, @NotNull PunishmentType type) {
        return this.getActivePunishments(user, type).stream().findFirst().orElse(null);
    }

    public void deletePunishment(@NotNull Punishment punishment) {
        this.getPunishments(punishment.getUser(), punishment.getType()).remove(punishment);
        this.plugin.runTaskAsync(task -> this.dataHandler.deletePunishment(punishment));
    }

    public boolean isMuted(@NotNull String user) {
        return this.getActivePunishment(user, PunishmentType.MUTE) != null;
    }

    public boolean isBanned(@NotNull String user) {
        return this.getActivePunishment(user, PunishmentType.BAN) != null;
    }

    public boolean canBePunished(@NotNull String user) {
        return !BansConfig.GENERAL_IMMUNIES.get().contains(user.toLowerCase());
    }

    private boolean canBePunished(@NotNull CommandSender admin, @NotNull String user) {
        if (!this.canBePunished(user)) {
            this.plugin.getMessage(BansLang.PUNISHMENT_ERROR_IMMUNE).replace(Placeholders.GENERIC_USER, user).send(admin);
            return false;
        }
        if (user.equalsIgnoreCase(admin.getName())) {
            this.plugin.getMessage(Lang.ERROR_COMMAND_SELF).send(admin);
            return false;
        }
        return true;
    }

    @NotNull
    private List<? extends Player> getPlayersToPunish(@NotNull String user) {
        return this.plugin.getServer().getOnlinePlayers().stream().filter(player -> {
            return player.getName().equalsIgnoreCase(user) || SunUtils.getIP(player).equalsIgnoreCase(user);
        }).toList();
    }

    public void punish(
        @NotNull String user, @NotNull CommandSender punisher,
        @NotNull String reason, long banTime,
        @NotNull PunishmentType type) {

        if (!this.canBePunished(punisher, user)) return;

        if (!punisher.hasPermission(BansPerms.BYPASS_DURATION_LIMIT) && punisher instanceof Player admin) {
            Map<String, Map<PunishmentType, RankDuration>> durationMap = BansConfig.PUNISHMENTS_RANK_MAX_TIMES.get();
            Set<RankDuration> durations = new HashSet<>();
            for (String rank : Hooks.getPermissionGroups(admin)) {
                RankDuration duration = durationMap.getOrDefault(rank, Collections.emptyMap()).get(type);
                if (duration != null) {
                    durations.add(duration);
                }
            }

            RankDuration duration = durations.stream().max(Comparator.comparingLong(RankDuration::getMillis)).orElse(null);
            if (duration != null && banTime > duration.getMillis()) {
                this.plugin.getMessage(BansLang.PUNISHMENT_ERROR_RANK_DURATION)
                    .replace(Placeholders.GENERIC_TIME, TimeUtil.formatTime(duration.getMillis()))
                    .send(punisher);
                return;
            }
        }

        // Fine user name if present.
        Player pTarget = plugin.getServer().getPlayer(user);
        if (pTarget != null) user = pTarget.getName();

        String userName = user;

        // Mutes and Bans can only have one active punishment instance, so we need to overwrite (expire) all others.
        // For warns we do the same, if max. warns amount is reached, and punish at the end of method.
        int punishmentsAmount = type == PunishmentType.WARN ? this.getActivePunishments(userName, type).size() + 1 : 1;
        int warnsMax = BansConfig.PUNISHMENTS_WARN_MAX_AMOUNT.get();
        if (type != PunishmentType.WARN || (warnsMax >= 1 && punishmentsAmount >= warnsMax)) {
            this.getActivePunishments(userName, type).forEach(punishment -> {
                punishment.expire();
                this.plugin.runTaskAsync(task -> this.dataHandler.savePunishment(punishment));
            });
        }

        // Create new punishment object and add it to the database.
        if (banTime > 0) banTime = System.currentTimeMillis() + banTime + 100L;
        Punishment punishment = new Punishment(type, userName, reason, punisher.getName(), banTime);
        this.getPunishments(userName, type).add(punishment);
        this.plugin.runTaskAsync(task -> {
            SunUser sunUser = plugin.getUserManager().getUserData(userName);
            if (sunUser != null) punishment.setUserId(sunUser.getId());

            this.dataHandler.addPunishment(punishment);
        });

        // Send messages.
        LangMessage msgUser = plugin.getMessage(BansLang.getPunishNotify(punishment)).replace(punishment.replacePlaceholders());
        LangMessage msgAdmin = plugin.getMessage(BansLang.getPunishSender(punishment)).replace(punishment.replacePlaceholders());
        LangMessage msgBroadcast = plugin.getMessage(BansLang.getPunishBroadcast(punishment)).replace(punishment.replacePlaceholders());

        msgAdmin.send(punisher);
        msgBroadcast.broadcast();

        // Notify players about their punishment.
        this.getPlayersToPunish(userName).forEach(player -> {
            if (type == PunishmentType.BAN) {
                player.kickPlayer(this.getDisconnectMessage(punishment));
            }
            else msgUser.send(player);
        });

        // Execute warn actions at the end to keep punishment messages order.
        if (type == PunishmentType.WARN) {
            List<String> commands = BansConfig.PUNISHMENTS_WARN_AUTO_COMMANDS.get().getOrDefault(punishmentsAmount, Collections.emptyList());
            commands.forEach(command -> {
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.replace(Placeholders.Player.NAME, userName));
            });
        }
    }

    public void kick(@NotNull String user, @NotNull CommandSender admin, @NotNull String reason) {
        if (!this.canBePunished(admin, user)) return;

        LangMessage banAdmin = plugin.getMessage(BansLang.KICK_DONE)
            .replace(Placeholders.PUNISHMENT_USER, user)
            .replace(Placeholders.PUNISHMENT_REASON, reason)
            .replace(Placeholders.PUNISHMENT_PUNISHER, admin.getName());

        LangMessage banBroadcast = plugin.getMessage(BansLang.KICK_BROADCAST)
            .replace(Placeholders.PUNISHMENT_USER, user)
            .replace(Placeholders.PUNISHMENT_REASON, reason)
            .replace(Placeholders.PUNISHMENT_PUNISHER, admin.getName());

        String banMsg = String.join("\n", BansConfig.GENERAL_DISCONNECT_INFO_KICK.get())
            .replace(Placeholders.PUNISHMENT_USER, user)
            .replace(Placeholders.PUNISHMENT_REASON, reason)
            .replace(Placeholders.PUNISHMENT_PUNISHER, admin.getName());

        // Kick all users with the same IP as banned user
        this.getPlayersToPunish(user).forEach(player -> player.kickPlayer(banMsg));

        banAdmin.send(admin);
        banBroadcast.broadcast();
    }

    public void unpunish(@NotNull String userName, @NotNull CommandSender admin, @NotNull PunishmentType type) {
        boolean isIP = RegexUtil.isIpAddress(userName);
        boolean hasPunishment = false;

        // When unpunishing by username, also check if the user has punishments by his IP,
        // so we can unpunish them too.
        if (type != PunishmentType.WARN && !isIP) {
            SunUser user = plugin.getUserManager().getUserData(userName);
            if (user != null && this.getActivePunishment(user.getIp(), type) != null) {
                this.unpunish(user.getIp(), admin, type);
                hasPunishment = true;
            }
        }

        // If used not banned by name or IP at all, then print error message.
        Punishment punishment = this.getActivePunishment(userName, type);
        if (punishment == null) {
            if (!hasPunishment) this.plugin.getMessage(BansLang.getNotPunished(type)).replace(Placeholders.GENERIC_USER, userName).send(admin);
            return;
        }

        LangMessage banAdmin = this.plugin.getMessage(BansLang.getForgiveSender(punishment)).replace(punishment.replacePlaceholders());
        LangMessage banBroadcast = this.plugin.getMessage(BansLang.getForgiveBroadcast(punishment))
            .replace(Placeholders.GENERIC_ADMIN, admin.getName()).replace(punishment.replacePlaceholders());

        punishment.expire();
        this.plugin.runTaskAsync(task -> this.dataHandler.savePunishment(punishment));

        banAdmin.send(admin);
        banBroadcast.broadcast();
    }

    @NotNull
    private String getDisconnectMessage(@NotNull Punishment punishment) {
        if (punishment.getType() == PunishmentType.BAN) {
            if (punishment.isPermanent()) {
                return punishment.replacePlaceholders().apply(String.join("\n", BansConfig.GENERAL_DISCONNECT_INFO_BAN_PERMANENT.get()));
            }
            else {
                return punishment.replacePlaceholders().apply(String.join("\n", BansConfig.GENERAL_DISCONNECT_INFO_BAN_TEMP.get()));
            }
        }
        else {
            return punishment.replacePlaceholders().apply(String.join("\n", BansConfig.GENERAL_DISCONNECT_INFO_KICK.get()));
        }
    }
}
