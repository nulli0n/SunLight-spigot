package su.nightexpress.sunlight.module.kits;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.integration.currency.EconomyBridge;
import su.nightexpress.nightcore.util.placeholder.TypedPlaceholder;
import su.nightexpress.nightcore.util.time.TimeFormatType;
import su.nightexpress.nightcore.util.time.TimeFormats;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.kits.model.Kit;

public class KitsPlaceholders {

    public static final String KIT_ID                  = "%kit_id%";
    public static final String KIT_NAME                = "%kit_name%";
    public static final String KIT_DESCRIPTION         = "%kit_description%";
    public static final String KIT_COOLDOWN            = "%kit_cooldown%";
    public static final String KIT_COST                = "%kit_cost_money%";

    @NotNull
    public static final TypedPlaceholder<Kit> KIT = TypedPlaceholder.builder(Kit.class)
        .with(KIT_ID, Kit::getId)
        .with(KIT_NAME, kit -> kit.definition().getName())
        .with(KIT_DESCRIPTION, kit -> String.join("\n", kit.definition().getDescription()))
        .with(KIT_COOLDOWN, kit -> kit.hasCooldown() ? (kit.isOneTimed() ? CoreLang.OTHER_ONE_TIMED.text() : TimeFormats.formatAmount(kit.definition().getCooldown() * 1000L, TimeFormatType.LITERAL)) : CoreLang.STATE_YES_NO.get(false))
        .with(KIT_COST, kit -> EconomyBridge.economyCurrency().filter(currency -> kit.hasCost()).map(currency -> currency.format(kit.definition().getCost())).orElse(Lang.OTHER_FREE.text()))
        .build();
}
