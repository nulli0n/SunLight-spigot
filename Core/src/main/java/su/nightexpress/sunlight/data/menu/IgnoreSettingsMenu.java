package su.nightexpress.sunlight.data.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.api.menu.impl.ConfigMenu;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.data.impl.IgnoredUser;

import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

public class IgnoreSettingsMenu extends ConfigMenu<SunLight> {

    private final Map<Player, IgnoredUser> objectMap;

    public IgnoreSettingsMenu(@NotNull SunLight plugin) {
        super(plugin, JYML.loadOrExtract(plugin, "/menu/user_ignore_settings.yml"));
        this.objectMap = new WeakHashMap<>();

        this.registerHandler(MenuItemType.class)
            .addClick(MenuItemType.RETURN, (viewer, event) -> plugin.runTask(task -> {
                plugin.getUserManager().getIgnoreListMenu().open(viewer.getPlayer(), 1);
            }));

        this.registerHandler(Type.class)
            .addClick(Type.HIDE_CHAT, (viewer, event) -> {
                this.getIgnoredUser(viewer).ifPresent(ignoredUser -> {
                    ignoredUser.setHideChatMessages(!ignoredUser.isHideChatMessages());
                    this.plugin.runTask(task -> this.open(viewer.getPlayer(), viewer.getPage()));
                });
            })
            .addClick(Type.DENY_CONVERSATIONS, (viewer, event) -> {
                this.getIgnoredUser(viewer).ifPresent(ignoredUser -> {
                    ignoredUser.setDenyConversations(!ignoredUser.isDenyConversations());
                    this.plugin.runTask(task -> this.open(viewer.getPlayer(), viewer.getPage()));
                });
            })
            .addClick(Type.DENY_TELEPORTS, (viewer, event) -> {
                this.getIgnoredUser(viewer).ifPresent(ignoredUser -> {
                    ignoredUser.setDenyTeleports(!ignoredUser.isDenyTeleports());
                    this.plugin.runTask(task -> this.open(viewer.getPlayer(), viewer.getPage()));
                });
            });

        this.load();

        this.getItems().forEach(menuItem -> {
            if (menuItem.getOptions().getDisplayModifier() == null) {
                menuItem.getOptions().setDisplayModifier((viewer, item) -> {
                    this.getIgnoredUser(viewer).ifPresent(ignoredUser -> ItemUtil.replace(item, ignoredUser.replacePlaceholders()));
                });
            }
        });
    }

    enum Type {
        HIDE_CHAT, DENY_CONVERSATIONS, DENY_TELEPORTS
    }

    public void open(@NotNull Player player, @NotNull IgnoredUser ignoredUser) {
        this.objectMap.put(player, ignoredUser);
        this.open(player, 1);
    }

    @NotNull
    private Optional<IgnoredUser> getIgnoredUser(@NotNull MenuViewer viewer) {
        return Optional.ofNullable(this.objectMap.get(viewer.getPlayer()));
    }

    @Override
    public void onClose(@NotNull MenuViewer viewer, @NotNull InventoryCloseEvent event) {
        super.onClose(viewer, event);
        this.objectMap.remove(viewer.getPlayer());
    }
}
