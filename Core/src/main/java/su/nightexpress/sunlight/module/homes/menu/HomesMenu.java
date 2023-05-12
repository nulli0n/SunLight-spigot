package su.nightexpress.sunlight.module.homes.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.api.menu.click.ClickHandler;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.ConfigMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.impl.Home;
import su.nightexpress.sunlight.module.homes.util.Placeholders;

import javax.annotation.Nullable;
import java.util.*;

public class HomesMenu extends ConfigMenu<SunLight> implements AutoPaged<Home> {

    private static final String PLACEHOLDER_RESPAWN    = "%respawn%";
    private static final String PLACEHOLDER_IS_DEFAULT = "%is_default%";

    private final HomesModule       module;
    private final String            homeName;
    private final List<String>      homeLoreDefault;
    private final List<String>      homeLoreRespawn;
    private final List<String>      homeLoreIsDefault;
    private final int[]             homeSlots;
    private final Map<Player, UUID> others;

    public HomesMenu(@NotNull HomesModule module) {
        super(module.plugin(), JYML.loadOrExtract(module.plugin(), module.getLocalPath() + "/menu/home_list.yml"));
        this.module = module;
        this.others = new WeakHashMap<>();

        this.homeName = Colorizer.apply(cfg.getString("Home.Name", Placeholders.HOME_NAME));
        this.homeLoreDefault = Colorizer.apply(cfg.getStringList("Home.Lore.Default"));
        this.homeLoreRespawn = Colorizer.apply(cfg.getStringList("Home.Lore.Respawn"));
        this.homeLoreIsDefault = Colorizer.apply(cfg.getStringList("Home.Lore.Is_Default"));
        this.homeSlots = cfg.getIntArray("Home.Slots");

        this.registerHandler(MenuItemType.class)
            .addClick(MenuItemType.CLOSE, (viewer, event) -> plugin.runTask(task -> viewer.getPlayer().closeInventory()))
            .addClick(MenuItemType.PAGE_PREVIOUS, ClickHandler.forPreviousPage(this))
            .addClick(MenuItemType.PAGE_NEXT, ClickHandler.forNextPage(this));

        this.load();
    }

    public void open(@NotNull Player player, @NotNull UUID userId) {
        this.others.put(player, userId);
        this.open(player, 1);
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        this.getItemsForPage(viewer).forEach(this::addItem);
    }

    @Nullable
    public UUID getOtherHolder(@NotNull Player player) {
        return this.others.get(player);
    }

    public void clearHolder(@NotNull UUID userId) {
        this.others.forEach((player , id) -> {
            if (id.equals(userId)) {
                this.plugin.runTask(task -> player.closeInventory());
            }
        });
    }

    @Override
    public void onClose(@NotNull MenuViewer viewer, @NotNull InventoryCloseEvent event) {
        super.onClose(viewer, event);
        this.others.remove(viewer.getPlayer());
    }

    @Override
    public int[] getObjectSlots() {
        return homeSlots;
    }

    @Override
    @NotNull
    public Comparator<Home> getObjectSorter() {
        return (o1, o2) -> 0;
    }

    @Override
    @NotNull
    public List<Home> getObjects(@NotNull Player player) {
        UUID userId = this.others.getOrDefault(player, player.getUniqueId());
        return this.module.getHomes(userId).values().stream().sorted(Comparator.comparing(Home::getId)).toList();
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull Home home) {
        ItemStack item = new ItemStack(home.getIcon());
        ItemUtil.mapMeta(item, meta -> {
            List<String> lore = this.homeLoreDefault;
            lore = StringUtil.replace(lore, PLACEHOLDER_RESPAWN, false, home.isRespawnPoint() ? this.homeLoreRespawn : Collections.emptyList());
            lore = StringUtil.replace(lore, PLACEHOLDER_IS_DEFAULT, false, home.isDefault() ? this.homeLoreIsDefault : Collections.emptyList());

            meta.setDisplayName(this.homeName);
            meta.setLore(lore);
            ItemUtil.replace(meta, home.replacePlaceholders());
        });
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull Home home) {
        return (viewer, event) -> {
            Player player = viewer.getPlayer();

            UUID userId = this.others.getOrDefault(player, player.getUniqueId());
            if (!this.module.isLoaded(userId)) {
                this.plugin.runTask(task -> player.closeInventory());
                return;
            }

            if (event.isRightClick()) {
                if (event.isShiftClick()) {
                    this.module.removeHome(home);
                    this.open(player, viewer.getPage());
                    return;
                }
                home.getEditor().open(player, 1);
            }
            else {
                home.teleport(player);
            }
        };
    }
}
