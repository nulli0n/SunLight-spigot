package su.nightexpress.sunlight.module.kits.menu;

import static su.nightexpress.nightcore.util.Placeholders.EMPTY_IF_ABOVE;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.BOLD;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.GREEN;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.ORANGE;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.RED;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.UNDERLINED;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.WHITE;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.YELLOW;
import static su.nightexpress.sunlight.SLPlaceholders.GENERIC_STATUS;
import static su.nightexpress.sunlight.module.kits.KitsPlaceholders.KIT_COOLDOWN;
import static su.nightexpress.sunlight.module.kits.KitsPlaceholders.KIT_COST;
import static su.nightexpress.sunlight.module.kits.KitsPlaceholders.KIT_DESCRIPTION;
import static su.nightexpress.sunlight.module.kits.KitsPlaceholders.KIT_NAME;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jspecify.annotations.NonNull;

import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.configuration.ConfigProperty;
import su.nightexpress.nightcore.configuration.ConfigTypes;
import su.nightexpress.nightcore.integration.currency.EconomyBridge;
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.EnumLocale;
import su.nightexpress.nightcore.locale.entry.IconLocale;
import su.nightexpress.nightcore.ui.inventory.item.ItemPopulator;
import su.nightexpress.nightcore.ui.inventory.item.MenuItem;
import su.nightexpress.nightcore.ui.inventory.menu.AbstractMenu;
import su.nightexpress.nightcore.ui.inventory.viewer.ViewerContext;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.kits.KitFiles;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.config.KitsPerms;
import su.nightexpress.sunlight.module.kits.data.KitData;
import su.nightexpress.sunlight.module.kits.model.Kit;

public class KitsMenu extends AbstractMenu {

    private static final EnumLocale<KitStatus> KIT_STATUS_LOCALE = LangEntry.builder("Kits.UI.KitBrowser.KitStatus")
        .enumeration(KitStatus.class, KitStatus::getDefaultText);

    private static final IconLocale KIT_ICON = LangEntry.iconBuilder("Kits.UI.KitBrowser.KitIcon")
        .accentColor(WHITE)
        .rawName(YELLOW.and(BOLD).wrap("Kit: ") + WHITE.wrap(KIT_NAME))
        .rawLore(KIT_DESCRIPTION)
        .rawLore(EMPTY_IF_ABOVE)
        .appendCurrent("Cost", KIT_COST)
        .appendCurrent("Cooldown", KIT_COOLDOWN).br()
        .rawLore(GENERIC_STATUS)
        .build();

    enum KitStatus {

        NO_PERMISSION(RED.wrap("✘ " + UNDERLINED.wrap("You aren't allowed to use this kit."))),
        TOO_EXPENSIVE(RED.wrap("✘ " + UNDERLINED.wrap("You can't afford this kit."))),
        ON_COOLDOWN(ORANGE.wrap("⌛ " + UNDERLINED.wrap("The kit is on cooldown."))),
        AVAILABLE(GREEN.wrap("→ " + UNDERLINED.wrap("Click to get this kit!")));

        private final String defaultText;

        KitStatus(@NonNull String defaultText) {
            this.defaultText = defaultText;
        }

        @NonNull
        public String getDefaultText() {
            return this.defaultText;
        }
    }

    private final KitsModule module;

    private ItemPopulator<Kit> kitPopulator;

    public KitsMenu(@NonNull SunLightPlugin plugin, @NonNull KitsModule module) {
        super(MenuType.GENERIC_9X4, "Kits");
        this.module = module;

        plugin.injectLang(this);
        this.load(plugin, FileConfig.load(module.getLocalUIPath(), KitFiles.FILE_UI_KIT_LIST));
    }

    @Override
    public void registerActions() {

    }

    @Override
    public void registerConditions() {

    }

    @Override
    public void defineDefaultLayout() {
        this.addBackgroundItem(Material.GRAY_STAINED_GLASS_PANE, IntStream.range(0, 27).toArray());
        this.addBackgroundItem(Material.BLACK_STAINED_GLASS_PANE, IntStream.range(27, 36).toArray());

        this.addNextPageItem(Material.ARROW, 35);
        this.addPreviousPageItem(Material.ARROW, 27);
    }

    @Override
    protected void onLoad(@NonNull FileConfig config) {
        int[] kitSlots = ConfigProperty.of(ConfigTypes.INT_ARRAY, "Kit.Slots", new int[]{10, 11, 12, 13, 14, 15, 16})
            .resolveWithDefaults(config);

        this.kitPopulator = ItemPopulator.builder(Kit.class)
            .actionProvider(kit -> context -> {
                Player player = context.getPlayer();

                if (context.getEvent().isRightClick()) {
                    this.module.previewKit(player, kit);
                    return;
                }

                KitStatus status = this.getKitStatus(player, kit);
                if (status != KitStatus.AVAILABLE)
                    return;

                context.getViewer().closeMenu();
                this.module.giveKit(kit, player, false, false);
            })
            .itemProvider((context, kit) -> {
                return kit.definition().getIcon()
                    .hideAllComponents()
                    .localized(KIT_ICON)
                    .replace(builder -> builder
                        .with(kit.placeholders())
                        .with(GENERIC_STATUS, () -> KIT_STATUS_LOCALE.getLocalized(this.getKitStatus(context
                            .getPlayer(), kit))));
            })
            .slots(kitSlots)
            .build();
    }

    @Override
    protected void onClick(@NonNull ViewerContext context, @NonNull InventoryClickEvent event) {

    }

    @Override
    protected void onDrag(@NonNull ViewerContext context, @NonNull InventoryDragEvent event) {

    }

    @Override
    protected void onClose(@NonNull ViewerContext context, @NonNull InventoryCloseEvent event) {

    }

    @Override
    public void onPrepare(@NonNull ViewerContext context, @NonNull InventoryView view, @NonNull Inventory inventory, @NonNull List<MenuItem> items) {
        Collection<Kit> availableKits;
        if (this.module.getSettings().isHideNoPermKits()) {
            availableKits = this.module.getKits(context.getPlayer());
        }
        else {
            availableKits = this.module.getKits();
        }

        List<Kit> kits = availableKits.stream().sorted(Comparator.comparingInt(kit -> kit.definition().getPriority()))
            .toList();

        this.kitPopulator.populateTo(context, kits, items);
    }

    @Override
    public void onReady(@NonNull ViewerContext context, @NonNull InventoryView view, @NonNull Inventory inventory) {

    }

    @Override
    public void onRender(@NonNull ViewerContext context, @NonNull InventoryView view, @NonNull Inventory inventory) {

    }

    @NonNull
    private KitStatus getKitStatus(@NonNull Player player, @NonNull Kit kit) {
        if (!kit.hasPermission(player))
            return KitStatus.NO_PERMISSION;

        if (kit.hasCost() && !player.hasPermission(KitsPerms.BYPASS_COST)) {
            double cost = kit.definition().getCost();
            double balance = EconomyBridge.api().queryBalance(player);
            if (balance < cost)
                return KitStatus.TOO_EXPENSIVE;
        }

        if (kit.hasCooldown() && !player.hasPermission(KitsPerms.BYPASS_COOLDOWN)) {
            KitData data = this.module.getKitData(player.getUniqueId(), kit.getId());
            if (data != null && !data.isCooldownExpired())
                return KitStatus.ON_COOLDOWN;
        }

        return KitStatus.AVAILABLE;
    }
}
