package su.nightexpress.sunlight.command.list;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.EntityUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.ToggleCommand;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.data.impl.settings.UserSetting;
import su.nightexpress.sunlight.utils.Cleanable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class GodCommand extends ToggleCommand implements Cleanable {

    public static final String NAME = "god";
    public static final UserSetting<Boolean> GOD_MODE = UserSetting.asBoolean("god_mode", false, false);

    private final Set<DamageSource> disabledIncoming;
    private final Set<DamageSource> disabledOutgoing;
    private final Set<String> disabledWorlds;
    private final Listener    listener;

    public GodCommand(@NotNull SunLight plugin, @NotNull JYML cfg, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_GOD, Perms.COMMAND_GOD_OTHERS);
        this.setAllowDataLoad();
        this.setDescription(plugin.getMessage(Lang.COMMAND_GOD_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_GOD_USAGE));

        this.disabledIncoming = new JOption<Set<DamageSource>>("God.Disabled_Damage.Incoming",
            (cfg2, path, def) -> {
                return cfg2.getStringSet(path).stream()
                    .map(str -> StringUtil.getEnum(str, DamageSource.class).orElse(null))
                    .filter(Objects::nonNull).collect(Collectors.toSet());
            },
            new HashSet<>(Arrays.asList(DamageSource.values())),
            "A list of disabled damage sources for incoming damage.",
            "Available values: " + String.join(", ", CollectionsUtil.getEnumsList(DamageSource.class))
        ).setWriter((cfg2, path, set) -> cfg2.set(path, set.stream().map(Enum::name).toList())).read(cfg);

        this.disabledOutgoing = new JOption<Set<DamageSource>>("God.Disabled_Damage.Outgoing",
            (cfg2, path, def) -> {
                return cfg2.getStringSet(path).stream()
                    .map(str -> StringUtil.getEnum(str, DamageSource.class).orElse(null))
                    .filter(Objects::nonNull).collect(Collectors.toSet());
            },
            new HashSet<>(Arrays.asList(DamageSource.values())),
            "A list of disabled damage sources for outgoing damage.",
            "Available values: " + String.join(", ", CollectionsUtil.getEnumsList(DamageSource.class))
        ).setWriter((cfg2, path, set) -> cfg2.set(path, set.stream().map(Enum::name).toList())).read(cfg);

        this.disabledWorlds = JOption.create("God.Disabled_Worlds", Set.of("my_custom_world"),
            "A list of worlds, where GodMode will have no effect.").read(cfg);

        (this.listener = new Listener(plugin)).registerListeners();
    }

    private enum DamageSource {
        PLAYER, MOB, GENERIC;

        @NotNull
        public static DamageSource getIncoming(@NotNull EntityDamageEvent e) {
            if (!(e instanceof EntityDamageByEntityEvent ede)) return GENERIC;

            LivingEntity damager = null;
            if (ede.getDamager() instanceof LivingEntity living) {
                damager = living;
            }
            else if (ede.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof LivingEntity living) {
                damager = living;
            }

            if (damager != null) {
                return damager instanceof Player ? PLAYER : MOB;
            }
            return GENERIC;
        }

        @NotNull
        public static DamageSource getOutgoing(@NotNull EntityDamageByEntityEvent e) {
            Entity victim = e.getEntity();
            if (victim instanceof LivingEntity living) {
                return living instanceof Player ? PLAYER : MOB;
            }
            return GENERIC;
        }
    }

    @Override
    public void clear() {
        this.listener.unregisterListeners();
        this.disabledOutgoing.clear();
        this.disabledIncoming.clear();
        this.disabledWorlds.clear();
    }

    private boolean isAllowedWorld(@NotNull World world) {
        return !disabledWorlds.contains(world.getName());
    }

    private boolean isAllowedWorld(@NotNull Player player) {
        if (player.hasPermission(Perms.COMMAND_GOD_BYPASS_WORLDS)) return true;
        return this.isAllowedWorld(player.getWorld());
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        Mode mode = this.getMode(sender, result);

        SunUser user = plugin.getUserManager().getUserData(target);
        user.getSettings().set(GOD_MODE, mode.apply(user.getSettings().get(GOD_MODE)));

        // Notify about god mode in disabled world.
        if (user.getSettings().get(GOD_MODE) && !this.isAllowedWorld(target)) {
            plugin.getMessage(Lang.COMMAND_GOD_NOTIFY_BAD_WORLD).send(target);
        }

        if (sender != target) {
            plugin.getMessage(Lang.COMMAND_GOD_TOGGLE_TARGET)
                .replace(Placeholders.GENERIC_STATE, Lang.getEnable(user.getSettings().get(GOD_MODE)))
                .replace(Placeholders.forPlayer(target))
                .send(sender);
        }
        if (!result.hasFlag(CommandFlags.SILENT)) {
            plugin.getMessage(Lang.COMMAND_GOD_TOGGLE_NOTIFY)
                .replace(Placeholders.GENERIC_STATE, Lang.getEnable(user.getSettings().get(GOD_MODE)))
                .send(target);
        }
    }

    private class Listener extends AbstractListener<SunLight> {

        public Listener(@NotNull SunLight plugin) {
            super(plugin);
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onGodWorldChange(PlayerChangedWorldEvent e) {
            Player player = e.getPlayer();
            if (EntityUtil.isNPC(player)) return;

            // Notify about god mode in disabled world.
            SunUser user = plugin.getUserManager().getUserData(player);
            if (user.getSettings().get(GOD_MODE) && !isAllowedWorld(player)) {
                plugin.getMessage(Lang.COMMAND_GOD_NOTIFY_BAD_WORLD).send(player);
            }
        }

        @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
        public void onGodDamageIncoming(EntityDamageEvent e) {
            Entity victim = e.getEntity();
            if (!(victim instanceof Player player)) return;
            if (EntityUtil.isNPC(player) || !isAllowedWorld(player)) return;

            SunUser user = plugin.getUserManager().getUserData(player);
            if (user.getSettings().get(GOD_MODE) && disabledIncoming.contains(DamageSource.getIncoming(e))) {
                e.setCancelled(true);
            }
        }

        @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
        public void onGodDamageOutgoing(EntityDamageByEntityEvent e) {
            Player damager;
            if (e.getDamager() instanceof Player player) {
                damager = player;
            }
            else if (e.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player player) {
                damager = player;
            }
            else return;

            if (EntityUtil.isNPC(damager) || !isAllowedWorld(damager)) return;

            SunUser user = this.plugin.getUserManager().getUserData(damager);
            if (user.getSettings().get(GOD_MODE) && disabledOutgoing.contains(DamageSource.getOutgoing(e))) {
                plugin.getMessage(Lang.COMMAND_GOD_DAMAGE_NOTIFY_OUT).send(damager);
                e.setCancelled(true);
            }
        }
    }
}
