package su.nightexpress.sunlight.module.playerwarps.featuring;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import su.nightexpress.nightcore.bridge.currency.Currency;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;
import su.nightexpress.nightcore.integration.currency.CurrencyId;
import su.nightexpress.nightcore.integration.currency.EconomyBridge;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.nightcore.util.placeholder.PlaceholderResolvable;
import su.nightexpress.nightcore.util.placeholder.PlaceholderResolver;
import su.nightexpress.sunlight.module.playerwarps.PlayerWarpsPlaceholders;

import java.util.Optional;

public record FeaturedSlot(@NonNull String id, @NonNull String currencyId, double price, long duration, int[] inventorySlots) implements Writeable, PlaceholderResolvable {

    @NonNull
    public static FeaturedSlot read(@NonNull FileConfig config, @NonNull String path) {
        String id = LowerCase.INTERNAL.apply(config.getString(path + ".Id", "null"));
        String currencyId = config.getString(path + ".Price.Currency", CurrencyId.VAULT);
        double price = config.getDouble(path + ".Price.Amount");
        long duration = config.getLong(path + ".Duration");
        int[] slotIndexes = config.getIntArray(path + ".Slots");

        return new FeaturedSlot(id, currencyId, price, duration, slotIndexes);
    }

    @Override
    public void write(@NonNull FileConfig config, @NonNull String path) {
        config.set(path + ".Id", this.id);
        config.set(path + ".Price.Currency", this.currencyId);
        config.set(path + ".Price.Amount", this.price);
        config.set(path + ".Duration", this.duration);
        config.setArray(path + ".Slots", this.inventorySlots);
    }

    @Override
    @NonNull
    public PlaceholderResolver placeholders() {
        return PlayerWarpsPlaceholders.FEATURED_SLOT.resolver(this);
    }

    @NonNull
    public Optional<Currency> currency() {
        return EconomyBridge.currency(this.currencyId);
    }

    public void pay(@NonNull Player player) {
        this.currency().ifPresent(currency -> currency.take(player, this.price));
    }

    public boolean canAfford(@NonNull Player player) {
        return this.currency().map(currency -> currency.getBalance(player)).orElse(0D) >= this.price;
    }

    public boolean containsSlot(int slotIndex) {
        return Lists.contains(this.inventorySlots, slotIndex);
    }

    public long createEndTimestamp() {
        return TimeUtil.createFutureTimestamp(this.duration);
    }
}
