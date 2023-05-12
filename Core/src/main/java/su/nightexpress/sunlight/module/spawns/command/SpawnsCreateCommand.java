package su.nightexpress.sunlight.module.spawns.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.Placeholders;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.spawns.SpawnsModule;
import su.nightexpress.sunlight.module.spawns.config.SpawnsLang;
import su.nightexpress.sunlight.module.spawns.util.SpawnsPerms;

import java.util.List;
import java.util.Map;

public class SpawnsCreateCommand extends ModuleCommand<SpawnsModule> {

    public static final String NAME = "create";

    public SpawnsCreateCommand(@NotNull SpawnsModule spawnsModule) {
        super(spawnsModule, new String[]{NAME}, SpawnsPerms.COMMAND_SPAWNS_CREATE);
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.plugin.getMessage(SpawnsLang.COMMAND_SPAWNS_CREATE_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.plugin.getMessage(SpawnsLang.COMMAND_SPAWNS_CREATE_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
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
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
        String spawnId = args.length >= 2 ? args[1] : Placeholders.DEFAULT;
        Player player = (Player) sender;
        this.module.createSpawn(player, spawnId);
    }
}
