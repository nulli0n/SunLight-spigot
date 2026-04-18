package su.nightexpress.sunlight.module.bans;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.bridge.chat.UniversalChatEvent;
import su.nightexpress.nightcore.bridge.chat.UniversalChatEventHandler;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.user.UserInfo;
import su.nightexpress.nightcore.util.CommandUtil;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.nightcore.util.placeholder.PlaceholderContext;
import su.nightexpress.nightcore.util.text.night.NightMessage;
import su.nightexpress.nightcore.util.time.TimeFormats;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.hook.placeholder.PlaceholderRegistry;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleContext;
import su.nightexpress.sunlight.module.bans.command.*;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.config.BansPerms;
import su.nightexpress.sunlight.module.bans.config.BansSettings;
import su.nightexpress.sunlight.module.bans.data.BansDataManager;
import su.nightexpress.sunlight.module.bans.listener.BansListener;
import su.nightexpress.sunlight.module.bans.menu.HistoryMenu;
import su.nightexpress.sunlight.module.bans.menu.PunishmentsMenu;
import su.nightexpress.sunlight.module.bans.punishment.*;
import su.nightexpress.sunlight.module.bans.time.BanTime;
import su.nightexpress.sunlight.module.bans.time.BanTimeUnit;
import su.nightexpress.sunlight.SLUtils;
import su.nightexpress.sunlight.utils.FutureUtils;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BansModule extends Module {

    private final BansSettings              settings;
    private final BansDataManager           dataManager;
    private final UniversalChatEventHandler chatHandler;

    private final Map<String, BanTimeUnit> timeUnitAliasMap;

    private final PunishmentRepository bansRepository;
    private final PunishmentRepository mutesRepository;
    private final PunishmentRepository warnsRepository;

    private HistoryMenu     historyMenu;
    private PunishmentsMenu punishmentsMenu;

    private boolean dataLoaded;

    public BansModule(@NotNull ModuleContext context) {
        super(context);
        this.settings = new BansSettings();
        this.dataManager = new BansDataManager(this.plugin, this.dataHandler, this);
        this.chatHandler = this::handleChatEvent;

        this.timeUnitAliasMap = new HashMap<>();

        this.bansRepository = new PunishmentRepository();
        this.mutesRepository = new PunishmentRepository();
        this.warnsRepository = new PunishmentRepository();
    }

    @Override
    protected void loadModule(@NotNull FileConfig config) {
        this.settings.load(config);
        this.plugin.injectLang(BansLang.class);
        this.dataManager.init(this.settings.dataTablePrefix.get());

        this.loadTimeAliases();
        this.loadMenu();
        this.loadData();

        this.addListener(new BansListener(this.plugin, this));
        this.plugin.addChatHandler(EventPriority.LOWEST, this.chatHandler);

        this.addAsyncTask(this::savePunishments, this.settings.dataSaveInterval.get());
    }

    @Override
    protected void unloadModule() {
        this.plugin.removeChatHandler(this.chatHandler);
        this.clearRepositories();
        this.dataLoaded = false;
    }

    @Override
    protected void registerPermissions(@NotNull PermissionTree root) {
        root.merge(BansPerms.ROOT);
    }

    @Override
    protected void registerCommands() {
        this.commandRegistry.addProvider("bans-punish",
            new PunishmentCommandsProvider(this.plugin, this, this.userManager));
        this.commandRegistry.addProvider("bans-pardon",
            new PardonCommandsProvider(this.plugin, this, this.userManager));
        this.commandRegistry.addProvider("bans-history",
            new HistoryCommandsProvider(this.plugin, this, this.userManager));
        this.commandRegistry.addProvider("bans-list", new ListCommandsProvider(this.plugin, this));

        if (this.settings.isAltCheckerEnabled()) {
            this.commandRegistry.addProvider("bans-alts", new AltsCommandProvider(this.plugin, this, this.userManager));
        }
    }

    @Override
    public void registerPlaceholders(@NotNull PlaceholderRegistry registry) {
        // TODO
    }

    private void clearRepositories() {
        this.bansRepository.clear();
        this.mutesRepository.clear();
        this.warnsRepository.clear();
    }

    private void loadTimeAliases() {
        this.settings.timeUnitAliases.get().forEach((unit, aliases) -> {
            for (String alias : aliases) {
                this.timeUnitAliasMap.put(alias, unit);
            }
        });
    }

    private void loadMenu() {
        this.historyMenu = new HistoryMenu(this.plugin, this);
        this.historyMenu.load(this.plugin, FileConfig.load(this.getLocalUIPath(), "history.yml"));

        this.punishmentsMenu = new PunishmentsMenu(this.plugin, this);
        this.punishmentsMenu.load(this.plugin, FileConfig.load(this.getLocalUIPath(), "punishments.yml"));
    }

    private void loadData() {
        this.dataLoaded = false;
        this.plugin.runTaskAsync(task -> {
            this.dataManager.getPlayerPunishments().forEach(punishment -> {
                this.getPunishmentRepository(punishment).addPlayerPunishment(punishment);
            });

            this.dataManager.getInetPunishments().forEach(punishment -> {
                this.getPunishmentRepository(punishment).addInetPunishment(punishment);
            });

            this.dataLoaded = true;
            this.plugin.runTask(this::kickBanned); // Kick banned players in case when there are new database ban entries for online players.
        });
    }

    private void savePunishments() {
        List<PlayerPunishment> playerPunishments = new ArrayList<>();
        List<InetPunishment> inetPunishments = new ArrayList<>();

        for (PunishmentType type : PunishmentType.values()) {
            PunishmentRepository repository = this.getPunishmentRepository(type);

            repository.getPlayerPunishments().stream().filter(AbstractPunishment::isDirty).peek(
                AbstractPunishment::markClean).forEach(playerPunishments::add);
            repository.getInetPunishments().stream().filter(AbstractPunishment::isDirty).peek(
                AbstractPunishment::markClean).forEach(inetPunishments::add);
        }

        this.dataManager.updatePlayerPunishments(playerPunishments);
        this.dataManager.updateInetPunishments(inetPunishments);
    }

    private void kickBanned() {
        Players.getOnline().forEach(player -> {
            AbstractPunishment punishment = this.bansRepository.getActivePlayerOrInetPunishment(player.getUniqueId(),
                SLUtils.getInetAddress(player).orElse(null));
            if (punishment == null) return;

            Players.kick(player, this.getKickScreenText(punishment));
        });
    }

    @NotNull
    public BansDataManager getDataManager() {
        return this.dataManager;
    }

    @NotNull
    public List<String> getReasonIds() {
        return List.copyOf(this.settings.reasonMap.get().keySet());
    }

    @NotNull
    public Set<PunishmentReason> getReasons() {
        return Set.copyOf(this.settings.reasonMap.get().values());
    }

    @Nullable
    public PunishmentReason getReasonById(@NotNull String id) {
        return this.settings.reasonMap.get().get(id.toLowerCase());
    }

    @NotNull
    public PunishmentReason getDefaultReason() {
        PunishmentReason reason = this.getReasonById(SLPlaceholders.DEFAULT);
        return reason == null ? new PunishmentReason(BansLang.OTHER_NO_REASON.text()) : reason;
    }

    @NotNull
    public PunishmentRepository getPunishmentRepository(@NotNull AbstractPunishment punishment) {
        return this.getPunishmentRepository(punishment.getType());
    }

    @NotNull
    public PunishmentRepository getPunishmentRepository(@NotNull PunishmentType type) {
        return switch (type) {
            case BAN -> this.getBansRepository();
            case MUTE -> this.getMutesRepository();
            case WARN -> this.getWarnsRepository();
        };
    }

    @NotNull
    public PunishmentRepository getBansRepository() {
        return this.bansRepository;
    }

    @NotNull
    public PunishmentRepository getMutesRepository() {
        return this.mutesRepository;
    }

    @NotNull
    public PunishmentRepository getWarnsRepository() {
        return this.warnsRepository;
    }

    @NotNull
    public Set<PlayerPunishment> getPlayerPunishments(@NotNull PunishmentType type) {
        return this.getPunishmentRepository(type).getPlayerPunishments();
    }

    @NotNull
    public Set<PlayerPunishment> getPlayerPunishments(@NotNull UUID playerId, @NotNull PunishmentType type) {
        return this.getPunishmentRepository(type).getPlayerPunishments(playerId);
    }

    @NotNull
    public Set<InetPunishment> getInetPunishments() {
        return this.getPunishmentRepository(PunishmentType.BAN).getInetPunishments();
    }

    @NotNull
    public Set<InetPunishment> getInetPunishments(@NotNull InetAddress address) {
        return this.getPunishmentRepository(PunishmentType.BAN).getInetPunishments(address);
    }

    @Nullable
    public PlayerPunishment getActivePlayerPunishment(@NotNull UUID playerId, @NotNull PunishmentType type) {
        return this.getPunishmentRepository(type).getActivePlayerPunishment(playerId);
    }

    @NotNull
    public Set<PlayerPunishment> getActivePlayerPunishments(@NotNull UUID playerId, @NotNull PunishmentType type) {
        return this.getPunishmentRepository(type).getActivePlayerPunishments(playerId);
    }

    @Nullable
    public InetPunishment getActiveInetPunishment(@NotNull InetAddress address) {
        return this.getPunishmentRepository(PunishmentType.BAN).getActiveInetPunishment(address);
    }

    @NotNull
    public Set<InetPunishment> getActiveInetPunishments(@NotNull InetAddress address) {
        return this.getPunishmentRepository(PunishmentType.BAN).getActiveInetPunishments(address);
    }

    public boolean hasActivePunishment(@NotNull UUID playerId, @NotNull PunishmentType type) {
        return !this.getActivePlayerPunishments(playerId, type).isEmpty();
    }

    public boolean hasActivePunishment(@NotNull InetAddress address, @NotNull PunishmentType type) {
        return !this.getActiveInetPunishments(address).isEmpty();
    }

    public boolean openPunishments(@NotNull Player player, @NotNull PunishmentType type) {
        return this.punishmentsMenu.show(player, type);
    }

    public boolean openHistory(@NotNull Player player, @NotNull UserInfo userInfo, @NotNull PunishmentType type) {
        return this.historyMenu.show(player, userInfo, type);
    }

    private void addPlayerPunishment(@NotNull PlayerPunishment punishment) {
        this.getPunishmentRepository(punishment).addPlayerPunishment(punishment);
        this.plugin.runTaskAsync(task -> this.dataManager.insertPunishment(punishment));
    }

    public void deletePlayerPunishment(@NotNull PlayerPunishment punishment) {
        this.getPunishmentRepository(punishment).removePlayerPunishment(punishment);
        this.plugin.runTaskAsync(task -> this.dataManager.deletePlayerPunishment(punishment));
    }

    private void addInetPunishment(@NotNull InetPunishment punishment) {
        this.getPunishmentRepository(punishment).addInetPunishment(punishment);
        this.plugin.runTaskAsync(task -> this.dataManager.insertPunishment(punishment));
    }

    public void deleteInetPunishment(@NotNull InetPunishment punishment) {
        this.getPunishmentRepository(punishment).removeInetPunishment(punishment);
        this.plugin.runTaskAsync(task -> this.dataManager.deleteInetPunishment(punishment));
    }

    public boolean isMuted(@NotNull Player player) {
        return this.isMuted(player.getUniqueId());
    }

    public boolean isMuted(@NotNull UUID playerId) {
        return this.hasActivePunishment(playerId, PunishmentType.MUTE);
    }

    public boolean isBanned(@NotNull UUID playerId) {
        return this.hasActivePunishment(playerId, PunishmentType.BAN);
    }

    public boolean hasImmunity(@NotNull Player player) {
        return this.hasImmunity(player.getName()) || this.hasImmunity(player.getUniqueId().toString());
    }

    public boolean hasImmunity(@NotNull InetAddress address) {
        return this.hasImmunity(address.getHostAddress());
    }

    public boolean hasImmunity(@NotNull String name) {
        return this.settings.immunityList.get().contains(LowerCase.INTERNAL.apply(name));
    }

    @NotNull
    private Set<Player> getPlayersToPunish(@NotNull AbstractPunishment punishData) {
        return this.plugin.getServer().getOnlinePlayers().stream().filter(punishData::isApplicable).collect(Collectors
            .toSet());
    }

    @NotNull
    public BanTime getGreatestDuration(@NotNull CommandSender sender, @NotNull PunishmentType type) {
        if (!(sender instanceof Player player)) return BanTime.permanent(); // No limits for Console.

        var durationMap = this.settings.durationLimitsRanks.get();

        Set<BanTime> banTimes = new HashSet<>();
        for (String rank : Players.getInheritanceGroups(player)) {
            BanTime banTime = durationMap.getOrDefault(rank, Collections.emptyMap()).get(type);
            if (banTime != null) {
                banTimes.add(banTime);
            }
        }

        return banTimes.stream().max((o1, o2) -> {
            if (o1.isGreater(o2)) return 1;
            if (o1.isSmaller(o2)) return -1;
            return 0;
        }).orElse(BanTime.permanent());
    }

    public int getRankPriority(@NotNull CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            return this.settings.consolePriority.get();
        }

        if (sender instanceof Player player) {
            return this.getRankPriority(Players.getInheritanceGroups(player));
        }

        return 0;
    }

    @NotNull
    private CompletableFuture<Integer> getRankPriority(@NotNull UserInfo profile) {
        return Players.getInheritanceGroups(profile.id()).thenApply(this::getRankPriority);
    }

    private int getRankPriority(@NotNull Set<String> ranks) {
        var priorityMap = this.settings.rankPriorities.get();
        return ranks.stream().mapToInt(rank -> priorityMap.getOrDefault(rank, 0)).max().orElse(0);
    }

    public void handleLoginEvent(@NotNull AsyncPlayerPreLoginEvent event) {
        InetAddress address = event.getAddress();
        UUID playerId = event.getUniqueId();

        AbstractPunishment punishment = this.bansRepository.getActivePlayerOrInetPunishment(playerId, address);
        if (punishment == null) return;

        // Update player name in case it was changed.
        if (punishment instanceof PlayerPunishment playerPunishment) {
            playerPunishment.updateName(event.getName());
        }

        String message = this.getKickScreenText(punishment);

        Players.disallowLogin(event, AsyncPlayerPreLoginEvent.Result.KICK_BANNED, message);
    }

    public void handleCommandEvent(@NotNull PlayerCommandPreprocessEvent event) {
        if (!this.settings.muteBlockCommandsEnabled.get()) return;

        Set<String> blockedCommands = this.settings.muteBlockCommandsList.get();
        if (blockedCommands.isEmpty()) return;

        Player player = event.getPlayer();
        if (!this.dataLoaded) {
            this.sendPrefixed(BansLang.ERROR_DATA_NOT_LOADED_USER_FEEDBACK, player);
            return;
        }

        PlayerPunishment punishment = this.getActivePlayerPunishment(player.getUniqueId(), PunishmentType.MUTE);
        if (punishment == null) return;

        String command = CommandUtil.getCommandName(event.getMessage());
        Set<String> aliases = CommandUtil.getAliases(command, true);

        if (aliases.stream().anyMatch(blockedCommands::contains)) {
            event.setCancelled(true);

            MessageLocale locale = punishment
                .isPermanent() ? BansLang.MUTE_SPEAK_NOTIFY_PERMANENT : BansLang.MUTE_SPEAK_NOTIFY_TEMPORAL;

            this.sendPrefixed(locale, player, builder -> builder.with(punishment.placeholders()));
        }
    }

    public void handleChatEvent(@NotNull UniversalChatEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        if (!this.dataLoaded) {
            this.sendPrefixed(BansLang.ERROR_DATA_NOT_LOADED_USER_FEEDBACK, player);
            return;
        }

        PlayerPunishment punishment = this.getActivePlayerPunishment(player.getUniqueId(), PunishmentType.MUTE);
        if (punishment == null) return;

        event.setCancelled(true);

        MessageLocale locale = punishment
            .isPermanent() ? BansLang.MUTE_SPEAK_NOTIFY_PERMANENT : BansLang.MUTE_SPEAK_NOTIFY_TEMPORAL;

        this.sendPrefixed(locale, player, builder -> builder.with(punishment.placeholders()));
    }

    public void handleJoin(@NotNull PlayerJoinEvent event) {
        if (!this.settings.isAltCheckerEnabled()) return;

        Player player = event.getPlayer();
        if (player.hasPermission(BansPerms.BYPASS_ALT_DETECTION)) return;

        SLUtils.getInetAddress(player).ifPresent(address -> this.lookupAltProfiles(address).thenAccept(alts -> {
            UserInfo profile = UserInfo.of(player);
            Set<CommandSender> receivers = new HashSet<>();
            receivers.add(this.plugin.getServer().getConsoleSender());
            receivers.addAll(Players.getOnline().stream().filter(staff -> staff.hasPermission(BansPerms.ALTS_NOTIFY))
                .collect(Collectors.toSet()));
            receivers.forEach(sender -> this.notifyAltProfiles(sender, profile, address, alts));
        }).whenComplete(FutureUtils::printStacktrace));
    }

    public boolean kick(@NotNull CommandSender sender, @NotNull Player victim, @NotNull PunishmentReason reason,
                        boolean silent) {
        if (!this.dataLoaded) {
            this.sendPrefixed(BansLang.ERROR_DATA_NOT_LOADED_ADMIN_FEEDBACK, sender);
            return false;
        }

        if (victim == sender) {
            this.sendPrefixed(BansLang.KICK_ERROR_YOURSELF, sender, builder -> builder.with(CommonPlaceholders.PLAYER
                .resolver(victim)));
            return false;
        }

        if (this.hasImmunity(victim)) {
            this.sendPrefixed(BansLang.IMMUNITY_FEEDBACK, sender, builder -> builder.with(
                BansPlaceholders.GENERIC_TARGET, victim::getName));
            return false;
        }

        int senderPriority = this.getRankPriority(sender);
        int victimPriority = this.getRankPriority(victim);

        if (victimPriority >= senderPriority) {
            this.sendPrefixed(BansLang.KICK_ERROR_LOW_PRIORITY, sender, builder -> builder.with(
                CommonPlaceholders.PLAYER.resolver(victim)));
            return false;
        }

        PlaceholderContext context = PlaceholderContext.builder()
            .with(CommonPlaceholders.PLAYER.resolver(victim))
            .with(BansPlaceholders.GENERIC_REASON, reason::getText)
            .with(BansPlaceholders.GENERIC_EXECUTOR, () -> SLUtils.getSenderName(sender))
            .with(BansPlaceholders.PUNISHMENT_TARGET, () -> Players.getDisplayNameSerialized(victim)) // old
            .with(BansPlaceholders.PUNISHMENT_REASON, reason::getText) // old
            .with(BansPlaceholders.PUNISHMENT_WHO, () -> SLUtils.getSenderName(sender)) // old
            .build();

        Players.kick(victim, NightMessage.parse(context.apply(String.join("\n", this.settings.disconnectScreenKick
            .get()))));

        this.sendPrefixed(BansLang.KICK_FEEDBACK, sender, context);
        if (!silent) {
            this.broadcastPrefixed(BansLang.KICK_BROADCAST, context);
        }

        return true;
    }

    @NotNull
    public CompletableFuture<Boolean> punishPlayer(@NotNull CommandSender sender, @NotNull UserInfo victim,
                                                   @NotNull PunishmentContext context) {
        return this.getRankPriority(victim).thenApplyAsync(victimPriority -> {

            return this.punishPlayer(sender, victim, victimPriority, context);

        }, this.plugin::runTask).whenComplete(FutureUtils::printStacktrace); // Back to main thread
    }

    public boolean punishPlayer(@NotNull CommandSender sender, @NotNull Player victim,
                                @NotNull PunishmentContext context) {
        return this.punishPlayer(sender, UserInfo.of(victim), this.getRankPriority(victim), context);
    }

    private boolean punishPlayer(@NotNull CommandSender sender, @NotNull UserInfo victim, int victimPriority,
                                 @NotNull PunishmentContext context) {
        if (!this.dataLoaded) {
            this.sendPrefixed(BansLang.ERROR_DATA_NOT_LOADED_ADMIN_FEEDBACK, sender);
            return false;
        }

        String playerName = victim.name();
        UUID playerId = victim.id();
        PunishmentType type = context.type();

        if (victim.isUser(sender)) {
            MessageLocale locale = switch (type) {
                case BAN -> BansLang.BAN_ERROR_YOURSELF;
                case MUTE -> BansLang.MUTE_ERROR_YOURSELF;
                case WARN -> BansLang.WARN_ERROR_YOURSELF;
            };

            this.sendPrefixed(locale, sender, builder -> builder.with(BansPlaceholders.PLAYER_NAME, () -> playerName));
            return false;
        }

        if (this.hasImmunity(playerName) || this.hasImmunity(playerId.toString())) {
            this.sendPrefixed(BansLang.IMMUNITY_FEEDBACK, sender, builder -> builder.with(
                BansPlaceholders.GENERIC_TARGET, () -> playerName));
            return false;
        }

        int senderPriority = this.getRankPriority(sender);
        if (victimPriority >= senderPriority) {
            MessageLocale locale = switch (type) {
                case BAN -> BansLang.BAN_ERROR_LOW_PRIORITY;
                case MUTE -> BansLang.MUTE_ERROR_LOW_PRIORITY;
                case WARN -> BansLang.WARN_ERROR_LOW_PRIORITY;
            };

            this.sendPrefixed(locale, sender, builder -> builder.with(BansPlaceholders.PLAYER_NAME, () -> playerName));
            return false;
        }

        PunishmentRepository repository = this.getPunishmentRepository(type);
        Set<PlayerPunishment> activePunishments = repository.getActivePlayerPunishments(playerId);

        BanTime banTime = context.time();
        PunishmentReason reason = context.reason();
        boolean silent = context.silent();

        if (!sender.hasPermission(BansPerms.BYPASS_DURATION_LIMIT)) {
            BanTime maxTime = this.getGreatestDuration(sender, type);
            if (banTime.isGreater(maxTime)) {
                MessageLocale locale = switch (type) {
                    case BAN -> BansLang.BAN_ERROR_HIGH_DURATION;
                    case MUTE -> BansLang.MUTE_ERROR_HIGH_DURATION;
                    case WARN -> BansLang.WARN_ERROR_HIGH_DURATION;
                };

                this.sendPrefixed(locale, sender, builder -> builder.with(SLPlaceholders.GENERIC_TIME, () -> TimeFormats
                    .toLiteral(maxTime.accumulated())));
                return false;
            }

            // Ensure players can't override higher bans/mutes with lower time values.
            if (type != PunishmentType.WARN && activePunishments.stream().anyMatch(punishment -> banTime.isSmaller(
                punishment.getRemainingDuration()))) {
                MessageLocale locale = switch (type) {
                    case BAN -> BansLang.BAN_ERROR_HIGHER_EXISTS;
                    case MUTE -> BansLang.MUTE_ERROR_HIGHER_EXISTS;
                    default -> throw new IllegalStateException("Unexpected value: " + type);
                };

                this.sendPrefixed(locale, sender, builder -> builder.with(SLPlaceholders.GENERIC_TIME, () -> TimeFormats
                    .toLiteral(banTime.accumulated())));
                return false;
            }
        }


        int punishments = activePunishments.size() + 1; // Count also this one.
        int maxWarns = this.settings.warnMaxAmountToReset.get();

        if (type != PunishmentType.WARN || (maxWarns > 0 && punishments >= maxWarns)) {
            activePunishments.forEach(punishment -> {
                punishment.setActive(false);
                punishment.markDirty();
                repository.updatePlayerPunishmentReferences(punishment);
            });
        }

        UUID banId = UUID.randomUUID();
        String who = sender.getName();
        long duration = banTime.accumulated();
        long createDate = System.currentTimeMillis();
        long expireDate = banTime.futureTimestamp();
        String textReason = reason.getText();

        PunishmentData data = new PunishmentData(banId, type, textReason, who, duration, createDate, expireDate);
        PlayerPunishment punishment = new PlayerPunishment(playerId, playerName, data, true);

        MessageLocale feedbackLocale = switch (type) {
            case BAN -> punishment.isPermanent() ? BansLang.BAN_PERMANENT_FEEDBACK : BansLang.BAN_TEMPORARY_FEEDBACK;
            case MUTE -> punishment.isPermanent() ? BansLang.MUTE_PERMANENT_FEEDBACK : BansLang.MUTE_TEMPORARY_FEEDBACK;
            case WARN -> punishment.isPermanent() ? BansLang.WARN_PERMANENT_FEEDBACK : BansLang.WARN_TEMPORARY_FEEDBACK;
        };

        MessageLocale broadcastLocale = switch (type) {
            case BAN -> punishment.isPermanent() ? BansLang.BAN_PERMANENT_BROADCAST : BansLang.BAN_TEMPORARY_BROADCAST;
            case MUTE -> punishment
                .isPermanent() ? BansLang.MUTE_PERMANENT_BROADCAST : BansLang.MUTE_TEMPORARY_BROADCAST;
            case WARN -> punishment
                .isPermanent() ? BansLang.WARN_PERMANENT_BROADCAST : BansLang.WARN_TEMPORARY_BROADCAST;
        };

        MessageLocale notifyLocale = switch (type) {
            case BAN -> null;
            case MUTE -> punishment.isPermanent() ? BansLang.MUTE_PERMANENT_NOTIFY : BansLang.MUTE_TEMPORARY_NOTIFY;
            case WARN -> punishment.isPermanent() ? BansLang.WARN_PERMANENT_NOTIFY : BansLang.WARN_TEMPORARY_NOTIFY;
        };

        this.addPlayerPunishment(punishment);
        this.notifyPunishment(punishment, sender, feedbackLocale, broadcastLocale, notifyLocale, silent);

        if (type == PunishmentType.WARN) {
            List<String> commands = this.settings.warnAutoCommands.get().get(punishments);
            if (commands != null && !commands.isEmpty()) {
                PlaceholderContext commandsContext = PlaceholderContext.builder()
                    .with(punishment.placeholders())
                    .with(SLPlaceholders.PLAYER_NAME, () -> playerName)
                    .build();

                // Apply placeholders per line to prevent custom commands with line breakers be splitted by PlaceholderContext.
                commands.replaceAll(commandsContext::apply);

                // Run next tick to execute out of punishment context.
                CommandSender console = this.plugin.getServer().getConsoleSender();
                this.plugin.runTask(() -> {
                    commands.forEach(command -> this.plugin.getServer().dispatchCommand(console, command));
                });
            }
        }

        return true;
    }

    public boolean banInet(@NotNull CommandSender sender, @NotNull InetAddress address,
                           @NotNull PunishmentReason reason, @NotNull BanTime banTime, boolean silent) {
        if (!this.dataLoaded) {
            this.sendPrefixed(BansLang.ERROR_DATA_NOT_LOADED_ADMIN_FEEDBACK, sender);
            return false;
        }

        if (this.hasImmunity(address)) {
            this.sendPrefixed(BansLang.IMMUNITY_FEEDBACK, sender, builder -> builder.with(
                BansPlaceholders.GENERIC_TARGET, address::getHostAddress));
            return false;
        }

        UUID banId = UUID.randomUUID();
        String admin = sender.getName();
        long duration = banTime.accumulated();
        long createDate = System.currentTimeMillis();
        long expireDate = banTime.futureTimestamp();
        String textReason = reason.getText();

        PunishmentData data = new PunishmentData(banId, PunishmentType.BAN, textReason, admin, duration, createDate, expireDate);
        InetPunishment punishment = new InetPunishment(address, data, true);

        MessageLocale feedbackLocale = punishment
            .isPermanent() ? BansLang.BAN_INET_PERMANENT_FEEDBACK : BansLang.BAN_INET_TEMPORARY_FEEDBACK;
        MessageLocale broadcastLocale = punishment
            .isPermanent() ? BansLang.BAN_INET_PERMANENT_BROADCAST : BansLang.BAN_INET_TEMPORARY_BROADCAST;
        MessageLocale notifyLocale = null;

        this.addInetPunishment(punishment);
        this.notifyPunishment(punishment, sender, feedbackLocale, broadcastLocale, notifyLocale, silent);

        return true;
    }

    private void notifyPunishment(@NotNull AbstractPunishment punishment,
                                  @NotNull CommandSender sender,
                                  @NotNull MessageLocale feedbackLocale,
                                  @NotNull MessageLocale broadcastLocale,
                                  @Nullable MessageLocale notifyLocale,
                                  boolean silent) {
        PlaceholderContext context = PlaceholderContext.builder().with(punishment.placeholders()).build();

        this.sendPrefixed(feedbackLocale, sender, context);

        if (!silent) {
            this.broadcastPrefixed(broadcastLocale, context);
        }

        // Notify players about their punishment.
        Set<Player> targets = this.getPlayersToPunish(punishment);
        if (punishment.getType() == PunishmentType.BAN) {
            targets.forEach(target -> Players.kick(target, this.getKickScreenText(punishment)));
        }
        else {
            if (notifyLocale != null) {
                this.sendPrefixed(notifyLocale, targets, context);
            }
        }
    }

    public boolean pardonPlayer(@NotNull UserInfo user, @NotNull CommandSender sender, @NotNull PunishmentType type,
                                boolean silent) {
        if (!this.dataLoaded) {
            this.sendPrefixed(BansLang.ERROR_DATA_NOT_LOADED_ADMIN_FEEDBACK, sender);
            return false;
        }

        String playerName = user.name();
        UUID playerId = user.id();

        Set<PlayerPunishment> activePunishments = this.getActivePlayerPunishments(playerId, type);
        if (activePunishments.isEmpty()) {
            MessageLocale locale = switch (type) {
                case BAN -> BansLang.UNBAN_PLAYER_ERROR_NOT_BANNED;
                case MUTE -> BansLang.UNMUTE_ERROR_NOT_MUTED;
                case WARN -> BansLang.UNWARN_ERROR_NOT_WARNED;
            };

            this.sendPrefixed(locale, sender, builder -> builder.with(BansPlaceholders.PUNISHMENT_TARGET,
                () -> playerName));
            return false;
        }

        if (this.settings.durationLimitsForPardon.get() && !sender.hasPermission(BansPerms.BYPASS_DURATION_LIMIT)) {
            BanTime maxTime = this.getGreatestDuration(sender, type);

            if (activePunishments.stream().anyMatch(punishment -> maxTime.isSmaller(punishment
                .getRemainingDuration()))) {
                MessageLocale locale = switch (type) {
                    case BAN -> BansLang.UNBAN_ERROR_HIGH_DURATION;
                    case MUTE -> BansLang.UNMUTE_ERROR_HIGH_DURATION;
                    case WARN -> BansLang.UNWARN_ERROR_HIGH_DURATION;
                };

                this.sendPrefixed(locale, sender, builder -> builder.with(SLPlaceholders.GENERIC_TIME, () -> TimeFormats
                    .toLiteral(maxTime.accumulated())));
                return false;
            }
        }

        MessageLocale feedbackLocale = switch (type) {
            case BAN -> BansLang.UNBAN_PLAYER_FEEDBACK;
            case MUTE -> BansLang.UNMUTE_SUCCESS_FEEDBACK;
            case WARN -> BansLang.UNWARN_SUCCESS_FEEDBACK;
        };

        MessageLocale broadcastLocale = switch (type) {
            case BAN -> BansLang.UNBAN_PLAYER_BROADCAST;
            case MUTE -> BansLang.UNMUTE_SUCCESS_BROADCAST;
            case WARN -> BansLang.UNWARN_SUCCESS_BROADCAST;
        };

        MessageLocale notifyLocale = switch (type) {
            case BAN -> null;
            case MUTE -> BansLang.UNMUTE_SUCCESS_NOTIFY;
            case WARN -> BansLang.UNWARN_SUCCESS_NOTIFY;
        };

        activePunishments.forEach(punishment -> {
            punishment.setActive(false);
            punishment.markDirty();
            this.getPunishmentRepository(type).updatePlayerPunishmentReferences(punishment);
        });

        this.notifyPardon(activePunishments, sender, feedbackLocale, broadcastLocale, notifyLocale, silent);
        return true;
    }

    public boolean pardonInet(@NotNull InetAddress address, @NotNull CommandSender sender, boolean silent) {
        if (!this.dataLoaded) {
            this.sendPrefixed(BansLang.ERROR_DATA_NOT_LOADED_ADMIN_FEEDBACK, sender);
            return false;
        }

        Set<InetPunishment> activePunishments = this.getActiveInetPunishments(address);
        if (activePunishments.isEmpty()) {
            this.sendPrefixed(BansLang.UNBAN_INET_ERROR_NOT_BANNED, sender, builder -> builder
                .with(BansPlaceholders.GENERIC_TARGET, address::getHostAddress)
                .with(BansPlaceholders.PUNISHMENT_TARGET, address::getHostAddress) // old
            );
            return false;
        }

        MessageLocale feedbackLocale = BansLang.UNBAN_INET_FEEDBACK;
        MessageLocale broadcastLocale = BansLang.UNBAN_INET_BROADCAST;

        activePunishments.forEach(punishment -> {
            punishment.setActive(false);
            punishment.markDirty();
            this.getPunishmentRepository(punishment.getType()).updateInetPunishmentReferences(punishment);
        });

        this.notifyPardon(activePunishments, sender, feedbackLocale, broadcastLocale, null, silent);
        return true;
    }

    private void notifyPardon(@NotNull Collection<? extends AbstractPunishment> punishments,
                              @NotNull CommandSender sender,
                              @NotNull MessageLocale feedbackLocale,
                              @NotNull MessageLocale broadcastLocale,
                              @Nullable MessageLocale notifyLocale,
                              boolean silent) {

        punishments.forEach(punishment -> {
            PlaceholderContext context = PlaceholderContext.builder()
                .with(punishment.placeholders())
                .with(BansPlaceholders.GENERIC_EXECUTOR, () -> SLUtils.getSenderName(sender))
                .build();

            this.sendPrefixed(feedbackLocale, sender, context);

            if (notifyLocale != null) {
                this.getPlayersToPunish(punishment).forEach(player -> this.sendPrefixed(notifyLocale, player, context));
            }
            if (!silent) {
                this.broadcastPrefixed(broadcastLocale, context);
            }
        });
    }

    @NotNull
    private String getKickScreenText(@NotNull AbstractPunishment punishment) {
        List<String> text;
        if (punishment instanceof PlayerPunishment) {
            text = punishment.isPermanent() ? this.settings.disconnectScreenPermaBan
                .get() : this.settings.disconnectScreenTempBan.get();
        }
        else {
            text = this.settings.disconnectScreenIpBan.get();
        }

        PlaceholderContext context = PlaceholderContext.builder().with(punishment.placeholders()).build();
        return context.apply(String.join("\n", text));
    }

    @NotNull
    public CompletableFuture<List<UserInfo>> lookupAltProfiles(@NotNull InetAddress address) {
        return CompletableFuture.supplyAsync(() -> this.dataHandler.getProfilesByInet(address));
    }

    public void notifyAltProfiles(@NotNull CommandSender sender, @NotNull UserInfo profile,
                                  @NotNull InetAddress address, @NotNull List<UserInfo> alts) {
        alts.removeIf(alt -> alt.id().equals(profile.id()));

        if (alts.isEmpty()) {
            this.sendPrefixed(BansLang.ALTS_GLOBAL_NOTHING, sender, builder -> builder.with(
                CommonPlaceholders.PLAYER_NAME, profile::name));
            return;
        }

        PlaceholderContext context = PlaceholderContext.builder()
            .with(CommonPlaceholders.PLAYER_NAME, profile::name)
            .with(SLPlaceholders.GENERIC_AMOUNT, () -> String.valueOf(alts.size()))
            .with(SLPlaceholders.GENERIC_ADDRESS, address::getHostAddress)
            .with(SLPlaceholders.GENERIC_ENTRY, () -> alts.stream()
                .map(alt -> PlaceholderContext.builder().with(SLPlaceholders.GENERIC_NAME, alt::name).build().apply(
                    BansLang.ALTS_GLOBAL_ENTRY.text()))
                .collect(Collectors.joining("\n"))
            )
            .build();

        this.sendPrefixed(BansLang.ALTS_GLOBAL_LIST, sender, context);
    }

    @Nullable
    public BanTimeUnit getTimeUnitByAlias(@NotNull String alias) {
        return this.timeUnitAliasMap.get(LowerCase.INTERNAL.apply(alias));
    }

    @NotNull
    public List<String> getTimeUnitAliases() {
        return List.copyOf(this.timeUnitAliasMap.keySet());
    }
}
