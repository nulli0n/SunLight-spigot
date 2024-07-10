package su.nightexpress.sunlight.core.menu;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.menu.MenuOptions;
import su.nightexpress.nightcore.menu.MenuSize;
import su.nightexpress.nightcore.menu.MenuViewer;
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
import su.nightexpress.sunlight.core.user.IgnoredUser;

import java.util.*;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.sunlight.Placeholders.*;

public class IgnoreSettingsMenu extends ConfigMenu<SunLightPlugin> implements Linked<IgnoredUser> {

    private static final String FILE_NAME = "ignore_settings.yml";

    private final ViewLink<IgnoredUser> link;
    private final ItemHandler returnHandler;
    private final ItemHandler hideChatHandler;
    private final ItemHandler denyConvoHandler;
    private final ItemHandler denyTeleportsHandler;

    public IgnoreSettingsMenu(@NotNull SunLightPlugin plugin) {
        super(plugin, FileConfig.loadOrExtract(plugin, Config.DIR_MENU, FILE_NAME));
        this.link = new ViewLink<>();

        this.addHandler(this.returnHandler = ItemHandler.forReturn(this, (viewer, event) -> {
            plugin.getUserManager().getIgnoreListMenu().open(viewer.getPlayer(), 1);
        }));

        this.addHandler(this.hideChatHandler = new ItemHandler("hide_chat", (viewer, event) -> {
            IgnoredUser ignoredUser = this.getLink(viewer);
            ignoredUser.setHideChatMessages(!ignoredUser.isHideChatMessages());
            this.runNextTick(() -> this.flush(viewer));
        }));

        this.addHandler(this.denyConvoHandler = new ItemHandler("deny_conversations", (viewer, event) -> {
            IgnoredUser ignoredUser = this.getLink(viewer);
            ignoredUser.setDenyConversations(!ignoredUser.isDenyConversations());
            this.runNextTick(() -> this.flush(viewer));
        }));

        this.addHandler(this.denyTeleportsHandler = new ItemHandler("deny_teleports", (viewer, event) -> {
            IgnoredUser ignoredUser = this.getLink(viewer);
            ignoredUser.setDenyTeleports(!ignoredUser.isDenyTeleports());
            this.runNextTick(() -> this.flush(viewer));
        }));

        this.load();

        this.getItems().forEach(menuItem -> menuItem.getOptions().addDisplayModifier((viewer, item) -> {
            ItemReplacer.replace(item, this.getLink(viewer).getPlaceholders());
        }));
    }

    @NotNull
    @Override
    public ViewLink<IgnoredUser> getLink() {
        return link;
    }

    enum Type {
        HIDE_CHAT, DENY_CONVERSATIONS, DENY_TELEPORTS
    }

    @Override
    protected void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {

    }

    @Override
    protected void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {

    }

    @Override
    @NotNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions(BLACK.enclose("Ignore Settings"), MenuSize.CHEST_36);
    }

    @Override
    @NotNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack back = ItemUtil.getSkinHead(SKIN_ARROW_DOWN);
        ItemUtil.editMeta(back, meta -> {
            meta.setDisplayName(Lang.EDITOR_ITEM_RETURN.getDefaultName());
        });
        list.add(new MenuItem(back).setPriority(10).setSlots(31).setHandler(this.returnHandler));

        ItemStack hideChat = new ItemStack(Material.MAP);
        ItemUtil.editMeta(hideChat, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Hide Chat")));
            meta.setLore(Lists.newList(
                LIGHT_YELLOW.enclose("● " + LIGHT_GRAY.enclose("Enabled: ") + IgnoredUser.PLACEHOLDER_HIDE_CHAT),
                " ",
                LIGHT_GRAY.enclose("Hides out chat messages"),
                LIGHT_GRAY.enclose("from this player."),
                " ",
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Click to " + LIGHT_YELLOW.enclose("toggle") + ".")
            ));
        });
        list.add(new MenuItem(hideChat).setPriority(10).setSlots(10).setHandler(this.hideChatHandler));

        ItemStack denyConvos = new ItemStack(Material.WRITABLE_BOOK);
        ItemUtil.editMeta(denyConvos, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Block Conversations")));
            meta.setLore(Lists.newList(
                LIGHT_YELLOW.enclose("● " + LIGHT_GRAY.enclose("Enabled: ") + IgnoredUser.PLACEHOLDER_DENY_CONVERSATIONS),
                " ",
                LIGHT_GRAY.enclose("Player can not send you"),
                LIGHT_GRAY.enclose("private messages."),
                " ",
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Click to " + LIGHT_YELLOW.enclose("toggle") + ".")
            ));
        });
        list.add(new MenuItem(denyConvos).setPriority(10).setSlots(13).setHandler(this.denyConvoHandler));

        ItemStack denyTP = new ItemStack(Material.ENDER_PEARL);
        ItemUtil.editMeta(denyTP, meta -> {
            meta.setDisplayName(LIGHT_YELLOW.enclose(BOLD.enclose("Block Teleports")));
            meta.setLore(Lists.newList(
                LIGHT_YELLOW.enclose("● " + LIGHT_GRAY.enclose("Enabled: ") + IgnoredUser.PLACEHOLDER_DENY_TELEPORTS),
                " ",
                LIGHT_GRAY.enclose("Player can not send you"),
                LIGHT_GRAY.enclose("teleport requests."),
                " ",
                LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("[▶]") + " Click to " + LIGHT_YELLOW.enclose("toggle") + ".")
            ));
        });
        list.add(new MenuItem(denyTP).setPriority(10).setSlots(13).setHandler(this.denyTeleportsHandler));

        return list;
    }

    @Override
    protected void loadAdditional() {

    }
}
