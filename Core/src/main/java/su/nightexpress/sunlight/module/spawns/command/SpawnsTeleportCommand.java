package su.nightexpress.sunlight.module.spawns.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.spawns.impl.Spawn;
import su.nightexpress.sunlight.module.spawns.SpawnsModule;
import su.nightexpress.sunlight.module.spawns.config.SpawnsLang;
import su.nightexpress.sunlight.module.spawns.util.Placeholders;
import su.nightexpress.sunlight.module.spawns.util.SpawnsPerms;

import java.util.List;
import java.util.Map;

public class SpawnsTeleportCommand extends ModuleCommand<SpawnsModule> {

    public static final String NAME = "teleport";

    public SpawnsTeleportCommand(@NotNull SpawnsModule module) {
        super(module, new String[]{NAME}, SpawnsPerms.COMMAND_SPAWNS_TELEPORT);
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.plugin.getMessage(SpawnsLang.COMMAND_SPAWNS_TELEPORT_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.plugin.getMessage(SpawnsLang.COMMAND_SPAWNS_TELEPORT_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return this.module.getSpawns().stream().filter(spawn -> spawn.hasPermission(player)).map(Spawn::getId).toList();
        }
        if (arg == 2 && player.hasPermission(SpawnsPerms.COMMAND_SPAWNS_TELEPORT_OTHERS)) {
            return CollectionsUtil.playerNames(player);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
        if (args.length >= 3 && !sender.hasPermission(SpawnsPerms.COMMAND_SPAWNS_TELEPORT_OTHERS)) {
            this.errorPermission(sender);
            return;
        }

        Spawn spawn = (args.length >= 2 ? this.module.getSpawnById(args[1]) : this.module.getSpawnByDefault()).orElse(null);
        if (spawn == null) {
            this.plugin.getMessage(SpawnsLang.SPAWN_ERROR_INVALID).send(sender);
            return;
        }

        String pName = args.length >= 3 ? args[2] : sender.getName();
        Player pTarget = this.plugin.getServer().getPlayer(pName);
        if (pTarget == null) {
            this.errorPlayer(sender);
            return;
        }

        boolean isForced = !sender.equals(pTarget);
        if (spawn.teleport(pTarget, isForced)) {
            if (isForced) {
                this.plugin.getMessage(SpawnsLang.COMMAND_SPAWNS_TELEPORT_NOTIFY)
                    .replace(spawn.replacePlaceholders())
                    .replace(Placeholders.Player.replacer(pTarget))
                    .send(sender);
            }
        }
    }
}
