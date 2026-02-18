package su.nightexpress.sunlight.module.kits;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.integration.currency.EconomyBridge;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.nightcore.util.time.TimeFormatType;
import su.nightexpress.nightcore.util.time.TimeFormats;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.hook.placeholder.PlaceholderRegistry;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleContext;
import su.nightexpress.sunlight.module.kits.command.KitsCommandProvider;
import su.nightexpress.sunlight.module.kits.config.KitsLang;
import su.nightexpress.sunlight.module.kits.config.KitsPerms;
import su.nightexpress.sunlight.module.kits.config.KitsSettings;
import su.nightexpress.sunlight.module.kits.data.KitData;
import su.nightexpress.sunlight.module.kits.data.KitDataManager;
import su.nightexpress.sunlight.module.kits.data.KitDataRepository;
import su.nightexpress.sunlight.module.kits.dialog.KitDialogKeys;
import su.nightexpress.sunlight.module.kits.dialog.impl.*;
import su.nightexpress.sunlight.module.kits.editor.KitContentEditorMenu;
import su.nightexpress.sunlight.module.kits.editor.KitSettingsEditorMenu;
import su.nightexpress.sunlight.module.kits.editor.KitsEditorMenu;
import su.nightexpress.sunlight.module.kits.listener.KitBindListener;
import su.nightexpress.sunlight.module.kits.menu.KitPreviewMenu;
import su.nightexpress.sunlight.module.kits.menu.KitsMenu;
import su.nightexpress.sunlight.module.kits.model.Kit;
import su.nightexpress.sunlight.module.kits.model.KitDefinition;
import su.nightexpress.sunlight.utils.FutureUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class KitsModule extends Module {

    private final KitsSettings      settings;
    private final KitDataManager    dataManager;
    private final KitDataRepository dataRepository;
    private final Map<String, Kit>  kitByIdMap;

    private KitsMenu       kitsMenu;
    private KitPreviewMenu previewMenu;

    private KitsEditorMenu        editorMenu;
    private KitSettingsEditorMenu settingsEditorMenu;
    private KitContentEditorMenu  contentEditorMenu;

    private boolean dataLoaded;

    public KitsModule(@NotNull ModuleContext context) {
        super(context);
        this.settings = new KitsSettings();
        this.dataManager = new KitDataManager(this.dataHandler);
        this.dataRepository = new KitDataRepository();
        this.kitByIdMap = new HashMap<>();
    }

    @Override
    protected void loadModule(@NotNull FileConfig config) {
        this.settings.load(config);
        this.plugin.injectLang(KitsLang.class);

        KitsUtils.loadKeys(this.plugin);

        this.dataManager.init();

        this.loadData();
        this.loadDialogs();
        this.loadUI();
        this.loadKits();

        if (this.settings.isBindToPlayers()) {
            this.addListener(new KitBindListener(this.plugin, this));
        }

        this.addAsyncTask(this::saveData, this.settings.getDataSaveInterval());
        this.addAsyncTask(this::saveKits, this.settings.getKitSaveInterval());
    }

    @Override
    protected void unloadModule() {
        this.dataLoaded = false;

        this.saveData();
        this.saveKits();

        this.kitByIdMap.clear();
        this.dataRepository.clear();
    }

    @Override
    protected void registerPermissions(@NotNull PermissionTree root) {
        root.merge(KitsPerms.ROOT);
    }

    @Override
    protected void registerCommands() {
        this.commandRegistry.addProvider("kits-commons", new KitsCommandProvider(this.plugin, this, this.userManager));
    }

    @Override
    public void registerPlaceholders(@NotNull PlaceholderRegistry registry) {
        registry.register("kits_is_on_cooldown", (player, payload) -> {
            return CoreLang.STATE_YES_NO.get(this.dataRepository.kitData(player.getUniqueId(), payload).map(KitData::hasCooldown).orElse(false));
        });

        registry.register("kits_is_available", (player, payload) -> {
            return CoreLang.STATE_YES_NO.get(this.dataRepository.kitData(player.getUniqueId(), payload).map(KitData::hasCooldown).orElse(false));
        });

        registry.register("kits_cooldown_raw", (player, payload) -> {
            return String.valueOf(this.dataRepository.kitData(player.getUniqueId(), payload).map(KitData::getCooldownDate).orElse(0L));
        });

        registry.register("kits_cooldown", (player, payload) -> {
            return TimeFormats.formatDuration(this.dataRepository.kitData(player.getUniqueId(), payload).map(KitData::getCooldownDate).orElse(0L), TimeFormatType.LITERAL);
        });
    }

    private void loadData() {
        this.plugin.runTaskAsync(() -> {
            this.dataManager.loadData().forEach(this.dataRepository::add);
            this.dataLoaded = true;
        });
    }

    private void loadDialogs() {
        // TODO Better texts
        this.dialogRegistry.register(KitDialogKeys.KIT_CREATION, KitCreationDialog::new);
        this.dialogRegistry.register(KitDialogKeys.KIT_NAME, KitNameDialog::new);
        this.dialogRegistry.register(KitDialogKeys.KIT_DESCRIPTION, KitDescriptionDialog::new);
        this.dialogRegistry.register(KitDialogKeys.KIT_PRIORITY, KitPriorityDialog::new);
        this.dialogRegistry.register(KitDialogKeys.KIT_COST, KitCostDialog::new);
        this.dialogRegistry.register(KitDialogKeys.KIT_COOLDOWN, KitCooldownDialog::new);
        this.dialogRegistry.register(KitDialogKeys.KIT_COMMANDS, KitCommandsDialog::new);
    }

    private void loadUI() {
        this.kitsMenu = new KitsMenu(this.plugin, this);
        this.previewMenu = new KitPreviewMenu(this.plugin, this);

        this.editorMenu = new KitsEditorMenu(this.plugin, this, this.dialogRegistry);
        this.settingsEditorMenu = new KitSettingsEditorMenu(this.plugin, this, this.dialogRegistry);
        this.contentEditorMenu = new KitContentEditorMenu(this.plugin, this);
    }

    private void loadKits() {
        FileUtil.findYamlFiles(this.getSystemPath() + KitFiles.DIR_KITS).forEach(file -> {
            Kit kit = Kit.fromFile(file);
            this.addKit(kit);
        });

        this.info("Loaded " + this.kitByIdMap.size() + " kits.");
    }

    private void saveData() {
        Set<KitData> dirties = this.dataRepository.getAll().stream().filter(KitData::isDirty).peek(KitData::markClean).collect(Collectors.toSet());

        this.dataManager.saveData(dirties);
    }

    private void saveKits() {
        this.getKits().stream().filter(Kit::isDirty).peek(Kit::markClean).forEach(kit -> {
            FileConfig config = FileConfig.load(kit.getPath());
            config.edit(kit::write);
        });
    }

    @NotNull
    public KitsSettings getSettings() {
        return this.settings;
    }

    @NotNull
    public KitDataManager getDataManager() {
        return this.dataManager;
    }

    @NotNull
    public KitDataRepository getDataRepository() {
        return this.dataRepository;
    }

    @Nullable
    public KitData getKitData(@NotNull UUID playerId, @NotNull String kitId) {
        return this.dataRepository.getKitData(playerId, kitId);
    }

    @NotNull
    public CompletableFuture<KitData> getKitDataOrCreate(@NotNull UUID playerId, @NotNull String kitId) {
        KitData data = this.getKitData(playerId, kitId);
        if (data != null) return CompletableFuture.completedFuture(data);

        return CompletableFuture.supplyAsync(() -> {
            KitData newData = KitData.create(playerId, kitId);
            this.dataRepository.add(newData);
            this.dataManager.addData(newData);
            return newData;
        }).whenComplete(FutureUtils::printStacktrace);
    }

    private void addKit(@NotNull Kit kit) {
        this.kitByIdMap.put(kit.getId(), kit);
    }

    public void createKit(@NotNull String name) throws IllegalArgumentException {
        String id = Strings.varStyle(name).orElseThrow(() -> new IllegalArgumentException("%s is not a valid name".formatted(name)));

        if (this.isKitExists(id)) throw new IllegalArgumentException("Kit %s already exists".formatted(name));

        Path file = Path.of(this.getSystemPath() + KitFiles.DIR_KITS, FileConfig.withExtension(id));
        FileConfig config = FileConfig.load(file);

        KitDefinition definition = KitDefinition.createDefault(StringUtil.capitalizeUnderscored(id));
        Kit kit = new Kit(file, id, definition);

        kit.write(config);
        this.addKit(kit);
    }

    public boolean giveKit(@NotNull Kit kit, @NotNull Player player, boolean force, boolean silent) {
        if (!this.dataLoaded) {
            this.sendPrefixed(KitsLang.DATA_ERROR_NOT_LOADED, player);
            return false;
        }

        // Check kit permission.
        if (!force && !kit.hasPermission(player)) {
            if (!silent) this.sendPrefixed(KitsLang.KIT_GET_ERROR_NO_PERMISSION, player, builder -> builder.with(kit.placeholders()));
            return false;
        }

        // Check kit cooldown.
        this.getKitDataOrCreate(player.getUniqueId(), kit.getId()).thenAcceptAsync(kitData -> {
            if (!force && !kitData.isCooldownExpired()) {
                if (!silent) {
                    this.sendPrefixed(!kitData.isCooldownExpirable() ? KitsLang.KIT_GET_ERROR_ONE_TIME : KitsLang.KIT_GET_ERROR_COOLDOWN, player, builder -> builder
                        .with(kit.placeholders())
                        .with(SLPlaceholders.GENERIC_COOLDOWN, () -> TimeFormats.formatDuration(kitData.getCooldownDate(), TimeFormatType.LITERAL))
                    );
                }
                return;
            }

            // Check kit money cost.
            if (!force && kit.hasCost() && !player.hasPermission(KitsPerms.BYPASS_COST) && EconomyBridge.hasEconomy()) {
                double cost = kit.definition().getCost();
                double balance = EconomyBridge.getEconomyBalance(player);
                if (balance < cost) {
                    if (!silent) this.sendPrefixed(KitsLang.KIT_GET_ERROR_NOT_ENOUGH_FUNDS, player, builder -> builder.with(kit.placeholders()));
                    return;
                }
                EconomyBridge.withdrawEconomy(player, cost);
            }

            // Give kit content.
            PlayerInventory inventory = player.getInventory();

            kit.definition().getContent().give((slot, itemStack) -> {
                if (this.settings.isBindToPlayers()) {
                    KitsUtils.setItemOwner(itemStack, player.getUniqueId());
                }

                ItemStack current = inventory.getItem(slot);
                if (current != null && !current.getType().isAir()) {
                    Players.addItem(player, new ItemStack(current));
                }
                inventory.setItem(slot, itemStack);
            });

            Players.dispatchCommands(player, kit.definition().getCommands());

            if (!force && kit.hasCooldown() && !player.hasPermission(KitsPerms.BYPASS_COOLDOWN)) {
                kitData.setCooldownDate(TimeUtil.createFutureTimestamp(kit.definition().getCooldown()));
                kitData.markDirty();
            }

            if (!silent) this.sendPrefixed(KitsLang.KIT_GET_NOTIFY, player, builder -> builder.with(kit.placeholders()));

        }, this.plugin::runTask).whenComplete(FutureUtils::printStacktrace);

        return true;
    }

    public void deleteKit(@NotNull Kit kit) {
        try {
            Files.delete(kit.getPath());
            this.plugin.runTaskAsync(() -> this.dataManager.deleteData(kit.getId()));
            this.kitByIdMap.remove(kit.getId());
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void openKitsMenu(@NotNull Player player) {
        this.kitsMenu.show(this.plugin, player);
    }

    public void openEditor(@NotNull Player player) {
        this.editorMenu.show(this.plugin, player);
    }

    public void openSettingsEditor(@NotNull Player player, @NotNull Kit kit) {
        this.settingsEditorMenu.show(this.plugin, player, kit);
    }

    public boolean openContentEditor(@NotNull Player player, @NotNull Kit kit) {
        return this.contentEditorMenu.show(this.plugin, player, kit);
    }

    public void previewKit(@NotNull Player player, @NotNull Kit kit) {
        this.previewMenu.show(this.plugin, player, kit);
    }

    public boolean isKitExists(@NotNull String id) {
        return this.getKitById(LowerCase.INTERNAL.apply(id)) != null;
    }

    @Nullable
    public Kit getKitById(@NotNull String id) {
        return this.kitByIdMap.get(id.toLowerCase());
    }

    @NotNull
    public Optional<Kit> kitById(@NotNull String id) {
        return Optional.ofNullable(this.getKitById(id));
    }

    @NotNull
    public Map<String, Kit> getKitByIdMap() {
        return Map.copyOf(this.kitByIdMap);
    }

    @NotNull
    public Set<Kit> getKits() {
        return Set.copyOf(this.kitByIdMap.values());
    }

    @NotNull
    public List<Kit> getKits(@NotNull Player player) {
        return this.kitByIdMap.values().stream().filter(kit -> kit.hasPermission(player)).toList();
    }

    @NotNull
    public List<String> getKitIds() {
        return new ArrayList<>(this.kitByIdMap.keySet());
    }

    @NotNull
    public List<String> getKitIds(@NotNull Player player) {
        return this.getKits(player).stream().map(Kit::getId).toList();
    }
}
