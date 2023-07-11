package su.nightexpress.sunlight.module.spawns.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.spawns.SpawnsModule;
import su.nightexpress.sunlight.module.spawns.config.SpawnsLang;
import su.nightexpress.sunlight.module.spawns.impl.Spawn;
import su.nightexpress.sunlight.module.spawns.util.SpawnsPerms;

import java.util.List;

public class SpawnsDeleteCommand extends ModuleCommand<SpawnsModule> {

    public static final String NAME = "delete";

    public SpawnsDeleteCommand(@NotNull SpawnsModule module) {
        super(module, new String[]{NAME}, SpawnsPerms.COMMAND_SPAWNS_DELETE);
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.plugin.getMessage(SpawnsLang.COMMAND_SPAWNS_DELETE_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.plugin.getMessage(SpawnsLang.COMMAND_SPAWNS_DELETE_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return this.module.getSpawnIds();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() <= 2) {
            this.printUsage(sender);
            return;
        }

        Spawn spawn = this.module.getSpawnById(result.getArg(1)).orElse(null);
        if (spawn == null) {
            this.plugin.getMessage(SpawnsLang.SPAWN_ERROR_INVALID);
            return;
        }

        this.plugin.getMessage(SpawnsLang.COMMAND_SPAWNS_DELETE_DONE).replace(spawn.replacePlaceholders()).send(sender);
        this.module.deleteSpawn(spawn);
    }
}
