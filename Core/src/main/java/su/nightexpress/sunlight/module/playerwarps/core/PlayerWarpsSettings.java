package su.nightexpress.sunlight.module.playerwarps.core;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.configuration.AbstractConfig;
import su.nightexpress.nightcore.configuration.ConfigProperty;
import su.nightexpress.nightcore.configuration.ConfigType;
import su.nightexpress.nightcore.configuration.ConfigTypes;
import su.nightexpress.nightcore.integration.currency.CurrencyId;
import su.nightexpress.nightcore.integration.permission.PermissionPlugins;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.Placeholders;
import su.nightexpress.nightcore.util.RankTable;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers;
import su.nightexpress.sunlight.SLConfigTypes;
import su.nightexpress.sunlight.module.playerwarps.category.NormalCategory;
import su.nightexpress.sunlight.module.playerwarps.featuring.FeaturedSlot;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PlayerWarpsSettings extends AbstractConfig {

    private static final ConfigType<FeaturedSlot> FEATURED_SLOT_CONFIG_TYPE = ConfigType.of(
        FeaturedSlot::read,
        FileConfig::set
    );

    private static final ConfigType<NormalCategory> WARP_CATEGORY_CONFIG_TYPE = ConfigType.of(
        NormalCategory::read,
        FileConfig::set
    );

    private final ConfigProperty<Integer> warpsSaveInterval = this.addProperty(ConfigTypes.INT, "Warp.Save-Interval",
        300,
        "Sets how often (in seconds) warp changes are saved to their disk files. Triggered on shutdown as well.",
        "[Default is 300]");

    private final ConfigProperty<Integer> warpsIdMaxCharacters = this.addProperty(ConfigTypes.INT, "Warp.Id-Max-Characters",
        20,
        "Sets the character limit for warp IDs upon creation.",
        "[Default is 20]");

    private final ConfigProperty<Integer> warpsNameMaxCharacters = this.addProperty(ConfigTypes.INT, "Warp.Name-Max-Characters",
        40,
        "Sets the character limit for warp names.",
        "[Default is 40]");

    private final ConfigProperty<Integer> warpsDescriptionMaxCharacters = this.addProperty(ConfigTypes.INT, "Warp.Description-Max-Characters",
        80,
        "Sets the character limit for warp descriptions.",
        "[Default is 80]");

    private final ConfigProperty<NightItem> warpsDefaultIcon = this.addProperty(ConfigTypes.NIGHT_ITEM, "Warp.Default-Icon",
        NightItem.fromType(Material.ENDER_PEARL),
        "Sets default icon for new warps.",
        Placeholders.URL_WIKI_ITEMS
    );

    private final ConfigProperty<Set<String>> warpsWorldBlacklist = this.addProperty(ConfigTypes.STRING_SET_LOWER_CASE, "Warp.Creation.World_Blacklist",
        Set.of("world_events", "custom_world"),
        "List of worlds where warp creation is disabled."
    );

    private final ConfigProperty<RankTable> warpsAmountPerRank = this.addProperty(SLConfigTypes.RANK_TABLE, "Warp.Amount_Per_Rank",
        RankTable.builder(RankTable.Mode.RANK, 0)
            .permissionPrefix("playerwarps.amount.")
            .addRankValue("rank1", 3)
            .addRankValue("rank2", 5)
            .addRankValue("owner", -1)
            .build(),
        "Sets the maximum number of warps a player can create based on their permissions or rank.",
        "The highest available value is always used.",
        "Setting to '-1' removes the limit entirely.",
        "[*] Requires %s with a compatible permissions plugin OR %s to work.".formatted(PermissionPlugins.VAULT, PermissionPlugins.LUCK_PERMS)
    );

    private final ConfigProperty<Map<String, NormalCategory>> categoryMap = this.addProperty(ConfigTypes.forMap(WARP_CATEGORY_CONFIG_TYPE, NormalCategory::id),
        "Warps.Categories",
        getDefaultCategories(),
        "Here you can create and edit warp categories."
    );

    private final ConfigProperty<Boolean> featuringEnabled = this.addProperty(ConfigTypes.BOOLEAN, "FeaturedWarps.Enabled",
        true,
        "Controls whether the 'Featued Warps' feature is enabled.",
        "This feature allows player to rent 'featured slots' in the Main Warps GUI to stick their warps there."
    );

    private final ConfigProperty<Integer> featuredUpdateInterval = this.addProperty(ConfigTypes.INT, "FeaturedWarps.Update-Interval",
        60,
        "Sets how often (in seconds) the featured warps cache is updated.",
        "[Default is 60]"
    );

    private final ConfigProperty<Map<String, FeaturedSlot>> featuringSlotMap = this.addProperty(ConfigTypes.forMapWithLowerKeys(FEATURED_SLOT_CONFIG_TYPE),
        "FeaturedWarps.Slots",
        getDefaultPromotionSlots(),
        "Here you can create and edit slots used for the Featued Warps feature."
    );

    private final ConfigProperty<Boolean> popularEnabled = this.addProperty(ConfigTypes.BOOLEAN, "PopularWarps.Enabled",
        true,
        "Controls whether the 'Popular Warps' feature is enabled.",
        "This feature allows players to see top 3 (depends on slots number) most visited warps in the Main Warps GUI."
    );

    private final ConfigProperty<int[]> popularSlots = this.addProperty(ConfigTypes.INT_ARRAY, "PopularWarps.Slots",
        new int[]{48, 49, 50},
        "Sets Main Warps GUI slots where the most visited warps are displayed."
    );

    private final ConfigProperty<Integer> popularUpdateInterval = this.addProperty(ConfigTypes.INT, "PopularWarps.Update-Interval",
        60,
        "Sets how often (in seconds) the popular warps cache is updated.",
        "[Default is 60]"
    );

    @NonNull
    private static Map<String, FeaturedSlot> getDefaultPromotionSlots() {
        return Map.of(
            "left", new FeaturedSlot("left", CurrencyId.VAULT, 1000, TimeUnit.SECONDS.convert(7, TimeUnit.DAYS), new int[]{1,2}),
            "right", new FeaturedSlot("right", CurrencyId.VAULT, 1000, TimeUnit.SECONDS.convert(7, TimeUnit.DAYS), new int[]{6,7})
        );
    }

    @NonNull
    private static Map<String, NormalCategory> getDefaultCategories() {
        Map<String, NormalCategory> map = new LinkedHashMap<>();

        NormalCategory category0 = new NormalCategory("default", "Default", true, Lists.newList(TagWrappers.GRAY.wrap("All other warps.")), NightItem.fromType(Material.COMPASS));
        NormalCategory category1 = new NormalCategory("shops", "Shops", false, Lists.newList(TagWrappers.GRAY.wrap("Buy items from player shops.")), NightItem.fromType(Material.EMERALD));
        NormalCategory category2 = new NormalCategory("farms", "Farms", false, Lists.newList(TagWrappers.GRAY.wrap("Farm mobs and items.")), NightItem.fromType(Material.CARROT));

        map.put(category0.id(), category0);
        map.put(category1.id(), category1);
        map.put(category2.id(), category2);

        return map;
    }

    public int getSaveInterval() {
        return this.warpsSaveInterval.get();
    }

    @NonNull
    public NightItem getDefaultIcon() {
        return this.warpsDefaultIcon.get().copy();
    }

    public int getWarpIdCharacterLimit() {
        return this.warpsIdMaxCharacters.get();
    }

    public int getWarpNameCharacterLimit() {
        return this.warpsNameMaxCharacters.get();
    }

    public int getWarpDescriptionCharacterLimit() {
        return this.warpsDescriptionMaxCharacters.get();
    }

    public boolean isBlacklistedWorld(@NonNull World world) {
        return this.warpsWorldBlacklist.get().contains(LowerCase.INTERNAL.apply(world.getName()));
    }

    public int getMaxWarpsAmount(@NonNull Player player) {
        return this.warpsAmountPerRank.get().getGreatestOrNegative(player).intValue();
    }

    @NonNull
    public Map<String, NormalCategory> getCategoryMap() {
        return this.categoryMap.get();
    }

    @NonNull
    public Set<NormalCategory> getCategories() {
        return Set.copyOf(this.getCategoryMap().values());
    }

    @Nullable
    public NormalCategory getCategory(@NonNull String id) {
        return this.getCategoryMap().get(LowerCase.INTERNAL.apply(id));
    }

    @Nullable
    public NormalCategory getPrimaryCategory() {
        return this.getCategoryMap().values().stream().filter(NormalCategory::primary).findFirst().orElse(null);
    }

    public boolean isFeaturingEnabled() {
        return this.featuringEnabled.get();
    }

    public int getFeaturedWarpsUpdateInterval() {
        return this.featuredUpdateInterval.get();
    }

    @NonNull
    public Map<String, FeaturedSlot> getFeaturingSlotMap() {
        return this.featuringSlotMap.get();
    }

    @Nullable
    public FeaturedSlot getFeaturingSlot(@NonNull String id) {
        return this.getFeaturingSlotMap().get(LowerCase.INTERNAL.apply(id));
    }

    public boolean isPopularEnabled() {
        return this.popularEnabled.get();
    }

    public int getPopularWarpsUpdateInterval() {
        return this.popularUpdateInterval.get();
    }

    public int[] getPopularSlots() {
        return this.popularSlots.get();
    }
}
