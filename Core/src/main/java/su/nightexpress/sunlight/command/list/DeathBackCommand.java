package su.nightexpress.sunlight.command.list;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.utils.EntityUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.TargetCommand;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.utils.Cleanable;
import su.nightexpress.sunlight.utils.FairTeleport;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static su.nightexpress.sunlight.utils.FairTeleport.fairTeleport;

public class DeathBackCommand extends TargetCommand implements Cleanable {

    public static final String NAME = "deathback";

    private final boolean     resetOnUse;
    private final Set<String> disabledWorlds;
    private final Map<UUID, Location> locationMap;

    private final Listener listener;

    public DeathBackCommand(@NotNull SunLight plugin, @NotNull JYML cfg, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_DEATHBACK, Perms.COMMAND_DEATHBACK_OTHERS, 0);
        this.setDescription(plugin.getMessage(Lang.COMMAND_DEATH_BACK_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_DEATH_BACK_USAGE));
        this.addFlag(CommandFlags.SILENT);

        this.locationMap = new HashMap<>();
        this.resetOnUse = JOption.create("DeathBack.Reset_On_Use", true,
            "Sets whether player's death location will be removed after use.").read(cfg);
        this.disabledWorlds = JOption.create("DeathBack.Disabled_Worlds", Set.of("my_custom_world"),
            "A list of worlds, where death back location won't be saved.",
            "This setting can be bypassed with '" + Perms.COMMAND_DEATHBACK_BYPASS_WORLDS.getName() + "' permission.").read(cfg);

        (this.listener = new Listener(plugin)).registerListeners();
    }

    @Override
    public void clear() {
        this.listener.unregisterListeners();
        this.disabledWorlds.clear();
        this.locationMap.clear();
    }

    @Nullable
    private Location getDeathLocation(@NotNull Player player) {
        if (this.resetOnUse) {
            return this.locationMap.remove(player.getUniqueId());
        }
        else return this.locationMap.get(player.getUniqueId());
    }

    private void setDeathLocation(@NotNull Player player, @NotNull Location location) {
        this.locationMap.put(player.getUniqueId(), location);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        boolean isSilent = result.hasFlag(CommandFlags.SILENT);

        Location deathLocation = getDeathLocation(target);
        if (deathLocation == null || deathLocation.getWorld() == null) {
            if (!isSilent) plugin.getMessage(Lang.COMMAND_DEATH_BACK_ERROR_NOTHING).send(sender);
            return;
        }

//        target.teleport(deathLocation);
        fairTeleport(target, deathLocation, plugin.getMessage(Lang.COMMAND_DEATH_BACK_NOTIFY));
//        if (!isSilent) {
//            plugin.getMessage(Lang.COMMAND_DEATH_BACK_NOTIFY).send(sender);
//        }
//        if (target != sender) {
//            plugin.getMessage(Lang.COMMAND_DEATH_BACK_TARGET).replace(Placeholders.forPlayer(target)).send(sender);
//        }
    }

    private class Listener extends AbstractListener<SunLight> {

        Listener(@NotNull SunLight plugin) {
            super(plugin);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        public void onPlayerDeath(PlayerDeathEvent e) {
            Player player = e.getEntity();
            if (EntityUtil.isNPC(player)) return;

            if (!player.hasPermission(Perms.COMMAND_DEATHBACK_BYPASS_WORLDS)) {
                if (disabledWorlds.contains(player.getWorld().getName())) {
                    return;
                }
            }

            setDeathLocation(player, player.getLocation());
        }
    }
}
