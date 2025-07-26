package su.nightexpress.sunlight.core.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
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
import su.nightexpress.nightcore.menu.link.Linked;
import su.nightexpress.nightcore.menu.link.ViewLink;
import su.nightexpress.nightcore.util.ItemReplacer;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.config.Config;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.core.user.IgnoredUser;

import java.util.*;
import java.util.stream.IntStream;

import static su.nightexpress.sunlight.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class IgnoreListMenu extends ConfigMenu<SunLightPlugin> implements AutoFilled<IgnoredUser>, Linked<UUID> {

    public static final String FILE_NAME = "ignore_list.yml";

    private String       userName;
    private List<String> userLore;
    private int[]        userSlots;

    private final ViewLink<UUID> link;

    public IgnoreListMenu(@NotNull SunLightPlugin plugin) {
        super(plugin, FileConfig.loadOrExtract(plugin, Config.DIR_MENU, FILE_NAME));
        this.link = new ViewLink<>();

        this.load();
    }

    @NotNull
    @Override
    public ViewLink<UUID> getLink() {
        return link;
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        this.autoFill(viewer);
    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    public void onAutoFill(@NotNull MenuViewer viewer, @NotNull AutoFill<IgnoredUser> autoFill) {
        UUID targetId = this.getLink(viewer);
        SunUser targetUser = plugin.getUserManager().getOrFetch(targetId);
        if (targetUser == null) return;

        autoFill.setSlots(this.userSlots);
        autoFill.setItems(targetUser.getIgnoredUsers().values());
        autoFill.setItemCreator(ignoredUser -> {
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            ItemUtil.editMeta(item, meta -> {
                if (meta instanceof SkullMeta skullMeta) {
                    skullMeta.setOwningPlayer(plugin.getServer().getOfflinePlayer(targetUser.getId()));
                }
            });
            ItemReplacer.create(item).readMeta().hideFlags().trimmed()
                .setDisplayName(this.userName)
                .setLore(this.userLore)
                .replace(ignoredUser.replacePlaceholders())
                .writeMeta();
            return item;
        });
        autoFill.setClickAction(ignoredUser -> (viewer1, event) -> {
            Player player = viewer1.getPlayer();
            if (event.isRightClick()) {
                targetUser.removeIgnoredUser(ignoredUser.getUserInfo().getId());
                this.plugin.getUserManager().save(targetUser);
                this.plugin.runTask(task -> this.open(player, viewer1.getPage()));
                return;
            }
            this.plugin.runTask(task -> plugin.getUserManager().getIgnoreSettingsMenu().open(player, ignoredUser));
        });
    }

    @Override
    @NotNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions("Ignored Players", MenuSize.CHEST_36);
    }

    @Override
    protected void loadAdditional() {
        this.userName = ConfigValue.create("User.Name",
            LIGHT_YELLOW.enclose(BOLD.enclose("Player: ")) + WHITE.enclose(GENERIC_NAME)
        ).read(cfg);

        this.userLore = ConfigValue.create("User.Lore", Lists.newList(
            LIGHT_YELLOW.enclose(BOLD.enclose("Info:")),
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Hide Chat: ") + IgnoredUser.PLACEHOLDER_HIDE_CHAT),
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Deny PMs: ") + IgnoredUser.PLACEHOLDER_DENY_CONVERSATIONS),
            LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Deny TPA: ") + IgnoredUser.PLACEHOLDER_DENY_TELEPORTS),
            " ",
            LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Left Click for " + LIGHT_YELLOW.enclose("settings") + "."),
            LIGHT_GRAY.enclose(LIGHT_GREEN.enclose("[▶]") + " Right Click to " + LIGHT_GREEN.enclose("unblock") + ".")
        )).read(cfg);

        this.userSlots = ConfigValue.create("User.Slots",
            IntStream.range(0, 27).toArray()
        ).read(cfg);
    }

    @Override
    @NotNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack close = ItemUtil.getSkinHead(SKIN_WRONG_MARK);
        ItemUtil.editMeta(close, meta -> {
            meta.setDisplayName(Lang.EDITOR_ITEM_CLOSE.getDefaultName());
        });
        list.add(new MenuItem(close).setPriority(10).setSlots(31).setHandler(ItemHandler.forClose(this)));

        ItemStack nextPage = ItemUtil.getSkinHead(SKIN_ARROW_RIGHT);
        ItemUtil.editMeta(nextPage, meta -> {
            meta.setDisplayName(Lang.EDITOR_ITEM_NEXT_PAGE.getDefaultName());
        });
        list.add(new MenuItem(nextPage).setPriority(10).setSlots(35).setHandler(ItemHandler.forNextPage(this)));

        ItemStack backPage = ItemUtil.getSkinHead(SKIN_ARROW_LEFT);
        ItemUtil.editMeta(backPage, meta -> {
            meta.setDisplayName(Lang.EDITOR_ITEM_PREVIOUS_PAGE.getDefaultName());
        });
        list.add(new MenuItem(backPage).setPriority(10).setSlots(27).setHandler(ItemHandler.forPreviousPage(this)));

        return list;
    }
}
