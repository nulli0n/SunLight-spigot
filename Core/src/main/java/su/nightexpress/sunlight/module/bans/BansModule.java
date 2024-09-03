package su.nightexpress.sunlight.module.bans;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.database.DatabaseConfig;
import su.nightexpress.nightcore.language.entry.LangText;
import su.nightexpress.nightcore.language.message.LangMessage;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.RankMap;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleInfo;
import su.nightexpress.sunlight.module.bans.command.*;
import su.nightexpress.sunlight.module.bans.config.BansConfig;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.config.BansPerms;
import su.nightexpress.sunlight.module.bans.data.BansDataHandler;
import su.nightexpress.sunlight.module.bans.listener.BansListener;
import su.nightexpress.sunlight.module.bans.menu.HistoryMenu;
import su.nightexpress.sunlight.module.bans.menu.PunishmentsMenu;
import su.nightexpress.sunlight.module.bans.punishment.*;
import su.nightexpress.sunlight.module.bans.util.BanTime;
import su.nightexpress.sunlight.module.bans.util.Placeholders;
import su.nightexpress.sunlight.module.bans.util.RankDuration;
import su.nightexpress.sunlight.module.bans.util.TimeUnit;
import su.nightexpress.sunlight.utils.SunUtils;
import su.nightexpress.sunlight.utils.UserInfo;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BansModule extends Module {

    private final Map<UUID, Map<PunishmentType, Set<PunishedPlayer>>> punishedPlayerMap;
    private final Map<String, Set<PunishedIP>>                        punishedIPMap;
    private final Map<String, Set<UserInfo>>                              altAccountsMap;

    private BansDataHandler dataHandler;
    private HistoryMenu     historyMenu;
    private PunishmentsMenu punishmentsMenu;

    public BansModule(@NotNull SunLightPlugin plugin, @NotNull String id) {
        super(plugin, id);
        this.punishedPlayerMap = new ConcurrentHashMap<>();
        this.punishedIPMap = new ConcurrentHashMap<>();
        this.altAccountsMap = new HashMap<>();
    }

    @Override
    protected void gatherInfo(@NotNull ModuleInfo moduleInfo) {
        moduleInfo.setConfigClass(BansConfig.class);
        moduleInfo.setLangClass(BansLang.class);
        moduleInfo.setPermissionsClass(BansPerms.class);
    }

    @Override
    protected void onModuleLoad() {
        this.dataHandler = new BansDataHandler(this.plugin, this, DatabaseConfig.read(this.getConfig(), "sunlight_bans", "bans.db"));
        this.dataHandler.setup();
        this.dataHandler.onSynchronize();

        this.loadMenu();
        this.loadSettings();
        this.loadCommands();
        if (BansConfig.isAltCheckerEnabled() && !BansConfig.ALTS_CHECK_CACHE_JOINED_ONLY.get()) {
            this.loadAlts();
        }

        this.addListener(new BansListener(this.plugin, this));
    }

    @Override
    protected void onModuleUnload() {
        this.historyMenu.clear();
        this.punishmentsMenu.clear();

        this.dataHandler.shutdown();

        this.punishedPlayerMap.clear();
        this.punishedIPMap.clear();
        this.altAccountsMap.clear();
    }

    private void loadMenu() {
        this.historyMenu = new HistoryMenu(this.plugin, this);
        this.punishmentsMenu = new PunishmentsMenu(this.plugin, this);
    }

    private void loadSettings() {
        for (TimeUnit timeUnit : TimeUnit.values()) {
            String[] aliases = ConfigValue.create("General.Time_Aliases." + timeUnit.name(), timeUnit.getAliases()).read(this.getConfig());
            timeUnit.setAliases(aliases);
        }
    }

    private void loadCommands() {
        PunishCommands.load(this.plugin, this);
        UnPunishCommands.load(this.plugin, this);
        HistoryCommands.load(this.plugin, this);
        ListCommands.load(this.plugin, this);
        BaseCommands.load(this.plugin, this);
    }

    private void loadAlts() {
        this.plugin.runTaskAsync(task -> {
            this.altAccountsMap.putAll(this.plugin.getData().getPlayerIPs());
        });
    }

    @NotNull
    public BansDataHandler getDataHandler() {
        return dataHandler;
    }

    @NotNull
    public static Map<String, PunishmentReason> getReasonMap() {
        return BansConfig.REASONS.get();
    }

    @Nullable
    public static PunishmentReason getReason(@NotNull String id) {
        return getReasonMap().get(id.toLowerCase());
    }

    @NotNull
    public static PunishmentReason getDefaultReason() {
        PunishmentReason reason = getReason(BansConfig.DEFAULT_REASON.get());
        return reason == null ? new PunishmentReason(BansLang.OTHER_NO_REASON.getString()) : reason;
    }

    @NotNull
    public static Collection<PunishmentReason> getReasons() {
        return getReasonMap().values();
    }

    @NotNull
    public Map<UUID, Map<PunishmentType, Set<PunishedPlayer>>> getPunishedPlayerMap() {
        return punishedPlayerMap;
    }

    @NotNull
    public Map<String, Set<PunishedIP>> getPunishedIPMap() {
        return punishedIPMap;
    }

    @NotNull
    public List<PunishedPlayer> getPunishedPlayers(@NotNull PunishmentType type) {
        List<PunishedPlayer> list = new ArrayList<>();
        this.punishedPlayerMap.forEach((userName, mapType) -> {
            list.addAll(mapType.getOrDefault(type, Collections.emptySet()));
        });
        return list;
    }

    @NotNull
    public List<PunishedIP> getPunishedIPs() {
        List<PunishedIP> list = new ArrayList<>();
        this.punishedIPMap.values().forEach(list::addAll);
        return list;
    }

    @NotNull
    public Map<PunishmentType, Set<PunishedPlayer>> getPlayerPunishments(@NotNull UUID playerId) {
        return this.punishedPlayerMap.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>());
    }

    @NotNull
    public Set<PunishedPlayer> getPlayerPunishments(@NotNull UUID playerId, @NotNull PunishmentType type) {
        return this.getPlayerPunishments(playerId).computeIfAbsent(type, k -> new HashSet<>());
    }

    @NotNull
    public Set<PunishedIP> getIPPunishments(@NotNull String address) {
        return this.punishedIPMap.computeIfAbsent(address, k -> new HashSet<>());
    }

    @NotNull
    public List<PunishedPlayer> getActivePlayerPunishments(@NotNull UUID playerId, @NotNull PunishmentType type) {
        return this.getPlayerPunishments(playerId, type).stream()
            .filter(Predicate.not(PunishData::isExpired))
            //.sorted(Comparator.comparingLong(PunishData::getCreateDate).reversed())
            .toList();
    }

    @NotNull
    public List<PunishedIP> getActiveIPPunishments(@NotNull String address) {
        return this.getIPPunishments(address).stream()
            .filter(Predicate.not(PunishData::isExpired))
            //.sorted(Comparator.comparingLong(PunishData::getCreateDate).reversed())
            .toList();
    }

    @Nullable
    public PunishedPlayer getActivePlayerPunishment(@NotNull UUID playerId, @NotNull PunishmentType type) {
        return this.getActivePlayerPunishments(playerId, type).stream().findFirst().orElse(null);
    }

    @Nullable
    public PunishedIP getActiveIPPunishment(@NotNull String address) {
        return this.getActiveIPPunishments(address).stream().findFirst().orElse(null);
    }

    public boolean openPunishments(@NotNull Player player, @NotNull PunishmentType type) {
        return this.punishmentsMenu.open(player, new PunishmentsMenu.Source(type));
    }

    public boolean openHistory(@NotNull Player player, @NotNull UserInfo userInfo, @NotNull PunishmentType type) {
        return this.historyMenu.open(player, new HistoryMenu.PlayerSource(userInfo, type));
    }

    public boolean openHistory(@NotNull Player player, @NotNull String address) {
        return this.historyMenu.open(player, new HistoryMenu.AddressSource(address, PunishmentType.BAN));
    }

    public void cachePunishmentData(@NotNull PunishData punishData, @NotNull PunishmentType type) {
        if (punishData instanceof PunishedIP punishedIP) {
            this.getIPPunishments(punishedIP.getAddress()).add(punishedIP);
        }
        else if (punishData instanceof PunishedPlayer punishedPlayer) {
            this.getPlayerPunishments(punishedPlayer.getPlayerId(), type).add(punishedPlayer);
        }
    }

    public void deletePunishment(@NotNull PunishData punishData, @NotNull PunishmentType type) {
        if (punishData instanceof PunishedIP punishedIP) {
            this.getIPPunishments(punishedIP.getAddress()).remove(punishedIP);
        }
        else if (punishData instanceof PunishedPlayer punishedPlayer) {
            this.getPlayerPunishments(punishedPlayer.getPlayerId(), type).remove(punishedPlayer);
        }
        this.plugin.runTaskAsync(task -> this.dataHandler.deleteData(punishData, type));
    }

    public boolean isMuted(@NotNull Player player) {
        return this.isMuted(player.getUniqueId());
    }

    public boolean isMuted(@NotNull UUID playerId) {
        return this.getActivePlayerPunishment(playerId, PunishmentType.MUTE) != null;
    }

    public boolean isBanned(@NotNull UUID playerId) {
        return this.getActivePlayerPunishment(playerId, PunishmentType.BAN) != null;
    }

    public boolean hasImmunity(@NotNull String target) {
        return BansConfig.IMMUNE_LIST.get().contains(target.toLowerCase()) || SunUtils.isLocalAddress(target);
    }

    private boolean canBePunished(@NotNull CommandSender executor, @NotNull String target) {
        if (this.hasImmunity(target)) {
            BansLang.PUNISHMENT_ERROR_IMMUNE.getMessage().replace(Placeholders.PUNISHMENT_TARGET, target).send(executor);
            return false;
        }

        if (target.equalsIgnoreCase(executor.getName())) {
            Lang.ERROR_COMMAND_NOT_YOURSELF.getMessage().send(executor);
            return false;
        }

        return true;
    }

    @NotNull
    private Set<Player> getPlayersToPunish(@NotNull PunishData punishData) {
        return this.plugin.getServer().getOnlinePlayers().stream().filter(punishData::isApplicable).collect(Collectors.toSet());
    }

    public static int getRankPriority(@NotNull Player player) {
        RankMap<Integer> map = BansConfig.PUNISHMENTS_RANK_PRIORITY.get();
        return map.getGreatest(player);
    }

    @Nullable
    public static BanTime parseBanTime(@NotNull String str) {
        TimeUnit timeUnit = null;
        for (TimeUnit unit : TimeUnit.values()) {
            for (String alias : unit.getAliases()) {
                if (!str.endsWith(alias)) continue;

                timeUnit = unit;
                str = str.substring(0, str.length() - alias.length());
                break;
            }
        }

        if (timeUnit == null) return null;
        if (timeUnit == TimeUnit.PERMANENT) return BanTime.PERMANENT;

        long amount = NumberUtil.getInteger(str, 0);
        if (amount == 0) return null;

        return new BanTime(timeUnit, amount);
    }

    @Nullable
    public RankDuration getGreatestDuration(@NotNull Player player, @NotNull PunishmentType type) {
        Map<String, Map<PunishmentType, RankDuration>> durationMap = BansConfig.PUNISHMENTS_RANK_MAX_TIMES.get();
        Set<RankDuration> durations = new HashSet<>();
        for (String rank : Players.getPermissionGroups(player)) {
            RankDuration duration = durationMap.getOrDefault(rank, Collections.emptyMap()).get(type);
            if (duration != null) {
                durations.add(duration);
            }
        }

        RankDuration permanent = durations.stream().filter(rankDuration -> rankDuration.getTimeUnit() == TimeUnit.PERMANENT).findFirst().orElse(null);
        if (permanent != null) return permanent;

        return durations.stream().max(Comparator.comparingLong(RankDuration::getInMillis)).orElse(null);
    }

    public boolean checkRankPriority(@NotNull CommandSender executor, @NotNull UserInfo targetInfo) {
        if (!(executor instanceof Player player)) return true;

        int punisherPriority = getRankPriority(player);
        Player target = this.plugin.getServer().getPlayer(targetInfo.getId());
        if (target == null) target = this.plugin.getSunNMS().loadPlayerData(targetInfo.getId(), targetInfo.getName());

        if (getRankPriority(target) >= punisherPriority) {
            BansLang.PUNISHMENT_ERROR_RANK_PRIORITY.getMessage()
                .replace(Placeholders.PUNISHMENT_TARGET, target.getDisplayName())
                .send(player);
            return false;
        }

        return true;
    }

    public boolean checkDuration(@NotNull CommandSender executor, @NotNull BanTime banTime, @NotNull PunishmentType type) {
        if (executor.hasPermission(BansPerms.BYPASS_DURATION_LIMIT)) return true;
        if (!(executor instanceof Player player)) return true;

        RankDuration highest = this.getGreatestDuration(player, type);
        // If there is allowed permanent punishment duration, then player can use any other duration.
        if (highest == null || highest.getTimeUnit() == TimeUnit.PERMANENT) return true;

        // Now make sure that specified duration is not permanent or under the limit, prevent otherwise.
        if (banTime.isPermanent() || banTime.getInMillis() > highest.getInMillis()) {
            BansLang.PUNISHMENT_ERROR_PUNISH_DURATION.getMessage()
                .replace(Placeholders.GENERIC_TIME, TimeUtil.formatTime(highest.getInMillis()))
                .send(executor);
            return false;
        }

        return true;
    }

    public boolean checkUnpunishDuration(@NotNull CommandSender executor, @NotNull PunishData punishData, @NotNull PunishmentType type) {
        if (!BansConfig.PUNISHMENTS_DURATION_LIMIT_TO_UNPUNISH.get()) return true;
        if (executor.hasPermission(BansPerms.BYPASS_DURATION_LIMIT)) return true;
        if (!(executor instanceof Player player)) return true;

        RankDuration highest = this.getGreatestDuration(player, type);
        // If there is allowed permanent punishment duration, then player can use any other duration.
        if (highest == null || highest.getTimeUnit() == TimeUnit.PERMANENT) return true;

        BanTime banTime = punishData.isPermanent() ? BanTime.PERMANENT : new BanTime(TimeUnit.SECONDS, TimeUnit.SECONDS.getAbsolute(punishData.getExpireDate() - punishData.getCreateDate()));

        // Now make sure that specified duration is not permanent or under the limit, prevent otherwise.
        if (banTime.isPermanent() || banTime.getInMillis() > highest.getInMillis()) {
            BansLang.PUNISHMENT_ERROR_UNPUNISH_DURATION.getMessage()
                .replace(Placeholders.GENERIC_TIME, TimeUtil.formatTime(highest.getInMillis()))
                .send(executor);
            return false;
        }

        return true;
    }

    public boolean kick(@NotNull Player player, @NotNull CommandSender executor, @NotNull PunishmentReason reason, boolean silent) {
        if (!this.canBePunished(executor, player.getName())) return false;
        if (!this.checkRankPriority(executor, new UserInfo(player))) return false;

        player.kickPlayer(NightMessage.asLegacy(String.join("\n", BansConfig.GENERAL_DISCONNECT_INFO_KICK.get())
            .replace(Placeholders.PUNISHMENT_REASON, reason.getText())
            .replace(Placeholders.PUNISHMENT_PUNISHER, SunUtils.getSenderName(executor))
        ));

        BansLang.KICK_DONE.getMessage()
            .replace(Placeholders.PUNISHMENT_TARGET, player.getDisplayName())
            .replace(Placeholders.PUNISHMENT_REASON, reason.getText())
            .send(executor);

        if (!silent) {
            BansLang.KICK_BROADCAST.getMessage()
                .replace(Placeholders.PUNISHMENT_TARGET, player.getDisplayName())
                .replace(Placeholders.PUNISHMENT_REASON, reason.getText())
                .replace(Placeholders.PUNISHMENT_PUNISHER, SunUtils.getSenderName(executor))
                .broadcast();
        }

        return true;
    }

    public boolean punishPlayer(@NotNull UserInfo userInfo, @NotNull CommandSender executor, @NotNull PunishmentReason reason, @NotNull BanTime banTime,
                             @NotNull PunishmentType type,
                             boolean silent) {
        String playerName = userInfo.getName();
        UUID playerId = userInfo.getId();

        if (!this.canBePunished(executor, playerName)) return false;
        if (!this.checkRankPriority(executor, userInfo)) return false;
        if (!this.checkDuration(executor, banTime, type)) return false;

        // Override all previous bans/mutes/warns by making them expired when a fresh one is added.
        // For warns, do that only if reached max. warns value to apply custom commands.
        List<PunishedPlayer> activePunishments = this.getActivePlayerPunishments(playerId, type);
        int punishAmount = activePunishments.size() + 1; // Count also this one.
        int maxWarns = BansConfig.PUNISHMENTS_WARN_MAX_AMOUNT.get();

        if (type != PunishmentType.WARN || (maxWarns > 0 && punishAmount >= maxWarns)) {
            activePunishments.forEach(PunishData::expire);
            this.plugin.runTaskAsync(task -> {
                activePunishments.forEach(data -> this.dataHandler.saveData(data, type));
            });
        }

        UUID banId = UUID.randomUUID();
        String admin = executor.getName();
        long createDate = System.currentTimeMillis();
        long expireDate = banTime.toTimestamp();
        String textReason = reason.getText();

        PunishedPlayer punishedPlayer = new PunishedPlayer(banId, playerId, playerName, textReason, admin, createDate, expireDate);
        this.punish(punishedPlayer, type, executor, silent);

        // Execute warn actions at the end to keep punishment messages order.
        if (type == PunishmentType.WARN) {
            List<String> commands = BansConfig.PUNISHMENTS_WARN_AUTO_COMMANDS.get().getOrDefault(punishAmount, Collections.emptyList());
            commands.forEach(command -> {
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.replace(Placeholders.PLAYER_NAME, playerName));
            });
        }

        return true;
    }

    public boolean banIP(@NotNull String address, @NotNull CommandSender executor, @NotNull PunishmentReason reason, @NotNull BanTime banTime, boolean silent) {
        if (!this.canBePunished(executor, address)) return false;
        if (!this.checkDuration(executor, banTime, PunishmentType.BAN)) return false;

        // Override all previous bans of that IP by making them expired when a fresh one is added.
        List<PunishedIP> activePunishments = this.getActiveIPPunishments(address);
        activePunishments.forEach(PunishData::expire);
        this.plugin.runTaskAsync(task -> {
            activePunishments.forEach(data -> this.dataHandler.saveData(data, PunishmentType.BAN));
        });

        UUID banId = UUID.randomUUID();
        String admin = executor.getName();
        long createDate = System.currentTimeMillis();
        long expireDate = banTime.toTimestamp();
        String textReason = reason.getText();

        PunishedIP punishedIP = new PunishedIP(banId, address, textReason, admin, createDate, expireDate);
        this.punish(punishedIP, PunishmentType.BAN, executor, silent);

        return true;
    }

    private void punish(@NotNull PunishData punishData, @NotNull PunishmentType type, @NotNull CommandSender executor, boolean silent) {
        this.cachePunishmentData(punishData, type);
        this.plugin.runTaskAsync(task -> this.dataHandler.addData(punishData, type));

        BansLang.getPunishOutput(punishData, type).getMessage().replace(punishData.replacePlaceholders()).send(executor);

        if (!silent) {
            BansLang.getPunishBroadcast(punishData, type).getMessage()
                    .replace(Placeholders.PUNISHMENT_PUNISHER, SunUtils.getSenderName(executor))
                    .replace(punishData.replacePlaceholders())
                    .broadcast();
        }

        // Notify players about their punishment.
        Set<Player> targets = this.getPlayersToPunish(punishData);
        if (type == PunishmentType.BAN) {
            List<String> list = punishData.isPermanent() ? BansConfig.GENERAL_DISCONNECT_INFO_BAN_PERMANENT.get() : BansConfig.GENERAL_DISCONNECT_INFO_BAN_TEMP.get();
            String info = NightMessage.asLegacy(punishData.replacePlaceholders().apply(String.join("\n", list)));

            targets.forEach(target -> target.kickPlayer(info));
        }
        else {
            LangText notifyMessage = BansLang.getPunishNotify(punishData, type);
            if (notifyMessage != null) {
                LangMessage message = notifyMessage.getMessage().replace(punishData.replacePlaceholders());

                targets.forEach(message::send);
            }
        }
    }

    public boolean unpunishPlayer(@NotNull UserInfo userInfo, @NotNull CommandSender executor, @NotNull PunishmentType type, boolean silent) {
        String playerName = userInfo.getName();
        UUID playerId = userInfo.getId();

        PunishedPlayer punishedPlayer = this.getActivePlayerPunishment(playerId, type);
        if (punishedPlayer == null) {
            BansLang.getNotPunished(type).getMessage().replace(Placeholders.PUNISHMENT_TARGET, playerName).send(executor);
            return false;
        }

        if (!this.checkUnpunishDuration(executor, punishedPlayer, type)) return false;

        this.unpunish(punishedPlayer, type, executor, silent);
        return true;
    }

    public boolean unbanIP(@NotNull String address, @NotNull CommandSender executor, boolean silent) {
        PunishedIP punishedIP = this.getActiveIPPunishment(address);
        if (punishedIP == null) {
            BansLang.PUNISHMENT_ERROR_IP_NOT_BANNED.getMessage().replace(Placeholders.PUNISHMENT_TARGET, address).send(executor);
            return false;
        }

        if (!this.checkUnpunishDuration(executor, punishedIP, PunishmentType.BAN)) return false;

        this.unpunish(punishedIP, PunishmentType.BAN, executor, silent);
        return true;
    }

    private void unpunish(@NotNull PunishData punishData, @NotNull PunishmentType type, @NotNull CommandSender executor, boolean silent) {
        punishData.expire();
        this.plugin.runTaskAsync(task -> this.dataHandler.saveData(punishData, type));

        BansLang.getUnPunishOutput(punishData, type).getMessage()
            .replace(punishData.replacePlaceholders())
            .send(executor);

        if (!silent) {
            BansLang.getUnPunishBroadcast(punishData, type).getMessage()
                .replace(Placeholders.PUNISHMENT_PUNISHER, SunUtils.getSenderName(executor))
                .replace(punishData.replacePlaceholders())
                .broadcast();
        }
    }

    @NotNull
    public Map<String, Set<UserInfo>> getAltAccountsMap() {
        return altAccountsMap;
    }

    @NotNull
    public Set<UserInfo> getAltAccounts(@NotNull InetAddress address) {
        return this.getAltAccounts(SunUtils.getRawAddress(address));
    }

    @NotNull
    public Set<UserInfo> getAltAccounts(@NotNull String inetAddress) {
        return this.altAccountsMap.getOrDefault(inetAddress, Collections.emptySet());
    }

    public void linkAltAccount(@NotNull SunUser user) {
        this.linkAltAccount(user.getInetAddress(), new UserInfo(user));
    }

    public void linkAltAccount(@NotNull String inetAddress, @NotNull UserInfo userInfo) {
        if (SunUtils.isLocalAddress(inetAddress)) return;

        this.altAccountsMap.computeIfAbsent(inetAddress, k -> new HashSet<>()).add(userInfo);
    }
}
