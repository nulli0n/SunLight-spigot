package su.nightexpress.sunlight.command.list;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.utils.EntityUtil;
import su.nexmedia.engine.utils.Pair;
import su.nexmedia.engine.utils.Placeholders;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.TargetCommand;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.utils.Cleanable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BackCommand extends TargetCommand implements Cleanable {

    public static final String NAME = "back";

    private final Listener    listener;
    private final double      minDistDiff;
    private final int         expireTime;
    private final Set<String> disabledWorlds;
    private final Set<String> ignoredCauses;

    private final Map<UUID, Pair<Location, Long>> locationMap;

    public BackCommand(@NotNull SunLight plugin, @NotNull JYML cfg, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_BACK, Perms.COMMAND_BACK_OTHERS, 0);
        this.setDescription(plugin.getMessage(Lang.COMMAND_BACK_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_BACK_USAGE));
        this.addFlag(CommandFlags.SILENT);

        this.locationMap = new HashMap<>();
        this.expireTime = JOption.create("Back.Expire_Time", 3600,
            "Sets how long (in seconds) back location will be stored before being removed & invalid.").read(cfg);
        this.minDistDiff = JOption.create("Back.Min_Distance_Difference", 10D,
            "Sets minimal distance between current and new player's locations to",
            "set or override current player's back location.").read(cfg);
        this.disabledWorlds = JOption.create("Back.Disabled_Worlds", Set.of("my_custom_world"),
            "A list of worlds, where back locations won't be saved.",
            "This setting can be bypassed with '" + Perms.COMMAND_BACK_BYPASS_WORLDS.getName() + "' permission").read(cfg);
        this.ignoredCauses = JOption.create("Back.Ignored_Causes", Set.of(PlayerTeleportEvent.TeleportCause.ENDER_PEARL.name()),
            "A list of teleport causes, that will be ignored for saving back location.",
            "This setting can be bypassed with '" + Perms.COMMAND_BACK_BYPASS_CAUSES.getName() + "' permission",
            "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/player/PlayerTeleportEvent.TeleportCause.html")
            .mapReader(set -> set.stream().map(String::toLowerCase).collect(Collectors.toSet())).read(cfg);

        (this.listener = new Listener(plugin)).registerListeners();
    }

    @Override
    public void clear() {
        this.listener.unregisterListeners();
        this.disabledWorlds.clear();
        this.locationMap.clear();
    }

    private void validate() {
        this.locationMap.values().removeIf(pair -> System.currentTimeMillis() > pair.getSecond());
    }

    @Nullable
    private Location getPreviousLocation(@NotNull Player player) {
        this.validate();
        Pair<Location, Long> pair = this.locationMap.get(player.getUniqueId());
        return pair == null ? null : pair.getFirst();
    }

    private void setPreviousLocation(@NotNull Player player, @NotNull Location location) {
        this.validate();
        long expireDate = System.currentTimeMillis() + this.expireTime * 1000L;
        this.locationMap.put(player.getUniqueId(), Pair.of(location, expireDate));
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        boolean isSilent = result.hasFlag(CommandFlags.SILENT);

        Location back = this.getPreviousLocation(target);
        if (back == null || back.getWorld() == null) {
            if (!isSilent) plugin.getMessage(Lang.COMMAND_BACK_ERROR_NOTHING).send(sender);
            return;
        }

        target.teleport(back);
        if (!isSilent) {
            plugin.getMessage(Lang.COMMAND_BACK_NOTIFY).send(target);
        }
        if (target != sender) {
            plugin.getMessage(Lang.COMMAND_BACK_TARGET).replace(Placeholders.forPlayer(target)).send(sender);
        }
    }

    private class Listener extends AbstractListener<SunLight> {

        Listener(@NotNull SunLight plugin) {
            super(plugin);
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onTeleport(PlayerTeleportEvent e) {
            Player player = e.getPlayer();
            if (EntityUtil.isNPC(player)) return;
            if (!player.hasPermission(Perms.COMMAND_BACK_BYPASS_CAUSES) && ignoredCauses.contains(e.getCause().name().toLowerCase())) {
                return;
            }

            Location locFrom = e.getFrom();
            Location locTo = e.getTo();
            if (locTo == null || locFrom.getWorld() == null) return;

            World worldFrom = locFrom.getWorld();
            World worldTo = locTo.getWorld();
            if (!player.hasPermission(Perms.COMMAND_BACK_BYPASS_WORLDS) && disabledWorlds.contains(worldFrom.getName())) {
                return;
            }
            if (worldFrom == worldTo && locFrom.distanceSquared(locTo) <= minDistDiff) {
                if (getPreviousLocation(player) != null) return;
            }

            setPreviousLocation(player, locFrom);
        }
    }
}
