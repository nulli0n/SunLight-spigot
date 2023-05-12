package su.nightexpress.sunlight.module.worlds.commands.main;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.worlds.WorldsModule;
import su.nightexpress.sunlight.module.worlds.config.WorldsLang;
import su.nightexpress.sunlight.module.worlds.config.WorldsPerms;
import su.nightexpress.sunlight.module.worlds.impl.WorldConfig;

import java.util.List;

public class UnloadSubCommand extends ModuleCommand<WorldsModule> {

    public static final String NAME = "unload";

    public UnloadSubCommand(@NotNull WorldsModule worldsModule) {
        super(worldsModule, new String[]{NAME}, WorldsPerms.COMMAND_WORLDS_UNLOAD);
        this.setDescription(this.plugin.getMessage(WorldsLang.COMMAND_WORLDS_UNLOAD_DESC));
        this.setUsage(this.plugin.getMessage(WorldsLang.COMMAND_WORLDS_UNLOAD_USAGE));
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return this.module.getWorldConfigs().stream().filter(WorldConfig::isLoaded).map(WorldConfig::getId).toList();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        WorldConfig worldConfig = this.module.getWorldById(result.getArg(1));
        if (worldConfig == null) {
            plugin.getMessage(Lang.ERROR_WORLD_INVALID).send(sender);
            return;
        }
        if (!worldConfig.unloadWorld()) {
            this.plugin.getMessage(WorldsLang.COMMAND_WORLDS_UNLOAD_ERROR).send(sender);
            return;
        }

        this.plugin.getMessage(WorldsLang.COMMAND_WORLDS_UNLOAD_DONE).replace(worldConfig.replacePlaceholders()).send(sender);
    }
}
