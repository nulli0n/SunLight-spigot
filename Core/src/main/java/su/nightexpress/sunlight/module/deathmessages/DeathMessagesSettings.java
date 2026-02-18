package su.nightexpress.sunlight.module.deathmessages;

import org.bukkit.damage.DamageType;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.configuration.AbstractConfig;
import su.nightexpress.nightcore.configuration.ConfigProperty;
import su.nightexpress.nightcore.configuration.ConfigType;
import su.nightexpress.nightcore.configuration.ConfigTypes;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.bridge.RegistryType;

import java.util.HashMap;
import java.util.Map;

import static su.nightexpress.nightcore.util.Placeholders.PLAYER_DISPLAY_NAME;
import static su.nightexpress.nightcore.util.text.tag.Tags.GRAY;
import static su.nightexpress.nightcore.util.text.tag.Tags.LIGHT_RED;
import static su.nightexpress.sunlight.SLPlaceholders.GENERIC_SOURCE;

public class DeathMessagesSettings extends AbstractConfig {

    private static final ConfigType<DeathMessage> DEATH_MESSAGE_CONFIG_TYPE = ConfigType.of(
        DeathMessage::read,
        FileConfig::set
    );

    private final ConfigProperty<Map<DamageType, DeathMessage>> damageTypeMessages = this.addProperty(
        ConfigTypes.forMap(str -> BukkitThing.getByString(RegistryType.DAMAGE_TYPE, str), BukkitThing::getAsString, DEATH_MESSAGE_CONFIG_TYPE),
        "Messages.Damage-Type",
        getDefaultsByType(),
        ""
    );

    private final ConfigProperty<Map<EntityType, DeathMessage>> entityTypeMessages = this.addProperty(
        ConfigTypes.forMap(str -> BukkitThing.getByString(RegistryType.ENTITY_TYPE, str), BukkitThing::getAsString, DEATH_MESSAGE_CONFIG_TYPE),
        "Messages.Causing-Entity",
        getDefaultsByEntity(),
        ""
    );

    @NotNull
    public Map<DamageType, DeathMessage> getDamageTypeMessages() {
        return this.damageTypeMessages.get();
    }

    @NotNull
    public Map<EntityType, DeathMessage> getEntityTypeMessages() {
        return this.entityTypeMessages.get();
    }

    @NotNull
    private static Map<DamageType, DeathMessage> getDefaultsByType() {
        String skull = "(" + LIGHT_RED.enclose("☠") + ") ";
        String swords = "(" + LIGHT_RED.enclose("⚔") + ") ";

        Map<DamageType, DeathMessage> map = new HashMap<>();

        map.put(DamageType.EXPLOSION, DeathMessage.simple(GRAY.enclose(skull + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " blowed up!")));
        map.put(DamageType.CACTUS, new DeathMessage(Lists.newList(
            GRAY.enclose(skull + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " tried to obtain some water from cactus."),
            GRAY.enclose(skull + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " died when accidiently touch a cactus.")
        )));
        map.put(DamageType.CRAMMING, DeathMessage.simple(GRAY.enclose(skull + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " trampled into the ground by a crowd.")));
        map.put(DamageType.DRAGON_BREATH, DeathMessage.simple(GRAY.enclose(skull + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " was incinerated by a Dragon Breath.")));
        map.put(DamageType.DROWN, new DeathMessage(Lists.newList(
            GRAY.enclose(skull + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " drowned."),
            GRAY.enclose(skull + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " forgot how to swim.")
        )));
        map.put(DamageType.MOB_ATTACK, DeathMessage.simple(GRAY.enclose(swords + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " was slain by " + LIGHT_RED.enclose(GENERIC_SOURCE) + ".")));
        map.put(DamageType.MOB_PROJECTILE, DeathMessage.simple(GRAY.enclose(swords + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " was shot by a " + LIGHT_RED.enclose(GENERIC_SOURCE) + ".")));
        map.put(DamageType.ARROW, DeathMessage.simple(GRAY.enclose(swords + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " was shot by a " + LIGHT_RED.enclose(GENERIC_SOURCE) + ".")));
        map.put(DamageType.PLAYER_ATTACK, DeathMessage.simple(GRAY.enclose(swords + LIGHT_RED.enclose(GENERIC_SOURCE) + " killed " + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + ".")));
        map.put(DamageType.FALL, DeathMessage.simple(GRAY.enclose(skull + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " fell from a high spot.")));
        map.put(DamageType.FALLING_ANVIL, DeathMessage.simple(GRAY.enclose(skull + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " was unable to hold an Anvil.")));
        map.put(DamageType.FLY_INTO_WALL, DeathMessage.simple(GRAY.enclose(skull + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " exceeded the speed limit with elytras.")));
        map.put(DamageType.IN_FIRE, DeathMessage.simple(GRAY.enclose(skull + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " burned to the ground.")));
        map.put(DamageType.ON_FIRE, DeathMessage.simple(GRAY.enclose(skull + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " burned to the ground.")));
        map.put(DamageType.FREEZE, new DeathMessage(Lists.newList(
            GRAY.enclose(skull + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " frozen to death."),
            GRAY.enclose(skull + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " fell asleep in the snow.")
        )));
        map.put(DamageType.HOT_FLOOR, DeathMessage.simple(GRAY.enclose(skull + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " walked too long on Magma Blocks.")));
        map.put(DamageType.LAVA, DeathMessage.simple(GRAY.enclose(skull + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " tried to swim in lava.")));
        map.put(DamageType.LIGHTNING_BOLT, DeathMessage.simple(GRAY.enclose(skull + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " was shot by a Lightning.")));
        map.put(DamageType.MAGIC, DeathMessage.simple(GRAY.enclose(skull + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " killed by a deadly magic.")));
        map.put(DamageType.SONIC_BOOM, DeathMessage.simple(GRAY.enclose(skull + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + "''s eardrums were exploded.")));
        map.put(DamageType.STARVE, DeathMessage.simple(GRAY.enclose(skull + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " forgot to eat.")));
        map.put(DamageType.IN_WALL, DeathMessage.simple(GRAY.enclose(skull + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " was buried alive.")));
        map.put(DamageType.THORNS, DeathMessage.simple(GRAY.enclose(skull + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " died when touching " + LIGHT_RED.enclose(GENERIC_SOURCE) + ".")));


        return map;
    }

