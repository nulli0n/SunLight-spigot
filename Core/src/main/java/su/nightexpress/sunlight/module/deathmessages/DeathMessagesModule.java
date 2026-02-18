package su.nightexpress.sunlight.module.deathmessages;

import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.nightcore.util.placeholder.PlaceholderContext;
import su.nightexpress.nightcore.util.text.night.NightMessage;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.exception.ModuleLoadException;
import su.nightexpress.sunlight.hook.placeholder.PlaceholderRegistry;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleContext;

public class DeathMessagesModule extends Module {

    private final DeathMessagesSettings settings;

    public DeathMessagesModule(@NotNull ModuleContext context) {
        super(context);
        this.settings = new DeathMessagesSettings();
    }

    @Override
    protected void loadModule(@NotNull FileConfig config) throws ModuleLoadException {
        this.settings.load(config);

        this.addListener(new DeathMessagesListener(this.plugin, this));
    }

    @Override
    protected void unloadModule() {

    }

    @Override
    protected void registerCommands() {

    }

    @Override
    protected void registerPermissions(@NotNull PermissionTree root) {

    }

    @Override
    public void registerPlaceholders(@NotNull PlaceholderRegistry registry) {

    }

    public void handleDeathEvent(@NotNull PlayerDeathEvent event) {
        EventUtils.getAdapter().setDeathMessage(event, null);

        Player player = event.getEntity();

        EntityDamageEvent lastEvent = player.getLastDamageCause();
        if (lastEvent == null) return;

        DamageSource damageSource = lastEvent.getDamageSource();

        Entity causingEntity = damageSource.getCausingEntity();
        Entity directEnttiy = damageSource.getDirectEntity();
        ItemStack weapon = null;

        if (causingEntity instanceof LivingEntity livingEntity) {
            weapon = EntityUtil.getItemInSlot(livingEntity, EquipmentSlot.HAND);
        }

        DeathContext context = new DeathContext(player, damageSource, causingEntity, directEnttiy, weapon);

        DeathMessage message = this.getMessage(context);
        if (message == null) return;

        String rawMessage = message.selectMessage();
        if (rawMessage == null) return;

        PlaceholderContext.Builder builder = PlaceholderContext.builder()
            .with(CommonPlaceholders.PLAYER.resolver(context.player()))
            .andThen(CommonPlaceholders.forPlaceholderAPI(context.player()));

        if (causingEntity != null) {
            builder.with(SLPlaceholders.GENERIC_SOURCE, () -> getName(causingEntity));
            builder.with(SLPlaceholders.GENERIC_ITEM, () -> getItemName(causingEntity));
        }

        String deathMessage = builder.build().apply(rawMessage);

        EventUtils.getAdapter().setDeathMessage(event, NightMessage.parse(deathMessage));
    }

    @Nullable
    public DeathMessage getMessage(@NotNull DeathContext context) {
        DeathMessage deathMessage = null;

        Entity causingEntity = context.causingEntity();
        Entity directEnttiy = context.directEntity();

        if (directEnttiy != null) {
            deathMessage = this.settings.getEntityTypeMessages().get(directEnttiy.getType());
        }
        if (causingEntity != null) {
            if (deathMessage == null) {
                deathMessage = this.settings.getEntityTypeMessages().get(causingEntity.getType());
            }
        }
        if (deathMessage == null) {
            deathMessage = this.settings.getDamageTypeMessages().get(context.damageSource().getDamageType());
        }
        return deathMessage;
    }

    @NotNull
    private static String getName(@NotNull Entity entity) {
        if (entity instanceof Player player) {
            return Players.getDisplayNameSerialized(player);
        }

        return EntityUtil.getNameSerialized(entity);
    }

    @NotNull
    private static String getItemName(@NotNull Entity entity) {
        if (!(entity instanceof LivingEntity living)) return "";

        EntityEquipment equipment = living.getEquipment();
        if (equipment == null) return "";

        ItemStack item = equipment.getItemInMainHand();
        return ItemUtil.getNameSerialized(item);
    }
}
