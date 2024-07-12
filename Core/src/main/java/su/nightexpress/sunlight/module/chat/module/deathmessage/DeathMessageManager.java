package su.nightexpress.sunlight.module.chat.module.deathmessage;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Registry;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.util.Placeholders;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.sunlight.module.chat.util.Placeholders.*;

@SuppressWarnings("UnstableApiUsage")
public class DeathMessageManager extends AbstractManager<SunLightPlugin> {

    public static final String FILE_NAME = "death_messages.yml";

    private final ChatModule module;

    private final Map<String, DeathMessage> byTypeMessages;
    private final Map<String, DeathMessage> byEntityMessages;

    private int range;

    public DeathMessageManager(@NotNull SunLightPlugin plugin, @NotNull ChatModule module) {
        super(plugin);
        this.module = module;
        this.byTypeMessages = new HashMap<>();
        this.byEntityMessages = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        this.loadConfig();

        this.addListener(new DeathMessageListener(this.plugin, this));
    }

    @Override
    protected void onShutdown() {
        this.byTypeMessages.clear();
        this.byEntityMessages.clear();
    }


    private void loadConfig() {
        FileConfig config = FileConfig.loadOrExtract(this.plugin, this.module.getLocalPath(), FILE_NAME);

        this.range = ConfigValue.create("Range",
            -1,
            "Sets the range for death messages broadcast.",
            "Set to 0 for a world-wide messages.",
            "Set to -1 for a server-wide messages.",
            "Set other value for blocks distance."
        ).read(config);

        if (config.getSection("By_DamageType").isEmpty()) {
            getDefaultsByType().forEach((type, message) -> message.write(config, "By_DamageType." + BukkitThing.toString(type)));
        }
        if (config.getSection("By_CausingEntity").isEmpty()) {
            getDefaultsByEntity().forEach((type, message) -> message.write(config, "By_CausingEntity." + BukkitThing.toString(type)));
        }

        config.getSection("By_DamageType").forEach(type -> {
            DamageType damageType = BukkitThing.fromRegistry(Registry.DAMAGE_TYPE, type);
            if (damageType == null) {
                this.module.error("Invalid damage type '" + type + "' in '" + FILE_NAME + "'!");
                return;
            }

            DeathMessage message = DeathMessage.read(config, "By_DamageType." + type);
            this.byTypeMessages.put(BukkitThing.toString(damageType), message);
        });

        config.getSection("By_CausingEntity").forEach(type -> {
            EntityType entityType = BukkitThing.getEntityType(type);
            if (entityType == null) {
                this.module.error("Invalid entity type '" + type + "' in '" + FILE_NAME + "'!");
                return;
            }

            DeathMessage message = DeathMessage.read(config, "By_CausingEntity." + type);
            this.byEntityMessages.put(BukkitThing.toString(entityType), message);
        });

        config.saveChanges();
    }

    @NotNull
    public Collection<? extends Entity> getRecievers(@NotNull Player dead) {
        if (this.range == -1) return plugin.getServer().getOnlinePlayers();
        if (this.range == 0D) return dead.getWorld().getPlayers();
        return dead.getWorld().getNearbyEntities(dead.getLocation(), this.range, this.range, this.range, e -> e.getType() == EntityType.PLAYER);
    }

    @Nullable
    public String getMessage(@NotNull Player player, @NotNull DamageSource damageSource) {
        DeathMessage deathMessage = null;
        PlaceholderMap placeholders = new PlaceholderMap();

        Entity causingEntity = damageSource.getCausingEntity();
        Entity directEnttiy = damageSource.getDirectEntity();

        if (directEnttiy != null) {
            deathMessage = this.byEntityMessages.get(BukkitThing.toString(directEnttiy.getType()));
        }
        if (causingEntity != null) {
            if (deathMessage == null) {
                deathMessage = this.byEntityMessages.get(BukkitThing.toString(causingEntity.getType()));
            }

            placeholders.add(Placeholders.GENERIC_SOURCE, getName(causingEntity));
            placeholders.add(Placeholders.GENERIC_ITEM, getItemName(causingEntity));
        }
        if (deathMessage == null) {
            deathMessage = this.byTypeMessages.get(BukkitThing.toString(damageSource.getDamageType()));
        }
        if (deathMessage == null) return null;

        String text = deathMessage.selectMessage();
        if (text == null) return null;

        if (Plugins.hasPlaceholderAPI()) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }
        text = Placeholders.forPlayer(player).apply(text);

        return placeholders.replacer().apply(text);
    }

    @NotNull
    private static String getName(@NotNull Entity entity) {
        if (entity instanceof Player) {
            return entity.getName();
        }
        if (entity instanceof LivingEntity) {
            String customName = entity.getCustomName();
            if (customName != null) {
                return customName;
            }
        }

        return LangAssets.get(entity.getType());
    }

    @NotNull
    private static String getItemName(@NotNull Entity entity) {
        if (!(entity instanceof LivingEntity living)) return "";

        EntityEquipment equipment = living.getEquipment();
        if (equipment == null) return "";

        ItemStack item = equipment.getItemInMainHand();
        return ItemUtil.getItemName(item);
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

        map.put(EntityType.PRIMED_TNT, DeathMessage.simple(GRAY.enclose(skull + LIGHT_RED.enclose(PLAYER_DISPLAY_NAME) + " lost his limbs due to TNT explosion!")));
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
