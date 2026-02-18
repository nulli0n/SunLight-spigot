package su.nightexpress.sunlight.module.homes.config;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.bridge.item.AdaptedItem;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.configuration.AbstractConfig;
import su.nightexpress.nightcore.configuration.ConfigProperty;
import su.nightexpress.nightcore.configuration.ConfigType;
import su.nightexpress.nightcore.configuration.ConfigTypes;
import su.nightexpress.nightcore.integration.item.impl.AdaptedItemStack;
import su.nightexpress.nightcore.integration.item.impl.AdaptedVanillaStack;
import su.nightexpress.nightcore.integration.permission.PermissionPlugins;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.RankTable;
import su.nightexpress.sunlight.module.homes.HomeDefaults;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class HomesSettings extends AbstractConfig {

    private static final ConfigType<AdaptedItem> ADAPTED_ITEM_CONFIG_TYPE = ConfigType.of(
        AdaptedItemStack::read,
        FileConfig::set
    );

    private static final ConfigType<RankTable> RANK_TABLE_CONFIG_TYPE = ConfigType.of(
        RankTable::read,
        FileConfig::set
    );

    private final ConfigProperty<Integer> dataSaveInterval = this.addProperty(ConfigTypes.INT, "Data.Save_Interval",
        60,
        "Sets how often (in seconds) loaded homes will save their changes to the database."
    );


    private final ConfigProperty<Boolean> bedModeEnabled = this.addProperty(ConfigTypes.BOOLEAN, "Creation.BedMode.Enabled",
        true,
        "Sets whether or not players can use beds to set home locations."
    );

    private final ConfigProperty<Boolean> bedModeOverrideRespawn = this.addProperty(ConfigTypes.BOOLEAN, "Creation.BedMode.OverrideRespawn",
        false,
        "When enabled, overrides vanilla bed respawn point mechanic.",
        "Respawn point is set to the latest created/updated bed home."
    );

    private final ConfigProperty<Boolean> bedModeColors = this.addProperty(ConfigTypes.BOOLEAN, "Creation.BedMode.Colors",
        false,
        "When enabled, each bed color will set different home point."
    );

    private final ConfigProperty<String> iconDefaultId = this.addProperty(ConfigTypes.STRING, "Home.Icon.Default-Id",
        BukkitThing.getValue(Material.RED_BED),
        ""
    );

    private final ConfigProperty<Map<String, AdaptedItem>> iconPresets = this.addProperty(ConfigTypes.forMapWithLowerKeys(ADAPTED_ITEM_CONFIG_TYPE),
        "Home.Icon.Presets",
        HomeDefaults.getDefaultIconPresets(),
        ""
    );


    private final ConfigProperty<Boolean> checkBuildAccess = this.addProperty(ConfigTypes.BOOLEAN, "Creation.Check_Build_Access",
        true,
        "When enabled, simulates player block place event to check for build access.",
        "If building is not allowed, home can't be created."
    );

    private final ConfigProperty<Set<String>> worldBlacklist = this.addProperty(ConfigTypes.STRING_SET_LOWER_CASE, "Creation.World_Blacklist",
        Set.of("world_name", "another_world"),
        "A list of worlds, where homes can not be created."
    );

    private final ConfigProperty<RankTable> homesPerRank = this.addProperty(RANK_TABLE_CONFIG_TYPE, "Creation.Amount_Per_Rank",
        RankTable.builder(RankTable.Mode.RANK, 1)
            .addRankValue("vip", 3)
            .addRankValue("gold", 5)
            .addRankValue("admin", -1)
            .permissionPrefix("homes.amount.")
            .build(),
        "Sets how much homes a player can create depends on their rank/permissions.",
        "",
        "If player is in multiple groups listed here, the greater value will be used.",
        "If player is not in any group listed here, the 'default' value will be used if present.",
        "",
        "[*] Requires %s with a compatible permissions plugin OR %s to work.".formatted(PermissionPlugins.VAULT, PermissionPlugins.LUCK_PERMS),
        "Use '-1' for unlimited amount."
    );

    public int getDataSaveInterval() {
        return this.dataSaveInterval.get();
    }

    public boolean isBedModeEnabled() {
        return this.bedModeEnabled.get();
    }

    public boolean isBedModeOverrideRespawn() {
        return this.bedModeOverrideRespawn.get();
    }

    public boolean isBedModeWithColors() {
        return this.bedModeColors.get();
    }

    @NotNull
    public AdaptedItem getIconOrDefault(@NotNull String iconId) {
        return Optional.ofNullable(this.getIconPreset(iconId))
            .or(() -> Optional.ofNullable(this.getIconPreset(this.getDefaultIconId())))
            .orElse(AdaptedVanillaStack.of(new ItemStack(Material.RED_BED)));
    }

    @NotNull
    public String getDefaultIconId() {
        return this.iconDefaultId.get();
    }

    @NotNull
    public Map<String, AdaptedItem> getIconPresets() {
        return Map.copyOf(this.iconPresets.get());
    }

    @Nullable
    public AdaptedItem getIconPreset(@NotNull String iconId) {
        return this.iconPresets.get().get(LowerCase.INTERNAL.apply(iconId));
    }

    public boolean isCheckBuildAccess() {
        return this.checkBuildAccess.get();
    }

    public boolean isBlacklistedWorld(@NotNull String world) {
        return this.worldBlacklist.get().contains(LowerCase.INTERNAL.apply(world));
    }

    @NotNull
    public RankTable getHomesByRankAmount() {
        return this.homesPerRank.get();
    }
}
