package su.nightexpress.sunlight.module.extras.impl.nerfphantoms;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Phantom;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.utils.EntityUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.extras.config.ExtrasConfig;

public class PhantomsListener extends AbstractListener<SunLight> {

    public PhantomsListener(@NotNull SunLight plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPhantomSpawn(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Phantom phantom)) return;
        if (ExtrasConfig.NERF_PHANTOMS_DISABLE_SPAWN.get().contains(event.getSpawnReason())) {
            event.setCancelled(true);
            return;
        }

        double health = ExtrasConfig.NERF_PHANTOMS_HEALTH.get();
        double damage = ExtrasConfig.NERF_PHANTOMS_DAMAGE_MODIFIER.get();

        for (Attribute attribute : new Attribute[]{Attribute.GENERIC_MAX_HEALTH, Attribute.GENERIC_ATTACK_DAMAGE}) {
            AttributeInstance instance = phantom.getAttribute(attribute);
            if (instance == null) continue;

            double val = switch (attribute) {
                case GENERIC_ATTACK_DAMAGE -> EntityUtil.getAttribute(phantom, attribute) * damage;
                case GENERIC_MAX_HEALTH -> health;
                default -> 0D;
            };
            instance.setBaseValue(Math.max(0, val));
        }

        phantom.setHealth(health);
    }
}
