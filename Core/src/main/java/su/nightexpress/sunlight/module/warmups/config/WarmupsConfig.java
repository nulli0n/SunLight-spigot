package su.nightexpress.sunlight.module.warmups.config;

import org.bukkit.Color;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.rankmap.IntRankMap;
import su.nightexpress.nightcore.util.wrapper.UniParticle;
import su.nightexpress.sunlight.api.type.TeleportType;

import java.util.Set;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.sunlight.Placeholders.*;

public class WarmupsConfig {

    public static final ConfigValue<Long> WARMUP_TICK_INTERVAL = ConfigValue.create("Warmup.Tick_Interval",
        2L,
        "Sets how often active warmups will 'tick' to update indicator and check for completion.",
        "[Asynchronous]",
        "[20 ticks = 1 second]",
        "[Default is 2 ticks]"
    );

    public static final ConfigValue<Boolean> WARMUP_CANCEL_ON_MOVE = ConfigValue.create("Warmup.CancelOnMove",
        true,
        "Controls whether warmup should be cancelled if player moves."
    );

    public static final ConfigValue<Boolean> WARMUP_CANCEL_ON_INTERACT = ConfigValue.create("Warmup.CancelOnInteract",
        true,
        "Controls whether warmup should be cancelled if player interacts with blocks or entities."
    );

    public static final ConfigValue<Boolean> WARMUP_CANCEL_ON_DAMAGE = ConfigValue.create("Warmup.CancelOnDamage",
        true,
        "Controls whether warmup should be cancelled if player damages or get damaged by something."
    );

    public static final ConfigValue<Double> WARMUP_MOVEMENT_THRESHOLD = ConfigValue.create("Warmup.MovementThreshold",
        0.5,
        "Sets threshold (in blocks) for the player movement to determine if warmup should be cancelled.",
        "[Default is 0.5 blocks]"
    );

    public static final ConfigValue<Boolean> WARMUP_PARTICLES_ENABLED = ConfigValue.create("Warmup.Particles.Enabled",
        true,
        "Creates particles around the player during warmup."
    );

    public static final ConfigValue<UniParticle> WARMUP_PARTICLES_LEFT = ConfigValue.create("Warmup.Particles.Left",
        UniParticle.redstone(Color.fromRGB(0, 224, 0), 2F)
    );

    public static final ConfigValue<UniParticle> WARMUP_PARTICLES_RIGHT = ConfigValue.create("Warmup.Particles.Right",
        UniParticle.redstone(Color.fromRGB(128, 224, 128), 2F)
    );

    public static final ConfigValue<Double> EFFECT_X  = ConfigValue.create("Warmup.Particles.EffectX", 10D);
    public static final ConfigValue<Double> EFFECT_Z1 = ConfigValue.create("Warmup.Particles.EffectZ1", 0.5D);
    public static final ConfigValue<Double> EFFECT_Z2 = ConfigValue.create("Warmup.Particles.EffectZ2", 0.5D);
    public static final ConfigValue<Double> EFFECT_Y  = ConfigValue.create("Warmup.Particles.EffectY", 1.25);

    public static final ConfigValue<Set<TeleportType>> TELEPORT_HANDLED_TYPES = ConfigValue.forSet("Teleport.HandledTypes",
        str -> StringUtil.getEnum(str, TeleportType.class).orElse(null),
        (cfg, path, set) -> cfg.set(path, Lists.modify(set, Enum::name)),
        () -> {
            Set<TeleportType> types = Lists.newSet(TeleportType.values());
            types.remove(TeleportType.OTHER);
            return types;
        },
        "The following teleport types will be handled by the Warmups module.",
        "Available types: [" + StringUtil.inlineEnum(TeleportType.class, ",") + "]",
        "[*] Forced teleports (spawn on join, by admin commands, etc.) won't be handled by Warmups module."
    );

    public static final ConfigValue<IntRankMap> TELEPORT_WARMUPS_BY_RANK = ConfigValue.create("Teleport.Warmups_By_Rank",
        IntRankMap::read,
        (cfg, path, map) -> map.write(cfg, path),
        () -> IntRankMap.ranked(5).addValue("vip", 4).addValue("admin", 0),
        "Sets teleport warmup values based on player's rank or permissions.",
        "Use 0 for no warmup."
    );

    public static final ConfigValue<Boolean> BAR_INDICATOR_ENABLED = ConfigValue.create("BarIndicator.Enabled",
        true,
        "Sets whether warmup boss bar indicator should be used."
    );

    public static final ConfigValue<String> BAR_INDICATOR_TELEPORT_TITLE = ConfigValue.create("BarIndicator.Teleport.Title",
        WHITE.enclose("Teleporting in " + GREEN.enclose(GENERIC_TIME + "s")),
        "Sets warmup indicator title for teleport warmups."
    );

    public static final ConfigValue<BarColor> BAR_INDICATOR_TELEPORT_COLOR = ConfigValue.create("BarIndicator.Teleport.Color",
        BarColor.class, BarColor.GREEN,
        "Sets warmup indicator color for teleport warmups."
    );

    public static final ConfigValue<BarStyle> BAR_INDICATOR_TELEPORT_STYLE = ConfigValue.create("BarIndicator.Teleport.Style",
        BarStyle.class, BarStyle.SEGMENTED_10,
        "Sets warmup indicator style for teleport warmups."
    );
}