    @NotNull
    private static Map<EntityType, DeathMessage> getDefaultsByEntity() {
        String skull = "(" + LIGHT_RED.enclose("☠") + ") ";
        String swords = "(" + LIGHT_RED.enclose("⚔") + ") ";

        Map<EntityType, DeathMessage> map = new HashMap<>();

        map.put(EntityType.TNT, DeathMessage.simple(GRAY.enclose(skull + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " lost his limbs due to TNT explosion!")));
        map.put(EntityType.BEE, DeathMessage.simple(GRAY.enclose(swords + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " attempted to steal honey from Bees.")));
        map.put(EntityType.BLAZE, new DeathMessage(Lists.newList(
            GRAY.enclose(swords + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " could not reflect Blaze''s fireball."),
            GRAY.enclose(swords + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " catched a fireball with his head.")
        )));
        map.put(EntityType.CAVE_SPIDER, new DeathMessage(Lists.newList(
            GRAY.enclose(swords + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " was bited to death by a little spidee."),
            GRAY.enclose(swords + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " climb down too deep in a cave with Spiders.")
        )));
        map.put(EntityType.DROWNED,DeathMessage.simple(GRAY.enclose(swords + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " was drowned by Drowned.")));
        map.put(EntityType.GHAST, DeathMessage.simple(GRAY.enclose(swords + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " was fireballed by a Ghast.")));
        map.put(EntityType.ENDERMAN, DeathMessage.simple(GRAY.enclose(swords + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " looked strange at Enderman.")));
        map.put(EntityType.ZOMBIE, DeathMessage.simple(GRAY.enclose(swords + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + "''s brain was eaten by Zombie.")));
        map.put(EntityType.ZOMBIE_VILLAGER, DeathMessage.simple(GRAY.enclose(swords + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " tried to talk with Zombie Villager.")));
        map.put(EntityType.PIGLIN, DeathMessage.simple(GRAY.enclose(swords + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " offered a very bad trade to Piglin.")));
        map.put(EntityType.CREEPER, DeathMessage.simple(GRAY.enclose(swords + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " could not run away from a Creeper in time.")));
        map.put(EntityType.SKELETON, DeathMessage.simple(GRAY.enclose(swords + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " was shot by a skeleton.")));
        map.put(EntityType.WITHER, DeathMessage.simple(GRAY.enclose(swords + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " could not beat Wither.")));

        return map;
    }
}
