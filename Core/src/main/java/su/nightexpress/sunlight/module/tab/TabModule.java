package su.nightexpress.sunlight.module.tab;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.EntityUtil;
import su.nexmedia.engine.utils.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.hook.HookId;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.tab.config.TabConfig;
import su.nightexpress.sunlight.module.tab.impl.NametagFormat;
import su.nightexpress.sunlight.module.tab.impl.TabListFormat;
import su.nightexpress.sunlight.module.tab.impl.TabNameFormat;
import su.nightexpress.sunlight.module.tab.listener.TabListener;
import su.nightexpress.sunlight.module.tab.task.NametagUpdateTask;
import su.nightexpress.sunlight.module.tab.task.TablistUpdateTask;
import su.nightexpress.sunlight.module.tab.util.PacketUtils;
import su.nightexpress.sunlight.utils.SimpleTextAnimator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TabModule extends Module {

    public final Map<String, SimpleTextAnimator> animationMap;

    private TablistUpdateTask tablistUpdateTask;
    private NametagUpdateTask nametagUpdateTask;

    public TabModule(@NotNull SunLight plugin, @NotNull String id) {
        super(plugin, id);
        this.animationMap = new HashMap<>();
    }

    @Override
    public void onLoad() {
        this.getConfig().initializeOptions(TabConfig.class);

        // Setup Animations
        JYML animConfig = JYML.loadOrExtract(this.plugin, this.getLocalPath(), "animations.yml");
        for (String animId : animConfig.getSection("")) {
            SimpleTextAnimator animator = SimpleTextAnimator.read(animConfig, animId, animId);
            this.animationMap.put(animator.getId(), animator);
        }

        this.addListener(new TabListener(this));

        this.tablistUpdateTask = new TablistUpdateTask(this, TabConfig.TABLIST_UPDATE_INTERVAL.get());
        this.tablistUpdateTask.start();

        if (Hooks.hasPlugin(HookId.PROTOCOL_LIB)) {
            this.nametagUpdateTask = new NametagUpdateTask(this, TabConfig.NAMETAG_UPDATE_INTERVAL.get());
            this.nametagUpdateTask.start();
        }
        else {
            this.error(HookId.PROTOCOL_LIB + " is not installed. Nametags and tab sorting will be disabled.");
        }
    }

    @Override
    public void onShutdown() {
        if (this.tablistUpdateTask != null) {
            this.tablistUpdateTask.stop();
            this.tablistUpdateTask = null;
        }
        if (this.nametagUpdateTask != null) {
            this.nametagUpdateTask.stop();
            this.nametagUpdateTask = null;
        }
        this.animationMap.clear();
    }

    @Nullable
    public TabListFormat getPlayerListFormat(@NotNull Player player) {
        Set<String> groups = Hooks.getPermissionGroups(player);
        return TabConfig.TABLIST_FORMAT_MAP.get().values().stream()
            .filter(format -> format.getWorlds().contains(player.getWorld().getName()) || format.getWorlds().contains(Placeholders.WILDCARD))
            .filter(format -> groups.stream().anyMatch(pRank -> format.getGroups().contains(pRank)) || format.getGroups().contains(Placeholders.WILDCARD))
            .max(Comparator.comparingInt(TabListFormat::getPriority))
            .orElse(null);
    }

    @Nullable
    public TabNameFormat getPlayerListName(@NotNull Player player) {
        Set<String> groups = Hooks.getPermissionGroups(player);
        return TabConfig.TABLIST_NAME_FORMAT.get().entrySet().stream()
            .filter(entry -> groups.contains(entry.getKey()) || entry.getKey().equalsIgnoreCase(Placeholders.DEFAULT))
            .map(Entry::getValue).max(Comparator.comparingInt(TabNameFormat::getPriority))
            .orElse(null);
    }

    @Nullable
    public NametagFormat getPlayerNametag(@NotNull Player player) {
        Set<String> groups = Hooks.getPermissionGroups(player);
        return TabConfig.NAMETAG_FORMAT.get().entrySet().stream()
            .filter(entry -> groups.contains(entry.getKey()) || entry.getKey().equalsIgnoreCase(Placeholders.DEFAULT))
            .map(Entry::getValue).max(Comparator.comparingInt(NametagFormat::getPriority))
            .orElse(null);
    }

    public void updateAll(@NotNull Player player) {
        this.updateTabListName(player);
        this.updateTabListFormat(player);
        this.updateNameTagsAndSortTab(player);
    }

    public void updateTabListFormat(@NotNull Player player) {
        if (EntityUtil.isNPC(player)) return;

        TabListFormat format = this.getPlayerListFormat(player);
        if (format == null) return;

        String header = format.getHeader();
        String footer = format.getFooter();

        for (SimpleTextAnimator animator : this.animationMap.values()) {
            header = animator.replace(header);
            footer = animator.replace(footer);
        }
        if (Hooks.hasPlaceholderAPI()) {
            header = Colorizer.apply(PlaceholderAPI.setPlaceholders(player, header));
            footer = Colorizer.apply(PlaceholderAPI.setPlaceholders(player, footer));
        }

        player.setPlayerListHeaderFooter(header, footer);
    }

    public void updateTabListName(@NotNull Player player) {
        if (EntityUtil.isNPC(player)) return;

        TabNameFormat listName = this.getPlayerListName(player);
        if (listName == null) return;

        String format = Placeholders.Player.replacer(player).apply(listName.getFormat());
        if (Hooks.hasPlaceholderAPI()) {
            format = Colorizer.apply(PlaceholderAPI.setPlaceholders(player, format));
        }

        player.setPlayerListName(format);
    }

    public void updateNameTagsAndSortTab() {
        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            this.updateNameTagsAndSortTab(player);
        }
    }

    public void updateNameTagsAndSortTab(@NotNull Player player) {
        if (!Hooks.hasPlugin(HookId.PROTOCOL_LIB)) return;
        if (EntityUtil.isNPC(player)) return;

        NametagFormat tag = this.getPlayerNametag(player);
        if (tag == null) return;

        PacketUtils.sendTeamPacket(player, tag);
    }
}
