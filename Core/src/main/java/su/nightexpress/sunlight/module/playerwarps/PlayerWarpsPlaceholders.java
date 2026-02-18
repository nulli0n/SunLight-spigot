package su.nightexpress.sunlight.module.playerwarps;

import su.nightexpress.nightcore.integration.currency.EconomyBridge;
import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.placeholder.TypedPlaceholder;
import su.nightexpress.nightcore.util.time.TimeFormatType;
import su.nightexpress.nightcore.util.time.TimeFormats;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.playerwarps.category.NormalCategory;
import su.nightexpress.sunlight.module.playerwarps.category.WarpCategory;
import su.nightexpress.sunlight.module.playerwarps.featuring.FeaturedSlot;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class PlayerWarpsPlaceholders {

    public static final String WARP_ID          = "%warp_id%";
    public static final String WARP_NAME        = "%warp_name%";
    public static final String WARP_DESCRIPTION = "%warp_description%";
    public static final String WARP_PRICE       = "%warp_price%";
    public static final String WARP_WORLD       = "%warp_location_world%";
    public static final String WARP_LOCATION_X  = "%warp_location_x%";
    public static final String WARP_LOCATION_Y  = "%warp_location_y%";
    public static final String WARP_LOCATION_Z  = "%warp_location_z%";
    public static final String WARP_CATEGORY    = "%warp_category%";
    public static final String WARP_VISITS      = "%warp_visits%";
    public static final String WARP_OWNER_NAME  = "%warp_owner_name%";

    public static final String SLOT_PRICE    = "%slot_price%";
    public static final String SLOT_DURATION = "%slot_duration%";

    public static final String CATEGORY_NAME        = "%category_name%";
    public static final String CATEGORY_DESCRIPTION = "%category_description%";

    public static final TypedPlaceholder<PlayerWarp> PLAYER_WARP = TypedPlaceholder.builder(PlayerWarp.class)
        .with(WARP_ID, PlayerWarp::getId)
        .with(WARP_NAME, PlayerWarp::getName)
        .with(WARP_DESCRIPTION, warp -> String.join("\n", warp.getDescription()))
        .with(WARP_PRICE, warp -> warp.hasPrice() ? NumberUtil.format(warp.getPrice()) : Lang.OTHER_FREE.text())
        .with(WARP_LOCATION_X, warp -> NumberUtil.format(warp.getBlockPos().getX()))
        .with(WARP_LOCATION_Y, warp -> NumberUtil.format(warp.getBlockPos().getY()))
        .with(WARP_LOCATION_Z, warp -> NumberUtil.format(warp.getBlockPos().getZ()))
        .with(WARP_WORLD, warp -> warp.isActive() ? LangAssets.get(warp.getWorld()) : warp.getWorldName())
        .with(WARP_CATEGORY, warp -> Optional.ofNullable(warp.getCategory()).map(WarpCategory::name).orElse(warp.getCategoryId()))
        .with(WARP_VISITS, warp -> String.valueOf(warp.getTotalVisits()))
        .with(WARP_OWNER_NAME, warp -> warp.getOwner().name())
        .build();

    public static final TypedPlaceholder<FeaturedSlot> FEATURED_SLOT = TypedPlaceholder.builder(FeaturedSlot.class)
        .with(SLOT_PRICE, slot -> EconomyBridge.getCurrencyOrDummy(slot.currencyId()).format(slot.price()))
        .with(SLOT_DURATION, slot -> TimeFormats.formatAmount(TimeUnit.SECONDS.toMillis(slot.duration()), TimeFormatType.LITERAL))
        .build();

    public static final TypedPlaceholder<NormalCategory> CATEGORY = TypedPlaceholder.builder(NormalCategory.class)
        .with(CATEGORY_NAME, NormalCategory::name)
        .with(CATEGORY_DESCRIPTION, category -> String.join("\n", category.description()))
        .build();
}
