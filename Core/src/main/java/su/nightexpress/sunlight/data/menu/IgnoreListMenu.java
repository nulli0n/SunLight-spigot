package su.nightexpress.sunlight.data.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.ConfigMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.data.impl.IgnoredUser;

import java.util.*;

public class IgnoreListMenu extends ConfigMenu<SunLight> implements AutoPaged<IgnoredUser> {

    private final String userName;
    private final List<String> userLore;
    private final int[] objectSlots;

    private final Map<Player, UUID> otherCache;

    public IgnoreListMenu(@NotNull SunLight plugin) {
        super(plugin, JYML.loadOrExtract(plugin, "/menu/user_ignore_list.yml"));
        this.otherCache = new WeakHashMap<>();
        this.userName = Colorizer.apply(cfg.getString("User.Name", Placeholders.GENERIC_NAME));
        this.userLore = Colorizer.apply(cfg.getStringList("User.Lore"));
        this.objectSlots = cfg.getIntArray("User.Slots");

        this.registerHandler(MenuItemType.class)
            .addClick(MenuItemType.CLOSE, (viewer, event) -> plugin.runTask(task -> viewer.getPlayer().closeInventory()));

        this.load();
    }

    public void open(@NotNull Player player, @NotNull UUID playerId) {
        this.otherCache.put(player, playerId);
        this.open(player, 1);
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        this.getItemsForPage(viewer).forEach(this::addItem);
    }

    @Override
    public void onClose(@NotNull MenuViewer viewer, @NotNull InventoryCloseEvent event) {
        super.onClose(viewer, event);
        this.otherCache.remove(viewer.getPlayer());
    }

    @Override
    public int[] getObjectSlots() {
        return objectSlots;
    }

    @Override
    @NotNull
    public List<IgnoredUser> getObjects(@NotNull Player player) {
        UUID playerId = this.otherCache.get(player);
        SunUser user = plugin.getUserManager().getUserData(playerId == null ? player.getUniqueId() : playerId);
        if (user == null) return Collections.emptyList();

        return new ArrayList<>(user.getIgnoredUsers().values());
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull IgnoredUser ignoredUser) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemUtil.mapMeta(item, meta -> {
            if (meta instanceof SkullMeta skullMeta) {
                skullMeta.setOwningPlayer(plugin.getServer().getOfflinePlayer(ignoredUser.getUserInfo().getId()));
            }
            meta.setDisplayName(this.userName);
            meta.setLore(this.userLore);
            meta.addItemFlags(ItemFlag.values());
            ItemUtil.replace(meta, ignoredUser.replacePlaceholders());
        });
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull IgnoredUser ignoredUser) {
        return (viewer, event) -> {
            Player player = viewer.getPlayer();
            SunUser user = plugin.getUserManager().getUserData(player);
            if (event.isRightClick()) {
                user.removeIgnoredUser(ignoredUser.getUserInfo().getId());
                this.plugin.getUserManager().saveUser(user);
                this.plugin.runTask(task -> this.open(player, viewer.getPage()));
                return;
            }
            this.plugin.runTask(task -> plugin.getUserManager().getIgnoreSettingsMenu().open(player, ignoredUser));
        };
    }
}
