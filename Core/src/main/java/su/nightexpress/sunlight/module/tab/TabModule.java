package su.nightexpress.sunlight.module.tab;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.hook.HookId;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleInfo;
import su.nightexpress.sunlight.module.tab.config.TabConfig;
import su.nightexpress.sunlight.module.tab.impl.NameTagFormat;
import su.nightexpress.sunlight.module.tab.impl.TabListFormat;
import su.nightexpress.sunlight.module.tab.impl.TabNameFormat;
import su.nightexpress.sunlight.module.tab.listener.TabListener;
import su.nightexpress.sunlight.module.tab.util.PacketUtils;
import su.nightexpress.sunlight.utils.DynamicText;

import java.util.*;
import java.util.Map.Entry;

public class TabModule extends Module {

    private final Map<String, DynamicText> animationMap;

    private boolean packetsEnabled;

    public TabModule(@NotNull SunLightPlugin plugin, @NotNull String id) {
        super(plugin, id);
        this.animationMap = new HashMap<>();
    }

    @Override
    protected void gatherInfo(@NotNull ModuleInfo moduleInfo) {
        moduleInfo.setConfigClass(TabConfig.class);
    }

    @Override
    protected void onModuleLoad() {
        this.loadAnimations();

        this.addListener(new TabListener(this.plugin, this));

        this.addTask(this.plugin.createAsyncTask(this::updateTablistFormat).setTicksInterval(TabConfig.TABLIST_UPDATE_INTERVAL.get()));

        if (Plugins.isLoaded(HookId.PROTOCOL_LIB)) {
            this.packetsEnabled = true;
            this.addTask(this.plugin.createAsyncTask(this::updateNameTagsAndSortTab).setTicksInterval(TabConfig.NAMETAG_UPDATE_INTERVAL.get()));
        }
        else {
            this.warn(HookId.PROTOCOL_LIB + " is not installed. Nametags and tab sorting will be disabled.");
        }
    }

    @Override
    protected void onModuleUnload() {
        this.animationMap.clear();
    }

    private void loadAnimations() {
        FileConfig config = FileConfig.loadOrExtract(this.plugin, this.getLocalPath(), TabConfig.FILE_ANIMATIONS);
        if (config.getSection("").isEmpty()) {
            TabConfig.getDefaultAnimations().forEach(animator -> animator.write(config, animator.getId()));
        }

        for (String sId : config.getSection("")) {
            DynamicText animator = DynamicText.read(config, sId, sId);
            this.animationMap.put(animator.getId(), animator);
        }

        config.saveChanges();
    }

    @NotNull
    public Collection<DynamicText> getAnimations() {
        return this.animationMap.values();
    }

    @Nullable
    public TabListFormat getPlayerListFormat(@NotNull Player player) {
        Set<String> ranks = Players.getPermissionGroups(player);

        return TabConfig.TABLIST_FORMAT_MAP.get().values().stream()
            .filter(format -> format.isGoodWorld(player.getWorld()) && format.isGoodRank(ranks))
            .max(Comparator.comparingInt(TabListFormat::getPriority))
            .orElse(null);
    }

    @Nullable
    public TabNameFormat getPlayerListName(@NotNull Player player) {
        Set<String> groups = Players.getPermissionGroups(player);

        return TabConfig.TABLIST_NAME_FORMAT.get().entrySet().stream()
            .filter(entry -> groups.contains(entry.getKey()) || entry.getKey().equalsIgnoreCase(Placeholders.DEFAULT))
            .map(Entry::getValue).max(Comparator.comparingInt(TabNameFormat::getPriority))
            .orElse(null);
    }

    @Nullable
    public NameTagFormat getPlayerNametag(@NotNull Player player) {
        Set<String> groups = Players.getPermissionGroups(player);

        return TabConfig.NAMETAG_FORMAT.get().entrySet().stream()
            .filter(entry -> groups.contains(entry.getKey()) || entry.getKey().equalsIgnoreCase(Placeholders.DEFAULT))
            .map(Entry::getValue).max(Comparator.comparingInt(NameTagFormat::getPriority))
            .orElse(null);
    }

    public void updateAll(@NotNull Player player) {
        this.updateTabListName(player);
        this.updateTabListFormat(player);
        this.updateNameTagsAndSortTab(player);
    }

    public void updateTabListFormat(@NotNull Player player) {
        TabListFormat format = this.getPlayerListFormat(player);
        if (format == null) return;

        String header = format.getHeader();
        String footer = format.getFooter();

        for (DynamicText animator : this.getAnimations()) {
            header = animator.replacePlaceholders().apply(header);
            footer = animator.replacePlaceholders().apply(footer);
        }
        if (Plugins.hasPlaceholderAPI()) {
            header = PlaceholderAPI.setPlaceholders(player, header);
            footer = PlaceholderAPI.setPlaceholders(player, footer);
        }

        player.setPlayerListHeaderFooter(NightMessage.asLegacy(header), NightMessage.asLegacy(footer));
    }

    public void updateTabListName(@NotNull Player player) {
        TabNameFormat nameFormat = this.getPlayerListName(player);
        if (nameFormat == null) return;

        String format = Placeholders.forPlayer(player).apply(nameFormat.getFormat());

        for (DynamicText animator : this.getAnimations()) {
            format = animator.replacePlaceholders().apply(format);
        }
        if (Plugins.hasPlaceholderAPI()) {
            format = PlaceholderAPI.setPlaceholders(player, format);
        }

        player.setPlayerListName(NightMessage.asLegacy(format));
    }

    public void updateTablistFormat() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            this.updateTabListFormat(player);
            this.updateTabListName(player);
        }
    }

    public void updateNameTagsAndSortTab() {
        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            this.updateNameTagsAndSortTab(player);
        }
    }

    public void updateNameTagsAndSortTab(@NotNull Player player) {
        if (!this.packetsEnabled) return;

        NameTagFormat tag = this.getPlayerNametag(player);
        if (tag == null) return;

        PacketUtils.sendTeamPacket(player, tag);
    }
}
