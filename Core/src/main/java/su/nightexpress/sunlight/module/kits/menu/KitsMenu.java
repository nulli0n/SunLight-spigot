package su.nightexpress.sunlight.module.kits.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuSize;
import su.nightexpress.nightcore.menu.MenuViewer;
import su.nightexpress.nightcore.menu.api.AutoFill;
import su.nightexpress.nightcore.menu.api.AutoFilled;
import su.nightexpress.nightcore.menu.impl.ConfigMenu;
import su.nightexpress.nightcore.menu.item.ItemHandler;
import su.nightexpress.nightcore.menu.item.MenuItem;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.module.kits.Kit;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.config.KitsConfig;
import su.nightexpress.sunlight.module.kits.util.Placeholders;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static su.nightexpress.sunlight.module.kits.util.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class KitsMenu extends ConfigMenu<SunLightPlugin> implements AutoFilled<Kit> {

    private static final String FILE_NAME = "kit_list.yml";

    private static final String NO_MONEY       = "%no_money%";
    private static final String NO_PERMISSION  = "%no_permission%";
    private static final String COOLDOWN       = "%cooldown%";
    private static final String INFO_COST      = "%info_cost%";
    private static final String INFO_COOLDOWN  = "%info_cooldown%";
    private static final String ACTION_GET     = "%action_get%";
    private static final String ACTION_PREVIEW = "%action_preview%";

    private final KitsModule module;

    private String       kitName;
    private List<String> kitLore;
    private int[]        kitSlots;

    private List<String> loreNoPerm;
    private List<String> loreNoMoney;
    private List<String> loreCooldown;
    private List<String> loreInfoCost;
    private List<String> loreInfoCooldown;
    private List<String> loreGet;
    private List<String> lorePreview;

    public KitsMenu(@NotNull SunLightPlugin plugin, @NotNull KitsModule module) {
        super(plugin, FileConfig.loadOrExtract(plugin, module.getLocalUIPath(), FILE_NAME));
        this.module = module;

        this.load();
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    public void onAutoFill(@NotNull MenuViewer viewer, @NotNull AutoFill<Kit> autoFill) {
        Player player = viewer.getPlayer();

        autoFill.setSlots(this.kitSlots);
        autoFill.setItems(this.module.getKits().stream()
            .filter(kit -> !KitsConfig.GUI_HIDE_NO_PERMISSION.get() || kit.hasPermission(player))
            .sorted(Comparator.comparingInt(Kit::getPriority).reversed())
            .toList()
        );
        autoFill.setItemCreator(kit -> {
            ItemStack item = kit.getIcon();
            PlaceholderMap placeholders = new PlaceholderMap();

            List<String> noPermLore = new ArrayList<>();
            List<String> noMoneyLore = new ArrayList<>();
            List<String> cooldownLore = new ArrayList<>();
            List<String> infoCostLore = new ArrayList<>();
            List<String> infoCooldownLore = new ArrayList<>();
            List<String> getLore = new ArrayList<>();
            List<String> previewLore = new ArrayList<>();

            if (kit.hasCooldown()) {
                infoCooldownLore.addAll(this.loreInfoCooldown);
            }
            if (kit.hasCost()) {
                infoCostLore.addAll(this.loreInfoCost);
            }

            if (!kit.hasPermission(player)) {
                noPermLore.addAll(this.loreNoPerm);
            }
            else {
                SunUser user = plugin.getUserManager().getUserData(player);
                user.getCooldown(kit).ifPresent(info -> {
                    cooldownLore.addAll(this.loreCooldown);
                    placeholders.add(Placeholders.GENERIC_COOLDOWN, info.isPermanent() ? Lang.OTHER_NEVER.getString() : TimeUtil.formatDuration(info.getExpireDate()));
                });

                if (!kit.canAfford(player)) {
                    noMoneyLore.addAll(this.loreNoMoney);
                }

                if (noMoneyLore.isEmpty() && cooldownLore.isEmpty()) {
                    getLore.addAll(this.loreGet);
                    previewLore.addAll(this.lorePreview);
                }
            }

            ItemReplacer.create(item).hideFlags().trimmed()
                .setDisplayName(this.kitName)
                .setLore(this.kitLore)
                .replace(INFO_COOLDOWN, infoCooldownLore)
                .replace(INFO_COST, infoCostLore)
                .replace(NO_PERMISSION, noPermLore)
                .replace(NO_MONEY, noMoneyLore)
                .replace(COOLDOWN, cooldownLore)
                .replace(ACTION_GET, getLore)
                .replace(ACTION_PREVIEW, previewLore)
                .replace(placeholders)
                .replace(kit.getPlaceholders())
                .writeMeta();

            return item;
        });
        autoFill.setClickAction(kit -> (viewer1, event) -> {
            if (event.isLeftClick()) {
                kit.give(player, false, false);
                this.runNextTick(player::closeInventory);
            }
            else if (event.isRightClick()) {
                this.runNextTick(() -> this.module.openKitPreview(player, kit));
            }
        });
    }

    @Override
    @NotNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions(BLACK.enclose("Kits"), MenuSize.CHEST_27);
    }

    @Override
    @NotNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack nextPage = ItemUtil.getSkinHead(SKIN_ARROW_RIGHT);
        ItemUtil.editMeta(nextPage, meta -> {
            meta.setDisplayName(Lang.EDITOR_ITEM_NEXT_PAGE.getDefaultName());
        });
        list.add(new MenuItem(nextPage).setPriority(10).setSlots(26).setHandler(ItemHandler.forNextPage(this)));

        ItemStack backPage = ItemUtil.getSkinHead(SKIN_ARROW_LEFT);
        ItemUtil.editMeta(backPage, meta -> {
            meta.setDisplayName(Lang.EDITOR_ITEM_PREVIOUS_PAGE.getDefaultName());
        });
        list.add(new MenuItem(backPage).setPriority(10).setSlots(18).setHandler(ItemHandler.forPreviousPage(this)));

        return list;
    }

    @Override
    protected void loadAdditional() {
        this.kitName = ConfigValue.create("Kit.Name",
            LIGHT_YELLOW.enclose(BOLD.enclose("Kit: ")) + WHITE.enclose(KIT_NAME)
        ).read(cfg);

        this.kitLore = ConfigValue.create("Kit.Lore.Default", Lists.newList(
            INFO_COST,
            INFO_COOLDOWN,
            "",
            KIT_DESCRIPTION,
            "",
            NO_PERMISSION,
            NO_MONEY,
            COOLDOWN,
            "",
            ACTION_GET,
            ACTION_PREVIEW
        )).read(cfg);

        this.loreGet = ConfigValue.create("Kit.Lore.Get", Lists.newList(
            LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Left-Click to " + LIGHT_YELLOW.enclose("get") + ".")
        )).read(cfg);

        this.lorePreview = ConfigValue.create("Kit.Lore.Preview", Lists.newList(
            LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Right-Click to " + LIGHT_YELLOW.enclose("preview") + ".")
        )).read(cfg);

        this.loreInfoCost = ConfigValue.create("Kit.Lore.InfoCost", Lists.newList(
            DARK_GRAY.enclose("Cost: " + ORANGE.enclose("$" + KIT_COST))
        )).read(cfg);

        this.loreInfoCooldown = ConfigValue.create("Kit.Lore.InfoCooldown", Lists.newList(
            DARK_GRAY.enclose("Cooldown: " + ORANGE.enclose(KIT_COOLDOWN))
        )).read(cfg);

        this.loreNoPerm = ConfigValue.create("Kit.Lore.No_Permission", Lists.newList(
            LIGHT_GRAY.enclose(LIGHT_RED.enclose("[❗]") + " You don't have " + LIGHT_RED.enclose("permission") + " to use this kit.")
        )).read(cfg);
        
        this.loreNoMoney = ConfigValue.create("Kit.Lore.No_Money", Lists.newList(
            LIGHT_GRAY.enclose(LIGHT_RED.enclose("[❗]") + " You need " + LIGHT_RED.enclose("$" + KIT_COST) + " to use this kit.")
        )).read(cfg);
        
        this.loreCooldown = ConfigValue.create("Kit.Lore.Cooldown", Lists.newList(
            LIGHT_GRAY.enclose(LIGHT_RED.enclose("[❗]") + " You can use this kit again in " + LIGHT_RED.enclose(GENERIC_COOLDOWN) + ".")
        )).read(cfg);
        
        this.kitSlots = ConfigValue.create("Kit.Slots", new int[] {10,11,12,13,14,15,16}).read(cfg);
    }
}
