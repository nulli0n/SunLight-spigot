package su.nightexpress.sunlight.module.deathmessages;

import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record DeathContext(@NotNull Player player,
                           @NotNull DamageSource damageSource,
                           @Nullable Entity causingEntity,
                           @Nullable Entity directEntity,
                           @Nullable ItemStack weapon) {

}
