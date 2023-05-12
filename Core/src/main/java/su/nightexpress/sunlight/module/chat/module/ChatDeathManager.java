package su.nightexpress.sunlight.module.chat.module;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.NexEngine;
import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.utils.*;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.chat.ChatModule;

import java.util.*;
import java.util.function.UnaryOperator;

public class ChatDeathManager extends AbstractManager<SunLight> {

    private final ChatModule chatModule;

    private static final String PLACEHOLDER_DAMAGER = "%damager%";
    private static final JOption<Double> RANGE = JOption.create("Range", -1D,
        "Sets the range for death messages.",
        "Set to 0 for a world-wide messages.",
        "Set to -1 for a server-wide messages.",
        "Set other value for certain blocks distance."
    );
    private static final JOption<Map<DamageCause, Map<String, List<String>>>> MESSAGES = new JOption<Map<DamageCause, Map<String, List<String>>>>("List",
        (cfg, path, def) -> {
            Map<DamageCause, Map<String, List<String>>> messages = new HashMap<>();
            for (String causeRaw : cfg.getSection(path)) {
                DamageCause cause = CollectionsUtil.getEnum(causeRaw, DamageCause.class);
                if (cause == null) continue;

                for (String damager : cfg.getSection(path + "." + causeRaw)) {
                    List<String> list = Colorizer.apply(cfg.getStringList(path + "." + causeRaw + "." + damager));
                    messages.computeIfAbsent(cause, k -> new HashMap<>()).put(damager, list);
                }
            }
            return messages;
        },
        HashMap::new,
        "A list of all custom death messages. Here you can add or remove messages.",
        "Use DamageCause as a primary key for messages section: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/entity/EntityDamageEvent.DamageCause.html",
        "For next levels, you can specify last attacker name/type as a key for different messages:",
        "- BLOCK_EXPLOSION, CONTACT, FALLING_BLOCK, SUFFOCATION -> Material Name: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html",
        "- ENTITY_ATTACK, ENTITY_SWEEP_ATTACK, ENTITY_EXPLODE, PROJECTILE -> Entity Type: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/EntityType.html",
        "You can also use 'default' key for all other damagers/sources.",
        "There is also a bit of internal placeholders:",
        "- All Player Placeholders from https://github.com/nulli0n/NexEngine-spigot/wiki/Internal-Placeholders",
        "- %damager% - For attacker/damager name.",
        "PlaceholderAPI is supported in all messages.",
        "JSON formatting is also supported in all messages: https://github.com/nulli0n/NexEngine-spigot/wiki/Language-Config#json-formatting,"
    );

    public ChatDeathManager(@NotNull ChatModule chatModule) {
        super(chatModule.plugin());
        this.chatModule = chatModule;
    }

    @Override
    protected void onLoad() {
        JYML cfg = JYML.loadOrExtract(plugin, chatModule.getLocalPath(), "death.messages.yml");
        cfg.initializeOptions(this);

        this.addListener(new Listener(this.plugin));
    }

    @Override
    protected void onShutdown() {
        MESSAGES.get().clear();
    }

    @NotNull
    public Collection<? extends Entity> getRecievers(@NotNull Player dead) {
        double range = RANGE.get();
        if (range == -1D) return plugin.getServer().getOnlinePlayers();
        if (range == 0D) return dead.getWorld().getPlayers();
        return dead.getWorld().getNearbyEntities(dead.getLocation(), range, range, range, e -> e.getType() == EntityType.PLAYER);
    }

    class Listener extends AbstractListener<SunLight> {

        public Listener(@NotNull SunLight plugin) {
            super(plugin);
        }

        @EventHandler(priority = EventPriority.NORMAL)
        public void onChatDeathMessage(PlayerDeathEvent e) {
            e.setDeathMessage(null);

            Player player = e.getEntity();
            EntityDamageEvent lastEvent = player.getLastDamageCause();
            if (lastEvent == null) return;

            DamageCause cause = lastEvent.getCause();
            UnaryOperator<String> mainReplacer = (str -> str);
            String damagerType = Placeholders.DEFAULT;

            if (lastEvent instanceof EntityDamageByEntityEvent entityEvent) {
                Entity damager = entityEvent.getDamager();

                /*if (damager instanceof Player killer) {
                    damagerType = killer.getType().name();
                    mainReplacer = Placeholders.Player.replacer(killer);
                }
                else */if (damager instanceof Projectile projectile && projectile.getShooter() instanceof LivingEntity shooter) {
                    damagerType = shooter.getType().name();
                    mainReplacer = /*(shooter instanceof Player ? Placeholders.Player.replacer(shooter) : */(str) -> str.replace(PLACEHOLDER_DAMAGER, getName(shooter));
                }
                else {
                    damagerType = damager.getType().name();
                    mainReplacer = (str) -> str.replace(PLACEHOLDER_DAMAGER, getName(damager));
                }
            }
            else if (lastEvent instanceof EntityDamageByBlockEvent blockEvent) {
                Block damager = blockEvent.getDamager();
                if (damager == null) return;

                damagerType = damager.getType().name();
                mainReplacer = (str) -> str.replace(PLACEHOLDER_DAMAGER, plugin.getLangManager().getEnum(damager.getType()));
            }

            Map<String, List<String>> messages = MESSAGES.get().getOrDefault(cause, Collections.emptyMap());
            List<String> list = messages.getOrDefault(damagerType, messages.getOrDefault(Placeholders.DEFAULT, Collections.emptyList()));
            if (list.isEmpty()) return;

            String deathMsg = Rnd.get(list);
            deathMsg = mainReplacer.apply(deathMsg);
            deathMsg = Placeholders.Player.replacer(player).apply(deathMsg);
            if (Hooks.hasPlaceholderAPI()) {
                deathMsg = PlaceholderAPI.setPlaceholders(player, deathMsg);
            }

            String finalDeathMsg = deathMsg;
            getRecievers(player).forEach(entity -> MessageUtil.sendWithJson(entity, finalDeathMsg));
        }
    }

    @NotNull
    private static String getName(@NotNull Entity entity) {
        if (entity instanceof Player) {
            return entity.getName();
        }
        if (entity instanceof LivingEntity) {
            String cName = entity.getCustomName();
            if (cName != null) {
                return cName;
            }
        }

        return NexEngine.get().getLangManager().getEnum(entity.getType());
    }
}
